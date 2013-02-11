package it.cnr.isti.vir.util;

public class IntByteArrayUtil {
	private static final int MASK = 0xff;

	/**
	 * convert byte array (of size 4) to float
	 * @param test
	 * @return
	 */
	public static final int byteArrayToInt(byte test[]) {
		int bits = 0;
		int i = 0;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((int) test[i] & MASK) << (shifter * 4);
			i++;
		}

		return bits;
	}
	
	public static final int byteArrayToInt(byte byteArray[], int byteOffset) {
		int bits = 0;
		int i = 0;
		for (int shifter = 3; shifter >= 0; shifter--) {
			bits |= ((int) byteArray[i+byteOffset] & MASK) << (shifter * 8);
			i++;
		}

		return bits;
	}
	
	public static final int[] byteArrayToIntArray(byte byteArr[], int byteOffset, int n) {
		int[] arr = new int[n];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=byteArrayToInt(byteArr, byteOffset+4*i);
		}
		return arr;
	}
	
	
	public static final void intArrayToByteArray(int[] n, byte[] byteArray, int byteOffset) {
		for ( int i=0; i<n.length; i++) {
			intToByteArray(n[i], byteArray, byteOffset+4*i );
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