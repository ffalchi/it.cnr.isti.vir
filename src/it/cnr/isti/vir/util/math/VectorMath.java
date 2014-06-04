/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi and Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.util.math;

public class VectorMath {

	public static final double[] subtraction(double[] v1, double[] v2) {
		int nD = v1.length;
		double[] res = new double[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - v2[iD];
		}
		return res;
	}

	public static final float[] subtraction(float[] v1, float[] v2) {
		int nD = v1.length;
		float[] res = new float[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - v2[iD];
		}
		return res;
	}
	
	public static final double mean(float[] v) {
		return sum(v) / (double) v.length;
	}
	
	public static final double sum(float[] v) {
		int nD = v.length;
		double res = 0;
		for (int iD = 0; iD < nD; iD++)
			res += v[iD];
		return res;
	}
	
	public static final double mean(double[] v) {
		return sum(v) / (double) v.length;
	}
	
	public static final double sum(double[] v) {
		int nD = v.length;
		double res = 0;
		for (int iD = 0; iD < nD; iD++)
			res += v[iD];
		return res;
	}
	
	public static final void multiply(float[] v, float c) {
		int nD = v.length;
		for (int iD = 0; iD < nD; iD++) {
			v[iD] *= c;
		}
	}

	public static final float[] getFloats(int[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}
	
	public static final float[] getFloats(double[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (float) values[i];
		}
		return res;		
	}

	public static final double[] getDoubles(int[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}
	
	public static final double[] getDoubles(float[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}	
	
}

