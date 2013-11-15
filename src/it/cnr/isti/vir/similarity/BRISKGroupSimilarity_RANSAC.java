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
import it.cnr.isti.vir.features.localfeatures.BRISKGroup;
import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.geom.HomographyTransformation;
import it.cnr.isti.vir.geom.RSTTransformation;
import it.cnr.isti.vir.geom.TransformationHypothesis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public class BRISKGroupSimilarity_RANSAC extends IGroupSimilarity<BRISKGroup> {
	
	boolean rejectUnConsistent = false;
	
	Class tr = HomographyTransformation.class;
	int cycles = 1000;
	int nHoughMaxForRANSAC = 10;
	double errorPerc = 0.1;
	double minXYDist = 0.1;
	double[] RANSAC_minMaxSR;
	int maxFDistSq = Integer.MAX_VALUE;
	
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BRISKGroup.class);
	
	private final double sqrLoweThr;
	
	public BRISKGroupSimilarity_RANSAC( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("loweThr");
		sqrLoweThr = Double.parseDouble(value);
		System.out.println("sqrLoweThr: " +  sqrLoweThr );
		
		value = properties.getProperty("RANSAC_tr");
		if ( value != null ) {
			if ( value.equals("RST")) {
				tr = RSTTransformation.class;
			} else if ( value.equals("Affine")) {
				tr = AffineTransformation.class;
			} else if ( value.equals("Homography")) {
				tr = HomographyTransformation.class;
			} else {
				throw new SimilarityOptionException("Option " + value + " not found!");
			}
			System.out.println("RANSAC TR: " +  tr );
		}
		
		value = properties.getProperty("maxFDistSq");
		if ( value != null) {
			double tValue = Double.parseDouble(value);
			maxFDistSq = (int) Math.floor(tValue * tValue );
			System.out.println("maxFDistSq: " + maxFDistSq);
		}
		
		value = properties.getProperty("RANSAC_cycles");
		if ( value != null) {
			cycles = Integer.parseInt(value);
			System.out.println("RANSAC cycles: " + cycles);
		}
		value = properties.getProperty("RANSAC_nBackets");
		if ( value != null) {
			nHoughMaxForRANSAC = Integer.parseInt(value);
			System.out.println("RANSAC nHoughMaxForRANSAC: " + nHoughMaxForRANSAC);
		}
		value = properties.getProperty("RANSAC_err");
		if ( value != null) {
			errorPerc = Double.parseDouble(value);
			System.out.println("RANSAC errorPerc: " + errorPerc);				
		}
		value = properties.getProperty("RANSAC_minDist");
		if ( value != null) {
			minXYDist = Double.parseDouble(value);
			System.out.println("RANSAC minDist: " + minXYDist);				
		}
		
		value = properties.getProperty("RANSAC_minSR");
		if ( value != null) {
			RANSAC_minMaxSR = new double[2];
			RANSAC_minMaxSR[0] = Double.parseDouble(value);
			System.out.println("RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
		}
		
		value = properties.getProperty("RANSAC_maxSR");
		if ( value != null) {
			RANSAC_minMaxSR[1] = Double.parseDouble(value);
			System.out.println("RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
		}
	}
	
	public BRISKGroupSimilarity_RANSAC(double loweThr) {
		this.sqrLoweThr = loweThr*loweThr;
	}
	
	public BRISKGroupSimilarity_RANSAC(String opt, double loweThr) throws Exception {
		super(opt);
		this.sqrLoweThr = loweThr*loweThr;
	}

	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((BRISKGroup) f1.getFeature(BRISKGroup.class), (BRISKGroup) f2.getFeature(BRISKGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((BRISKGroup) f1.getFeature(BRISKGroup.class), (BRISKGroup) f2.getFeature(BRISKGroup.class));
	}
	
	@Override
	public final double distance( BRISKGroup g1,  BRISKGroup g2) {
		double sim = 0;
		distCount++;
		
		LocalFeaturesMatches matches = null;
		ArrayList<TransformationHypothesis> trArr = null;
		
		if ( maxFDistSq != Integer.MAX_VALUE )
			matches = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr, maxFDistSq );
		else
			matches = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr );
		if ( matches == null || matches.size() < 2 ) return 1.0; 
		Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(matches.getMatches(), false, RANSAC_minMaxSR);
		trArr = matches.getRANSAC( ht, cycles, nHoughMaxForRANSAC, errorPerc, tr, minXYDist, true, rejectUnConsistent);
		
		if ( trArr == null || trArr.size() == 0 ) sim = 0.0;
		else sim = trArr.get(0).getHarmonicMeanOfPercentageMatches();
//		else sim = (double) trArr.size() / 1000;
		if ( sim > 1.0 ) sim = 1.0;

		return 1.0 - sim;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public String toString() {
		return super.toString() + " sqrConfThr=" + sqrLoweThr + " ";
	}
	
}

