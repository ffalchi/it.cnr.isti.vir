package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.SIFT;

public class L2NNLoweMatcher {

	static final public SIFT get(SIFT s1, ALocalFeaturesGroup<SIFT> sg, double conf, int maxFDsq) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		SIFT curr, best = null;
		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = L2.getSquared(s1.values, curr.values, distsq2);
			if (dsq >= 0) {
				if (dsq < distsq1) {
					distsq2 = distsq1;
					distsq1 = dsq;
					best = curr;
				} else if (dsq < distsq2) {
					distsq2 = dsq;
				}
			}
		}

		if (distsq1 > maxFDsq) return null;
		if (distsq2 == 0) return null;
		
		// Lowe check
		if ((double) distsq1 / (double) distsq2 < conf) return best;
		
		return null;
	}
	
	static final public RootSIFT get(RootSIFT s1, ALocalFeaturesGroup<RootSIFT> sg, double conf, int maxFDsq) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		RootSIFT curr, best = null;
		RootSIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = L2.getSquared(s1.values, curr.values, distsq2);
			if (dsq >= 0) {
				if (dsq < distsq1) {
					distsq2 = distsq1;
					distsq1 = dsq;
					best = curr;
				} else if (dsq < distsq2) {
					distsq2 = dsq;
				}
			}
		}

		if (distsq1 > maxFDsq) return null;
		if (distsq2 == 0) return null;
		
		// Lowe check
		if ((double) distsq1 / (double) distsq2 < conf) return best;
		
		return null;
	}
	
	static final public FloatsLF get(FloatsLF s1, ALocalFeaturesGroup<FloatsLF> sg, double conf, double maxFDsq) {
		double distsq1 = Double.MAX_VALUE;
		double distsq2 = Double.MAX_VALUE;
		double dsq = 0;
		FloatsLF curr, best = null;
		FloatsLF[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = L2.getSquared(s1.values, curr.values, distsq2);
			if (dsq >= 0) {
				if (dsq < distsq1) {
					distsq2 = distsq1;
					distsq1 = dsq;
					best = curr;
				} else if (dsq < distsq2) {
					distsq2 = dsq;
				}
			}
		}

		if (distsq1 > maxFDsq) return null;
		if (distsq2 == 0) return null;
		
		// Lowe check
		if ((double) distsq1 / (double) distsq2 < conf) return best;
		
		return null;
	}
	
	static final public LocalFeaturesMatches getMatchesSIFT(ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2, double dRatioThr, final int maxLFDistSq) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SIFT match = get(arr[i], sg2, dRatioThr, maxLFDistSq );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
	
	static final public LocalFeaturesMatches getMatchesRootSIFT(ALocalFeaturesGroup<RootSIFT> sg1, ALocalFeaturesGroup<RootSIFT> sg2, double dRatioThr, final int maxLFDistSq) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		RootSIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			RootSIFT match = get(arr[i], sg2, dRatioThr, maxLFDistSq );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
	
	static final public LocalFeaturesMatches getMatchesFloatsLF(ALocalFeaturesGroup<FloatsLF> sg1, ALocalFeaturesGroup<FloatsLF> sg2, double dRatioThr, final double maxLFDistSq) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		FloatsLF[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			FloatsLF match = get(arr[i], sg2, dRatioThr, maxLFDistSq );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
	
	static final public int getNMatches(ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2, double conf) {
		if (sg2.size() < 2)
			return 0;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			if (getMatch(arr[i], sg2, conf) != null)
				nMatches++;
		}

		return nMatches;
	}
	
	static final public double getPercMatches(ALocalFeaturesGroup<SIFT> sg1,	ALocalFeaturesGroup<SIFT> sg2, double conf) {
		return (double) getNMatches(sg1, sg2, conf) / sg1.size();
	}

	static final public SIFT getMatch(SIFT s1, ALocalFeaturesGroup<SIFT> sg, double conf) {
		return getMatch(s1, sg, Integer.MAX_VALUE);
	}
	
	static final public LocalFeaturesMatches getMatchesSIFT(ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2, double dRatioThr) {
		return getMatchesSIFT(sg1, sg2, dRatioThr, Integer.MAX_VALUE);
	}
	
	static final public LocalFeaturesMatches getMatchesRootSIFT(ALocalFeaturesGroup<RootSIFT> sg1, ALocalFeaturesGroup<RootSIFT> sg2, double dRatioThr) {
		return getMatchesRootSIFT(sg1, sg2, dRatioThr, Integer.MAX_VALUE);
	}
	
	static final public LocalFeaturesMatches getMatchesFloatsLF(ALocalFeaturesGroup<FloatsLF> sg1, ALocalFeaturesGroup<FloatsLF> sg2, double dRatioThr) {
		return getMatchesFloatsLF(sg1, sg2, dRatioThr, Double.MAX_VALUE);
	}


	
}
