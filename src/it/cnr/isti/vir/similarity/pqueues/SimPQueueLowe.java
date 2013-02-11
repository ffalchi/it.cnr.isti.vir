package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

import java.util.Arrays;
import java.util.Iterator;

public class SimPQueueLowe<O> extends SimilarityPQueue<O>{
	

	private AbstractLabel bestClabel = null;
	private AbstractLabel secondClabel = null;
	
	private final SimPQueueDMax<O> bests = new SimPQueueDMax();
	private O second = null;
	
	private double bestDist = Double.MAX_VALUE; 
	//private double secondDist = Double.MAX_VALUE;  excDistance
	
	@Override
	public Integer getK() {
		return null;
	}

	@Override
	public Double getLastDist() {
		if ( bestClabel == null ) return null;
		if ( secondClabel == null) return bests.getLastDist();
		return excDistance;
	}

	@Override
	public Double getRange() {
		return null;
	}

	@Override
	public void offer(O object, double distance) {
		if ( distance>=excDistance ) return ;
		
		//ObjectWithDistance<LinkedFeature> currObj = new ObjectWithDistance<LinkedFeature>(linkedF, distance);
		//ClassLabel givenLabel = ((FeaturesCollectorHTwithIDClassified) linkedF.getCollection()).getLabel();
		AbstractLabel givenLabel = ((ILabeled) object).getLabel();
		
		if ( bestClabel == null ) {
			// initializing
			bests.offer( object, distance);
			bestClabel = givenLabel;
			bestDist = distance;
			return;
		}
		
		if ( givenLabel.equals( bestClabel ) ) {
			// same label of best point
			// !( distance < secondDist !
			bests.offer( object, distance);
			if ( distance < bestDist) {
				bestDist = distance;
			}
			return;
		}
				
		// givenLabel != bestClabel
		if ( distance<bestDist) {
			
			
			second = bests.getBestAndEmpty().getObj();
			secondClabel = bestClabel;
			excDistance   = bestDist;
			bests.offer( object, distance);
			bestClabel   = givenLabel;
			bestDist	 = distance;
			return;
		}
		
		// just a new point after the best (with label != bestClabel)
		second = object;
		secondClabel = givenLabel;
		excDistance  = distance;
		
		return;
	}

	@Override
	public int size() {
		int count = bests.size();
		if ( second != null ) count++;
		return count;
	}
	
	public final ObjectWithDistance<O>[] getSortedArray() {
		if ( secondClabel == null ) return null;
		int size = size();
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[size];
		Iterator<ObjectWithDistance<O>> it=bests.iterator();
		for (int i=0; i<bests.size(); i++) {
			arr[i] = it.next();
		}
		arr[size-1] = new ObjectWithDistance(second, excDistance);;
		Arrays.sort(arr);
		
		return arr;		
	}
	
	public final ObjectWithDistance<O>[] getSortedArrayAndEmpty() {
		if ( secondClabel == null ) return null;
		int size = size();
		
		ObjectWithDistance<O>[] bestsArr = bests.getSortedArrayAndEmpty();
		
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[size];
		for (int i=0; i<bests.size(); i++) {
			arr[i] = bestsArr[i];
		}
		arr[size-1] = new ObjectWithDistance(second, excDistance);;
		Arrays.sort(arr);
		
		return arr;		
	}

	@Override
	public ISimilarityResults getResults() {
		ObjectWithDistance<O>[] arr = getSortedArray();
		SimilarityResults res = new SimilarityResults(null, arr);
		return res;
	}

	@Override
	public ISimilarityResults getResultsAndEmpty() {
		ObjectWithDistance<O>[] arr = getSortedArrayAndEmpty();
		SimilarityResults res = new SimilarityResults(null, arr);
		return res;
	}
	
	@Override
	public O getFirstObject() {
		return bests.getFirstObject();
	}

}
