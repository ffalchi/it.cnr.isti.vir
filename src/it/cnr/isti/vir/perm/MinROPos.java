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
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Permutation;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.SpearmanFootrule;
import it.cnr.isti.vir.similarity.SpearmanRho;
import it.cnr.isti.vir.similarity.metric.SAPIRMetric;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.Nulls;
import it.cnr.isti.vir.util.ParallelOptions;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.Statistic;

import java.util.Arrays;
import java.util.HashSet;

public class MinROPos {

	
	public static int nTries = 1000;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		ISimilarity permSim = new SpearmanFootrule(0);
		
		//  TO DO 
		String archiveFileName = "X:/CoPhIR/rnd/CoPhIR_100k_rnd";
		nTries = 10;		
		int initNRO = 10000;
		int ki=100;
		String outFileName = "X:/CoPhIR/RO/f/f_100k_1k_100_["+ki+"_"+nTries+"t]";
		
//		// test
//		String archiveFileName = "X:/CoPhIR/1MExp/dataset_subset/CoPhIR_1M_100k_rnd";
//		String outFileName = "X:/CoPhIR/1MExp/ro/minROPos/minROPos-100k-1000k-100t-1";
//		nTries = 100;
//		int initNRO = 1000;
//		int ki=1;
		
		FeaturesCollectorsArchive archive = new FeaturesCollectorsArchive(archiveFileName);
		SAPIRMetric sim = new SAPIRMetric(); 
		AbstractFeaturesCollector[] data = archive.getAllArray();
		Log.info(data.length + " objects were read.");
		AbstractFeaturesCollector[] res = order(sim, permSim, data, initNRO, ki);
		
		Centroids centroids = new Centroids(res);
		centroids.writeData(outFileName);
	}
	
	
	public static AbstractFeaturesCollector[] order(ISimilarity sim, ISimilarity permSim, AbstractFeaturesCollector[] set, int candidatesN, int permLength) {
		AbstractFeaturesCollector[] copySet = set.clone();
		RandomOperations.shuffle(set);
		
		AbstractFeaturesCollector[] candidates = new AbstractFeaturesCollector[candidatesN];
		AbstractFeaturesCollector[] examples   = new AbstractFeaturesCollector[copySet.length-candidatesN];
		System.arraycopy(copySet, 0, candidates, 0, candidatesN);
		System.arraycopy(copySet, candidatesN, examples, 0, copySet.length-candidatesN);
		
		Log.info("Using " + candidates.length + " as candidates and " + examples.length + " as examples");
		
		//return orderMinCoeffVar(sim, candidates, examples, permLength, nTries);
		return orderMinROPosVar(sim, permSim, candidates, examples, permLength, nTries);
	}
	
