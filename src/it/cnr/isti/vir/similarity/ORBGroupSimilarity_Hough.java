package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.ORBGroup;

import java.util.Properties;

public class ORBGroupSimilarity_Hough  extends IGroupSimilarity<ORBGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ORBGroup.class);
	
	private final double sqrLoweThr;
	
	public ORBGroupSimilarity_Hough( Properties properties) {
		String value = properties.getProperty("loweThr");
		sqrLoweThr = Double.parseDouble(value);
	}
	
	public ORBGroupSimilarity_Hough(double loweThr) {
		this.sqrLoweThr = loweThr*loweThr;
	}
	
	public ORBGroupSimilarity_Hough(String opt, double loweThr) throws SimilarityOptionException {
		super(opt);
		this.sqrLoweThr = loweThr*loweThr;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}
	
	@Override
	public final double distance( ORBGroup g1,  ORBGroup g2) {
		double sim = 0;
		
		LocalFeaturesMatches matches12 = null;
		LocalFeaturesMatches matches21 = null;
		switch (option) {
			case optFt1:
				matches12 = ORBGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				sim = (double) matches12.size() / g1.size();
				break;
			case optFt2:
				matches21 = ORBGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim = (double) matches21.size() / g2.size();
				break;
			
			case optAvg:
				matches12 = ORBGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = ORBGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim = ( (double) matches12.size() / g1.size() + (double) matches21.size() / g2.size() ) / 2.0;
				break;
			// min distance corresponds to max similarity and viceversa
				
			case optMin:
				matches12 = ORBGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = ORBGroup.getLoweMatches( g2, g1, sqrLoweThr );
				matches21.filter_LoweHoughTransform();
				sim =  Math.max( (double) matches12.size() / g1.size() , (double) matches21.size() / g2.size() );
				break;
				
			case optMax:
				matches12 = ORBGroup.getLoweMatches( g1, g2, sqrLoweThr );
				matches12.filter_LoweHoughTransform();
				matches21 = ORBGroup.getLoweMatches( g2, g1, sqrLoweThr );
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



}
