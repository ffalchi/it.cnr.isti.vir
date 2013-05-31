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
