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
package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;

import java.util.Collection;
import java.util.Properties;

public class RootSIFTMetric implements IMetric<RootSIFT>, ILocalFeaturesMetric<RootSIFT>, IMeanEvaluator<RootSIFT> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(RootSIFT.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public RootSIFTMetric(Properties properties) {
		
	}
	
	public RootSIFTMetric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public final Class getRequestedFeatureClass() {
		return RootSIFT.class;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class), max);
	}
	
	@Override
	public final double distance(RootSIFT f1, RootSIFT f2) {
		return RootSIFT.getL2SQDistance_Norm( f1, f2 );	
	}
	
	@Override
	public final double distance(RootSIFT f1, RootSIFT f2, double max) {
		distCount++;
		return RootSIFT.getL2SQDistance_Norm( f1, f2, max);
	}
//
//	@Override
//	public final double distance(IFeature f1, IFeature f2) {
//		double dist = RootSIFT.getDistance_Norm(((RootSIFT) f1.getFeature(RootSIFT.class)), ((RootSIFT) f2.getFeature(RootSIFT.class)));		
//		distCount++;
//		
//		return dist;
//	}
//	
//	@Override
//	public final double distance(IFeature f1, IFeature f2, double max) {
//		double dist = RootSIFT.getDistance_Norm((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class), max);		
//		distCount++;
//		
//		return dist;
//	}
	
	@Override
	public RootSIFT getMean(Collection<RootSIFT> coll) {
		return RootSIFT.getMean(coll);
	}
	
	@Override
	public final Class getRequestedFeatureGroupClass() {
		return RootSIFTGroup.class;
	}
	
}
