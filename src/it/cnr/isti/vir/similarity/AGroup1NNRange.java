package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;

import java.util.Properties;

public abstract class AGroup1NNRange<F extends ALocalFeaturesGroup> extends AGroupSimilarity<F>  {
	
	public AGroup1NNRange( Properties properties ) throws SimilarityOptionException {
		super(properties);
	}
	
}
