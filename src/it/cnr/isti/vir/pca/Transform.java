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
