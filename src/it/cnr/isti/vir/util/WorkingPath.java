package it.cnr.isti.vir.util;

import java.io.File;
import java.nio.file.Paths;

public class WorkingPath {

	static String currPath = null;
	
	static public void setWorkingPath(String workingPath) {
		currPath = workingPath;
	}
	
	static public String getWorkingPath() {
		if ( currPath == null )
		return Paths.get("").toAbsolutePath().toString();
		return currPath;
	}
	
	static public File getWorkingDir() {
		return new File(currPath);
	}
	
	static public File getFile( String fileName ) {
		String path = currPath;
		if ( path == null ) {
			path = Paths.get("").toAbsolutePath().toString();
		}
		File file = new File(fileName);
		if ( file.isAbsolute() ) return file;
		else return  new File(path, fileName);
	} 
	
	static public String getAbsolutePath( String fileName ) {
		return getFile(fileName).getAbsolutePath();
	} 
	
}
