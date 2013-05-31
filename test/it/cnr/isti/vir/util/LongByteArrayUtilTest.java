package it.cnr.isti.vir.util;

import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class LongByteArrayUtilTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	static final int nRndTests = 100;
	static final int MAX_VALUE_LENGTH = 100;
	static final int MAX_OFFSET = 100;
	
	@Test
    public void test() {
    	for ( int i=0; i<nRndTests; i++) {
    		long value = rnd.nextLong();
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE+rndOffset];
    		LongByteArrayUtil.convToBytes(value, bytes, rndOffset);
    		long decodedValue = LongByteArrayUtil.get(bytes, rndOffset);
    		
    		assert(decodedValue != value);
    	}
    }
	
	@Test
    public void testArr() {
    	for ( int i=0; i<nRndTests; i++) {
    		
    		// Random Values
    		int rndArrLength = rnd.nextInt(MAX_VALUE_LENGTH);
    		long[] value = new long[rndArrLength];
    		for ( int i2=0; i2<rndArrLength; i2++) {
    			value[i2] = rnd.nextLong();
    		}
    		
    		// Init bytes
    		int rndOffset = rnd.nextInt(MAX_OFFSET);
    		byte[] bytes = new byte[Integer.SIZE*rndArrLength+rndOffset];
    		
    		// Encoding and decoding
    		LongByteArrayUtil.convToBytes(value, bytes, rndOffset);
    		long[] decodedValue = LongByteArrayUtil.get(bytes, rndOffset, rndArrLength);
    		
    		assert(Arrays.equals(decodedValue,value));
    	}
    }

}
