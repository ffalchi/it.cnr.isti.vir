package it.cnr.isti.vir.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class SortTest {

	@Test
	public void test() {
		int[] random1 = RandomOperations.getOrderedInts(1000);
		int[] random2 = RandomOperations.getOrderedInts(1000);
		Sort.sortAscending( random1, random2 ); 
		
		boolean sorted = true;
		for(int i = 1; i < random1.length; i++) {
		    if(random1[i-1] > random1[i]){
		          sorted = false;
		          break;
		    }
		}
		
		assertTrue(sorted);
	}

}
