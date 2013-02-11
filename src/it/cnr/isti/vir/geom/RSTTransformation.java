/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;
import java.awt.image.BufferedImage;

/**
 *
 * @author Fabrizio
 */
public class RSTTransformation extends HomographyTransformation {

    public static double[] getScaleAndRot(float[][] pxy, float[][] puv) {
        return getScaleAndRot(pxy[0], pxy[1], puv[0], puv[1]);
    }
    public static double[] getScaleAndRot(float[] pxy0, float[] pxy1, float[] puv0, float[] puv1) {

    
        double vx = pxy0[0]-pxy1[0];
        double vy = pxy0[1]-pxy1[1];

        double vu = puv0[0]-puv1[0];
        double vv = puv0[1]-puv1[1];

        double sqrvxy = (vx*vx+vy*vy);
        double sqrvuv = (vu*vu+vv*vv);

        double anglexy = Math.asin ( vy / Math.sqrt(sqrvxy) );
        double angleuv = Math.asin ( vv / Math.sqrt(sqrvuv) );

        return new double[]{ Math.sqrt( sqrvuv / sqrvxy  ), angleuv-anglexy };
    }

    public static HomographyTransformation getTransformation(float[][] pxy, float[][] puv) {

        double[] scalRot = getScaleAndRot(pxy, puv);

        double[] res = new double[9];

        res[0] =  res[4] = scalRot[0]*Math.cos(scalRot[1]);
        res[1] = -( res[3] = scalRot[0]*Math.sin(scalRot[1]) );

        res[2] = puv[0][0] - res[0] * pxy[0][0] - res[1] * pxy[0][1];
        res[5] = puv[0][1] - res[3] * pxy[0][0] - res[4] * pxy[0][1];

        res[8] = 1.0;
        return new HomographyTransformation(res);
    }
   
    private static final double[] getMatrix(double scale, double rot, double[] tr ) {
        double[] res = new double[9];
        res[0] =  res[4] = scale*Math.cos(rot);
        res[1] = -( res[3] = scale*Math.sin(rot) );
        res[2] = tr[0];
        res[5] = tr[1];
        res[6] = res[7] = 0.0;
        res[8] = 1.0;
        return res;
    }

    public RSTTransformation(double scale, double rot, double[] tr ) {
        super(getMatrix(scale, rot, tr));
    }


}
