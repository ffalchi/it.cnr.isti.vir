package it.cnr.isti.vir.util;

public class L2 {

	public static int getSquared(byte[] v1, byte[] v2) {
		int dist = 0;	    
		int dif = 0;		
	    for (int i = 0; i < 128; i++) {
	    	dif = (int) v1[i] - (int) v2[i];
	    	dist += dif * dif;
	    }		
		return dist;
	}
	
	public static int getSquared(byte[] v1, byte[] v2, int maxDist) {
		int dist = 0;	    
		int dif = 0;
	    for (int i = 0; i < 128; i++) {
	    	dif = (int) v1[i] - (int) v2[i];
	    	dist += dif * dif;
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
	}
	
	public static double get(float[]f1, float[]f2) {
		return  Math.sqrt(getSquared(f1,f2));
	}

	
	public static float getSquared(float[]f1, float[]f2) {
		float acc = 0;
		for ( int j=0; j<f1.length; j++) {
			float diff = f1[j] - f2[j];
			acc += diff * diff;
		}
		return acc; 
	}
	
	public static float get(float[][] f1, float[][] f2) {
		float dist = 0;
		
		for ( int i=0; i<f1.length; i++) {
			dist += get(f1[i], f2[i]);
		}
		
		return dist;
	}
}
