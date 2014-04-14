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
package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.similarity.metric.IMetric;
import it.cnr.isti.vir.similarity.pqueues.AbstractSimPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.util.ParallelOptions;
import it.cnr.isti.vir.util.Reordering;
import it.cnr.isti.vir.util.SplitInGroups;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class MultipleKNNPQueueID<F>  {
	
	private KNNPQueue<F>[] knn;
	private AbstractFeature[] qObj;
	private double[][] intDist;
	private final IMetric<F> comp;
	private final Integer k;
	private final boolean storeID;
		
	// not usable for multithread
	final double[] tempDs;
	final int[] tempI;
	
	private boolean silent = false;
	
	public ISimilarityResults getResults(int i) {
		return knn[i].getResults();
	}
	

	public ISimilarityResults[] getResults() {
		ISimilarityResults[] res = new ISimilarityResults[knn.length];
		for (int i=0; i<res.length; i++) {
			res[i] = knn[i].getResults();
		}
		return res;
	}
	
//	public MultipleKNNPQueueID(MultipleKNNPQueueID<F> given, HashSet<ID> exclusionHashSet) {
//		this.qObj = given.qObj;
//		this.comp = given.comp;
//		this.k = given.k;
//		this.storeID = given.storeID;
//		this.distET = given.distET;
//		
//		this.tempDs = null;
//		this.tempI = null;
//		
//		this.knn = new KNNPQueue[given.knn.length];
//		for ( int i=0; i<knn.length; i++ ) {
//			this.knn[i] = new KNNPQueue(given.knn[i], exclusionHashSet);
//		}
//	}
	
	public final void setSilent(boolean silent) {
		this.silent = silent;
	}

	//private final RecentsAsPivots recentsFilter;
	@SuppressWarnings("unchecked")
	
	public MultipleKNNPQueueID(	Collection queryColl,
								Integer k,
								IMetric comp,
								boolean useInterDistances,
								IQueriesOrdering ordering,
								Integer nRecents
								) {
//		this(queryColl, k, comp, useInterDistances, ordering, nRecents, false, true, SimPQueueDMax.class, false );
		this(queryColl, k, comp, useInterDistances, ordering, nRecents, true, SimPQueueDMax.class, false );
	}
	
	
	public MultipleKNNPQueueID(	Collection queryColl,
								Integer k,
								IMetric comp,
								Class pQueueClass
								) {
		
		this (			queryColl,
						k,
						comp,
						false,
						null,
						null,
//						true,
						false,
						pQueueClass,
						true );
	}
	
	public MultipleKNNPQueueID(	Collection queryColl,
								Integer k,
								IMetric comp,
								boolean useInterDistances,
								IQueriesOrdering ordering,
								Integer nRecents,
//								boolean distET,
								boolean storeID,
								Class pQueueClass,
								boolean silent) {
		
		this.storeID = storeID;
		int size = queryColl.size();
		qObj = new AbstractFeature[size];
		this.comp = comp;
		this.k = k;
		this.silent = silent;
		

		//temporary
		tempDs = new double[qObj.length];
		tempI = new int[qObj.length];
		
		int count=0;
		for (Iterator<F> it = queryColl.iterator(); it.hasNext();) {
			qObj[count++] = (AbstractFeature) it.next();
		}
		
		if ( useInterDistances == true ) {
			intDist = evalInterDistances();
		} else {
			intDist = null;
		}
		
		
		//init reordering
		if ( intDist!=null && ordering != null ) {
			reorder( ordering.getOrder(intDist));
		}
		
				
		knn = new KNNPQueue[qObj.length];
		for ( int i=0; i<knn.length; i++ ) {
			AbstractSimPQueue queue = null;
			try {
				// for SimPQueue_kNN
				queue = (AbstractSimPQueue) pQueueClass.getConstructor(int.class).newInstance((int) k);
			} catch (Exception e1) {
				try {	
					// basically for SimPQueueLowe and SimPQueueLowe2NN
					queue = (AbstractSimPQueue) pQueueClass.getConstructor().newInstance();
				} catch (Exception e2) {
					e1.printStackTrace();
					e2.printStackTrace();
				}
			}
			knn[i] = new KNNPQueue<F>(queue, comp, (F) qObj[i], storeID);
		}
		
//		if ( nRecents != null && nRecents > 0 ) {
//			recentsFilter = new RecentsAsPivots(knn, nRecents, comp);
//		} else {
//			recentsFilter = null;
//		}
		
	}
	
	public int size() {
		return knn.length;
	}

	static class OfferAll implements Runnable {
        private final int from;
        private final int to;
        private final Collection arrColl;
        private final KNNPQueue[] knn;
        
        OfferAll(KNNPQueue[] knn, int from, int to, Collection arrColl) {
            this.from = from;
            this.to = to;
            this.arrColl = arrColl;
            this.knn = knn;
        }
        
        @Override
        public void run() {
        	for ( Object obj : arrColl ) {
	            for (int i = from; i <= to; i++) {
	            	knn[i].offer( obj);
	            }
        	}
        }                
    }
	
	// Parallel on the objects
	public final void offer(Collection<F> coll ) throws InterruptedException {
		int threadN = ParallelOptions.getNFreeProcessors()+1;
        Thread[] thread = new Thread[threadN];
        int[] group = SplitInGroups.split(knn.length, thread.length);
        int from=0;
        for ( int i=0; i<group.length; i++ ) {
        	int curr=group[i];
        	if ( curr == 0 ) break;
        	int to=from+curr-1;
        	thread[i] = new Thread( new OfferAll(knn, from,to,coll) ) ;
        	thread[i].start();
        	from=to+1;
        }
        
        for ( Thread t : thread ) {
    		if ( t != null ) t.join();
        }
        ParallelOptions.free(threadN-1);
	}
	
	public final void offer(F obj) {
		offer(obj, 0, knn.length);
	}
	
	public final void offer(F obj, int min, int max) {
		
		
		if ( intDist != null ) {
			int distCount = 0;
			for ( int i=min; i<max; i++ ) {
				
				if ( intDist != null && intDistFiltered(i, tempDs, tempI, distCount) ) continue;
				
				// using max
				double dist = -1;
//				if ( distET )
					dist = comp.distance( knn[i].query, obj, knn[i].excDistance );
//				else dist = comp.distance( knn[i].query, obj);
				
				if ( dist >= 0 ) {
					tempDs[distCount] = dist;			
					tempI[distCount] = i;
				
					if ( dist < knn[i].excDistance ) knn[i].offer(obj, dist);
					distCount++;
				}
					
			}
		} else {
//			if ( distET ) {
			for ( int i=min; i<max; i++ ) {
				// using max
				double dist = comp.distance( knn[i].query, obj, knn[i].excDistance );
				if ( dist >= 0 && dist < knn[i].excDistance ) knn[i].offer(obj, dist);
			}
		} 
//			else {
//			for ( int i=min; i<max; i++ ) {
//				double dist = comp.distance( knn[i].query, obj);
//				if ( dist < knn[i].excDistance ) knn[i].offer(obj, dist);
//			}
//		}
		
//		if ( recentsFilter != null ) {
//			recentsFilter.add(obj, tempDs);
//			//recentsFilter.add(obj, tempIDDs);
//		}
	}
	
	protected final double[][] evalInterDistances() {
		double temp[][] = new double[qObj.length][];
		for ( int i=0; i<temp.length; i++ ) {
			temp[i] = new double[i];
		}
		for ( int i=0; i<temp.length; i++ ) {
			for ( int j=0; j<i; j++ ) {
				temp[i][j] = comp.distance( (F) qObj[i], (F) qObj[j]);
			}
		}
		return temp;
	}
	
	
	public final void reorder(Collection<Integer> ordList) {

		Reordering.reorder(ordList, qObj);
		Reordering.reorder(ordList, knn);
		intDist = Reordering.reorderTrMatrix(ordList, intDist);
		
	}
	
	public final double getAvgLastDist() {
		double sum = 0;
		for ( int i=0; i<knn.length; i++ ) {
			sum += knn[i].getLastDist();
		}
		return sum /(double) knn.length;
	}
	
	public final double getAvgIntDist() {
		double avg = 0;
		// i=0 is not useful
		
		if ( intDist == null ) return -1;
		int count = 0;
		for ( int i=1; i<intDist.length; i++ ) {
			double temp = 0;
			for ( int j=0; j<intDist[i].length; j++ ) {
				temp = intDist[i][j];
				avg += temp;
			}
			count += intDist[i].length;
		}
		return avg / (double) count;
	}
	
	private final boolean intDistFiltered(int id, double[] tempDs, int[] tempIDs, int set) {
		
		for ( int j=0; j<set; j++) if ( Math.abs( intDist[id][tempIDs[j]] - tempDs[j] ) > knn[id].excDistance) return true;
//			double diff =  intDist[id][tempIDs[j]] - tempDs[j];
//			if ( diff > knn[id].excDistance) return true;
//			if ( -diff > knn[id].excDistance) return true;

		return false;
	}
	
	
	public final KNNPQueue<F> getKNN( Object givenObj ) {
		
		for ( int i=0; i<knn.length; i++ ) {
			if ( knn[i].query == givenObj ) return knn[i];
		}		
		
		return null;
	}

	public void writeResultsIDs(DataOutputStream out) throws IOException {
		for ( int i=0; i<knn.length; i++ ) {
			knn[i].getResults().writeIDData(out);
		}		
	}
	
	public final KNNPQueue<F> getKNN( int i ) {
		return knn[i];
	}
	
	public KNNPQueue<F> get(int index) {
		return knn[index];
	}



	
}
