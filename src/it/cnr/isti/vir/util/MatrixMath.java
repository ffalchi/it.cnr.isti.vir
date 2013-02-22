package it.cnr.isti.vir.util;

public class MatrixMath {

	public static double[] times(double[][] matrix, double[] vector) {
		double[] res = new double[matrix.length];
		for(int i1=0; i1<matrix.length; i1++) {
			for(int i2=0; i2<matrix.length; i2++) {
				res[i1] += matrix[i1][i2] * vector[i2];
			}
		}
		return res;
	}
}
