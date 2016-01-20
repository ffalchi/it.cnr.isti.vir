/*******************************************************************************
 * Copyright (c) 2013, Claudio Gennaro (NeMIS Lab., ISTI-CNR, Italy)
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

public class PearsonDistance {

	
	public static final double get(float[]f1, float[]f2) {
		double acc = 0;
		double accn1 = 0;
		double accn2 = 0;
		
		float mean1 = mean(f1);
		float mean2 = mean(f2);
		
		
		for ( int j=0; j<f1.length; j++) {
			double prod = (f1[j] - mean1) * (f2[j] - mean2);
			double norm1 = (f1[j] - mean1) * (f1[j] - mean1);
			double norm2 = (f2[j] - mean2) * (f2[j] - mean2);
			acc += prod;
			accn1 += norm1;
			accn2 += norm2;
		}
		return 1.0 - acc/Math.sqrt(accn1*accn2); 
	}
	
	public static final double get(float[]f1, float[]f2, double max) {
		double acc = 0;
		double accn1 = 0;
		double accn2 = 0;
		
		float mean1 = mean(f1);
		float mean2 = mean(f2);
		
		
		for ( int j=0; j<f1.length; j++) {
			double prod = (f1[j] - mean1) * (f2[j] - mean2);
			double norm1 = (f1[j] - mean1) * (f1[j] - mean1);
			double norm2 = (f2[j] - mean2) * (f2[j] - mean2);
			acc += prod;
			accn1 += norm1;
			accn2 += norm2;
		}
		double dist = 1.0 - acc/Math.sqrt(accn1*accn2);
		
		if (dist > max) 
			return -dist; 
		else
			return 1.0 - acc/Math.sqrt(accn1*accn2); 
	}
	
	private static float mean(float[] f){
		float mean = 0;
	    for (int i = 0; i < f.length; i++) {
	    	mean += f[i];
	    }		
		return mean/(float)f.length;
	}
	
}
