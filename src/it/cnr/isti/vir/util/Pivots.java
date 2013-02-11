package it.cnr.isti.vir.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Pivots {
	
	public static final double getTrMatrixAvg(double[][] intDist) {
		double avg = 0;
		// i=0 is not useful
		
		if ( intDist == null ) return -1;
		int count = 0;
		for ( int i=1; i<intDist.length; i++ ) {
			double temp = 0;
			for ( int j=0; j<intDist[i].length; j++ ) {
				temp = intDist[i][j];
				avg += temp;
			}
			count += intDist[i].length;
		}
		return avg / (double) count;
	}
	
	public static final Collection<Integer> reordering(Integer triesMax, Integer nObjects, float[][] intDist ) {
		return reordering( triesMax, nObjects, intDist, intDist.length);
	}
	
	public static final Collection<Integer> reordering(Integer triesMax, Integer nObjects, float[][] intDist, int nToOrder) {
		
		int size=intDist.length;
		
//		if ( nObjects != null ) {
//			System.out.print("Random reordering");			
//			LinkedList<Integer> ordList = RandomOperations.getRandomOrderedIntegers(size);
//			System.out.print(" ... done.\n");
//			return ordList;
//		}
		
		// initialize list of indexes with a random ordered list of objects
		LinkedList<Integer> list = new LinkedList(RandomOperations.getRandomOrderedIntegers(size));
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(size);
		
		double[][] last = new double[size][];
		for ( int i=1; i<size; i++ ) {
			last[i] = new double[i];
		}
		
		// at each step in finding new order
		while ( orderedList.size() < nToOrder ) {
			if ( orderedList.size() % 10 == 0 ) System.out.println("--> reordering pivots cycle " + orderedList.size());
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.reorderLinkedList(list);
			
			// for each not ordered image (not more then triesMax)
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					for ( int j=0; j<i; j++) {
						double d1 = 0;
						
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr )d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;

						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr )d2 = intDist[curr][j];
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
					else if ( i < best )d1 = intDist[best][i];
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
	
	public static final ArrayList<Integer> search(Integer triesMax, Integer nObjects, float[][] intDist, int nPivots) {
		
		int size=intDist.length;
		
//		if ( nObjects != null ) {
//			System.out.print("Random reordering");			
//			LinkedList<Integer> ordList = RandomOperations.getRandomOrderedIntegers(size);
//			System.out.print(" ... done.\n");
//			return ordList;
//		}
		
		// initialize list of indexes with a random ordered list of objects
		LinkedList<Integer> list = new LinkedList(RandomOperations.getRandomOrderedIntegers(size));
		
		ArrayList<Integer> res = new ArrayList<Integer>(size);
		
		double[][] last = new double[size][];
		for ( int i=1; i<size; i++ ) {
			last[i] = new double[i];
		}
		
		// at each step in finding new order
		while ( res.size() < nPivots ) {
			System.out.println("--> searching pivot number " + res.size());
			Integer best = null;
			double bestSum = -1;
			
			if ( triesMax != null && list.size() > triesMax ) RandomOperations.reorderLinkedList(list);
			
			// for each object not pivot
			int triesCount = 0;
			for (	Iterator<Integer> it = list.iterator();
					it.hasNext() && ( triesMax == null || triesCount < triesMax);
					triesCount++) {
				int curr = it.next();
				double currSum = 0;
				
				// consider only first nObjects
				for ( int i=0; i<size && ((nObjects==null) || i<nObjects); i++ ) {
					for ( int j=0; j<i; j++) {
						double d1 = 0;
						
						if ( i > curr ) d1 = intDist[i][curr];
						else if ( i < curr )d1 = intDist[curr][i];
						//else d1 = 0;
						
						double d2 = 0;

						if ( j > curr ) d2 = intDist[j][curr];
						else if ( j < curr )d2 = intDist[curr][j];
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
			
			res.add( best ); //add to orderedList
			list.remove(best); // removes from to do list
			
			// last updating using new 
			for ( int i=0; i<last.length; i++ ) {
				for ( int j=0; j<i; j++) {
					double d1 = 0;
					if ( i > best ) d1 = intDist[i][best];
					else if ( i < best )d1 = intDist[best][i];
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
		
		return res;
		
	}
	
	
//	public void randomReordering() {
//		reorder(getRandomOrderedIntegers(qObj.length));
//	}

}
