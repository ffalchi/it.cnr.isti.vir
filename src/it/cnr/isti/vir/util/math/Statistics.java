package it.cnr.isti.vir.util.math;

public class Statistics {
	
	public static double getStandardDeviation(double mean, double[] values) {
		
		return Math.sqrt(getVariance(mean,values));
	}
	
	public static double getVariance(double mean, double[] values) {
		int n = values.length;
		
		double acc = 0.0;
		
		for ( int i=0; i<n; i++ ) {
			double t = values[i]-mean;
			acc += t*t;
		}
		
		acc /= n;
		
		return acc;
	}

}
