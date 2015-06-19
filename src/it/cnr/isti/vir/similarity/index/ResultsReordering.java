package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.knn.KNNPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.Iterator;

// For kNN searching
class reordThread implements Runnable {
	private final AbstractFeaturesCollector q;
	private final Iterator it;
	private final KNNPQueue knn;
	private final ISimilarity sim;
	private final FeaturesCollectorsArchive fca;
	private final double actualMinDist;
	private final int actualK;
	private final boolean onlyID;
	
	reordThread(
			AbstractFeaturesCollector q,
			Iterator it,
			KNNPQueue knn,
			ISimilarity sim,
			FeaturesCollectorsArchive fca,
			double actualMinDist,
			int actualK,
			boolean onlyID
			) {

		this.it = it;
		this.knn = knn;
		this.sim = sim; 
		this.q = q;
		this.fca = fca;
		this.actualMinDist = actualMinDist;
		this.actualK = actualK;
		this.onlyID = onlyID;
	}

	@Override
	public void run() {
		int count =0;
		while ( true ) {
			AbstractFeaturesCollector fc = null;
			Object curr = null;
			synchronized (it) {
				if ( !it.hasNext() )
					return;
				curr = it.next();				
			}
			AbstractID currID = ((IHasID) ((ObjectWithDistance)curr).obj).getID();
			try {
				fc = fca.get(currID);
			} catch (ArchiveException e) {
				e.printStackTrace();
				return;
			}
			double dist = sim.distance(q, fc);
			if ( dist < actualMinDist ) {
				if ( onlyID ) knn.offer(((IHasID) fc).getID(), dist);
				else knn.offer(fc, dist);
			}
			if ( dist < 1.0 ) count++;
			if ( count >= actualK) {
				break;
			}
		}
	}
}

public class ResultsReordering {

	
	public static ISimilarityResults reorder(
			ISimilarityResults res,
			FeaturesCollectorsArchive fca,
			ISimilarity sim,
			boolean onlyID ) throws ArchiveException, InterruptedException {
		return reorder(res, fca, sim, 1.0, null, null, onlyID );
	}	
	
	/**
	 * @param res
	 * @param fca
	 * @param sim
	 * @param maxNResults
	 * @return
	 * @throws ArchiveException
	 * @throws InterruptedException 
	 */
	public static ISimilarityResults reorder(
			ISimilarityResults res,
			FeaturesCollectorsArchive fca,
			ISimilarity sim,
			Integer maxNResults,
			boolean onlyID) throws ArchiveException, InterruptedException {
		return reorder(res, fca, sim, 1.0, maxNResults, null, onlyID );
	}
	
	


	/**
	 * @param res
	 * @param fca
	 * @param sim
	 * @param minDistance
	 * @param maxNResults
	 * @param exitDistance
	 * @param otherAddingDist distance to be added to not reordered results
	 * @return
	 * @throws ArchiveException
	 * @throws InterruptedException 
	 */
	public static ISimilarityResults reorder(
			ISimilarityResults res,
			FeaturesCollectorsArchive fca,
			ISimilarity sim,
			Double minDistance,
			Integer maxNResults,
			Double exitDistance,
			boolean onlyID
			) throws ArchiveException, InterruptedException {
		
		int count = 0;
		
		int actualK = res.size();
		if ( maxNResults != null ) actualK = Math.min(actualK, maxNResults);
		
		AbstractFeaturesCollector query = (AbstractFeaturesCollector) res.getQuery();
		
		double actualMinDist = Double.MAX_VALUE;
		if ( minDistance != null ) actualMinDist = minDistance;
		SimPQueueArr pQueue = new SimPQueueArr(actualK);
		KNNPQueue knn = new KNNPQueue(pQueue, sim, query);
		
		int nThread = ParallelOptions.reserveNFreeProcessors()+1;
		Thread[] thread = new Thread[nThread];
		Iterator it = res.iterator();
		for ( int ti=0; ti<thread.length; ti++ ) {
			thread[ti] = new Thread( new reordThread(query, it, knn, sim, fca, actualMinDist, actualK, onlyID) ) ;
        	thread[ti].start();
		}
        for ( Thread t : thread ) {
        	t.join();
        }
        ParallelOptions.free(nThread-1);
		
//		// ORB Reordering
//		for ( Object curr : res ) {
//			AbstractID currID = ((IHasID) ((ObjectWithDistance)curr).obj).getID();
//			AbstractFeaturesCollector fc = fca.get(currID);
//			if ( fc == null ) throw new ArchiveException("Object " + currID + " was not found in  " + fca.getfile().getAbsolutePath());
//			double dist = sim.distance(query, fc);
//			if ( dist < actualMinDist ) knn.offer(fc, dist);
//			if ( dist < 1.0 ) count++;
//			if ( count >= actualK) {
//				break;
//			}
//		}
		
		return knn.getResults();
	}
	
}
