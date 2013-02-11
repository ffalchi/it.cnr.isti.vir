package it.cnr.isti.vir.util;

public class FloatByteArrayUtil {
	private static final int MASK = 0xff;

	/**
	 * convert byte array (of size 4) to float
	 * @param test
	 * @return
	 */
	public static final float byteArrayToFloat(byte test[]) {
		int bits = 0;
		int i = 0;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((int) test[i] & MASK) << (shifter * 8);
			i++;
		}

		return Float.intBitsToFloat(bits);
	}
	
	public static final float byteArrayToFloat(byte byteArray[], int byteOffset) {
		int bits = 0;
		int i = 0;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((int) byteArray[i+byteOffset] & MASK) << (shifter * 8);
			i++;
		}

		return Float.intBitsToFloat(bits);
	}
	
	public static final float[] byteArrayToFloatArray(byte byteArr[], int byteOffset, int nFloats) {
		float[] arr = new float[nFloats];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=byteArrayToFloat(byteArr, byteOffset+8*i);
		}
		return arr;
	}
	
	

	/**
	 * convert float to byte array (of size 4)
	 * @param f
	 * @return
	 */
	public static final byte[] floatToByteArray(float f) {
		int i = Float.floatToRawIntBits(f);
		return intToByteArray(i);
	}
	
	public static final void floatToByteArray(float f, byte[] byteArray, int byteOffset) {
		intToByteArray(Float.floatToRawIntBits(f), byteArray, byteOffset );
	}
	
	public static final void floatArrayToByteArray(float[] f, byte[] byteArray, int byteOffset) {
		for ( int i=0; i<f.length; i++) {
			intToByteArray(Float.floatToRawIntBits(f[i]), byteArray, byteOffset+8*i );
		}
		
	}


	/**
	 * convert int to byte array (of size 4)
	 * @param param
	 * @return
	 */
	public static final byte[] intToByteArray(int param) {
		byte[] result = new byte[4];
		for (int i = 0; i < 4; i++) {
			result[i] = (byte) ((param >>> (3 - i) * 8) & MASK);
		}
		return result;
	}
	
	public static final void intToByteArray(int param, byte[] byteArr, int byteOffset) {
		for (int i = 0; i < 4; i++) {
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