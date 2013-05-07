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

import it.cnr.isti.vir.features.FeaturesCollectorHT;
import it.cnr.isti.vir.features.FeaturesCollectorHTwithID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

public abstract class KNN<ResultClass> {
	
	public final ISimilarity comp;
	public final FeaturesCollectorHT qObj;
	protected double excludingDistance = -1;
	
	public final int k;
	protected TreeSet<ObjectWithDistance<ResultClass>> set = new TreeSet<ObjectWithDistance<ResultClass>>();
		
	public KNN(FeaturesCollectorHT qObj, int k, ISimilarity comp ) {
		this.qObj = qObj;
		this.k = k;
		this.comp = comp;
		assert(k>0);
	}
	
	public KNN(KNN givenKNN) {
		this.comp = givenKNN.comp;
		this.qObj = givenKNN.qObj;
		this.k = givenKNN.k;
		for(Iterator<FeaturesCollectorHT> it=givenKNN.set.iterator(); it.hasNext();) {
			add(it.next());
		}
	}
	
	public void removeFirstIfAt0Dist() {
		ObjectWithDistance<ResultClass> obj = set.iterator().next();
		if ( obj.dist == 0 ) set.remove(obj);
	}	
	
	public Iterator<ObjectWithDistance<ResultClass>> iterator() {
		return set.iterator();
	}
	
	public final void addAll( Collection<FeaturesCollectorHT> objColl) {
		for(Iterator<FeaturesCollectorHT> it=objColl.iterator(); it.hasNext();) {
			add(it.next());
		}
	}

	public final void add( ObjectWithDistance<ResultClass> res ) {
		if ( res.dist <= excludingDistance || set.size() < k ) {
			set.add( res );
			check();
		}
	}
	
	public final void add( FeaturesCollectorHT givenObj ) {
		double qObjDist = comp.distance( qObj, givenObj );
		if ( qObjDist < excludingDistance || set.size() < k ) {
			this.addBlind( givenObj, qObjDist );
		}
	}

	
	public boolean equals( KNN<ResultClass> givenResults ) {
		if ( set.size() != givenResults.set.size() ) return false;
		if ( set.last().dist != givenResults.set.last().dist) return false;
			
		double kDist = set.last().dist;		
		
		for ( Iterator<ObjectWithDistance<ResultClass>> it1 = set.iterator(); it1.hasNext(); ) {
			ObjectWithDistance<ResultClass> res1 = it1.next();
			ObjectWithDistance<ResultClass> res2 = null;
			boolean found = false;
			for ( Iterator<ObjectWithDistance<ResultClass>> it2 = givenResults.set.iterator(); it2.hasNext(); ) {
				res2 = it2.next();
				if ( res1.obj.equals( res2.obj ) ) {
					found = true;
					break;
				}
			}
			
			if ( !found && res1.dist != kDist ) return false;
			if ( found && res1.dist != res2.dist ) return false;
		}
		
		//inverse
		for ( Iterator<ObjectWithDistance<ResultClass>> it1 = givenResults.set.iterator(); it1.hasNext(); ) {
			ObjectWithDistance<ResultClass>res1 = it1.next();
			ObjectWithDistance<ResultClass> res2 = null;
			boolean found = false;
			for ( Iterator<ObjectWithDistance<ResultClass>> it2 = set.iterator(); it2.hasNext(); ) {
				res2 = it2.next();
				if ( res1.obj.equals( res2.obj )) {
					found = true;
					break;
				}
			}
			
			if ( !found && res1.dist != kDist ) return false;
			if ( found && res1.dist != res2.dist ) return false;
		}
		
		return true;
		
	}

	protected abstract void addBlind( FeaturesCollectorHT givenObj, double dist );
	


	public final double getLastDist() {
		return excludingDistance;
	}
	public final boolean isFull() {
		return set.size() == k;
	}
	
	public final ObjectWithDistance<ResultClass> last() {
		return set.last();
	}
	
	/*
	 * First element is number 1
	 */
	public final Double getDistanceOf(int resultNumber){
		
		if ( resultNumber >= k ) return null;
		
		return getObjWithDistance(resultNumber).dist;
	}
	
	/*
	 * First element is number 1
	 */
	public final ResultClass getObject(int resultNumber){
		
		if ( resultNumber >= k ) return null;
		
		return getObjWithDistance(resultNumber).obj;
	}
	
	/*
	 * First element is number 1
	 */
	public final ObjectWithDistance<ResultClass> getObjWithDistance(int resultNumber ) {
		
		if ( resultNumber >= k ) return null;
		
		Iterator<ObjectWithDistance<ResultClass>> it = set.iterator();
		ObjectWithDistance<ResultClass> curr = null;
		for ( int i = 0; i<resultNumber; i++  ) {
			curr = it.next();
		}
		return curr;
	}
	
	public final synchronized ObjectWithDistance<ResultClass>[] getResultsArr() {
		ObjectWithDistance<ResultClass>[] arr = new ObjectWithDistance[set.size()];
		
		int i = 0;
		for ( Iterator<ObjectWithDistance<ResultClass>> it = set.iterator(); it.hasNext();  ) {
			arr[i++] = it.next();
		}
		
		return arr;
	}
	
	public String toString() {
		String str = qObj.toString() +"\t";
		for ( Iterator<ObjectWithDistance<ResultClass>> it = set.iterator(); it.hasNext(); ) {
			str += it.next() + "\n";
		}
		return str;
	}

	public final boolean isEmpty() {
		return set.isEmpty();
	}
	
	protected final void check() {
		if ( set.size() == k ) {
			excludingDistance = set.last().dist;
		}
		
		while ( set.size() > k ) {
			ObjectWithDistance<ResultClass> temp = set.last();
			set.remove( temp );
			excludingDistance = set.last().dist;
		}
	}
	
	public int getSize() {
		return set.size();
	}
	
	public int hashCode() {
		  assert false : "hashCode not designed";
		  return 42; // any arbitrary constant will do 
	  }

}
