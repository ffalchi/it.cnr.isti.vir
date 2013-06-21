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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.util.RandomOperations;

import java.awt.image.BufferedImage;

import Jama.EigenvalueDecomposition;


/*
 *      u       h0  h1  h2      x
 *  w * v   =   h3  h4  h5      y
 *      1       h6  h7  h8      1
 */



/*
 * Homographic Matrix h = 3x3
 *
 * Model: ( x y 1 )
 * Query: ( u v 1 )
 *
 * A =  (   x   y   1   0   0   0   -ux  -uy  -u
 *          0   0   0   x   y   1   -vx  -vy  -v   )
 *
 * A h = 0
 *
 * AtA = 0
 */



/**
 *
 * @author Fabrizio
 */
public class HomographyTransformation extends AbstractTransformation {

	static double minDet = 0.01;
	static double maxDet = 1.0;
	
    public static HomographyTransformation getRandom() {
        double[] values = RandomOperations.getDoubleArray(9);
        return new HomographyTransformation(values);
    }

    protected final double[] values; // 9
    protected double[] invV;

    private static final double[] zeros = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final Jama.Matrix b = new Jama.Matrix(zeros, zeros.length);

    /*
     *
     */

    public final HomographyTransformation getDeNormalized( ALocalFeaturesGroup gr1, ALocalFeaturesGroup gr2 ) {
        double[] newValues = new double[9];

        float  scale1 = gr1.getNormScale();
        float[] mean1 = gr1.getMeanXY();
        float  scale2 = gr2.getNormScale();
        float[] mean2 = gr2.getMeanXY();


        /*
         *  |   1/s     0       -tx/s     |   |           |   |   s     0     tx     |
         *  |   0       1/s     -ty/s     | * | values    | * |   0     s     ty     |
         *  |   0       0       1         |   |           |   |   0     0     1      |
         */

        newValues[0] = values[0] / scale1 + mean1[0] * values[6];
        newValues[1] = values[1] / scale1 + mean1[0] * values[7];
        newValues[2] = values[2] / scale1 + mean1[0] * values[8];
        newValues[3] = values[3] / scale1 + mean1[1] * values[6];
        newValues[4] = values[4] / scale1 + mean1[1] * values[7];
        newValues[5] = values[5] / scale1 + mean1[1] * values[8];
        newValues[6] = values[6];
        newValues[7] = values[7];
        newValues[8] = values[8];


        newValues[0] = scale2 * newValues[0];
        newValues[1] = scale2 * newValues[1];
        newValues[2] = - mean2[0] * newValues[0] - mean2[1] * newValues[1] + newValues[2]  ;
        newValues[3] = scale2 * newValues[3];
        newValues[4] = scale2 * newValues[4];
        newValues[5] = - mean2[0] * newValues[3] - mean2[1] * newValues[4] + newValues[5]  ;
        newValues[6] = scale2 * newValues[6];
        newValues[7] = scale2 * newValues[7];
        newValues[8] = - mean2[0] * newValues[6] - mean2[1] * newValues[7] + newValues[8]  ;


        return new HomographyTransformation(newValues);
    }

    public static HomographyTransformation getTransformation(float[][] pxy, float[][] puv) {

        // Matrix A
         double[][] tempM = new double[2 * pxy.length][9];
        for (int i = 0; i < pxy.length; i++) {

            int r = i * 2;
            tempM[r][0] = pxy[i][0];
            tempM[r][1] = pxy[i][1];
            tempM[r][2] = 1;
            tempM[r][3] = 0;
            tempM[r][4] = 0;
            tempM[r][5] = 0;
            tempM[r][6] = -puv[i][0] * pxy[i][0];
            tempM[r][7] = -puv[i][0] * pxy[i][1];
            tempM[r][8] = -puv[i][0];

            r++;
            tempM[r][0] = 0;
            tempM[r][1] = 0;
            tempM[r][2] = 0;
            tempM[r][3] = pxy[i][0];
            tempM[r][4] = pxy[i][1];
            tempM[r][5] = 1;
            tempM[r][6] = -puv[i][1] * pxy[i][0];
            tempM[r][7] = -puv[i][1] * pxy[i][1];
            tempM[r][8] = -puv[i][1];
        }
        Jama.Matrix A = new Jama.Matrix(tempM, pxy.length * 2, 9);
        Jama.Matrix AtA = A.transpose().times(A);

//                SingularValueDecomposition svd = AAt.svd();
        EigenvalueDecomposition eig = AtA.eig();
        double[] eigR = eig.getRealEigenvalues();
//                double[] eigI = eig.getImagEigenvalues();

        double[][] eigM = eig.getV().transpose().getArray();
//                double[] svdArr = svd.getSingularValues();
        double min = Double.MAX_VALUE;
        int minIndex = -1;
        for (int i = 0; i < eigR.length; i++) {
            double abs = Math.abs(eigR[i]);
            if (abs < min) {
                min = abs;
                minIndex = i;
            }
        }

        
        HomographyTransformation tRes = new HomographyTransformation(eigM[minIndex]);
        if ( tRes.isNice() ) return tRes;
                    
        return null;
    }

