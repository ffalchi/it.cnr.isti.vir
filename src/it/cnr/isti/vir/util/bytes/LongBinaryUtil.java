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
