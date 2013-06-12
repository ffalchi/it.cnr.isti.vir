package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class BoFLFTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	

	@Test
	public void testIO() throws IOException {
		
		BoFLF f = new BoFLF( rnd.nextInt() );
		byte[] bytes = f.getBytes();		
		BoFLF f2 = new BoFLF(ByteBuffer.wrap(bytes));
		assert(f.equals(f2));
		
		KeyPoint kp = KeyPoint.getRandom();
		f = new BoFLF(rnd.nextInt(), kp );
		bytes = f.getBytes();		
		f2 = new BoFLF(ByteBuffer.wrap(bytes));
		assertTrue(f.equals(f2));
		
	}
	

}
