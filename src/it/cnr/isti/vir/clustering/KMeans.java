package it.cnr.isti.vir.clustering;

import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.similarity.ILFSimilarity;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.util.RandomOperations;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class KMeans<O> {

	private final IMeanEvaluator<O> meanEval;
	private final ISimilarity<O> sim;
	private final ArrayList<O> objects;
	
	private double[] centroidDistance;
	private int[] assignedCentroid;
	
	private O[] centroids;
	private Collection<O>[] clusters;
	
	private int[] centroidChangedIndexes;
	private boolean[] centroidChanged;
	
	private static final int eqCentroidsChange_Max = 100;
	
	private double distRedThr = 0.9999;
	
	public double getDistRedThr() {
		return distRedThr;
	}

	public void setDistRedThr(double distRedThr) {
		this.distRedThr = distRedThr;
	}

	public KMeans(ArrayList<O> objects, O[] initCentroids, ISimilarity<O> sim) {
		this.centroids = initCentroids;
		this.objects = objects;
		this.meanEval = (IMeanEvaluator<O>) sim;
		this.sim = sim;
	}
	
	public KMeans(ArrayList<O> objects, ISimilarity<O> sim) {
		
		this.objects = objects;
		this.meanEval = (IMeanEvaluator<O>) sim;
		this.sim = sim;
		randomOrdering();
		
	}
	
	public final void randomOrdering() {
		// InitCentroids
		System.out.print("Random reordering data ... ");
		RandomOperations.reorderArrayList(objects);
		System.out.println(" done.");
	}
	
	public final void randomInitCentroids(int k) {

		HashSet<Integer> selected = new HashSet<Integer>();
		O[] initCentroids = (O[]) Array.newInstance(objects.iterator().next().getClass(), k);		
		for ( int i=0; i<initCentroids.length; i++ ) {
			int rnd = -1;
			do {
				rnd = RandomOperations.getInt(0, objects.size());
			} while ( selected.contains(rnd));
			selected.add(rnd);
			initCentroids[i]= (O) objects.get(rnd);
		}
		centroids = (O[]) initCentroids;
	}
	
	public final void kMeansPP(int k) {
		System.out.print("Performing k-means++:\n   ");
		O[] initCentroids = (O[]) Array.newInstance(objects.iterator().next().getClass(), k);	
		
		// first centroid is taken randomly
		initCentroids[0]= (O) objects.get( RandomOperations.getInt(0, objects.size()) );
		
		final double[] dFromNNCentroid = new double[objects.size()];
		Arrays.fill(dFromNNCentroid, Double.MAX_VALUE);
		
		// For parallel
//		final int nObjPerThread = (int) Math.ceil( objects.size() / ParallelOptions.nThreads);
//		ArrayList<Integer> arrList = new ArrayList(objects.size());
//		for (int iO = 0; iO<objects.size(); iO+=nObjPerThread) {
//			arrList.add(iO);
//		}
		
		for ( int i=1; i<initCentroids.length; i++ ) {
			if ( i%10 == 0 ) System.out.print(" "+i);
			
			
//			if ( false ) { //serial
				// for each object 
				for (int iO = 0; iO<objects.size(); iO++) {
					O curr = objects.get(iO);
	
					double dist = sim.distance(curr, initCentroids[i-1], dFromNNCentroid[iO] );
					if ( dist > 0 && dist < dFromNNCentroid[iO]) {
						dFromNNCentroid[iO]=dist;
					}		
	
				}
//			} else {
//				final O currCentr = initCentroids[i-1];
//				
//				Parallel.forEach(arrList, new Function<Integer, Void>() {
//					public Void apply(Integer i) {
//							int max = i+nObjPerThread;
//							if ( max > objects.size() )
//								max = objects.size();
//							for (int iO = i; iO<max; iO++) {
//								O curr = objects.get(iO);
//								double dist = sim.distance(curr, currCentr, dFromNNCentroid[iO] );
//								if ( dist > 0 && dist < dFromNNCentroid[iO]) {
//									dFromNNCentroid[iO]=dist;
//								}	
//							}
//							return null;
//						}
//				});
//			}
			
			double sqSum = 0;
			for (int iO = 0; iO<objects.size(); iO++) {
				//new centroid will be choosen with squared distance probability
				sqSum+=dFromNNCentroid[iO]*dFromNNCentroid[iO];
			}
		
			// selecting next centroid random weighted squared distance
			double rnd = RandomOperations.getDouble(sqSum);
			double tempSum = dFromNNCentroid[0];
			int selected = 0;
			for ( selected=1; tempSum < rnd; selected++ ) {
				double currValue = dFromNNCentroid[selected];
				tempSum += currValue*currValue;
			}
			initCentroids[i]=objects.get(selected);
			
		}	
		System.out.println( " " + initCentroids.length + "." );
		centroids = initCentroids;
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
	
	public O[] runAlgorithm(File tempWordsOutFile) throws IOException {
		System.out.println("Performing k-Means.");
		
		int nCentroidsChanges = centroids.length;
		int lastNCentroidChanges = centroids.length;
		int count = 0; 
		double currDistortion = Double.MAX_VALUE;
		long lastTimeMillis = System.currentTimeMillis();
		System.out.println(
				"\tnIter"	+
				"\tchanges"	+			
				"\tdRed" +	
				"\tsecs" );		
		
		clusters = new ArrayList[centroids.length];
		for ( int i=0; i<clusters.length; i++) {
			clusters[i]=new ArrayList();
		}
		
		assignedCentroid = new int[objects.size()];
		centroidDistance = new double[objects.size()];
		
		
		// For parallel
//		ArrayList<Integer> arrList = new ArrayList(objects.size());
//		final int nObjPerThread = (int) Math.ceil( objects.size() / ParallelOptions.nThreads);
//		for (int iO = 0; iO<objects.size(); iO+=nObjPerThread) {
//			arrList.add(iO);
//		}
		
		for (int iterations=0; nCentroidsChanges>0; iterations++ ){
			
			if ( tempWordsOutFile != null ) {
				LFWords words = new LFWords(getCentroids(true), (ILFSimilarity) sim);
				DataOutputStream out = new DataOutputStream(new FileOutputStream(tempWordsOutFile + "_" + iterations + ".dat"));
				words.writeData(out);				
				out.close();
			}
			
			double newDistortion = 0.0;
			
			// Clear
			for ( int i=0; i<clusters.length; i++) {
				clusters[i].clear();
			}
			
			
			
			final int currlastNCentroidChanges = lastNCentroidChanges;
			// checking and assigning centroids to objects
			for (int iObj = 0; iObj < objects.size(); iObj++) {
				O obj = objects.get(iObj);

				if (currlastNCentroidChanges != centroids.length
						&& centroidChanged[assignedCentroid[iObj]] == false) {
					// assigned centroid has not changed
					for (int iChanged = 0; iChanged < centroidChangedIndexes.length
							&& centroidChangedIndexes[iChanged] > 0; iChanged++) {
						int currI = centroidChangedIndexes[iChanged];
						if (centroids[currI] == null)
							continue;
						// Searching nearest centroid (between changed)
						double dist = sim.distance(obj,
								centroids[currI],
								centroidDistance[iObj]);
						if (dist > 0 && dist < centroidDistance[iObj]) {
							// assign object to centroid
							centroidDistance[iObj] = dist;
							assignedCentroid[iObj] = currI;
						}
					}


				} else {
					// old centroid changed. Searching new one
					double minDist = Double.MAX_VALUE;
					int nearest = -1;
					for (int i = 0; i < centroids.length; i++) {
						if (centroids[i] == null)
							continue;
						double dist = sim.distance(obj, centroids[i],
								minDist);
						if (dist > 0 && dist < minDist) {
							minDist = dist;
							nearest = i;
						}
					}
					// Assign to nearest
					centroidDistance[iObj] = minDist;
					assignedCentroid[iObj] = nearest;

				}
			}
			
			
			// PARALLEL with Conja
			/*
			final int currlastNCentroidChanges = lastNCentroidChanges;
			// Parallel
			Parallel.forEach(arrList, new Function<Integer, Void>() {
				public Void apply(Integer p) {
					int max = p + nObjPerThread;
					if (max > objects.size())
						max = objects.size();

					// checking and assigning centroids to objects
					for (int iObj = p; iObj < max; iObj++) {
						O obj = objects.get(iObj);

						if (currlastNCentroidChanges != centroids.length
								&& centroidChanged[assignedCentroid[iObj]] == false) {
							// assigned centroid has not changed
							for (int iChanged = 0; iChanged < centroidChangedIndexes.length
									&& centroidChangedIndexes[iChanged] > 0; iChanged++) {
								int currI = centroidChangedIndexes[iChanged];
								if (centroids[currI] == null)
									continue;
								// Searching nearest centroid (between changed)
								double dist = sim.distance(obj,
										centroids[currI],
										centroidDistance[iObj]);
								if (dist > 0 && dist < centroidDistance[iObj]) {
									// assign object to centroid
									centroidDistance[iObj] = dist;
									assignedCentroid[iObj] = currI;
								}
							}


						} else {
							// old centroid changed. Searching new one
							double minDist = Double.MAX_VALUE;
							int nearest = -1;
							for (int i = 0; i < centroids.length; i++) {
								if (centroids[i] == null)
									continue;
								double dist = sim.distance(obj, centroids[i],
										minDist);
								if (dist > 0 && dist < minDist) {
									minDist = dist;
									nearest = i;
								}
							}
							// Assign to nearest
							centroidDistance[iObj] = minDist;
							assignedCentroid[iObj] = nearest;

						}
					}
					return null;
				}
			});*/
			
			for (int iObj = 0; iObj<objects.size(); iObj++) {
				clusters[assignedCentroid[iObj]].add(objects.get(iObj));
				newDistortion+= centroidDistance[iObj]*centroidDistance[iObj];
			}
			

			lastNCentroidChanges = nCentroidsChanges;
			nCentroidsChanges = evaluateCentroids();
			
			System.out.println(
					"\t" + iterations +
					"\t" + nCentroidsChanges +
					"\t" + newDistortion/currDistortion +
					"\t" + (System.currentTimeMillis()-lastTimeMillis)/1000);
			
			lastTimeMillis = System.currentTimeMillis();
			if ( (newDistortion<Double.MAX_VALUE) && ((double) newDistortion / (double) currDistortion) >= distRedThr) {
				break;
			}
			
			currDistortion = newDistortion;
			
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
		
		return centroids;
		
	}
	
	
	public int evaluateCentroids() {
		int changesCount = 0;
		if ( centroidChangedIndexes == null ) {
			centroidChangedIndexes = new int[centroids.length];
		} 
		if ( centroidChanged == null ) {
			centroidChanged = new boolean[centroids.length];
		} 
		for ( int i=0; i<centroids.length; i++) {
			if ( centroids[i] == null ) {
				centroidChanged[i]= false;
				continue;
			}
			O newCentroid = meanEval.getMean(clusters[i]);
			if ( newCentroid == null ) {
				centroids[i] = newCentroid;
				centroidChanged[i]= true;
				changesCount++;
				continue;
			}
			
			// centroids changed?
			if ( !centroids[i].equals(newCentroid) ) {
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
		return changesCount;
	}

	public long[] getClusterSizes() {
		long[] clustersSize = new long[centroids.length];
		for ( int i=0; i<clusters.length; i++) {
			clustersSize[i]=clusters[i].size();
		}
		return clustersSize;
	}
	
}
