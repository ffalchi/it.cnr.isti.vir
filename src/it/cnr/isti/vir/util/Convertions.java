package it.cnr.isti.vir.util;

public final class Convertions {
	
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
		//return (int) (b & 0xFF);
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
