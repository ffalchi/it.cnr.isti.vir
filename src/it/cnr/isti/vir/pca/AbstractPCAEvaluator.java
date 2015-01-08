package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.features.IFloatValues;

import java.util.Collection;

public abstract class AbstractPCAEvaluator {

	
	protected abstract PrincipalComponents compute( Collection<IFloatValues> coll ) throws Exception;
	
}
