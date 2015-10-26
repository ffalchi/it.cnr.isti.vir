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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;
import it.cnr.isti.vir.util.math.Mean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Floats extends AbstractFeature implements IFloatValues {

	public AbstractFeaturesCollector linkedFC;
	
	public float[] values;

    public Floats(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public Floats(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new float[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getFloat();
			}
		}	
	}
	
	public Floats(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Float.BYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = FloatByteArrayUtil.get(bytes, 0, size);
		}
    }
	
	public Floats(float[] values) {
		this.values = values;
	}

	public Floats(DataInput in ) throws Exception {
		this(in, null);
	}
	
	
	
	final int getDim() {
		return values.length;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length );
			byte[] b = new byte[FloatByteArrayUtil.BYTES*values.length];
			FloatByteArrayUtil.convToBytes(values, b, 0);
			out.write(b);
		}
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		if ( values == null ) {
			buff.putInt( 0);
		} else {
			buff.putInt( values.length);
			for ( int i=0; i<values.length; i++ )
				buff.putFloat(values[i]);
		}
	}
	


	@Override
	public int getLength() {
		return values.length;
	}

	@Override
	public float[] getValues() {
		return values;
	}
	
	public static Floats getMean(Collection<Floats> coll) {
		if ( coll.size() == 0 ) return null;
		float[][] values = new float[coll.size()][];
		int i=0;
		for ( Iterator<Floats> it = coll.iterator(); it.hasNext(); ) {
			values[i++] = it.next().values;
		}
				
		return new Floats(Mean.getMean(values));		
	}

	public void reduceToDim(int dim) throws Exception {
		if ( dim > values.length )
				throw new Exception("Requested dimensionality greater than current.");
		values = Arrays.copyOf(values, dim);
		
	}
	
}
