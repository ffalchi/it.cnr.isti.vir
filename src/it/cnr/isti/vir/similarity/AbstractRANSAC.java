package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.geom.HomographyTransformation;
import it.cnr.isti.vir.geom.RSTTransformation;
import it.cnr.isti.vir.geom.TransformationHypothesis;
import it.cnr.isti.vir.global.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public abstract class AbstractRANSAC<F extends ALocalFeaturesGroup> extends AGroupSimilarity<F> {
	
	boolean rejectUnConsistent = false;
		
	Class tr = HomographyTransformation.class;
	int cycles = 2000;
	int nHoughMaxForRANSAC = 10;
	double errorPerc = 0.1;
	double minXYDist = 0.1;
	double[] RANSAC_minMaxSR;
	//private static int matchingDistance = 35; 
	
	double maxFDist = Double.MAX_VALUE;
	double sqMaxFDist = maxFDist*maxFDist;
	int maxFDist_int = Integer.MAX_VALUE;
	int sqMaxFDist_int = Integer.MAX_VALUE;
	
	
	protected double loweThr = 1.0;
	protected double sqLoweThr = loweThr*loweThr;
	
	protected long totNMatches = 0;
	protected long totNMatchesFiltered = 0;
	protected long totNMatchesUnused = 0;
	
	
	public final synchronized void updateStats(int nMatches, int nMatchesFiltered, int nMatchesUnused) {
		totNMatches += nMatches;
		totNMatchesFiltered += nMatchesFiltered;
		totNMatchesUnused += nMatchesUnused;
	}
	
	public void setMaxFDist(double given) {
		maxFDist = given;
		maxFDist_int = (int) Math.floor(maxFDist);
		sqMaxFDist = maxFDist*maxFDist;
		sqMaxFDist_int = maxFDist_int * maxFDist_int;
		Log.info_verbose("maxFDist: " + maxFDist);
	}
	
	public void setLoweThr(double given) {
		loweThr = given;
		sqLoweThr = loweThr*loweThr;
		Log.info_verbose("RANSAC loweThr: " +  loweThr );		
	}
	
	public AbstractRANSAC(String opt) throws SimilarityOptionException {
		super(opt);
	}
	
	public AbstractRANSAC() {
	}
	
	public AbstractRANSAC( Properties properties ) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("RANSAC_loweThr");
		if ( value != null ) {
			setLoweThr(Double.parseDouble(value));			
		} else {
			// Legacy
			value = properties.getProperty("loweThr");
			if ( value != null ) {
				setLoweThr(Double.parseDouble(value));			
			}
		}
		
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
			Log.info_verbose("RANSAC TR: " +  tr );
		}
		
		value = properties.getProperty("RANSAC_maxFDist");
		if ( value != null) {
			setMaxFDist( Double.parseDouble(value) );
			
		} else {
			// LEGACY
			value = properties.getProperty("maxFDist");
			if ( value != null) {
				setMaxFDist( Double.parseDouble(value) );			
			}
		}
		
		value = properties.getProperty("RANSAC_cycles");
		if ( value != null) {
			cycles = Integer.parseInt(value);
			Log.info_verbose("RANSAC cycles: " + cycles);
		}
		value = properties.getProperty("RANSAC_nBackets");
		if ( value != null) {
			nHoughMaxForRANSAC = Integer.parseInt(value);
			Log.info_verbose("RANSAC nHoughMaxForRANSAC: " + nHoughMaxForRANSAC);
		}
		value = properties.getProperty("RANSAC_err");
		if ( value != null) {
			errorPerc = Double.parseDouble(value);
			Log.info_verbose("RANSAC errorPerc: " + errorPerc);				
		}
		value = properties.getProperty("RANSAC_minDist");
		if ( value != null) {
			minXYDist = Double.parseDouble(value);
			Log.info_verbose("RANSAC minDist: " + minXYDist);				
		}
		
		value = properties.getProperty("RANSAC_minSR");
		if ( value != null) {
			RANSAC_minMaxSR = new double[2];
			RANSAC_minMaxSR[0] = Double.parseDouble(value);
			Log.info_verbose("RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
		}
		
		value = properties.getProperty("RANSAC_maxSR");
		if ( value != null) {
			RANSAC_minMaxSR[1] = Double.parseDouble(value);
			Log.info_verbose("RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
		}
	}
	
	public abstract LocalFeaturesMatches getMatches(F f1, F f2);
	
	public final ArrayList<TransformationHypothesis> getTrHypothesis(LocalFeaturesMatches matches ) {
		
		Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(matches.getMatches(), false, RANSAC_minMaxSR);
		return 	matches.getRANSAC( ht, cycles, nHoughMaxForRANSAC, errorPerc, tr, minXYDist, true, rejectUnConsistent);
		
	}
	
	public final double distance( F g1,  F g2) {
		double sim = 0;
		distCount++;
		
		LocalFeaturesMatches matches = getMatches(g1,g2);
		
		if ( matches == null || matches.size() == 0 ) return 1.0;
		
		ArrayList<TransformationHypothesis> trArr = getTrHypothesis(matches);
		
		if ( trArr == null || trArr.size() == 0 ) {
			sim = 0.0;
			updateStats(0, 0, matches.size());
		} else {
			sim = trArr.get(0).getHarmonicMeanOfPercentageMatches();
			updateStats(matches.size(), trArr.get(0).getMatches().size(), 0);
		}
		if ( sim > 1.0 ) sim = 1.0;
		
		return 1.0 - sim;
	}
	
	
	public String getStatsString() {
		return "totNMatchesUnused:\t" + totNMatchesUnused + "\n" +
				"totNMtchs:\t" + totNMatches +"\n" +
				"totNMtchsF:\t" + totNMatchesFiltered +"\n"
				;
	}
	
	public String toString() {
		return super.toString() + " loweThrloweThrloweThr=" + loweThr + " ";
	}
}
