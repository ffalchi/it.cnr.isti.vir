package it.cnr.isti.vir.util;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class SplitInGroupsTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	static final int nRndTests = 1000;
	
	@Test
    public void test() {
    	for ( int i=0; i<nRndTests; i++) {
    		int value = rnd.nextInt(1000000);
    		int nGroup = rnd.nextInt(value);
    		int[] group = SplitInGroups.split(value, nGroup);
    		
    		int sum = 0;
    		for ( int val : group ) {
    			sum += val;
    		}
    		assertTrue(sum == value);
    	}
    }
}
