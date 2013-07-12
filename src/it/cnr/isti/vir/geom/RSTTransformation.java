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

        // return { scale, rotation }
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
