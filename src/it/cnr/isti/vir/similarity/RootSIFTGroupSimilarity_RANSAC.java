package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;

import java.util.Properties;

public class RootSIFTGroupSimilarity_RANSAC extends AbstractRANSAC<RootSIFTGroup> {

	protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(RootSIFTGroup.class);
	
	@Override
	public Class getRequestedGroup() {
		return RootSIFTGroup.class;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public RootSIFTGroupSimilarity_RANSAC() {
		super();
	}
	
	public RootSIFTGroupSimilarity_RANSAC( Properties properties ) throws SimilarityOptionException {
		super(properties);
		
		if ( maxFDist != Double.MAX_VALUE ) {
			// Because of UBytes and L2 Norm of SIFT Vectors
			setMaxFDist( maxFDist * ( 256 * Math.sqrt(128) * 2 ));
		}
		
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((RootSIFTGroup) f1.getFeature(RootSIFTGroup.class), (RootSIFTGroup) f2.getFeature(RootSIFTGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((RootSIFTGroup) f1.getFeature(RootSIFTGroup.class), (RootSIFTGroup) f2.getFeature(RootSIFTGroup.class));
	}
		@Override
	public final LocalFeaturesMatches getMatches( RootSIFTGroup g1,  RootSIFTGroup g2) {
		if ( loweThr >= 1.0 )
			return L2NNMatcher.getMatchesRootSIFT(g1, g2, sqMaxFDist_int);
		else if ( sqMaxFDist_int == Integer.MAX_VALUE ) 
			return L2NNLoweMatcher.getMatchesRootSIFT( g1, g2, sqLoweThr );
		
		// both parameters are used
		return L2NNLoweMatcher.getMatchesRootSIFT( g1, g2, sqLoweThr, sqMaxFDist_int );
	}
				
}
	
//	boolean rejectUnConsistent = true;
//	
//	Class tr = HomographyTransformation.class;
//	int cycles = 1000;
//	int nHoughMaxForRANSAC = 10;
//	double errorPerc = 0.1;
//	double minXYDist = 0.1;
//	double[] RANSAC_minMaxSR;
//	int maxFDistSq = Integer.MAX_VALUE;
//	
//	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(RootSIFTGroup.class);
//	
//	private final double sqrLoweThr;
//	
//	public RootSIFTGroupSimilarity_RANSAC( Properties properties) throws SimilarityOptionException {
//		super(properties);
//		String value = properties.getProperty("loweThr");
//		sqrLoweThr = Double.parseDouble(value);
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
//			System.out.println("RANSAC TR: " +  tr );
//		}
//		
//		value = properties.getProperty("maxFDistSq");
//		if ( value != null) {
//			double tValue = Double.parseDouble(value);
//			maxFDistSq = (int) Math.floor(tValue * tValue );
//			System.out.println("maxFDistSq: " + maxFDistSq);
//		}
//		
//		value = properties.getProperty("RANSAC_cycles");
//		if ( value != null) {
//			cycles = Integer.parseInt(value);
//			System.out.println("RANSAC cycles: " + cycles);
//		}
//		value = properties.getProperty("RANSAC_nBackets");
//		if ( value != null) {
//			nHoughMaxForRANSAC = Integer.parseInt(value);
//			System.out.println("RANSAC nHoughMaxForRANSAC: " + nHoughMaxForRANSAC);
//		}
//		value = properties.getProperty("RANSAC_err");
//		if ( value != null) {
//			errorPerc = Double.parseDouble(value);
//			System.out.println("RANSAC errorPerc: " + errorPerc);				
//		}
//		value = properties.getProperty("RANSAC_minDist");
//		if ( value != null) {
//			minXYDist = Double.parseDouble(value);
//			System.out.println("RANSAC minDist: " + minXYDist);				
//		}
//		
//		value = properties.getProperty("RANSAC_minSR");
//		if ( value != null) {
//			RANSAC_minMaxSR = new double[2];
//			RANSAC_minMaxSR[0] = Double.parseDouble(value);
//			System.out.println("RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
//		}
//		
//		value = properties.getProperty("RANSAC_maxSR");
//		if ( value != null) {
//			RANSAC_minMaxSR[1] = Double.parseDouble(value);
//			System.out.println("RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
//		}
//	}
//	
//	public RootSIFTGroupSimilarity_RANSAC(double loweThr) {
//		this.sqrLoweThr = loweThr*loweThr;
//	}
//	
//	public RootSIFTGroupSimilarity_RANSAC(String opt, double loweThr) throws Exception {
//		super(opt);
//		this.sqrLoweThr = loweThr*loweThr;
//	}
//
//	@Override
//	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
//		return distance((RootSIFTGroup) f1.getFeature(RootSIFTGroup.class), (RootSIFTGroup) f2.getFeature(RootSIFTGroup.class));
//	}
//	
//	@Override
//	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
//		return distance((RootSIFTGroup) f1.getFeature(RootSIFTGroup.class), (RootSIFTGroup) f2.getFeature(RootSIFTGroup.class));
//	}
//	
//	@Override
//	public final double distance( RootSIFTGroup g1,  RootSIFTGroup g2) {
//		double sim = 0;
//		distCount++;
//		
//		LocalFeaturesMatches matches = null;
//		ArrayList<TransformationHypothesis> trArr = null;
//		
//		if ( maxFDistSq != Integer.MAX_VALUE )
//			matches = RootSIFTGroup.getLoweMatches( g1, g2, sqrLoweThr, maxFDistSq );
//		else
//			matches = RootSIFTGroup.getLoweMatches( g1, g2, sqrLoweThr );
//		if ( matches == null || matches.size() < 2 ) return 1.0; 
//		Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(matches.getMatches(), false, RANSAC_minMaxSR);
//		trArr = matches.getRANSAC( ht, cycles, nHoughMaxForRANSAC, errorPerc, tr, minXYDist, true, rejectUnConsistent);
//		
//		if ( trArr == null || trArr.size() == 0 ) sim = 0.0;
//		else sim = trArr.get(0).getHarmonicMeanOfPercentageMatches();
////		else sim = (double) trArr.size() / 1000;
//		if ( sim > 1.0 ) sim = 1.0;
//		
////		switch (option) {
////			case optFt1:
////				matches12 = SURFGroup.getLoweMatches( g1, g2, sqrLoweThr );
//////				System.out.println("\nLowe matches n: " + matches12.size());
////				if ( matches12.size() < 3 ) return 1.0;
//////				System.out.println(matches12.toString());				
////				
////				
//////				matches12.filter_LoweHoughTransform();		
//////				System.out.println("HoughTransform matches n: " + matches12.size());
//////				System.out.println(matches12.toString());
//////				if ( matches12.size() == 0 ) return 1.0;
////				
////				matches12.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
//////				System.out.println("RANSAC matches n: " + matches12.size());
////				if ( matches12.size() == 0 ) return 1.0;
//////				System.out.println(matches12.toString());
////								
////				sim = (double) matches12.size() / 1000;
////				if ( sim > 1.0 ) sim = 1.0;
////				break;
////				
////			case optFt2:
////				matches21 = SURFGroup.getLoweMatches( g2, g1, sqrLoweThr );
////				matches21.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				sim = (double) matches21.size() / g2.size();
////				break;
////			
////			case optAvg:
////				matches12 = SURFGroup.getLoweMatches( g1, g2, sqrLoweThr );
////				if ( matches12.size() < 3 ) return 1.0;
////				matches12.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				matches21 = SURFGroup.getLoweMatches( g2, g1, sqrLoweThr );
////				if ( matches21.size() < 3 ) return 1.0;
////				matches21.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				sim = ( (double) matches12.size() / g1.size() + (double) matches21.size() / g2.size() ) / 2.0;
////				break;
////				
////			// min distance corresponds to max similarity and viceversa
////			case optMin:
////				matches12 = SURFGroup.getLoweMatches( g1, g2, sqrLoweThr );
////				if ( matches12.size() < 3 ) return 1.0;
////				matches12.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				matches21 = SURFGroup.getLoweMatches( g2, g1, sqrLoweThr );
////				if ( matches21.size() < 3 ) return 1.0;
////				matches21.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				sim =  Math.max( (double) matches12.size() / g1.size() , (double) matches21.size() / g2.size() );
////				break;
////				
////			case optMax:
//////				System.out.println("MAX DISTANCE");
////				matches12 = SURFGroup.getLoweMatches( g1, g2, sqrLoweThr );
////				matches12.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				matches21 = SURFGroup.getLoweMatches( g2, g1, sqrLoweThr );
////				matches21.filter_RANSAC(cycles, nHoughMaxForRANSAC, errorPerc);
////				sim =  Math.min( (double) matches12.size() / g1.size() , (double) matches21.size() / g2.size() );
////				break;	
////			default: 	break;
////		}
//					
//		
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
//		return super.toString() + " sqrConfThr=" + sqrLoweThr + " ";
//	}


