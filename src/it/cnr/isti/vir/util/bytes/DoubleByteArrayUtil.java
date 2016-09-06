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

public class DoubleByteArrayUtil {
	//private static final int MASK = 0xff;	

	public static final int BYTES = Double.SIZE / 8;
	
	/**
	 * @param byteArray		source bytes
	 * @param byteOffset	source offset
	 * @return
	 */
	public static final double get(byte encodedValue[], int byteOffset) {
		long longValue = LongByteArrayUtil.get(encodedValue, byteOffset);
		return Double.longBitsToDouble(longValue);
	}
	
	/**
	 * @param byteArr		source bytes
	 * @param byteOffset	source offset
	 * @param nDoubless		desired array length
	 * @return
	 */
	public static final double[] get(byte byteArr[], int byteOffset, int nDoubles) {
		double[] arr = new double[nDoubles];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=get(byteArr, byteOffset+(Double.SIZE/Byte.SIZE)*i);
		}
		return arr;
	}
	
	
	/**
	 * @param d				source
	 * @param byteArray		dest
	 * @param byteOffset	dest offset
	 * @return				new offset
	 */
	public static final int convToBytes(double d, byte[] byteArray, int byteOffset) {
		return LongByteArrayUtil.convToBytes(Double.doubleToRawLongBits(d), byteArray, byteOffset );
	}
	
	/**
	 * @param d				source array
	 * @param byteArray		dest
	 * @param byteOffset	dest offset
	 * @return				new offset
	 */
	public static final int convToBytes(double[] d, byte[] byteArray, int byteOffset) {
		int offset = byteOffset;
		for ( int i=0; i<d.length; i++) {
			offset = LongByteArrayUtil.convToBytes(Double.doubleToRawLongBits(d[i]), byteArray, offset );
		}
		return offset;
	}
}
