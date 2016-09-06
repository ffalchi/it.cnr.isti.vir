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
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.util.Properties;
import java.util.spi.TimeZoneNameProvider;

public class BifocalMerging {
	public static final String className = "BifocalMerging";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".inArchive=<input archive>");
		System.out.println("- "+className+".outArchive=<output archive>");
		System.out.println("- "+className+".lfWeight=<float>");
		System.out.println("- "+className+".aggWeight=<float>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length <= 1 ) {
			usage();
		}

		Launch.launch(VLADConvert.class.getName(), args[0]);	
	
	}
	
	public static void launch(Properties prop) throws Exception {
		
		File inArchiveFile  = PropertiesUtils.getFile( prop, className+".inArchive" );
		File outArchiveFile = PropertiesUtils.getFile( prop, className+".outArchive");
		float lfWeight  = PropertiesUtils.getFloat(prop, className+".lfWeight");
		float aggWeight = PropertiesUtils.getFloat(prop, className+".aggWeight");
		
		createVLAD(inArchiveFile, outArchiveFile, lfWeight, aggWeight );		
	}
	

	public static void createVLAD(
		File inArchiveFile, File outArchiveFile, 
		float lfWeight, float aggWeight ) throws Exception {
		
		
		FeaturesCollectorsArchive in = new FeaturesCollectorsArchive(inArchiveFile);
		
		FeaturesCollectorsArchive_Buffered out =
				FeaturesCollectorsArchive_Buffered.create(
						outArchiveFile,
						in.getIDClass(),
						in.getFcClass()
						);
		
		TimeManager tm = new TimeManager(in.size());
		for ( AbstractFeaturesCollector fc : in ) {
			SIFTGroup siftGroup = fc.getFeature(SIFTGroup.class);
			Floats vlad = fc.getFeature(Floats.class);
			FloatsLFGroup c = getMerged(siftGroup, vlad, lfWeight, aggWeight);
			
			AbstractFeaturesCollector res = fc.createWithSameInfo(c);
			
			out.add(res);
			
			tm.reportProgress();
		}
		
		in.close();
		out.close();
		
	}
	
	public static FloatsLFGroup getMerged(SIFTGroup siftGroup, Floats vlad, float lfWeight, float aggWeight) {
		
		FloatsLF[] arr = new FloatsLF[siftGroup.size()];
		
		int dim = SIFT.VLEN + vlad.getLength();
		
		float[] aVLAD = vlad.values;
		float[] wVLAD = new float[aVLAD.length];
		
		for ( int i=0; i<wVLAD.length; i++ ) {
			wVLAD[i] = aggWeight*aVLAD[i];
		}
		
		for ( int is=0; is<arr.length; is++ ) {
			SIFT s = siftGroup.lfArr[is];
			float[] c = new float[dim];
			
			int ic=0;
			for ( int i=0; i<SIFT.VLEN; i++ ) {
				c[ic++] = lfWeight * s.getFloat(i);
			}
			
			for ( int i=0; i<wVLAD.length; i++ ) {
				c[ic++] = wVLAD[i];
			}
			
			arr[is] = new FloatsLF(s.getKeyPoint(), c);
		}
		
		return new FloatsLFGroup(arr); 
	}
}
