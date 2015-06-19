package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IDoubleValues;
import it.cnr.isti.vir.features.IFloatByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.IUByteValues;
import it.cnr.isti.vir.util.math.VectorMath;

public class ArrayValuesConversion {
	
	public static final double[] getDoubles(IArrayValues given) {
		
		if ( given instanceof IDoubleValues)
			return getDoubles( (IDoubleValues) given );
		
		if ( given instanceof IFloatValues)
			return getDoubles( (IFloatValues) given );
		
		if ( given instanceof IIntValues)
			return getDoubles( (IIntValues) given );
		
		if ( given instanceof IByteValues)
			return getDoubles( (IByteValues) given );

		if ( given instanceof IUByteValues)
			return getDoubles( (IUByteValues) given );
		
		if ( given instanceof IFloatByteValues)
			return getDoubles( (IFloatByteValues) given );
		
		//throw new Exception("Objet type was unknown");
		return null;		
	}
	
	private static final double[] getDoubles(IFloatValues given) {
		return VectorMath.getDoubles(given.getValues());
	}
	
	private static final double[] getDoubles(IDoubleValues given) {
		return given.getValues();
	}
	
	private static final double[] getDoubles(IIntValues given) {
		return VectorMath.getDoubles(given.getValues());
	}
	
	private static final double[] getDoubles(IByteValues given) {
		return VectorMath.getDoubles(given.getValues());
	}	
	
	private static final double[] getDoubles(IUByteValues given) {
		return VectorMath.getDoubles_UBytes(given.getValues());
	}
	
	private static final double[] getDoubles(IFloatByteValues given) {
		return VectorMath.getDoubles_FloatBytes(given.getValues());
	}
	
}
