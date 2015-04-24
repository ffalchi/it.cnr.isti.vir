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
import it.cnr.isti.vir.features.localfeatures.SURF;
import it.cnr.isti.vir.features.localfeatures.SURFGroup;

import java.util.Collection;
import java.util.Properties;

public class SURFMetric implements IMetric<SURF>, ILocalFeaturesMetric<SURF>, IMeanEvaluator<SURF> {

	private static long distCount = 0;
	private static final Class reqFeature = SURF.class;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(reqFeature);
	

	public SURFMetric(Properties properties) {
		
	}
	
	public SURFMetric() {
		
	}
	
	public final long getDistCount() {
		return distCount;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public Class getRequestedFeatureClass() {
		return SURF.class;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((SURF) f1.getFeature(SURF.class), (SURF) f2.getFeature(SURF.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((SURF) f1.getFeature(SURF.class), (SURF) f2.getFeature(SURF.class));
	}
	
	@Override
	public final double distance(SURF f1, SURF f2) {
		return Math.sqrt( SURF.getSquaredDistance_laplace( f1, f2)  / SURF.maxSQRDistValue );
	}

	@Override
	public final double distance(SURF f1, SURF f2, double max) {
		double sqrMax = max*max;
		return Math.sqrt( SURF.getSquaredDistance_laplace( f1, f2, (sqrMax*SURF.maxSQRDistValue) )  / SURF.maxSQRDistValue  );
	}
	

	@Override
	public SURF getMean(Collection<SURF> coll) {
		return SURF.getMean_laplace(coll);
	}

	@Override
	public final Class getRequestedFeatureGroupClass() {
		return SURFGroup.class;
	}

//	@Override
//	public final double distance(IFeatureCollector if1, IFeatureCollector if2, double max) {
//		distCount++;
//		SURF f1 = null;
//		SURF f2 = null;
//		if ( if1 instanceof SURF) f1 = (SURF) if1;
//		else f1 = (SURF) if1.getFeature(fClass);
//		if ( if2 instanceof SURF) f2 = (SURF) if2;
//		else f2 = (SURF) if2.getFeature(fClass);		
//
//		// not using max
//		return Math.sqrt( SURF.getSquaredDistance( f1, f2 ) / SURF_maxSQRDistValue );	
//	}
//	
//	public final double distance(IFeatureCollector if1, IFeatureCollector if2 ) {
//		distCount++;
//		SURF f1 = null;
//		SURF f2 = null;
//		if ( if1 instanceof SURF) f1 = (SURF) if1;
//		else f1 = (SURF) if1.getFeature(fClass);
//		if ( if2 instanceof SURF) f2 = (SURF) if2;
//		else f2 = (SURF) if2.getFeature(fClass);		
//		
//		return Math.sqrt( SURF.getSquaredDistance( f1, f2)  / SURF_maxSQRDistValue );	
//	}
	 

//	@Override
//	public final double distance(SURF s1, SURF s2) {
//		if ( s1.laplace != s2.laplace ) return SURF.maxSQDist;
//		
//		double dist = 0;
//	    double dif = 0;
//	    
//	    for (int i = 0; i < SURF.ivecLength; i++) {
//	    	dif = s1.ivec[i] - s2.ivec[i];
//	    	dist += dif * dif;
//	    }
//		
//		return Math.sqrt( dist  / SURF.maxSQRDistValue );
//	}
//
//	@Override
//	public final double distance(SURF s1, SURF s2, double max) {
//		if ( s1.laplace != s2.laplace ) return SURF.maxSQDist;
//		
//		double dist = 0;
//	    double dif = 0;
//	    
//	    for (int i = 0; i < SURF.ivecLength; i++) {
//	    	dif = s1.ivec[i] - s2.ivec[i];
//	    	dist += dif * dif;
//	    }
//		
//		return Math.sqrt( dist  / SURF.maxSQRDistValue );
//	}

//	public final void offer(Collection<SURF> coll, KNNPQueue<SURF>[] kNN) {
//		
//		for ( Iterator<SURF> it = coll.iterator(); it.hasNext(); ) {
//			SURF curr = it.next();
//			for ( int i=0; i<kNN.length; i++ ) {
//				double distance = distance(curr, kNN[i].query);
//				if ( distance < kNN[i].excDistance ) kNN[i].offer(curr, distance);
//			}
//		}
//	}

	public String getStatsString() { return ""; };
}
