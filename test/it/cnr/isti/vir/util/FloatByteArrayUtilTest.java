package it.cnr.isti.vir.util;

import static org.junit.Assert.assertTrue;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class FloatByteArrayUtilTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	static final int nRndTests = 100;
	static final int MAX_VALUE_LENGTH = 100;
	static final int MAX_OFFSET = 100;
	
	@Test
    public void test() {
    	for ( int i=0; i<nRndTests; i++) {
    		float value = rnd.nextFloat();
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE+rndOffset];
    		FloatByteArrayUtil.convToBytes(value, bytes, rndOffset);
    		float decodedValue = FloatByteArrayUtil.get(bytes, rndOffset);
    		
    		assertTrue(decodedValue == value);
    	}
    }
	
	@Test
    public void testArr() {
    	for ( int i=0; i<nRndTests; i++) {
    		
    		// Random Values
    		int rndArrLength = rnd.nextInt(MAX_VALUE_LENGTH);
    		float[] value = new float[rndArrLength];
    		for ( int i2=0; i2<rndArrLength; i2++) {
    			value[i2] = rnd.nextFloat();
    		}
    		
    		// Init bytes
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE*rndArrLength+rndOffset];
    		
    		// Encoding and decoding
    		FloatByteArrayUtil.convToBytes(value, bytes, rndOffset);
    		float[] decodedValue = FloatByteArrayUtil.get(bytes, rndOffset, rndArrLength);
    		
    		assertTrue(Arrays.equals(decodedValue,value));
    	}
    }

}
