package it.cnr.isti.vir.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class CoPhIR_tar_gz extends CoPhIRReader {

//	protected GZIPInputStream gzIn = null;
		
	// GZ Tar file name
	public CoPhIR_tar_gz(String filename) throws FileNotFoundException, IOException {
		this(new File(filename));
	}
	
	public CoPhIR_tar_gz(File file) throws FileNotFoundException, IOException {
		super();
		br = new BufferedReader(
				new InputStreamReader(
						new GZIPInputStream(
								new FileInputStream(file)) ));
	}
	
	
	
}
