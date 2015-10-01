package it.cnr.isti.vir.distance;

import it.cnr.isti.vir.features.FloatsL2Norm_Bytes;
import it.cnr.isti.vir.features.FloatsL2Norm_CompSparseBytes;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.math.Normalize;

import org.junit.Test;

public class L2Test {

	@Test
	public void test() {
		
		int length = 4096;
		float[] f1 = new float[length];
		float[] f2 = new float[length];
		
		double nonZeroProb = 0.2;
		
		for ( int i=0; i<f1.length; i++) {
			if ( RandomOperations.trueORfalse(nonZeroProb)) {
				f1[i] = RandomOperations.getFloat(16.0F);
			}
		}
		
		for ( int i=0; i<f2.length; i++) {
			if ( RandomOperations.trueORfalse(nonZeroProb)) {
				f2[i] = RandomOperations.getFloat(16.0F);
			}
		}
		
		
		Normalize.l2(f1);
		Normalize.l2(f2);
		
		byte[] byte1 = FloatsL2Norm_Bytes.getBytes(f1);
		byte[] byte2 = FloatsL2Norm_Bytes.getBytes(f2);
		
		byte[] comp1 = FloatsL2Norm_CompSparseBytes.getComp(f1);
		byte[] comp2 = FloatsL2Norm_CompSparseBytes.getComp(f2);
		
//		System.out.println( L2.get(f1, f2) );
//		System.out.println( L2.get(byte1, byte2) / 255.0 );
//		System.out.println( L2.get_fromCompSparseBytes(comp1, comp2) / 255.0 );
	
		
		int[] i1 = new int[length];
		int[] i2 = new int[length];		
		for ( int i=0; i<length; i++) {
			i1[i]=byte1[i];
			i2[i]=byte2[i];
		}
	
		long f_millis = 0;
		long b_millis = 0;
		long c_millis = 0;
		long i_millis = 0;
		
		int tries = 100000;
		for ( int i=0; i<tries; i++ ) {
			long start = System.currentTimeMillis();
			L2.get(f1, f2);
			f_millis += System.currentTimeMillis()-start;
			
			start = System.currentTimeMillis();
			L2.get(i1, i2);
			i_millis += System.currentTimeMillis()-start;
			
			start = System.currentTimeMillis();
			L2.get(byte1, byte2);
			b_millis += System.currentTimeMillis()-start;
			
			start = System.currentTimeMillis();
			L2.get_fromCompSparseBytes(comp1, comp2);
			c_millis += System.currentTimeMillis()-start;
		}
		
		
		
		System.out.println( "Floats\t" + L2.get(f1, f2)  + "\t" + f_millis/(float)tries + " millis");
		System.out.println( "Integer\t" + L2.get(i1, i2) / 255.0 + "\t" + i_millis/(float)tries + " millis");
		System.out.println( "Bytes\t" +  L2.get(byte1, byte2) / 255.0   + "\t" + b_millis/(float)tries + " millis");
		System.out.println( "CBytes\t" + L2.get_fromCompSparseBytes(comp1, comp2) / 255.0   + "\t" + c_millis/(float)tries + " millis");
	}

}
