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
package 
it.cnr.isti.vir.geom;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class AffineTransformation extends AbstractTransformation {

	// | u  |           | m0	m2 |	| x |		| tx |
	// | v	|	=   | m1	m3 |	| y |	+	| ty |
	
//	double[] values; // | m0 m1 m2 m3 tx ty|

	
	final double[] values;
	double[] invValues;
	
	public AffineTransformation(double[] values) {
		this.values = values;
		this.invValues = getInverseValues();
	}

	public double[] getInverseValues() {
		if (invValues == null) {
			this.invValues = this.evInverseValues();
		}
		return invValues;
	}

	public final double[] evInverseValues() {
		// INVERSE MATRIX
		//
		// 1 / ( m0 * m3 - m1 * m2 )
		//
		// * | m3 -m1 |
		// | -m2 m0 |

		double[] resValues = new double[6];

		double norm = (values[0] * values[3] - values[1] * values[2]);
		if (norm == 0.0)
			return null;
		norm = 1.0 / norm;

		resValues[0] = norm * values[3];
		resValues[1] = -norm * values[1];
		resValues[2] = -norm * values[2];
		resValues[3] = norm * values[0];
		resValues[4] = -(resValues[0] * values[4] + resValues[2] * values[5]);
		resValues[5] = -(resValues[1] * values[4] + resValues[3] * values[5]);

		return resValues;
	}
	
	public AffineTransformation getInverse( ) {	
		return new AffineTransformation(getInverseValues());
	}

	public void getTransformed(float[] xy, float[] uv) {
		getTransformed_Static(xy, values, uv);
	}

	public void getInvTransformed(float[] xy, float[] uv) {
//		getTransformed_Static(xy, invValues, uv);
	}

//	public final double[] getTransformed( double[] xy ) {
//            double[] res = new double[2];
//            getTransformed_Static(xy, values, res);
//            return res;
//	}
	
//	public final double[] getInvTransformed( double[] xy ) {
//             double[] res = new double[2];
//             getTransformed_Static(xy, invValues, res);
//             return res;
//	}
	
//	public final int[] getInverseTransformedCoordinates_int( double[] xy ) {
//		return getTransformedCoordinates_int(xy, invValues);
//	}
	
	public final double getScale( ) {
		return Math.sqrt( Math.pow( values[0]+values[1] , 2) + Math.pow( values[3]+values[4] , 2) ) / Math.sqrt(2.0);
	}
	
	public final double getOri() {
		return getTransformedOrientation(Math.PI)-Math.PI;
	}


	public double getTransformedOrientation(double angle) {
		double[] coord = new double[2];
		coord[0] = Math.sin(angle);
		coord[1] = Math.cos(angle);
		
		double[] trCoord = new double[2];
		// m1*x+m2*y
		trCoord[0] = values[0]*coord[0]+values[2]*coord[1];
		// m3*x+m4*y
		trCoord[1] = values[1]*coord[0]+values[3]*coord[1];
		
		if ( trCoord[1] == 0 ) {
			if ( trCoord[0] > 0 ) return 0;
			else return Math.PI;
		}
		
		if ( trCoord[0] == 0 ) {
			if ( trCoord[1] > 0 ) return Math.PI/2.0;
			else return -Math.PI/2.0;
		}
		
		double newOri = Math.atan(trCoord[0]/trCoord[1]);
		
		if ( trCoord[0]<0 ) {
			newOri += Math.PI;
		}
		
		while ( newOri >  Math.PI ) newOri -= 2.0 * Math.PI;
		while ( newOri < -Math.PI ) newOri += 2.0 * Math.PI;
		
		return newOri;
		
	}
	




	/*
	 * 
	 */
	public static final void getTransformed_Static( float[] xy, double[] trValues, float[] uv) {
//                double[] res = new double[2];
                // m1*x+m2*y+x
                uv[0] = (float) ( trValues[0]*xy[0]+trValues[2]*xy[1]+trValues[4] );
                // m3*x+m4*y+y
                uv[1] = (float) ( trValues[1]*xy[0]+trValues[3]*xy[1]+trValues[5] );
//                return uv;
	}
	
//	/*
//	 *
//	 */
//	public static final int[] getTransformedCoordinates_int( double[] xy, double[] trValues) {
//		int[] res = new int[2];
//
//		// m1*x+m2*y+x
//		res[0] = (int) ( trValues[0]*xy[0]+trValues[2]*xy[1]+trValues[4] );
//
//		// m3*x+m4*y+y
//		res[1] = (int) ( trValues[1]*xy[0]+trValues[3]*xy[1]+trValues[5] );
//
//		return res;
//	}
	

//	public static final double[] getInverseTransformedCoordinates( double[] xy, double[] trValues) {
//		
//		double[] res = new double[2];
//		double tx = xy[0] - trValues[4];
//		double ty = xy[1] - trValues[5];
//		
//		double norm = 1 / ( trValues[0] * trValues[3] - trValues[1] * trValues[2] );
//
//		// INVERSE MATRIX
//		//
//		//	1 / ( m1 * m4 - m2 * m3 )
//		//
//		//	*	|	 m4		-m2		|
//		//		|	-m3	 	 m1 	|
//		
//		// norm * (   m4*x - m2*y )
//		res[0] = norm * (   trValues[3] * tx - trValues[1] * ty );
//		// norm * ( - m3*x + m1*y )
//		res[1] = norm * ( - trValues[2] * tx + trValues[0] * ty );
//		
//		
//		return res;
//	}

	
	public final static void getMapped(float[] result, float[] xy, float scale, float ori) {

		// scale
		float tx = xy[0]*scale;
		float ty = xy[1]*scale;
		
		// rotation matrix
		/*
		 * |	cos		-sin	| 		|	x	|
		 * |	sin		cos		|	*	|	y	|
		 */
		
                // double[] resXY = new double[2];
		//x =
                result[0] = (float) ( tx*Math.cos(ori) - ty*Math.sin(ori) );
                result[1] = (float) ( tx*Math.sin(ori) + ty*Math.cos(ori) );
	}
	
	public final static void getInverseMapped(float[] result, float[] xy, float scale, float ori) {

		getMapped(result, xy, 1.0f/scale, -ori);
	}
	
	public String toString() {
		String tStr = "";
		tStr += "|\t" + values[0] + "\t"+values[2] + "\t|\n";
		tStr += "|\t" + values[1] + "\t"+values[3] + "\t|\n";
		tStr += values[4] + "\n";
		tStr += values[5] + "\n";
		
		double ori = -getTransformedOrientation(0.0);
		tStr += "ori: " + ori + "(" + ( (180 / Math.PI) * ori ) + "�)\n";
		tStr += "scale: " + getScale() + "\n";
		float[] tVec = { 100.0f, 100.0f };
		float[] trCoor = getTransformed( tVec );
		tStr += "["+tVec[0] +"," + tVec[1] +"] -> [" + trCoor[0] + ", " + trCoor[1] + "]\n";
		
		return tStr;
	}
	
	/**
         * LEAST SQUARE
	 * Search for a transformation mapping imgP_xy to imgP_uv
	 * @param puv		orig
	 * @param pxy		transofrmed
	 * @return
	 */
	public static final AffineTransformation getTransformation( float[][] pxy, float[][] puv ) {
		
		double[] values = new double[6];
			
		// Matrix A
		double[][] tempM = new double[2*pxy.length][6];
		for ( int i=0; i<pxy.length; i++) {
			
			int r = i*2;			
			tempM[r][0] = pxy[i][0];
                        tempM[r][1] = 0;
			tempM[r][2] = pxy[i][1];
                        tempM[r][3] = 0;
                        tempM[r][4] = 1;
			tempM[r][5] = 0;
			
			r++;
			tempM[r][0] = 0;
                        tempM[r][1] = pxy[i][0];
			tempM[r][2] = 0;
			tempM[r][3] = pxy[i][1];
                        tempM[r][4] = 0;
			tempM[r][5] = 1;			      
		}
		Jama.Matrix A = new Jama.Matrix(tempM);
		
		// Vector b
		double[] tempB = new double[2*puv.length];
		for ( int i=0; i<puv.length; i++) {
			int r = i*2;
			tempB[r] = puv[i][0];
			tempB[r+1] = puv[i][1];
		}
		Jama.Matrix b = new Jama.Matrix(tempB,tempB.length);
		
		Jama.Matrix x = null;
		try {
                    x = A.solve(b);
		} catch (Exception e) {
                    //e.printStackTrace();
                    return null;
		}
		
//		Jama.Matrix Residual = A.times(x).minus(b);
//		double rnorm = Residual.normInf();
		
		double[][] arr = x.transpose().getArray();
		
		return new AffineTransformation(arr[0]);
	}  



        public BufferedImage overPrint(BufferedImage bck, BufferedImage over, java.awt.geom.AffineTransform jTr  ) {
            //BufferedImage outImg = new BufferedImage( bck.getWidth(), bck.getHeight(), BufferedImage.TYPE_3BYTE_BGR );

            Graphics2D graphics2D = bck.createGraphics();

            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics2D.drawImage(bck, 0, 0, null);

            graphics2D.drawImage(over, jTr, null);
            graphics2D.dispose();

            return bck;
        }
        
	public 	BufferedImage overPrint_inverted(BufferedImage src, BufferedImage model) {

            return overPrint(model, src, new java.awt.geom.AffineTransform(invValues) );

//		BufferedImage outImg	= new BufferedImage( model.getWidth(), model.getHeight(), BufferedImage.TYPE_3BYTE_BGR );

//		double[] xy = new double[]();
//		for ( int x=0; x<model.getWidth(); x++ ) {
//			for ( int y=0; y<model.getHeight(); y++ ) {
//				xy.setLocation(x, y);
//				double[] invTrXY = getTransformedCoordinates( xy );
//				int trX = (int) Math.round( invTrXY[0] );
//				int trY = (int) Math.round( invTrXY[1] );
//				if ( trX >  0 && trY > 0 &&
//					 trX < src.getWidth() &&
//					 trY < src.getHeight() ) {
//					outImg.setRGB( x, y, src.getRGB(trX, trY));
//				} else {
//					outImg.setRGB( x, y, model.getRGB(x, y));
//				}
//			}
//		}
//		return outImg;
	}



//        public BufferedImage overPrint_cropped( BufferedImage src, BufferedImage model, LocalFeaturesMatches matches ){
//            double[] mB = matches.getMatchingBox();
//            BufferedImage modelCrop = model.getSubimage( (int) mB[0], (int) mB[1], (int) (mB[2]-mB[0]), (int) (mB[3]-mB[1]) );
//
//            double[] t = this.getTransformed( mB );
//
//            return overPrint(src, modelCrop, new java.awt.geom.AffineTransform(getTranslatedValues( t )));
//        }


        public 	BufferedImage overPrint(BufferedImage bck, BufferedImage over, float[][] box ) {

            if ( box != null ) {
                return overPrint(bck, over, new java.awt.geom.AffineTransform(copyChangingTranslation( this.getTransformed( box[0] ) ).values) );
            }
            return overPrint(bck, over, new java.awt.geom.AffineTransform(values) );

//
//		double[] xy = new double[2];
//		for ( int x=0; x<src.getWidth(); x++ ) {
//			for ( int y=0; y<src.getHeight(); y++ ) {
//				xy[0] = x;
//				xy[1] = y;
//				int[] invTrXY = getInverseTransformedCoordinates_int( xy );
////				int invtrX = (int) Math.round( invTrXY[0] );
////				int invtrY = (int) Math.round( invTrXY[1] );
//				if ( invTrXY[0] >  0 && invTrXY[0] < model.getWidth() &&
//					 invTrXY[1] > 0 && invTrXY[1] < model.getHeight()
//					 ) {
//					outImg.setRGB( x, y, model.getRGB(invTrXY[0], invTrXY[1]));
//				} else {
//					outImg.setRGB( x, y, src.getRGB(x, y));
//				}
//			}
//		}
//		return outImg;
	}

    public int getNPointsForEstimation() {
        return 3;
    }




    public AffineTransformation copyChangingTranslation(float[] trasl) {
        double[] res = new double[6];

        for ( int i=0; i<4; i++) {
            res[i] = values[i];
        }
        res[4] = trasl[0];
        res[5] = trasl[1];
        return new AffineTransformation(res);
    }

    public double getDeterminant() {
        return values[0]*values[3]-values[2]*values[1];
    }

    @Override
    public AbstractTransformation getDeNormalized(ALocalFeaturesGroup gr1, ALocalFeaturesGroup gr2) {
        double[] newValues = new double[6];

        float  scale1 = gr1.getNormScale();
        float[] mean1 = gr1.getMeanXY();
        float  scale2 = gr2.getNormScale();
        float[] mean2 = gr2.getMeanXY();

        /*
         *  |   1/s     0       -tx/s     |   | v0  v2  v4 |   |   s     0     tx     |
         *  |   0       1/s     -ty/s     | * | v1  v3  v5 | * |   0     s     ty     |
         *  |   0       0       1         |   |  0   0   1 |   |   0     0     1      |
         */

        newValues[0] = values[0] / scale1;
        newValues[1] = values[1] / scale1;
        newValues[2] = values[2] / scale1;
        newValues[3] = values[3] / scale1;

        newValues[4] = values[4] / scale1 + mean1[0];
        newValues[5] = values[5] / scale1 + mean1[1];

        newValues[0] = scale2 * newValues[0];
        newValues[1] = scale2 * newValues[1];
        newValues[2] = scale2 * newValues[2];
        newValues[3] = scale2 * newValues[3];
        
        newValues[4] = - mean2[0] * newValues[0] - mean2[1] * newValues[2] + newValues[4]  ;
        newValues[5] = - mean2[0] * newValues[1] - mean2[1] * newValues[3] + newValues[5]  ;
        
//
//        for ( int i=0; i<4; i++) {
//            newValues[i] *= scale2;
//        }
//        newValues[4] = - values[0] * mean2[0]  - values[2] * mean2[1] + values[4]  ;
//        newValues[5] = - values[1] * mean2[0]  - values[3] * mean2[1] + values[5]  ;
//
//        for ( int i=0; i<4; i++) {
//            newValues[i] /= scale1;
//        }
//        newValues[4] += mean1[0];
//        newValues[5] += mean1[1];

        return new AffineTransformation(newValues);
    }

//    public double[] getTranslatedValues(double[] xy) {
//        double[] res = new double[6];
//        for ( int i=0; i<6; i++) {
//            res[i] = values[i];
//        }
//        res[4] = xy[0];
//        res[5] = xy[1];
//        return res;
//     }
}

