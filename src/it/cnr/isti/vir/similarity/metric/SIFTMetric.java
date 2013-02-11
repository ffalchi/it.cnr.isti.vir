package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;

import java.util.Collection;
import java.util.Properties;

public class SIFTMetric implements Metric<SIFT>, ILocalFeaturesMetric<SIFT>, IMeanEvaluator<SIFT> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(SIFT.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public SIFTMetric(Properties properties) {
		
	}
	
	public SIFTMetric() {
		
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public final Class getRequestedFeatureClass() {
		return SIFT.class;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((SIFT) f1.getFeature(SIFT.class), (SIFT) f2.getFeature(SIFT.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((SIFT) f1.getFeature(SIFT.class), (SIFT) f2.getFeature(SIFT.class), max);
	}
	
	@Override
	public final double distance(SIFT f1, SIFT f2) {
		return SIFT.getDistance_Norm( f1, f2 );	
	}
	
	@Override
	public final double distance(SIFT f1, SIFT f2, double max) {
		distCount++;
		return SIFT.getDistance_Norm( f1, f2, max);
	}
//
//	@Override
//	public final double distance(IFeature f1, IFeature f2) {
//		double dist = SIFT.getDistance_Norm(((SIFT) f1.getFeature(SIFT.class)), ((SIFT) f2.getFeature(SIFT.class)));		
//		distCount++;
//		
//		return dist;
//	}
//	
//	@Override
//	public final double distance(IFeature f1, IFeature f2, double max) {
//		double dist = SIFT.getDistance_Norm((SIFT) f1.getFeature(SIFT.class), (SIFT) f2.getFeature(SIFT.class), max);		
//		distCount++;
//		
//		return dist;
//	}
	
	@Override
	public SIFT getMean(Collection<SIFT> coll) {
		return SIFT.getMean(coll);
	}
	
	@Override
	public final Class getRequestedFeatureGroupClass() {
		return SIFTGroup.class;
	}
	
}
