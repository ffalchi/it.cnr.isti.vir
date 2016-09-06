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
package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IDoubleValues;
import it.cnr.isti.vir.features.IFloatByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.IUByteValues;
import it.cnr.isti.vir.util.math.VectorMath;

import java.util.Arrays;
import java.util.Collection;

public class MatrixConversion {

	
	public static final double[][] getDoubles(IArrayValues[] arr) throws Exception {
		return getDoubles( Arrays.asList( arr ));
	}
	
	public static final double[][] getDoubles(Collection<? extends IArrayValues> coll) throws Exception {
		if ( coll.size() == 0 ) return null;
		IArrayValues firstObj = coll.iterator().next();
		
		if ( firstObj instanceof IDoubleValues)
			return getDoubles_fromDoubles( (Collection<IDoubleValues>) coll );
		
		if ( firstObj instanceof IFloatValues)
			return getDoubles_fromFloats( (Collection<IFloatValues>) coll );
		
		if ( firstObj instanceof IIntValues)
			return getDoubles_fromInts( (Collection<IIntValues>) coll );
		
		if ( firstObj instanceof IByteValues)
			return getDoubles_fromBytes( (Collection<IByteValues>) coll );

		if ( firstObj instanceof IUByteValues)
			return getDoubles_fromUBytes( (Collection<IUByteValues>) coll );

		if ( firstObj instanceof IFloatByteValues)
			return getDoubles_fromFloatBytes( (Collection<IFloatByteValues>) coll );
		
		throw new Exception("Collection objet type was unknown");
				
	}
		
	private static final double[][] getDoubles_fromFloats(Collection<IFloatValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IFloatValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromDoubles(Collection<IDoubleValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IDoubleValues curr : coll ) {
			matrix[i] = curr.getValues();
			i++;
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromInts(Collection<IIntValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IIntValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromBytes(Collection<IByteValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IByteValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	
	private static final double[][] getDoubles_fromUBytes(Collection<IUByteValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IUByteValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles_UBytes(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromFloatBytes(Collection<IFloatByteValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IFloatByteValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles_FloatBytes(curr.getValues());
			i++;
		}
		return matrix;
	}	
	
	public static final float[][] getFloats(Collection<? extends IArrayValues> coll) throws Exception {
		if ( coll.size() == 0 ) return null;
		IArrayValues firstObj = coll.iterator().next();
		
		if ( firstObj instanceof IDoubleValues)
			return getFloats_fromDoubles( (Collection<IDoubleValues>) coll );
		
		if ( firstObj instanceof IFloatValues)
			return getFloats_fromFloats( (Collection<IFloatValues>) coll );
		
		if ( firstObj instanceof IIntValues)
			return getFloats_fromInts( (Collection<IIntValues>) coll );
		
		if ( firstObj instanceof IByteValues)
			return getFloats_fromBytes( (Collection<IByteValues>) coll );

		if ( firstObj instanceof IUByteValues)
			return getFloats_fromUBytes( (Collection<IUByteValues>) coll );
		
		if ( firstObj instanceof IFloatByteValues)
			return getFloats_fromFloatBytes( (Collection<IFloatByteValues>) coll );
			
		throw new Exception("Collection objet type was unknown");
				
	}
		
	private static final float[][] getFloats_fromFloats(Collection<IFloatValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IFloatValues curr : coll ) {
			matrix[i] = curr.getValues();
			i++;
		}
		return matrix;
	}
	
	private static final float[][] getFloats_fromDoubles(Collection<IDoubleValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IDoubleValues curr : coll ) {
			matrix[i] = VectorMath.getFloats(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final float[][] getFloats_fromInts(Collection<IIntValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IIntValues curr : coll ) {
			matrix[i] = VectorMath.getFloats(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final float[][] getFloats_fromBytes(Collection<IByteValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IByteValues curr : coll ) {
			matrix[i] = VectorMath.getFloats(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	
	private static final float[][] getFloats_fromUBytes(Collection<IUByteValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IUByteValues curr : coll ) {
			matrix[i] = VectorMath.getFloats_UBytes(curr.getValues());
			i++;
		}
		return matrix;
	}
	
	private static final float[][] getFloats_fromFloatBytes(Collection<IFloatByteValues> coll) {
		float[][] matrix = new float[coll.size()][];
		int i=0;

		for ( IFloatByteValues curr : coll ) {
			matrix[i] = VectorMath.getFloats_FloatBytes(curr.getValues());
			i++;
		}
		return matrix;
	}
}
