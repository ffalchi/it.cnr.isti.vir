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
package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.util.Pivots;
import it.cnr.isti.vir.util.RandomOperations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class QueriesOrder1 implements IQueriesOrdering{

	private final Integer triesMax;
	private final Integer nObjects ;
	
	public QueriesOrder1(int triesMax, int nObjects) {
		this.triesMax = triesMax;
		this.nObjects = nObjects;
	}
	
	public QueriesOrder1() {
		this.triesMax = null;
		this.nObjects = null;		
	}

	
	public String toString() {
		return this.getClass() + " (triesMax="+triesMax+" nObjects4Tries=" + nObjects +")";
	}
	
	public final ArrayList<Integer> getOrder(double[][] intDist) {
	
		int size=intDist.length;
		
//		if ( nObjects != null ) {
//			System.out.print("Random reordering");			
//			LinkedList<Integer> ordList = RandomOperations.getRandomOrderedIntegers(size);
//			System.out.print(" ... done.\n");
//			return ordList;
//		}
		
		// initialize list of indexes with a random ordered list of objects
		LinkedList<Integer> list = RandomOperations.getRandomOrderedLinkedListIntegers(size);
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(size);
		
		double[][] last = new double[size][];
		for ( int i=0; i<size; i++ ) {
			last[i] = new double[i];
			// initializing (probably not necessary)
			for ( int j=0; j<i; j++) {
				last[i][j] = 0;
			}
		}
		
		// at each step in finding new order
		while ( orderedList.size() < size ) {
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.reorderLinkedList(list);
			
			// for each not ordered image (not more then triesMax)
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount ++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					for ( int j=0; j<i; j++) {
						double d1 = 0;
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr ) d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;
						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr ) d2 = intDist[curr][j];
						//else d2 = 0;
						
						double abs = Math.abs( d1 - d2 );
						double temp = last[i][j];
						if ( abs > temp ) currSum += abs-temp;
						//currSum += Math.max(last[i][j], Math.abs( d1 - d2));
					}
				}			
				
				if ( currSum > bestSum ) {
					best = curr;
					bestSum = currSum;
				}				
			} // next best found
			
			orderedList.add( best ); //add to orderedList
			list.remove(best); // removes from to do list
			
			// last updating using new 
			for ( int i=0; i<last.length; i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > best ) d1 = intDist[i][best];
					else if ( i < best ) d1 = intDist[best][i];
					//else d1 = 0;
					
					double d2 = 0;
					if ( j > best ) d2 = intDist[j][best];
					else if ( j < best ) d2 = intDist[best][j];
					//else d2 = 0;
					
					last[i][j] = Math.max(last[i][j], Math.abs( d1 - d2 ));
				}
			}	
			
			
			//System.out.println(orderedList.size() + ":\t" +best);
			//System.out.print(".");
		}
		
		
		
		
		return orderedList;
	}
	
}
