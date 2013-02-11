package it.cnr.isti.vir.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Reordering {

	public static final void reorder(Collection<Integer> ord, Object[] arr) {
		Object[] newArr = null;
		if ( arr != null ) {
			newArr = new Object[arr.length];
			int i = 0;
			for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
				newArr[i++] = arr[it.next()];
			}
			if ( ord.size() < arr.length ) {
				
				HashSet<Integer> hSet = new HashSet(ord.size());
				for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
					hSet.add(it.next());
				}
				for(int iArr=0; iArr<arr.length; iArr++ ) {
					if ( hSet.contains(iArr)) continue;
					newArr[i++] = arr[iArr];
				}
			}
			for(int iArr=0; iArr<arr.length; iArr++ ) {
				arr[iArr] = newArr[iArr];
			}
		}		
	}
	
	public static final void putFirst(Collection<Integer> ord, Object[] arr) {
		Object[] newArr = null;
		if ( arr != null ) {
			newArr = new Object[arr.length];
			int i = 0;
			for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
				newArr[i++] = arr[it.next()];
			}
			for(i=0; i<arr.length; i++ ) {
				arr[i] = newArr[i];
			}
		}		
	}
	
	public static final void reorder(Collection<Integer> ord, double[] arr) {
		double[] newArr = null;
		if ( arr != null ) {
			newArr = new double[arr.length];
			int i = 0;
			for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
				newArr[i++] = arr[it.next()];
			}
			if ( ord.size() < arr.length ) {
				
				HashSet<Integer> hSet = new HashSet(ord.size());
				for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
					hSet.add(it.next());
				}
				for(int iArr=0; iArr<arr.length; iArr++ ) {
					if ( hSet.contains(iArr)) continue;
					newArr[i++] = arr[iArr];
				}
			}
			for(i=0; i<arr.length; i++ ) {
				arr[i] = newArr[i];
			}
		}		
	}
	
	public static final void reorder(Collection<Integer> ord, float[] arr) {
		float[] newArr = null;
		if ( arr != null ) {
			newArr = new float[arr.length];
			int i = 0;
			for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
				newArr[i++] = arr[it.next()];
			}
			if ( ord.size() < arr.length ) {
				
				HashSet<Integer> hSet = new HashSet(ord.size());
				for(Iterator<Integer> it = ord.iterator(); it.hasNext(); ) {
					hSet.add(it.next());
				}
				for(int iArr=0; iArr<arr.length; iArr++ ) {
					if ( hSet.contains(iArr)) continue;
					newArr[i++] = arr[iArr];
				}
			}
			for(i=0; i<arr.length; i++ ) {
				arr[i] = newArr[i];
			}
		}		
	}
	
	public static final double[][] reorderTrMatrix(Collection<Integer> ord, double[][] arr) {
		
		if ( arr != null ) {
			double[][] newArr = new double[arr.length][];
			int x = 0;
			for(Iterator<Integer> itx = ord.iterator(); itx.hasNext(); ) {
				newArr[x] = new double[x];
				int y = 0;
				Integer currx = itx.next();
				for(Iterator<Integer> ity = ord.iterator(); (y < x); ) {
					Integer curry = ity.next();
					if ( currx > curry ) {
						newArr[x][y] = arr[currx][curry];
					} else {
						newArr[x][y] = arr[curry][currx];
					}
					y++;
				}
				x++;
			}
			return newArr;
		}
		return null;
	}
	
}
