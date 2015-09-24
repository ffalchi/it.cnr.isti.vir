package it.cnr.isti.vir.id;

import java.io.File;

public class IDConvert_yfcc100m_old implements IID2File, IID2URL {

	File rootPath;
	String suffix;
	
	public IDConvert_yfcc100m_old(File rootPath ) {
		this.rootPath = rootPath;

	}
	
	public File getFile(String id) {
		
		return new File(
				rootPath.getAbsolutePath() +
				File.separator + id.substring(0,  3) +
				File.separator + id +
				".jpg" );
		
	}

	public String getURL(String id) {
		
		return 
				rootPath.getAbsolutePath() +
				File.separator + id.substring(0,  3) +
				File.separator + id +
				".jpg";
		
	}

}
