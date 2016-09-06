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
	
	
	
	public static final float[] getFloats(IArrayValues given) {
		
		if ( given instanceof IDoubleValues)
			return getFloats( (IDoubleValues) given );
		
		if ( given instanceof IFloatValues)
			return getFloats( (IFloatValues) given );
		
		if ( given instanceof IIntValues)
			return getFloats( (IIntValues) given );
		
		if ( given instanceof IByteValues)
			return getFloats( (IByteValues) given );

		if ( given instanceof IUByteValues)
			return getFloats( (IUByteValues) given );
		
		if ( given instanceof IFloatByteValues)
			return getFloats( (IFloatByteValues) given );
		
		//throw new Exception("Objet type was unknown");
		return null;		
	}
	
	private static final float[] getFloats(IFloatValues given) {
		return given.getValues();
	}
	
	private static final float[] getFloats(IDoubleValues given) {
		return VectorMath.getFloats( given.getValues() );
	}
	
	private static final float[] getFloats(IIntValues given) {
		return VectorMath.getFloats(given.getValues());
	}
	
	private static final float[] getFloats(IByteValues given) {
		return VectorMath.getFloats(given.getValues());
	}	
	
	private static final float[] getFloats(IUByteValues given) {
		return VectorMath.getFloats_UBytes(given.getValues());
	}
	
	private static final float[] getFloats(IFloatByteValues given) {
		return VectorMath.getFloats_FloatBytes(given.getValues());
	}
}
