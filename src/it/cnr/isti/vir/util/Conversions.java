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

public final class Conversions {
	
	protected static final String[] getNumbers(String str) {
		return str.split("\\s+");
	}

	public static final byte[] stringToByteArray(String str) {

		String[] numbers = getNumbers(str);

		byte[] bytes = new byte[numbers.length];

		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = Byte.parseByte(numbers[i]);
		}

		return bytes;

	}

	public static final byte stringToUnsignedByte(String str) {

		return (byte) (Integer.parseInt(str) -128);

	}
	
	public static final byte[] stringToUnsignedByteArray(String str) {

		String[] numbers = getNumbers(str);

		byte[] bytes = new byte[numbers.length];

		for (int i = 0; i < bytes.length; i++) {
			//bytes[i] = (byte) Integer.parseInt(numbers[i]);
			bytes[i] = (byte) (Integer.parseInt(numbers[i]) -128);
		}

		return bytes;

	}

	public static final short[] stringToShortArray(String str) {

		String[] numbers = getNumbers(str);

		short[] shorts = new short[numbers.length];

		for (int i = 0; i < shorts.length; i++) {
			shorts[i] = Short.parseShort(numbers[i]);
		}

		return shorts;

	}

	
	public static final int unsignedByteToInt(byte b) {
		return b+128;
	}	

	public static final int[] unsignedByteArrayToIntArray(byte[] b) {
		//if (b == null) return null;
			
		int[] result = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			//result[i] = (int) (b[i] & 0xFF);
			result[i] = (int) b[i] +128;
		}
		return result;
	}
	
}
