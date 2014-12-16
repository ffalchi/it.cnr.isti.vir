package it.cnr.isti.vir.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class NestedProperties {

	public static Properties load(String pFName) throws FileNotFoundException, IOException {
		Properties properties = new java.util.Properties();
		load(properties, pFName);
		return properties;
	}
	
	public static void load(Properties properties, String pFName) throws FileNotFoundException, IOException {
	    
		File propertiesFile = WorkingPath.getFile(pFName);
		System.out.println("Loading properties " + propertiesFile);
		
		// Reading this
		Properties thisProperties = new java.util.Properties();
		thisProperties.load(new FileInputStream( propertiesFile));
		
		// Setting Working Directory
		String workingDirHereStr = thisProperties.getProperty("workingDirHere");
		if ( workingDirHereStr != "" && Boolean.parseBoolean(workingDirHereStr) ) {
			if ( propertiesFile.getParent() != null ) {
				WorkingPath.setWorkingPath( propertiesFile.getParent().toString() );
			}
			System.out.println("WorkingDirectory: " + WorkingPath.getWorkingPath() );
		}
		
		// Nested properties
	    String nestedPropertiesStr = thisProperties.getProperty("nestedProperties");
	    if ( nestedPropertiesStr != null ) {
	    	load( properties, nestedPropertiesStr );
	    }
		
	    // Merging properties
		properties.putAll(thisProperties);
	    
	}
}
