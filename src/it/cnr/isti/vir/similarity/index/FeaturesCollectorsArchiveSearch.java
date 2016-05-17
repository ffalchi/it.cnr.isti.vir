package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.pqueues.AbstractSimPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.TimeManager;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class FeaturesCollectorsArchiveSearch  implements IkNNExecuter {
	
	protected final FeaturesCollectorsArchive archive;
	
	public FeaturesCollectorsArchiveSearch(FeaturesCollectorsArchive archive) {
		this.archive = archive;
	}
	
	public FeaturesCollectorsArchiveSearch(File archiveFile) throws Exception {
		archive = FeaturesCollectorsArchive.open(archiveFile, false);
	}
	
	public synchronized ISimilarityResults<IHasID>[] getKNN_IDs(
			AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim)
			throws SecurityException, IllegalArgumentException, IOException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {
		return getKNN(qObj, k, sim, true);
	}

	public synchronized ISimilarityResults[] getKNN(
			AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim)
			throws SecurityException, IllegalArgumentException, IOException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {
		return getKNN(qObj, k, sim, false);
	}
	
	public synchronized ISimilarityResults<IHasID> getKNN_IDs(AbstractFeaturesCollector qObj, int k,
			final ISimilarity sim ) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		AbstractFeaturesCollector[] qObjs = {qObj};
		return getKNN( qObjs, k, sim, true)[0];
	}
	
	public synchronized ISimilarityResults getKNN(AbstractFeaturesCollector qObj, int k,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		AbstractFeaturesCollector[] qObjs = {qObj};
		return getKNN( qObjs, k, sim, onlyID)[0];
	}
	
	private synchronized ISimilarityResults getKNN(AbstractFeaturesCollector qObj, int k,
			final ISimilarity sim ) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		AbstractFeaturesCollector[] qObjs = {qObj};
		return getKNN( qObjs, k, sim, true)[0];
	}
	
	public SimilarityResults[] getKNN(
			AbstractFeaturesCollector[] qObj, int k,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		SimPQueueDMax[] kNNQueue = new SimPQueueDMax[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueDMax(k);
		}

		search(qObj, kNNQueue, sim, onlyID);

		SimilarityResults[] res = new SimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();

		}

		return res;
	}
	
	public SimilarityResults[] getRange(
			AbstractFeaturesCollector[] qObj, double range,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		SimPQueueDMax[] pQueue = new SimPQueueDMax[qObj.length];
		for (int i = 0; i < pQueue.length; i++) {
			pQueue[i] = new SimPQueueDMax(range);
		}

		search(qObj, pQueue, sim, onlyID);

		SimilarityResults[] res = new SimilarityResults[pQueue.length];
		for (int i = 0; i < pQueue.length; i++) {
			res[i] = pQueue[i].getResults();

		}

		return res;
	}
	
	
	public class pQueueThread implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final AbstractSimPQueue[] knn;
		private final boolean onlyID;
		private final ISimilarity sim;
		private final AbstractFeaturesCollector[] q;
		private final TimeManager tm;
		
		pQueueThread(
				AbstractFeaturesCollector[] q,
				ISimilarity sim,
				AbstractSimPQueue[] knn,
				Iterator<AbstractFeaturesCollector> it,
				boolean onlyID,
				TimeManager tm
				) {

			this.it = it;
			this.knn = knn;
			this.onlyID = onlyID;
			this.sim = sim; 
			this.q = q;
			this.tm = tm;
		}

		@Override
		public void run() {
			// each query is processed on an independent thread
			while ( true) {
				AbstractFeaturesCollector obj = null;
				synchronized ( it ) {
					if ( it.hasNext() ) {
						obj = it.next();
						tm.reportProgress();
					} else {
						return;
					}
				}
				for (int iQ = 0; iQ<q.length; iQ++) {
				
					double dist = sim.distance(q[iQ], obj, knn[iQ].excDistance );
					if ( dist >= 0) {
						if ( onlyID)
							knn[iQ].offer(obj.getID(), dist);
						else 
							knn[iQ].offer(obj, dist);
					}
				
				}
			}
		}
	}
	
	public synchronized void search(
			Collection<AbstractFeaturesCollector> qObj,
			AbstractSimPQueue[] kNNQueue,
			final ISimilarity sim,
			final boolean onlyID) 
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {
	
		search(
				qObj.toArray(new AbstractFeaturesCollector[qObj.size()]),
				kNNQueue,
				sim,
				onlyID);
	}
	
	public synchronized void search(
			AbstractFeaturesCollector[] qObj,
			AbstractSimPQueue[] kNNQueue,
			final ISimilarity sim,
			final boolean onlyID) 
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {
		
		synchronized (archive) {
			
			boolean parallel = true;
			if (!parallel) {
				int iObj = 0;
				for (AbstractFeaturesCollector fc : archive) {
	
					for (int i = 0; i < kNNQueue.length; i++) {
						double dist = sim.distance(qObj[i], fc,  kNNQueue[i].excDistance );
						if ( dist < 0 ) continue;
						if ( onlyID)
							kNNQueue[i].offer(((IHasID) fc).getID(), dist );
						else 
							kNNQueue[i].offer(fc, dist);
					}
	
					if ( (iObj+1) % 100 == 0 ) {
						System.out.println( (iObj+1) + " of " + archive.size() + " processed.");
					}
					iObj++;
				}				
			
			} else {
				
				Iterator<AbstractFeaturesCollector> it = archive.iterator();
				TimeManager tm = new TimeManager();
				tm.setTotNEle(archive.size());
				int nThread = ParallelOptions.reserveNFreeProcessors()+1;
				Thread[] thread = new Thread[nThread];
				for ( int ti=0; ti<thread.length; ti++ ) {
					thread[ti] = new Thread( new pQueueThread(qObj, sim, kNNQueue, it, onlyID, tm) ) ;
		        	thread[ti].start();
				}
				
		        for ( Thread t : thread ) {
		        	t.join();
		        }
		        ParallelOptions.free(nThread-1);
			}
		}
	}
	
	public synchronized void saveKNN(
			AbstractFeaturesCollector[] qObj,
			ISimilarity sim,
			int k,
			int batchNObjs,
			String outFNPrefix,
			int[] nObjs)
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, InterruptedException {

		String sep = ",";
		
		SimPQueueDMax[] kNNQueue = new SimPQueueDMax[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueDMax(k);
		}
		
		int[] ordNObjs = nObjs.clone();
		Arrays.sort(ordNObjs);
		int iNObjs = 0;
		
		synchronized (archive) {
			
			int nObj = archive.size();
					
			Iterator<AbstractFeaturesCollector> it = archive.iterator();
			
			
			
			// iterates through multiple batches
			for (int iObj = 0; iObj < nObj;  ) {
				
				int batchSize = batchNObjs;
				
				if ( iObj + batchNObjs > nObj ) batchSize = nObj-iObj;
				if ( iObj + batchNObjs > ordNObjs[iNObjs] ) batchSize = ordNObjs[iNObjs]-iObj;
				
				AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
				
				// reading objects in batch
				for ( int i=0; i<objects.length; i++  ) {
					objects[i] = it.next();
					iObj++;
				}
				
				TimeManager tm = new TimeManager();
				tm.setTotNEle(nObj);
				
				
				// kNNQueues are performed in parallels
				Iterator<AbstractFeaturesCollector> itObj = Arrays.asList(objects).iterator();
				int nThread = ParallelOptions.reserveNFreeProcessors();
				Thread[] thread = new Thread[nThread];
				for ( int ti=0; ti<thread.length; ti++ ) {
					thread[ti] = new Thread( new pQueueThread(qObj, sim, kNNQueue, it, true, tm) ) ;
		        	thread[ti].start();
				}
		        for ( Thread t : thread ) {
		        	t.join();
		        }
		        ParallelOptions.free(nThread);
//				int bnt = ParallelOptions.reserveNFreeProcessors();
//				final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / (bnt+1) );
//				final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
//				int ti = 0;
//		        Thread[] thread = new Thread[nThread];
//		        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
//		        	int to = from+nQueriesPerThread-1;
//		        	if ( to >= qObj.length ) to =qObj.length-1;
//		        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from, to, objects, true) ) ;
//		        	thread[ti].start();
//		        	ti++;
//		        }
//		        
//		        for ( Thread t : thread ) {
//	        		if ( t != null ) t.join();
//		        }
//		        ParallelOptions.free(bnt);
				Log.info(iObj + "/" + nObj);
				
				
				
				if ( iObj >= ordNObjs[iNObjs] || iObj == nObj) {
					Log.info("Saving");
					iNObjs++;					
					
					boolean binaryFlag = false;
					
					if ( binaryFlag == true ) {
						File binaryFile = new File( outFNPrefix + "_" + iObj + "n.res.dat");
						DataOutputStream binaryOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(binaryFile)));
						
						for (int i = 0; i < kNNQueue.length; i++) {
							SimilarityResults currRes = (SimilarityResults) kNNQueue[i].getResults();
							currRes.setQuery(qObj[i]);
							currRes.writeIDData(binaryOut);							
						}
						
						binaryOut.close();
					} else {
						
						File csvFile = csvFile = new File( outFNPrefix + "_" + iObj + "n.csv");
						ISimilarityResults[] res = new ISimilarityResults[kNNQueue.length];
						for (int i = 0; i < kNNQueue.length; i++) {
							res[i] = kNNQueue[i].getResults();
						}
						BufferedWriter csvOut = new BufferedWriter( new FileWriter(csvFile) );
						for ( int i=0; i<res.length; i++ ) {
							res[i].setQuery(qObj[i]);
							
							csvOut.write(""+((IHasID) qObj[i]).getID().toString() +"");
							for (Iterator<ObjectWithDistance> it2 = res[i].iterator(); it2.hasNext(); ) {
								ObjectWithDistance curr = it2.next();
								csvOut.write(sep + ((IHasID) curr.obj).getID()+ sep +curr.dist +"");
							}				
							csvOut.write("\n");
						}	
						
						csvOut.close();
					}
				}
			}
		}

	}

	@Override
	public ISimilarityResults<AbstractFeature> getKNNResults(
			AbstractFeature qObj, int k) throws Exception {
		return this.getKNNResults(qObj, k);
	}



	
}
