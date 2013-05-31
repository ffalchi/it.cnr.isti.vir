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


public class LongByteArrayUtil {
	private static final int MASK = 0xff;

	
	/**
	 * @param encodedValue	source bytes
	 * @param byteOffset	source bytes offset
	 * @return
	 */
	public static final long get(byte encodedValue[], int byteOffset) {
	    int index = byteOffset;
	    long value = (long) encodedValue[index++] << Byte.SIZE * 7;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 6;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 5;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 4;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 3;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 2;
	    value ^= (long) (encodedValue[index++] & 0xFF) << Byte.SIZE * 1;
	    value ^= (long) (encodedValue[index++] & 0xFF);
	    return value;
	}
	
	/**
	 * @param byteArr		source bytes
	 * @param byteOffset	source bytes offset
	 * @param n				desired array length
	 * @return
	 */
	public static final long[] get(byte byteArr[], int byteOffset, int n) {
		long[] arr = new long[n];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=get(byteArr, byteOffset+(Long.SIZE/Byte.SIZE)*i);
		}
		return arr;
	}
	
	
	/**
	 * @param data			long array
	 * @param byteArray		dest bytes
	 * @param byteOffset	dest bytes offset
	 */
	public static final int convToBytes(long[] data, byte[] byteArr, int byteOffset) {
		int index = byteOffset;
		for ( int i=0; i<data.length; i++) {
			index  = convToBytes(data[i], byteArr, index );
		}
		return index;
	}
	
	/**
	 * @param value			long source value
	 * @param encodedValue	dest bytes
	 * @param byteOffset	dest bytes offset
	 */
	public static final int convToBytes(long value, byte[] encodedValue, int byteOffset) {
	    int index = byteOffset;
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 7);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 6);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 5);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 4);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 3);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 2);   
	    encodedValue[index++] = (byte) (value >> Byte.SIZE);   
	    encodedValue[index++] = (byte) value;
	    return index;
	}

}
