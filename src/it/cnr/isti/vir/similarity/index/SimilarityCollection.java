/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
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
	
	public void add(AbstractFeaturesCollector obj) {
		if ( coll == null ) coll = new ArrayList();
		coll.add(obj);
	}

	@Override
	public synchronized ISimilarityResults getKNNResults(AbstractFeaturesCollector qObj, int k) {
		//KNNObjects knn = new KNNObjects(qObj, k, sim);
		KNNPQueue knn = 	new KNNPQueue(	new SimPQueueDMax(k),sim, qObj );
		knn.offerAll(coll);
		return knn.getResults();
	}

	public synchronized ISimilarityResults[] getKNNResults(AbstractFeaturesCollector[] qObj, int k) {
		ISimilarityResults[] res = new ISimilarityResults[qObj.length];
		for ( int i=0; i<res.length; i++) {
			KNNPQueue knn = 	new KNNPQueue(	new SimPQueueDMax(k),sim, qObj[i] );
			knn.offerAll(coll);
			res[i] = knn.getResults();
		}
		return res;
	}
	
	
	public String toString() {
		return this.getClass() + "\n   similarity: " + sim;
	}
	
	public int size() {
		return coll.size();
	}
}
