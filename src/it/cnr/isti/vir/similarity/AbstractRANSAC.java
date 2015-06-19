package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.geom.AbstractTransformation;
import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.geom.HomographyTransformation;
import it.cnr.isti.vir.geom.RSTTransformation;
import it.cnr.isti.vir.geom.TransformationHypothesis;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Properties;

public abstract class AbstractRANSAC<F extends ALocalFeaturesGroup> extends AGroupSimilarity<F> {
	
	boolean rejectUnConsistent = false;
		
	Class<? extends AbstractTransformation> tr = HomographyTransformation.class;
	int minMatches = 10; //Transformations.getNPointsForEstimation(tr) * 2;
	double minPercMatches = 0.0;
	int cycles = 1000;
	int nHoughMaxForRANSAC = 3;
	double errorPerc = 0.1;
	double minXYDist = 0.1;
	double[] RANSAC_minMaxSR;
	//private static int matchingDistance = 35; 
	
	double maxFDist = Double.MAX_VALUE;
	double sqMaxFDist = maxFDist*maxFDist;
	int maxFDist_int = Integer.MAX_VALUE;
	int sqMaxFDist_int = Integer.MAX_VALUE;
	
	int minHoughMatches = 0;
	
	protected double loweThr = 1.0;
	protected double sqLoweThr = loweThr*loweThr;
	
	protected long totNMatches = 0;
	protected long totNMatchesFiltered = 0;
	protected long totNMatchesUnused = 0;
	protected long totNRANSAC = 0;
	
