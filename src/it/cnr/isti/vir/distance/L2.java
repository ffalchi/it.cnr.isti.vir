/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.distance;

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

	
	public static double getSquared(float[]f1, float[]f2) {
		double acc = 0;
		for ( int j=0; j<f1.length; j++) {
			double diff = f1[j] - f2[j];
			acc += diff * diff;
		}
		return acc; 
	}
	
	public static double get(float[][] f1, float[][] f2) {
		double dist = 0;
		
		for ( int i=0; i<f1.length; i++) {
			dist += get(f1[i], f2[i]);
		}
		
		return dist;
	}
}
