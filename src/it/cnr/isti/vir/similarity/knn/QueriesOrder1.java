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
