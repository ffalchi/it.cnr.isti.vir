package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class PCAProject {

	public static void usage() {
		System.out.println("PCAProject <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- PCAProject.inArchive=<archive file name>");
		System.out.println("- PCAProject.outArchive=<archive file name>");
		System.out.println("- PCA.PC=<principal components file>");
		System.out.println("- [PCAProject.dim]");
		System.out.println("- PCA.FeatureClass=<feature class to be projects>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(RootSIFTConvert.class.getName(), args[0]);
		}
		
	}
	
	static  class Project implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final FeaturesCollectorsArchive out;
		private final PrincipalComponents pc;
		private final Class c;
		private final TimeManager tm;
		
		public Project(
				Iterator<AbstractFeaturesCollector> it,
				FeaturesCollectorsArchive out,
				PrincipalComponents pc,
				Class c,
				TimeManager tm ) {
			this.it = it;
			this.out = out;
			this.c = c;
			this.tm = tm;
			this.pc = pc;
		}

		@Override
		public void run() {
			AbstractFeaturesCollector fc = null;
			while (true) {
				synchronized ( it ) {
					if ( it.hasNext() ) fc = it.next();
					else break;
				}
				Log.info_verbose_progress(tm, out.size());
				
				AbstractFeature currf = fc.getFeature(c);
				Floats projected = new Floats(pc.project_float( (IArrayValues) currf));
				
				AbstractFeaturesCollector newFC = fc.createWithSameInfo(projected);

				try {
					out.add(newFC);
				} catch (ArchiveException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	static  class Project_LF implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final FeaturesCollectorsArchive out;
		private final PrincipalComponents pc;
		private final Class<ALocalFeaturesGroup> lfGroupClass;
		private final TimeManager tm;
		
		public Project_LF(
				Iterator<AbstractFeaturesCollector> it,
				FeaturesCollectorsArchive out,
				PrincipalComponents pc,
				Class<ALocalFeaturesGroup> lfGroupClass,
				TimeManager tm ) {
			this.it = it;
			this.out = out;
			this.lfGroupClass = lfGroupClass;
			this.tm = tm;
			this.pc = pc;
		}

		@Override
		public void run() {
			AbstractFeaturesCollector fc = null;
			while (true) {
				synchronized ( it ) {
					if ( it.hasNext() ) fc = it.next();
					else break;
				}
				Log.info_verbose_progress(tm, out.size());
				
				ALocalFeaturesGroup currGroup = fc.getFeature(lfGroupClass);
				ALocalFeature[] currLFs = currGroup.lfArr;
				FloatsLF[] projected = new FloatsLF[currGroup.size()];
				for ( int i=0; i<currLFs.length; i++ ) {
					projected[i] = new FloatsLF(pc.project_float( (IArrayValues) currLFs[i])); 
				}
				FloatsLFGroup projectedGroup = new FloatsLFGroup(projected);
				AbstractFeaturesCollector newFC = fc.createWithSameInfo(projectedGroup);
				projectedGroup.setLinkedFC(newFC);
				
				try {
					out.add(newFC);
				} catch (ArchiveException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	public static void launch(Properties prop) throws Exception {
		
		File inFile  = PropertiesUtils.getFile(prop, "PCAProject.inArchive");
		File outFile = PropertiesUtils.getFile(prop, "PCAProject.outArchive");
		File pcFile  = PropertiesUtils.getFile(prop, "PCA.PC"); 
		Class c = PropertiesUtils.getClass(prop, "PCA.FeatureClass");
		int dim = PropertiesUtils.getInt_orDefault(prop, "PCAProject.dim", -1);
		
		FeaturesCollectorsArchive in = new FeaturesCollectorsArchive(inFile);
		FeaturesCollectorsArchive out = in.getSameType(outFile);
		
		PrincipalComponents pc = PrincipalComponents.read(pcFile);
		
		Log.info(pc.toString());
		
		pc.setProjDim(dim);
		
		Log.info( in.getInfo() );
		
		int count=0;
		TimeManager tm = new TimeManager();
		tm.setTotNEle(in.size());
		if ( ALocalFeature.class.isAssignableFrom(c)  ) {
			Class<ALocalFeaturesGroup> lfGroupClass = ALocalFeaturesGroup.getGroupClass( c );

			int nThread = ParallelOptions.reserveNFreeProcessors() +1 ;
			
			Thread[] thread = new Thread[nThread];
			Iterator<AbstractFeaturesCollector> it = in.iterator();
			for ( int ti=0; ti<nThread; ti++ ) {
				thread[ti] = new Thread(new Project_LF(it, out, pc, lfGroupClass, tm ));
				thread[ti].start();
			}
		        
	        for ( int ti=0; ti<thread.length && thread[ti] != null; ti++ ) {
	        	try {
					thread[ti].join();
					thread[ti] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		        
	        ParallelOptions.free(nThread-1);
			
//			for ( AbstractFeaturesCollector fc : in ) {
//				count++;
//				Log.info_verbose_progress(tm, count, in.size());
//				
//				ALocalFeaturesGroup currGroup = fc.getFeature(lfGroupClass);
//				ALocalFeature[] currLFs = currGroup.lfArr;
//				FloatsLF[] projected = new FloatsLF[currGroup.size()];
//				for ( int i=0; i<currLFs.length; i++ ) {
//					projected[i] = new FloatsLF(pc.project_float( (IArrayValues) currLFs[i])); 
//				}
//				FloatsLFGroup projectedGroup = new FloatsLFGroup(projected);
//				AbstractFeaturesCollector newFC = fc.createWithSameInfo(projectedGroup);
//				projectedGroup.setLinkedFC(newFC);
//				
//				out.add(newFC);
//			}
		} else {
			int nThread = ParallelOptions.reserveNFreeProcessors() +1 ;
			
			Thread[] thread = new Thread[nThread];
			Iterator<AbstractFeaturesCollector> it = in.iterator();
			for ( int ti=0; ti<nThread; ti++ ) {
				thread[ti] = new Thread(new Project(it, out, pc, c, tm ));
				thread[ti].start();
			}
		        
	        for ( int ti=0; ti<thread.length && thread[ti] != null; ti++ ) {
	        	try {
					thread[ti].join();
					thread[ti] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		        
	        ParallelOptions.free(nThread-1);
			
//			for ( AbstractFeaturesCollector fc : in ) {
//				count++;
//				if ( tm.hasToOutput() )
//					Log.info_verbose(tm.getProgressString(count, in.size()));
//				
//				AbstractFeature currf = fc.getFeature(c);
//				Floats projected = new Floats(pc.project_float( (IArrayValues) currf));
//				
//				AbstractFeaturesCollector newFC = fc.createWithSameInfo(projected);
//
//				out.add(newFC);
//			}
		}
		
		in.close();
		out.close();
		
	}
}
