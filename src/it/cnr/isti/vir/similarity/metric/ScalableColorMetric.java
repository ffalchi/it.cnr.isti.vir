package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;

public class ScalableColorMetric  implements Metric<ScalableColor> {
	
	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(ScalableColor.class);
	
	public final long getDistCount() {
		return distCount;
	}

	public static final double norm = 1.0/3000.0;  //SC
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((ScalableColor) f1.getFeature(ScalableColor.class), (ScalableColor) f2.getFeature(ScalableColor.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((ScalableColor) f1.getFeature(ScalableColor.class), (ScalableColor) f2.getFeature(ScalableColor.class), max);
	}
	
	@Override
	public final double distance(ScalableColor fc1, ScalableColor fc2, double max) {
		return distance(fc1, fc2);
	}
	
//	public final double distance(ScalableColor f1, ScalableColor f2, int option ) {
//		return distance(f1,f2, 1);
//	}

	
	public final double distance(ScalableColor f1, ScalableColor f2 ) {
		distCount++;
		return norm * ScalableColor.mpeg7XMDistance( f1, f2 );
	}
	
	public String toString() {
		return this.getClass().toString();
	}
}
