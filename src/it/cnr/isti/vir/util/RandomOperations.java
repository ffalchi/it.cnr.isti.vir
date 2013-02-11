package it.cnr.isti.vir.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class RandomOperations {
	final static Random r = new Random(23);
	
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

	public static double[] getRandomDoubleArray(int size) {
		double[] arr = new double[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=r.nextDouble();
		}
		return arr;
	}
	
	public static float[] getRandomFloatArray(int size) {
		float[] arr = new float[size];
		for ( int i=0; i<arr.length; i++) {
			arr[i]=r.nextFloat();
		}
		return arr;
	}
	
	public static final void reorderArray(Object[] arr) {
		//--- Shuffle by exchanging each element randomly
		for (int i=0; i<arr.length; i++) {
		    int randomPosition = r.nextInt(arr.length);
		    Object temp = arr[i];
		    arr[i] = arr[randomPosition];
		    arr[randomPosition] = temp;
		}
	}
	
	public static final void reorderArrays(Object[] arr1, Object[] arr2) {
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
	
	public static final void reorderArrays(Object[] arr1, int[] arr2) {
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
	
	public static final void reorderArrays(Object[] arr1, float[] arr2) {
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
	
	public static final void reorderArrayList(ArrayList arr) {
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
		
		reorderArray(tempArr);
		
		while (list.size() > 0 ) list.remove();
		
		for ( i=0; i<tempArr.length; i++ ) {
			list.add(tempArr[i]);
		}
	}
	
}
