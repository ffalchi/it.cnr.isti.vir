package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Gmm;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

public class GmmMain {

	public static final String className = "GmmMain";
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".inArchive=<archive file name>");
		System.out.println("- "+className+".k=<number of Gaussians>");
		System.out.println("- "+className+".outFileName=<file name>");
		System.out.println("- "+className+".featureClass=<class of the feature>");
		System.out.println("- "+className+".centroidsFileName=<File Name>");
		System.out.println("Properties file optionals:");
		System.out.println("- ["+className+".minDistortionFileName=<File Name>]");
		System.exit(0);
	}

	public static void main(String[] args) throws Exception {
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(GmmMain.class.getName(), args[0]);
		}
	}

	public static void launch(Properties prop) throws Exception {
		// Input Archive (learning archive)
		File inFile  = PropertiesUtils.getFile(prop,className+".inArchive");
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive( inFile );
		//number of Gaussian
		String outAbsolutePath = PropertiesUtils.getAbsolutePath(prop, className+".outFileName");
		//centroids file name
		File centroidsFile  = PropertiesUtils.getFile(prop, className+".centroidsFileName");
		int k = PropertiesUtils.getInt(prop,className+ ".k");

		// Features or Local Features Group class
		Class c = PropertiesUtils.getClass(prop, className+".featureClass");

		File minDistortionFile = PropertiesUtils.getFile_orNull(prop, className+".minDistortionOutFileName");

		Log.info("GMM computation, number of Gaussian " + k);
		Log.info("Learning Archive:"+ inArchive.getInfo());

		LFWords<AbstractFeature> centroid = new LFWords(centroidsFile);
		double minDistSqr=0.0;

		if(minDistortionFile !=null) {
			BufferedInputStream bf = new BufferedInputStream(new FileInputStream(minDistortionFile));
			// read min distortion
			byte[] data = new byte[k];
			bf.read(data);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			minDistSqr  = buffer.getDouble();
			bf.close();
		}
		else {
			if ( ALocalFeature.class.isAssignableFrom(c)  ) {
				Class<? extends ALocalFeaturesGroup> cGroup = ALocalFeaturesGroup.getGroupClass( c );
				Log.info("Group class has been set to: " + cGroup);
				int nLF_archive = inArchive.getNumberOfLocalFeatures(cGroup);			
				int counter = 0;
				for ( AbstractFeaturesCollector fc : inArchive ) {
					ALocalFeaturesGroup lfGroup = fc.getFeature(cGroup);
					if ( lfGroup == null || lfGroup.size() == 0 ) continue;
					ALocalFeature[] lfArr = lfGroup.lfArr;
					for(ALocalFeature lf:lfArr) {
						double dist=centroid.getNNDistance(lf);
						minDistSqr+=dist*dist;
						counter++;	
					}
				}
				minDistSqr/=counter;
				Log.info("Set Min Distortion (squared): " +minDistSqr );
			} else {			
				int counter = 0;
				for ( AbstractFeaturesCollector fc : inArchive ) {
					AbstractFeature af = fc.getFeature(c);
					if ( af == null ) continue;
					double dist=centroid.getNNDistance(af);
					minDistSqr+=dist*dist;
					counter++;
				}
				minDistSqr/=counter;
				Log.info("Set Min Distortion (squared): " +minDistSqr );
			}

		}

		Gmm gmm = new Gmm(inArchive, centroid, (float) minDistSqr);
		// write gmmFileName
		gmm.writeData(outAbsolutePath);
		
		inArchive.close();
	}
}	
