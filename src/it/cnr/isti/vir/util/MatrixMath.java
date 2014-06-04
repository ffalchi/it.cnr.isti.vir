/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi and Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
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


public class MatrixMath {

	public static double[] times(double[][] matrix, double[] vector) {
		if (matrix[0].length == vector.length) {
			double[] res = new double[matrix.length];
			for (int i1 = 0; i1 < matrix.length; i1++) {
				for (int i2 = 0; i2 < vector.length; i2++) {
					res[i1] += matrix[i1][i2] * vector[i2];

				}
			}
			return res;
		} else {
			System.out
					.println("Error in matrix-vector product: dimensions are not consistent.");

			return null;
		}
	}


	// floatMatrix*floatVector
	public static float[] times(float[][] matrix, float[] vector) {
		if (matrix[0].length == vector.length) {
			float[] res = new float[matrix.length];
			for (int i1 = 0; i1 < matrix.length; i1++) {
				for (int i2 = 0; i2 < vector.length; i2++)
					res[i1] += matrix[i1][i2] * vector[i2];
			}
			return res;
		} else {
			System.out
					.println("Error in matrix-vector product: dimensions are not consistent.");

			return null;
		}
	}

     
	// floatMatrix*floatVector
	public static float[] times(double[][] matrix, float[] vector) {
		if (matrix[0].length == vector.length) {
			float[] res = new float[matrix.length];
			for (int i1 = 0; i1 < matrix.length; i1++) {
				for (int i2 = 0; i2 < vector.length; i2++)
					res[i1] += matrix[i1][i2] * vector[i2];
			}
			return res;
		} else {
			System.out.println("Error in matrix-vector product: dimensions are not consistent.");

			return null;
		}
	}
        

	// floatMatrix*doubleVector
	public static double[] times(float[][] matrix, double[] vector) {
		if (matrix[0].length == vector.length) {
			double[] res = new double[matrix.length];
			for (int i1 = 0; i1 < matrix.length; i1++) {
				for (int i2 = 0; i2 < vector.length; i2++)
					res[i1] += matrix[i1][i2] * vector[i2];
			}
			return res;
		} else {

			System.out
					.println("Error in matrix-vector product: dimensions are not consistent.");
			return null;
		}
	}


	// doubleMatrix*doubleMatrix
	public static double[][] times(double[][] matrix1, double[][] matrix2) {
		if (matrix1[0].length == matrix2.length) {
			double[][] res = new double[matrix1.length][matrix2[0].length];
			for (int i1 = 0; i1 < matrix1.length; i1++) {
				for (int i2 = 0; i2 < matrix2[0].length; i2++) {
					for (int k = 0; k < matrix2.length; k++)
						res[i1][i2] += matrix1[i1][k] * matrix2[k][i2];
				}
			}
			return res;
		} else {
			System.out
					.println("Error in matrix product: dimensions are not consistent. ");
			return null;
		}
	}


	// floatMatrix*floatMatrix
	public static float[][] times(float[][] matrix1, float[][] matrix2) {
		if (matrix1[0].length == matrix2.length) {
			float[][] res = new float[matrix1.length][matrix2[0].length];
			for (int i1 = 0; i1 < matrix1.length; i1++) {
				for (int i2 = 0; i2 < matrix2[0].length; i2++) {
					for (int k = 0; k < matrix2.length; k++)
						res[i1][i2] += matrix1[i1][k] * matrix2[k][i2];
				}
			}
			return res;
		} else {
			System.out
					.println("Error in matrix product: dimensions are not consistent. ");
			return null;
		}
	}


	public static long count_occurrences(double[][] m, double val) {
		long count = 0;
		for (int i1 = 0; i1 < m.length; i1++)
			for (int i2 = 0; i2 < m[0].length; i2++) {
				if (m[i1][i2] == val)
					count++;
			}
		return count;
	}

	public static long count_occurrences(double[][] m, float val) {
		long count = 0;
		for (int i1 = 0; i1 < m.length; i1++)
			for (int i2 = 0; i2 < m[0].length; i2++) {
				if ((float) m[i1][i2] == val)
					count++;
			}
		return count;
	}
    
     
    }