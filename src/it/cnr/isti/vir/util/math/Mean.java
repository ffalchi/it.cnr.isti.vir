/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
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
import it.cnr.isti.vir.features.IDoubleValues;
import it.cnr.isti.vir.features.IFloatByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.IUByteValues;

import java.util.Collection;


public  class Mean {

	
	public static byte[] getMean(byte[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		long[] tempVec = new long[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}
		
		byte[] newVec = new byte[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = (byte) ( Math.round( (double) tempVec[iD] / n ) );
		}
				
		return newVec;
	}

	public static short[] getMean(short[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		long[] tempVec = new long[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}
		
		short[] newVec = new short[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = (short) ( Math.round( (double) tempVec[iD] / n ) );
		}
				
		return newVec;
	}
	
	public static float[] getMean(float[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		double[] tempVec = new double[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
				tempVec[iD] += data[i][iD];
			}
		}
		
		float[] newVec = new float[dim];
		for ( int iD=0; iD<dim; iD++) {
			newVec[iD] = (float) tempVec[iD] / (float)  n;
		}
				
		return newVec;
	}
	
	
	public static double[] getMean(double[][] data) {
		final int dim = data[0].length;
		final int n = data.length;
		double[] tempVec = new double[dim];
		for ( int i=0; i<n; i++ ) {
			for ( int iD=0; iD<dim; iD++) {
					tempVec[iD] += data[i][iD];
			}
		}

		for ( int iD=0; iD<dim; iD++) {
			tempVec[iD] = tempVec[iD] / n;
		}
				
		return tempVec;
	}
	
	public static double getMean(double[] data) {
		double sum =0;
		for ( double curr : data ) sum += curr;
		return sum / data.length;
	}
	
	
	public static final double getAvg(float[][] intDist) {
		double avg = 0;
		int count = 0;
		for ( int i=1; i<intDist.length; i++ ) {
			for ( int j=0; j<intDist[i].length; j++ ) {
				avg += intDist[i][j];
			}
			count += intDist[i].length;
		}
		return avg / count;
	}
	
	

	public static final double[] getMeans(Collection<? extends IArrayValues> coll) throws Exception {
		if ( coll.size() == 0 ) return null;
		IArrayValues firstObj = coll.iterator().next();
		
		if ( firstObj instanceof IDoubleValues)
			return getMeans_fromDoubles( (Collection<IDoubleValues>) coll );
		
		if ( firstObj instanceof IFloatValues)
			return getMeans_fromFloats( (Collection<IFloatValues>) coll );
		
		if ( firstObj instanceof IIntValues)
			return getMeans_fromInts( (Collection<IIntValues>) coll );
		
		if ( firstObj instanceof IByteValues)
			return getMeans_fromBytes( (Collection<IByteValues>) coll );

		if ( firstObj instanceof IUByteValues)
			return getMeans_fromUBytes( (Collection<IUByteValues>) coll );

		if ( firstObj instanceof IFloatByteValues)
			return getMeans_fromFloatBytes( (Collection<IFloatByteValues>) coll );
		
		throw new Exception("Collection objet type was unknown");
				
	}
	
	private static final double[] getMeans_fromDoubles(Collection<IDoubleValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IDoubleValues curr : coll ) {
			VectorMath.add(res, curr.getValues());
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
	
	private static final double[] getMeans_fromFloats(Collection<IFloatValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IFloatValues curr : coll ) {
			VectorMath.add(res,  VectorMath.getDoubles(curr.getValues()));
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
	
	private static final double[] getMeans_fromInts(Collection<IIntValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IIntValues curr : coll ) {
			VectorMath.add(res,  VectorMath.getDoubles(curr.getValues()));
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
	
	private static final double[] getMeans_fromBytes(Collection<IByteValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IByteValues curr : coll ) {
			VectorMath.add(res,  VectorMath.getDoubles(curr.getValues()));
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
	
	private static final double[] getMeans_fromUBytes(Collection<IUByteValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IUByteValues curr : coll ) {
			VectorMath.add(res,  VectorMath.getDoubles_UBytes(curr.getValues()));
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
	
	private static final double[] getMeans_fromFloatBytes(Collection<IFloatByteValues> coll) {
		double[] res = new double[coll.iterator().next().getLength()];
		
		for ( IFloatByteValues curr : coll ) {
			VectorMath.add(res,  VectorMath.getDoubles_FloatBytes(curr.getValues()));
		}
		VectorMath.multiply(res, 1/coll.size());
		return res;
	}
}
