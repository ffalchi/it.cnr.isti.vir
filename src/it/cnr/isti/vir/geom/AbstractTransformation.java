/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import java.awt.image.BufferedImage;

/**
 *
 * @author Fabrizio
 */
public abstract class AbstractTransformation {

    public abstract AbstractTransformation getInverse();

    public abstract AbstractTransformation getDeNormalized(AbstractLFGroup gr1, AbstractLFGroup gr2);
    
    public abstract void getTransformed(float[] xy, float[] uv);

    public abstract void getInvTransformed(float[] xy, float[] uv);

    public final float[] getTransformed(float[] xy ) {
    	float[] res = new float[2];
        getTransformed( xy, res);
        return res;
    }

    public final float[] getInvTransformed(float[] xy ) {
    	float[] res = new float[2];
        getInvTransformed( xy, res);
        return res;
    }

    public abstract double[] getInverseValues();

//    public abstract Transformation getTranslated(double[] trasl );

//    {
//            double[] res = new double[6];
//            for ( int i=0; i<6; i++) {
//                res[i] = values[i];
//            }
//            res[4] = xy[0];
//            res[5] = xy[1];
//            return res;
//    }

    public BufferedImage overPrint_cropped(BufferedImage src, BufferedImage model, LocalFeaturesMatches matches) {
    	float[][] mB = matches.getMatchingBox();
            BufferedImage modelCrop = model.getSubimage( (int) mB[0][0], (int) mB[0][1], (int) (mB[1][0]-mB[0][0]), (int) (mB[1][1]-mB[0][1]) );

            
            return overPrint(src, modelCrop, mB );
    }

    public BufferedImage overPrint(BufferedImage bck, BufferedImage over ) {
        return overPrint(bck, over, null);
    }
    public abstract BufferedImage overPrint(BufferedImage bck, BufferedImage over, float[][] box );

    public abstract double getDeterminant();
}
