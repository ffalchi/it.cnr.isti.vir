package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.FloatsL2Norm_Bytes;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.util.Properties;

public class Floats2Bytes {
	public static void usage() {
		System.out.println("Floats2Bytes <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- Floats2Bytes.in=<archive file name>");
		System.out.println("- Floats2Bytes.out=<archive file name>");
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(KMeansMain.class.getName(), args[0]);
		}
		
	}
	
	public static void launch(Properties prop) throws Exception {
		File in = PropertiesUtils.getFile(prop, "Floats2Bytes.in");
		File out = PropertiesUtils.getFile(prop, "Floats2Bytes.out");
		
		launch(in, out);
	}
	
	public static void launch(String inFN, String outFN ) throws Exception {
		launch(new File(inFN), new File(outFN));
	}
	
	public static void launch(File inF, File outF) throws Exception {
		FeaturesCollectorsArchive inFCA = new FeaturesCollectorsArchive(inF);
		FeaturesCollectorsArchive_Buffered outFCA = 
				new FeaturesCollectorsArchive_Buffered(
						outF,
						inFCA.getIDClass(),
						inFCA.getFcClass());
		
		TimeManager tm = new TimeManager(inFCA.size());
		for ( AbstractFeaturesCollector f : inFCA ) {
			Floats ff = f.getFeature(Floats.class);
			
			FloatsL2Norm_Bytes bytes = new FloatsL2Norm_Bytes(ff.values);
			
			f.discard(Floats.class);
			f.add(bytes);
			outFCA.add(f);
			tm.reportProgress();
		}
		
		outFCA.close();
		inFCA.close();
		
	}
}
