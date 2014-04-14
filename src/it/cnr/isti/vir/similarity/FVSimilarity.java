/*******************************************************************************
 * Copyright (c) 2013, Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
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
import it.cnr.isti.vir.features.FV;
import java.util.Properties;
/**
 *
 * @author Lucia Vadicamo
 */
public class FVSimilarity implements ISimilarity<FV> {
	
	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(FV.class);
	
	public FVSimilarity() {
		
	}
	
	public FVSimilarity( Properties properties) throws SimilarityOptionException {
		this();
	}
	
	@Override
	public double distance(FV f1, FV f2) {
		return FV.getDistance(f1, f2);
	}

	@Override
	public double distance(FV f1, FV f2, double max) {
		return FV.getDistance(f1, f2, max);
	}

	@Override
	public double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2) {
		return distance((FV) f1.getFeature(FV.class), (FV) f2.getFeature(FV.class));
	}

	@Override
	public double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max) {
		return distance((FV) f1.getFeature(FV.class), (FV) f2.getFeature(FV.class), max);
	}

	@Override
	public long getDistCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}

}

