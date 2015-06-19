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
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;
import it.cnr.isti.vir.util.math.Mean;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class SIFT extends ALocalFeature<SIFTGroup> implements IUByteValues {

	public static final int VLEN = 128;
    private final static float sqrt2 = (float) Math.sqrt(2.0);
	private static final double maxSQRDistValue = 255 * 255 * 128;
	
	public final byte[] values;     			/* Vector of descriptor values (- 128 for storing in java byte) */
	
	public Class getGroupClass() { return SIFTGroup.class; };
	
	@Override
	public final int getLength() {
		return VLEN;
	}
	
	public SIFT(DataInput str ) throws IOException {
		super(str);

		values = new byte[VLEN];
		str.readFully(values);
	}
	
	public SIFT(ByteBuffer src ) throws IOException {
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
	
	public byte[] getValues() {
		return values;
	}	
	
	private SIFT(byte[] values) {
		super((KeyPoint) null, null);
		this.values = values;
	}

	public SIFT(KeyPoint kp, byte[] values) {
		this(kp, values, null);
	}
	
	public SIFT(KeyPoint kp, byte[] values, SIFTGroup group) {
		this.kp = kp;
		this.values = values;
		this.linkedGroup = group;
	}
	
	
	@Override
	public int compareTo(ALocalFeature<SIFTGroup> given) {
		if ( this == given ) return 0;
		if ( this.kp != given.kp ) {
			if ( kp == null ) return -1;
			if ( given.kp == null ) return 1;
			int tComp = this.kp.compareTo( given.kp);	
			if ( tComp != 0 ) return tComp;
		}
		for ( int i=0; i<values.length; i++ ) {
			int tComp = Byte.compare(values[i], ((SIFT)given).values[i]);
			if ( tComp != 0 ) return tComp;
		}
		return 0;
	}

	public static final double getDistance_Norm(SIFT s1, SIFT s2 ) {
		return Math.sqrt(getL2SquaredDistance(s1,s2)/maxSQRDistValue);
	}
	
	public static final double getDistance_Norm(SIFT s1, SIFT s2, double maxDist ) {
		double distMax = maxDist*maxDist;
		return Math.sqrt(getL2SquaredDistance(s1,s2,(int) Math.ceil(distMax*maxSQRDistValue))/maxSQRDistValue);
	}
	
	public static final double getDistanceSqr_Norm(SIFT s1, SIFT s2 ) {
		return getL2SquaredDistance(s1,s2)/maxSQRDistValue;
	}
	
	public static final double getDistanceSqr_Norm(SIFT s1, SIFT s2, double maxDist ) {
		return getL2SquaredDistance(s1,s2,(int) Math.ceil(maxDist*maxSQRDistValue))/maxSQRDistValue;
	}
	
	public static final int getL2SquaredDistance(SIFT s1, SIFT s2) {
		return L2.getSquared(s1.values, s2.values);
	}
	

	public static final int getL2SquaredDistance(SIFT s1, SIFT s2, int maxDist ) {
		return L2.getSquared(s1.values, s2.values, maxDist);
	}
	
//	private static final int step=32;
//	public static final int getSquaredDistance(SIFT s1, SIFT s2, int maxDist ) {
//		int dist = 0;
//	    
//		int dif = 0;
//		int i=0;
//		while (i<127) {			
//			
//			for (int j=0; j<step; j++, i++) {
//				//dif = ((int) s1.values[i] & 0xFF) - ((int) s2.values[i] & 0xFF);
//				dif = (int) s1.values[i] - (int) s2.values[i];
//		    	dist += dif * dif;				
//			}
//			
//			// to check only step after step
//			if ( dist > maxDist ) return -dist;	
//		}
//		return dist;
//	}




    /*
	public Point2D.Double getPoint2D() {
		return new Point2D.Double( data[rowIndex], data[colIndex] );
	}*/


	public static SIFT getMean(Collection<SIFT> coll) {
		if ( coll.size() == 0 ) return null;
		byte[][] bytes = new byte[coll.size()][];
		int i=0;
		for ( Iterator<SIFT> it = coll.iterator(); it.hasNext(); ) {
			bytes[i++] = it.next().values;
		}
				
		return new SIFT(Mean.getMean(bytes));		
	}

	public String toString() {
		String tStr = super.toString();
		tStr+= "[";
		for (int i=0; i<values.length; i++ ) {
			tStr += values[i] + " ";
		}
		tStr+="]\n";
		return tStr;
	}

	/*
	 *  returns true if all values are zero
	 */
	public boolean hasZeroValues() {
		for ( int i=0; i<VLEN; i++) {
			if ( values[i] != 0 ) return false;
		}
		return true;
	}


	/*
	public SIFT getUnlinked() {
		return new SIFT(values);
	}*/


	// LEGACY
	
	public static SIFT read_old(ByteBuffer src ) throws IOException {
		return read_old(src, null);
	}
	
	public static SIFT read_old(ByteBuffer src, SIFTGroup group) throws IOException {

		float x = src.getFloat();
		src.getFloat();
		float y = src.getFloat();
		src.getFloat();
		float scale = src.getFloat();
		src.getFloat();
		float ori = src.getFloat();
		src.getFloat();
		
		KeyPoint kp = new KeyPoint(x, y, ori, scale);
				
		byte[] values = new byte[VLEN];			
		for ( int iValues=0; iValues<VLEN; iValues++ ) {
			values[iValues] = src.get();
		}
		
		return new SIFT(kp, values, group);
	}
	
	public static SIFT[] getSIFT_old(DataInput in, int nSIFT, SIFTGroup group) throws IOException {
		byte[] byteArr = new byte[nSIFT*160];
		in.readFully(byteArr);
		return getSIFT_old(byteArr, group);
	}	
	
	public static final SIFT[] getSIFT_old(byte[] byteArr, SIFTGroup group ){
		int size = byteArr.length / 160;
		SIFT[] res = new SIFT[size];
		for (int i=0, currIndex = 0; i<size; i++, currIndex += 160) {
			
			float[] tData = FloatByteArrayUtil.get(byteArr, currIndex, 4); 	// TO DO !!!
						
			byte[] tValues = new byte[VLEN];			
			for ( int iValues=0; iValues<VLEN; iValues++ ) {
				tValues[iValues]=byteArr[currIndex+4*8+iValues];
			}
			
			KeyPoint kp = new KeyPoint(tData[0], tData[1], tData[3], tData[2]);
			res[i] = new SIFT(kp, tValues, group);
		}
		return res;
	}
	

	/* .. each keypoint is
	   specified by 4 floating point numbers giving subpixel row and
	   column location, scale, and orientation (in radians from -PI to
	   PI).  Then the descriptor vector for each keypoint is given as a
	   list of integers in range [0,255].
	*/
	public SIFT(BufferedReader br) throws IOException {
		values = new byte[128];
		
		// First line parsing
	    String[] temp = br.readLine().split("(\\s)+");
	    kp = new KeyPoint(
	    		Float.parseFloat(temp[0]),	// x
	    		Float.parseFloat(temp[1]),  // y
	    		Float.parseFloat(temp[3]),  // scale
	    		Float.parseFloat(temp[2])); // ori

		
		int count = 0;
		// in case values are on the same line of row, col, scale, ori...
		for ( int i=4; i<temp.length; i++ ) {
			values[count++] = (byte)( Integer.parseInt(temp[i]) - 128 ); // UNSIGNED BYTES
		}
			    
		// TO DO! Values must end with a endline 
	    while ( count<values.length ) {
	    	temp = br.readLine().split("\\s+");
			for (int i = 0; i < temp.length; i++) {
				if ( !temp[i].equals("") ) values[count++] = (byte) ( Integer.parseInt(temp[i]) - 128 ); // UNSIGNED BYTES
			}
	    }
	}

	public float getFloat(int index) {
		return (values[index] + 128) / 255.0f;
	}
	
//	public static float getFloatValue(byte v) {
//		return (v + 128) / 255.0f;
//	}
	
	public static double getL2NormFactor() {
		return ( 255 * Math.sqrt(VLEN) * 2 );
	}


}