    public double getDeterminant() {
        return  + values[0] * values[4] * values[8]
                + values[1] * values[5] * values[6]
                + values[2] * values[3] * values[7]
                - values[0] * values[5] * values[7]
                - values[1] * values[3] * values[8]
                - values[2] * values[4] * values[6] ;
    }

    public HomographyTransformation(double[] values) {
        this.values = values;        
    }

    public final double[] getInverseValues() {
        if ( invV == null ) {
            invV = evInvValues();
        }
        return invV;
    }

    public final void getTransformed(float[] xy, float[] uv) {
        double w = values[6] * xy[0] + values[7] * xy[1] + values[8];
        uv[0] = (float) ( ( values[0] * xy[0] + values[1] * xy[1] + values[2] ) / w );
        uv[1] = (float) ( ( values[3] * xy[0] + values[4] * xy[1] + values[5] ) / w );   
    }

    public final static void getTransformed_static(float[] xy, float[] uv, double[] values) {
        double w = values[6] * xy[0] + values[7] * xy[1] + values[8];
        uv[0] = (float) ( ( values[0] * xy[0] + values[1] * xy[1] + values[2] ) / w );
        uv[1] = (float) ( ( values[3] * xy[0] + values[4] * xy[1] + values[5] ) / w );         
    }

    public final void getInvTransformed(float[] xy, float[] uv) {
        if ( invV == null ) {
            invV = evInvValues();
        }
        getTransformed_static(xy, uv, invV);
    }

    private final double[] evInvValues() {
        Jama.Matrix A = new Jama.Matrix(values, 3).transpose();
        Jama.Matrix inv = A.inverse();
        return inv.getRowPackedCopy();
    }

    @Override
    public BufferedImage overPrint(BufferedImage bck, BufferedImage over, float[][] matchingBox) {

    	float[] xy = new float[2];
    	float[] invTrXY = new float[2];

    	float trX = 0;
    	float trY = 0;
        int minX = 0;
        int maxX = bck.getWidth();
        int minY = 0;
        int maxY = bck.getHeight();

        if ( matchingBox != null ) {
            trX = matchingBox[0][0];
            trY = matchingBox[0][1];
//            double[] t1 = this.getTransformed(matchingBox[0]);
//            double[] t2 = this.getTransformed(matchingBox[0]);
//
//            double t = Math.min(t1[0],t2[0]);
//            if ( t1[0] > minX ) minX = (int) t1[0];
//            if ( t1[1] > minY ) minY = (int) t1[1];
//            if ( t2[0] < maxX ) maxX = (int) t2[0];
//            if ( t2[1] < maxY ) maxY = (int) t2[1];
        }

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                xy[0] = x;
                xy[1] = y;
                getInvTransformed(xy, invTrXY);
//				int invtrX = (int) Math.round( invTrXY[0] );
//				int invtrY = (int) Math.round( invTrXY[1] );
                invTrXY[0] -= trX;
                invTrXY[1] -= trY;
                if (invTrXY[0] > 0
                        && invTrXY[0] < over.getWidth()
                        && invTrXY[1] > 0
                        && invTrXY[1] < over.getHeight()) {
                    bck.setRGB(x, y, over.getRGB( (int) invTrXY[0], (int) invTrXY[1] ));
                }
            }
        }
        return bck;
    }

    public HomographyTransformation getInverse() {
        return new HomographyTransformation(getInverseValues());
    }
    
//    @Override
//    public Transformation getTranslated(double[] trasl) {
//        double[] newValues = values.clone();
////        newValues[2] += trasl[0];
////        newValues[5] += trasl[1];
//
//        return new HomographyTransformation(newValues);
//    }

    
    // from a BRIEF demo online
    // http://answers.opencv.org/question/2588/check-if-homography-is-good/
    // http://stackoverflow.com/questions/14954220/how-to-check-if-obtained-homography-matrix-is-good
    // http://stackoverflow.com/questions/10667834/trying-to-understand-the-affine-transform/
    public boolean isNice()
    {
      double det = this.getDeterminant();
      if ( 	det < minDet || det > maxDet )  return false;
      
      return true;
    }
    
    // from a BRIEF demo online
    // http://answers.opencv.org/question/2588/check-if-homography-is-good/
    // http://stackoverflow.com/questions/14954220/how-to-check-if-obtained-homography-matrix-is-good
    // http://stackoverflow.com/questions/10667834/trying-to-understand-the-affine-transform/
    public boolean isNice_Other()
    {
    	
        double det = values[0] * values[4] - values[3] * values[2];
        if (det < 0)
          return false;

        double N1 = Math.sqrt(values[0]*values[0] + values[3] * values[3]);
        if (N1 > 4 || N1 < 0.1)
          return false;

        double N2 = Math.sqrt(values[1] * values[1] + values[4] * values[4]);
        if (N2 > 4 || N2 < 0.1)
          return false;

        double N3 = Math.sqrt(values[6] * values[6] + values[7] * values[7]);
        if (N3 > 0.002)
          return false;
    	
        return true;

    }
      


}
