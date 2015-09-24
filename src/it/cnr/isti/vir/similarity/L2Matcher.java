package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;

public class L2Matcher {

	
	
	static final public LocalFeaturesMatches getNMatches(ALocalFeaturesGroup<SIFT> g1, ALocalFeaturesGroup<SIFT> g2, int squaredRange) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		
		if ( g2.size() == 0 ) return null;
		int nMatches = 0;
		
		for ( SIFT curr1 : g1.lfArr ) {
			
			for ( SIFT curr2 : g2.lfArr ) {
				
				int sqDist = L2.getSquared(curr1.values, curr2.values, squaredRange );
			
				if ( sqDist >= 0 && sqDist < squaredRange ) {
					nMatches++;
				}
				
			}
			
		}
		
		return matches;
	}
	
	static final public int getMinNMatchingFeatures(ALocalFeaturesGroup<SIFT> g1, ALocalFeaturesGroup<SIFT> g2, int squaredRange) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		
		if ( g1.size() == 0 || g2.size() == 0 ) return 0;
		int nMatch1 = 0;
		int nMatch2 = 0;
		
		boolean[] matches2 = new boolean[g2.size()]; 
		for ( SIFT curr1 : g1.lfArr ) {
			boolean curr1Matched = false;
			for ( int i=0; i<g2.size(); i++ ) {
				SIFT curr2 = g2.lfArr[i];
				int sqDist = L2.getSquared(curr1.values, curr2.values, squaredRange );
			
				if ( sqDist >= 0 && sqDist <= squaredRange ) {
					
					matches2[i] = true;
					
					// we have a mtach
					if ( !curr1Matched ) {
						nMatch1++;
						curr1Matched = true;
					}
										
				}
				
			}
			
		}
		
		for ( boolean c : matches2 ) {
			if ( c ) nMatch2++;
		}
		
		
		return Math.min(nMatch1, nMatch2);
	}
	
	/**
	 * Penalayzing burst
	 * 
	 * @param g1
	 * @param g2
	 * @param squaredRange
	 * @return
	 */
	static final public double getMinNMatchingFeatures_Burst(ALocalFeaturesGroup<SIFT> g1, ALocalFeaturesGroup<SIFT> g2, int squaredRange) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		
		if ( g1.size() == 0 || g2.size() == 0 ) return 0;
		
		double mtch1 = 0;
		
		int[] matches2 = new int[g2.size()]; 
		for ( SIFT curr1 : g1.lfArr ) {
			int curr1Mtchs = 0;
			for ( int i2=0; i2<g2.size(); i2++ ) {
				SIFT curr2 = g2.lfArr[i2];
				int sqDist = L2.getSquared(curr1.values, curr2.values, squaredRange );
			
				if ( sqDist >= 0 && sqDist <= squaredRange ) {
					
					matches2[i2]++;
					
					// we have a match for curr1
					curr1Mtchs++;;
															
				}
				
			}
			
			if ( curr1Mtchs != 0)
				mtch1 += 1.0 / curr1Mtchs; 
			
		}
		
		
		double mtch2 = 0;
		
		for ( int c : matches2 ) {
			if ( c != 0 ) mtch2 += 1.0  / c;
		}
		
		return Math.min(mtch1, mtch2);
	}
}
