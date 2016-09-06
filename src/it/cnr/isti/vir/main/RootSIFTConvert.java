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
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.util.Properties;

public class RootSIFTConvert {

	public static void usage() {
		System.out.println("RootSIFTConvert <properties filename>.properties");
		System.out.println("or:    RootSIFTConvert <in-SIFTArchive> <out-SIFTArchive>");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- SIFTArchive=<ArchiveFileName>");
		System.out.println("- RootSIFTArchive=<ArchiveFileName>");
		System.out.println("- [RootSIFTFloats]=<true (false def)>");
		System.out.println("- [RootSIFT.L2Norm]=<true (false def)>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 0 || args.length > 2) {
			usage();
		} else if ( args.length == 1 ) {
			Launch.launch(RootSIFTConvert.class.getName(), args[0]);
		} else {
			convert(new File(args[0]), new File(args[1]), false, false);
		}
		
	}
		
	public static void launch(Properties prop) throws Exception {
		
		convert(
				PropertiesUtils.getFile(prop, "SIFTArchive"),
				PropertiesUtils.getFile(prop, "RootSIFTArchive"),
				PropertiesUtils.getBoolean(prop, "RootSIFT.L2Norm", false),
				PropertiesUtils.getBoolean(prop, "RootSIFTFloats", false)
				);
	}
	
	public static void convert(File inSIFTArchive, File outRootSIFTArchive, boolean l2Norm, boolean floats ) throws Exception {
		FeaturesCollectorsArchive ina = new FeaturesCollectorsArchive(inSIFTArchive);
		FeaturesCollectorsArchive outa = ina.getSameType(outRootSIFTArchive);
		
		RootSIFT.setL2Norm(l2Norm);
		
		int count=0;
		TimeManager tm = new TimeManager();
		if ( !floats )
			Log.info(
				"Converting SIFTs in " + inSIFTArchive.getAbsolutePath() + "\n" +
				" to RootSIFTs in " + outRootSIFTArchive.getAbsolutePath() );
		else
			Log.info(
				"Converting SIFTs in " + inSIFTArchive.getAbsolutePath() + "\n" +
				" to RootSIFTFloat in " + outRootSIFTArchive.getAbsolutePath() );
		for ( AbstractFeaturesCollector fc : ina ) {
			count++;
			Log.info_verbose_progress(tm, count, ina.size());
			SIFTGroup sifts = fc.getFeature(SIFTGroup.class);
			if ( !floats ) {
				RootSIFTGroup rootSIFTs = new RootSIFTGroup(sifts, fc);
				fc.discard(SIFTGroup.class);
				fc.add(rootSIFTs);
			} else {
				FloatsLFGroup rootSIFTs = RootSIFTGroup.getFloatsLFGroup(sifts, fc);
				fc.discard(SIFTGroup.class);
				fc.add(rootSIFTs);
			}
			
			
			
			outa.add(fc);
		}
		
		outa.close();
		ina.close();
	}

}
