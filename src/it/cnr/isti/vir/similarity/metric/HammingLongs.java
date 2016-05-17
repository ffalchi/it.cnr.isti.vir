package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.clustering.IMeanEvaluator;
import it.cnr.isti.vir.distance.Hamming;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.BinaryLongs;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.util.bytes.LongBinaryUtil;

import java.util.Collection;
import java.util.Properties;

public class HammingLongs  implements IMetric<BinaryLongs>, IMeanEvaluator<BinaryLongs> {

	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BinaryLongs.class);
	
	public final long getDistCount() {
		return distCount;
	}
	
	public HammingLongs(Properties properties) {

	}
	
	public HammingLongs() {
		
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
		return distance( f1.getFeature(BinaryLongs.class), f2.getFeature(BinaryLongs.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance( f1.getFeature(BinaryLongs.class), f2.getFeature(BinaryLongs.class), max);
	}
	
	@Override
	public final double distance(BinaryLongs f1, BinaryLongs f2) {
		return Hamming.distance(f1.getValues(), f2.getValues() );	
	}
	
	@Override
	public final double distance(BinaryLongs f1, BinaryLongs f2, double max) {
		return Hamming.distance(f1.getValues(), f2.getValues(), (int) Math.ceil(max) );
	}
	
	public String getStatsString() { return ""; };
	
	@Override
	public BinaryLongs getMean(Collection<BinaryLongs> coll) {
		if ( coll.size() == 0 ) return null;
		return new BinaryLongs(LongBinaryUtil.getMean( coll) );
	}
}
