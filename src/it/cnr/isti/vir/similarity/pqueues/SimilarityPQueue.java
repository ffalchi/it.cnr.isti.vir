package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.util.HashSet;

public abstract class SimilarityPQueue<E> {
	
//	public abstract SimilarityPQueue<E> getExcluding(HashSet<ID> excHashSet);
	
	public double excDistance = Double.MAX_VALUE;
	
	public boolean isFull() {
		return excDistance != Double.MAX_VALUE;
	}
	
	public abstract Double getRange();
	
	public abstract Integer getK();
	
	public abstract void offer (E obj, double dist);
	
	public abstract int size();

	public abstract ISimilarityResults getResults();

	//public abstract boolean isFull();
	
	public abstract Double getLastDist();

	public abstract ISimilarityResults getResultsAndEmpty();
	
	public abstract E getFirstObject();
	
	
	
}
