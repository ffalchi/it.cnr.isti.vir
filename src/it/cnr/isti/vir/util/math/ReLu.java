package it.cnr.isti.vir.util.math;

public class ReLu {

	public final static void perform(float[] values ) {
		
		for ( int i=0; i<values.length; i++) {
			values[i] = Math.max(0.0F, values[i]);
		}

	}
	
	
}
