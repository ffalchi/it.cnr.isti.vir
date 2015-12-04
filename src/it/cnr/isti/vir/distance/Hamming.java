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


public class Hamming {

	public static final int distance(long[] bits1, long[] bits2, int max) {
		int acc = 0;
		for ( int i=0; i<bits1.length; i++) {
			acc+=Long.bitCount(bits1[i]^bits2[i]);
			if ( acc > max ) return -acc;
		}		
		return acc;
	}
	
	public static final int distance(long[] bits1, long[] bits2) {
		int acc = 0;
		for ( int i=0; i<bits1.length; i++) {
			acc+=Long.bitCount(bits1[i]^bits2[i]);
		}		
		return acc;
	}
	
	public static final int distance_offset(long[] data1, long[] data2, int data2Offset) {
	
		int res = 0;
		for ( int i=0; i<data1.length; i++) {
			long xor = data1[i]^data2[data2Offset+i];
			res += Long.bitCount(xor);
		}
		return res;
	}
	
	public static final int distance_offset(long[] data1, int data1Offset, long[] data2, int data2Offset, int dim) {
	
		int res = 0;
		for	( int i=0; i<dim; i++) {
			long xor = data1[data1Offset+i]^data2[data2Offset+i];
			res += Long.bitCount(xor);
		}
		return res;
	}
	
	
	public static final int distance_offset(long[] data1, long[] data2, int data2Offset, int dim) {
	
		int res = 0;
		for ( int i=0; i<dim; i++) {
			long xor = data1[i]^data2[data2Offset+i];
			res += Long.bitCount(xor);
		}
		return res;
	}
	
	
	public static final float distance_norm(long[] bits1, long[] bits2, int nBits, double max) {
		return distance(bits1, bits2, (int) Math.ceil(max*nBits))  / (float)  nBits;
	}
	
	public static final float distance_norm(long[] bits1, long[] bits2, int nBits) {
		return distance(bits1, bits2) / (float)  nBits;
	}
	
	
	
	
	public static final float distance_norm(long[] bits1, long[] bits2, double max) {
		return distance_norm(bits1, bits2, bits1.length*Long.SIZE, max);
	}

	
	public static final float distance_norm(long[] bits1, long[] bits2 ) {
		return distance_norm(bits1, bits2, bits1.length*Long.SIZE );
	}
	

	

//
//public static final int distance(long[] data1, int data1Offset, long[] data2, int data2Offset, int dim) {
//	
//	int res = 0;
//	for ( int i=0; i<dim; i++) {
//		long xor = data1[data1Offset+i]^data2[data2Offset+i];
//		res += Long.bitCount(xor);
//	}
//	return res;
//}
//
//public static final int distance(long[] data1, long[] data2, int data2Offset, int dim) {
//	
//	int res = 0;
//	for ( int i=0; i<dim; i++) {
//		long xor = data1[i]^data2[data2Offset+i];
//		res += Long.bitCount(xor);
//	}
//	return res;
//}
	

}
