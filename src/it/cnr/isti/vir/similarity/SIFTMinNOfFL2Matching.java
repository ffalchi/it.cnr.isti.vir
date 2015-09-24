package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.util.Properties;

public class SIFTMinNOfFL2Matching extends AGroupSimilarity<SIFTGroup> {

protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(SIFTGroup.class);
	
	int sqMaxFDist;

	boolean reduceBurstiness;
	
	@Override
	public Class getRequestedGroup() {
		return SIFTGroup.class;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public SIFTMinNOfFL2Matching() {
		super();
	}
	
	public SIFTMinNOfFL2Matching( Properties properties ) throws SimilarityOptionException {
		super(properties);
		
		double maxFDist;
		try {
			maxFDist = PropertiesUtils.getDouble(properties, "maxFDist");
		} catch (Exception e) {
			throw new SimilarityOptionException(e.toString());
		}
		
		reduceBurstiness = PropertiesUtils.getBoolean(properties, "reduceBurstiness", false);
		
		sqMaxFDist = (int) (maxFDist*maxFDist*getSQL2NormFactor());
		
		Log.info_verbose(this.getClass().toString() + " sqMaxFDist: " + sqMaxFDist);
	
		
	}
	
	public static final int getSQL2NormFactor() {
		return ( 256 * 256 * 128 * 2 * 2  );
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		return distance((SIFTGroup) f1.getFeature(SIFTGroup.class), (SIFTGroup) f2.getFeature(SIFTGroup.class));
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance((SIFTGroup) f1.getFeature(SIFTGroup.class), (SIFTGroup) f2.getFeature(SIFTGroup.class));
	}

	@Override
	public final double distance( SIFTGroup g1,  SIFTGroup g2) {
		if ( reduceBurstiness = true ) 
			return 1.0 - L2Matcher.getMinNMatchingFeatures_Burst(g1, g2, sqMaxFDist) / (double) g1.size();
		else
			return 1.0 - L2Matcher.getMinNMatchingFeatures(g1, g2, sqMaxFDist) / (double) g1.size();
				
	}

	@Override
	public String getStatsString() {
		return "sqMaxFDist: " + sqMaxFDist;
	}
}
