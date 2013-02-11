package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;

public interface ILFSimilarity<E> extends ISimilarity<E> {

	public Class<E> getRequestedFeatureClass();
	
	public Class<AbstractLFGroup> getRequestedFeatureGroupClass();
}
