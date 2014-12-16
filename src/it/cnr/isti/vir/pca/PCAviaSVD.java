/*******************************************************************************
 * Copyright (c) 2013, Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
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

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.*;
import org.ejml.ops.SingularOps;

public class PCAviaSVD {
    /** Vector of empirical means.   */
    double[] means;
    
    /** Matrix of principal components (principal components are stored in row).  */
    DenseMatrix64F principalComponentsMatrix;
    
    public double[] getMeans() {
        return means;
    }
    
    public DenseMatrix64F getPrincipalComponentsMatrix() {
        return principalComponentsMatrix;
    }
    
    
    /**
     *
     * @param dataMatrix Set of all data vectors, one vector per column
     * @throws Exception
     */
    public PCAviaSVD(double[][] dataMatrix) throws Exception {//dataMatrix=[x1|x2|..|xN], xi  D-dim
        int D=dataMatrix.length;
        int N=dataMatrix[0].length;
        
        //evaluating means
        means = new double[D];
        System.out.print("PCA is evaluating mean...");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < D; j++)
                means[j] += dataMatrix[j][i];
        
        for (int i = 0; i < D; i++)
            means[i] /= N;
        
        System.out.println("done");
        //data centering
        DenseMatrix64F X= new DenseMatrix64F(D,N);
        for(int i1=0; i1<D;i1++)
            for(int i2=0;i2<N; i2++)
                X.set(i1, i2, dataMatrix[i1][i2]-means[i1]);
        
        //evaluating SVD
        System.out.print("PCA is evaluating principal components via SVD...");
        SingularValueDecomposition<DenseMatrix64F> svd=DecompositionFactory.svd(D, N, true, false, false);//compute only U
        
        
        long start = System.currentTimeMillis();
        if(!svd.decompose(X))
            throw new Exception("SVD failed");
        
        System.out.println("Time: " + (System.currentTimeMillis()-start)/1000 );
        
        DenseMatrix64F U=svd.getU(null, true);//U trasposed
        DenseMatrix64F W= svd.getW(null);
        
        //sorting principal components
        SingularOps.descendingOrder(U,true , W, null, false);
        
        principalComponentsMatrix=U;
        System.out.println("done");
    }
    /**
     *
     * @param reductionDim  Number of dimensions used to describe the data (in the dimensionally reduced space)
     * @return Matrix of first <i>reductionDim</i> principal components.
     * @throws Exception
     */
    public double[][] getPcaMatrix(int reductionDim) throws Exception {
        int D=principalComponentsMatrix.numRows;
        if(reductionDim>D || reductionDim< 0){
            Log.info_verbose("The number of dimensions to be used to describe the data "
                    + "must be positive and less than or equal to the dimension of original data. ");
            Log.info_verbose("The reduced dimension is set equal to original data dimension.");
            reductionDim=D;
        }
        
        double[][] pcaMatrix=new double[reductionDim][D];
        for(int i1=0; i1<reductionDim;i1++)
            for(int i2=0; i2<D;i2++)
                pcaMatrix[i1][i2]= principalComponentsMatrix.get(i1, i2);
        
        return pcaMatrix;      
        
    }
    
 /**
 * @return Principal components matrix 
 * @throws Exception
 */
public double[][] getPcaMatrix() throws Exception {
        int D=principalComponentsMatrix.numRows;
               
        double[][] pcaMatrix=new double[D][D];
        for(int i1=0; i1<D;i1++)
            for(int i2=0; i2<D;i2++)
                pcaMatrix[i1][i2]= principalComponentsMatrix.get(i1, i2);
        
        return pcaMatrix;      
        
    
 }
    
    
}

