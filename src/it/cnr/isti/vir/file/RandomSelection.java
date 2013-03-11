package it.cnr.isti.vir.file;

import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.RandomOperations;

import java.io.File;
import java.util.Arrays;

public class RandomSelection {

	
	public static void main(String[] args) throws Exception{
		
		int n = 100000;
		String inDirName = "X:\\CoPhIR\\dat";
		String outArchiveName = "T:\\CoPhIR_100k_rnd";
		
		FeaturesCollectorsArchives archives = new FeaturesCollectorsArchives(new File(inDirName), false);
		
		FeaturesCollectorsArchive outArchive = archives.getArchive(0).getSameType(new File(outArchiveName));
		Log.info(archives.size() + " objects in the archives");
		
		Log.info("Selecting random objects.");
		int[] randomSelected = RandomOperations.getInts(0, archives.size()-1, n );
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
	}
}
