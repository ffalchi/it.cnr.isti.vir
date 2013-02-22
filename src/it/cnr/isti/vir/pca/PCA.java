package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.util.Log;
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
