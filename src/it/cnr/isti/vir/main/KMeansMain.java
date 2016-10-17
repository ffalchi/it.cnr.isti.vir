/*******************************************************************************
 * Copyright (c), Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

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

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;

public class KMeansMain {

	static final double distRedThr_def = 0.000001;
	
	public static void usage() {
		System.out.println("KMeansMain <properties filename>.properties");
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
		System.out.println("- [kMeans.minDistortionOutFileName=<File Name>]");	
		System.out.println("- [kMeans.learningPointOutFileName=<File Name>]");
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
		int k = PropertiesUtils.getInt(prop, "kMeans.k");
		int maxNObjs = PropertiesUtils.getInt_maxIfNotExists(prop, "kMeans.maxNObjs");
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
		
		File minDistortionOutFile  = PropertiesUtils.getFile_orNull(prop,"kMeans.minDistortionOutFileName");
		File learningPointOutFile  = PropertiesUtils.getFile_orNull(prop,"kMeans.learningPointOutFileName");
		
		FeaturesCollectorsArchive archive = new FeaturesCollectorsArchive( dataFile );
		
		Log.info(archive.getInfo());
		
		ArrayList<AbstractFeature> list;
		if ( fGroupClass != null ) {
			if(learningPointOutFile!=null)
				list = (ArrayList) archive.getRandomLocalFeatures(fGroupClass, maxNObjs, true,learningPointOutFile);
			else
			list = (ArrayList) archive.getRandomLocalFeatures(fGroupClass, maxNObjs, true);
		} else {
			if(learningPointOutFile!=null)
				list = archive.getRandomFeatures(fClass, maxNObjs,learningPointOutFile);
			else
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
		
		if ( minDistortionOutFile != null ) {
			DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(minDistortionOutFile)));
			outStream.writeDouble(minDistortion);
			outStream.close();
		}
		
		File outFile = new File(outAbsolutePath);
		Files.copy(bestOutFile.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		
		archive.close();
	}
}	
