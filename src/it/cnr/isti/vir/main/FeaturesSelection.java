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

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class FeaturesSelection {
	public static void usage() {
		System.out.println(" FeaturesSelection <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- inArchiveFileName=<archive file name>");
		System.out.println("- outArchiveFileName=<file name>");
		System.out.println("- featureClass=<class of the feature>");
		System.out.println("- maxNFeatures=<n>");
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
		// maxNFeatures
		int maxNFeatures = PropertiesUtils.getInt(prop, "maxNFeatures");
		
		// Input Archive
		File inFile  = PropertiesUtils.getFile(prop, "inArchiveFileName");
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive( inFile );
	
		// Output Archive
		File outFile  = PropertiesUtils.getFile(prop, "outArchiveFileName");
		FeaturesCollectorsArchive outArchive = inArchive.getSameType(outFile);
		
		// Features or Local Features Group class
		Class c = PropertiesUtils.getClass(prop, "featureClass");
		
		// Getting probability 
		if ( ALocalFeature.class.isAssignableFrom(c)  ) {
			Class<? extends ALocalFeaturesGroup> cGroup = ALocalFeaturesGroup.getGroupClass( c );
			Log.info("Group class has been set to: " + cGroup);
			int nLF_archive = inArchive.getNumberOfLocalFeatures(cGroup);
			Log.info( inFile.getAbsolutePath() + " contains " + nLF_archive + " " + c +"\n");
			double prob = maxNFeatures / (double) nLF_archive;
			if ( prob > 1.0 || prob < 0) prob = 1.0;
			
			int counter = 0;
			TimeManager tm = new TimeManager();
			for ( AbstractFeaturesCollector fc : inArchive ) {
				ALocalFeaturesGroup lfGroup = fc.getFeature(cGroup);
				if ( lfGroup == null || lfGroup.size() == 0 ) continue;
				ALocalFeature[] lfArr = lfGroup.lfArr;
				ArrayList<ALocalFeature> arrList = null;
				
				ALocalFeaturesGroup mewLFGroup = lfGroup.getReducedRandom(prob);
				
				if ( mewLFGroup.size() != 0 ) {
					counter += mewLFGroup.size();				
				
					outArchive.add(fc.createWithSameInfo(mewLFGroup));
				}
				
				Log.info_verbose_progress(tm, counter, inArchive.size());
				
			}
			
			
		} else {
			double prob = maxNFeatures / (double) inArchive.size();
			if ( prob > 1.0 || prob < 0) prob = 1.0;
			
			int counter = 0;
			TimeManager tm = new TimeManager();
			for ( AbstractFeaturesCollector fc : inArchive ) {
				AbstractFeature af = fc.getFeature(c);
				if ( af == null ) continue;
				
				counter++;
				Log.info_verbose_progress(tm, counter, inArchive.size());
				if ( RandomOperations.trueORfalse(prob) ) {
					outArchive.add(fc.createWithSameInfo(af));
				}
			}
			Log.info(outArchive.size() + " " + c.getName() + " were selected" );
		}
		
		outArchive.close();
		inArchive.close();
	}

	
	public static void selection(String inArchiveFileName,
			String outArchiveFileName,String featureClass,int maxNFeatures) throws Exception {
		 Class c=Class.forName(featureClass);
		// Input Archive
				File inFile  = new File(inArchiveFileName);
				FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive( inFile );
			
				// Output Archive
				File outFile  = new File(outArchiveFileName);
				FeaturesCollectorsArchive outArchive = inArchive.getSameType(outFile);
		// Getting probability 
				if ( ALocalFeature.class.isAssignableFrom(c)  ) {
					Class<? extends ALocalFeaturesGroup> cGroup = ALocalFeaturesGroup.getGroupClass( c );
					Log.info("Group class has been set to: " + cGroup);
					int nLF_archive = inArchive.getNumberOfLocalFeatures(cGroup);
					Log.info( inFile.getAbsolutePath() + " contains " + nLF_archive + " " + c +"\n");
					double prob = maxNFeatures / (double) nLF_archive;
					if ( prob > 1.0 || prob < 0) prob = 1.0;
					
					int counter = 0;
					TimeManager tm = new TimeManager();
					for ( AbstractFeaturesCollector fc : inArchive ) {
						ALocalFeaturesGroup lfGroup = fc.getFeature(cGroup);
						if ( lfGroup == null || lfGroup.size() == 0 ) continue;
						ALocalFeature[] lfArr = lfGroup.lfArr;
						ArrayList<ALocalFeature> arrList = null;
						
						ALocalFeaturesGroup mewLFGroup = lfGroup.getReducedRandom(prob);
						
						if ( mewLFGroup.size() != 0 ) {
							counter += mewLFGroup.size();				
						
							outArchive.add(fc.createWithSameInfo(mewLFGroup));
						}
						
						Log.info_verbose_progress(tm, counter, inArchive.size());
						
					}
					
					
				} else {
					double prob = maxNFeatures / (double) inArchive.size();
					if ( prob > 1.0 || prob < 0) prob = 1.0;
					
					int counter = 0;
					TimeManager tm = new TimeManager();
					for ( AbstractFeaturesCollector fc : inArchive ) {
						AbstractFeature af = fc.getFeature(c);
						if ( af == null ) continue;
						
						counter++;
						Log.info_verbose_progress(tm, counter, inArchive.size());
						if ( RandomOperations.trueORfalse(prob) ) {
							outArchive.add(fc.createWithSameInfo(af));
						}
					}
					Log.info(outArchive.size() + " " + c.getName() + " were selected" );
				}
				outArchive.close();
				inArchive.close();
	}
	
}
