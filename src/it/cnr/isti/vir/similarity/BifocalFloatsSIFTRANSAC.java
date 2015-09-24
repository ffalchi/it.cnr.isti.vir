package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.util.Properties;

public class BifocalFloatsSIFTRANSAC  extends AbstractRANSAC<SIFTGroup> {

	protected static final FeatureClassCollector reqFeatures = new FeatureClassCollector(SIFTGroup.class, Floats.class );
	
	protected double lfExcDist;
	
	protected double floatsExcDist;
	
	@Override
	public Class getRequestedGroup() {
		return SIFTGroup.class;
	}
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	public BifocalFloatsSIFTRANSAC() {
		super();
	}
	
	public BifocalFloatsSIFTRANSAC( Properties properties ) throws SimilarityOptionException {
		super(properties);
		
		if ( maxFDist != Double.MAX_VALUE ) {
			// Because of UBytes and L2 Norm of SIFT Vectors
			// TO DO !!!! Wrong!!! setMaxFDist( maxFDist * 255 );
			setMaxFDist( maxFDist * ( 256 * Math.sqrt(128) * 2 ));
		}
		
		try {
			floatsExcDist 	 = PropertiesUtils.getDouble(properties, "Bifocal.GlobalExcDist");
			lfExcDist 		 = PropertiesUtils.getDouble(properties, "Bifocal.LFExcDist");
		} catch (Exception e) {
			throw new SimilarityOptionException(e.getMessage());
		}
		
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
		
		double globalDist = L2.get(f1.getFeature(Floats.class), f2.getFeature(Floats.class)) ;
		//if ( globalDist >= 0.7 ) return 1.0;
		
		double maxLFDist = ( 1.0 - globalDist / floatsExcDist ) * lfExcDist;
		//if ( maxLFDist < 0.01 ) return 1.0;
		maxLFDist *=  SIFTGroupSimilarity_RANSAC.getL2NormFactor();
		
		//double maxLFDist = 0.03 * SIFTGroupSimilarity_RANSAC.getL2NormFactor();
		
		int sqMaxLFDist_int = (int) Math.floor(maxLFDist*maxLFDist);
		
		SIFTGroup g1 = f1.getFeature(SIFTGroup.class);
		SIFTGroup g2 = f2.getFeature(SIFTGroup.class);
		return distance(g1, g2, L2NNMatcher.getSIFT(g1, g2, sqMaxLFDist_int ));
		
	}
	
	@Override
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		return distance(f1, f2);
		//return distance((SIFTGroup) f1.getFeature(SIFTGroup.class), (SIFTGroup) f2.getFeature(SIFTGroup.class));
	}

	@Override
	public final LocalFeaturesMatches getMatches( SIFTGroup g1,  SIFTGroup g2 ) {
//		if ( loweThr >= 1.0 )
//			return L2NNMatcher.getSIFT(g1, g2, sqMaxFDist_int);
//		else if ( sqMaxFDist_int == Integer.MAX_VALUE ) 
//			return L2NNLoweMatcher.getMatchesSIFT( g1, g2, sqLoweThr );
//		
//		// both parameters are used
//		return L2NNLoweMatcher.getMatchesSIFT( g1, g2, sqLoweThr, sqMaxFDist_int );
		return null;
	}
			
}
