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
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.SAPIRFeature;
import it.cnr.isti.vir.util.math.Mean;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class SAPIRFeatureMetric implements IMetric<AbstractFeaturesCollector>, IMeanEvaluator<SAPIRFeature> {

	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(
			SAPIRFeature.class );
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public SAPIRFeatureMetric() {
		
	}
	
	public SAPIRFeatureMetric(Properties prop) {
		
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance(f1.getFeature(SAPIRFeature.class), f2.getFeature(SAPIRFeature.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance(f1.getFeature(SAPIRFeature.class), f2.getFeature(SAPIRFeature.class), max);
	}	

	
	public static final double distance(SAPIRFeature f1, SAPIRFeature f2 ) {
		return SAPIRFeature.mpeg7XMDistance((SAPIRFeature) f1, (SAPIRFeature) f2);
	}
	
	
	public static final double distance(SAPIRFeature f1, SAPIRFeature f2, double max ) {
		return SAPIRFeature.mpeg7XMDistance((SAPIRFeature) f1, (SAPIRFeature) f2, max);
	}
	
	public String toString() {
		return this.getClass().toString();
	}


	public SAPIRFeature getMean(Collection coll) {
		int size = coll.size();
		
		if ( coll.size() == 0 ) return null;
		
		float[][] v = new float[size][];
		int i=0;
		if ( coll.iterator().next() instanceof AbstractFeaturesCollector ) {
			for ( Iterator<AbstractFeaturesCollector> it = coll.iterator(); it.hasNext(); ) {
				SAPIRFeature curr = it.next().getFeature(SAPIRFeature.class);
				v[i++] = curr.l1Values;
			}
			float[] vMean = Mean.getMean(v);
			return new SAPIRFeature(vMean);
		} else {
			for ( Iterator it = coll.iterator(); it.hasNext(); ) {
				SAPIRFeature curr = (SAPIRFeature) it.next();
				v[i++] = curr.l1Values;
			}
			float[] vMean = Mean.getMean(v);
			return new SAPIRFeature(vMean);
		}
		
	}

	@Override
	public long getDistCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getStatsString() { return ""; };

}
