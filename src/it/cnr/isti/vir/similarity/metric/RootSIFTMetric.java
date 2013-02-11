package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;

import java.util.Collection;
import java.util.Properties;

public class RootSIFTMetric implements Metric<RootSIFT>, ILocalFeaturesMetric<RootSIFT>, IMeanEvaluator<RootSIFT> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(RootSIFT.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public RootSIFTMetric(Properties properties) {
		
	}
	
	public RootSIFTMetric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public final Class getRequestedFeatureClass() {
		return RootSIFT.class;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class), max);
	}
	
	@Override
	public final double distance(RootSIFT f1, RootSIFT f2) {
		return RootSIFT.getDistance_Norm( f1, f2 );	
	}
	
	@Override
	public final double distance(RootSIFT f1, RootSIFT f2, double max) {
		distCount++;
		return RootSIFT.getDistance_Norm( f1, f2, max);
	}
//
//	@Override
//	public final double distance(IFeature f1, IFeature f2) {
//		double dist = RootSIFT.getDistance_Norm(((RootSIFT) f1.getFeature(RootSIFT.class)), ((RootSIFT) f2.getFeature(RootSIFT.class)));		
//		distCount++;
//		
//		return dist;
//	}
//	
//	@Override
//	public final double distance(IFeature f1, IFeature f2, double max) {
//		double dist = RootSIFT.getDistance_Norm((RootSIFT) f1.getFeature(RootSIFT.class), (RootSIFT) f2.getFeature(RootSIFT.class), max);		
//		distCount++;
//		
//		return dist;
//	}
	
	@Override
	public RootSIFT getMean(Collection<RootSIFT> coll) {
		return RootSIFT.getMean(coll);
	}
	
	@Override
	public final Class getRequestedFeatureGroupClass() {
		return RootSIFTGroup.class;
	}
	
}
