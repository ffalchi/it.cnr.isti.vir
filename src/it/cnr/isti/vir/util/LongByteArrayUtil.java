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
package it.cnr.isti.vir.util;

public class LongByteArrayUtil {
	private static final int MASK = 0xff;

	/**
	 * convert byte array (of size 4) to float
	 * @param test
	 * @return
	 */
	public static final long byteArrayToLong(byte test[]) {
		long bits = 0;
		int i = 0;
		for (int shifter = 7; shifter >= 0; shifter--) {
			bits |= ((long) test[i] & MASK) << (shifter * 4);
			i++;
		}

		return bits;
	}
	
	public static final long byteArrayToLong(byte byteArray[], int byteOffset) {
		long bits = 0;
		int i = 0;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((long) byteArray[i+byteOffset] & MASK) << (shifter * 8);
			i++;
		}

		return bits;
	}
	
	public static final long[] byteArrayToLongArray(byte byteArr[], int byteOffset, int n) {
		long[] arr = new long[n];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=byteArrayToLong(byteArr, byteOffset+4*i);
		}
		return arr;
	}
	
	
	public static final void longArrayToByteArray(long[] n, byte[] byteArray, int byteOffset) {
		for ( int i=0; i<n.length; i++) {
			longToByteArray(n[i], byteArray, byteOffset+4*i );
		}
		
	}

	/**
	 * convert int to byte array (of size 4)
	 * @param param
	 * @return
	 */
	public static final byte[] longToByteArray(long param) {
		byte[] result = new byte[4];
		for (int i = 0; i < 8; i++) {
			result[i] = (byte) ((param >>> (3 - i) * 8) & MASK);
		}
		return result;
	}
	
	public static final void longToByteArray(long param, byte[] byteArr, int byteOffset) {
		for (int i = 0; i < 8; i++) {
			byteArr[i+byteOffset] = (byte) ((param >>> (3 - i) * 8) & MASK);
		}
	}

	/**
	 * convert byte array to String.
	 * @param byteArray
	 * @return
	 */
	public static final String byteArrayToString(byte[] byteArray) {
		StringBuilder sb = new StringBuilder("[");
		if(byteArray == null) {
			throw new IllegalArgumentException("byteArray must not be null");
		}
		int arrayLen = byteArray.length;
		for(int i = 0; i < arrayLen; i++) {
			sb.append(byteArray[i]);
			if(i == arrayLen - 1) {
				sb.append("]");
			} else{
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
