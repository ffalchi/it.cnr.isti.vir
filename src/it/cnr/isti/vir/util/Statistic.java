/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.util;

import it.cnr.isti.vir.util.math.VectorMath;

import java.util.Arrays;

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

	/**
	 * @param value
	 * @return percentile
	 */
	public static double getPercentile(double[] values, double value) {
		double[] ord = values.clone();
		Arrays.sort(ord);
		int i=0;
		for ( i=0; i<ord.length; ) {
			if ( ord[i] >= value ) return i/(double) values.length;
		}
		return 1.0;
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
				double v = getCovariance(input, i, j, mean);
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
	
	private static double getCoeffOfMeanDev(double[] values, double mean) {
		return getMeanDev(values, mean)/mean;
	}
	
	private static double getMeanDev(double[] values, double mean) {
		double sum = 0;
		for ( int i=0; i<values.length; i++) {
			double diff = values[i]-mean;
			sum += Math.abs(diff);
		}
		return sum/values.length;
	}
	
	public static final double getCoeffOfVariation(double[] values) {
		double mean = getMean(values);
		return getCoeffOfVariation(values, mean);
	}
	
	public static final double getCoeffOfVariation(double[] values, double mean) {
		return getStandardDeviation(values, mean)/mean;
	}
	
	public static final double getStandardDeviation(double[] values) {
		double mean = getMean(values);
		return getStandardDeviation(values, mean);
	}
	
	public static final double getStandardDeviation(double[] values, double mean) {
		return Math.sqrt(getVariance(values, mean));
	}
	
	public static final double getIntrinsicDimensionality(double[] values) {
		double mean = getMean(values);
		return (getVariance(values,mean));
	}
	
	public static final double getIntrinsicDimensionality(double mean, double variance) {
		return mean*mean/variance/2.0;
	}
	
	public static final double getVariance(double[] values, double mean) {
		double sum = 0.0;
		for ( int i=0; i<values.length; i++) {
			double diff = values[i]-mean;
			sum += diff * diff;
		}
		return sum/values.length;
	}
	
	public static final double getMean(double[] values) {
		double sum = 0;
		
		for ( int i=0; i<values.length; i++) {
			sum += values[i];
		}
		return sum/values.length;
	}


	
	
	
	
}
