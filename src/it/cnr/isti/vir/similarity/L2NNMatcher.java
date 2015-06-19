package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.SIFT;

public class L2NNMatcher {

	
	// SIFT
	static final public SIFT get(SIFT q, ALocalFeaturesGroup<SIFT> g, int sqMaxDist) {
		SIFT res = null;
		int resDist = sqMaxDist;
		
		for ( SIFT curr : g.lfArr ) {
			int sqD = L2.getSquared(q.values, curr.values, resDist );
			if ( sqD >= 0 &&  sqD < resDist ) {
				res = curr;
				resDist = sqD;
			}
		}
		
		return res;
	}
	
	static final public LocalFeaturesMatches getSIFT(ALocalFeaturesGroup<SIFT> g1, ALocalFeaturesGroup<SIFT> g2, int sqMaxDist) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( g2.size() == 0 ) return null;
		int nMatches = 0;
		for ( SIFT curr : g1.lfArr ) {
			SIFT match = get(curr, g2, sqMaxDist );
			if ( match != null)
				matches.add( new LocalFeatureMatch( curr, match ) );
		}
		
		return matches;
	}

	// RootSIFT
	static final public RootSIFT get(RootSIFT q, ALocalFeaturesGroup<RootSIFT> g, int sqMaxDist) {
		RootSIFT res = null;
		int resDist = sqMaxDist;
		
		for ( RootSIFT curr : g.lfArr ) {
			int sqD = L2.getSquared(q.values, curr.values, sqMaxDist );
			if ( sqD >= 0 &&  sqD < resDist ) {
				res = curr;
				resDist = sqD;
			}
		}
		
		return res;
	}
	
	static final public LocalFeaturesMatches getMatchesRootSIFT(ALocalFeaturesGroup<RootSIFT> g1, ALocalFeaturesGroup<RootSIFT> g2, int sqMaxDist) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( g2.size() == 0 ) return null;
		int nMatches = 0;
		for ( RootSIFT curr : g1.lfArr ) {
			RootSIFT match = get(curr, g2, sqMaxDist );
			if ( match != null)
				matches.add( new LocalFeatureMatch( curr, match ) );
		}
		
		return matches;
	}
	
	
	// FloatsLF
	static final public FloatsLF get(FloatsLF q, ALocalFeaturesGroup<FloatsLF> g, double sqMaxDist) {
		FloatsLF res = null;
		double resDist = sqMaxDist;
		
		for ( FloatsLF curr : g.lfArr ) {
			double sqD = L2.getSquared(q.values, curr.values, sqMaxDist );
			if ( sqD >= 0 && sqD < resDist ) {
				res = curr;
				resDist = sqD;
			}
		}
		
		return res;
	}
	
	static final public LocalFeaturesMatches getMatchesFloatsLF(ALocalFeaturesGroup<FloatsLF> g1, ALocalFeaturesGroup<FloatsLF> g2, double sqMaxDist) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( g2.size() == 0 ) return null;
		int nMatches = 0;
		for ( FloatsLF curr : g1.lfArr ) {
			FloatsLF match = get(curr, g2, sqMaxDist );
			if ( match != null)
				matches.add( new LocalFeatureMatch( curr, match ) );
		}
		
		return matches;
	}
	
}
