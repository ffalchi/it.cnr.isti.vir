package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;

import java.util.Properties;

public class BoFRANSACSimilarity extends AbstractRANSAC<BoFLFGroup> {

	protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BoFLFGroup.class);
	
	@Override
	public Class getRequestedGroup()  { return BoFLFGroup.class; } 
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public BoFRANSACSimilarity() {
		super();
	}
	
	public BoFRANSACSimilarity( Properties properties ) throws SimilarityOptionException {
		super(properties);
	}

	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((BoFLFGroup) f1.getFeature(BoFLFGroup.class), (BoFLFGroup) f2.getFeature(BoFLFGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((BoFLFGroup) f1.getFeature(BoFLFGroup.class), (BoFLFGroup) f2.getFeature(BoFLFGroup.class));
	}

	@Override
	public final LocalFeaturesMatches getMatches( BoFLFGroup g1,  BoFLFGroup g2) {
		
		return BoFLFGroup.getMatches( g1, g2 );

	}

}
