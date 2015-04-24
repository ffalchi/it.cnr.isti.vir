package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.BRISKGroup;
import it.cnr.isti.vir.features.localfeatures.ORBGroup;

import java.util.Properties;

public class BRISKGroupSimilarity_Hough  extends AGroupSimilarity<BRISKGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BRISKGroup.class);
	
	private final double sqrLoweThr;

	@Override
	public Class getRequestedGroup()  { return BRISKGroup.class; } 
	
	public BRISKGroupSimilarity_Hough( Properties properties) {
		String value = properties.getProperty("loweThr");
		sqrLoweThr = Double.parseDouble(value);
	}
	
	public BRISKGroupSimilarity_Hough(double loweThr) {
		this.sqrLoweThr = loweThr*loweThr;
	}
	
	public BRISKGroupSimilarity_Hough(String opt, double loweThr) throws SimilarityOptionException {
		super(opt);
		this.sqrLoweThr = loweThr*loweThr;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
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
		
		LocalFeaturesMatches matches12 = null;
		LocalFeaturesMatches matches21 = null;
		switch (option) {
			case optFt1:
				matches12 = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				sim = (double) matches12.size() / g1.size();
				break;
			case optFt2:
				matches21 = BRISKGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim = (double) matches21.size() / g2.size();
				break;
			
			case optAvg:
				matches12 = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = BRISKGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim = ( (double) matches12.size() / g1.size() + (double) matches21.size() / g2.size() ) / 2.0;
				break;
			// min distance corresponds to max similarity and viceversa
				
			case optMin:
				matches12 = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = BRISKGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim =  Math.max( (double) matches12.size() / g1.size() , (double) matches21.size() / g2.size() );
				break;
				
			case optMax:
				matches12 = BRISKGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = BRISKGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim =  Math.min( (double) matches12.size() / g1.size() , (double) matches21.size() / g2.size() );
				break;	
			default: 	break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}	
	
	public String toString() {
		return super.toString() + " sqrConfThr=" + sqrLoweThr + " ";
	}

	public String getStatsString() { return ""; };


}
