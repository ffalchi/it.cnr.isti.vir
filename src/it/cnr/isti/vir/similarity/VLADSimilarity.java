package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.features.localfeatures.VLAD;

import java.util.Properties;

public class VLADSimilarity implements ISimilarity<VLAD> {
	
	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(VLAD.class);
	
	public VLADSimilarity() {
		
	}
	
	public VLADSimilarity( Properties properties) throws SimilarityOptionException {
		this();
	}
	
	@Override
	public double distance(VLAD f1, VLAD f2) {
		return VLAD.getDistance(f1, f2);
	}

	@Override
	public double distance(VLAD f1, VLAD f2, double max) {
		return VLAD.getDistance(f1, f2, max);
	}

	@Override
	public double distance(IFeaturesCollector f1, IFeaturesCollector f2) {
		return distance((VLAD) f1.getFeature(VLAD.class), (VLAD) f2.getFeature(VLAD.class));
	}

	@Override
	public double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max) {
		return distance((VLAD) f1.getFeature(VLAD.class), (VLAD) f2.getFeature(VLAD.class), max);
	}

	@Override
	public long getDistCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {
		return reqFeatures;
	}

}
