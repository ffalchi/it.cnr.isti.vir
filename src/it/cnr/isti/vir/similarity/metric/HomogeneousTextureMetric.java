package it.cnr.isti.vir.similarity.metric;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;

public class HomogeneousTextureMetric implements Metric<HomogeneousTexture> {
	
	private static long distCount = 0;
	private static final FeatureClassCollector reqFeatures = new FeatureClassCollector(HomogeneousTexture.class);
	
	public final long getDistCount() {
		return distCount;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}	
	
	// option can be "n", "r", "s", "rs", "sr"
	public final int option = HomogeneousTexture.N_OPTION;;
	
	public static final double norm = 1.0/25.0;  //HT
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2 ) {
		return distance((HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class));
	}
	
	@Override
	public final double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max ) {
		return distance((HomogeneousTexture) f1.getFeature(HomogeneousTexture.class), (HomogeneousTexture) f2.getFeature(HomogeneousTexture.class), max);
	}
	
	@Override
	public final double distance(HomogeneousTexture f1, HomogeneousTexture f2, double max) {
		return distance(f1, f2);
	}
	
	public final double distance(HomogeneousTexture f1, HomogeneousTexture f2, int option ) {
		distCount++;
		return norm * HomogeneousTexture.mpeg7XMDistance( f1, f2, option);
	}
	
	public final double distance(HomogeneousTexture n1, HomogeneousTexture n2 ) {
		distCount++;
		return norm * HomogeneousTexture.mpeg7XMDistance( n1, n2, option );
	}	
	
	public String toString() {
		return this.getClass() + " option: " + option;
	}
	
}