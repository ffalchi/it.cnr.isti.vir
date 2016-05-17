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
import it.cnr.isti.vir.util.PropertiesUtils;

import java.util.Properties;

public class SpearmanRho implements ISimilarity<Permutation>{

	private int l = Integer.MAX_VALUE;
	
	public SpearmanRho(Properties prop) {
		l = PropertiesUtils.getInt_orDefault(prop, "SpearmanRho.l", l);
	}
	
	public SpearmanRho() {};
	
	public int getMaxDist() {
		int res=0;
		for ( int i=1; i<=l; i++) {
			res+=i*i;
		}
		return res*2;
	}
	
	public SpearmanRho(int maxLength) {
		if ( maxLength >= 0)
			this.l =maxLength;
	}
	
	public void setMaxLength(int maxLength) {
		this.l =maxLength;
		if ( this.l < 0) this.l = 0;
	}
	
	@Override
	public final double distance(Permutation f1, Permutation f2) {
		int maxPos = l;
//		int maxPos = Math.max(f1.getOrdLength(), f2.getOrdLength());
//		if ( l!= 0 ) maxPos = Math.min(maxPos, l);
			
		return sfd_l(f1.getROPositions(), f2.getROPositions(), maxPos );	
	}

	@Override
	public final double distance(Permutation f1, Permutation f2, double max) {
		

		int maxPos = l;
//		int maxPos = Math.max(f1.getOrdLength(), f2.getOrdLength());
//		if ( l!= 0 ) maxPos = Math.min(maxPos, l);
			
		return sfd_l(f1.getROPositions(), f2.getROPositions(), maxPos, (long) Math.ceil( max ));
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
			return distance(f1.getFeature(Permutation.class), f2.getFeature(Permutation.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance(f1.getFeature(Permutation.class), f2.getFeature(Permutation.class), max);
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

//	public static long sfd(int[] pos1, int[] pos2) {
//		long sum = 0;
//		for ( int i=0; i<pos1.length; i++) {
//			long temp = pos1[i] - pos2[i];
//			sum += temp*temp;
//		}
//		return sum;
//	}
//	
//	public static long sfd(int[] pos1, int[] pos2, long max) {
//		long sum = 0;
//		for ( int i=0; i<pos1.length; i++) {
//			long temp = pos1[i] - pos2[i];
//			sum += temp*temp;
//			if ( sum > max ) return -sum;
//		}
//		return sum;
//	}
	
	/**
	 * @param obj1
	 * @param obj2
	 * @param permLength max permutation length
	 * @return
	 */
	public static final double sfd_l(int[] pos1, int[] pos2, int maxPos ) {
		
		return sfd_l(pos1, pos2, maxPos, Long.MAX_VALUE);
	}

	/**
	 * @param obj1
	 * @param obj2
	 * @param max
	 * @param permLength max permutation length
	 * @return
	 */
	public static final double  sfd_l(int[] pos1, int[] pos2, long maxPos, long max ) {
		
		long sum = 0;
		for ( int i=0; i<pos1.length; i++) {
			long temp =
					Math.min( pos1[i], maxPos ) -
					Math.min( pos2[i], maxPos );

			sum += temp*temp;
			if ( sum > max ) return -sum;
		}
		return sum;
	}
	
	public String getStatsString() { return ""; };

}
