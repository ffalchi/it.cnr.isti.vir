package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.knn.KNNExecuter;
import it.cnr.isti.vir.similarity.knn.KNNPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class SimilarityCollection implements KNNExecuter {

	protected ISimilarity sim;
	protected Collection coll = null;
	
	public SimilarityCollection(ISimilarity sim) {
		this.sim = sim;
		coll = new LinkedList();
	}
	
	
	public SimilarityCollection(ISimilarity sim, Collection objCollection) {
		this.sim = sim;
		coll = objCollection;	
	}
	
	public void add(IFeaturesCollector obj) {
		if ( coll == null ) coll = new ArrayList();
		coll.add(obj);
	}

	@Override
	public synchronized ISimilarityResults getKNNResults(IFeaturesCollector qObj, int k) {
		//KNNObjects knn = new KNNObjects(qObj, k, sim);
		KNNPQueue knn = 	new KNNPQueue(	new SimPQueueDMax(k),sim, qObj );
		knn.offerAll(coll);
		return knn.getResults();
	}
	
	
	public String toString() {
		return this.getClass() + "\n   similarity: " + sim;
	}
	
	public int size() {
		return coll.size();
	}
}
