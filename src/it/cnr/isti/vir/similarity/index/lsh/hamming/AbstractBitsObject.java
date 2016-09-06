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

package it.cnr.isti.vir.similarity.index.lsh.hamming;

import it.cnr.isti.vir.util.RandomOperations;


public abstract class AbstractBitsObject {

	public static final int nLongs = 64;
	public static final int nBitsBatch = 64;
	public static final int nBits = nLongs*nBitsBatch;
	
	public static final long[] getRandomData() {
		long[] res = new long[nLongs];
		for(int i=0; i<res.length; i++) {
			res[i] = RandomOperations.getLong();
			
		}
		return res;
	}
	
	
	public static final long[] getRandomData(double percentageOfOnes) {
		int nOnes = (int) (nBits*percentageOfOnes);		
		long[] res = new long[nLongs];
		for(int i=0; i<nOnes; i++) {
			while(true) {
				int currRandom = RandomOperations.getInt(nBits);
				int int_pos = currRandom/nBitsBatch;
				long mask = 1L<<currRandom%nBitsBatch;
				if ( (res[int_pos]&mask) == 0) {
					res[int_pos]=res[int_pos]|mask;
					break;
				}
			}
		}
		return res;
	}
	
	public static final int bitCount(long[] data) {
		int res = 0;
		for (long v: data) {
			res += Long.bitCount(v);
		}
		return res;
	}
	
//	public final long[] perturbateRandom(long[] orig, int nBits) {
//		long[] res = orig.clone();
//		for(int i=0; i<nBits; i++) {
//			int currRandom = RandomOperations.getInt(LSHHammingLongs.nBits);
//			int int_pos = currRandom/64;
//			long mask = 1L<<currRandom/64;
//			res[int_pos]=res[int_pos]^mask;
//		}
//		return res;
//	}

	public final int[] perturbateRandomExactly(int[] orig, int nBits) {
		int[] res = orig.clone();
		int[] pert = RandomOperations.getDistinctInts(nBits, LSHHammingLongs.nBits);
		
		for(int bit : pert) {
			int int_pos = bit/64;
			int mask = 1<<bit/64;
			res[int_pos]=res[int_pos]^mask;
		}
		return res;
	}
	
}
