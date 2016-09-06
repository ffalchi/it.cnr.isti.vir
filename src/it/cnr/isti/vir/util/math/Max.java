package it.cnr.isti.vir.util.math;

public class Max {

	public static double get(double[] values) {
		double res = Double.MIN_VALUE;
		
		for ( double v : values )
			if ( v>res) res = v;
		
		return res;
	}
	
	public static float get(float[] values) {
		float res = Float.MIN_VALUE;
		
		for ( float v : values )
			if ( v>res) res = v;
		
		return res;
	}
	
	public static int get(byte[] values) {
		byte res = Byte.MIN_VALUE;
		
		for ( byte v : values )
			if ( v>res) res = v;
		
		return res;
	}

	public static int get(int[] values) {
		int res = Integer.MIN_VALUE;
		
		for ( int v : values )
			if ( v>res) res = v;
		
		return res;
	}
	
	public static long get(long[] values) {
		long res = Long.MIN_VALUE;
		
		for ( long v : values )
			if ( v>res) res = v;
		
		return res;
	}
}
