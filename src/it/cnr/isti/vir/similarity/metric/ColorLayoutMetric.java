package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;

public class ColorLayoutMetric  implements Metric<ColorLayout> {
	
	
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ColorLayout.class);
	private static long distCount = 0;
	
	public final long getDistCount() {
		return distCount;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}	
	
	public static final double norm = 1.0/300.0;  //CL
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((ColorLayout) f1.getFeature(ColorLayout.class), (ColorLayout) f2.getFeature(ColorLayout.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((ColorLayout) f1.getFeature(ColorLayout.class), (ColorLayout) f2.getFeature(ColorLayout.class), max);
	}
	
	@Override
	public final double distance(ColorLayout fc1, ColorLayout fc2, double max) {
		return distance(fc1, fc2);
	}
	
	public final double distance(ColorLayout f1, ColorLayout f2 ) {
		double dist = 0;
		
		dist += norm * ColorLayout.mpeg7XMDistance( f1 , f2 );
		distCount++;
		
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
}