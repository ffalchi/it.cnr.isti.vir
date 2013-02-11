package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;

public class EdgeHistogramMetric implements Metric<EdgeHistogram> {
	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(EdgeHistogram.class);
	
	public final long getDistCount() {
		return distCount;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public static final double norm = 1.0/ 68.0;
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((EdgeHistogram) f1.getFeature(EdgeHistogram.class), (EdgeHistogram) f2.getFeature(EdgeHistogram.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((EdgeHistogram) f1.getFeature(EdgeHistogram.class), (EdgeHistogram) f2.getFeature(EdgeHistogram.class), max);
	}
	
	@Override
	public final double distance(EdgeHistogram fc1, EdgeHistogram fc2, double max) {
		return distance(fc1, fc2);
	}
	
	public final double distance(EdgeHistogram f1, EdgeHistogram f2 ) {
		distCount++;
		return norm * EdgeHistogram.mpeg7XMDistance( f1, f2 );
	}
	
	public String toString() {
		return this.getClass().toString();
	}

}
