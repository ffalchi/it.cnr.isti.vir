package it.cnr.isti.vir.id;

import java.io.File;


public class ID2FileName_MIRFlickr implements IID2File, IID2URL {

	String suffix;
	String rootPath;
	
	String mapName = "IDURL";
	
	public ID2FileName_MIRFlickr(String rootPath) {
		this(rootPath, ".jpg");
	}
	
	public ID2FileName_MIRFlickr(String rootPath, String suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	public final String getURL(int id) {

		int subdir = (int) (id / 10000);
		return rootPath + File.separator + subdir + File.separator + id + ".jpg";
	}
	
	public final String getURL(IDInteger id) {
		return getURL(id.id);
	}

	@Override
	public String getURL(String id) {
		return getURL(Integer.parseInt(id));
	}

	@Override
	public File getFile(String id) {
		// TODO Auto-generated method stub
		return new File( getURL( Integer.parseInt(id)));
	}
	
}
