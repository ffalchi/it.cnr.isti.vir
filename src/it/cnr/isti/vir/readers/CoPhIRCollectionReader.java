package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.mpeg7.SAPIRObject;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IDInteger;

import java.io.File;
import java.io.FilenameFilter;

public class CoPhIRCollectionReader {

	

	public static void main(String [ ] args) throws Exception
	{
	      //String collectionDirectoryName = args[0];
	      //String outputArchiveFileName = args[1];
	      
	      String collectionDirectoryName = "T:\\CoPhiR-tar\\";
	      String outputArchiveFileName = "X:\\CoPhiR.dat";
	      
	      File folder = new File(collectionDirectoryName);
	      FilenameFilter filter = new FilenameFilter() {
	    	  public boolean accept(File dir, String filename)
	    		{ return filename.endsWith(".tgz"); }
	      };
	 	         
	      File[] listOfFiles = folder.listFiles(filter);     
          
	      System.out.println(listOfFiles.length + " files were found.");
	      
	      File archiveFile = new File(outputArchiveFileName);	      
	      FeaturesCollectorsArchive archive =
	    		  new FeaturesCollectorsArchive(archiveFile, new FeatureClassCollector(SAPIRObject.class), IDInteger.class, SAPIRObject.class );
	      

	      for (int i = 0; i < listOfFiles.length; i++) {
	    	  System.out.println("Reading: " + listOfFiles[i]);
	    	  CoPhIR_tar_gz reader = new CoPhIR_tar_gz(listOfFiles[i]);
	    	  
	    	  int tCount = 0;
	    	  SAPIRObject curr = null;
	    	  while ( (curr = reader.getObj()) != null ) {
	    		  tCount++;
	    		  if ( tCount % 100000 == 0 ) System.out.println("\t" + tCount);
	    		  //System.out.println(curr);
	    		  archive.add(curr);
	    	  }
	    	  System.out.println("\t" + tCount + "objects were found.");
	    	  System.out.println("The archive contains " + archive.size() + "objects");
	    	  
	      }
	      archive.close();
	}
}
