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
