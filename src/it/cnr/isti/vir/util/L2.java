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
}
