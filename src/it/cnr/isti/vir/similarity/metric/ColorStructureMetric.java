package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;

public class ColorStructureMetric  implements Metric<ColorStructure> {
	
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ColorStructure.class);
	private static long distCount = 0;
	
	public final long getDistCount() {
		return distCount;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public static final double norm = 1.0/10200.0; //CS
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((ColorStructure) f1.getFeature(ColorStructure.class), (ColorStructure) f2.getFeature(ColorStructure.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((ColorStructure) f1.getFeature(ColorStructure.class), (ColorStructure) f2.getFeature(ColorStructure.class), max);
	}
	
	public final double distance(ColorStructure f1, ColorStructure f2 ) {
		distCount++;
		return norm * ColorStructure.mpeg7XMDistance( f1, f2 );
	}
	
	@Override
	public final double distance(ColorStructure fc1, ColorStructure fc2, double max) {
		distCount++;
		return distance(fc1, fc2);
	}
	
	public String toString() {
		return this.getClass().toString();
	}
	
}
