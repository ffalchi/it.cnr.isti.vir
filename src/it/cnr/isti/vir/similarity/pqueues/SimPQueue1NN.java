package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

public class SimPQueue1NN<O> extends SimilarityPQueue<O>  {

//	double excDistance = Double.MAX_VALUE;
	O object = null;

	
//	@Override
//	public double getExcDistance() {
//		if ( object == null ) return -Double.MAX_VALUE;
//		return distance;
//	}
	
	public void reset() {
		excDistance = Double.MAX_VALUE;
		object = null;
	}

	@Override
	public final Integer getK() {
		return 1;
	}

	@Override
	public final Double getLastDist() {
		if ( object == null ) return null;
		return excDistance;
	}	

	@Override
	public Double getRange() {
		return null;
	}

	@Override
	public ISimilarityResults getResults() {
		return new SimilarityResults(new ObjectWithDistance<O>(object, excDistance));
	}


	@Override
	public final void offer(O obj, double dist) {
		if ( dist < excDistance ) {
			object = obj;
			excDistance = dist;
		}
	}

	@Override
	public final int size() {
		if ( object == null ) return 0;
		return 1;
	}

	@Override
	public ISimilarityResults getResultsAndEmpty() {
		ISimilarityResults res = getResults();
		object = null;
		excDistance = Double.MAX_VALUE;
		return res;
	}

	@Override
	public O getFirstObject() {
		return object;
	}
	
}
