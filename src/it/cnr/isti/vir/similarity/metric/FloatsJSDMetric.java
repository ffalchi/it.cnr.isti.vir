package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.distance.JensenShannonDistance;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;

import java.util.Collection;
import java.util.Properties;

public class FloatsJSDMetric  implements IMetric<Floats>, IMeanEvaluator<Floats> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(Floats.class);
	
	@Override
	public final long getDistCount() {
		return distCount;
	}
	
	public FloatsJSDMetric(Properties properties) {
		
	}
	
	public FloatsJSDMetric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance( f1.getFeature(Floats.class), f2.getFeature(Floats.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance( f1.getFeature(Floats.class), f2.getFeature(Floats.class), max);
	}
	
	@Override
	public final double distance(Floats f1, Floats f2) {
		return JensenShannonDistance.get(f1.getValues(), f2.getValues());	
	}
	
	@Override
	public final double distance(Floats f1, Floats f2, double max) {
		return JensenShannonDistance.get(f1.getValues(), f2.getValues(), max );
	}

	@Override
	public Floats getMean(Collection<Floats> coll) {
		return Floats.getMean(coll);
	}
	
	@Override
	public String getStatsString() { return ""; };
	
}
