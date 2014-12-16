package it.cnr.isti.vir.util;

import java.io.File;

public class WorkingPath {

	static String parent = "";
	
	static public void setWorkingPath(String workingPath) {
		parent = workingPath;
	}
	
	static public String getWorkingPath() {
		return parent;
	}
	
	static public File getWorkingDir() {
		return new File(parent);
	}
	
	static public File getFile( String fileName ) {
		File file = new File(fileName);
		if ( file.isAbsolute() ) return file;
		else return  new File(parent, fileName);
	} 
	
}
