/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;
import it.cnr.isti.vir.util.bytes.IntByteArrayUtil;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;


public class BoFLFSoft extends ALocalFeature<BoFLFSoftGroup> {

	public  final int[] bag;
	public 	final float[] wBag;
	
	public final int getDataByteSize() {
		return bag.length * 4 + wBag.length * 4;
	}
	
	public final int getByteSize() {
		return 1 + 4 + 4 + 4 * 2 + bag.length * 4 + wBag.length * 4;
	}
	
	public BoFLFSoft(KeyPoint kp, int[] bag, float[] wBag ) {
		this.kp = kp;
		this.bag = bag;
		this.wBag = wBag;
	}

	
	public BoFLFSoft(DataInput in ) throws IOException {
		super(in);
		byte size = in.readByte();
		bag = new int[size];
		wBag = new float[size];
		for ( int i=0; i<size; i++ ) {
			bag[i] = in.readInt();
		}
		for ( int i=0; i<size; i++ ) {
			wBag[i] = in.readFloat();
		}		
	}
	
	public BoFLFSoft(ByteBuffer in ) throws IOException {
		super(in);
		byte size = in.get();
		bag = new int[size];
		wBag = new float[size];
		for ( int i=0; i<size; i++ ) {
			bag[i] = in.getInt();
		}
		for ( int i=0; i<size; i++ ) {
			wBag[i] = in.getFloat();
		}		
	}

	public final int putDescriptor(byte[] bArr, int bArrI) {
		int offSet = bArrI;
		bArr[offSet++] = (byte) bag.length;
		offSet = IntByteArrayUtil.convToBytes(bag, bArr, offSet);
		offSet = FloatByteArrayUtil.convToBytes(wBag, bArr, offSet);
		return offSet;		
	}
	

	@Override
	public int compareTo(ALocalFeature<BoFLFSoftGroup> o) {
		BoFLFSoft given = (BoFLFSoft) o;
		for ( int i=0; i<bag.length; i++ ) {
			if ( bag[i] != given.bag[i] ) 	return bag[i]-given.bag[i];
		}
		for ( int i=0; i<wBag.length; i++ ) {
			if ( wBag[i] != given.wBag[i] ) return Float.compare( wBag[i], given.wBag[i] );
		}
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}

	@Override
	public AbstractLabel getLabel() {
		return ((ILabeled) linkedGroup).getLabel();
	}

	@Override
	public Class getGroupClass() {
		return BoFLFGroup.class;
	}

}
