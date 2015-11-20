package it.cnr.isti.vir.id;

import java.io.File;


public class ID2FileName_Dir implements IID2File, IID2URL {

	String suffix;
	String rootPath;

	
	public ID2FileName_Dir(String rootPath) {
		this(rootPath, ".jpg");
	}
	
	public ID2FileName_Dir(String rootPath, String suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	public final String getURL(String id) {

		return rootPath + File.separator + id + ".jpg";
	}
	
	public final String getURL(AbstractID id) {
		return getURL(id.toString());
	}


	@Override
	public File getFile(String id) {
		// TODO Auto-generated method stub
		return new File( getURL( id ));
	}
	
}
