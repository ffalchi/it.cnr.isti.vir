/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi and Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.util.math;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.IUByteValues;

public class VectorMath {

	
	
	public static final double[] subtraction(double[] v1, double[] v2) {
		int nD = v1.length;
		double[] res = new double[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - v2[iD];
		}
		return res;
	}
	
	/**
	 * @param v1	is modified
	 * @param v2	is subctrated
	 * @return
	 */
	public static final void subtract(double[] v1, double[] v2) {
		int nD = v1.length;
		for (int iD = 0; iD < nD; iD++) {
			v1[iD] = v1[iD] - v2[iD];
		}
	}

	public static final float[] subtraction(float[] v1, float[] v2) {
		int nD = v1.length;
		float[] res = new float[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - v2[iD];
		}
		return res;
	}
	
	public static final float[] subtraction_float(float[] v1, double[] v2) {
		int nD = v1.length;
		float[] res = new float[nD];
		for (int iD = 0; iD < nD; iD++) {
			res[iD] = v1[iD] - (float) v2[iD];
		}
		return res;
	}
	
	public static final double mean(float[] v) {
		return sum(v) / (double) v.length;
	}
	
	public static final double sum(float[] v) {
		int nD = v.length;
		double res = 0;
		for (int iD = 0; iD < nD; iD++)
			res += v[iD];
		return res;
	}
	
	public static final double mean(double[] v) {
		return sum(v) / (double) v.length;
	}
	
	public static final double sum(double[] v) {
		int nD = v.length;
		double res = 0;
		for (int iD = 0; iD < nD; iD++)
			res += v[iD];
		return res;
	}
	
	public static final double scalarProduct( double[] a, double[] b) {
		double res = 0;
		
		for ( int i=0; i<a.length; i++) {
			res += a[i] * b[i];
		}
		
		return res;		
	}
	
	public static final double scalarProduct( float[] a, double[] b) {
		double res = 0;
		
		for ( int i=0; i<a.length; i++) {
			res += a[i] * b[i];
		}
		
		return res;		
	}
	
	public static final double scalarProduct( double[] a, float[] b) {
		double res = 0;
		
		for ( int i=0; i<a.length; i++) {
			res += a[i] * b[i];
		}
		
		return (float) res;		
	}
	
	public static final float scalarProduct( float[] a, float[] b) {
		float res = 0;
		
		for ( int i=0; i<a.length; i++) {
			res += a[i] * b[i];
		}
		
		return res;		
	}
	
	public static final void multiply(float[] v, float c) {
		int nD = v.length;
		for (int iD = 0; iD < nD; iD++) {
			v[iD] *= c;
		}
	}
	
	public static final void multiply(double[] v, double c) {
		int nD = v.length;
		for (int iD = 0; iD < nD; iD++) {
			v[iD] *= c;
		}
	}
	
	public static final float[] getFloats_UBytes(byte[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (values[i] + 128);
		}
		return res;		
	}
	
	public static final float[] getFloats_FloatBytes(byte[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (values[i] + 128) / 255.0f;
		}
		return res;		
	}

	public static final float[] getFloats(byte[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}

	public static final float[] getFloats(int[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}
	
	public static final float[] getFloats(double[] values ) {
		float[] res = new float[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (float) values[i];
		}
		return res;		
	}
	
	public static final double[] getDoubles_UBytes(byte[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (values[i] + 128);
		}
		return res;		
	}
	
	public static final double[] getDoubles_FloatBytes(byte[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = (values[i] + 128) / 255.0;
		}
		return res;		
	}

	public static final double[] getDoubles(byte[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}
	
	public static final double[] getDoubles(int[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}
	
	public static final double[] getDoubles(float[] values ) {
		double[] res = new double[values.length];
		for ( int i=0; i<values.length; i++) {
			res[i] = values[i];
		}
		return res;		
	}

	/**
	 * Sums values in b to a.
	 * @param a
	 * @param b
	 */
	public static final void add(double[] a, double[] b) {
		for ( int i=0; i<a.length; i++) {
			a[i] += b[i];
		}
	}
	
	/**
	 * Sums values in b to a.
	 * @param a
	 * @param b
	 */
	public static final void add(double[] a, float[] b) {
		for ( int i=0; i<a.length; i++) {
			a[i] += b[i];
		}
	}	
	
	/**
	 * Sums values in b to a.
	 * @param a
	 * @param b
	 */
	public static final void add(float[] a, double[] b) {
		for ( int i=0; i<a.length; i++) {
			a[i] += b[i];
		}
	}	
	
	 
	public static final double[] getDiff_double( float[] a, float[] b ) {
		double[] res = new double[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (double) a[i] - b[i];
		}
		return res;
	}

	public static final double[] getDiff_double( byte[] a, byte[] b ) {
		double[] res = new double[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (double) a[i] - b[i];
		}
		return res;
	}

	public static final double[] getDiff_double( int[] a, int[] b ) {
		double[] res = new double[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (double) a[i] - b[i];
		}
		return res;
	}
	 
	public static final float[] getDiff_float( float[] a, float[] b ) {
		float[] res = new float[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (float) a[i] - b[i];
		}
		return res;
	}

	public static final float[] getDiff_float( byte[] a, byte[] b ) {
		float[] res = new float[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (float) a[i] - b[i];
		}
		return res;
	}

	public static final float[] getDiff_float( int[] a, int[] b ) {
		float[] res = new float[a.length];
		
		for ( int i=0; i<a.length; i++ ) {
			res[i] = (float) a[i] - b[i];
		}
		return res;
	}
	
	public static final double[] diff(IArrayValues a, IArrayValues b) throws Exception {
	
		
		if ( a instanceof IFloatValues ) {
			return getDiff_double( ((IFloatValues) a).getValues(), ((IFloatValues) b).getValues() );
			
		} else if ( a instanceof IByteValues ) {
			return getDiff_double( ((IByteValues) a).getValues(), ((IByteValues) b).getValues() );
			
		} else if ( a instanceof IUByteValues ) {
			return getDiff_double( ((IUByteValues) a).getValues(), ((IUByteValues) b).getValues() );
				
		} else if ( a instanceof IIntValues  ) {
			return getDiff_double( ((IIntValues) a).getValues(), ((IIntValues) b).getValues() );
			

		} else {
        	throw new Exception( "diff can't be computed for " + a.getClass() );
		}		
		
	}
	
	public static final float[] diff_float(IArrayValues a, IArrayValues b) throws Exception {
	
		
		if ( a instanceof IFloatValues ) {
			return getDiff_float( ((IFloatValues) a).getValues(), ((IFloatValues) b).getValues() );
			
		} else if ( a instanceof IByteValues ) {
			return getDiff_float( ((IByteValues) a).getValues(), ((IByteValues) b).getValues() );
			
		} else if ( a instanceof IUByteValues ) {
			return getDiff_float( ((IUByteValues) a).getValues(), ((IUByteValues) b).getValues() );
				
		} else if ( a instanceof IIntValues  ) {
			return getDiff_float( ((IIntValues) a).getValues(), ((IIntValues) b).getValues() );
			

		} else {
        	throw new Exception( "diff can't be computed for " + a.getClass() );
		}		
		
	}
	
	


}

