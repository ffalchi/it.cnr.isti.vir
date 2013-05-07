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

import it.cnr.isti.vir.features.IFeaturesCollector;

import java.io.DataInput;
import java.nio.ByteBuffer;

public class ORBGroup extends ALocalFeaturesGroup<ORB> {

	public static final byte version = 0;
	
	public ORBGroup(ORB[] arr, IFeaturesCollector fc) {
		super(arr, fc);
	}

	public ORBGroup(IFeaturesCollector fc) {
		super(fc);
	}

	public ORBGroup(DataInput in) throws Exception {
		this(in, null);
	}
	
	public ORBGroup(DataInput in, IFeaturesCollector fc) throws Exception {
		super(fc);
		in.readByte(); // version
		int nBytes = in.readInt();
		byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
		lfArr = new ORB[bBuffer.getInt()];
		for (int i = 0; i < lfArr.length; i++) {
			this.lfArr[i] = new ORB(bBuffer, this);
		}
	}
	
	
	public ORBGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
		super(fc);
		in.get(); // version
		in.getInt(); // nBytes
		int nLFs = in.getInt();
		lfArr = new ORB[nLFs];
		for ( int i = 0; i < nLFs; i++ ) {
			lfArr[i] = new ORB(in, this);
		}
	}

	@Override
	public Class<ORB> getLocalFeatureClass() {
		return ORB.class;
	}

	@Override
	public ALocalFeaturesGroup<ORB> create(ORB[] arr, IFeaturesCollector fc) {
		return new ORBGroup( arr, fc);
	}
	@Override
	public byte getSerVersion() {
		return version;
	}

}
