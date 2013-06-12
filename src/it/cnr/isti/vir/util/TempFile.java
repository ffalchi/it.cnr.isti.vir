package it.cnr.isti.vir.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TempFile {

	public static final String getTempFileName() {
		String property = "java.io.tmpdir";
        
        // Get the temporary directory
        String tempDir = System.getProperty(property);
        
        // temp file
	    File temp = null;
        while ( temp == null || temp.exists() ) {
	    	String tempFileName = tempDir + RandomOperations.getRandomUUIdString() + ".tmp";
	    	temp = new File(tempFileName);
	    	temp.deleteOnExit();
	    }
        return temp.getAbsolutePath();
	}
	
	public static final File createTempFile(String extension) throws IOException {
        
		String property = "java.io.tmpdir";
        
        // Get the temporary directory
        String tempDir = System.getProperty(property);
        
        // temp file
	    File temp = null;
        while ( temp == null || temp.exists() ) {
	    	String tempFileName = tempDir + RandomOperations.getRandomUUIdString() + extension;
	    	temp = new File(tempFileName);
	    	temp.deleteOnExit();
	    }
	    
	    return temp;
	}
	
	public static final File createTempFile(String content, String extension) throws IOException {
        File tFile = createTempFile(extension);
        
        BufferedWriter out = new BufferedWriter(new FileWriter(tFile));
        out.write(content);
        out.close();
        return tFile;
	}
	
}
