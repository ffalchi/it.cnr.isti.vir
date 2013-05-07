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

import it.cnr.isti.vir.util.IntByteArrayUtil;
import it.cnr.isti.vir.util.Trigonometry;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BoFLF extends ALocalFeature<BoFLFGroup> {

	public final int bag;
	
	public final Class getGroupClass() { return BoFLFGroup.class; };
	
	public BoFLF(int bag, KeyPoint kp, BoFLFGroup linkedGroup) {
		this.kp = kp;
		this.bag = bag;
		this.linkedGroup = linkedGroup;
	}

	public BoFLF(BoFLF given, BoFLFGroup group) {
		this(given.bag, given.kp, group);
	}	
	
	
	public final int getDataByteSize() {
		return 8;
	}
	
	public int putBytes(byte[] bArr, int bArrI) {
		IntByteArrayUtil.intToByteArray(bag, bArr, bArrI);
		return bArrI + 8;
	}
	
	public BoFLF(DataInput str, BoFLFGroup group) throws IOException {
		
		linkedGroup = group;
		
		byte kpExists = str.readByte();
		if ( kpExists != -1 ) {
			new KeyPoint(str);
		}		
		
		bag = str.readInt();

	}
	
	public BoFLF(ByteBuffer in, BoFLFGroup linkedGroup) {
		this.linkedGroup = linkedGroup;
		byte kpExists = in.get();
		if ( kpExists != -1 ) {
			kp = new KeyPoint(in);
		}
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
	public int compareTo(ALocalFeature o) {
		BoFLF given = (BoFLF) o;
		int tComp = Integer.compare(bag, given.bag);
		if (tComp != 0) return tComp;
		tComp = kp.compareTo(given.kp);
		if (tComp != 0) return tComp;
		return 0;
	}


	public String toString() {
		return bag + ", " + kp;
	}

}
