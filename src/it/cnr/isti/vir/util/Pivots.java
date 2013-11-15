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
package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.metric.IMetric;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Pivots {

	
	public static final Collection<Integer> reordering(Integer triesMax, Integer nObjects, float[][] intDist ) {
		return reordering( triesMax, nObjects, intDist, intDist.length);
	}
	
	public static final Collection<Integer> reordering(Integer triesMax, Integer nObjects, float[][] intDist, int nToOrder) {
		
		int size=intDist.length;
		
		// Random reordering
		LinkedList<Integer> list = new LinkedList(RandomOperations.getRandomOrderedIntegers(size));
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(size);
		
		double[][] last = new double[size][];
		for ( int i=1; i<size; i++ ) {
			last[i] = new double[i];
		}
		
		// at each step in finding new order
		while ( orderedList.size() < nToOrder ) {
			if ( orderedList.size() % 10 == 0 ) System.out.println("--> reordering pivots cycle " + orderedList.size());
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.shuffle(list);
			
			// for each not ordered image (not more then triesMax)
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					for ( int j=0; j<i; j++) {
						double d1 = 0;
						
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr )d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;

						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr )d2 = intDist[curr][j];
						//else d2 = 0;
						
						double abs = Math.abs( d1 - d2 );
						double temp = last[i][j];
						if ( abs > temp ) currSum += abs-temp;
						//currSum += Math.max(last[i][j], Math.abs( d1 - d2));
					}
				}			
				
				if ( currSum > bestSum ) {
					best = curr;
					bestSum = currSum;
				}				
			} // next best found
			
			orderedList.add( best ); //add to orderedList
			list.remove(best); // removes from to do list
			
			// last updating using new 
			for ( int i=0; i<last.length; i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > best ) d1 = intDist[i][best];
					else if ( i < best )d1 = intDist[best][i];
					//else d1 = 0;
					
					double d2 = 0;
					if ( j > best ) d2 = intDist[j][best];
					else if ( j < best ) d2 = intDist[best][j];
					//else d2 = 0;
					
					last[i][j] = Math.max(last[i][j], Math.abs( d1 - d2 ));
				}
			}	
			
			
			//System.out.println(orderedList.size() + ":\t" +best);
			//System.out.print(".");
		}
		
		return orderedList;
		
	}
	
	public static final ArrayList<Integer> search(Integer triesMax, Integer nObjects, float[][] intDist, int nPivots) {
		
		int size=intDist.length;
		
		// Random ordering
		LinkedList<Integer> list = new LinkedList(RandomOperations.getRandomOrderedIntegers(size));
		
		ArrayList<Integer> res = new ArrayList<Integer>(size);
		
		// for interdistances
		double[][] last = new double[size][];
		for ( int i=1; i<size; i++ ) {
			last[i] = new double[i];
		}
		
		while ( res.size() < nPivots ) {
			System.out.println("--> searching pivot number " + res.size());
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.shuffle(list);
			
			// for each object not pivot
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					for ( int j=0; j<i; j++) {
						double d1 = 0;
						
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr )d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;

						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr )d2 = intDist[curr][j];
						//else d2 = 0;
						
						double abs = Math.abs( d1 - d2 );
						double temp = last[i][j];
						if ( abs > temp ) currSum += abs-temp;
						//currSum += Math.max(last[i][j], Math.abs( d1 - d2));
					}
				}			
				
				if ( currSum > bestSum ) {
					best = curr;
					bestSum = currSum;
				}				
			} // next best found
			
			res.add( best ); //add to orderedList
			list.remove(best); // removes from to do list
			
			// last updating using new 
			for ( int i=0; i<last.length; i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > best ) d1 = intDist[i][best];
					else if ( i < best )d1 = intDist[best][i];
					//else d1 = 0;
					
					double d2 = 0;
					if ( j > best ) d2 = intDist[j][best];
					else if ( j < best ) d2 = intDist[best][j];
					//else d2 = 0;
					
					last[i][j] = Math.max(last[i][j], Math.abs( d1 - d2 ));
				}
			}	
			
			
			//System.out.println(orderedList.size() + ":\t" +best);
			//System.out.print(".");
		}
		
		return res;
		
	}
	

	public static final AbstractFeaturesCollector[] search(FeaturesCollectorsArchive candidatePivots, AbstractFeaturesCollector[] testObjects, IMetric sim, int nPivots, int nTries) throws ArchiveException, SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, InterruptedException {
		return search(new FeaturesCollectorsArchives(candidatePivots), testObjects, sim, nPivots, nTries);
	}
	
	
	// For objects pivot distance evaluation
	public static class PivObjsDistances implements Runnable {
		private final AbstractFeaturesCollector piv;
		private final AbstractFeaturesCollector[] objs;
		private final int from;
		private final int to;
		private final float[] dist;
		private final IMetric sim;


		PivObjsDistances(AbstractFeaturesCollector piv, AbstractFeaturesCollector[]  objs, IMetric sim, int from, int to, float[] res) {
			this.piv = piv;
			this.objs = objs;
			this.from = from;
			this.to = to;
			this.sim = sim; 
			this.dist = res;
		}

		@Override
		public void run() {
			for (int i = from; i<=to; i++) {
				dist[i] = (float) sim.distance(piv, objs[i]);
			}
		}
	}
	
	public static final AbstractFeaturesCollector[] search(FeaturesCollectorsArchives candidatePivots, AbstractFeaturesCollector[] testObjects, IMetric sim, int nPivots, int nTries) throws ArchiveException, InterruptedException {
		
		// creating testObjects.length pairs of objects
		int[] obj1 = RandomOperations.getRandomIntArray(testObjects.length, 0, testObjects.length-1);
		int[] obj2 = RandomOperations.getRandomIntArray(testObjects.length, 0, testObjects.length-1);
		
		// distances in the pivoted space between pairs of objects
		float[] ooPDist = new float[testObjects.length];
		
		// distance between curr pivot and objects
		float[] opDist = new float[testObjects.length];
		
		// distance between best pivot and objects
		float[] bestOPDist = new float[testObjects.length];
		
		AbstractFeaturesCollector[] piv = new AbstractFeaturesCollector[nPivots];
		
		// kNNQueues are performed in parallels
		final int nQueriesPerThread = (int) Math.ceil((double) testObjects.length / ParallelOptions.nThreads);
		final int nThread = (int) Math.ceil((double) testObjects.length / nQueriesPerThread);
		
		Log.info_verbose("nPiv\tid\tgain\textRMin");
		long start = System.currentTimeMillis();
		for ( int iP=0; iP<piv.length; iP++) {
			
			AbstractFeaturesCollector best = null;
			double bestSumGain = -1;
			
			for ( int iT=0; iT<nTries; iT++) {
				//System.out.print(".");
				AbstractFeaturesCollector curr = candidatePivots.get(RandomOperations.getInt(0, candidatePivots.size()));
				double currSumGain = 0;
				
				// evaluating distances between all test objects and curr Pivots
				for ( int i=0; i<ooPDist.length; i++) {
					//opDist[i] = sim.distance(curr, testObjects[i]);
									
					int ti = 0;
			        Thread[] thread = new Thread[nThread];
			        for ( int from=0; from<testObjects.length; from+=nQueriesPerThread) {
			        	int to = from+nQueriesPerThread-1;
			        	if ( to >= testObjects.length ) to =testObjects.length-1;
			        	thread[ti] = new Thread( new PivObjsDistances(curr, testObjects, sim, from, to, opDist) ) ;
			        	thread[ti].start();
			        	ti++;
			        }
			        
			        for ( Thread t : thread ) {
		        		if ( t != null ) t.join();
			        }
					
				}
				
				for ( int i=0; i<ooPDist.length; i++) {
					double abs = Math.abs(opDist[obj1[i]]-opDist[obj2[i]]); 
					if ( abs > ooPDist[i] ) currSumGain += abs;
				}
				
				if ( currSumGain > bestSumGain ) {
					best = curr;
					bestSumGain = currSumGain;
					bestOPDist = opDist; // current is best
					opDist = bestOPDist; // will be used for next try
				}				
			}
			
			piv[iP] = best;
			double avgSum = 0.0;
			// pDist updating using new 
			for ( int i=0; i<ooPDist.length; i++ ) {
				float abs = Math.abs(bestOPDist[obj1[i]]-bestOPDist[obj2[i]]); 
				if ( abs > ooPDist[i] ) ooPDist[i] = abs;
				avgSum += ooPDist[i];
			}	
			
			long now = System.currentTimeMillis();
			
			float avgGain = (float) bestSumGain / ooPDist.length;
			// extimated time in minutes 
			long ext = (now - start) / (iP+1) * (piv.length-iP-1) / 1000 / 60;
			Log.info_verbose(iP + "\t" + ((IHasID) best).getID() +"\t"+ avgGain + "\t"+ avgSum + "\t" + ext);
		}
		
		return piv;
		
	}
	/*
	public static final IFeaturesCollector[] search_localOptimum(FeaturesCollectorsArchives candidatePivots, IFeaturesCollector[] testObjects, IMetric sim, IFeaturesCollector[] pivots) throws ArchiveException {
		
		final int nPivots = pivots.length;
		final int nTObjs = testObjects.length;
		
		// creating testObjects.length pairs of objects
		final int[] obj1 = RandomOperations.getRandomIntArray(nTObjs, 0, nTObjs-1);
		final int[] obj2 = RandomOperations.getRandomIntArray(nTObjs, 0, nTObjs-1);

		// objects-pivot are performed in parallels
		final int nQueriesPerThread = (int) Math.ceil((double) nTObjs / ParallelOptions.nThreads);
		final int nThread = (int) Math.ceil((double) nTObjs / nQueriesPerThread);
		
		// distances in the pivoted space between pairs of objects
		final float[][] ooPDist = new float[nPivots][nTObjs];
		
		final int[] bestPiv = new int[nTObjs];
		final int[] secBestPiv = new int[nTObjs];
		Arrays.fill(bestPiv, -1);
		Arrays.fill(secBestPiv, -1);
		
		Log.info("Evaluating lower bounds.");
		
		// ooPDist init
		for ( int i=0; i<nPivots; i++) {
			Log.info(i + "/" + nPivots);
			IFeaturesCollector curr = pivots[i];
			int ti = 0;
	        Thread[] thread = new Thread[nThread];
	        for ( int from=0; from<nTObjs; from+=nQueriesPerThread) {
	        	int to = from+nQueriesPerThread-1;
	        	if ( to >= nTObjs ) to = nTObjs-1;
	        	thread[ti] = new Thread( new PivObjsDistances(curr, testObjects, sim, from,to, ooPDist[i]) ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        for ( Thread t : thread ) {
        		if ( t != null ) t.join();
	        }	
		}
		
		Log.info("Evaluating best and second best pivots for each objects pair.");
		for ( int iOO; iOO<nTObjs; iOO++) {
			float bestLB = Float.NEGATIVE_INFINITY;
			float sbestLB = Float.NEGATIVE_INFINITY;
			for ( int iP; iP<nTObjs; iP++) {
				float currLowerBound = ooPDist[iP][iOO];
				if ( currLowerBound > bestLB ) {
					// better than best
					secBestPiv[iOO] = bestPiv[iOO];
					bestPiv[iOO] = iP;
					sbestLB = bestLB;
					bestLB = currLowerBound;
				} if ( currLowerBound > sbestLB ) {
					secBestPiv[iOO] = iP;
					sbestLB = currLowerBound;
				}
			}
		}
		
		
		float[] pivContr = new float[nPivots];
		
		Log.info("i\trem\tcontr\tnew\tcontr")
		// as in Bustos et al. 2003 we repeat the substitution of pivots nPivots times
		for ( int i=0; i<nPivots; i++) {
			
			// evaluating pivContributions
			for ( int iOO=0; iOO<nTObjs; iOO++) {
				int best = bestPiv[iOO];
				pivContr[best] += ooPDist[best][iOO] - ooPDist[secBestPiv[iOO]][iOO];
			}
			
			// searching victim
			int iPWorst = 0;
			float pWorstContr = Float.POSITIVE_INFINITY;
			for ( int iP=0; iP<nPivots; iP++) {
				if ( pivContr[iP] > pWorstContr) {
					iPWorst = iP;
					pWorstContr = pivContr[iP];
				}
			}
			IFeaturesCollector victim = pivots[iPWorst];
			
			// TO DO FROM HERE ON
			for ( int iT=0; iT<nTries; iT++) {
				//System.out.print(".");
				IFeaturesCollector curr = candidatePivots.get(RandomOperations.getInt(0, candidatePivots.size()));
				double currSumGain = 0;
				
				// evaluating distances between all test objects and curr Pivots
				for ( int i=0; i<ooPDist.length; i++) {
					//opDist[i] = sim.distance(curr, testObjects[i]);
									
					int ti = 0;
			        Thread[] thread = new Thread[nThread];
			        for ( int from=0; from<testObjects.length; from+=nQueriesPerThread) {
			        	int to = from+nQueriesPerThread-1;
			        	if ( to >= testObjects.length ) to =testObjects.length-1;
			        	thread[ti] = new Thread( new PivObjsDistances(curr, testObjects, sim, from,to, opDist) ) ;
			        	thread[ti].start();
			        	ti++;
			        }
			        
			        for ( Thread t : thread ) {
		        		if ( t != null ) t.join();
			        }
					
				}
				
				for ( int i=0; i<ooPDist.length; i++) {
					double abs = Math.abs(opDist[obj1[i]]-opDist[obj2[i]]); 
					if ( abs > ooPDist[i] ) currSumGain += abs;
				}
				
				if ( currSumGain > bestSumGain ) {
					best = curr;
					bestSumGain = currSumGain;
					bestOPDist = opDist; // current is best
					opDist = bestOPDist; // will be used for next try
				}				
			}
			
			
					
			Log.info(i + "\t" + ((IHasID) victim).getID() + "\t" + pWorstContr + ((IHasID) pivots[iPworst]).getID() + "\t" + ??  )
		}
		
		IFeaturesCollector[] piv = new IFeaturesCollector[nPivots];
		

		
		Log.info_verbose("nPiv\tid\tgain\textRMin");
		long start = System.currentTimeMillis();
		for ( int iP=0; iP<piv.length; iP++) {
			
			IFeaturesCollector best = null;
			double bestSumGain = -1;
			
			for ( int iT=0; iT<nTries; iT++) {
				//System.out.print(".");
				IFeaturesCollector curr = candidatePivots.get(RandomOperations.getInt(0, candidatePivots.size()));
				double currSumGain = 0;
				
				// evaluating distances between all test objects and curr Pivots
				for ( int i=0; i<ooPDist.length; i++) {
					//opDist[i] = sim.distance(curr, testObjects[i]);
									
					int ti = 0;
			        Thread[] thread = new Thread[nThread];
			        for ( int from=0; from<testObjects.length; from+=nQueriesPerThread) {
			        	int to = from+nQueriesPerThread-1;
			        	if ( to >= testObjects.length ) to =testObjects.length-1;
			        	thread[ti] = new Thread( new PivObjsDistances(curr, testObjects, sim, from,to, opDist) ) ;
			        	thread[ti].start();
			        	ti++;
			        }
			        
			        for ( Thread t : thread ) {
		        		if ( t != null ) t.join();
			        }
					
				}
				
				for ( int i=0; i<ooPDist.length; i++) {
					double abs = Math.abs(opDist[obj1[i]]-opDist[obj2[i]]); 
					if ( abs > ooPDist[i] ) currSumGain += abs;
				}
				
				if ( currSumGain > bestSumGain ) {
					best = curr;
					bestSumGain = currSumGain;
					bestOPDist = opDist; // current is best
					opDist = bestOPDist; // will be used for next try
				}				
			}
			
			piv[iP] = best;
			
			// pDist updating using new 
			for ( int i=0; i<ooPDist.length; i++ ) {
				double abs = Math.abs(bestOPDist[obj1[i]]-bestOPDist[obj2[i]]); 
				if ( abs > ooPDist[i] ) ooPDist[i] = abs;
			}	
			
			long now = System.currentTimeMillis();
			
			float avgGain = (float) bestSumGain / ooPDist.length;
			// extimated time in minutes 
			long ext = (now - start) / (iP+1) * (piv.length-iP-1) / 1000 / 60;
			Log.info_verbose(iP + "\t" + ((IHasID) best).getID() +"\t"+ avgGain + "\t" + ext);
		}
		
		return piv;
		
	}
*/
}
