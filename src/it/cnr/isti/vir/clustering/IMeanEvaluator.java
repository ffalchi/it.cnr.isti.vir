package it.cnr.isti.vir.clustering;

import java.util.Collection;

public interface IMeanEvaluator<O> {

	public O getMean(Collection<O> coll);
	
}
