package it.cnr.isti.vir.util;

import it.cnr.isti.vir.util.math.Normalize;


public class RandomVectors {

	
	public static final float[] getUniformlyDistributedFloats(int dim ) {
		return  RandomOperations.getUniformlyDistributedFloats(dim);
	}
	
	public static final float[] getL2NormalizedFloats(int dim ) {
		float[] res = getUniformlyDistributedFloats( dim );
		Normalize.l2( res );		
		return res;
	}	
	
	
}
