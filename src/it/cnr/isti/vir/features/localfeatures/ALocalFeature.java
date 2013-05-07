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

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.IFeature;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ALocalFeature<LFGroup extends ALocalFeaturesGroup> implements Cloneable, IFeature, Comparable<ALocalFeature<LFGroup>>, ILabeled {
	
	protected KeyPoint kp = null; 
	protected LFGroup linkedGroup = null;
	
	public abstract int getDataByteSize();
	
	public abstract int putBytes(byte[] byteArr, int offset);
	
	public byte[] getBytes() {
		int byteSize = getDataByteSize() + 1;
		if ( kp != null ) {
			byteSize += kp.getByteSize();
		}
		byte[] bytes = new byte[byteSize];
		int bArrI = 0;
		if ( kp == null )
			bytes[bArrI++] = -1;
		else 
			bytes[bArrI++] = 1;
		bArrI = kp.putBytes(bytes, bArrI);
		bArrI = putBytes(bytes, bArrI);
	
		return bytes;
	}
	
	public ALocalFeature(DataInput str ) throws IOException {
		byte kpExists = str.readByte();
		if ( kpExists != -1 ) {
			new KeyPoint(str);
		}
	}
	
	public ALocalFeature(DataInput str, LFGroup group) throws IOException {
		this(str);
		this.linkedGroup = group;
	}
	
	public ALocalFeature(ByteBuffer src ) throws IOException {
		byte kpExists = src.get();
		if ( kpExists != -1 ) {
			kp = new KeyPoint(src);
		}
	}
	
	public ALocalFeature(ByteBuffer src, LFGroup group ) throws IOException {
		this(src);
		this.linkedGroup = group;		
	}
	
	public ALocalFeature() {};
	
	public ALocalFeature(KeyPoint kp, LFGroup linkedGroup) {
		this.kp = kp;
		this.linkedGroup = linkedGroup;
	}
	
	public ALocalFeature(KeyPoint kp) {
		this(kp, null);
	}
	
	public ALocalFeature(LFGroup linkedGroup) {
		this((KeyPoint) null, linkedGroup);
	}
	
	public LFGroup getLinkedGroup() {
		return linkedGroup;
	}
		
	public ALocalFeature unlink() {
		linkedGroup = null;
		return this;
	}
	
	public ALocalFeature getUnlinked() {
		return this.clone().unlink();
	}
		
	public ALocalFeature clone() {
		try {
			return (ALocalFeature) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final float getScale() {
		return kp.getScale();
	}

    public final float getNormScale() {
        return linkedGroup.getNormScale() * getScale();
    }

	public final float getOrientation() {
		return kp.getOri();
	}

	public float[] getXY() {
		return kp.getXY();
	}

	public synchronized float[] getNormXY() {

		return kp.getNormXY(linkedGroup);
	}
	
	
	public AbstractLabel getLabel() {
		return ((ILabeled) linkedGroup).getLabel();
	}
	
	
	public final void writeData(DataOutput str) throws IOException {
		str.write(getBytes());
	}
	
	
	
	public abstract Class<LFGroup> getGroupClass();
	

	@Override
	public int compareTo(ALocalFeature<LFGroup> o) {
		if ( this == o ) return 0;
		if ( kp == null ) return 1;
		return kp.compareTo(o.kp);
	}
	
	@Override
	public final boolean equals(Object obj) {
		if ( obj == null ) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		return 0 == this.compareTo((ALocalFeature<LFGroup>) obj);
	}

	
	//public abstract byte[] getBytes();
	
	//public float getScale();

    //public float getNormScale();
	
	//public float getOrientation();
	
	//public float[] getXY();

    //public float[] getNormXY();
	
	//public abstract ILocalFeature<LFGroup> getUnlinked(); 
    
}
