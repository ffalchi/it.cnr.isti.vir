package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.LATCHGroup;

import java.util.Properties;

public class LATCHGroupSimilarity  extends AGroupSimilarity<LATCHGroup> {

	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(LATCHGroup.class);
	
	
	private final double sqrLoweThr;
	

	@Override
	public Class getRequestedGroup()  { return LATCHGroup.class; } 
	
	public LATCHGroupSimilarity( Properties properties) throws SimilarityOptionException {
		super(properties);
		String value = properties.getProperty("loweThr");
		if ( value != null )
			sqrLoweThr = Double.parseDouble(value);
		else 
			sqrLoweThr = 0.8*0.8;
	}
	
	
	public LATCHGroupSimilarity() throws Exception {
		super("query");
		this.sqrLoweThr = 0.8*0.8;
	}
	
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance(f1.getFeature(LATCHGroup.class), f2.getFeature(LATCHGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance(f1.getFeature(LATCHGroup.class), f2.getFeature(LATCHGroup.class));
	}
	
	@Override
	public final double distance( LATCHGroup g1,  LATCHGroup g2) {
		double sim = 0;
		
		switch (option) {
		case optFt1:	sim = LATCHGroup.getLowePercMatches( g1, g2, sqrLoweThr ); break;
		case optFt2:	sim = LATCHGroup.getLowePercMatches( g2, g1, sqrLoweThr ); break;
		case optAvg:	sim = LATCHGroup.getLowePercMatches( g1, g2, sqrLoweThr )+LATCHGroup.getLowePercMatches( g2, g1, sqrLoweThr )/2.0;	break;
		// min distance corresponds to max similarity and viceversa
		case optMin:	sim = Math.max(LATCHGroup.getLowePercMatches( g1, g2, sqrLoweThr ), LATCHGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;
		case optMax:	sim = Math.min(LATCHGroup.getLowePercMatches( g1, g2, sqrLoweThr ), LATCHGroup.getLowePercMatches( g2, g1, sqrLoweThr )); break;	
		default: 	break;
		}
					
		distCount++;
		
		return 1.0 - sim;
	}	
	
	@Override
	public String toString() {
		return super.toString() + " sqrLoweThr=" + sqrLoweThr + " ";
	}
	
	@Override
	public String getStatsString() { return ""; };
}
