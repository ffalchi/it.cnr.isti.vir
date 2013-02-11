package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SimPQueueLowe2Labels<O> extends SimilarityPQueue<O> {

	private AbstractLabel bestClabel = null;
	private AbstractLabel secondClabel = null;
	
	private ArrayList<ObjectWithDistance<O>> bestArr = new ArrayList(100);
	private O second = null;
	
	private double bestDist = Double.MAX_VALUE;
	// private double secondDist = Double.MAX_VALUE; // excDist
	
//	@Override
//	public double getExcDistance() {
//		if ( second != null ) return secondDist;
//		else return -Double.MAX_VALUE;
//	}

	@Override
	public Integer getK() {
		return null;
	}

	@Override
	public synchronized Double getLastDist() {
		if ( bestClabel == null ) return null;
		if ( secondClabel == null) return bestDist;
		return excDistance;
	}

	@Override
	public Double getRange() {
		return null;
	}

	@Override
	public synchronized ISimilarityResults getResults() {
		ObjectWithDistance<O>[] arr = getSortedArray();
		SimilarityResults res = new SimilarityResults(null, arr);
		return res;
	}

	@Override
	public synchronized void offer(O object, double distance) {
		if ( distance>=excDistance ) return;
		
		//ObjectWithDistance<LinkedFeature> currObj = new ObjectWithDistance<LinkedFeature>(linkedF, distance);
		//ClassLabel givenLabel = ((FeaturesCollectorHTwithIDClassified) linkedF.getCollection()).getLabel();
		AbstractLabel givenLabel = ((ILabeled) object).getLabel();
		
		if ( bestClabel == null ) {
			// initializing
			bestArr.add(new ObjectWithDistance(object, distance));
			bestClabel = givenLabel;
			bestDist = distance;
			return;
		}
		
		if ( givenLabel.equals( bestClabel ) ) {
			// same label of best point
			bestArr.add(new ObjectWithDistance(object, distance));
			bestDist = distance;
			return;
		}
				
		// givenLabel != bestClabel
		if ( distance<bestDist) {
			Collections.sort(bestArr);
			second = bestArr.get(0).getObj();
			secondClabel = bestClabel;
			excDistance   = bestDist;
			//full = true;
			bestArr.clear();
			bestArr.add(new ObjectWithDistance(object, distance));
			bestClabel   = givenLabel;
			bestDist	 = distance;
			return;
		}
		
		// just a new point after the best (with label != bestClabel)
		second = object;
		secondClabel = givenLabel;
		excDistance   = distance;
		//full = true;
		
		return;
	}

	@Override
	public synchronized int size() {
		if ( bestArr.size() == 0 ) return 0;
		if ( second == null ) return 1 + bestArr.size();
		return 2;
	}
	
	public synchronized final ObjectWithDistance<O>[] getSortedArray() {
		if ( bestClabel == null ) return null;
		int size = size();
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[size];
		Collections.sort(bestArr);
		for ( int i=0; i<size-1; i++) {
			arr[i] = bestArr.get(i);
		}
		
		arr[size-1] = new ObjectWithDistance(second, excDistance);;
//		if ( secondClabel == null ) {
//			arr[1] = new ObjectWithDistance(null, excDistance);;
//		} else {
//			arr[1] = new ObjectWithDistance(second, excDistance);;
//		}
		
		return arr;		
	}

	@Override
	public synchronized ISimilarityResults getResultsAndEmpty() {
		// TO DO!
		return getResults();
	}

	@Override
	public synchronized O getFirstObject() {
		Collections.sort(bestArr);
		return bestArr.get(0).getObj();
	}

}
