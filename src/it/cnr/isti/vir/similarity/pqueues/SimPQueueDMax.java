package it.cnr.isti.vir.similarity.pqueues;

import it.cnr.isti.vir.similarity.DecreasingComparator;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class SimPQueueDMax<O> extends SimilarityPQueue<O>{
	
	public final int k;
	public final double range;
	private static int initSize = 11;
	
	private static Comparator comp = new DecreasingComparator();
	
	final PriorityQueue<ObjectWithDistance<O>> pQueue;
	
	public static void setInitSize(int given ) {
		initSize = given;
	}
	
	public SimPQueueDMax() {
		k=-Integer.MAX_VALUE;
		range=-Double.MAX_VALUE;
		pQueue = new PriorityQueue<ObjectWithDistance<O>>(initSize, new DecreasingComparator());
	}
	
	public void empty() {
		while ( pQueue.poll() != null );
	}
	
	public ObjectWithDistance<O> getBestAndEmpty() {
		ObjectWithDistance<O> curr = pQueue.poll();
		while ( pQueue.peek() != null ) {
			curr = pQueue.poll();
		}
		return curr;
	}
	
	public void removeLast() {
		pQueue.poll();
	}
	
	public SimPQueueDMax(double range) {
		this.range = range;
		excDistance =  range + Double.MIN_VALUE;
		this.k = -1;
		pQueue = new PriorityQueue<ObjectWithDistance<O>>(initSize, comp);
	}
	
	public SimPQueueDMax(int k) {
		this.k = k;
		this.range = -1;
		pQueue = new PriorityQueue<ObjectWithDistance<O>>(k, comp);
	}

	@Override
	public final Integer getK() {
		return k;
	}

	@Override
	public final Double getRange() {
		return range;
	}
	
	
//	private final void setExcDistance() {
//		
//		if (pQueue.size()<k) {
//			excDistance = Double.MAX_VALUE;
//		}
//		double excK = Double.MAX_VALUE;
//		double excRange = Double.MAX_VALUE;
//		
//		if ( k>0 && pQueue.size()>=k ) {
//			excK = pQueue.peek().dist;
//		}
//				
//		if ( range >=0 ) excRange = range;
//		
//		excDistance =  Math.min(excK, excRange);
//	}

	@Override
	public final void offer(O object, double distance) {
		
		// excDistance takes also into account range!!!
		if ( distance >= excDistance) return ;
		
		if ( k<0 ) {
			pQueue.offer(new ObjectWithDistance(object, distance));
		} else { // k>= 0
			/// TEST ///
//			if ( pQueue.size() > k ) {
//				System.err.println("SimPQueueDMax has more than k objects");
//			}
			
			if ( pQueue.size() < k ) {
				pQueue.offer(new ObjectWithDistance(object, distance));
				if ( pQueue.size() == k ) {
					if ( range <0 ) excDistance = pQueue.peek().dist;
				}
			} else {// pQueue.size()==k
				ObjectWithDistance temp = pQueue.poll();
				temp.reset(object, distance);
				pQueue.offer(temp);
				if ( range <0 ) excDistance = pQueue.peek().dist;
			}
		}
	}
		
//		// excDistance also consider range!!!
//		if ( distance >= excDistance) return ;
//		
//		if ( pQueue.size() == k && k>=0 ) {
//			// we can safely poll because distance<pQueue.peek().dist
//			ObjectWithDistance temp = pQueue.poll();
//			temp.reset(object, distance);
//			pQueue.offer(temp);
//			if ( range < 0 ) excDistance = pQueue.peek().dist;
//			return;
//		}
//	
//		// distance < excDistnace < range
//		pQueue.offer(new ObjectWithDistance(object, distance));
		
		
		
		
		//if ( pQueue.size()== k && range < 0) excDistance = pQueue.peek().dist;
		
//		if ( (range <0 || distance<= range) // filtering for range
//			 &&
//			 (k <0 || pQueue.size()<k || distance<pQueue.peek().dist) // filtering for k
//			) {
//			
//			if ( k>=0 && pQueue.size() > k ) {
//				// we can safely poll because distance<pQueue.peek().dist
//				ObjectWithDistance old = pQueue.poll();
//				old.reset(object, distance);
//				pQueue.offer(old);
//			} else {
//				pQueue.offer(new ObjectWithDistance(object, distance));
//			}
//			
//			return true;
//		}
//		return false;

	
	public ObjectWithDistance<O>[] getSortedArray() {
		int size = pQueue.size();
		if ( size == 0 ) return null;
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[size];
		
		Iterator<ObjectWithDistance<O>> it=pQueue.iterator();
		for (int i=0; i<pQueue.size(); i++) {
			arr[i] = it.next();
		}
		Arrays.sort(arr);
		
		return arr;		
	}
	
	public ObjectWithDistance<O>[] getSortedArrayAndEmpty() {
		int size = pQueue.size();
		if ( size == 0 ) return null;
		ObjectWithDistance<O>[] arr = new ObjectWithDistance[size];
		
		for (int i=pQueue.size()-1; i>=0; i--) {
			arr[i] = pQueue.poll();
		}
		
		return arr;		
	}
	
	protected Iterator<ObjectWithDistance<O>> iterator() {
		return pQueue.iterator();
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

//	@Override
//	public ObjectWithDistance<ObjectClass> peek() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public ObjectWithDistance<ObjectClass> poll() {
//		// TODO Auto-generated method stub
//		return null;
//	}

	@Override
	public final int size() {
		if ( k>0 && pQueue.size()>=k ) return k;
		else return pQueue.size();
	}

	@Override
	public boolean isFull() {
		if (k<0) return false;
		return pQueue.size()>=k;
	}

	@Override
	public Double getLastDist() {
		return pQueue.peek().dist;
	}

	@Override
	public O getFirstObject() {
		if ( pQueue.size() == 0 ) return null;
		Iterator<ObjectWithDistance<O>> it=pQueue.iterator();
		ObjectWithDistance<O> best = it.next();
		for (int i=1; i<pQueue.size(); i++) {
			ObjectWithDistance<O> curr = it.next();
			if ( curr.dist < best.dist) best = curr;
		}
		return best.obj;
	}

//	@Override
//	public SimilarityPQueue<O> getExcluding(HashSet<ID> excHashSet) {
//		SimPQueueDMax res = new SimPQueueDMax();
//		
//		ObjectWithDistance<O>[] arr = getSortedArray(); 
//		
//		for ( int i=0; i<arr.length; i++) {
//			if ( excHashSet.contains( ((HasID) arr[i]).getID())) {
//					continue;
//			}
//			res.pQueue.offer(arr[i]);
//		}		
//		
//		return res;
//	}

}
