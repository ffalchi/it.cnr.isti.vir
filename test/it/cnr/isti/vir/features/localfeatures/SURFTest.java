package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

public class SURFTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	public static float[] getRndData() {
		float[] data = new float[SURF.VECLENGTH];
		for ( int i=0; i<SURF.VECLENGTH; i++ ) {
			data[i] = rnd.nextFloat();
		}
		return data;
	}
	
	@Test
	public void testIO() throws IOException {
		
		SURF f = new SURF( getRndData(), (byte) (rnd.nextInt(3)-1), rnd.nextFloat(), null );
		byte[] bytes = f.getBytes();		
		SURF f2 = new SURF(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new SURF(getRndData(), (byte) (rnd.nextInt(3)-1), rnd.nextFloat(), kp );
		bytes = f.getBytes();		
		f2 = new SURF(ByteBuffer.wrap(bytes));
		if ( !f.equals(f2)) {
			String message = 
					"SURFGroupTest.ioTest\n" +
					f + f2;
			fail(message);
		}
		
	}
	

}
