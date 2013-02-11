/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.geom;

import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

/**
 *
 * @author Fabrizio
 */
public class TransformationHypothesis implements Comparable {

    private final AbstractTransformation trNorm;
//    private final Transformation trDeNorm;
    private final LocalFeaturesMatches matches;
    private Double perMatches = null;
    private Double perMatchesOnMatching = null;
    private Double maxPercentageMatches = null;


    public AbstractTransformation getTrDeNorm() {
        return trNorm.getDeNormalized(matches.getLFGroup(), matches.getMatchingLFGroup());
    }

    public LocalFeaturesMatches getMatches() {
        return matches;
    }

    public TransformationHypothesis( AbstractTransformation tr, LocalFeaturesMatches matches ) {
        this.trNorm = tr;
        this.matches = matches;
    }

    public Double getMaxPercentageMatches() {
        if ( maxPercentageMatches == null ) initPercentageMatches();
        return maxPercentageMatches;
    }
    
    public Double getHarmonicMeanOfPercentageMatches() {
        if ( maxPercentageMatches == null ) initPercentageMatches();
        return 2*perMatches*perMatchesOnMatching/(perMatches+perMatchesOnMatching);
    }

    public synchronized void initPercentageMatches() {
//    	float[][] box = matches.getBox();
//    	float[][] matchingBox = matches.getMatchingBox();
        // int n1 = matches.getLFGroup().countInBox(box);
        // int n2 = matches.getMatchingLFGroup().countInBox(matchingBox);
        perMatches = (double) matches.size() / matches.getLFGroup().size();
      	perMatchesOnMatching = (double) matches.getMatchingLFGroup().size();
      	if ( perMatches > perMatchesOnMatching ) maxPercentageMatches = perMatches;
      	else maxPercentageMatches = perMatchesOnMatching;
    }

///    public int compareTo(Object o) {
//        AffineTransformationHypothesis given = (AffineTransformationHypothesis) o;
 //       return -getMaxPercentageMatches().compareTo(given.getMaxPercentageMatches());
//    }
    public int compareTo(Object o) {
        TransformationHypothesis given = (TransformationHypothesis) o;
        return given.matches.size() - matches.size() ;
    }

    public String toString() {
        String tStr = new String();
//        tStr += tr;
        tStr += "maxPercentageMatches: " + maxPercentageMatches + "\n";
        tStr += "nMatches: " + matches.size() + "\n";
        tStr += "Determinant: " + trNorm.getDeterminant();
        return tStr;
    }

	public Double getPerMatches() {
		if ( maxPercentageMatches == null ) initPercentageMatches();
		return perMatches;
	}

	public Double getPerMatchesOnMatching() {
		if ( maxPercentageMatches == null ) initPercentageMatches();
		return perMatchesOnMatching;
	}

}
