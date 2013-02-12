/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
