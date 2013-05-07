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
package it.cnr.isti.vir.perm;

import it.cnr.isti.vir.clustering.Centroids;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.metric.SAPIRMetric;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.ParallelOptions;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.SimilarityUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class FarthestFirstTraversal {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		RandomOperations.setRandomSeed();
		
		// 1M Exp
//		String archivesFileName = "X:/CoPhIR/1MExp/dataset";
//		int nTries = 100000;
//		int nMaxObj = 100;
//		int nCandidatesCycles = 10;
//		String outFileName = "X:/CoPhIR/1MExp/ro/fft/fft-1M-"+nMaxObj+"-"+nTries+"t";
		
		// Full
		String archivesFileName = "X:/CoPhIR/1MExp/dataset";
		//String archivesFileName = "X:/CoPhIR/dat/";
		int nTries = 100000;
		int nMaxObj = 10000;
		int nCandidatesCycles = 100;
		String outFileName = "X:/CoPhIR/RO/fft/fft-106M-"+nMaxObj+"-"+nTries+"t";
		
		FeaturesCollectorsArchives archives = new FeaturesCollectorsArchives(archivesFileName, false);
		SAPIRMetric sim = new SAPIRMetric(); 
		Log.info(archives.size() + " were found.");
		IFeaturesCollector[] res = select(sim, archives, nMaxObj, nTries, nCandidatesCycles);
		
		double minInterDist = SimilarityUtil.getMinInterDistance(res, sim);

		Log.info("minInterDist: " + minInterDist);
		
		Centroids centroids = new Centroids(res);
		centroids.writeData(outFileName+"_"+minInterDist);
	}
	
	public static class SubsetBest implements Runnable {

		ISimilarity sim;
		IFeaturesCollector[] obj;
		int from;
		int to;
		IFeaturesCollector[] ro;
		int nRO;
		
		public double maxMinDist=Double.MIN_VALUE;
		public int bestCandidate=-1;
		
		public SubsetBest(ISimilarity sim, IFeaturesCollector[] obj, int from, int to, IFeaturesCollector[] ro, int nRO) {
			this.sim = sim;
			this.obj = obj;
			this.ro = ro;
			this.from=from;
			this.to=to;
			this.nRO = nRO;
			
		}

		@Override
		public void run() {
			for( int i=from; i<to; i++) {
				IFeaturesCollector candidate = obj[i];
				double minDist = Double.MAX_VALUE;
				for ( int iRO=0; iRO<nRO; iRO++) {
					double currDist = sim.distance(candidate, ro[iRO], minDist);
					if ( currDist>=0 && currDist<minDist) minDist = currDist;
				}
				
				if ( minDist>maxMinDist ) {
					maxMinDist=minDist;
					bestCandidate=i;
				}
			}
		}

	}
	
	public static IFeaturesCollector[] select(ISimilarity sim, FeaturesCollectorsArchives archives, int nMaxObj, int maxNTries, int nCandidatesCycles) throws ArchiveException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		IFeaturesCollector[] res = new IFeaturesCollector[nMaxObj];
		
		
		
		int nObjPerCycle = Math.min(maxNTries, archives.size());
		final int nObjsPerThread = (int) Math.ceil((double) nObjPerCycle / ParallelOptions.nThreads);
		final int nThread = (int) Math.ceil((double) nObjPerCycle / nObjsPerThread);
		
		//IFeaturesCollector[] rndOrd = new IFeaturesCollector[nObjPerCycle];
	

		
		IFeaturesCollector[] candidates = new IFeaturesCollector[nObjPerCycle];
		if ( nObjPerCycle == archives.size()) {
			Log.info("Reading all objects");
			archives.getAll().toArray(candidates);
		}
		
		// first is randomly selected
		res[0] = archives.get(RandomOperations.getInt(0, archives.size()-1));
		Log.info(1 + "\t" + ((IHasID) res[0]).getID());
		
		for(int iRes=1; iRes<res.length; iRes++) {

			if ( nObjPerCycle != archives.size() && (iRes-1) % nCandidatesCycles == 0) {
				// at each cycle we randomly select nObjPerCycle objects from the dataset
				int[] rIDs = RandomOperations.getDistinctIntArray(nObjPerCycle, 0, archives.size()-1);
				Arrays.sort(rIDs);
				for ( int i=0; i<rIDs.length; i++) {
					candidates[i] = archives.get(rIDs[i]);
				}
			}
			RandomOperations.shuffle(candidates);
			
			double maxMinDist = Double.MIN_VALUE;
			int bestCandidate=-1;
			
			
			int ti = 0;
			
			SubsetBest[] runnable = new SubsetBest[ParallelOptions.nThreads];
			Thread[] thread = new Thread[ParallelOptions.nThreads];
	        for ( int from=0; from<nObjPerCycle; from+=nObjsPerThread) {
	        	int to = from+nObjsPerThread-1;
	        	if ( to >= nObjPerCycle ) to =nObjPerCycle-1;	 
	        	runnable[ti] = new SubsetBest( sim, candidates, from, to, res, iRes);
	        	thread[ti] = new Thread( runnable[ti] ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        // waiting threads
	        for ( ti=0; ti<thread.length; ti++ ) {
	        	if ( thread[ti] == null ) continue;
	        	try {
					thread[ti].join();
					
					double minDist = runnable[ti].maxMinDist;
					if ( minDist>maxMinDist ) {
						maxMinDist=minDist;
						bestCandidate=runnable[ti].bestCandidate;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }	
			/*
			
			for( int i=0; i<rndOrd.length && i<nTries; i++) {
				IFeaturesCollector candidate = rndOrd[i];
				double minDist = Double.MAX_VALUE;
				for ( int iRO=0; iRO<iRes; iRO++) {
					double currDist = sim.distance(candidate, res[iRO], minDist);
					if ( currDist>=0 && currDist<minDist) minDist = currDist;
				}
				
				if ( minDist>maxMinDist ) {
					maxMinDist=minDist;
					bestCandidate=i;
				}
			}
			*/
			res[iRes]=candidates[bestCandidate];
			String tStr = (iRes+1) + "\t" + ((IHasID) res[iRes]).getID() + "\t" + maxMinDist;
			if ( nObjPerCycle != archives.size() && (iRes-1) % nCandidatesCycles == 0 ) {
				tStr += "\tnew candidates";
			}
			Log.info(tStr);
		}
		
		
		return res;
	}

}
