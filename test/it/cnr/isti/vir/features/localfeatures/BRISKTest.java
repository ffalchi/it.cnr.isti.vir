package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class BRISKTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	public static long[] getRndData() {
		long[] data = new long[ORB.NLONG];
		for ( int i=0; i<ORB.NLONG; i++ ) {
			data[i] = rnd.nextLong();
		}
		return data;
	}
	
	@Test
	public void testIO() throws IOException {
		
		BRISK f = new BRISK(null, getRndData() );
		byte[] bytes = f.getBytes();		
		BRISK f2 = new BRISK(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new BRISK(kp, getRndData() );
		bytes = f.getBytes();		
		f2 = new BRISK(ByteBuffer.wrap(bytes));
		assertTrue(f.equals(f2));
		
	}
	
	@Test
	public void testMean() throws IOException {
		BRISK f = new BRISK(null, getRndData() );
		ArrayList<BRISK> coll = new ArrayList<BRISK>();
		for ( int i=0; i<10000; i++) {
			coll.add(f.getRandomPerturbated(8));
		}
		BRISK mean = BRISK.getMean(coll);
		assertEquals(f, mean);
	}

}
