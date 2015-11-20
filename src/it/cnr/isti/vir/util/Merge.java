package it.cnr.isti.vir.util;

public class Merge {

	public static float[] merge(float[] f1, float[] f2) {
		float[] res = new float[f1.length+f2.length];
		
		int j=0;
		for ( int i=0; i<f1.length; i++) {
			res[j++] = f1[i];
		}
		
		for ( int i=0; i<f2.length; i++) {
			res[j++] = f2[i];
		}
		
		return res;
	}
	              
} 
