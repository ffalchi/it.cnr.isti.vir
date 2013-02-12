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
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;

public class HomogeneousTextureMetric implements Metric<HomogeneousTexture> {
	
	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(HomogeneousTexture.class);
	
	public final long getDistCount() {
		return distCount;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}	
	
	// option can be "n", "r", "s", "rs", "sr"
	public final int option = HomogeneousTexture.N_OPTION;;
	
	public static final double norm = 1.0/25.0;  //HT
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class), max);
	}
	
	@Override
	public final double distance(HomogeneousTexture f1, HomogeneousTexture f2, double max) {
		return distance(f1, f2);
	}
	
	public final double distance(HomogeneousTexture f1, HomogeneousTexture f2, int option ) {
		distCount++;
		return norm * HomogeneousTexture.mpeg7XMDistance( f1, f2, option);
	}
	
	public final double distance(HomogeneousTexture n1, HomogeneousTexture n2 ) {
		distCount++;
		return norm * HomogeneousTexture.mpeg7XMDistance( n1, n2, option );
	}	
	
	public String toString() {
		return this.getClass() + " option: " + option;
	}
	
}
