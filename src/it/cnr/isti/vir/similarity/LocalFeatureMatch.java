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
package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.features.localfeatures.ILocalFeature;
import it.cnr.isti.vir.geom.AbstractTransformation;

public class LocalFeatureMatch {
	public final ILocalFeature lf;
	public final ILocalFeature lfMatching;
	public final float[] xy;
	public final float[] matchingxy;
	public final float[] nxy;
	public final float[] matchingnxy;
	private final double weight;
	private final int index;
	
	public final double getWeight() {
		return weight;
	}
	
	public final int getIndex() {
		return index;
	}
	
	public final float[][] getMaxBox() {
		return lfMatching.getLinkedGroup().getBox();
	}

	public final float[] getMaxBoxWidthHeight() {
		return lf.getLinkedGroup().getBoxWidthHeight();
	}

	public final float[] getMatchingMaxBoxWidthHeight() {
		return lfMatching.getLinkedGroup().getBoxWidthHeight();
	}

	public final float getMatchingOri() {
		return lfMatching.getOrientation();
	}

	public final float getPointOri() {
		return lf.getOrientation();
	}

	public final float[] getXY() {
		return xy;
	}

	public final float[] getMatchingXY() {
		return matchingxy;
	}

        public final float[] getNormXY() {
		return nxy;
	}

	public final float[] getMatchingNormXY() {
		return matchingnxy;
	}

	public final void getMatchingXY_mapped(float[] result, float scale, float ori) {
		AffineTransformation.getMapped( result, matchingxy, scale, ori);
	}
	
	public final void getMatchingNXY_mapped(float[] result, float scale, float ori) {
		AffineTransformation.getMapped( result, matchingnxy, scale, ori);
	}
	
	// inverse mapping
	public final void getXY_mapped(float[] result, float scale, float ori) {
		AffineTransformation.getInverseMapped( result, xy, scale, ori);
	}
	
	public LocalFeatureMatch(ILocalFeature lf, ILocalFeature matchingLF, double weight, int index ) {		
		super();
		this.lf = lf;
		this.lfMatching = matchingLF;
                xy = lf.getXY();
                matchingxy = lfMatching.getXY();
                nxy = lf.getNormXY();
                matchingnxy = lfMatching.getNormXY();
		this.weight = weight;
		this.index = index;
	}
	
	public LocalFeatureMatch(ILocalFeature lf, ILocalFeature matchingLF) {
		this(lf, matchingLF, 1.0, -1);
	}

	public float getScaleRatio() {
		return lf.getScale() / lfMatching.getScale();
	}

        public float getNormScaleRatio() {
		return lf.getNormScale() / lfMatching.getNormScale();
	}
	
	public float getOrientationDiff() {
		float diff =  lfMatching.getOrientation() - lf.getOrientation();
		if ( diff < -Math.PI ) diff +=   2.0 * Math.PI;
		if ( diff >  Math.PI ) diff += - 2.0 * Math.PI;
//		System.out.println(diff);
		return diff;
	}
	
	/* NOTE THAT:
	 * 	Lowe considers for x and y the:
	 *  "projected training image dimension
	 *   using the predicted scale"
	 */	
//	public final void getMappedXYDiff(Transformation t, double[] res ) {
//		// transform queryLF in trainingSpace
//		getXYDiff( t.getTransformed(matchingxy), xy, res );
//
//		// normalizing considering source (query) max box
//		double[] maxBox = getMaxBoxWidthHeight();
//
//		res[0] /= maxBox[0];
//                res[1] /= maxBox[1];
//
//	}

	public String toString() {
		String tStr = "";
		tStr += xy[0] + " " + xy[1] + ", ";
		tStr += matchingxy[0] + " " + matchingxy[1] + " ";
		return tStr;
	}

	
	public static void getXYDiff(float[] xy1, float[] xy2, float[] res) {
		res[0] = xy1[0]-xy2[0];
        res[1] = xy1[1]-xy2[1];
	}
	
//	public final void getXYDiff(float[] res) {
//		getXYDiff(xy, matchingxy, res );
//	}

}
