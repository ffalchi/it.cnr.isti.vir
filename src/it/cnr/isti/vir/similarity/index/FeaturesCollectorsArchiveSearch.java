package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.ParallelOptions;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;

public class FeaturesCollectorsArchiveSearch  implements IkNNExecuter {
	
	protected final FeaturesCollectorsArchive archive;
	
	public FeaturesCollectorsArchiveSearch(FeaturesCollectorsArchive archive) {
		this.archive = archive;
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
	
	public ISimilarityResults[] getKNN(
			AbstractFeaturesCollector[] qObj, int k,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, InterruptedException {
		
		SimPQueueArr[] kNNQueue = new SimPQueueArr[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueArr(k);
		}

		getKNN(qObj, kNNQueue, sim, onlyID);

		ISimilarityResults[] res = new ISimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();
		}

		return res;
	}
	
	// For kNN searching
	class kNNThread implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final SimPQueueArr[] knn;
		private final boolean onlyID;
		private final ISimilarity sim;
		private final AbstractFeaturesCollector[] q;

		kNNThread(AbstractFeaturesCollector[] q, ISimilarity sim, SimPQueueArr[] knn, int from, int to, AbstractFeaturesCollector[]  objs, boolean onlyID) {
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
					double dist = sim.distance(q[iQ], obj, knn[iQ].excDistance );
					if ( dist >= 0) {
						if ( onlyID)
							knn[iQ].offer(((IHasID) obj).getID(), dist);
						else 
							knn[iQ].offer(obj, dist);
					}
				}
			}
		}
	}
	
	public synchronized void getKNN(
			AbstractFeaturesCollector[] qObj,
			SimPQueueArr[] kNNQueue,
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
				
				int tParallelBatchSize = 10000;
				
				int nObj = archive.size();
				
				while ( tParallelBatchSize > nObj) {
					tParallelBatchSize = tParallelBatchSize / 10;
				}
				
				final int parallelBatchSize = tParallelBatchSize;
				
				Iterator<AbstractFeaturesCollector> it = archive.iterator();
				// iterates through multiple batches
				for (int iObj = 0; iObj < nObj;  ) {
					
					int batchSize = parallelBatchSize;
					if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
					AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
					
					// reading objects in batch
					for ( int i=0; i<objects.length; i++  ) {
						objects[i] = it.next();
						iObj++;
					}
					
					// kNNQueues are performed in parallels
					int bnt = ParallelOptions.getNFreeProcessors();
					final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / (bnt+1) );
					final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
					int ti = 0;
			        Thread[] thread = new Thread[nThread];
			        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
			        	int to = from+nQueriesPerThread-1;
			        	if ( to >= qObj.length ) to =qObj.length-1;
			        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from, to, objects, onlyID) ) ;
			        	thread[ti].start();
			        	ti++;
			        }
			        
			        for ( Thread t : thread ) {
		        		if ( t != null ) t.join();
			        }
			        ParallelOptions.free(bnt);
					Log.info(iObj + "/" + nObj);
				}
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
		
		SimPQueueArr[] kNNQueue = new SimPQueueArr[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueArr(k);
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
				
				// kNNQueues are performed in parallels
				int bnt = ParallelOptions.getNFreeProcessors();
				final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / (bnt+1) );
				final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
				int ti = 0;
		        Thread[] thread = new Thread[nThread];
		        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
		        	int to = from+nQueriesPerThread-1;
		        	if ( to >= qObj.length ) to =qObj.length-1;
		        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from, to, objects, true) ) ;
		        	thread[ti].start();
		        	ti++;
		        }
		        
		        for ( Thread t : thread ) {
	        		if ( t != null ) t.join();
		        }
		        ParallelOptions.free(bnt);
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
