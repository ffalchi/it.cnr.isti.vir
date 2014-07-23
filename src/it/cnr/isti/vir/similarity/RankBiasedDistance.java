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
import it.cnr.isti.vir.features.Permutation;
import it.cnr.isti.vir.similarity.metric.IMetric;

/**
 * @author Fabrizio Falchi
 *
 * Implemented following
 * "A similarity measure for indefinte rankings"
 * William Webber, Alistair Moffat, Justin Zobel (Univ. of Melbourne, Australia)
 * ACM TOIS, Vol. 28, No. 4, Article 20, November 2010
 * 
 * RBD = 1 - RBO
 */
public class RankBiasedDistance implements ISimilarity<Permutation>, IMetric<Permutation> {

	private int maxLength = 0;

	private long dCount = 0;
	
	private double p = 0.999;

	public RankBiasedDistance() {};
	public RankBiasedDistance(double p) {
		this.p = p;
	};
	
	public RankBiasedDistance(int maxLength) {
		if ( maxLength >= 0)
			this.maxLength =maxLength;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;//righe da invertire?
		if ( maxLength < 0) maxLength = 0;//righe da invertire?
	}
	
	@Override
	public double distance(Permutation f1, Permutation f2) {
		dCount++;
		if ( maxLength == 0 ) return d(f1.getROPositions(), f2.getROPositions());
		return d_l(f1.getROPositions(), f2.getROPositions(), maxLength);
	}

	@Override
	public double distance(Permutation f1, Permutation f2, double max) {
		dCount++;
		if ( maxLength == 0 ) return d(f1.getROPositions(), f2.getROPositions(), (long) Math.ceil( max ));
		return d_l(f1.getROPositions(), f2.getROPositions(), (long) Math.ceil( max ), maxLength);
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((Permutation) f1.getFeature(Permutation.class), (Permutation) f2.getFeature(Permutation.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((Permutation) f1.getFeature(Permutation.class), (Permutation) f2.getFeature(Permutation.class), max);
	}
	
	public int getMaxDist() {
		return 1;
	}
	
	@Override
	public long getDistCount() {
		return dCount;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return new FeatureClassCollector( Permutation.class );
	}

	
	/**
	 * @param p
	 * @param intersectionIncr
	 * @return
	 */
	public static double rbo(double p, byte[] intersectionIncr) {
		
		double acc =0;
		int intersection = 0;
		// the weight of depth=1 will be 1.0
		double w = (1/p);
		for ( int d=0; d<intersectionIncr.length; d++ ) {
			intersection += intersectionIncr[d];
			w *= p;
			acc += w * ( intersection / (d+1) );
		}
		
		return (1-p)*acc;
	}
	
	/**
	 * @param pos1
	 * @param pos2
	 * @return
	 */
	public double d(int[] pos1, int[] pos2) {
		assert pos1.length == pos2.length;
		
		int idMax = pos1.length;
		
		// store the increment of intersection at various depth
		// at each depth the increment can be 0,1 or 2
		byte[] intersectionIncr = new byte[idMax];
		
		for ( int i=0; i<idMax; i++ ) {
			// first position is reported as 0
			// the same for depth
			int depth = Math.max(pos1[i],pos2[i]);
			intersectionIncr[depth]++;
		}
		
		return 1.0 - rbo(p, intersectionIncr);
	}
	
	public double d(int[] pos1, int[] pos2, long max) {
		// TO DO !!!
		return d(pos1, pos2);
	}
	
	/**
	 * @param obj1
	 * @param obj2
	 * @param permLength max permutation length
	 * @return
	 */
	public double d_l(int[] pos1, int[] pos2, int permLength) {
	
		assert pos1.length == pos2.length;
		
		int idMax = pos1.length;
		
		// store the increment of intersection at various depth
		// at each depth the increment can be 0,1 or 2
		byte[] intersectionIncr = new byte[permLength];
		
		for ( int i=0; i<idMax; i++ ) {
			// first position is reported as 0
			// the same for depth
			if ( pos1[i] < permLength && pos2[i] < permLength )  {
				int depth = Math.max(pos1[i], pos2[i]);
				intersectionIncr[depth]++;
			}
		}
		
		return 1.0 - rbo(p, intersectionIncr);
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param max
	 * @param permLength max permutation length
	 * @return
	 */
	public double  d_l(int[] pos1, int[] pos2, long max, int permLength) {
		// TO DO !!!
		return d_l(pos1, pos2, permLength );
	}

}
