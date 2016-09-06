/*******************************************************************************
 * Copyright (c) 2016, Lucia Vadicamo and Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
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

import it.cnr.isti.vir.distance.Hamming;
import it.cnr.isti.vir.features.ILongBinaryValues;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class AKAZE extends ALocalFeature<AKAZEGroup> implements ILongBinaryValues {

	public static final int NLONG = 8;
	public static final int BYTES_LENGTH = NLONG * Long.SIZE / Byte.SIZE;
	public static final int BITS_LENGTH = Byte.SIZE * BYTES_LENGTH;
	
	// 256 bits in 4x8 bytes
	public final long[] data;
	
	@Override
	public final int getDataByteSize() {
		return BYTES_LENGTH;
	}
	
	public AKAZE(KeyPoint kp, long[] data) {
		this.kp = kp;
		this.data = data;
	}
	
	@Override
	public final int putDescriptor(byte[] bArr, int bArrI) {
		return LongByteArrayUtil.convToBytes(data, bArr, bArrI);
	}
		
	public AKAZE(DataInput str) throws IOException {
		super(str);
		data = new long[NLONG];
		for ( int i=0; i<NLONG; i++ ) {
			data[i] = str.readLong();
		}

	}
	
	public AKAZE(ByteBuffer src ) throws IOException {
		super(src);
		data = new long[NLONG];
		for ( int i=0; i<NLONG; i++ ) {
			data[i] = src.getLong();
		}
	}
	
	
	public AKAZE(BufferedReader br ) throws IOException
	{
		String[] metadata = br.readLine().split("(\\s)+");
		float x = Float.parseFloat(metadata[0]);
		float y = Float.parseFloat(metadata[1]);
		float ori = Float.parseFloat(metadata[2]);
		float scale = Float.parseFloat(metadata[3]);
		//float y = Float.parseFloat(metadata[4]);
		kp = new KeyPoint(x,y,ori,scale);
		
		// Load the interest points in Mikolajczyk's format
		
//		assert(temp.length == ivecLength+6);
		
		String[] bytes = br.readLine().split("(\\s)+");
		byte[] bytesValues = new byte[32];
		for ( int i=0; i<BYTES_LENGTH; i++) {
			bytesValues[i] = (byte) Integer.parseInt(bytes[i]);
		}
		data = new long[4];
		ByteBuffer.wrap(bytesValues).asLongBuffer().get(data);
	}

	
		
	public static final float getDistance_Norm(AKAZE o1, AKAZE o2) {
		return Hamming.distance_norm(o1.data, o2.data, BITS_LENGTH);
	}
	
	public static final int getDistance(AKAZE o1, AKAZE o2) {
		return Hamming.distance(o1.data, o2.data);
	}
	
	@Override
	public int compareTo(ALocalFeature<AKAZEGroup> given) {
		if ( this == given ) return 0;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		for ( int i=0; i<data.length; i++ ) {
			int tComp = Long.compare(data[i], ((AKAZE)given).data[i]);
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}



	@Override
	public Class<AKAZEGroup> getGroupClass() {
		return AKAZEGroup.class;
	}

	
	@Override
	public String toString() {
		String tStr = super.toString();
		tStr += "\tdata:";
		for ( long value : data ) {
			tStr += "\t" + value;
		}
		tStr += "\n";
		return tStr;
	}
	
	public AKAZE getRandomPerturbated(int nBits) {
		long[] newData = RandomOperations.getPerturbated(data, nBits);
		return new AKAZE(this.kp, newData);
	}

	@Override
	public int getLength() {
		return NLONG;
	}

	@Override
	public long[] getValues() {
		return data;
	}

	@Override
	public int getNBits() {
		return BITS_LENGTH;
	}

}
