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

public class FloatsL2Norm_Bytes extends AbstractFeature implements IUByteValues {

	public AbstractFeaturesCollector linkedFC;
	
	public byte[] values;
	
	final int getDim() {
		return values.length;
	}

	public FloatsL2Norm_Bytes(float[] floatValues) {
		values = getBytes(floatValues);
	}
	
	public static byte[] getBytes(float[] floatValues) {
		byte[] values = new byte[floatValues.length];
		
		for ( int i=0; i<floatValues.length; i++ ) {
			values[i] = (byte) ( Math.round( floatValues[i] * 255.0F ) - 128 );
		}
		return values;
	}
	
	
	public FloatsL2Norm_Bytes(byte[] values) {
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
	
    public FloatsL2Norm_Bytes(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public FloatsL2Norm_Bytes(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new byte[size];
			in.get(values);
		}	
	}
	
	public FloatsL2Norm_Bytes(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			values = new byte[size];
			in.readFully(values);
		}
    }
	


	public FloatsL2Norm_Bytes(DataInput in ) throws Exception {
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
	
	public static FloatsL2Norm_Bytes getMean(Collection<FloatsL2Norm_Bytes> coll) {
		if ( coll.size() == 0 ) return null;
		byte[][] values = new byte[coll.size()][];
		int i=0;
		for ( Iterator<FloatsL2Norm_Bytes> it = coll.iterator(); it.hasNext(); ) {
			values[i++] = it.next().values;
		}
				
		return new FloatsL2Norm_Bytes(Mean.getMean(values));		
	}

	public void reduceToDim(int dim) throws Exception {
		if ( dim > values.length )
				throw new Exception("Requested dimensionality greater than current.");
		values = Arrays.copyOf(values, dim);		
	}
	
}
