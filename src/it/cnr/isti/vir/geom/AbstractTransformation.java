/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import java.awt.image.BufferedImage;

/**
 *
 * @author Fabrizio
 */
public abstract class AbstractTransformation {

    public abstract AbstractTransformation getInverse();

    public abstract AbstractTransformation getDeNormalized(ALocalFeaturesGroup gr1, ALocalFeaturesGroup gr2);
    
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
