package it.cnr.isti.vir.id;

import java.io.File;

public class ID2FileName_EAGLE implements IID2File, IID2URL {

	File rootPath;
	String suffix;
	
	public ID2FileName_EAGLE(String  rootPath ) {
		this.rootPath = new File(rootPath);

	}
	
	
	public ID2FileName_EAGLE(File rootPath ) {
		this.rootPath = rootPath;

	}
	
	public File getFile(String id) {
		String dir;
		String temp =id.substring(0,  3);
		int t = Integer.parseInt(temp);
		if ( t <= 1 ) 
			dir = "1";
		else
			dir = Integer.toString(t);
		return new File(
				 
				rootPath.getAbsolutePath() +
				File.separator + dir +
				File.separator + id +
				".jpg" );
		
	}

	public String getURL(String id) {
		
		return getFile(id).getAbsolutePath();
		
	}

}
