package it.cnr.isti.vir.features.localfeatures;

import static org.junit.Assert.fail;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.util.TempFile;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import org.junit.Test;

public class SIFTGroupTest {

	static final Random rnd = new Random(System.currentTimeMillis());
	
	public SIFTGroup getRandomGroup() {
		SIFT[] f = new SIFT[rnd.nextInt(1000)];
		for ( int i1=0; i1<f.length; i1++) {
			f[i1] = new SIFT(KeyPoint.getRandom(), SIFTTest.getRndData() );		
		}
		return new SIFTGroup(f);
	}
	
	@Test
	public void test() throws IOException {
		
		SIFTGroup g = getRandomGroup();
		
		byte[] bytes = g.getBytes();		
		SIFTGroup g2 = new SIFTGroup(ByteBuffer.wrap(bytes));
		if ( !g.equals(g2)) {
			String message = 
					"SIFTGroupTest.test\n" +
					g + g2;
			fail(message);
		}
		
	}
	
	@Test
	public void ioTest() throws Exception {
		File tFile = TempFile.createTempFile(".dat");
		FeaturesCollectorsArchive archive = FeaturesCollectorsArchive.create(tFile);
		SIFTGroup g1 = getRandomGroup();
		SIFTGroup g2 = getRandomGroup();
		FeaturesCollectorArr fc1 = new FeaturesCollectorArr(g1, new IDString("0"));
		FeaturesCollectorArr fc2 = new FeaturesCollectorArr(g2, new IDString("1"));
		archive.add(fc1);
		archive.add(fc2);
		archive.close();
		archive = new FeaturesCollectorsArchive(tFile);
		AbstractFeaturesCollector fc1Read = archive.get(0);
		AbstractFeaturesCollector fc2Read = archive.get(1);
		archive = new FeaturesCollectorsArchive(tFile);
		if ( !fc1.equals(fc1Read)) {
			String message = 
					"SIFTGroupTest.ioTest\n" +
					fc1 + fc1Read;
			fail(message);
		}
		if ( !fc2Read.equals(fc2Read)) {
			String message = 
					"SIFTGroupTest.ioTest\n" +
					fc2 + fc2Read;
			fail(message);
		}
	}

}
