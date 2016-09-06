package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.Bmm;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

public class BmmMain {

	public static final String className = "BmmMain";
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("-"+className+".inArchive=<archive file name>");
		System.out.println("-"+className+" .k=<number of Bernoulli>");
		System.out.println("- "+className+".outFileName=<file name>");
		
	//	System.out.println("Properties file optionals:");
		//TO DO: maxNObjs=<max number of random objects to consider>]"
		//System.out.println("- "+className+".featureClass=<class of the feature>");
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(BmmMain.class.getName(), args[0]);
		}
	}

	public static void launch(Properties prop) throws Exception {
		// Input Archive (learning archive)
		File inFile  = PropertiesUtils.getFile(prop,className+".inArchive");
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive( inFile );
		//number of Bernoulli
		String outAbsolutePath = PropertiesUtils.getAbsolutePath(prop, className+".outFileName");
		//centroids file name
		File folder=(new File(outAbsolutePath)).getParentFile();
		 if (!folder.exists()) {
			 folder.mkdirs();
 			}
		
		int k = PropertiesUtils.getInt(prop, className+".k");

		
		Log.info("BMM computation, number of Bernoulli  " + k);
		Log.info("Learning Archive:"+ inArchive.getInfo());


		Bmm bmm = new Bmm(inArchive, k);
		// write bmmFileName
		bmm.writeData(outAbsolutePath);
		
		inArchive.close();
	}
}	
