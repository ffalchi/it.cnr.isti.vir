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
					archiveFile, SAPIRObject.getFCClasses(), IDInteger.class,
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
