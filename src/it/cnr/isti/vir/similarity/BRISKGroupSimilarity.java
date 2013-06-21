package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.BRISKGroup;

import java.util.Properties;

public class BRISKGroupSimilarity  extends IGroupSimilarity<BRISKGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BRISKGroup.class);
	
	
	private final double sqrLoweThr;
	
	
	public BRISKGroupSimilarity( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("loweThr");
		if ( value != null )
			sqrLoweThr = Double.parseDouble(value);
		else 
			sqrLoweThr = 0.8*0.8;
	}
	
	
	public BRISKGroupSimilarity() throws Exception {
		super("query");
		this.sqrLoweThr = 0.8*0.8;
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
			case optFt1:	sim = BRISKGroup.getLowePercMatches( g1, g2, sqrLoweThr ); break;
			case optFt2:	sim = BRISKGroup.getLowePercMatches( g2, g1, sqrLoweThr ); break;
			case optAvg:	sim = BRISKGroup.getLowePercMatches( g1, g2, sqrLoweThr )+BRISKGroup.getLowePercMatches( g2, g1, sqrLoweThr )/2.0;	break;
			// min distance corresponds to max similarity and viceversa
			case optMin:	sim = Math.max(BRISKGroup.getLowePercMatches( g1, g2, sqrLoweThr ), BRISKGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;
			case optMax:	sim = Math.min(BRISKGroup.getLowePercMatches( g1, g2, sqrLoweThr ), BRISKGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;	
			default: 		break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}

	public String toString() {
		return super.toString() + " sqrLoweThr=" + sqrLoweThr + " ";
	}
}
