package it.cnr.isti.vir.readers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class CoPhIR_tar extends CoPhIRReader {
	
//	protected GZIPInputStream gzIn = null;
	
	public CoPhIR_tar(FileReader fileR) {
		super();
		br = new BufferedReader( fileR );
	}
	
	public CoPhIR_tar(InputStreamReader inSR) {
		super();
		br = new BufferedReader(inSR);
	}
	
	public CoPhIR_tar(String filename) throws FileNotFoundException {
		super();
		br = new BufferedReader(new FileReader(filename));
	}
}
