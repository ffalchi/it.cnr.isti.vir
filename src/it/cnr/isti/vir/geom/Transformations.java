/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

/**
 *
 * @author Fabrizio
 */
public class Transformations {

    

    public static final Integer getNPointsForEstimation(Class trClass) {
        
        if ( trClass.equals(RSTTransformation.class) ) return 2;
        if ( trClass.equals(AffineTransformation.class) ) return 3;
        if ( trClass.equals(HomographyTransformation.class) ) return 4;
        return null;
    }

    public static final AbstractTransformation getTransformation(Class trClass, float[][] pSrc, float[][] pDest) {
        if ( trClass.equals(AffineTransformation.class) ) {
            return AffineTransformation.getTransformation(pSrc, pDest);
        }
        if ( trClass.equals(HomographyTransformation.class) ) {
            return HomographyTransformation.getTransformation(pSrc, pDest);
        }
        if ( trClass.equals(RSTTransformation.class) ) {
            return RSTTransformation.getTransformation(pSrc, pDest);
        }
        return null;
    }
}
