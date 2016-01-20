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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.util.math.Mean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class FloatsL2Norm_CompSparseBytes extends AbstractFeature implements IUByteValues {

	public AbstractFeaturesCollector linkedFC;
	
	public byte[] values;
	
	final int getDim() {
		return values.length;
	}
	
	public static byte[] getComp(byte[] origValues) {
		byte[] res = new byte[getCompSize(origValues)];
		int ir=0;
		for ( int i=0; i<origValues.length; ) {
			byte curr = origValues[i++];
			res[ir++]= curr;
			if ( curr == -128 ) {
				int count = 0;
				while ( i<origValues.length && origValues[i] == -128 && count < 255 ) {
					i++;
					count++;
				}
				res[ir++]= (byte) (count-128);
			}
		}
				
		return res;
	}

	public static byte[] getComp(float[] floatValues) {
		return getComp(FloatsL2Norm_UBytes.getBytes(floatValues));
	}
	public FloatsL2Norm_CompSparseBytes(float[] floatValues) {
		this( getComp(floatValues) );
	}
	
	public static final int getDecompSize(byte[] values) {
		int size = 0;
		
		for ( int i=0; i<values.length; i++ ) {
			size++;
			if ( values[i] == -128 ) {
				size += values[++i] +128;
			} 
		}
		
		return size;
	}
	
	public static int getCompSize(byte[] origValues) {
		int res = 0;
		for ( int i=0; i<origValues.length; ) {
			byte curr = origValues[i++];
			res++;
			if ( curr == -128 ) {
				int count = 0;
				while ( i<origValues.length && origValues[i] == -128 && count < 255 ) {
					i++;
					count++;
				}
				res++;
			}
		}
				
		return res;
	}
	
	public byte[] getDecomp() {
		return getDecomp(values);
	}
	
	public static final byte[] getDecomp(byte[] values) {
		byte[] res = new byte[getDecompSize(values)];
		Arrays.fill(res, (byte) -128);
		int ir=0;
		int iv=0;
		

		while ( iv<values.length ) {
			byte curr = values[iv++];
			if ( curr == -128 ) {
				ir += values[iv++] + 128 +1;
			} else {
				res[ir++] = curr;
			}
		}
		return res;

	}
	
	public FloatsL2Norm_CompSparseBytes(byte[] values) {
		this.values = values;
	}

	
	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length );
			out.write(values);
		}
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		if ( values == null ) {
			buff.putInt( 0);
		} else {
			buff.putInt( values.length);
			buff.put(values);
		}
	}
	
    public FloatsL2Norm_CompSparseBytes(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public FloatsL2Norm_CompSparseBytes(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new byte[size];
			in.get(values);
		}	
	}
	
	public FloatsL2Norm_CompSparseBytes(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			values = new byte[size];
			in.readFully(values);
		}
    }
	


	public FloatsL2Norm_CompSparseBytes(DataInput in ) throws Exception {
		this(in, null);
	}

	@Override
	public int getLength() {
		return values.length;
	}

	@Override
	public byte[] getValues() {
		return values;
	}
	
	public static FloatsL2Norm_CompSparseBytes getMean(Collection<FloatsL2Norm_CompSparseBytes> coll) {
		if ( coll.size() == 0 ) return null;
		byte[][] values = new byte[coll.size()][];
		int i=0;
		for ( Iterator<FloatsL2Norm_CompSparseBytes> it = coll.iterator(); it.hasNext(); ) {
			values[i++] = it.next().getDecomp();
		}
				
		return new FloatsL2Norm_CompSparseBytes(Mean.getMean(values));		
	}

	public void reduceToDim(int dim) throws Exception {
		if ( dim > values.length )
				throw new Exception("Requested dimensionality greater than current.");
		values = Arrays.copyOf(values, dim);		
	}
	
}
