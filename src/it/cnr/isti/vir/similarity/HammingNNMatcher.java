package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.Hamming;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.ORB;

public class HammingNNMatcher {

	static final public ORB getMatch(ORB q, ALocalFeaturesGroup<ORB> sg, int maxD) {
		ORB best = null;
		int resDist = maxD;

		for (ORB curr : sg.lfArr) {
			int d = Hamming.distance(q.data, curr.data );
			if ( d < resDist) {
				resDist = d;
				best = curr;
			}
		}
		return best;
	}
	
	static final public LocalFeaturesMatches getMatches(ALocalFeaturesGroup<ORB> sg1, ALocalFeaturesGroup<ORB> sg2, final int maxD) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		ORB[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			ORB match = getMatch(arr[i], sg2, maxD );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
	
}
