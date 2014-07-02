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
