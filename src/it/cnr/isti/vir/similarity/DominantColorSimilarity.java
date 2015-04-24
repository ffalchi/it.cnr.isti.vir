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
package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.DominantColor;

public class DominantColorSimilarity   implements ISimilarity<DominantColor> {
	
	private static long distCount = 0;
	private static final Class reqFeature = DominantColor.class;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(reqFeature);
	
	public final long getDistCount() {
		return distCount;
	}

	public static final double norm = 1.0; ///10200.0; //DC
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((DominantColor) f1.getFeature(DominantColor.class), (DominantColor) f2.getFeature(DominantColor.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((DominantColor) f1.getFeature(DominantColor.class), (DominantColor) f2.getFeature(DominantColor.class), max);
	}
	
	public final double distance(DominantColor d1, DominantColor d2, double max ) {
		return distance(d1, d2);
	}
	
	public final double distance(DominantColor d1, DominantColor d2 ) {
		double dist = 0;

		dist += norm * DominantColor.mpeg7XMDistance( d1, d2 );
		distCount++;
		
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}

	public String getStatsString() { return ""; };
}