//	public static IFeaturesCollector[] order(ISimilarity sim, IFeaturesCollector[] candidates, IFeaturesCollector[] examples, int permLength ) {
//		Log.info("Evaluating Permutations");
//		Permutation[] obj = getPermutations(examples, candidates, sim);
//		
//		
//		IFeaturesCollector[] tCand = candidates.clone();
//		IFeaturesCollector[] res = new IFeaturesCollector[tCand.length];
//		int iRes = candidates.length-1;
//		int[] roOcc = new int[tCand.length];
//		while ( iRes >= 0 ) {
//			int tPL = Math.min(permLength, iRes+1 );
//			
//			Arrays.fill(roOcc, 0);
//			for ( Permutation p : obj ) p.addToROOcc(roOcc, tPL);
//			
//			int min = Integer.MAX_VALUE;
//			int minPos = -1;
//			int max = Integer.MIN_VALUE;
//			int maxPos = -1;
//			double variance = 0;
//			int sum =0;
//			double avg = tPL * obj.length/ (double) (iRes+1);
//			for (int i=0; i<roOcc.length; i++ ) {
//				if ( tCand[i] == null ) continue;
//				int v = roOcc[i];
//				sum += v;
//				double diff = v - avg;
//				variance += diff*diff;
//				if ( v>max ) {
//					max = v;
//					maxPos = i;
//				}
//				if ( v<min ) {
//					min = v;
//					minPos = i;
//				}
//			}
//			variance /= (iRes+1);
//			double coeffVar = Math.sqrt(variance)/avg ;
//			
//			int delPos = minPos;
//			int delVal = min;
//			
////			if ( iRes % 2 == 0 ) {
////				delPos = maxPos;
////				delVal = max;
////			}
//			
//			//Falchi Esuli
//			if ( max / avg > avg / min ) {
//				delPos = maxPos;
//				delVal = max;
//			}
//			
////			FUNZIONA MOLTO PEGGIO			
////			if ( delVal != 0 ) { 
////				double minAbsDev = avg - min;
////				double maxAbsDev = max - avg;
////				if ( maxAbsDev > minAbsDev) {
////					delPos = maxPos;
////					delVal = max;
////				}
////			}
//			
//			res[iRes] = tCand[delPos];
//			tCand[delPos] = null;			
//			for ( Permutation o : obj ) {
//				// removing min				
//				o.removeRO(delPos);
//			}
//			
//			Log.info((iRes+1) + "\t" + ((IHasID) res[iRes]).getID() + "\t" + (delVal-avg) +"\t" + (double) sum / (iRes+1) + "\t" + coeffVar );
//			iRes--;
//				
//		}
//		
//		// removing nulls
//		return res;
//	}

	
	
	public static double getCoeffVar(int[] roOcc, double avg, AbstractFeaturesCollector[] candidates, int nCand, Integer exc ) {
		double variance = 0;
		for (int i = 0; i < roOcc.length; i++) {
			if ( candidates[i] != null && (exc==null || i!=exc)) {
				double diff = roOcc[i] - avg;
				variance += diff * diff;
			}
		}
		variance /= nCand;  
		return Math.sqrt(variance) / avg;
	}
	
	
	public static double getCoeffOfVariation(int[][] roPosOcc, double avg, Integer exc, int permLength ) {
		double variance = 0;
		int roCount = 0;
		for (int iRO = 0; iRO < roPosOcc.length; iRO++) {
			if ( roPosOcc[iRO] != null && (exc==null || iRO!=exc) ) {
				roCount++;
				for (int i = 0; i <permLength; i++) {
					double diff = roPosOcc[iRO][i] - avg;
					variance += diff * diff;
				}
			}
		}
		return Math.sqrt(variance) / permLength / (double) roCount / avg;
	}
	
	public static double getMean(int[][] roPosOcc, int permLength ) {
		double sum = 0;
		int roCount = 0;
		for (int iRO = 0; iRO < roPosOcc.length; iRO++) {
			if ( roPosOcc[iRO] != null ) {
				roCount++;
				for (int i = 0; i <permLength; i++) {
					sum +=  roPosOcc[iRO][i];
				}
			}
		}
		return sum / permLength / (double) roCount;
	}
	
	public static int getFirstZeroPosition(int[] values, AbstractFeature[] tCand) {
		for ( int i=0; i<values.length; i++) {
			if ( tCand[i] != null && values[i] == 0 ) return i;
		}
		return -1;
	}
	
	// For kNN searching
	public static class MinCoeffVarThread implements Runnable {
		private final int from;
		private final int to;
		private final int[] tries;
		private final Permutation[] obj;
		private final double[] res;
		private final double avg;
		private final int nCand;
		private final int tPL;
		private final int[] roOcc;
		private final AbstractFeaturesCollector[] ro;

		public MinCoeffVarThread(int[] tries, Permutation[]  obj, int from, int to, int tPL, double[] res, double avg, int nCand, int iniCandN, AbstractFeaturesCollector[] candidates ) {
			this.from = from;
			this.to = to;
			this.tries = tries;
			this.obj = obj;
			this.tPL = tPL;
			this.res = res;
			this.avg = avg;
			this.nCand = nCand;
			roOcc = new int[iniCandN];
			this.ro = candidates;
		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				int i = tries[iO];
				Arrays.fill(roOcc,0);
				for ( Permutation p : obj ) p.addToROOcc(roOcc, tPL, i);
				res[iO] = getCoeffVar(roOcc, avg, ro, nCand, i);
			}
		}

	}
	
	
	
	public static AbstractFeaturesCollector[] orderMinCoeffVar(ISimilarity sim,
			AbstractFeaturesCollector[] candidates, AbstractFeaturesCollector[] examples,
			int permLength, int nTriesMax ) {
		
		
		// PERMUTATIONS
		Log.info("Evaluating Permutations");
		Permutation[] obj = getPermutations(examples, candidates, sim);
		for ( Permutation o : obj ) o.convertToOrdered();
		
		// INIT
		AbstractFeaturesCollector[] tCand = candidates.clone();
		AbstractFeaturesCollector[] res = new AbstractFeaturesCollector[tCand.length];
		int iRes = 0;
		int nCand = candidates.length;
		
		// RO Occurencies stats
		double avgROOcc = permLength * obj.length / (double) (nCand);
		int[] roOcc = new int[candidates.length];
		Arrays.fill(roOcc, 0);
		for ( Permutation p : obj ) p.addToROOcc(roOcc, permLength);
		double coeffVar = getCoeffVar(roOcc, avgROOcc, tCand, nCand, null);
		
		// for stats
		double avgROPosOcc =  obj.length / (double) (nCand);
		int[][] roPosOcc = new int[candidates.length][permLength];
		fillZeros(roPosOcc);
		for ( Permutation p : obj ) p.addToROPosOcc(roPosOcc, permLength);
		double coeffOfVariationROOcc = getCoeffOfVariation(roPosOcc, avgROPosOcc, null, permLength);
		
		Log.info("nCand\tID\tcoeffVar\tcoeffOfVariationROOcc\tintrinsicDim");
		
		while (iRes < res.length-permLength) {
			double minCoeffVar = Double.MAX_VALUE;
			int delPos = -1;
			
			// we are pretending to have removed tCand[i]
			avgROPosOcc = obj.length / (double) (nCand-1 );
			avgROOcc = permLength * obj.length / (double) (nCand-1 );
			int[] tries = Nulls.getNotNullsPos(tCand);
			RandomOperations.shuffle(tries);

			int nTries = Math.min(nTriesMax, tries.length);
			double[] tCoeffVar = new double[nTries];

			final int nObjsPerThread = (int) Math.ceil((double) nTries / ParallelOptions.nThreads);
			final int nThread = (int) Math.ceil((double) nTries / nObjsPerThread);
			int ti = 0;
	        Thread[] thread = new Thread[nThread];
	        for ( int from=0; from<nTries; from+=nObjsPerThread) {
	        	int to = from+nObjsPerThread-1;
	        	if ( to >= nTries ) to =nTries-1;
	        	thread[ti] = new Thread( new MinCoeffVarThread( tries, obj, from, to, permLength, tCoeffVar, avgROOcc,  nCand-1, candidates.length, tCand) ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        // waiting threads
	        for ( ti=0; ti<thread.length; ti++ ) {
	        	try {
					thread[ti].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }			
	        
	        // selecting worst
			for ( int iT=0; iT<nTries; iT++) {
				if ( tCoeffVar[iT] < minCoeffVar  ) {
					minCoeffVar = tCoeffVar[iT];
					delPos = tries[iT];
				}
			}

			// assigning
			res[res.length-1-iRes] = tCand[delPos];
			tCand[delPos] = null;
			for (Permutation o : obj) {
				o.removeRO(delPos);
			}
			
			
			Log.info(nCand + "\t" + ((IHasID) res[res.length-1-iRes]).getID() + "\t" + coeffVar + "\t" + coeffOfVariationROOcc);
			
			coeffVar = minCoeffVar;
			iRes++;
			nCand--;

			fillZeros(roPosOcc);
			for ( Permutation p : obj ) p.addToROPosOcc(roPosOcc, permLength);
			coeffOfVariationROOcc = getCoeffOfVariation(roPosOcc, avgROPosOcc, null, permLength);
			
		}
		
		while (iRes < res.length ) {
			int delPos = Nulls.getNotNullsPos(tCand)[0];
			res[res.length-1-iRes] = tCand[delPos];
			tCand[delPos] = null;
			// removing min
			for (Permutation o : obj) {
				o.removeRO(delPos);
			}
			Log.info(nCand + "\t" + ((IHasID) res[res.length-1-iRes]).getID() );
			iRes++;
			nCand--;
		}		
		return res;
	}
	
	public static void fillZeros(int[][] values) {
		for ( int i=0; i<values.length; i++) {
			if ( values[i]!=null) Arrays.fill(values[i], 0);
		}
	}
	
	public static class MinROPosVarThread implements Runnable {
		private final int from;
		private final int to;
		private final int[] tries;
		private final Permutation[] obj;
		private final double[] res;
		private final double avg;
		private final int nCand;
		private final int permLength;
		private final int[][] roOcc;

		public MinROPosVarThread(int[] tries, Permutation[]  obj, int from, int to, int permLength, double[] res, double avg, int nCand, int iniCandN, int[][] roOcc ) {
			this.from = from;
			this.to = to;
			this.tries = tries;
			this.obj = obj;
			this.permLength = permLength;
			this.res = res;
			this.avg = avg;
			this.nCand = nCand;
			this.roOcc = roOcc;
		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				int i = tries[iO];
				fillZeros(roOcc);
				for ( Permutation p : obj ) p.addToROPosOcc(roOcc, permLength, i);
				res[iO] = getCoeffOfVariation(roOcc, avg, i, permLength);
				//res[iO] = getMean(roOcc, permLength);
			}
		}

	}
	
	public static double getIntrisincDimensionality(AbstractFeature[] obj, int[] ord1, int[] ord2, ISimilarity sim) {
		double[] dist = null;
		if ( ord1 != null ) {
			dist = new double[ord1.length];
			for (int i=0; i<ord1.length; i++) {
				dist[i] = sim.distance(obj[ord1[i]], obj[ord2[i]]);
			}
		} else {
			// all combinations
			dist = new double[obj.length*obj.length];
			for (int i1=0; i1<obj.length; i1++) {
				for (int i2=0; i2<obj.length; i2++) {
					dist[i1*obj.length+i2] = sim.distance(obj[i1], obj[i2]);
				}
			}
		}
		
		double mean = Statistic.getMean(dist);
		double variance = Statistic.getVariance(dist, mean);
		
		return mean*mean/variance/2.0;
	}
	
	public static AbstractFeaturesCollector[] orderMinROPosVar(ISimilarity sim, ISimilarity permSim,
			AbstractFeaturesCollector[] candidates, AbstractFeaturesCollector[] examples,
			int permLength, int nTriesMax ) {
		
		
		// for stats
		Log.info("Evaluating data intrinsic dimensionality");
		int[] ord1 = RandomOperations.getRandomOrderedInts(examples.length);
		int[] ord2 = RandomOperations.getRandomOrderedInts(examples.length);
		double dataIntrinsicDim = getIntrisincDimensionality(examples, ord1, ord2, sim);
		
		Log.info("Data intrinsic dimensionality: " + dataIntrinsicDim );
		
		Log.info("Evaluating Permutations");
		Permutation[] obj = getPermutations(examples, candidates, sim);
		for ( Permutation o : obj ) o.convertToOrdered();
		
		AbstractFeaturesCollector[] tCand = candidates.clone();
		AbstractFeaturesCollector[] res = new AbstractFeaturesCollector[tCand.length];
		int iRes = 0;
		int nCand = candidates.length;
		
		// Eval
		double avgROPosOcc =  obj.length / (double) (nCand);
		int[][] roPosOcc = new int[candidates.length][permLength];
		fillZeros(roPosOcc);
		for ( Permutation p : obj ) p.addToROPosOcc(roPosOcc, permLength);
		double eval = getCoeffOfVariation(roPosOcc, avgROPosOcc, null, permLength);
		
		// for stats
		double avgROOcc = permLength * obj.length / (double) (nCand);
		int[] roOcc = new int[candidates.length];
		Arrays.fill(roOcc, 0);
		for ( Permutation p : obj ) p.addToROOcc(roOcc, permLength);
		double coeffVar = getCoeffVar(roOcc, avgROOcc, tCand, nCand, null);
		
		// for stats
		double intrinsicDim = getIntrisincDimensionality(obj, ord1, ord2, permSim);
		
		
		Log.info("nCand\tID\tavg\tcoeffVar\tcoeffOfVariationROOcc");
		
		while (iRes < res.length-permLength) {
			double minCoeffVar = Double.MAX_VALUE;
			int delPos = -1;
			
			// we are pretending to have removed tCand[i]
			avgROPosOcc = obj.length / (double) (nCand-1 );
			avgROOcc = permLength * obj.length / (double) (nCand-1 );
			int[] tries = Nulls.getNotNullsPos(tCand);
			RandomOperations.shuffle(tries);

			int nTries = Math.min(nTriesMax, tries.length);
			double[] tCoeffVar = new double[nTries];

			final int nObjsPerThread = (int) Math.ceil((double) nTries / ParallelOptions.nThreads);
			final int nThread = (int) Math.ceil((double) nTries / nObjsPerThread);
			int ti = 0;
			
			fillZeros(roPosOcc);
	        Thread[] thread = new Thread[nThread];
	        for ( int from=0; from<nTries; from+=nObjsPerThread) {
	        	int to = from+nObjsPerThread-1;
	        	if ( to >= nTries ) to =nTries-1;
	        	int[][] roOccTh = new int[candidates.length][];	        	
	        	for ( int i=0; i<candidates.length; i++) {
	        		if ( roPosOcc[i]!=null ) roOccTh[i]= new int[permLength];
	        	}
	        	
	        	thread[ti] = new Thread( new MinROPosVarThread( tries, obj, from, to, permLength, tCoeffVar, avgROPosOcc,  nCand-1, candidates.length, roOccTh) ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        // waiting threads
	        for ( ti=0; ti<thread.length; ti++ ) {
	        	try {
					thread[ti].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }			
	        
	        // selecting worst
			for ( int iT=0; iT<nTries; iT++) {
				if ( tCoeffVar[iT] < minCoeffVar  ) {
					minCoeffVar = tCoeffVar[iT];
					delPos = tries[iT];
				}
			}

			// assigning
			res[res.length-1-iRes] = tCand[delPos];
			tCand[delPos] = null;
			roPosOcc[delPos] = null;
			for (Permutation o : obj) {
				o.removeRO(delPos);
			}
			
			String tStr = nCand + "\t" + ((IHasID) res[res.length-1-iRes]).getID() + "\t" + avgROPosOcc + "\t" + coeffVar + "\t" + eval ;
			if ( iRes % 10 == 0 ) {
				tStr += "\t" + intrinsicDim; 
			}
			Log.info(tStr);
			eval = minCoeffVar;
			iRes++;
			nCand--;
			
			Arrays.fill(roOcc, 0);
			for ( Permutation p : obj ) p.addToROOcc(roOcc, permLength);
			coeffVar = getCoeffVar(roOcc, avgROOcc, tCand, nCand, null);
			
			if ( iRes % 10 == 0 ) {
				
				intrinsicDim = getIntrisincDimensionality(obj, ord1, ord2, permSim);
			}
		}
		
		while (iRes < res.length ) {
			int delPos = Nulls.getNotNullsPos(tCand)[0];
			res[res.length-1-iRes] = tCand[delPos];
			tCand[delPos] = null;
			roPosOcc[delPos] = null;
			// removing min
			for (Permutation o : obj) {
				o.removeRO(delPos);
			}
			Log.info(nCand + "\t" + ((IHasID) res[res.length-1-iRes]).getID() );
			iRes++;
			nCand--;
		}		
		return res;
	}

	public static class Bucket {
		int[] perm;
		
		public Bucket(int[] perm) {
			this.perm = perm;
		}
		
		public int hashCode() {
			return Arrays.hashCode(perm);
		}
		
	    @Override
	    public boolean equals(Object other) {
	        if (other == null) return false;
	        if (other == this) return true;
	        if (!(other instanceof Bucket))return false;
	        Bucket otherBucket = (Bucket)other;
	        if ( perm.length != otherBucket.perm.length ) return false;
	        for ( int i=0; i<perm.length; i++) {
	        	if ( perm[i]!=otherBucket.perm[i]) return false;
	        }
	        return true;
	    }
	}
	
	public static HashSet<Bucket> getBuckets(Permutation[] obj, int permLength ) {
		return getBuckets(obj, permLength, null);
	}
	
	public static HashSet<Bucket> getBuckets(Permutation[] obj, int permLength, Integer excluded ) {
		HashSet<Bucket> buckets = new HashSet<Bucket>();
		for ( int i=0; i<obj.length; i++) {
			Bucket currBucket = new Bucket(obj[i].getOrdRO(permLength, excluded));
			if ( !buckets.contains(currBucket))
				buckets.add(currBucket);
		}
		return buckets;
	}
	
	
	
//	public static IFeaturesCollector[] order3(ISimilarity sim,
//			IFeaturesCollector[] candidates, IFeaturesCollector[] examples,
//			int permLength) {
//		
//		Log.info("Evaluating Permutations");
//		Permutation[] obj = getPermutations(examples, candidates, sim);	
//		
//		IFeaturesCollector[] tCand = candidates.clone();
//		IFeaturesCollector[] res = new IFeaturesCollector[tCand.length];
//		int iRes = candidates.length - 1;
//		
//		HashSet<Bucket> buckets = getBuckets(obj, permLength );
//		
//		int tPL = Math.min(permLength, iRes + 1);
//		
//		// removing never occurring buckets
//		int[] roOcc = new int[candidates.length];
//		for ( Permutation p : obj ) p.addToROOcc(roOcc, tPL);
//		int delPos = -1;
//		while ( (delPos = getFirstZeroPosition(roOcc, tCand)) >= 0) {
//			res[iRes] = tCand[delPos];
//			tCand[delPos] = null;
//			for (Permutation o : obj) {
//				// removing min
//				o.removeRO(delPos);
//			}
//			Log.info((iRes + 1) + "*\t" + ((IHasID) res[iRes]).getID() + "\t" + buckets.size());
//			
//			buckets = getBuckets(obj, permLength );
//			iRes--;
//		}
//		
//		double currEval = buckets.size();
//		while (iRes >= 0) {
//			delPos = -1;
//			// we are pretending to have removed tCand[i]
//			tPL = Math.min(permLength, iRes );
//
//			double maxEval = Double.MIN_VALUE;
//			
//			// searching worst
//			for (int i=0; i<tCand.length; i++ ) {
//				if ( tCand[i]==null) continue;
//				if ( iRes == 0 ) {
//					maxEval = 0.0;
//					delPos = i;
//					break;
//				}
//				
//				buckets = getBuckets(obj, tPL, i);
//				double tEval = buckets.size();
//				if ( tEval > maxEval  ) {
//					maxEval = tEval;
//					delPos = i;
//				}
//			}
//
//			res[iRes] = tCand[delPos];
//			tCand[delPos] = null;
//			for (Permutation o : obj) {
//				// removing min
//				o.removeRO(delPos);
//			}
//			
//			Log.info((iRes + 1) + "\t" + ((IHasID) res[iRes]).getID() + "\t" + currEval);
//			
//			currEval = maxEval;
//			iRes--;
//
//		}
//
//		// removing nulls
//		return res;
//	}
	
	
	public static double[] getDistances(Permutation[] obj1, Permutation[] obj2, ISimilarity sim ) {
		double[] res = new double[obj1.length];
		for (int i=0; i<res.length; i++) {
			res[i] = sim.distance(obj1, obj2);
		}
		return res;
	}
	
	public static AbstractFeaturesCollector[] order4(ISimilarity sim,
			AbstractFeaturesCollector[] candidates, AbstractFeaturesCollector[] examples,
			int permLength) {
		
		Log.info("Evaluating Permutations");
		Permutation[] obj = getPermutations(examples, candidates, sim);	
		
		AbstractFeaturesCollector[] tCand = candidates.clone();
		AbstractFeaturesCollector[] res = new AbstractFeaturesCollector[tCand.length];
		int iRes = candidates.length - 1;
		
		Permutation[] obj1 = obj.clone();
		Permutation[] obj2 = obj.clone();
		RandomOperations.shuffle(obj1);
		RandomOperations.shuffle(obj2);
		
		((SpearmanRho) sim).setMaxLength(permLength);
		double[] distances = getDistances(obj1, obj2, sim);
		double prevCoeffOfVariation = Statistic.getCoeffOfVariation(distances);
		
		HashSet<Bucket> buckets = getBuckets(obj, permLength );
		
		int tPL = Math.min(permLength, iRes + 1);
		
		// removing never occurring buckets
		int[] roOcc = new int[candidates.length];
		for ( Permutation p : obj ) p.addToROOcc(roOcc, tPL);
		int delPos = -1;
		while ( (delPos = getFirstZeroPosition(roOcc, tCand)) >= 0) {
			res[iRes] = tCand[delPos];
			tCand[delPos] = null;
			for (Permutation o : obj) {
				// removing min
				o.removeRO(delPos);
			}
			Log.info((iRes + 1) + "*\t" + ((IHasID) res[iRes]).getID() + "\t" + prevCoeffOfVariation);
			
			((SpearmanRho) sim).setMaxLength(tPL);
			prevCoeffOfVariation = Statistic.getCoeffOfVariation(getDistances(obj1, obj2, sim));
			buckets = getBuckets(obj, permLength );
			iRes--;
		}
		
		double currEval = buckets.size();
		while (iRes >= 0) {
			delPos = -1;
			// we are pretending to have removed tCand[i]
			tPL = Math.min(permLength, iRes );

			double maxEval = Double.MIN_VALUE;
			
			// searching worst
			for (int i=0; i<tCand.length; i++ ) {
				if ( tCand[i]==null) continue;
				if ( iRes == 0 ) {
					maxEval = 0.0;
					delPos = i;
					break;
				}
				
				buckets = getBuckets(obj, tPL, i);
				double tEval = buckets.size();
				if ( tEval > maxEval  ) {
					maxEval = tEval;
					delPos = i;
				}
			}

			res[iRes] = tCand[delPos];
			tCand[delPos] = null;
			for (Permutation o : obj) {
				// removing min
				o.removeRO(delPos);
			}
			
			Log.info((iRes + 1) + "\t" + ((IHasID) res[iRes]).getID() + "\t" + currEval);
			
			currEval = maxEval;
			iRes--;

		}

		// removing nulls
		return res;
	}

	// For kNN searching
	public static class PermThread implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final AbstractFeaturesCollector[] ro;
		private final ISimilarity sim;
		private final Permutation[] res;

		public PermThread(AbstractFeaturesCollector[] ro, ISimilarity sim, AbstractFeaturesCollector[]  objs, int from, int to, Permutation[] res ) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.ro = ro;
			this.sim = sim; 
			this.res = res;
			
		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				res[iO] = new Permutation(objs[iO], ro, sim);
			}
		}
	}
	
	
	public static Permutation[] getPermutations(AbstractFeaturesCollector[] objs, AbstractFeaturesCollector[] ro, ISimilarity sim ) {
		Permutation[] res = new Permutation[objs.length];
//		for (int i = 0; i < objs.length; i++) {
//			res[i] = new Permutation(objs[i], ro, sim, false);
//		}	
		
		// kNNQueues are performed in parallels
		final int nObjsPerThread = (int) Math.ceil((double) objs.length / ParallelOptions.nThreads);
		final int nThread = (int) Math.ceil((double) objs.length / nObjsPerThread);
		int ti = 0;
        Thread[] thread = new Thread[nThread];
        for ( int from=0; from<objs.length; from+=nObjsPerThread) {
        	int to = from+nObjsPerThread-1;
        	if ( to >= objs.length ) to =objs.length-1;
        	thread[ti] = new Thread( new PermThread(ro, sim, objs, from,to, res) ) ;
        	thread[ti].start();
        	ti++;
        }
        
        for ( ti=0; ti<thread.length; ti++ ) {
        	try {
				thread[ti].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return res;
	}
}
