package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.*;
import it.cnr.isti.vir.util.bytes.LongBinaryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class ORBTest {

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
		
		ORB f = new ORB(null, getRndData() );
		byte[] bytes = f.getBytes();		
		ORB f2 = new ORB(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new ORB(kp, getRndData() );
		bytes = f.getBytes();		
		f2 = new ORB(ByteBuffer.wrap(bytes));
		assertTrue(f.equals(f2));
		
	}
	
	@Test
	public void testMean() throws IOException {
		ORB f = new ORB(null, getRndData() );
		ArrayList<ORB> coll = new ArrayList<ORB>();
		for ( int i=0; i<10000; i++) {
			coll.add(f.getRandomPerturbated(8));
		}
		ORB mean = new ORB( null, LongBinaryUtil.getMean( coll) );
		assertEquals(f, mean);
	}

}
