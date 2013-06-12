package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class BoFLFGroupTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	@Test
	public void testKPNulls() throws Exception {
		
		BoFLF[] f = new BoFLF[rnd.nextInt(1000)];
		for ( int i1=0; i1<f.length; i1++) {
			int bag = rnd.nextInt();
			f[i1] = new BoFLF(bag, null );//KeyPoint.getRandom() );		
		}

		BoFLFGroup g = new BoFLFGroup(f);
		byte[] bytes = g.getBytes();		
		BoFLFGroup g2 = new BoFLFGroup(ByteBuffer.wrap(bytes));
		assertTrue(g.equals(g2));

	}

	
	@Test
	public void test() throws Exception {
		
		BoFLF[] f = new BoFLF[rnd.nextInt(1000)];
		for ( int i1=0; i1<f.length; i1++) {
			int bag = rnd.nextInt();
			f[i1] = new BoFLF(bag, KeyPoint.getRandom() );		
		}

		BoFLFGroup g = new BoFLFGroup(f);
		byte[] bytes = g.getBytes();		
		BoFLFGroup g2 = new BoFLFGroup(ByteBuffer.wrap(bytes));
		assertTrue(g.equals(g2));

	}
}
