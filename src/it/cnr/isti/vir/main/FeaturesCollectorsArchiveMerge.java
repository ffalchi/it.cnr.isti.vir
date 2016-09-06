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
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

public class FeaturesCollectorsArchiveMerge {

	public static final String className = "FeaturesCollectorsArchiveMerge";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- archive1FileName=<archive file name>");
		System.out.println("- archive2FileName=<file name>");
		System.out.println("- outputArchiveFileName=<file name>");
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
	
		File f1 = PropertiesUtils.getFile(prop, "archive1FileName");
		File f2 = PropertiesUtils.getFile(prop, "archive2FileName");
		File outf = PropertiesUtils.getFile(prop, "outputArchiveFileName");
		
	}
		
		
	public static void launch(File f1, File f2, File outf)  throws Exception {
		
		
		FeaturesCollectorsArchive fca1 = new FeaturesCollectorsArchive(f1);
		FeaturesCollectorsArchive fca2 = new FeaturesCollectorsArchive(f2);
		
		FeaturesCollectorsArchive_Buffered outFca
			= FeaturesCollectorsArchive_Buffered.create( outf, fca1.getIDClass(), FeaturesCollectorArr.class );
	
		for ( AbstractFeaturesCollector fc1 : fca1 ) {
			AbstractID id = ((IHasID) fc1).getID();
			AbstractFeaturesCollector fc2 = fca2.get( id  );
			
			FeaturesCollectorArr outFC = new FeaturesCollectorArr( fc1.getFeatures(), id );
			outFC.addAll(fc2.getFeatures());
			outFca.add( outFC );
			
		}
		
		outFca.close();
	}
}
