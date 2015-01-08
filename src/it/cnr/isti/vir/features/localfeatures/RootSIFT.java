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

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.IUByteValues;
import it.cnr.isti.vir.util.math.Mean;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class RootSIFT extends ALocalFeature<RootSIFTGroup> implements IUByteValues {

	static final int VLEN = 128;
    private final static float sqrt2 = (float) Math.sqrt(2.0);
	private static final double maxSQRDistValue = 255 * 255 * 128;
	
	final byte[] values;     			/* Vector of descriptor values (- 128 for storing in java byte) */
	
	public Class getGroupClass() { return RootSIFTGroup.class; };
	
	@Override
	public final int getLength() {
		return VLEN;
	}
	
	@Override
	public byte[] getValues() {
		return values;
	}	
	
	public RootSIFT(SIFT sift, RootSIFTGroup givenLinkedGroup) {
		linkedGroup = givenLinkedGroup;
		kp = sift.kp;
		values = RootSIFT.getRootSIFTValues( sift.values );
	}
		
	public RootSIFT(DataInput str ) throws IOException {
		super(str);

		values = new byte[VLEN];
		str.readFully(values);
	}
	
	public RootSIFT(ByteBuffer src ) throws IOException {
		super(src);

		values = new byte[VLEN];
		src.get(values);
	}
	
	public int getDataByteSize() {
		return VLEN;
	}
	
	public int putDescriptor(byte[] bArr, int bArrI) {
		System.arraycopy(values, 0, bArr, bArrI, VLEN);
		return bArrI + VLEN;
	}

	
	private RootSIFT(byte[] values) {
		super((KeyPoint) null, null);
		this.values = values;
	}

	
	
	public RootSIFT(KeyPoint kp, byte[] values, RootSIFTGroup group) {
		this.kp = kp;
		this.values = values;
		this.linkedGroup = group;
	}
	
	
	@Override
	public int compareTo(ALocalFeature<RootSIFTGroup> given) {
		if ( this == given ) return 0;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		for ( int i=0; i<values.length; i++ ) {
			int tComp = Byte.compare(values[i], ((RootSIFT)given).values[i]);
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}
	
	/**
	 * This is the core of the RootSIFT 
	 * 
	 * @param value
	 * @param sum
	 * @return
	 */
	public final static int getRootSIFTValue( int value, int sum ) {
		return (int)
			(
				Math.sqrt( (float) value / sum ) 	// between 0 and 1
				* 255						// to have between 0 and 255
			);
	}
	
	public final static byte[] getRootSIFTValues( byte[] siftValues ) {
		int sum = 0;
		for ( int i=0; i<siftValues.length; i++ ) {
			sum += siftValues[i];
		}
		sum += 128*128;
		
		byte[] values = new byte[128];
		for ( int i=0; i<siftValues.length; i++ ) {
			values[i] = (byte) (getRootSIFTValue( siftValues[i]+128, sum)-128);
		}
		
		return values;
		
	}
	
	public RootSIFT(BufferedReader br, RootSIFTGroup group) throws IOException {
		SIFT sift = new SIFT(br);			    
		this.kp = sift.kp;
		this.linkedGroup = group;
		this.values = getRootSIFTValues(sift.values);
	}
	
	public static RootSIFT getMean(Collection<RootSIFT> coll) {
		if ( coll.size() == 0 ) return null;
		byte[][] bytes = new byte[coll.size()][];
		int i=0;
		for ( Iterator<RootSIFT> it = coll.iterator(); it.hasNext(); ) {
			bytes[i++] = it.next().values;
		}
				
		return new RootSIFT(Mean.getMean(bytes));		
	}
	
	public static final double getL2SQDistance_Norm(RootSIFT s1, RootSIFT s2 ) {
		return getL2SQDistance(s1,s2)/maxSQRDistValue;
	}
	
	public static final double getL2SQDistance_Norm(RootSIFT s1, RootSIFT s2, double maxDist ) {
		return getL2SQDistance(s1,s2,(int) Math.ceil(maxDist*maxSQRDistValue))/maxSQRDistValue;
	}
	
	public static final double getL2Distance(RootSIFT s1, RootSIFT s2) {
		return Math.sqrt( getL2SQDistance_Norm(s1, s2) );
	}

	public static final double getL2Distance(RootSIFT s1, RootSIFT s2, int maxDist ) {
		return Math.sqrt( getL2SQDistance_Norm(s1, s2, maxDist) );
	}
	
	public static final int getL2SQDistance(RootSIFT s1, RootSIFT s2) {
		return L2.getSquared(s1.values, s2.values);
	}

	public static final int getL2SQDistance(RootSIFT s1, RootSIFT s2, int maxDist ) {
		return L2.getSquared(s1.values, s2.values, maxDist);
	}

	
}
