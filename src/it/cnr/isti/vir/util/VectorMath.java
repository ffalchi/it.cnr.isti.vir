package it.cnr.isti.vir.util;

public class VectorMath {

	public static double[] subtraction(double[] v1, double[] v2) {
		int nD = v1.length;
		double[] res = new double[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - v2[iD];
		}
		return res;
	}
	
}

