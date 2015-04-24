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

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.ORBGroup;

import java.util.Properties;

public class ORBGroupSimilarity_RANSAC extends AbstractRANSAC<ORBGroup> {

	protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ORBGroup.class);
	
	@Override
	public Class getRequestedGroup()  { return ORBGroup.class; } 
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public ORBGroupSimilarity_RANSAC() {
		super();
	}
	
	public ORBGroupSimilarity_RANSAC( Properties properties ) throws SimilarityOptionException {
		super(properties);
	}

	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}

	@Override
	public final LocalFeaturesMatches getMatches( ORBGroup g1,  ORBGroup g2) {
		
		if ( loweThr >= 1.0 ) 
			return HammingNNMatcher.getMatches(g1, g2, maxFDist_int);
		else if ( maxFDist_int == Integer.MAX_VALUE )
			return ORBGroup.getLoweMatches( g1, g2, loweThr );
		
		return ORBGroup.getLoweMatches( g1, g2, loweThr, maxFDist_int );	
	}

}

