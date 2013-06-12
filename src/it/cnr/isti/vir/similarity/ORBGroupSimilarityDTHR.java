package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.ORBGroup;

import java.util.Properties;

public class ORBGroupSimilarityDTHR  extends IGroupSimilarity<ORBGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ORBGroup.class);
	
	private final int radius;
	
	private final int DEFAULT_RADIUS_RANGE = 35;
		
	public ORBGroupSimilarityDTHR(int radius) throws Exception {
		super("query");
		this.radius = radius;
	}
	
	public ORBGroupSimilarityDTHR() throws Exception {
		super("query");
		radius = DEFAULT_RADIUS_RANGE;
	}
	
	public ORBGroupSimilarityDTHR( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("ORBGroupSimilarity.radius");
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
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((ORBGroup) f1.getFeature(ORBGroup.class), (ORBGroup) f2.getFeature(ORBGroup.class));
	}
	
	@Override
	public final double distance( ORBGroup g1,  ORBGroup g2) {
		double sim = 0;
		
		switch (option) {
			case optFt1:	sim = ORBGroup.getRangePercMatches( g1, g2, radius ); break;
			case optFt2:	sim = ORBGroup.getRangePercMatches( g2, g1, radius ); break;
			case optAvg:	sim = (ORBGroup.getRangePercMatches( g1, g2, radius )+ORBGroup.getRangePercMatches( g2, g1, radius ))/2.0;	break;
			// min distance corresponds to max similarity and viceversa
			case optMin:	sim = Math.max(ORBGroup.getRangePercMatches( g1, g2, radius ), ORBGroup.getRangePercMatches( g2, g1, radius )); break;
			case optMax:	sim = Math.min(ORBGroup.getRangePercMatches( g1, g2, radius ), ORBGroup.getRangePercMatches( g2, g1, radius )); break;	
			default: 	break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}	
	
	public String toString() {
		return super.toString() + " radius=" + radius + " ";
	}
}
