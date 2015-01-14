package it.cnr.isti.vir.main;

import it.cnr.isti.vir.clustering.Centroids;
import it.cnr.isti.vir.clustering.KMeans;
import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.similarity.ILFSimilarity;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;

public class KMeansMain {

	static final double distRedThr_def = 0.999;
	
	public static void usage() {
		System.out.println("PCAProject <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- kMeans.data=<archive file name>");
		System.out.println("- kMeans.k=<number of clusters>");
		System.out.println("- kMeans.outFileName=<file name>");
		System.out.println("- kMeans.similarity=<similarity class name>");
		System.out.println("Properties file optionals:");
		System.out.println("- [kMeans.maxNObjs=<max number of random objects to consider>]");
		System.out.println("- [kMeans.medoid=<true or false (def. false)>]");
		System.out.println("- [kMeans.distRedThr=<" + distRedThr_def + " default>]");
		System.out.println("- [kMeans.nIterations=< 1 default>");
		System.out.println("- [kMeans.maxMinPerIteration=<minutes>]");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(KMeansMain.class.getName(), args[0]);
		}
		
	}
	
	public static void launch(Properties prop) throws Exception {
		
		File dataFile  = PropertiesUtils.getFile(prop, "kMeans.data");
		int k = PropertiesUtils.getInt_maxIfNotExists(prop, "kMeans.k");
		int maxNObjs = PropertiesUtils.getInt(prop, "kMeans.maxNObjs");
		String outAbsolutePath = PropertiesUtils.getAbsolutePath(prop, "kMeans.outFileName");
		ISimilarity sim = (ISimilarity) PropertiesUtils.instantiateObjectWithProperties(prop, "kMeans.similarity");
		boolean medoid = PropertiesUtils.getIfExistsDefFalse(prop, "kMeans.medoid");
		
		double distRedThr = PropertiesUtils.getDouble_orDefault(prop, "kMeans.distRedThr", distRedThr_def);
		int nIterations = PropertiesUtils.getInt_orDefault(prop, "kMeans.nIterations", 1);
		int maxMinPerIteration = PropertiesUtils.getInt_orDefault(prop, "kMeans.maxMinPerIteration", Integer.MAX_VALUE);
		
		Class fClass = sim.getRequestedFeaturesClasses().getRequestedClass();
		Class fGroupClass = null;
		if ( sim instanceof ILFSimilarity ) {
			fGroupClass = ((ILFSimilarity) sim).getRequestedFeatureGroupClass();
		}
		Log.info("Requested features group: " + fGroupClass);
		
		FeaturesCollectorsArchive archive = new FeaturesCollectorsArchive( dataFile );
		
		Log.info(archive.getInfo());
		
		ArrayList<AbstractFeature> list;
		if ( fGroupClass != null ) {
			list = (ArrayList) archive.getRandomLocalFeatures(fGroupClass, maxNObjs);
		} else {
			list = archive.getRandomFeatures(fClass, maxNObjs);
		}
		
		double minDistortion = Double.MAX_VALUE;
		File bestOutFile = null;
		
		for ( int i=0; i<nIterations; i++ ) {
			
			KMeans<AbstractFeature> kmeans = new KMeans<AbstractFeature>(list, sim);
			if ( medoid == true ) kmeans.setIsMedoid(medoid);
			Log.info("Setting medoid to " + medoid );
			kmeans.setDistRedThr(distRedThr);
			kmeans.setMaxMinPerIteration(maxMinPerIteration);
			kmeans.kMeansPP(k);
			kmeans.runAlgorithm(null);
			
			String fName = outAbsolutePath + "_" +kmeans.getDistortion()+ ".dat";
			File file = new File(fName);
			if ( kmeans.getDistortion() < minDistortion ) {
				minDistortion = kmeans.getDistortion();
				bestOutFile = file;
			}
			
			System.out.println("Writing to " + fName);
			DataOutputStream out = new DataOutputStream( new BufferedOutputStream ( new FileOutputStream(file) ));
			if ( fGroupClass != null ) {
				// LocalFeatures
				LFWords<AbstractFeature> words = new LFWords<AbstractFeature>(kmeans.getCentroids(true), (ILFSimilarity) sim);
				words.writeData(out);			
				out.close();	
			} else {			
				Centroids centroids = new Centroids(kmeans.getCentroids(true));
				centroids.writeData(out);
				out.close();
			}

		}

		System.out.println("Minimun distortion: " + minDistortion);
		File outFile = new File(outAbsolutePath);
		Files.copy(bestOutFile.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		archive.close();
	}
}	
