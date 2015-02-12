package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.VLAD;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.WorkingPath;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

public class VLADConvert {

	public static final String className = "VLADConvert";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".lfArchive=<input LF archive>");
		System.out.println("- "+className+".dictionary=<input dictionaries directory>");
		System.out.println("- "+className+".vladArchive=<output directory>");
		System.out.println("- ["+className+".VLADPC=<PCA principal components for VLAD>]");
		System.out.println("- ["+className+".VLADPC_n=<n principal components>]");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 1 ) {
			usage();
		}

		Launch.launch(VLADConvert.class.getName(), args[0]);	
	
	}
	
	public static void launch(Properties prop) throws Exception {
		
		File lfArchive_file = PropertiesUtils.getFile( prop, className+".lfArchive");
		File dictionary_file = PropertiesUtils.getFile(prop, className+".dictionary");
		File vladArchive_file = PropertiesUtils.getFile(prop, className+".vladArchive");
		File vladPC_file = PropertiesUtils.getFile_orNull(prop, className+".vladPC_file");
		int vladPC_n = -1;
		if (vladPC_file != null ) {
			vladPC_n = PropertiesUtils.getInt(prop, className+".VLADPC_n");
		}
		
		createVLAD(lfArchive_file, dictionary_file, vladArchive_file, vladPC_file, vladPC_n );		
	}
	

	public static void createVLAD(File lfArchive_file, File dictionary_file, File bowArchive_file, File vladPC_file, int vladPC_n ) throws Exception {

		
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file );

		LFWords words = new LFWords(dictionary_file);
		Class<ALocalFeaturesGroup> lfGroup_class =  (Class<ALocalFeaturesGroup>) words.getLocalFeaturesGroupClass();
		
		Log.info("\t" + dictionary_file.getAbsolutePath() + "\t" + words.size());
		
		Log.info("Creating: " + bowArchive_file);
		
		FeaturesCollectorsArchive outArchive = inArchive.getSameType( bowArchive_file );
		
		PrincipalComponents pc = null;
		if ( vladPC_file != null ) { 
			PrincipalComponents.read(vladPC_file);
			if ( vladPC_n > 0 ) pc.setProjDim(vladPC_n);
		}
		TimeManager timeManager = new  TimeManager();
		for (Iterator<AbstractFeaturesCollector> it = inArchive.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector curr = it.next();
			
			ALocalFeaturesGroup lfs = curr.getFeature(lfGroup_class);
			
			
			VLAD vlad = VLAD.getVLAD(lfs, words);
			if (pc != null ) {
				outArchive.add(vlad, ((IHasID) curr).getID() );
			} else {
				Floats f = new Floats(pc.project_float( (IArrayValues) vlad));
				outArchive.add(f, ((IHasID) curr).getID() );
			}
			
			Log.info_verbose_progress(timeManager, outArchive.size(), inArchive.size());
				
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
