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
