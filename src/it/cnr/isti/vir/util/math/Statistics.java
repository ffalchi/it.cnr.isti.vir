package it.cnr.isti.vir.util.math;

public class Statistics {

	public static double getStandardDeviation(double mean, double[] values) {

		return Math.sqrt(getVariance(mean, values));
	}

	public static double getVariance(double mean, double[] values) {
		int n = values.length;

		double acc = 0.0;

		for (int i = 0; i < n; i++) {
			double t = values[i] - mean;
			acc += t * t;
		}

		acc /= n;

		return acc;
	}

	/**
	 * FIXME: MinMax already exists in it.cnr.isti.vir.utils, but works only for int[].
	 * Code should be merged with the following code without breaking anything.. 
	 * @author Fabio Carrara <fabio.carrara@isti.cnr.it>
	 */
	
	public static class MinMax {
		public double min;
		public int iMin;
		public double max;
		public int iMax;

		public MinMax(double min, int iMin, double max, int iMax) {
			this.min = min;
			this.iMin = iMin;
			this.max = max;
			this.iMax = iMax;
		}
	}

	public static MinMax getMinMax(double[] values) {
		MinMax res = new MinMax(values[0], 0, values[0], 0);
		for (int i = 1; i < values.length; i++) {
			if (res.min > values[i]) {
				res.min = values[i];
				res.iMin = i;
			}
			if (res.max < values[i]) {
				res.max = values[i];
				res.iMax = i;
			}
		}

		return res;
	}

}
