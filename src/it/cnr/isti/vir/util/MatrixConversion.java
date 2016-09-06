package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IDoubleValues;
import it.cnr.isti.vir.features.IFloatByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.ILongBinaryValues;
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
		
		if ( firstObj instanceof ILongBinaryValues)
			return getDoubles_fromLongBytes( (Collection<ILongBinaryValues>) coll );
		
		throw new Exception("Collection objet type was unknown");
				
	}
	
	/*lucia*/
	private static final double[][] getDoubles_fromLongBytes(Collection<ILongBinaryValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( ILongBinaryValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles_ILongBinary(curr);//
			i++;
		}
		return matrix;
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
