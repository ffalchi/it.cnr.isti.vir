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
import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.FloatsL2Norm_Bytes;
import it.cnr.isti.vir.util.math.Mean;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class FloatsL2NormBytes_L2Metric  implements IMetric<FloatsL2Norm_Bytes>, IMeanEvaluator<FloatsL2Norm_Bytes> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(FloatsL2Norm_Bytes.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public FloatsL2NormBytes_L2Metric(Properties properties) {
		
	}
	
	public FloatsL2NormBytes_L2Metric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance( f1.getFeature(FloatsL2Norm_Bytes.class), f2.getFeature(FloatsL2Norm_Bytes.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance( f1.getFeature(FloatsL2Norm_Bytes.class), f2.getFeature(FloatsL2Norm_Bytes.class), max);
	}
	
	@Override
	public final double distance(FloatsL2Norm_Bytes f1, FloatsL2Norm_Bytes f2) {
		return L2.get(f1.getValues(), f2.getValues()) / 255.0 ;	
	}
	
	@Override
	public final double distance(FloatsL2Norm_Bytes f1, FloatsL2Norm_Bytes f2, double max) {
		return L2.get(f1.getValues(), f2.getValues(), max*255.0 ) / 255.0;
	}

	@Override
	public FloatsL2Norm_Bytes getMean(Collection<FloatsL2Norm_Bytes> coll) {
		return FloatsL2Norm_Bytes.getMean(coll);
	}
	
	public String getStatsString() { return ""; };

	
}
