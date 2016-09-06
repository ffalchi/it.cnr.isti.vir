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
