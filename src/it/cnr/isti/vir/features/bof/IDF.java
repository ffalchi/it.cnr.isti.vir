package it.cnr.isti.vir.features.bof;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class IDF {

	float[] values;
	
	public IDF(float[] val) {
		values = val;
	}
	
	public IDF(File file) throws IOException {
		DataInputStream in = new DataInputStream( new BufferedInputStream ( new FileInputStream(file) ));
		int nBytes = in.available();
		int nValues = nBytes / 4;
		values = new float[nValues];
		for (int i = 0; i < values.length; i++) {
			values[i] = in.readFloat();
		}
		
	}
	
	public float[] getValues() {
		return values;
	}
	
	public void save(File file) throws IOException {
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream ( new FileOutputStream(file) ));
		writeData(out);
		out.close();
	}

	public void writeData(DataOutput out) throws IOException {
		for (int i = 0; i < values.length; i++) {
			out.writeFloat(values[i]);
		}
	}
}
