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
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;

import java.util.Properties;

public class FloatsLFGroupSimilarity_RANSAC extends AbstractRANSAC<FloatsLFGroup> {
	

	protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(FloatsLFGroup.class);
	
	@Override
	public Class getRequestedGroup() {
		return FloatsLFGroup.class;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public FloatsLFGroupSimilarity_RANSAC() {
		super();
	}
	
	public FloatsLFGroupSimilarity_RANSAC( Properties properties ) throws SimilarityOptionException {
		super(properties);
		
		if ( maxFDist != Double.MAX_VALUE ) {
			// Because of UBytes and L2 Norm of SIFT Vectors
			setMaxFDist( maxFDist * ( 256 * Math.sqrt(128) * 2 ));
		}
		
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((FloatsLFGroup) f1.getFeature(FloatsLFGroup.class), (FloatsLFGroup) f2.getFeature(FloatsLFGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((FloatsLFGroup) f1.getFeature(FloatsLFGroup.class), (FloatsLFGroup) f2.getFeature(FloatsLFGroup.class));
	}
	
	@Override
	public final LocalFeaturesMatches getMatches( FloatsLFGroup g1,  FloatsLFGroup g2) {
		if ( loweThr >= 1.0 )
			return L2NNMatcher.getMatchesFloatsLF(g1, g2, sqMaxFDist_int);
		else if ( sqMaxFDist_int == Integer.MAX_VALUE ) 
			return L2NNLoweMatcher.getMatchesFloatsLF( g1, g2, sqLoweThr );
		
		// both parameters are used
		return L2NNLoweMatcher.getMatchesFloatsLF( g1, g2, sqLoweThr, sqMaxFDist );
	}
				
}
	
//	boolean rejectUnConsistent = false;
//		
//	Class tr = HomographyTransformation.class;
//	int cycles = 2000;
//	int nHoughMaxForRANSAC = 10;
//	double errorPerc = 0.1;
//	double minXYDist = 0.1;
//	double[] RANSAC_minMaxSR;
//	private static int matchingDistance = 35; 
//	double maxSqLFDist = 0.5*0.5;
//	
//	//double loweThrDef = 1.0;
//	
//	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(FloatsLFGroup.class);
//	
//	//private final double loweThr;
//	
//	private long totNMatches = 0;
//	private long totNMatchesFiltered = 0;
//	private long totNMatchesUnused = 0;
//
//	@Override
//	public Class getRequestedGroup()  { return FloatsLFGroup.class; } 
//	
//	public FloatsLFGroupSimilarity_RANSAC( Properties properties ) throws SimilarityOptionException {
//		super(properties);
//		String value;
////		value = properties.getProperty("loweThr");
////		if ( value != null ) {
////			loweThr = Double.parseDouble(value);
////			Log.info_verbose("loweThr: " +  loweThr );
////		} else  {
////			loweThr = loweThrDef;
////		}
//		
//		value = properties.getProperty("RANSAC_tr");
//		if ( value != null ) {
//			if ( value.equals("RST")) {
//				tr = RSTTransformation.class;
//			} else if ( value.equals("Affine")) {
//				tr = AffineTransformation.class;
//			} else if ( value.equals("Homography")) {
//				tr = HomographyTransformation.class;
//			} else {
//				throw new SimilarityOptionException("Option " + value + " not found!");
//			}
//			Log.info_verbose("RANSAC TR: " +  tr );
//		}
//		
//		value = properties.getProperty("maxFDist");
//		if ( value != null) {
//			double tValue = Double.parseDouble(value);
//			double maxFDist = (int) Math.floor(tValue);
//			maxSqLFDist = maxFDist * maxFDist;
//			Log.info_verbose("maxFDist: " + maxFDist);
//		}
//		
//		value = properties.getProperty("RANSAC_cycles");
//		if ( value != null) {
//			cycles = Integer.parseInt(value);
//			Log.info_verbose("RANSAC cycles: " + cycles);
//		}
//		value = properties.getProperty("RANSAC_nBackets");
//		if ( value != null) {
//			nHoughMaxForRANSAC = Integer.parseInt(value);
//			Log.info_verbose("RANSAC nHoughMaxForRANSAC: " + nHoughMaxForRANSAC);
//		}
//		value = properties.getProperty("RANSAC_err");
//		if ( value != null) {
//			errorPerc = Double.parseDouble(value);
//			Log.info_verbose("RANSAC errorPerc: " + errorPerc);				
//		}
//		value = properties.getProperty("RANSAC_minDist");
//		if ( value != null) {
//			minXYDist = Double.parseDouble(value);
//			Log.info_verbose("RANSAC minDist: " + minXYDist);				
//		}
//		
//		value = properties.getProperty("RANSAC_minSR");
//		if ( value != null) {
//			RANSAC_minMaxSR = new double[2];
//			RANSAC_minMaxSR[0] = Double.parseDouble(value);
//			Log.info_verbose("RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
//		}
//		
//		value = properties.getProperty("RANSAC_maxSR");
//		if ( value != null) {
//			RANSAC_minMaxSR[1] = Double.parseDouble(value);
//			Log.info_verbose("RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
//		}
//	}
//	
//	public FloatsLFGroupSimilarity_RANSAC() {
//
//	}
//
//
//	@Override
//	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
//		return distance((FloatsLFGroup) f1.getFeature(FloatsLFGroup.class), (FloatsLFGroup) f2.getFeature(FloatsLFGroup.class));
//	}
//	
//	@Override
//	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
//		return distance((FloatsLFGroup) f1.getFeature(FloatsLFGroup.class), (FloatsLFGroup) f2.getFeature(FloatsLFGroup.class));
//	}
//	
//	public final synchronized void updateStats(int nMatches, int nMatchesFiltered, int nMatchesUnused) {
//		totNMatches += nMatches;
//		totNMatchesFiltered += nMatchesFiltered;
//		totNMatchesUnused += nMatchesUnused;
//	}
//	
//	@Override
//	public final double distance( FloatsLFGroup g1,  FloatsLFGroup g2) {
//		double sim = 0;
//		distCount++;
//		
//		LocalFeaturesMatches matches = null;
//		ArrayList<TransformationHypothesis> trArr = null;
//		
//		matches = FloatsLFGroup.getL2NN( g1, g2, maxSqLFDist );
//		
//		
//		if ( matches == null || matches.size() == 0 ) return 1.0;
//		
//		
//		Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(matches.getMatches(), false, RANSAC_minMaxSR);
//		trArr = matches.getRANSAC( ht, cycles, nHoughMaxForRANSAC, errorPerc, tr, minXYDist, true, rejectUnConsistent);
//		
//		if ( trArr == null || trArr.size() == 0 ) {
//			sim = 0.0;
//			updateStats(0, 0, matches.size());
//		}
//		else {
//			sim = trArr.get(0).getHarmonicMeanOfPercentageMatches();
//			updateStats(matches.size(), trArr.get(0).getMatches().size(), 0);
//		}
//		if ( sim > 1.0 ) sim = 1.0;
//		
//		return 1.0 - sim;
//	}
//	
//	@Override
//	public FeatureClassCollector getRequestedFeaturesClasses() {		
//		return reqFeatures;
//	}
//	
//	public String toString() {
//		return super.toString() + " maxSqLFDist=" + maxSqLFDist + " ";
//	}
//	
//	
//	public String getStatsString() {
//		return "totNMatchesUnused:\t" + totNMatchesUnused + "\n" +
//				"totNMtchs:\t" + totNMatches +"\n" +
//				"totNMtchsF:\t" + totNMatchesFiltered +"\n"
//				;
//	}
//}

