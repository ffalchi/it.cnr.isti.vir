package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class SIFTTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	public static byte[] getRndData() {
		byte[] data = new byte[SIFT.VLEN];
		for ( int i=0; i<SIFT.VLEN; i++ ) {
			data[i] = (byte) rnd.nextInt();
		}
		return data;
	}
	
	@Test
	public void testIO() throws IOException {
		
		SIFT f = new SIFT(null, getRndData() );
		byte[] bytes = f.getBytes();		
		SIFT f2 = new SIFT(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new SIFT(kp, getRndData() );
		bytes = f.getBytes();		
		f2 = new SIFT(ByteBuffer.wrap(bytes));
		if ( !f.equals(f2)) {
			String message = 
					"SIFTGroupTest.ioTest\n" +
					f + f2;
			fail(message);
		}
		
	}
	
	@Test
	public void testMean() throws IOException {
		SIFT f = new SIFT(null, getRndData() );
		ArrayList<SIFT> coll = new ArrayList<SIFT>();
		for ( int i=0; i<100; i++) {
			coll.add(f);
		}
		SIFT mean = SIFT.getMean(coll);

		if ( !f.equals(mean)) {
			String message = 
					"SIFTGroupTest.ioTest\n" +
					f + mean;
			fail(message);
		};
	}

}
