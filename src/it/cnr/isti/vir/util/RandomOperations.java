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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class RandomOperations {
	final static Random r = new Random(23);
	
	public static final void setRandomSeed()
	{
		r.setSeed(System.currentTimeMillis());
	}
	
	public static final void setSeed(long seed)
	{
		r.setSeed(seed);
	}
	
	public static final double getDouble(double max) {
		return max*r.nextDouble();
	}
	
	public static final float getFloat(float max) {
		return max*r.nextFloat();
	}
	
	/**
	 * @param min inclusive min int
	 * @param max inclusive max int
	 * @return
	 */
	public static final int getInt(int min, int max) {
		return r.nextInt(max - min + 1) + min;
	}
	

	
	public static final boolean trueORfalse(double yesRate) {
		double value = r.nextDouble(); // random value between 0 and 1.0
		return value <= yesRate;
	}

	public static Point2D.Double getRandomPoint2DDouble() {
		return new Point2D.Double(r.nextDouble(), r.nextDouble());
	}

	public static double[][] getRandomDoubleMatrix(int s1, int s2) {
		double[][] arr = new double[s1][s2];
		for ( int i1=0; i1<arr.length; i1++) {
			for ( int i2=0; i2<arr[0].length; i2++) {
				arr[i1][i2]=r.nextDouble();
			}
		}
		return arr;
	}
	
	public static int[] getRandomIntArray(int size, int min, int max) {
		int[] arr = new int[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=getInt(min, max);
		}
		return arr;
	}
	
	public static int[] getDistinctIntArray(int size, int min, int max) {
		HashSet<Integer> hSet = new HashSet(2*size);
		int[] arr = new int[size];
		for ( int i=0; i<arr.length; i++) {
			int temp =getInt(min, max);
			while(hSet.contains(temp)) {
				temp =getInt(min, max);
			}
			arr[i] = temp;
			hSet.add(temp);
		}
		return arr;
	}
	
	public static double[] getDoubleArray(int size) {
		double[] arr = new double[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=r.nextDouble();
		}
		return arr;
	}
	
	public static float[] getFloatArray(int size) {
		float[] arr = new float[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=r.nextFloat();
		}
		return arr;
	}
	
	public static final void shuffle(Object[] arr) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr.length; i++) {
		    int randomPosition = r.nextInt(arr.length);
		    Object temp = arr[i];
		    arr[i] = arr[randomPosition];
		    arr[randomPosition] = temp;
		}
	}
	
	public static final void shuffle(int[] arr) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr.length; i++) {
		    int randomPosition = r.nextInt(arr.length);
		    int temp = arr[i];
		    arr[i] = arr[randomPosition];
		    arr[randomPosition] = temp;
		}
	}
	
	public static final void shuffle(Object[] arr1, Object[] arr2) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr1.length; i++) {
		    int randomPosition = r.nextInt(arr1.length);
		    Object temp = arr1[i];
		    arr1[i] = arr1[randomPosition];
		    arr1[randomPosition] = temp;
		    temp = arr2[i];
		    arr2[i] = arr2[randomPosition];
		    arr2[randomPosition] = temp;
		}
	}
	
	public static final void shuffle(Object[] arr1, int[] arr2) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr1.length; i++) {
		    int randomPosition = r.nextInt(arr1.length);
		    Object temp = arr1[i];
		    arr1[i] = arr1[randomPosition];
		    arr1[randomPosition] = temp;
		    int intTemp = arr2[i];
		    arr2[i] = arr2[randomPosition];
		    arr2[randomPosition] = intTemp;
		}
	}
	
	public static final void shuffle(Object[] arr1, float[] arr2) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr1.length; i++) {
		    int randomPosition = r.nextInt(arr1.length);
		    Object temp = arr1[i];
		    arr1[i] = arr1[randomPosition];
		    arr1[randomPosition] = temp;
		    float t = arr2[i];
		    arr2[i] = arr2[randomPosition];
		    arr2[randomPosition] = t;
		}
	}
	
	public static final void shuffle(ArrayList arr) {
		//--- Shuffle by exchanging each element randomly
		int size = arr.size();
		for (int i=0; i<size; i++) {
		    int randomPosition = r.nextInt(size);
		    Object temp = arr.get(i);
		    arr.set(i, arr.get(randomPosition));
		    arr.set(randomPosition, temp);
		}
	}
	
	public static final LinkedList getNRandomObjectList( LinkedList givenList, int n ) {
		assert ( n > givenList.size() );
		LinkedList outList = new LinkedList();
		
		ArrayList<Integer> random = getRandomOrderedIntegers(givenList.size());
		
		for (Iterator<Integer> it=random.iterator(); it.hasNext() && outList.size() < n;) {
			outList.add(givenList.get(it.next()));
		}
		
		
		return outList;
	}
	
	public static final LinkedList[] splitObjectList( LinkedList givenList, double ratio ) {
		int n = (int) Math.round(givenList.size() * ratio );
		LinkedList list1 = new LinkedList();
		LinkedList list2 = new LinkedList();
		
		ArrayList<Integer> random = getRandomOrderedIntegers(givenList.size());
		
		for (Iterator<Integer> it=random.iterator(); it.hasNext();) {
			if ( list1.size() < n ) list1.add(givenList.get(it.next()));
			else list2.add(givenList.get(it.next()));
		}
		
		LinkedList[] listArr = { list1, list2 };
		return listArr;		
		
	}
	
	/**
	 * @param maxValue	[not included]
	 * @return
	 */
	public static final int[] getRandomOrderedInts(int maxValue) {
		ArrayList<Integer> list = new ArrayList<Integer>(maxValue);
		for ( int i=0; i<maxValue; i++ ) {
			list.add(i);
		}
		
		int[] orderedList = new int[maxValue];
		for ( int i=0; i<orderedList.length; i++ ){
			int nextInt = r.nextInt( list.size() );
			Integer next = list.get(nextInt);
			orderedList[i] = next;
			list.remove(next);
		}
		
		return orderedList;
	}

	
	public static final ArrayList<Integer> getRandomOrderedIntegers(int maxValue) {
		ArrayList<Integer> list = new ArrayList<Integer>(maxValue);
		for ( int i=0; i<maxValue; i++ ) {
			list.add(i);
		}
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(maxValue);
		while( orderedList.size() < maxValue ) {
			int nextInt = r.nextInt( list.size() );
			Integer next = list.get(nextInt);
			orderedList.add(next);
			list.remove(next);
		}
		
		return orderedList;
	}
	
	public static final LinkedList<Integer> getRandomOrderedLinkedListIntegers(int maxValue) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		for ( int i=0; i<maxValue; i++ ) {
			list.add(i);
		}
		
		LinkedList<Integer> orderedList = new LinkedList<Integer>();
		while( orderedList.size() < maxValue ) {
			int nextInt = r.nextInt( list.size() );
			Integer next = list.get(nextInt);
			orderedList.add(next);
			list.remove(next);
		}
		
		return orderedList;
	}
	
	@SuppressWarnings("unchecked")
	public static final void reorderLinkedList(LinkedList list ) {
		Object[] tempArr = new Object[list.size()];
		
		int i =0;
		for ( Iterator it= list.iterator(); it.hasNext(); i++ ) {
			tempArr[i] = it.next();
		}
		
		shuffle(tempArr);
		
		while (list.size() > 0 ) list.remove();
		
		for ( i=0; i<tempArr.length; i++ ) {
			list.add(tempArr[i]);
		}
	}


	
}