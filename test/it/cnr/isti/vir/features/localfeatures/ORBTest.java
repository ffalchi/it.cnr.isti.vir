package it.cnr.isti.vir.features.localfeatures;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class ORBTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	@Test
	public void testIO() throws IOException {
		long[] data = new long[ORB.VLENGTH];
		for ( int i=0; i<ORB.VLENGTH; i++ ) {
			data[i] = rnd.nextLong();
		}
		
		ORB f = new ORB(null, data );
		byte[] bytes = f.getBytes();		
		ORB f2 = new ORB(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new ORB(kp, data );
		bytes = f.getBytes();		
		f2 = new ORB(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
	}

}
