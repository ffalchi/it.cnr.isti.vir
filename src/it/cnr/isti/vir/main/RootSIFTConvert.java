package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.util.Properties;

public class RootSIFTConvert {

	public static void usage() {
		System.out.println("RootSIFTConvert <properties filename>.properties");
		System.out.println("or:    RootSIFTConvert <in-SIFTArchive> <out-SIFTArchive>");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- SIFTArchive=<ArchiveFileName>");
		System.out.println("- RootSIFTArchive=<ArchiveFileName>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 0 || args.length > 2) {
			usage();
		} else if ( args.length == 1 ) {
			Launch.launch(RootSIFTConvert.class.getName(), args[0]);
		} else {
			convert(new File(args[0]), new File(args[1]));
		}
		
	}
		
	public static void launch(Properties prop) throws Exception {
		
		convert(
				PropertiesUtils.getFile(prop, "SIFTArchive"),
				PropertiesUtils.getFile(prop, "RootSIFTArchive")
				);
	}
	
	public static void convert(File inSIFTArchive, File outRootSIFTArchive) throws Exception {
		FeaturesCollectorsArchive ina = new FeaturesCollectorsArchive(inSIFTArchive);
		FeaturesCollectorsArchive outa = ina.getSameType(outRootSIFTArchive);
		
		int count=0;
		TimeManager tm = new TimeManager();
		Log.info(
				"Converting SIFTs in " + inSIFTArchive.getAbsolutePath() +
				" to RootSIFTs in " + outRootSIFTArchive.getAbsolutePath() );
		for ( AbstractFeaturesCollector fc : ina ) {
			count++;
			if ( tm.hasToOutput() )
				Log.info_verbose(tm.getProgressString(count, ina.size()));
			SIFTGroup sifts = fc.getFeature(SIFTGroup.class);
			RootSIFTGroup rootSIFTs = new RootSIFTGroup(sifts, fc);
			fc.discard(SIFTGroup.class);
			fc.add(rootSIFTs);
			
			outa.add(fc);
		}
		
		outa.close();
		ina.close();
	}

}
