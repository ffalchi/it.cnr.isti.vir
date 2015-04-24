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
import it.cnr.isti.vir.features.Permutation;

public class SpearmanFootruleLog implements ISimilarity<Permutation>{

	private int maxLength = 0;
	
	private static double[] log;
	public SpearmanFootruleLog() {};
	
	static {
		// position 0 must be considered 1
		log = new double[10000];
		for ( int i=0; i<log.length; i++) {
			log[i] = Math.log((double) (i + 1));
		}
	}
	
	public SpearmanFootruleLog(int maxLength) {
		if ( maxLength >= 0)
			this.maxLength =maxLength;
	}
	
	
	
	public static double distance(int[] roPosition1, int[] roPosition2 ) {
		double sum = 0;
		for ( int i=0; i<roPosition1.length; i++) {
			sum += Math.abs(log[roPosition1[i]] - log[roPosition2[i]]);
		}
		return sum;
	}
	
	public static double distance(int[] roPosition1, int[] roPosition2, int max) {
		double sum = 0;
		for ( int i=0; i<roPosition1.length; i++) {
			sum += Math.abs(log[roPosition1[i]] - log[roPosition2[i]]);
			if ( sum > max ) return -sum;
		}
		return sum;
	}
	
	/**
	 * @param obj1
	 * @param obj2
	 * @param permLength max permutation length
	 * @return
	 */
	public static double distance_l(int[] pos1, int[] pos2, int permLength) {
	
		double sum = 0;
		for ( int i=0; i<pos1.length; i++) {
			int t1 = pos1[i];
			int t2 = pos2[i];
			if ( t1 >= permLength &&  t2 >= permLength) continue;
			if ( t1 >= permLength ) t1 = permLength;
			if ( t2 >= permLength ) t2 = permLength;
			sum += Math.abs( log[t1] - log[t2] );
		}
		return sum;
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param max
	 * @param permLength max permutation length
	 * @return
	 */
	public static double  distance_l(int[] pos1, int[] pos2, int max, int permLength) {
		
		double sum = 0;
		for ( int i=0; i<pos1.length; i++) {
			int t1 = pos1[i];
			int t2 = pos2[i];
			if ( t1 >= permLength &&  t2 >= permLength) continue;
			if ( t1 >= permLength ) t1 = permLength;
			if ( t2 >= permLength ) t2 = permLength;
			sum += Math.abs( log[t1] - log[t2] );
			if ( sum > max ) return -sum;
		}
		return sum;
	}

	
	@Override
	public double distance(Permutation f1, Permutation f2) {
		if ( maxLength == 0 ) return this.distance(f1.getROPositions(), f2.getROPositions());
		return distance_l(f1.getROPositions(), f2.getROPositions(), maxLength);
	}

	@Override
	public double distance(Permutation f1, Permutation f2, double max) {
		if ( maxLength == 0 ) return this.distance(f1.getROPositions(), f2.getROPositions());
		return distance_l(f1.getROPositions(), f2.getROPositions(), (int) Math.ceil( max ), maxLength);
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((Permutation) f1.getFeature(Permutation.class), (Permutation) f2.getFeature(Permutation.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((Permutation) f1.getFeature(Permutation.class), (Permutation) f2.getFeature(Permutation.class));
	}

	@Override
	public long getDistCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		// TODO Auto-generated method stub
		return new FeatureClassCollector( Permutation.class );
	}

	public String getStatsString() { return ""; };

}
