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
package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;
import it.cnr.isti.vir.util.bytes.IntByteArrayUtil;
import it.cnr.isti.vir.util.math.Mean;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class FloatsLF extends ALocalFeature<FloatsLFGroup> implements IFloatValues{
	

	public final float[] values; 
	
	@Override
	public final int getLength() {
		return values.length;
	}

    @Override
    public float[] getValues() {
        return values;
    }

    public FloatsLF(DataInput str) throws IOException {
        super(str);
        int dim = str.readInt();
        values = new float[dim];
        for (int i = 0; i < values.length; i++) {
            values[i] = str.readFloat();
        }
    }

    public FloatsLF(ByteBuffer src) throws IOException {
        super(src);
        int dim = src.getInt();
        values = new float[dim];
        for (int i = 0; i < values.length; i++) {
            values[i] = src.getFloat();
        }
    }
        
    
    public FloatsLF(KeyPoint kp, float[] values) {
		this(kp, values, null);
	}
	
	public FloatsLF(KeyPoint kp, float[] values, FloatsLFGroup group) {
		this.kp = kp;
		this.values = values;
		this.linkedGroup = group;
	}
	
	public FloatsLF(float[] values) {
		super((KeyPoint) null, null);
		this.values = values;
	}

	public int getDataByteSize() {
		return values.length*4 +4;
	}
	
	public int putDescriptor(byte[] bArr, int bArrI) {
		bArrI=IntByteArrayUtil.convToBytes(values.length, bArr, bArrI);//
		return FloatByteArrayUtil.convToBytes(values, bArr, bArrI);
	}

	@Override
	public int compareTo(ALocalFeature<FloatsLFGroup> given) {
		if ( this == given ) return 0;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		for ( int i=0; i<values.length; i++ ) {
			int tComp = Float.compare(values[i], ((FloatsLF)given).values[i]);
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}

	@Override
	public Class<FloatsLFGroup> getGroupClass() {
		return FloatsLFGroup.class;
	}

	public static FloatsLF getMean(Collection<FloatsLF> coll) {
		if ( coll.size() == 0 ) return null;
		float[][] values = new float[coll.size()][];
		int i=0;
		for ( Iterator<FloatsLF> it = coll.iterator(); it.hasNext(); ) {
			values[i++] = it.next().values;
		}
				
		return new FloatsLF(Mean.getMean(values));		
	}	
	
}
