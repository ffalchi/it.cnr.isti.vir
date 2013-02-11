package it.cnr.isti.vir.util;

public class Trigonometry {

	/*
	 * returns values between 0 and 2.0*Math.Pi
	 */
	public static final double getStdRadian( double value ) {
		double res = value;
		if ( res >= 0) {
			while ( res >= 2.0*Math.PI) {
				res -= 2.0*Math.PI;
			}
			return res;
		} else {
			while ( res < 0 ) {
				res += 2.0*Math.PI;
			}
			return res;
		}
	}
}
