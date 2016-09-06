/*******************************************************************************
 * Copyright (c), Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
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

import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;
import it.cnr.isti.vir.util.math.Binarization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BinaryLongs extends AbstractFeature implements ILongBinaryValues {

	public AbstractFeaturesCollector linkedFC;
	
	public long[] values;

    public BinaryLongs(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public BinaryLongs(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new long[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getLong();
			}
		}	
	}
	
	public BinaryLongs(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Long.BYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = LongByteArrayUtil.getArr(bytes, 0, size);
		}
    }
	
	public BinaryLongs(long[] values) {
		this.values = values;
	}
	
	public BinaryLongs(float[] f ) {
		values = Binarization.getLongs(f );
	}

	public BinaryLongs(float[] f, float thr) {
		values = Binarization.getLongs(f, thr);
	}
	
	public BinaryLongs(DataInput in ) throws Exception {
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
			byte[] b = new byte[LongByteArrayUtil.BYTES*values.length];
			LongByteArrayUtil.convToBytes(values, b, 0);
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
	public final long[] getValues() {
		return values;
	}


	@Override
	public int getNBits() {
		return values.length * 64;
	}

	public int bitCount() {
		int res = 0;
		for (int i=0; i<values.length; i++) {
			res += Long.bitCount(values[i]);
		}
		
		return res;
	}
	
}
