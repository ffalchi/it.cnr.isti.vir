package it.cnr.isti.vir.similarity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SimilarityClasses {

	static final byte version = 0;
	
	public static ISimilarity read(DataInput in) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		//Similarity 
		char[] chars = new char[in.readInt()];
		for(int i=0; i<chars.length; i++) {
			chars[i]=in.readChar();
		}
		String simClassName = new String(chars);
		return (ISimilarity) Class.forName(simClassName).newInstance();
	}
	
	public static void write(ISimilarity sim, DataOutput out) throws IOException {
		//Similarity 
		String simClassName = sim.getClass().getCanonicalName();
		out.writeInt(simClassName.length());
		out.writeChars(simClassName);
	}
	
	
}
