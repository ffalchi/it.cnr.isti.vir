package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class ORBTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	public static long[] getRndData() {
		long[] data = new long[ORB.VLENGTH];
		for ( int i=0; i<ORB.VLENGTH; i++ ) {
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
		for ( int i=0; i<100; i++) {
			coll.add(f);
		}
		ORB mean = ORB.getMean(coll);
		assertTrue(f.equals(mean));
	}

}
