package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.ORBGroup;
import it.cnr.isti.vir.features.localfeatures.SURFGroup;

import java.util.Properties;

public class ORBGroupSimilarity  extends IGroupSimilarity<ORBGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ORBGroup.class);
	
	
	private final double sqrLoweThr;
	
	
	public ORBGroupSimilarity( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("loweThr");
		if ( value != null )
			sqrLoweThr = Double.parseDouble(value);
		else 
			sqrLoweThr = 0.8*0.8;
	}
	
	
	public ORBGroupSimilarity() throws Exception {
		super("query");
		this.sqrLoweThr = 0.8*0.8;
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
		case optFt1:	sim = ORBGroup.getLowePercMatches( g1, g2, sqrLoweThr ); break;
		case optFt2:	sim = ORBGroup.getLowePercMatches( g2, g1, sqrLoweThr ); break;
		case optAvg:	sim = ORBGroup.getLowePercMatches( g1, g2, sqrLoweThr )+ORBGroup.getLowePercMatches( g2, g1, sqrLoweThr )/2.0;	break;
		// min distance corresponds to max similarity and viceversa
		case optMin:	sim = Math.max(ORBGroup.getLowePercMatches( g1, g2, sqrLoweThr ), ORBGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;
		case optMax:	sim = Math.min(ORBGroup.getLowePercMatches( g1, g2, sqrLoweThr ), ORBGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;	
		default: 	break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}	
	
	public String toString() {
		return super.toString() + " sqrLoweThr=" + sqrLoweThr + " ";
	}
}
