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
package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;

public class SAPIRMetric implements Metric<IFeaturesCollector> {
	
	private static long distCount = 0;
	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(
			ColorLayout.class,
			ColorStructure.class,
			ScalableColor.class,
			EdgeHistogram.class,
			HomogeneousTexture.class );
	
	protected static final int htOption = HomogeneousTexture.N_OPTION;
	
	public final long getDistCount() {
		return distCount;
	}
	
	public static final double wSAPIR_Norm = 1.0 / ( 1.5+2.5+4.5+0.5+2.5 ); 

	public static final double[] wSAPIR = {
		1.5  * 1.0/300.0,  //CL
		2.5  * 1.0/10200.0, //CS
		4.5  * 1.0/68.0, //EH
		0.5  * 1.0/25.0,  //HT
		2.5  * 1.0/3000.0  //SC
	};
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
	
		return distance(f1,f2, Double.MAX_VALUE);
	}
	
	
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		distCount++;
		double dist = 0;

		//EH
		dist += wSAPIR[2] * EdgeHistogram.mpeg7XMDistance( (EdgeHistogram) f1.getFeature(EdgeHistogram.class), (EdgeHistogram) f2.getFeature(EdgeHistogram.class) );
		if ( dist > max ) return -dist;
		
		//CS
		dist += wSAPIR[1] * ColorStructure.mpeg7XMDistance( (ColorStructure) f1.getFeature(ColorStructure.class), (ColorStructure) f2.getFeature(ColorStructure.class) );
		if ( dist > max ) return -dist;		
		
		//SC
		dist += wSAPIR[4] * ScalableColor.mpeg7XMDistance( (ScalableColor) f1.getFeature(ScalableColor.class), (ScalableColor) f2.getFeature(ScalableColor.class) );
		if ( dist > max ) return -dist;	
		
		//CL
		dist += wSAPIR[0] * ColorLayout.mpeg7XMDistance( (ColorLayout) f1.getFeature(ColorLayout.class), (ColorLayout) f2.getFeature(ColorLayout.class) );
		if ( dist > max ) return -dist;	
		
		//HT
		dist += wSAPIR[3] * HomogeneousTexture.mpeg7XMDistance( (HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class), htOption );
				
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
}
