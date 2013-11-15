package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.ParallelOptions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

public class FeaturesCollectorsArchiveSearch_multiSim extends FeaturesCollectorsArchiveSearch {

	public FeaturesCollectorsArchiveSearch_multiSim(
			FeaturesCollectorsArchive archive) {
		super(archive);
	}

	// For kNN multiple sim searching
	class kNNThread_multiSim implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final SimPQueueArr[][] knn;
		private final boolean onlyID;
		private final ISimilarity[] sim;
		private final AbstractFeaturesCollector[] q;

		kNNThread_multiSim(AbstractFeaturesCollector[] q, ISimilarity[] sim, SimPQueueArr[][] knn, int from, int to, AbstractFeaturesCollector[]  objs, boolean onlyID) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.knn = knn;
			this.onlyID = onlyID;
			this.sim = sim; 
			this.q = q;
		}

		@Override
		public void run() {
			// each query is processed on an independent thread
			for (int iQ = from; iQ<=to; iQ++) {
				//System.out.println(iQ);
				for ( AbstractFeaturesCollector obj : objs ) {
					for ( int iS=0; iS<sim.length;iS++) {
						double dist = sim[iS].distance(q[iQ], obj, knn[iS][iQ].excDistance );
						if ( dist >= 0) {
							if ( onlyID)
								knn[iS][iQ].offer(((IHasID) obj).getID(), dist);
							else 
								knn[iS][iQ].offer(obj, dist);
						}
					}
				}
			}
		}
	}
	
	public synchronized void getKNNs(AbstractFeaturesCollector[] qObj, SimPQueueArr[][] kNNQueue, final ISimilarity[] sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {


		final int parallelBatchSize = 100000;
		
		int nObj = archive.size();
		Iterator<AbstractFeaturesCollector> it = archive.iterator();
		
		// iterates through multiple batches
		for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
			int batchSize = parallelBatchSize;
			if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
			AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
			
			// reading objects in batch
			for ( int i=0; i<objects.length; i++  ) {
				objects[i] = it.next();
				iObj++;
			}
			
			// kNNQueues are performed in parallels
			final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue[0].length / ParallelOptions.nThreads);
			final int nThread = (int) Math.ceil((double) kNNQueue[0].length / nQueriesPerThread);
			int ti = 0;
	        Thread[] thread = new Thread[nThread];
	        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
	        	int to = from+nQueriesPerThread-1;
	        	if ( to >= qObj.length ) to =qObj.length-1;
	        	thread[ti] = new Thread( new kNNThread_multiSim(qObj, sim, kNNQueue, from,to,objects, onlyID) ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        for ( Thread t : thread ) {
        		if ( t != null ) t.join();
	        }
			
			Log.info((iObj+parallelBatchSize) + "/" + nObj);
		}
				
	}
	
	
}
