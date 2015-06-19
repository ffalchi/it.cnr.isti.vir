package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
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
		System.out.println("- [RootSIFTFloats]=<true (false def)>");
		System.out.println("- [RootSIFT.L2Norm]=<true (false def)>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 0 || args.length > 2) {
			usage();
		} else if ( args.length == 1 ) {
			Launch.launch(RootSIFTConvert.class.getName(), args[0]);
		} else {
			convert(new File(args[0]), new File(args[1]), false, false);
		}
		
	}
		
	public static void launch(Properties prop) throws Exception {
		
		convert(
				PropertiesUtils.getFile(prop, "SIFTArchive"),
				PropertiesUtils.getFile(prop, "RootSIFTArchive"),
				PropertiesUtils.getBoolean(prop, "RootSIFT.L2Norm", false),
				PropertiesUtils.getBoolean(prop, "RootSIFTFloats", false)
				);
	}
	
	public static void convert(File inSIFTArchive, File outRootSIFTArchive, boolean l2Norm, boolean floats ) throws Exception {
		FeaturesCollectorsArchive ina = new FeaturesCollectorsArchive(inSIFTArchive);
		FeaturesCollectorsArchive outa = ina.getSameType(outRootSIFTArchive);
		
		RootSIFT.setL2Norm(l2Norm);
		
		int count=0;
		TimeManager tm = new TimeManager();
		if ( !floats )
			Log.info(
				"Converting SIFTs in " + inSIFTArchive.getAbsolutePath() + "\n" +
				" to RootSIFTs in " + outRootSIFTArchive.getAbsolutePath() );
		else
			Log.info(
				"Converting SIFTs in " + inSIFTArchive.getAbsolutePath() + "\n" +
				" to RootSIFTFloat in " + outRootSIFTArchive.getAbsolutePath() );
		for ( AbstractFeaturesCollector fc : ina ) {
			count++;
			Log.info_verbose_progress(tm, count, ina.size());
			SIFTGroup sifts = fc.getFeature(SIFTGroup.class);
			if ( !floats ) {
				RootSIFTGroup rootSIFTs = new RootSIFTGroup(sifts, fc);
				fc.discard(SIFTGroup.class);
				fc.add(rootSIFTs);
			} else {
				FloatsLFGroup rootSIFTs = RootSIFTGroup.getFloatsLFGroup(sifts, fc);
				fc.discard(SIFTGroup.class);
				fc.add(rootSIFTs);
			}
			
			
			
			outa.add(fc);
		}
		
		outa.close();
		ina.close();
	}

}
