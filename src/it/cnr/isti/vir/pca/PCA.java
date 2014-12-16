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
package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.Statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class PCA {

	Matrix covMatrix;
	EigenvalueDecomposition eigenstuff;
	SortedSet<PrincipalComponent> principalComponents;
	double[] means;

	public PCA(double[][] input) {
		means = new double[input[0].length];
		Log.info_verbose("PCA is evaluating means");
		means = Statistic.getMeans(input);
		Log.info_verbose("PCA is evaluating covariance");
		double[][] cov = Statistic.getCovariance(input, means);
		covMatrix = new Matrix(cov);
		Log.info_verbose("PCA is evaluating Eigens");
		eigenstuff = covMatrix.eig();
		double[] eigenvalues = eigenstuff.getRealEigenvalues();
		Matrix eigenvectors = eigenstuff.getV();
		double[][] vecs = eigenvectors.getArray();
		int numComponents = eigenvectors.getColumnDimension();
		principalComponents = new TreeSet<PrincipalComponent>();
		for (int i = 0; i < numComponents; i++) {
			double[] eigenvector = new double[numComponents];
			for (int j = 0; j < numComponents; j++) {
				eigenvector[j] = vecs[i][j];
			}
			principalComponents.add(new PrincipalComponent(eigenvalues[i],eigenvector));
		}
	}

	public double[] getDominantComponentsEigenValues() {
		double[] res = new double[principalComponents.size()];
		int i=0;
		for (PrincipalComponent pc : principalComponents) {
			res[i++] = pc.eigenValue;
		}
		return res;
	}
	
	
	/**
	 * Returns the top n principle components in descending order of relevance.
	 */
	public List<PrincipalComponent> getDominantComponents(int n) {
		List<PrincipalComponent> ret = new ArrayList<PrincipalComponent>();
		int count = 0;
		for (PrincipalComponent pc : principalComponents) {
			ret.add(pc);
			count++;
			if (count >= n) {
				break;
			}
		}
		return ret;
	}

	public static Matrix getDominantComponentsMatrix(List<PrincipalComponent> dom) {
		int nVectors = dom.get(0).eigenVector.length;
		int nDim = dom.size();
		Matrix matrix = new Matrix(nVectors, nDim);
		for (int col = 0; col < nDim; col++) {
			for (int row = 0; row < nVectors; row++) {
				matrix.set(row, col, dom.get(col).eigenVector[row]);
			}
		}
		return matrix;
	}
	
	public Matrix getDominantComponentsMatrix() {
		return getDominantComponentsMatrix(getDominantComponents(principalComponents.size()));
	}
	
	public Matrix getDominantComponentsMatrix(int n) {
		return getDominantComponentsMatrix(getDominantComponents(n));
	}


	public int getNumComponents() {
		return principalComponents.size();
	}

	public double[] getMeans() {
		return means;
	}
	

}
