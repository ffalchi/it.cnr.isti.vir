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

public class FloatByteArrayUtil {
	private static final int MASK = 0xff;	

	/**
	 * @param byteArray		source bytes
	 * @param byteOffset	source offset
	 * @return
	 */
	public static final float get(byte encodedValue[], int byteOffset) {
		int intValue = IntByteArrayUtil.get(encodedValue, byteOffset);
		return Float.intBitsToFloat(intValue);
	}
	
	/**
	 * @param byteArr		source bytes
	 * @param byteOffset	source offset
	 * @param nFloats		desired array length
	 * @return
	 */
	public static final float[] get(byte byteArr[], int byteOffset, int nFloats) {
		float[] arr = new float[nFloats];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=get(byteArr, byteOffset+8*i);
		}
		return arr;
	}
	
	
	/**
	 * @param f				source
	 * @param byteArray		dest
	 * @param byteOffset	dest offset
	 * @return				new offset
	 */
	public static final int convToBytes(float f, byte[] byteArray, int byteOffset) {
		return IntByteArrayUtil.intToByteArray(Float.floatToRawIntBits(f), byteArray, byteOffset );
	}
	
	/**
	 * @param f				source array
	 * @param byteArray		dest
	 * @param byteOffset	dest offset
	 * @return				new offset
	 */
	public static final int convToBytes(float[] f, byte[] byteArray, int byteOffset) {
		int offset = byteOffset;
		for ( int i=0; i<f.length; i++) {
			offset = IntByteArrayUtil.intToByteArray(Float.floatToRawIntBits(f[i]), byteArray, offset );
		}
		return offset;
	}

}
