package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.localfeatures.SIFT;

import java.util.Collection;
import java.util.Properties;

public class FloatsLFL1Metric  implements IMetric<Floats>, IMeanEvaluator<Floats> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(Floats.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public FloatsLFL1Metric(Properties properties) {
		
	}
	
	public FloatsLFL1Metric() {
		
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
		return distance( f1.getFeature(Floats.class), f2.getFeature(Floats.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance( f1.getFeature(Floats.class), f2.getFeature(Floats.class), max);
	}
	
	@Override
	public final double distance(Floats f1, Floats f2) {
		return L2.get(f1.getValues(), f2.getValues());	
	}
	
	@Override
	public final double distance(Floats f1, Floats f2, double max) {
		// TODO 
		return L2.get(f1.getValues(), f2.getValues() );
	}

	@Override
	public Floats getMean(Collection<Floats> coll) {
		return Floats.getMean(coll);
	}
	
}