	protected boolean onlyNMatches = false;
	protected boolean nHoughMatches = false;
	
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
		Log.info_verbose(this.getClass().getName() + ": maxFDist: " + maxFDist);
	}
	
	public void setLoweThr(double given) {
		loweThr = given;
		sqLoweThr = loweThr*loweThr;
		Log.info_verbose(this.getClass().getName() + ": RANSAC loweThr: " +  loweThr );		
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
			Log.info_verbose(this.getClass().getName() + ": RANSAC TR: " +  tr );
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
			Log.info_verbose(this.getClass().getName() + ": RANSAC cycles: " + cycles);
		}
		value = properties.getProperty("RANSAC_nBackets");
		if ( value != null) {
			nHoughMaxForRANSAC = Integer.parseInt(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC nHoughMaxForRANSAC: " + nHoughMaxForRANSAC);
		}
		value = properties.getProperty("RANSAC_err");
		if ( value != null) {
			errorPerc = Double.parseDouble(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC errorPerc: " + errorPerc);				
		}
		value = properties.getProperty("RANSAC_minDist");
		if ( value != null) {
			minXYDist = Double.parseDouble(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC minDist: " + minXYDist);				
		}
		
		value = properties.getProperty("RANSAC_minSR");
		if ( value != null) {
			RANSAC_minMaxSR = new double[2];
			RANSAC_minMaxSR[0] = Double.parseDouble(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
		}
		
		value = properties.getProperty("RANSAC_maxSR");
		if ( value != null) {
			RANSAC_minMaxSR[1] = Double.parseDouble(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
		}
		
		onlyNMatches = PropertiesUtils.getBoolean(properties, "RANSAC_onlyNMatches", false);
		Log.info_verbose(this.getClass().getName() + ": onlyNMatches: " + onlyNMatches);
		
		nHoughMatches = PropertiesUtils.getBoolean(properties, "RANSAC_nHoughMatches", false);
		Log.info_verbose(this.getClass().getName() + ": nHoughMatches: " + nHoughMatches);
		
		value = properties.getProperty("RANSAC_minHoughMatches");
		if ( value != null) {
			minHoughMatches = Integer.parseInt(value);
			Log.info_verbose(this.getClass().getName() + ": RANSAC_minHoughMatches: " + minHoughMatches);				
		}
		
	}
	
	public abstract LocalFeaturesMatches getMatches(F f1, F f2);
	
	public final Hashtable<Long, LocalFeaturesMatches> getLoweHoughTransforms_HT(LocalFeaturesMatches matches ) {
		if ( matches.size() <  minMatches ) return null;
		return LoweHoughTransform.getLoweHoughTransforms_HT(matches.getMatches(), false, RANSAC_minMaxSR);
	}
	
	public final ArrayList<TransformationHypothesis> getTrHypothesis(LocalFeaturesMatches matches, int qN ) {
		
		Hashtable<Long, LocalFeaturesMatches> ht = this.getLoweHoughTransforms_HT(matches);
		
		if ( ht == null || ht.size() == 0 ) return null;
		LocalFeaturesMatches[] ordered = LoweHoughTransform.orderHT(ht);
		
		if ( ordered[0].size() < minHoughMatches || ordered[0].size() / (double) qN < minPercMatches ) return null;
		
		totNRANSAC++;
		
		return 	matches.getRANSAC( ht, cycles, nHoughMaxForRANSAC, errorPerc, tr, minXYDist, true, rejectUnConsistent);
		
	}
	
	public double distance( F g1,  F g2) {
		
		return distance(g1, g2, getMatches(g1,g2));
	}
	
	public final double distance( F g1,  F g2, LocalFeaturesMatches matches) {
		
		distCount++;
		
		if ( matches == null || matches.size() == 0 ) return 1.0;
		
		if ( matches.size() / (double) g1.size() < minPercMatches ) {
			updateStats(0, matches.size(), 0);
			return 1.0;
		}
		
		if ( nHoughMatches ) {
			Hashtable<Long, LocalFeaturesMatches> ht = this.getLoweHoughTransforms_HT(matches);
			if ( ht == null || ht.size() == 0 ) {
				updateStats(0, matches.size(), 0);
				return 1.0;
			}
			LocalFeaturesMatches[] ordered = LoweHoughTransform.orderHT(ht);
			updateStats(0, matches.size(), 0);
			return 1.0-this.getPercentage(ordered[0].size(), g1.size(), g2.size() );
		} else if (  onlyNMatches ) {
			updateStats(0, matches.size(), 0);
			return 1.0-this.getPercentage(matches.size(), g1.size(), g2.size() );
		}
		
		double res = 0;
		
		if ( matches.size() / (double) g1.size() < minPercMatches ) {
			updateStats(0, matches.size(), 0);
			return 1.0;
		}
		
		ArrayList<TransformationHypothesis> trArr = getTrHypothesis(matches, g1.size() );
		
		if ( trArr == null || trArr.size() == 0 ) {
			res = 0.0;
			updateStats(0, 0, matches.size());
		} else {

			res = this.getPercentage(trArr.get(0).getMatches().size(), g1.size(), g2.size() );

			updateStats(matches.size(), trArr.get(0).getMatches().size(), 0);
		}
		if ( res > 1.0 ) res = 1.0;
		
		return 1.0 - res;
		
	}
	
	


	
	public String getStatsString() {
		return "totNMatchesUnused:\t" + totNMatchesUnused + "\n" +
				"totNMtchs:\t" + totNMatches +"\n" +
				"totNMtchsF:\t" + totNMatchesFiltered +"\n" +
				"totNRANSAC:\t" + totNRANSAC + "\n"
				;
	}
	
	public String toString() {
		return super.toString() + "\n" +
				"\t" + "onlyNMatches=" + onlyNMatches + "\n" +
				"\t" + "nHoughMatches=" + nHoughMatches + "\n" +
				"\t" + "loweThr=" + loweThr + "\n" +
				"\t" + "minMatches=" + minMatches + "\n" +
				"\t" + "minPercMatches=" + minPercMatches  + "\n" +
				"\t" + "cycles= " + cycles + "\n" +
				"\t" + "tr=" + tr.getName() + "\n" +
				"\t" + "nHoughMaxForRANSAC=" + nHoughMaxForRANSAC + "\n" +
				"\t" + "errorPerc=" + errorPerc + "\n" +
				"\t" + "minXYDist=" +minXYDist + "\n" +
				"\t" + "RANSAC_minMaxSR=" + Arrays.toString( RANSAC_minMaxSR )  + "\n" +
				"\t" + "maxFDist=" + maxFDist + "\n" +
				"\t" + "sqMaxFDist_int" + sqMaxFDist_int + "\n" + 
				"\t" + "minPercMatches=" + minPercMatches + "\n" + 
				"\t" + "minHoughMatches=" + minHoughMatches + "\n";
		
	} 
}
