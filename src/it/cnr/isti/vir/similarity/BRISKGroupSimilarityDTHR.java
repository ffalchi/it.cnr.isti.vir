package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.BRISKGroup;

import java.util.Properties;

public class BRISKGroupSimilarityDTHR  extends IGroupSimilarity<BRISKGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BRISKGroup.class);
	
	private final int radius;
	
	private final int DEFAULT_RADIUS_RANGE = 35;
		
	public BRISKGroupSimilarityDTHR(int radius) throws Exception {
		super("query");
		this.radius = radius;
	}
	
	public BRISKGroupSimilarityDTHR() throws Exception {
		super("query");
		radius = DEFAULT_RADIUS_RANGE;
	}
	
	public BRISKGroupSimilarityDTHR( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("BRISKGroupSimilarity.radius");
		if ( value!=null) {
			radius = Integer.parseInt(value);
		} else {
			radius = DEFAULT_RADIUS_RANGE;
		}
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
		
		switch (option) {
			case optFt1:	sim = BRISKGroup.getRangePercMatches( g1, g2, radius ); break;
			case optFt2:	sim = BRISKGroup.getRangePercMatches( g2, g1, radius ); break;
			case optAvg:	sim = (BRISKGroup.getRangePercMatches( g1, g2, radius )+BRISKGroup.getRangePercMatches( g2, g1, radius ))/2.0;	break;
			// min distance corresponds to max similarity and viceversa
			case optMin:	sim = Math.max(BRISKGroup.getRangePercMatches( g1, g2, radius ), BRISKGroup.getRangePercMatches( g2, g1, radius )); break;
			case optMax:	sim = Math.min(BRISKGroup.getRangePercMatches( g1, g2, radius ), BRISKGroup.getRangePercMatches( g2, g1, radius )); break;	
			default: 	break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}	
	
	public String toString() {
		return super.toString() + " radius=" + radius + " ";
	}
}
