package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.localfeatures.SIFT;

import java.util.Iterator;

public  class Mean {

	public static byte[] getMean(byte[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		long[] tempVec = new long[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}
		
		byte[] newVec = new byte[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = (byte) ( Math.round( (double) tempVec[iD] / n ) );
		}
				
		return newVec;
	}

	public static short[] getMean(short[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		long[] tempVec = new long[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}
		
		short[] newVec = new short[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = (short) ( Math.round( (double) tempVec[iD] / n ) );
		}
				
		return newVec;
	}
	
	public static float[] getMean(float[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		double[] tempVec = new double[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}
		
		float[] newVec = new float[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = Math.round( tempVec[iD] / n );
		}
				
		return newVec;
	}
	
	
	public static double[] getMean(double[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		double[] tempVec = new double[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}

		for ( int iD=0; iD<dim; iD++) {
			tempVec[iD] = Math.round( tempVec[iD] / n );
		}
				
		return tempVec;
	}
	
}
