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

public class IntByteArrayUtil {
	private static final int MASK = 0xff;

	
	/**
	 * @param encodedValue		Source bytes
	 * @param byteOffset		Source bytes offset
	 * @return
	 */
	public static final int get(byte encodedValue[], int byteOffset) {
	    int index = byteOffset;
	    int value = encodedValue[index++] << Byte.SIZE * 3;
	    value ^= (encodedValue[index++] & 0xFF) << Byte.SIZE * 2;
	    value ^= (encodedValue[index++] & 0xFF) << Byte.SIZE * 1;
	    value ^= (encodedValue[index++] & 0xFF);
	    return value;
	}
	
	/**
	 * @param byteArr		Source bytes
	 * @param byteOffset	Source bytes offset
	 * @param n				Desired array length
	 * @return
	 */
	public static final int[] get(byte byteArr[], int byteOffset, int n) {
		int[] arr = new int[n];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=get(byteArr, byteOffset+(Integer.SIZE/Byte.SIZE)*i);
		}
		return arr;
	}
	
	
	/**
	 * @param n				Source array
	 * @param byteArray		Destination bytes
	 * @param byteOffset	Destination offset
	 * @return				Updated offset
	 */
	public static final int convToBytes(int[] n, byte[] byteArray, int byteOffset) {
		int index = byteOffset;
		for ( int i=0; i<n.length; i++) {
			index = convToBytes(n[i], byteArray, index );
		}
		return index;
	}

	
	/**
	 * @param value			Source value
	 * @param encodedValue	Destination bytes
	 * @param byteOffset	Destination offset
	 * @return				Updated offset
	 */
	public static final int convToBytes(int value, byte[] encodedValue, int byteOffset) {
	    int index = byteOffset;
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 3);
	    encodedValue[index++] = (byte) (value >> Byte.SIZE * 2);   
	    encodedValue[index++] = (byte) (value >> Byte.SIZE);   
	    encodedValue[index++] = (byte) value;
	    return index;
	}

}
