package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class BRISKGroupTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	@Test
	public void test() throws IOException {
		
		BRISK[] f = new BRISK[rnd.nextInt(1000)];
		for ( int i1=0; i1<f.length; i1++) {
			long[] data = new long[ORB.NLONG];
			for ( int i=0; i<ORB.NLONG; i++ ) {
				data[i] = rnd.nextLong();
			}
			f[i1] = new BRISK(KeyPoint.getRandom(), data );		
		}

		BRISKGroup g = new BRISKGroup(f);
		byte[] bytes = g.getBytes();		
		BRISKGroup g2 = new BRISKGroup(ByteBuffer.wrap(bytes));
		assertTrue(g.equals(g2));

	}

}
