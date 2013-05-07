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
package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

public class SimPQueueLowe2NN<O> extends AbstractSimPQueue<O> {

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
