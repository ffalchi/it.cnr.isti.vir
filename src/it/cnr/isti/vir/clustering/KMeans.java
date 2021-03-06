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
package it.cnr.isti.vir.clustering;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.similarity.ILFSimilarity;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.TimeManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class KMeans<O extends AbstractFeature> {

	private final IMeanEvaluator<O> meanEval;
	private final ISimilarity<O> sim;
	private final O[] objects;
	private final Class<? extends O> objectsClass;
	
	private float[] centroidDistance;
	private int[] assignedCentroid;
	
	private O[] centroids;
	private Collection<O>[] clusters;
	
	private int[] centroidChangedIndexes;
	private boolean[] centroidChanged;
	private boolean[] clusterChanged;
	
	private TimeManager tm = new TimeManager();
	private long maxMillisPerIteration = Integer.MAX_VALUE;
	public long getMaxMillisPerIteration() {
		return maxMillisPerIteration;
	}

	public void setMaxMinPerIteration(int maxMinPerIteration) {
		this.maxMillisPerIteration = maxMinPerIteration * 60 * 1000;
		Log.info_verbose("kMeans maxMillisPerIteration was set to: " + maxMillisPerIteration); 
	}

	public void setMaxMillisPerIteration(int maxMillisPerIteration) {
		this.maxMillisPerIteration = maxMillisPerIteration;
		Log.info_verbose("kMeans maxMillisPerIteration was set to: " + maxMillisPerIteration); 
	}
	
	private static final int eqCentroidsChange_Max = 100;
	
	private double distRedThr = 0.9999;
	
	private boolean isMedoid = false;
	
	double currDistortion = Double.MAX_VALUE;
	
	public double getDistortion() {
		return currDistortion;
	}
	
	public boolean getIsMedoid() {
		return isMedoid;
	}
	
	public void setIsMedoid(boolean isMedoid) {
		this.isMedoid = isMedoid;
	}
	
	public KMeans(ArrayList<O> objects, ISimilarity<O> sim) {
		
		this(
				objects.toArray(
					(O[]) Array.newInstance(objects.get(0).getClass(), objects.size())),
					sim);	
		
	}
	
	public KMeans(O[] objects, ISimilarity<O> sim) {
		
		this.objects = objects;
		objectsClass = (Class<? extends O>) this.objects[0].getClass();
		this.meanEval = (IMeanEvaluator<O>) sim;
		this.sim = sim;
		randomOrdering();
		
	}
	
	
	public double getDistRedThr() {
		return distRedThr;
	}

	public void setDistRedThr(double distRedThr) {
		this.distRedThr = distRedThr;
	}

	public KMeans(O[] objects, O[] initCentroids, ISimilarity<O> sim) {
		this.centroids = initCentroids;
		this.objects = objects;
		objectsClass = (Class<? extends O>) this.objects[0].getClass();
		this.meanEval = (IMeanEvaluator<O>) sim;
		this.sim = sim;
	}

	
	public final void randomOrdering() {
		// InitCentroids
		System.out.print("Random reordering data ... ");
		RandomOperations.shuffle(objects);
		System.out.println(" done.");
	}
	
	public final void randomInitCentroids(int k) {

		HashSet<Integer> selected = new HashSet<Integer>();
		O[] initCentroids = (O[]) Array.newInstance(objects[0].getClass(), k);		
		for ( int i=0; i<initCentroids.length; i++ ) {
			int rnd = -1;
			do {
				rnd = RandomOperations.getInt(objects.length);
			} while ( selected.contains(rnd));
			selected.add(rnd);
			initCentroids[i]= (O) objects[rnd];
		}
		centroids = (O[]) initCentroids;
	}
	

	class KMeanpp implements Runnable {
		private final int from;
		private final int to;
		private final O lastCentroid;
		private final float[] dFromNNCentroid;
		private final float[] dFromNNCentroidSQR;
		private final O[] objects;
		
		KMeanpp(O[] objects, O lastCentroid, int from, int to, float[] dFromNNCentroid, float[] dFromNNCentroidSQR) {
			this.from = from;
			this.to = to;
			this.lastCentroid = lastCentroid;
			this.objects = objects;
			this.dFromNNCentroid = dFromNNCentroid;
			this.dFromNNCentroidSQR = dFromNNCentroidSQR;
		}
		
//		public void set(O lastCentroid) {
//			this.lastCentroid = lastCentroid;
//		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				if ( dFromNNCentroid[iO] == 0.0 ) continue;
				float dist = (float) sim.distance(objects[iO], lastCentroid, dFromNNCentroid[iO] );
				if ( dist >= 0 && dist < dFromNNCentroid[iO]) {
					dFromNNCentroid[iO]=dist;
					dFromNNCentroidSQR[iO]=dist*dist;
				}		
				
			}
		}
	}
	
	
	public final void kMeansPP(int k) throws InterruptedException {
		
		int nObjs_kMPP = k*10;
		if ( nObjs_kMPP > objects.length) nObjs_kMPP=objects.length;
		Log.info("kMeans++ will use " + nObjs_kMPP + " randomly selected objects");
		O[] objects_kMPP = (O[]) RandomOperations.getRandomObjects(objects, nObjs_kMPP);
		
		Log.info("Performing k-means++:");
		O[] initCentroids = (O[]) Array.newInstance(objects_kMPP[0].getClass(), k);	
		
		// first centroid is taken randomly
		initCentroids[0]= (O) objects_kMPP[RandomOperations.getInt(objects_kMPP.length)];
		
		final float[] dFromNNCentroid = new float[objects_kMPP.length];
		final float[] dFromNNCentroidSQR = new float[objects_kMPP.length];
		Arrays.fill(dFromNNCentroid, Float.MAX_VALUE);
		

		int bookedThreads = ParallelOptions.reserveNFreeProcessors() ;
		if ( bookedThreads < 1) {
			for ( int i=1; i<initCentroids.length; i++ ) {
				Log.info_verbose_progress(tm, i, initCentroids.length);
				
				for (int iO = 0; iO<objects_kMPP.length; iO++) {
					O curr = objects_kMPP[iO];
	
					float dist = (float) sim.distance(curr, initCentroids[i-1], dFromNNCentroid[iO] );
					if ( dist >= 0 && dist < dFromNNCentroid[iO]) {
						dFromNNCentroid[iO]= dist;	
						dFromNNCentroidSQR[iO] = dist*dist;
					}		
					
				}
				
				double sqSum = 0;
				for (int iO = 0; iO<objects_kMPP.length; iO++) {
					// new centroid will be choosen with squared distance probability
					sqSum+=dFromNNCentroidSQR[iO];
				}
			
				if ( sqSum == 0.0 ) break;
				
				// selecting next centroid random weighted squared distance
				double rnd = RandomOperations.getDouble(sqSum);
				double tempSum = 0;
				int selected = 0;
				for ( selected=0; true; selected++ ) {				
					tempSum += dFromNNCentroidSQR[selected];
					if ( tempSum >= rnd ) break;
				}
				if ( dFromNNCentroidSQR[selected] == 0.0 ) System.err.println("Duplicated centroid was selected by kMeams++");
				initCentroids[i]=objects_kMPP[selected];
				
			}
				
		} else {
			// kNNQueues are performed in parallels
			final int nObjsPerThread = (int) Math.ceil((double) objects_kMPP.length / (bookedThreads+1));
			final int nThread = (int) Math.ceil((double) objects_kMPP.length / nObjsPerThread);
			
			
			for ( int i=1; i<initCentroids.length; i++ ) {
				Log.info_verbose_progress(tm, i, initCentroids.length);
				
				
				Thread[] thread = new Thread[nThread];
				int ti = 0;
				for ( int from=0; from<objects_kMPP.length; from+=nObjsPerThread) {
		        	int to = from+nObjsPerThread-1;
		        	if ( to >= objects_kMPP.length ) to = objects_kMPP.length-1;
					thread[ti] = new Thread( new KMeanpp(objects_kMPP,initCentroids[i-1],  from, to, dFromNNCentroid, dFromNNCentroidSQR)  ) ;
		        	thread[ti].start();
		        	ti++;
				}
				
		        for ( Thread t : thread ) {
		        	t.join();
		        }
		        
		        double sqSum = 0;
				for (int iO = 0; iO<objects_kMPP.length; iO++) {
					//new centroid will be choosen with squared distance probability
					sqSum+=dFromNNCentroidSQR[iO];
				}		
				
				if ( sqSum == 0.0 ) break;
				// selecting next centroid random weighted squared distance
				double rnd = RandomOperations.getDouble(sqSum);
				double tempSum = 0;
				int selected = 0;
				for ( selected=0; true; selected++ ) {				
					tempSum += dFromNNCentroidSQR[selected];
					if ( tempSum >= rnd ) break;
				}


				initCentroids[i]=objects_kMPP[selected];
			}
			ParallelOptions.free(bookedThreads);
		}
		
		//Log.info_verbose_indent( "done." );
		centroids = initCentroids;
		

	}
	
	
	public int getNNotNullCentroids() {

		int count =0;
		for ( int i=0; i<centroids.length; i++ ) {
			if ( centroids[i] != null ) {
				count++;
			}
		}

		return count;
	}
	
	public O[] getCentroids(boolean notNulls ) {
		if ( notNulls) {
			
			ArrayList<O> temp = new ArrayList<O>( centroids.length);
			for ( int i=0; i<centroids.length; i++ ) {
				if ( centroids[i] != null ) {
					temp.add(centroids[i]);
				}
			}
			O[] res = (O[]) Array.newInstance(temp.get(0).getClass(), temp.size());
			
			return (O[]) temp.toArray(res);
		}
		return centroids;
	}
	
	// For assigning
	class KMeanAss implements Runnable {
		private final int from;
		private final int to;
		private final int currlastNCentroidChanges;

		KMeanAss(int currlastNCentroidChanges, int from, int to) {
			this.from = from;
			this.to = to;
			this.currlastNCentroidChanges = currlastNCentroidChanges;
		}

		@Override
		public void run() {
			for (int iObj = from; iObj <= to; iObj++) {
				O obj = objects[iObj];

				if (currlastNCentroidChanges != centroids.length && centroidChanged[assignedCentroid[iObj]] == false) {
					// assigned centroid has not changed
					float minDist = centroidDistance[iObj];
					int nearest = assignedCentroid[iObj];
					
					for (int iChanged = 0; iChanged < centroidChangedIndexes.length
							&& centroidChangedIndexes[iChanged] > 0; iChanged++) {
						int currI = centroidChangedIndexes[iChanged];
						if (centroids[currI] == null)
							continue;
						
						// Searching nearest centroid (only between changed)
						float dist = (float) sim.distance(obj,
								centroids[currI],
								minDist);
						
						if (dist >= 0 && dist < minDist) {
							minDist = dist;
							nearest = currI;
						} 
						
						/*if (dist > 0 && dist < centroidDistance[iObj]) {
							// assign object to centroid
							centroidDistance[iObj] = dist;
							// clusters assignedCentroid[iObj] & currI will be affected
							assignedCentroid[iObj] = currI;
						}*/
					}
					
					if ( nearest != assignedCentroid[iObj] ) {
						// clusters assignedCentroid[iObj] & nearest will be affected
						clusterChanged[assignedCentroid[iObj]]  = true;
						clusterChanged[nearest]  = true;
						
						centroidDistance[iObj] = minDist;
						assignedCentroid[iObj] = nearest;
					}

				} else {
					
					// old centroid changed. Searching new one between all
					float minDist = Float.MAX_VALUE;
					int nearest = -1;
					for (int i = 0; i < centroids.length; i++) {
						if (centroids[i] == null)
							continue;
						float dist = (float) sim.distance(obj, centroids[i], minDist);
						if (dist >= 0 && dist < minDist) {
							minDist = dist;
							nearest = i;
						} 
					}
					// clusters assignedCentroid[iObj] & nearest will be affected
					clusterChanged[assignedCentroid[iObj]]  = true;
					clusterChanged[nearest]  = true;
					
					// Assign to nearest
					centroidDistance[iObj] = minDist;
					assignedCentroid[iObj] = nearest;

				}
			}
		}
	}
	
	public O[] runAlgorithm(File tempWordsOutFile) throws IOException, InterruptedException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		System.out.println("Performing k-Means.");
		
		int nCentroidsChanges = centroids.length;
		int lastNCentroidChanges = centroids.length;
		int count = 0; 
		
		long lastTimeMillis = System.currentTimeMillis();
		System.out.println(
				"\tnIter"	+
				"\tc"+
				"\tchg"+			
				"\tdistortion" +		
				"\tdistRed" +	
				"\tsecs" );		
		
		clusters = new ArrayList[centroids.length];
		for ( int i=0; i<clusters.length; i++) {
			clusters[i]=new ArrayList();
		}
		
		clusterChanged = new boolean[centroids.length];
		assignedCentroid = new int[objects.length];
		centroidDistance = new float[objects.length];
		
		
		for (int iterations=0; nCentroidsChanges>0; iterations++ ){
			
			Arrays.fill(clusterChanged, false);
			
			// Clear
			for ( int i=0; i<clusters.length; i++) {
				clusters[i].clear();
			}		
			
			final int currlastNCentroidChanges = lastNCentroidChanges;		
			
			int bookedProcessors = ParallelOptions.reserveNFreeProcessors();
			if ( bookedProcessors < 1 ) {
			
				// checking and assigning centroids to objects
				for (int iObj = 0; iObj < objects.length; iObj++) {
					O obj = objects[iObj];
	
					if (currlastNCentroidChanges != centroids.length && centroidChanged[assignedCentroid[iObj]] == false) {
						// assigned centroid has not changed
						float minDist = centroidDistance[iObj];
						int nearest = assignedCentroid[iObj];
						
						for (int iChanged = 0; iChanged < centroidChangedIndexes.length
								&& centroidChangedIndexes[iChanged] > 0; iChanged++) {
							int currI = centroidChangedIndexes[iChanged];
							if (centroids[currI] == null)
								continue;
							
							// Searching nearest centroid (only between changed)
							float dist = (float) sim.distance(obj,
									centroids[currI],
									minDist);
							
							if (dist >= 0 && dist < minDist) {
								minDist = dist;
								nearest = currI;
							} 
							
							/*if (dist > 0 && dist < centroidDistance[iObj]) {
								// assign object to centroid
								centroidDistance[iObj] = dist;
								// clusters assignedCentroid[iObj] & currI will be affected
								assignedCentroid[iObj] = currI;
							}*/
						}
						
						if ( nearest != assignedCentroid[iObj] ) {
							// clusters assignedCentroid[iObj] & nearest will be affected
							clusterChanged[assignedCentroid[iObj]]  = true;
							clusterChanged[nearest]  = true;
							
							centroidDistance[iObj] = minDist;
							assignedCentroid[iObj] = nearest;
						}
	
					} else {
						
						// old centroid changed. Searching new one between all
						float minDist = Float.MAX_VALUE;
						int nearest = -1;
						for (int i = 0; i < centroids.length; i++) {
							if (centroids[i] == null)
								continue;
							float dist = (float) sim.distance(obj, centroids[i], minDist);
							if (dist >= 0 && dist < minDist) {
								minDist = dist;
								nearest = i;
							} 
						}
						// clusters assignedCentroid[iObj] & nearest will be affected
						clusterChanged[assignedCentroid[iObj]]  = true;
						clusterChanged[nearest]  = true;
						
						// Assign to nearest
						centroidDistance[iObj] = minDist;
						assignedCentroid[iObj] = nearest;
	
					}
				}
			} else {
				// kNNQueues are performed in parallels
				final int nObjsPerThread = (int) Math.ceil((double) objects.length / (bookedProcessors+1) );
				final int nThread = (int) Math.ceil((double) objects.length / nObjsPerThread);
				int ti = 0;
		        Thread[] thread = new Thread[nThread];
		        for ( int from=0; from<objects.length; from+=nObjsPerThread) {
		        	int to = from+nObjsPerThread-1;
		        	if ( to >= objects.length ) to = objects.length-1;
		        	thread[ti] = new Thread( new KMeanAss(currlastNCentroidChanges, from, to) ) ;
		        	thread[ti].start();
		        	ti++;
		        }
		        
		        for ( ti=0; ti<thread.length; ti++ ) {
					thread[ti].join();
		        }
		        ParallelOptions.free(bookedProcessors );
			}
			
			
			double newDistortion = 0.0;
			
			// each object is assigned to a cluster and distortion incremented
			for (int iObj = 0; iObj<objects.length; iObj++) {
				clusters[assignedCentroid[iObj]].add(objects[iObj]);
				newDistortion+= centroidDistance[iObj]*centroidDistance[iObj];
			}
			newDistortion /= objects.length;


			
			System.out.println(
					"\t" + iterations +
					"\t" + getNNotNullCentroids() + 	
					"\t" + nCentroidsChanges +
					"\t" + newDistortion +
					"\t" + (1.0-newDistortion/currDistortion) +
					"\t" + (System.currentTimeMillis()-lastTimeMillis)/1000);
			
			lastTimeMillis = System.currentTimeMillis();
			if ( (newDistortion<Double.MAX_VALUE) && (1.0 - newDistortion / currDistortion) < distRedThr) {
				Log.info("Distortion reduction was less than threshold " + distRedThr );
				break;
			}
			
			
			if  ( tm.getTime() >= maxMillisPerIteration ) {
				Log.info("Execution time limit of " + maxMillisPerIteration + " was reached." );
				break;
			}
			
			
			lastNCentroidChanges = nCentroidsChanges;
			nCentroidsChanges = evaluateCentroids();			
			currDistortion = newDistortion;
			
			if ( nCentroidsChanges == 0 ) {
				Log.info("Centroids did not change on last step.");
			}

			if ( tempWordsOutFile != null ) {
				if ( sim instanceof ILFSimilarity) {
					LFWords words = new LFWords(getCentroids(true), (ILFSimilarity) sim);
					DataOutputStream out = new DataOutputStream(new FileOutputStream(tempWordsOutFile + "_" + iterations + ".dat"));
					words.writeData(out);				
					out.close();
				} else {
					Centroids centroids = new Centroids((AbstractFeature[]) getCentroids(true));
					DataOutputStream out = new DataOutputStream(new FileOutputStream(tempWordsOutFile + "_" + iterations + ".dat"));
					centroids.writeData(out);				
					out.close();
				}
			}
			
//			if ( lastCentroidChanges == centroidsChanges ) count++;
//			else count = 0;
//			System.out.print(" " + centroidsChanges);
//			if ( iterations % 10 == 0) System.out.println("");
//			if ( count == eqCentroidsChange_Max ) {
//				System.out.println("\nCentroids changes were " + centroidsChanges + " for the last " + eqCentroidsChange_Max + " cycles.");
//				break; 
//			}

			
		}
		
		System.out.println();
		System.out.print("clusters sizes:");
		long[] size = getClusterSizes();
		for ( long l : size ) {
			System.out.print("\t" + l ) ;
		}
		System.out.println();
		/*
		if ( centroids[0] instanceof IHasID ) {
			for ( int i=0; i<centroids.length; i++ ) {
				Log.info(((IHasID) centroids[i]).getID().toString());
			}
		}
		*/
		return centroids;
		
	}
	
	public O getMedoid(Collection<O> cluster) {
		double minSum = Double.MAX_VALUE;
		O best = null;
		for (Iterator<O> it1 = cluster.iterator(); it1.hasNext(); ) {
			O candidate = it1.next();
			double candidateSum = 0;
			for (Iterator<O> it2 = cluster.iterator(); it2.hasNext(); ) {
				O curr = it2.next();
				candidateSum += sim.distance(candidate, curr);
				if ( candidateSum > minSum ) break;
			}
			if ( candidateSum < minSum ) {
				best = candidate;
				minSum = candidateSum;
			}
		}
		return best;
		
	}
	
	public int evaluateCentroids() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int changesCount = 0;
		
		if ( centroidChangedIndexes == null ) {
			centroidChangedIndexes = new int[centroids.length];
		} 
		if ( centroidChanged == null ) {
			centroidChanged = new boolean[centroids.length];
		} 
		
		// iterating on centroids
		for ( int i=0; i<centroids.length; i++) {
			
			// if old centroids is null it is kept null
			if ( centroids[i] == null || !clusterChanged[i] ) {
				centroidChanged[i]= false;
				continue;
			}
			
			O newCentroid = null;
			if ( isMedoid == true ) {
				newCentroid = getMedoid(clusters[i]);
			} else {
				newCentroid = meanEval.getMean(clusters[i]);
				
				if ( newCentroid != null && AbstractFeaturesCollector.class.isAssignableFrom(objectsClass)) {
					newCentroid = objectsClass.getConstructor(AbstractFeature.class).newInstance(newCentroid);
				}				
			}
			
			// checks for duplicated centroids
			if ( newCentroid != null ) {
				for ( int j=0;j<i;j++) {
					if ( centroids[j]!=null && sim.distance(centroids[j],newCentroid)== 0.0) {
						newCentroid = null;
						break;
					}
				}
			}
			
			if ( newCentroid == null ) {
				// previous was not null
				centroids[i] = newCentroid;
				centroidChanged[i]= true;
				changesCount++;
				continue;
			}
			
			// centroids changed?
			if ( sim.distance(centroids[i],newCentroid)!= 0 ) {
				centroids[i] = newCentroid;
				centroidChangedIndexes[changesCount]= i;
				changesCount++;
				centroidChanged[i]= true;
			} else {
				centroidChanged[i]= false;
			}
		}
		if ( changesCount < centroidChangedIndexes.length ) {
			centroidChangedIndexes[changesCount]= -1;
		}
		
		//if ( equalsCentroidsExist() ) System.err.println("equalCentroidsExist");
		return changesCount;
	}

	public long[] getClusterSizes() {
		long[] clustersSize = new long[centroids.length];
		for ( int i=0; i<clusters.length; i++) {
			clustersSize[i]=clusters[i].size();
		}
		return clustersSize;
	}
	
	public boolean equalsCentroidsExist() {
		return equalsExist(centroids);
	}
	
	public boolean equalsExist(O[] centroids) {
		for (int i=0; i<centroids.length; i++) {
			O c = centroids[i];
			if ( c == null ) continue;
			for (int j=i+1; j<centroids.length; j++) {
				if ( centroids[j] == null ) continue;
				if ( sim.distance(c,centroids[j])== 0.0)
					return true;				
			}
			
		}
		return false;
	}
	
}
