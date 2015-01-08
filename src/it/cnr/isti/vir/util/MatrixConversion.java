package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IDoubleValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.IUByteValues;
import it.cnr.isti.vir.util.math.VectorMath;

import java.util.Collection;

public class MatrixConversion {

	
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
		
		throw new Exception("Collection objet type was unknown");
				
	}
		
	private static final double[][] getDoubles_fromFloats(Collection<IFloatValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IFloatValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromDoubles(Collection<IDoubleValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IDoubleValues curr : coll ) {
			matrix[i] = curr.getValues();
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromInts(Collection<IIntValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IIntValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
		}
		return matrix;
	}
	
	private static final double[][] getDoubles_fromBytes(Collection<IByteValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IByteValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles(curr.getValues());
		}
		return matrix;
	}
	
	
	private static final double[][] getDoubles_fromUBytes(Collection<IUByteValues> coll) {
		double[][] matrix = new double[coll.size()][];
		int i=0;

		for ( IByteValues curr : coll ) {
			matrix[i] = VectorMath.getDoubles_UBytes(curr.getValues());
		}
		return matrix;
	}
	
}
