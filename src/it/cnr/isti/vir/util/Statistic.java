package it.cnr.isti.vir.util;

public class Statistic {
	
	
	public static double[][] getNormalized_ZeroMean(double[][] input) {
		return getNormalized_ZeroMean(input, getMeans(input));
	}

	public static double[][] getNormalized_ZeroMean(double[][] input, double[] mean) {
		int nVectors = input.length;
		int nD = input[0].length;
		double[][] ret = new double[nVectors][nD];
		for (int iObj = 0; iObj < nVectors; iObj++) {
			ret[iObj] = VectorMath.subtraction(input[iObj], mean);
		}
		return ret;
	}

	
	public static double[] getMeans(double[][] input) {
		int nVectors = input.length;
		int nD = input[0].length;

		double[] mean = new double[nD];
		for (int i = 0; i < nVectors; i++) {
			double[] vec = input[i];
			for (int j = 0; j < nD; j++) {
				mean[j] = mean[j] + vec[j];
			}
		}
		for (int i = 0; i < mean.length; i++) {
			mean[i] = mean[i] / nVectors;
		}
		
		return mean;
	}
	
	
	public static double[][] getCovariance(double[][] input) {
		return getCovariance(input, getMeans(input));
	}
	

	
	
	public static double[][] getCovariance(double[][] input, double[] mean) {
		int nD = input[0].length;
		double[][] ret = new double[nD][nD];
		for (int i = 0; i < nD; i++) {
			for (int j = i; j < nD; j++) {
				double v = getCovariance(input, j, i, mean);
				ret[i][j] = v;
				ret[j][i] = v;
			}
		}
		return ret;
	}

	/**
	 * Returns the covariance.
	 */
	private static double getCovariance(double[][] data, int d1, int d2, double[] means) {
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			double v1 = data[i][d1] - means[d1];
			double v2 = data[i][d2] - means[d2];
			sum = sum + (v1 * v2);
		}
		int n = data.length;
		double ret = (sum / (n - 1));
		return ret;
	}

}
