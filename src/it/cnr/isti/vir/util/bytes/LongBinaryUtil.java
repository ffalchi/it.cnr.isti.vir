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
package it.cnr.isti.vir.util.bytes;

import it.cnr.isti.vir.features.ILongBinaryValues;
import it.cnr.isti.vir.util.RandomOperations;

import java.util.Collection;

public class LongBinaryUtil {
	
	public static long[] getMean(Collection coll) {
		long[][] temp = new long[coll.size()][];
		int i=0;
		for ( Object curr : coll ) {
			temp[i++] = ((ILongBinaryValues) curr).getValues();
		}
		return getMean( temp );
	}
	
	public static long[] getMeanFromLongs(Collection<long[]> coll) {
		long[][] temp = new long[coll.size()][];
		coll.toArray(temp);
		return getMean( temp );
	}
	
	public static long[] getMean(long[][] coll) {
		
		if ( coll.length == 0 ) return null;
		
		int nlongs = coll[0].length;
		int[] bitsSum = new int[nlongs*Long.SIZE];
		
		for ( long[] currVec : coll ) {
			//long[] currVec = it.next();
			int iBitSum = 0;
			for ( int iLong=0; iLong<currVec.length; iLong++ ) {
				// for each bit
				long mask = 1; 
				for ( int i=0; i<Long.SIZE; i++) {
					if ( (currVec[iLong] & mask) != 0 ) bitsSum[iBitSum]++;
					iBitSum++;
					mask = mask << 1;
				}
			}
		}
		
		long[] newValues = new long[nlongs];
		
		int threshold = coll.length / 2;
		long oneLong = 1;
		for ( int i=0; i<bitsSum.length; i++ ) {
			if ( bitsSum[i] > threshold
//					||
//				 (	bitsSum[i] == threshold
//				 	&&	RandomOperations.getBoolean()
//				 	)
				 	) {
				newValues[i/Long.SIZE] ^= (oneLong << i%Long.SIZE );
			}
		}
		return newValues;
	}
}
