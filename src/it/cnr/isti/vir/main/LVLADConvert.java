package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.features.localfeatures.KeyPoint;
import it.cnr.isti.vir.features.localfeatures.LVLAD;
import it.cnr.isti.vir.features.localfeatures.VLAD;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.WorkingPath;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

public class LVLADConvert {

	public static final String className = "LVLADConvert";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".lfArchive=<input LF archive>");
		System.out.println("- "+className+".dictionary=<input dictionaries directory>");
		System.out.println("- "+className+".vladArchive=<output directory>");
		System.out.println("- ["+className+".VLADPC=<PCA principal components for VLAD>]");
		System.out.println("- ["+className+".VLADPC_n=<n principal components>]");
		System.out.println("- ["+className+".intraNorm=<def false>]");
		System.out.println("- ["+className+".SSR=<def false>]");
		System.out.println("- ["+className+".RN=<def false>]");
		System.out.println("- ["+className+".nRnd=<n>]");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 1 ) {
			usage();
		}

		Launch.launch(LVLADConvert.class.getName(), args[0]);	
	
	}
	
	public static void launch(Properties prop) throws Exception {
		
		File lfArchive_file = PropertiesUtils.getFile( prop, className+".lfArchive");
		File dictionary_file = PropertiesUtils.getFile(prop, className+".dictionary");
		File vladArchive_file = PropertiesUtils.getFile(prop, className+".vladArchive");
		File vladPC_file = PropertiesUtils.getFile_orNull(prop, className+".VLADPC");
		boolean intraNorm = PropertiesUtils.getBoolean(prop, className+".intraNorm", false);
		boolean rn = PropertiesUtils.getBoolean(prop, className+".RN", false);
		Double ssr = PropertiesUtils.getDouble_orNull(prop, className+".SSR");
		Integer nRnd = PropertiesUtils.getInt_orNull(prop, className+".nRnd"); 
		int vladPC_n = -1;
		if (vladPC_file != null ) {
			vladPC_n = PropertiesUtils.getInt(prop, className+".VLADPC_n");
		}
		
		createVLAD(lfArchive_file, dictionary_file, vladArchive_file, vladPC_file, vladPC_n, ssr, intraNorm, rn, nRnd  );		
	}
	

	private static class  ConvertThread implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final TimeManager tm;

		private final LFWords words;
		private final Class<ALocalFeaturesGroup> lfGroup_class;
		private final Double ssr;
		private final boolean intraNorm;
		private final boolean rn;
		private final PrincipalComponents pc;
		private final FeaturesCollectorsArchive outArchive;
		
		ConvertThread(
				Iterator<AbstractFeaturesCollector> it,
				TimeManager tm,
				LFWords words,
				Class<ALocalFeaturesGroup> lfGroup_class,
				Double ssr,
				boolean intraNorm,
				boolean rn,
				PrincipalComponents pc,
				FeaturesCollectorsArchive outArchive
				) {

			this.it = it;
			this.tm = tm;
			this.words = words;
			this.lfGroup_class = lfGroup_class;
			this.ssr = ssr;
			this.intraNorm = intraNorm;
			this.rn = rn;
			this.pc = pc;
			this.outArchive = outArchive;
			
		}

		@Override
		public void run() {
			// each query is processed on an independent thread
			while ( true) {
				AbstractFeaturesCollector currFC = null;
				synchronized ( it ) {
					if ( it.hasNext() ) {
						currFC = it.next();
						tm.reportProgress();
					} else {
						return;
					}
				}
				
				ALocalFeaturesGroup lfs = currFC.getFeature(lfGroup_class);
				
				AbstractID id = ((IHasID) currFC).getID();
				
				if ( lfs == null || lfs.size() == 0 ) {
					System.out.println(id + " was not considered because has no LFs");
				} else {
						
					try {
						FloatsLFGroup resGroup = LVLAD.getLVLAD(lfs.lfArr, words, ssr, intraNorm, rn, pc);
						
						outArchive.add(resGroup, id );
					} catch (Exception e) {
						e.printStackTrace();
					}
						
				}

			}
		}
	}
	
	
	public static void createVLAD(
			File lfArchive_file, File dictionary_file, File bowArchive_file, File vladPC_file,
			int vladPC_n,
			Double ssr,
			boolean intraNorm,
			boolean rn,
			Integer nRnd ) throws Exception {

		
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file );

		LFWords words = new LFWords(dictionary_file);
		
		Class<ALocalFeaturesGroup> lfGroup_class = (Class<ALocalFeaturesGroup>) words.getLocalFeaturesGroupClass();
		
		int[] lfIDs = null;
		if ( nRnd != null ) {
			int tot = inArchive.getNumberOfLocalFeatures(lfGroup_class);
			
			Log.info("Selecting " + nRnd + " random number of local features");
			
			lfIDs = RandomOperations.getDistinctInts(nRnd, tot);
			
			Arrays.sort(lfIDs);
		}
		
		
				
		Log.info("\t" + dictionary_file.getAbsolutePath() + "\t" + words.size());
		
		Log.info("Creating: " + bowArchive_file);
		
		FeaturesCollectorsArchive outArchive = inArchive.getSameType( bowArchive_file );
		
		PrincipalComponents pc = null;
		if ( vladPC_file != null ) { 
			pc = PrincipalComponents.read(vladPC_file);
			if ( vladPC_n > 0 ) pc.setProjDim(vladPC_n);
		}
		TimeManager timeManager = new  TimeManager( inArchive.size() );
		
		int lfCount = 0;
		int lfIDs_i = 0;
		
		if ( lfIDs == null ) {
			
			TimeManager tm = new TimeManager();
			tm.setTotNEle(inArchive.size());
			int nThread = ParallelOptions.reserveNFreeProcessors()+1;
			Thread[] thread = new Thread[nThread];
			Iterator<AbstractFeaturesCollector> it = inArchive.iterator();
			for ( int ti=0; ti<thread.length; ti++ ) {
				thread[ti] = new Thread(
						new ConvertThread(
								it,
								tm,
								words,
								lfGroup_class,
								ssr,
								intraNorm,
								rn,
								pc,
								outArchive
								) );
	        	thread[ti].start();
			}
	        for ( Thread t : thread ) {
	        	t.join();
	        }
	        ParallelOptions.free(nThread-1);
			
		} else {
			
			for (Iterator<AbstractFeaturesCollector> it = inArchive.iterator(); it.hasNext(); ) {
				AbstractFeaturesCollector currFC = it.next();
				
				ALocalFeaturesGroup lfs = currFC.getFeature(lfGroup_class);
				
				AbstractID id = ((IHasID) currFC).getID();
				
				if ( lfs == null || lfs.size() == 0 ) {
					System.out.println(id + " was not considered because has no LFs");
				} else {
						
					for ( ALocalFeature currLF : lfs.lfArr ) {
						if ( lfIDs[lfIDs_i] == lfCount ) {
					 
							KeyPoint kp = currLF.getKeyPoint();					
							float scale = kp.getScale();					
							double range = scale;
						
							ArrayList<ALocalFeature> inRange = lfs.getInRange(kp, range);
						
							ALocalFeature[] temp = new ALocalFeature[inRange.size()];
							inRange.toArray(temp);
							
							VLAD vlad = VLAD.getVLAD(temp, words, ssr, intraNorm, rn);
							if (pc == null ) {
								outArchive.add(vlad, new IDString(String.valueOf(lfCount)) );
							} else {
								Floats f = new Floats(pc.project_float( (IArrayValues) vlad));
								outArchive.add(f, new IDString(String.valueOf(lfCount)) );
							}							
							
							
							lfIDs_i++;
							
							if ( lfIDs_i == lfIDs.length ) break;
						}
						
						lfCount++;
					}
					
					if ( lfIDs_i == lfIDs.length ) break;	
					
				}
					
				
				timeManager.reportProgress();
					
			}
		}
		
		System.out.print( outArchive.getInfo() );
		
		System.out.println("VLADs were created in " + timeManager.getTotalTime_STR());
		
		System.out.println();
		
		outArchive.close();
		
	}
	
	public static final File getBoWArchivesFile( String prefix, int n ) {
		String outFileName = prefix + "_BoW_" + n + ".dat";
		return WorkingPath.getFile(outFileName);	
	}
}
