package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

public class FeaturesCollectorsArchiveMerge {

	public static final String className = "FeaturesCollectorsArchiveMerge";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- archive1FileName=<archive file name>");
		System.out.println("- archive2FileName=<file name>");
		System.out.println("- outputArchiveFileName=<file name>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(FeaturesSelection.class.getName(), args[0]);
		}
		
	}
	
	
	public static void launch(Properties prop) throws Exception {
	
		File f1 = PropertiesUtils.getFile(prop, "archive1FileName");
		File f2 = PropertiesUtils.getFile(prop, "archive2FileName");
		File outf = PropertiesUtils.getFile(prop, "outputArchiveFileName");
		
	}
		
		
	public static void launch(File f1, File f2, File outf)  throws Exception {
		
		
		FeaturesCollectorsArchive fca1 = new FeaturesCollectorsArchive(f1);
		FeaturesCollectorsArchive fca2 = new FeaturesCollectorsArchive(f2);
		
		FeaturesCollectorsArchive_Buffered outFca
			= FeaturesCollectorsArchive_Buffered.create( outf, fca1.getIDClass(), FeaturesCollectorArr.class );
	
		for ( AbstractFeaturesCollector fc1 : fca1 ) {
			AbstractID id = ((IHasID) fc1).getID();
			AbstractFeaturesCollector fc2 = fca2.get( id  );
			
			FeaturesCollectorArr outFC = new FeaturesCollectorArr( fc1.getFeatures(), id );
			outFC.addAll(fc2.getFeatures());
			outFca.add( outFC );
			
		}
		
		outFca.close();
	}
}
