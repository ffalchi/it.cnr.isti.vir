package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.WorkingPath;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

public class BoWConvert {

	public static void usage() {
		System.out.println("Usage: BoWConvert <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- BoWConvert.lfArchive=<input LF archive>");
		System.out.println("- BoWConvert.dictionary=<input dictionaries directory>");
		System.out.println("- BoWConvert.bowArchive=<output directory>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 1 ) {
			usage();
		}

		Launch.launch(BoWConvert.class.getName(), args[0]);	
	
	}
	
	public static void launch(Properties prop) throws Exception {
		
		File lfArchive_file = PropertiesUtils.getFile( prop, "BoWConvert.lfArchive");
		File bowArchive_file = PropertiesUtils.getFile(prop, "BoWConvert.bowArchive");
		File dictionary_file = PropertiesUtils.getFile(prop, "BoWConvert.dictionary");
		
		createBoW(lfArchive_file, dictionary_file, bowArchive_file );		
	}
	
	/**
	 * @param lfArchive_file	input archive
	 * @param dictionary_file	word coming from kMeans
	 * @param bowArchive_file	output archive
	 * @throws Exception
	 */
	public static void createBoW(File lfArchive_file, File dictionary_file, File bowArchive_file ) throws Exception {

		
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file );

		LFWords words = new LFWords(dictionary_file);
		Class<ALocalFeaturesGroup> lfGroup_class =  (Class<ALocalFeaturesGroup>) words.getLocalFeaturesGroupClass();
		
		Log.info("\t" + dictionary_file.getAbsolutePath() + "\t" + words.size());
		
		Log.info("Creating: " + bowArchive_file);
		
		FeaturesCollectorsArchive outArchive = inArchive.getSameType( bowArchive_file );
		
		TimeManager timeManager = new  TimeManager();
		for (Iterator<AbstractFeaturesCollector> it = inArchive.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector curr = it.next();
			
			ALocalFeaturesGroup lfs = curr.getFeature(lfGroup_class);
			
			BoFLFGroup bofGroup = new BoFLFGroup( lfs, words);
			
			outArchive.add(bofGroup, ((IHasID) curr).getID() );
			
			Log.info_verbose_progress(timeManager, outArchive.size(), inArchive.size());				
		}
		
		System.out.print( outArchive.getInfo() );
		
		System.out.println("BoW were created in " + timeManager.getTotalTime_STR());
		
		System.out.println();
		
		outArchive.close();
		
	}
	
	public static final File getBoWArchivesFile( String prefix, int n ) {
		String outFileName = prefix + "_BoW_" + n + ".dat";
		return WorkingPath.getFile(outFileName);	
	}
}
