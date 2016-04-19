package it.cnr.isti.vir.util.math;

public class Binarization {

	
	/**
	 * 
	 * This method binarizes floats in longs.
	 * Float length must be multiple of 64
	 * 
	 * @param f
	 * @param thr
	 * @return
	 */
	static final public long[] getLongs(float[] f, float thr) {
		
		long[] res = new long[f.length / 64];
		
		for ( int iRes=0, iF=0; iRes<res.length; iRes++ ) {
			for ( int ib=0; ib<64; ib++, iF++) {
				if ( f[iF] > thr )
					res[iRes] = res[iRes] | ( 1L << ib );
			}
		}
		
		return res;
		
	}
	
	/**
	 * 
	 * This method binarizes floats in longs.
	 * Float length must be multiple of 64
	 * 
	 * @param f
	 * @param thr
	 * @return
	 */
	static final public long[] getLongs(float[] f ) {
		
		long[] res = new long[f.length / 64];
		
		for ( int iRes=0, iF=0; iRes<res.length; iRes++ ) {
			for ( int ib=0; ib<64; ib++, iF++) {
				if ( f[iF] > 0 )
					res[iRes] = res[iRes] | ( 1L << ib );
			}
		}
		
		return res;
		
	}
}
