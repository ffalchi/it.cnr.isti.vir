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
		
		int k = PropertiesUtils.getInt(prop, className+".k");

		
		Log.info("BMM computation, number of Bernoulli  " + k);
		Log.info("Learning Archive:"+ inArchive.getInfo());


		Bmm bmm = new Bmm(inArchive, k);
		// write bmmFileName
		bmm.writeData(outAbsolutePath);
		
		inArchive.close();
	}
}	
