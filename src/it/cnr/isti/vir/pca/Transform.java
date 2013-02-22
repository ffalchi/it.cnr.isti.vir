package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.util.MatrixMath;
import it.cnr.isti.vir.util.VectorMath;

import java.util.List;

import Jama.Matrix;

public class Transform {

	double[] means;
	double[][] tr;
	
	public Transform(PCA pcaResults, int k) {
		this( pcaResults.getDominantComponents(k), pcaResults.getMeans());
	}
	
	public Transform(List<PrincipalComponent> pc, double[] means) {
		 Matrix features = PCA.getDominantComponentsMatrix(pc);
		 tr = features.transpose().getArray();
		 this.means = means;
	}
	
	
	public double[] getTransformed(double[] vec ) {
	    double[] vNorm = VectorMath.subtraction(vec, means);

	    return MatrixMath.times(tr,  vNorm);
	}
	/*
	public double[] getTransformed(double[] vec ) {
		double[] res = new double[featuresXpose.getRowDimension()];
		
	    double[] vNorm = VectorMath.subtraction(vec, means);
	    
	    double[][] temp = new double[1][];
	    temp[0] = vNorm;
	    Matrix adjustedInput = new Matrix( temp );
	    adjustedInput = adjustedInput.transpose();
	    Matrix xformedData = featuresXpose.times(adjustedInput);
	    
	    return xformedData.transpose().getArray()[0];
	}*/
}
