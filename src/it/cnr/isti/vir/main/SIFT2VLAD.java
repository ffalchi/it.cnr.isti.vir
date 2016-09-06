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
import it.cnr.isti.vir.features.utils.VLADAggregator;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.WorkingPath;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

public class SIFT2VLAD {

	public static final String className = "SIFT2VLAD";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".siftArchive=<file name>");
		System.out.println("- "+className+".outArchive=<file name>");
		
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 1 ) {
			usage();
		}

		Launch.launch(SIFT2VLAD.class.getName(), args[0]);	
	
	}
	

	public static void launch(Properties prop) throws Exception {
		
		File lfArchive_file = PropertiesUtils.getFile( prop, className+".siftArchive");
		File outArchive_file = PropertiesUtils.getFile(prop, className+".outArchive");
		
		VLADAggregator aggregator = new VLADAggregator(prop);
		
		createVLAD(aggregator, lfArchive_file, outArchive_file );
		
	}
	

	
	public static void createVLAD(VLADAggregator aggregator, File lfArchive_file, File vladArchive_file) throws Exception {

		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file);

		FeaturesCollectorsArchive outArchive = inArchive.getSameType(vladArchive_file);

		TimeManager timeManager = new TimeManager();
		for (Iterator<AbstractFeaturesCollector> it = inArchive.iterator(); it.hasNext();) {
			AbstractFeaturesCollector curr = it.next();

			AbstractFeature f = aggregator.get(curr);
			outArchive.add(f, ((IHasID) curr).getID());


			Log.info_verbose_progress(timeManager, outArchive.size(),
					inArchive.size());

		}

		System.out.print(outArchive.getInfo());

		System.out.println("VLADs were created in "
				+ timeManager.getTotalTime_STR());

		System.out.println();

		outArchive.close();

	}
	
//	public static void createVLAD(
//			File lfArchive_file, File rootSIFT_PC_file,
//			File dictionary_file, File bowArchive_file, File vladPC_file,
//			int rootSIFTPC_N, int vladPC_n) throws Exception {
//		
//		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file );
//		
//		PrincipalComponents rootSIFT_PC = PrincipalComponents.read(rootSIFT_PC_file);
//		rootSIFT_PC.setProjDim(rootSIFTPC_N);
//		
//		LFWords words = new LFWords(dictionary_file);
//		
//		Log.info("\t" + dictionary_file.getAbsolutePath() + "\t" + words.size());
//		
//		Log.info("Creating: " + bowArchive_file);
//		
//		FeaturesCollectorsArchive outArchive = inArchive.getSameType( bowArchive_file );
//		
//		PrincipalComponents vlad_PC = null;
//		if ( vladPC_file != null ) { 
//			vlad_PC = PrincipalComponents.read(vladPC_file);
//			if ( vladPC_n > 0 ) vlad_PC.setProjDim(vladPC_n);
//		}
//		TimeManager timeManager = new  TimeManager();
//		for (Iterator<AbstractFeaturesCollector> it = inArchive.iterator(); it.hasNext(); ) {
//			AbstractFeaturesCollector curr = it.next();
//			
//			SIFTGroup sifts = curr.getFeature(SIFTGroup.class);
//			RootSIFTGroup rootSIFT = new RootSIFTGroup(sifts, null);
//			FloatsLFGroup rootSIFTPCA = rootSIFT_PC.project(rootSIFT);
//			
//			VLAD vlad = VLAD.getVLAD(rootSIFTPCA, words);
//			
//			if (vlad_PC == null ) {
//				outArchive.add(vlad, ((IHasID) curr).getID() );
//			} else {
//				Floats f = new Floats(vlad_PC.project_float( (IArrayValues) vlad));
//				outArchive.add(f, ((IHasID) curr).getID() );
//			}
//			
//			Log.info_verbose_progress(timeManager, outArchive.size(), inArchive.size());
//				
//		}
//		
//		System.out.print( outArchive.getInfo() );
//		
//		System.out.println("VLADs were created in " + timeManager.getTotalTime_STR());
//		
//		System.out.println();
//		
//		outArchive.close();
//		
//	}
	
	public static final File getBoWArchivesFile( String prefix, int n ) {
		String outFileName = prefix + "_BoW_" + n + ".dat";
		return WorkingPath.getFile(outFileName);	
	}
}
