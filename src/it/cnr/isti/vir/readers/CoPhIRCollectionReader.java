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
package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.mpeg7.SAPIRObject;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.id.IDInteger;
import it.cnr.isti.vir.util.Log;

import java.io.File;
import java.io.FilenameFilter;

public class CoPhIRCollectionReader {

	public static void main(String[] args) throws Exception {
		// String collectionDirectoryName = args[0];
		// String outputArchiveFileName = args[1];

		Log.setVerbose(true);

		String collectionDirectoryName = "T:\\CoPhiR-tar\\";
		String outputArchiveFileDirectoryName = "X:\\CoPhiR\\dat";

		File folder = new File(collectionDirectoryName);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".tgz");
			}
		};

		File[] listOfFiles = folder.listFiles(filter);

		Log.info_verbose(listOfFiles.length + " files were found.");
		
		for (int i = 0; i < listOfFiles.length; i++) {
			Long start = System.currentTimeMillis();
			String outputArchiveFileName = outputArchiveFileDirectoryName
					+ File.separator + listOfFiles[i].getName() + ".dat";
			File archiveFile = new File(outputArchiveFileName);
			// FeaturesCollectorsArchive archive = new
			// FeaturesCollectorsArchive(archiveFile,
			// SAPIRObject.getFCClasses(), IDInteger.class, SAPIRObject.class );

			FeaturesCollectorsArchive_Buffered archive = new FeaturesCollectorsArchive_Buffered(
					archiveFile, IDInteger.class,
					SAPIRObject.class);
			Log.info_verbose("Reading: " + listOfFiles[i]);
			Log.info_verbose("Saving to: " + outputArchiveFileName);
			CoPhIR_tar_gz reader = new CoPhIR_tar_gz(listOfFiles[i]);

			int tCount = 0;
			SAPIRObject curr = null;
			while ((curr = reader.getObj()) != null) {
				tCount++;
				// System.out.println(curr);
				archive.add(curr);
				/*
				 * SAPIRObject saved = (SAPIRObject) archive.get(curr.getID());
				 * if ( !curr.equals(saved)) { //ERROR SAVING
				 * System.out.println("ERROR SAVING:"); System.out.println(curr
				 * +"\nDIFFEARS FROM SAVED\n" + saved); }
				 */
				if (tCount % 100000 == 0) {
					Log.info_verbose("\t" + tCount +"\tavgMillis: "+ ((double) (System.currentTimeMillis()-start)/tCount));
					
				}
			}

			archive.close();
			Log.info("\t" + tCount + " objects were found.");

			if (Log.isVerbose()) {
				FeaturesCollectorsArchive tArchive = new FeaturesCollectorsArchive(
						archiveFile);
				Log.info_verbose("The archive contains " + tArchive.size()
						+ "objects");
				Log.info_verbose("Object 100:\n" + tArchive.get(100).toString());
				tArchive.close();
			}
		}

	}
}
