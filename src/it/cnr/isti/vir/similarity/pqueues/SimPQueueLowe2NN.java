package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

public class SimPQueueLowe2NN<O> extends SimilarityPQueue<O> {

	private AbstractLabel bestClabel = null;
	private AbstractLabel secondClabel = null;
	
	private O best = null;
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
			best = object;
			bestClabel = givenLabel;
			bestDist = distance;
			return;
		}
		
		if ( givenLabel.equals( bestClabel ) ) {
			// same label of best point
			if ( distance < bestDist ) {
				best = object;
				bestDist = distance;
				return;
			}
			return;
		}
				
		// givenLabel != bestClabel
		if ( distance<bestDist) {
			second = best;
			secondClabel = bestClabel;
			excDistance   = bestDist;
			//full = true;
			best = object;
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
		if ( best == null ) return 0;
		if ( second == null ) return 1;
		return 2;
	}
	
	public synchronized final ObjectWithDistance<O>[] getSortedArray() {
		if ( bestClabel == null ) return null;
		
		
		if ( best == null) return null;
			
		if ( second == null) {
			ObjectWithDistance<O>[] arr =  new ObjectWithDistance[1];
			arr[0] = new ObjectWithDistance(best, bestDist);
			return arr;
		}
		
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[2];
		arr[0] = new ObjectWithDistance(best, bestDist);
		arr[1] = new ObjectWithDistance(second, excDistance);;

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
		return best;
	}

}
