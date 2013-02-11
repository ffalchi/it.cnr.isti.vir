package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.DominantColor;

public class DominantColorSimilarity   implements ISimilarity<DominantColor> {
	
	private static long distCount = 0;
	private static final Class reqFeature = DominantColor.class;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(reqFeature);
	
	public final long getDistCount() {
		return distCount;
	}

	public static final double norm = 1.0; ///10200.0; //DC
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((DominantColor) f1.getFeature(DominantColor.class), (DominantColor) f2.getFeature(DominantColor.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((DominantColor) f1.getFeature(DominantColor.class), (DominantColor) f2.getFeature(DominantColor.class), max);
	}
	
	public final double distance(DominantColor d1, DominantColor d2, double max ) {
		return distance(d1, d2);
	}
	
	public final double distance(DominantColor d1, DominantColor d2 ) {
		double dist = 0;

		dist += norm * DominantColor.mpeg7XMDistance( d1, d2 );
		distCount++;
		
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}
	
}
