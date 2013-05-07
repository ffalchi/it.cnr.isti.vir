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
package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.util.HammingDistance;
import it.cnr.isti.vir.util.LongByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class ORB extends ALocalFeature<ORBGroup> {

	static final int vLen = 8;
	
	// 256 bits in 8x8 bytes
	private long[] data;
	
	public ORB(KeyPoint kp, long[] data, ORBGroup linkedGroup) {
		this.kp = kp;
		this.linkedGroup = linkedGroup;
	}
	
	public ORB(KeyPoint kp, long[] data) {
		this(kp, data, null);
	}
	
	public final int getDataByteSize() {
		return vLen*8;
	}
	
	public int putBytes(byte[] bArr, int bArrI) {
		LongByteArrayUtil.longArrayToByteArray(data, bArr, bArrI);
		return bArrI + vLen*8;
	}
	
	public ORB(ByteBuffer src, ORBGroup group ) throws IOException {
		this.linkedGroup = group;
		byte kpExists = src.get();
		if ( kpExists != -1 ) {
			kp = new KeyPoint(src);
		}
		data = new long[vLen];
		for ( int i=0; i<vLen; i++ ) {
			data[i] = src.getLong();
		}
	}

	public ORB(DataInput str, ORBGroup group) throws IOException {
		
		linkedGroup = group;
		
		byte kpExists = str.readByte();
		if ( kpExists != -1 ) {
			new KeyPoint(str);
		}		
		
		data = new long[vLen];
		for ( int i=0; i<vLen; i++ ) {
			data[i] = str.readLong();
		}

	}



	public static ORB getMean(Collection<ORB> coll) {
		
		if ( coll.size() == 0 ) return null;
		
		int[] bitSum = new int[vLen*64];
		
		for ( Iterator<ORB> it = coll.iterator(); it.hasNext(); ) {
			long[] currVec = it.next().data;
			int iBitSum = 0;
			for ( int iLong=0; iLong<currVec.length; iLong++ ) {
				// for each bit
				long mask = 1; 
				for ( int i=0; i<63; i++) {
					bitSum[iBitSum++] += currVec[i] & mask;
					mask = mask << 1;
				}
			}
		}
		
		long[] newValues = new long[vLen];
		
		int threshold = coll.size() / 2;
		int iLong = 0;
		int iBit = 0;
		for ( int i=0; i<bitSum.length; i++ ) {
			long zeroOne = 0;
			if ( bitSum[i] > threshold ) zeroOne = 1;
			
			newValues[iLong] = (newValues[iLong] << 1) & zeroOne;
			
			if ( iBit == 63 ) {
				iLong++;
				iBit = 0;
			} else {
				iBit++;
			}
		}
		return new ORB( null, newValues );
	}
	
		
	public static float getDistance_Norm(ORB o1, ORB o2) {
		return HammingDistance.distance_norm(o1.data, o2.data);
	}
	
	@Override
	public int compareTo(ALocalFeature<ORBGroup> given) {
		if ( this == given ) return 0;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		for ( int i=0; i<data.length; i++ ) {
			int tComp = Long.compare(data[i], ((ORB)given).data[i]);
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}

	@Override
	public Class<ORBGroup> getGroupClass() {
		return ORBGroup.class;
	}

}
