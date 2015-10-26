package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.CNNFloats;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;

import java.util.Properties;

public class CNNFloatsAndFloatsWL2Metric  implements IMetric<AbstractFeaturesCollector> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(Floats.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public CNNFloatsAndFloatsWL2Metric(Properties properties) {
		
	}
	
	public CNNFloatsAndFloatsWL2Metric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		double d1 = L2.get( f1.getFeature(CNNFloats.class).values, f2.getFeature(CNNFloats.class).values);
		double d2 = L2.get( f1.getFeature(Floats.class).values, f2.getFeature(Floats.class).values);
		return d1 + d2;
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		double d1 = L2.get( f1.getFeature(CNNFloats.class).values, f2.getFeature(CNNFloats.class).values, max);
		double d2 = L2.get( f1.getFeature(Floats.class).getValues(), f2.getFeature(Floats.class).getValues(), max);
		return d1 + d2;
	}

	
	public String getStatsString() { return ""; };
	
}
