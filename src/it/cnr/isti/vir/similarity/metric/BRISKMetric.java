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
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.BRISK;
import it.cnr.isti.vir.features.localfeatures.BRISKGroup;

import java.util.Collection;
import java.util.Properties;

public class BRISKMetric implements IMetric<BRISK>, ILocalFeaturesMetric<BRISK>, IMeanEvaluator<BRISK> {

	private static long distCount;
	
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BRISK.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public BRISKMetric(Properties properties) {
		
	}
	
	public BRISKMetric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public final Class getRequestedFeatureClass() {
		return BRISK.class;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance(f1.getFeature(BRISK.class), f2.getFeature(BRISK.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance(f1.getFeature(BRISK.class), f2.getFeature(BRISK.class), max);
	}
	
	@Override
	public final double distance(BRISK f1, BRISK f2) {
		return BRISK.getDistance_Norm( f1, f2 );	
	}
	
	@Override
	public final double distance(BRISK f1, BRISK f2, double max) {
		distCount++;
		return BRISK.getDistance_Norm( f1, f2 ); // , max); !! TO DO
	}
	
	@Override
	public BRISK getMean(Collection<BRISK> coll) {
		return BRISK.getMean(coll);
	}
	
	@Override
	public final Class getRequestedFeatureGroupClass() {
		return BRISKGroup.class;
	}
	
}
