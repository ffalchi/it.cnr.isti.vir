package it.cnr.isti.vir.util.math;

import java.util.Arrays;

public class Normalize {

	 /**
	  *  @param values
	 * l1 norm
	 * In case the sum of the values is 0.0, 
	 * values[] is substitute with a constant vector if flag nozeronorm is true
	 * values[] is left unchanged if nozeronorm is false
	 */
	public static final void l1( double[] values, boolean nozeronorm ) {
		double norm = Norm.l1(values);
		if ( norm > 0.0) {
			if(norm != 1.0) {
				for (int i=0; i<values.length; i++) {
					values[i] = values[i] / norm;	        	
				}        
			}
		}
		else {
			if(nozeronorm) {
				Arrays.fill(values, 1/(double) values.length);
			}
		}
	}
	 /**
	  *  @param values
	 * l1 norm
	 * In case the sum of the values is 0.0, 
	 * values[] is substitute with a constant vector if flag nozeronorm is true
	 * values[] is left unchanged if nozeronorm is false
	 */
	public static final void l1( float[] values, boolean nozeronorm ) {
		double norm = Norm.l1(values);
		if ( norm > 0.0) {
			if(norm != 1.0) {
				for (int i=0; i<values.length; i++) {
					values[i] =values[i] / (float) norm;	        	
				}        
			}
		}
		else {
			if(nozeronorm) {
				Arrays.fill(values, 1/(float) values.length);
			}
		}
	}
	
	/**
	 * @param values
	 * 
	 * In case the sum of the values is 0.0, values[] is left unchanged
	 */
	public static final void l2( double[] values ) {
		double norm = Norm.l2(values);
        if ( norm > 0.0 && norm != 1.0) {
	        for (int i=0; i<values.length; i++) {
	        	values[i] = values[i] / norm;	        	
	        }        
        }
	}
	
	
	/**
	 * @param values
	 * 
	 * In case the sum of the values is 0.0, values[] is left unchanged
	 */
	public static final void l2( float[] values ) {
		float norm = Norm.l2(values);
        if ( norm > 0.0F && norm != 1.0F) {
	        for (int i=0; i<values.length; i++) {
	        	values[i] = values[i] / norm;	        	
	        } 
        }
	}
	
	
	/**
	 * @param values
	 * 
	 * In case the sum of the values is 0.0, values[] is left unchanged
	 */
	public static final void l2( float[] values, int start, int end ) {
		float norm = Norm.l2(values, start, end );
        if ( norm > 0.0F && norm != 1.0F) {
	        for (int i=start; i<end; i++) {
	        	values[i] = values[i] / norm;	        	
	        }        
        }
	}
	
	/**
	 * @param values
	 * 
	 * In case the sum of the values is 0.0, values[] is left unchanged
	 */
	public static final void l2( double[] values, int start, int end ) {
		double norm = Norm.l2(values, start, end );
        if ( norm > 0.0 && norm != 1.0) {
	        for (int i=start; i<end; i++) {
	        	values[i] = values[i] / norm;	        	
	        }        
        }
	}
	
	public static final void ssr( float[] values ) {
		int size = values.length;
		// Power Normalization 0.5
        for (int i=0; i<size; i++) {     
        	if ( values[i] > 0.0F )
        		values[i] =   (float) Math.sqrt(values[i]);
        	else  if ( values[i] < 0.0F )
        		values[i] = - (float) Math.sqrt(-values[i]);
        }
	}
	
	public static final void ssr( double[] values ) {
		int size = values.length;
		// Power Normalization 0.5
        for (int i=0; i<size; i++) {     
        	if ( values[i] > 0.0 )
        		values[i] =   Math.sqrt(values[i]);
        	else  if ( values[i] < 0.0 )
        		values[i] = - Math.sqrt(-values[i]);
        }
	}
	
	/**
	 * @param values	the array to be power 0.5 normalized
	 */
	public static final void ssr(int[] values) {
		int size = values.length;
		// Power Normalization 0.5
        for (int i=0; i<size; i++) {     
        	if ( values[i] == 0 ) values[i] = 0;
        	else if ( values[i] > 0 )
        		values[i] =   (int) Math.sqrt(values[i]);
        	else 
        		values[i] = - (int) Math.sqrt(-values[i]);
        }
	}
	
	/**
	 * Signed Square Rooting Normalization
	 * 
	 * @param values	the array to be power 0.5 normalized
	 */
//	public static final float[] ssr_float(int[] values) {
//		int size = values.length;
//		float[] res = new float[size];
//		// Power Normalization 0.5
//        for (int i=0; i<size; i++) {     
//        	if ( values[i] == 0 ) res[i] = 0.0F;
//        	else if ( values[i] > 0 )
//        		res[i] =   (float) Math.sqrt((double) values[i]);
//        	else 
//        		res[i] = - (float) Math.sqrt((double) -values[i]);
//        }
//        return res;
//	}	
	
	public static final float[] sPower_float( int[] values, double a ) {
		int size = values.length;
		float[] res = new float[size];
        for (int i=0; i<size; i++) {     
        	if ( values[i] == 0 ) res[i] = 0.0F;
        	else if ( values[i] > 0 )
        		res[i] =   (float) Math.pow(values[i], a);
        	else 
        		res[i] = - (float) Math.pow(-values[i], a);
        }
        return res;
	}
	
	public static final void sPower( double[] values, double a ) {
		int size = values.length;
		for (int i=0; i<size; i++) {     
        	if ( values[i] == 0 ) values[i] = 0.0F;
        	else if ( values[i] > 0 )
        		values[i] =   Math.pow( values[i], a);
        	else 
        		values[i] = - Math.pow(-values[i], a);
        }
	}	
	
	public static final void sPower( float[] values, double a ) {
		int size = values.length;
		for (int i=0; i<size; i++) {     
        	if ( values[i] > 0.0F )
        		values[i] =   (float) Math.pow(values[i], a);
        	else if ( values[i] < 0.0F ) 
        		values[i] = - (float) Math.pow(-values[i], a);
        }
	}
	
	
	
}
