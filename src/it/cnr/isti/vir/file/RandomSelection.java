/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.file;

import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.RandomOperations;

import java.io.File;
import java.util.Arrays;

public class RandomSelection {

	
	public static void main(String[] args) throws Exception{
		
		RandomOperations.setSeed(System.currentTimeMillis());
		
		
		String inDirName = "M:/CoPhIR/dat";
		//String inDirName = "X:/CoPhIR/1MExp/dataset/CoPhIR_1M_rnd";
		//String inDirName = "W:/CoPhIR/CoPhIR-RND/CoPhIR_10M_rnd";
		
		//int n = 10000000;
		//String outArchiveName = "W:/CoPhIR/CoPhIR-RND/CoPhIR_10M_rnd";
		
		//int n = 1000000;
		//String outArchiveName = "W:/CoPhIR/CoPhIR-RND/CoPhIR_1M_rnd";
						
		//int n = 30000000;
		//String outArchiveName = "W:/CoPhIR/subsets/CoPhIR_30M";
		
		int n = 100;
		String outArchiveName = "W:/CoPhIR/Groundtruth/CoPhIR_Queries_100";
		
		
		if ( args != null && args.length > 0 ) {
			inDirName = args[0];
			outArchiveName = args[1];
			n = Integer.parseInt(args[2]);
		}
		
		FeaturesCollectorsArchives archives = new FeaturesCollectorsArchives(new File(inDirName), false);
		
		FeaturesCollectorsArchive outArchive = archives.getArchive(0).getSameType(new File(outArchiveName));
		Log.info(archives.size() + " objects in the archives");
		
		Log.info("Selecting random objects.");
		int[] randomSelected = RandomOperations.getDistinctInts(n, 0, archives.size()-1 );
		Log.info("Ordering random objects.");
		Arrays.sort(randomSelected);
		
		Log.info("Reading and saving objects.");
		for ( int i=0; i<n; i++ ) {
			//Log.info_verbose(""+ randomSelected[i]);
			outArchive.add(archives.get(randomSelected[i]));
			if ( i%10000 == 0) Log.info_verbose(i + " /" + n);
		}
		
		Log.info("Created archives of " + outArchive.size() + " elements randomly seleceted.");
		outArchive.close();
		archives.close();
	}
}
