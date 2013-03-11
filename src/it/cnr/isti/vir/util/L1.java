package it.cnr.isti.vir.util;

public class L1 {
	
	public static int get(byte[] v1, byte[] v2) {
		int dist = 0;		
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    }		
		return dist;
	}
	
	public static int get(byte[] v1, byte[] v2, int maxDist) {
		int dist = 0;
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
	}
	
	public static int get(short[] v1, short[] v2) {
		int dist = 0;		
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    }		
		return dist;
	}
	
	public static int get(short[] v1, short[] v2, int maxDist) {
		int dist = 0;
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
	}
	
	public static int get(int[] v1, int[] v2) {
		int dist = 0;		
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    }		
		return dist;
	}
	
	public static int get(int[] v1, int[] v2, int maxDist) {
		int dist = 0;
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs(v1[i] - v2[i]);
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
	}
	
	
	public static double get(float[] v1, float[] v2) {
		double dist = 0;		
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs((double) v1[i] - v2[i]);
	    }		
		return dist;
	}
	
	public static double get(float[] v1, float[] v2, float maxDist) {
		float dist = 0;
	    for (int i = 0; i < v1.length; i++) {
	    	dist += Math.abs((double) v1[i] - v2[i]);
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
	}
}
