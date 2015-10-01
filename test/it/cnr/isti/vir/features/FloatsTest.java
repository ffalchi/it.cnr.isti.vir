package it.cnr.isti.vir.features;

import static org.junit.Assert.assertTrue;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.TempFile;

import java.io.File;

import org.junit.Test;

public class FloatsTest {

	@Test
	public void test() {
		float[] f = RandomOperations.getRandomFloatArray(1024);
		
		Floats ff = new Floats(f);
		
		for ( int i=0; i<f.length; i++ ) {
			assertTrue(f[i] == ff.getValues()[i]);
		}
	}

	@Test
	public void testArchive() throws Exception {
		
		File file = TempFile.createTempFile(".dat");
		
		FeaturesCollectorsArchive fca = FeaturesCollectorsArchive.create(file );
		
		float[] f = RandomOperations.getRandomFloatArray(1024);
		Floats ff = new Floats(f);
		
		FeaturesCollectorArr fc = new FeaturesCollectorArr(ff, new IDString("Test"));
		
		fca.add(fc);
		
		fca.close();
		
		fca = new FeaturesCollectorsArchive(file);
		
		Floats saved = fca.iterator().next().getFeature(Floats.class);
		
		float[] f_saved = saved.values;
		for ( int i=0; i<f.length; i++ ) {
			assertTrue(f[i] == f_saved[i]);
		}
		
		file.delete();
	}
}
