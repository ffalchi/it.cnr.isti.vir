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

import it.cnr.isti.vir.util.Trigonometry;
import it.cnr.isti.vir.util.bytes.IntByteArrayUtil;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BoFLF extends ALocalFeature<BoFLFGroup> {

	public final int bag;
	
	public final Class getGroupClass() { return BoFLFGroup.class; };
	
	public BoFLF(int bag ) {
		this(bag, null);
	}
	
	public BoFLF(int bag, KeyPoint kp ) {
		this.kp = kp;
		this.bag = bag;
	}
	
	public BoFLF(int bag, KeyPoint kp, BoFLFGroup linkedGroup) {
		this.kp = kp;
		this.bag = bag;
		this.linkedGroup = linkedGroup;
	}

	public BoFLF(BoFLF given, BoFLFGroup group) {
		this(given.bag, given.kp, group);
	}	
	
	
	public final int getDataByteSize() {
		return Integer.SIZE/Byte.SIZE;
	}
	
	public int putDescriptor(byte[] bArr, int bArrI) {
		return IntByteArrayUtil.convToBytes(bag, bArr, bArrI);
	}
	
	public BoFLF(DataInput str) throws IOException {
		super(str);			
		bag = str.readInt();
	}
	
	public BoFLF(ByteBuffer in) throws IOException {
		super(in);
		bag = in.getInt();
	}
	
	public static BoFLF read_old(ByteBuffer in, BoFLFGroup linkedGroup) {
		int bag = in.getInt();
		float x = in.getFloat();
		float y = in.getFloat();
		float ori = in.getFloat();
		float scale = in.getFloat();
		KeyPoint kp = new KeyPoint(x, y, ori, scale);
		return new BoFLF(bag, kp, linkedGroup);
}
	

	public static BoFLF readTOPSURF(String line, BoFLFGroup linkedGroup) {
		String[] temp = line.split("(\\s)+");
		int i = 0;
		if ( temp[0].equals("") ) i++; 
		int tBag = Integer.parseInt(temp[i++]);
		float x = Float.parseFloat(temp[i++]);
		float y = Float.parseFloat(temp[i++]);
		float tScale = Float.parseFloat(temp[i++]);
		float tOri   = (float) Trigonometry.getStdRadian( Double.parseDouble(temp[i++]) );
		
		return new BoFLF( tBag, new KeyPoint(x,y,tOri,tScale), linkedGroup) ;
	}

	@Override
	public int compareTo(ALocalFeature<BoFLFGroup> o) {
		BoFLF given = (BoFLF) o;
		int tComp = Integer.compare(bag, given.bag);
		if (tComp != 0) return tComp;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}


	public String toString() {
		return bag + ", " + kp;
		//return bag + " " ;
	}

}
