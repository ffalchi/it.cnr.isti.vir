package it.cnr.isti.vir.util;

import static org.junit.Assert.*;
import it.cnr.isti.vir.util.bytes.IntByteArrayUtil;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class IntByteArrayUtilTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	static final int nRndTests = 100;
	static final int MAX_VALUE_LENGTH = 100;
	static final int MAX_OFFSET = 100;
	
	@Test
    public void testInt() {
    	for ( int i=0; i<nRndTests; i++) {
    		int value = rnd.nextInt();
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE+rndOffset];
    		IntByteArrayUtil.intToByteArray(value, bytes, rndOffset);
    		int decodedValue = IntByteArrayUtil.get(bytes, rndOffset);
    		
    		assertTrue(decodedValue == value);
    	}
    }
	
	@Test
    public void testIntArr() {
    	for ( int i=0; i<nRndTests; i++) {
    		
    		// Random Values
    		int rndArrLength = rnd.nextInt(MAX_VALUE_LENGTH);
    		int[] value = new int[rndArrLength];
    		for ( int i2=0; i2<rndArrLength; i2++) {
    			value[i2] = rnd.nextInt();
    		}
    		
    		// Init bytes
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE*rndArrLength+rndOffset];
    		
    		// Encoding and decoding
    		IntByteArrayUtil.intArrayToByteArray(value, bytes, rndOffset);
    		int[] decodedValue = IntByteArrayUtil.get(bytes, rndOffset, rndArrLength);
    		
    		assertTrue(Arrays.equals(decodedValue,value));
    	}
    }

}
