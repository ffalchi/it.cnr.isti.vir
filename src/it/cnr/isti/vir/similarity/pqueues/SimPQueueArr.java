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

import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

import java.util.Arrays;

public class SimPQueueArr<O> extends AbstractSimPQueue<O> {

	int objCount = 0;
	final ObjectWithDistance<O> arr[];
	private final int size;
	
	public SimPQueueArr(int k) {
		size = k;
		arr = new ObjectWithDistance[size];
	}
	
	public void reset() {
		for (int i=0; i<objCount; i++) {
			arr[i]= null;
		}
		objCount = 0;
		excDistance = Double.MAX_VALUE;
	}
	
//	@Override
//	public double getExcDistance() {
//		if ( objCount<arr.length) return -Double.MAX_VALUE;
//		return arr[arr.length-1].dist;
//	}

	@Override
	public Integer getK() {
		return size;
	}

	@Override
	public Double getRange() {
		return Double.MAX_VALUE;
	}

	@Override
	public SimilarityResults getResults() {
		
		if ( objCount < size) {
			//System.out.println("CUTTED " + objCount + "/" + size);
			ObjectWithDistance[] tArr = Arrays.copyOf(arr, objCount);
			Arrays.sort(tArr);
			return new SimilarityResults(tArr);
		}
		//System.out.println("NOT CUTTED");
		Arrays.sort(arr);
		return new SimilarityResults(arr);
	}

	@Override
	public synchronized void offer(O obj, double distance) {

		if ( Double.isNaN(distance) ) {
			System.out.println("NaN Distance Offered to SimPQueueArr");
			return;
		}
 		
		if ( objCount < arr.length) {
			arr[objCount++] = new ObjectWithDistance(obj,distance);
			if ( objCount == size ) {
				Arrays.sort(arr);
				excDistance = arr[size-1].getDist();
			}
		} else if ( distance<arr[size-1].getDist() ) {
			arr[arr.length-1].reset(obj,distance);
			Arrays.sort(arr);
			excDistance = arr[size-1].getDist();
		}
		
	}

	@Override
	public int size() {
		return objCount;
	}


	@Override
	public boolean isFull() {
		return objCount >= arr.length;
	}


	@Override
	public Double getLastDist() {
		return arr[objCount-1].dist;
	}


	@Override
	public ISimilarityResults getResultsAndEmpty() {
		// TODO Auto-generated method stub
		return getResults();
	}


	@Override
	public O getFirstObject() {
		return arr[0].obj;
	}

	
}
