package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class ORBGroupTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	@Test
	public void test() throws IOException {
		
		ORB[] f = new ORB[rnd.nextInt(1000)];
		for ( int i1=0; i1<f.length; i1++) {
			long[] data = new long[ORB.NLONG];
			for ( int i=0; i<ORB.NLONG; i++ ) {
				data[i] = rnd.nextLong();
			}
			f[i1] = new ORB(KeyPoint.getRandom(), data );		
		}

		ORBGroup g = new ORBGroup(f);
		byte[] bytes = g.getBytes();		
		ORBGroup g2 = new ORBGroup(ByteBuffer.wrap(bytes));
		assertTrue(g.equals(g2));

	}

}
