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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RandomOperations {
	final static Random r = new Random(System.currentTimeMillis());
	
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
	
	public static final long getLong() {
		return r.nextLong();
	}
	
	public static final float getFloat() {
		return r.nextFloat();
	}
	
	public static final byte getByte() {
		return (byte) r.nextInt();
	}
	
	/**
	 * Randomly generates a positive float
	 * @param max
	 * @return		a positive float
	 */
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
	
	/**
	 * Returns random int (0 inclusive, max exclusive)
	 * @param exclusive max int
	 * @return
	 */
	public static final int getInt( int max) {
		return r.nextInt(max);
	}
	
	public static final boolean trueORfalse(double yesRate) {
		if ( yesRate >= 1.0 ) return true;
		if ( yesRate <= 0.0 ) return false;
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
	
	public static int[] getInts(int size, int min, int max) {
		int[] arr = new int[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=getInt(min, max);
		}
		return arr;
	}
	
	public static int[] getDistinctInts(int size, int min, int max) {
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
	
	
	

	/**
	 * Returns size ints betweeon 0 (inclusive) and max (exclusive)
	 * 
	 * @param size
	 * @param max	exclusive
	 * @return
	 */
	public static int[] getDistinctInts(int size, int max) {
		HashSet<Integer> hSet = new HashSet(2*size);

		int[] arr = new int[size];
		for ( int i=0; i<arr.length; i++) {
			int temp = getInt( max);
			while(hSet.contains(temp)) {
				temp = getInt( max);
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
	
	/**
	 * Returns uniformly distributed floats between 0.0 and 1.0
	 * @param size
	 * @return 
	 */
	public static float[] getUniformlyDistributedFloats(int size) {
		float[] arr = new float[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=r.nextFloat();
		}
		return arr;
	}
	
	/**
	 * Returns Gaussian "normally" distributed floats between 0.0 and 1.0
	 * @param size
	 * @return 
	 */
	public static float[] getGaussianFloats(int size) {
		float[] arr = new float[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]= (float) r.nextGaussian();
		}
		return arr;
	}
	
	/**
	 * Returns Gaussian "normally" distributed doubles between 0.0 and 1.0
	 * @param size
	 * @return 
	 */
	public static double[] getGaussianDoubles(int size) {
		double[] arr = new double[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]= r.nextGaussian();
		}
		return arr;
	}
	

	/**
	 * Fisher–Yates shuffle
	 * @param arr
	 */
	public static final void shuffle(Object[] arr) {

	    for (int i = arr.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      // Simple swap
	      Object a = arr[index];
	      arr[index] = arr[i];
	      arr[i] = a;
	    }
	}
	
	/**
	 * Fisher–Yates shuffle
	 * @param arr
	 */
	public static final void shuffle(int[] arr) {
		for (int i = arr.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      // Simple swap
	      int a = arr[index];
	      arr[index] = arr[i];
	      arr[i] = a;
	    }
	}
	
	/**
	 * Fisher–Yates shuffle
	 * @param arr
	 */
	public static final void shuffle(Object[] arr1, Object[] arr2) {
	    for (int i = arr1.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      
	      Object a = arr1[index];
	      arr1[index] = arr1[i];
	      arr1[i] = a;
	      
	      a = arr2[index];
	      arr2[index] = arr2[i];
	      arr2[i] = a;
		}
	}
	
	/**
	 * Fisher–Yates shuffle
	 * @param arr
	 */
	public static final void shuffle(Object[] arr1, int[] arr2) {
	    for (int i = arr1.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      
	      Object a = arr1[index];
	      arr1[index] = arr1[i];
	      arr1[i] = a;
	      
	      int a2 = arr2[index];
	      arr2[index] = arr2[i];
	      arr2[i] = a2;
		}
	}
	
	/**
	 * Fisher–Yates shuffle
	 * @param arr1
	 * @param arr2
	 */
	public static final void shuffle(Object[] arr1, float[] arr2) {
	    for (int i = arr1.length - 1; i > 0; i--)
	    {
	      int index = r.nextInt(i + 1);
	      
	      Object a = arr1[index];
	      arr1[index] = arr1[i];
	      arr1[i] = a;
	      
	      float a2 = arr2[index];
	      arr2[index] = arr2[i];
	      arr2[i] = a2;
		}
	}
	
	/**
	 * Java shuffle
	 * @param arr
	 */
	public static final void shuffle(ArrayList arr) {
		Collections.shuffle(arr);
	}
	
	public static final <T> T[] getRandomObjects( T[] given, int n ) {
		if ( n>=given.length) return given;
		T[] res = (T[]) java.lang.reflect.Array.newInstance(given.getClass().getComponentType(), n);
		int[] rnd = RandomOperations.getDistinctInts(n, given.length);
		for (int i=0; i<n; i++) {
			res[i]=given[rnd[i]];
		}
				
		return res;
	}
	
	public static final ArrayList getRandomObjects( AbstractList given, double prob ) {
		//ArrayList res = new ArrayList((int) (given.size() * prob));
		ArrayList res = new ArrayList((int) (given.size() * prob));
		
		for (Iterator it=given.iterator(); it.hasNext();) {
			Object curr = it.next();
			if( RandomOperations.trueORfalse(prob))
				res.add(curr);
		}		
		
		return res;
	}
	
	public static final ArrayList getNRandomObjectList( AbstractList givenList, int n ) {
		ArrayList outList = new ArrayList();
		
		ArrayList<Integer> random = getOrderedIntegersAL(givenList.size());
		
		for (Iterator<Integer> it=random.iterator(); it.hasNext() && outList.size() < n;) {
			outList.add(givenList.get(it.next()));
		}
		
		
		return outList;
	}
	
	public static final LinkedList[] splitObjectList( LinkedList givenList, double ratio ) {
		int n = (int) Math.round(givenList.size() * ratio );
		LinkedList list1 = new LinkedList();
		LinkedList list2 = new LinkedList();
		
		ArrayList<Integer> random = getOrderedIntegersAL(givenList.size());
		
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
	public static final int[] getOrderedInts(int n) {
		ArrayList<Integer> list = new ArrayList<Integer>(n);
		for ( int i=0; i<n; i++ ) {
			list.add(i);
		}
		
		int[] orderedList = new int[n];
		for ( int i=0; i<orderedList.length; i++ ){
			int nextInt = r.nextInt( list.size() );
			Integer next = list.get(nextInt);
			orderedList[i] = next;
			list.remove(next);
		}
		
		return orderedList;
	}

	
	public static final ArrayList<Integer> getOrderedIntegersAL(int n) {
		ArrayList<Integer> list = new ArrayList<Integer>(n);
		for ( int i=0; i<n; i++ ) {
			list.add(i);
		}
		
		ArrayList<Integer> orderedList = new ArrayList<Integer>(n);
		while( orderedList.size() < n ) {
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
	public static final void shuffle(List list ) {
		Object[] tempArr = new Object[list.size()];
		
		int i =0;
		for ( Iterator it= list.iterator(); it.hasNext(); i++ ) {
			tempArr[i] = it.next();
		}
		
		shuffle(tempArr);
		
		list.clear();
		
		for ( i=0; i<tempArr.length; i++ ) {
			list.add(tempArr[i]);
		}
	}

	public static float getFloat(float min, float max) {
		return getFloat(max-min)+min;
	}

	public static long[] getRandomLongArray(int vlength) {
		long[] res = new long[vlength];
		for ( int i=0; i<vlength; i++) {
			res[i] = r.nextLong();
		}
		return res;
	}

	public static float[] getRandomFloatArray(int vlength) {
		float[] res = new float[vlength];
		for ( int i=0; i<vlength; i++) {
			res[i] = RandomOperations.getFloat();
		}
		return res;
	}
	
	/**
	 * Get complete integers between min (inclusive) and max (exclusive)
	 * random ordered.
	 * @param min
	 * @param max
	 * @return
	 */
	public static final int[] getRandomOrderedInts(int min, int max) {
		int[] res = new int[max-min];
		int curr = min;
		for ( int i=0; i<res.length; i++) {
			res[i] = curr++;
		}
		shuffle(res);
		return res;
	}

	public static boolean getBoolean() {
		return r.nextBoolean();
	}

	
	public static String getRandomUUIdString() {
		UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
	}
	
	
	public static final int[] getPerturbated(int[] orig, int nBitsPerturbated) {
		int[] res = orig.clone();
		int nBits = Integer.SIZE * res.length;
		for(int i=0; i<nBits; i++) {
			int currRandom = RandomOperations.getInt(nBits);
			int int_pos = currRandom/Integer.SIZE;
			int mask = 1<<currRandom%Integer.SIZE;
			res[int_pos]=res[int_pos]^mask;
		}
		return res;
	}
	

	public static final long[] getPerturbated(long[] orig, int nBitsPerturbated) {
		long[] res = orig.clone();
		int nBits = Long.SIZE * orig.length;
		int[] pert = RandomOperations.getDistinctInts(nBitsPerturbated, nBits);
		
		for(int bit : pert) {
			int pos = bit/Long.SIZE;
			long mask = 1L<<bit%Long.SIZE;
			res[pos]=res[pos]^mask;
		}
		return res;
	}
	
}
