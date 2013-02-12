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
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.util.FloatByteArrayUtil;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class SIFT implements ILocalFeature {

	
	private static double max = Double.MIN_VALUE;
	private static double min = Double.MAX_VALUE;
	
	private static final double maxSQRDistValue = 255 * 255 * 128;
	
	// the greater the scale, the more the importance
	
	/* 	from experiments:
	 *  SCALE: 	0.71 	and 	88 	were found
	 *  ORI: 	1.29	and			were found
	 *  ROW and COL are in pixels
	 */	
	
//	final float row, col;             	/* Subpixel location of keypoint. */
//	final float scale, ori;           	/* Scale and orientation (range [-PI,PI]) */

	static final int dataLen = 4;  
	static final int vLen = 128;
	static final int byteSize = vLen + dataLen * 8; 

	
	final float[] data;
	public final static int colIndex 	= 0;
	public final static int rowIndex 	= 1;
	public final static int scaleIndex  = 2;
	public final static int oriIndex 	= 3;
	
	final byte[] values;     			/* Vector of descriptor values (- 128 for storing in java byte) */

    private float[] xy;
    private float[] normxy;

    private final static float sqrt2 = (float) Math.sqrt(2.0);
//	static final byte version = 0;
    
	private SIFTGroup linkedGroup;
	
	public byte[] getValues() {
		return values;
	}
	
	
	public SIFTGroup getLinkedGroup() {
		return linkedGroup;
	}
	
	public SIFT getUnlinked() {
		return new SIFT(values);
	}
	
	private SIFT(byte[] values) {
		this.values = values;
		data = new float[4];
		data[rowIndex]	 = Float.MIN_VALUE;
		data[colIndex]	 = Float.MIN_VALUE;
		data[scaleIndex] = Float.MIN_VALUE;
		data[oriIndex]	 = Float.MIN_VALUE;
		linkedGroup = null;
	}

	public SIFT(DataInput str ) throws IOException {
		this(str, null);
	}

	public SIFT(ByteBuffer src ) throws IOException {
		this(src, null);
	}
	
	public SIFT(ByteBuffer src, SIFTGroup group) throws IOException {
		
		linkedGroup = group;
		
		data = new float[dataLen];
		double temp = 0;
		for ( int i=0; i<dataLen; i++ ) {
			data[i] = src.getFloat();
			temp = src.getFloat();			
		}
				
		values = new byte[vLen];			
		for ( int iValues=0; iValues<vLen; iValues++ ) {
			values[iValues] = src.get();
		}

	}
	
	public SIFT(DataInput str, SIFTGroup group) throws IOException {
		
		linkedGroup = group;
		
		byte[] byteArr = new byte[byteSize];
		str.readFully(byteArr);
		data = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, 0, dataLen);
		
		values = new byte[vLen];			
		for ( int iValues=0; iValues<vLen; iValues++ ) {
			values[iValues]=byteArr[dataLen*8+iValues];
		}

	}
	
	public SIFT(float[] data, byte[] values, SIFTGroup group) {
		this.data = data;
		this.values = values;
		this.linkedGroup = group;
	}
	
	public static SIFT[] getSIFT(DataInput in, int nSIFT, SIFTGroup group) throws IOException {
		byte[] byteArr = new byte[nSIFT*byteSize];
		in.readFully(byteArr);
		return getSIFT(byteArr, group);
	}
	
	public static final SIFT[] getSIFT(byte[] byteArr, SIFTGroup group ){
		int size = byteArr.length / byteSize;
		SIFT[] res = new SIFT[size];
		for (int i=0, currIndex = 0; i<size; i++, currIndex += byteSize) {
			
			float[] tData = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, currIndex, dataLen); 	// TO DO !!!
						
			byte[] tValues = new byte[vLen];			
			for ( int iValues=0; iValues<vLen; iValues++ ) {
				tValues[iValues]=byteArr[currIndex+dataLen*8+iValues];
			}
			
			res[i] = new SIFT(tData, tValues, group);
		}
		return res;
	}
		
	public final static byte[] getBytes(SIFT[] sift) {
		byte[] res = new byte[sift.length*byteSize];
		for (int i=0, currIndex = 0; i<sift.length; i++, currIndex += byteSize) {
			
			FloatByteArrayUtil.floatArrayToByteArray(sift[i].data, res, currIndex);
			
			for ( int iV=0; iV<vLen; iV++ ) {
				res[currIndex+dataLen*8+iV]=sift[i].values[iV];
			}
			
		}
		return res;
	}
	
	public final void writeData(DataOutput str) throws IOException {
		
		byte[] res = new byte[byteSize];
		FloatByteArrayUtil.floatArrayToByteArray(data, res, 0);
		
		for ( int iV=0; iV<vLen; iV++ ) {
			res[dataLen*8+iV]=values[iV];
		}
		
		str.write(res);
		
	}
	
	public final Class getGroupClass() { return SIFTGroup.class; };
	
	/* .. each keypoint is
	   specified by 4 floating point numbers giving subpixel row and
	   column location, scale, and orientation (in radians from -PI to
	   PI).  Then the descriptor vector for each keypoint is given as a
	   list of integers in range [0,255].
	*/
	public SIFT(BufferedReader br, SIFTGroup group) throws IOException {
		linkedGroup = group;
		values = new byte[128];
		
		// First line parsing
	    String[] temp = br.readLine().split("(\\s)+");
	    
	    data = new float[4];
		data[0] = Float.parseFloat(temp[0]);
		data[1] = Float.parseFloat(temp[1]);
		data[2] = Float.parseFloat(temp[2]);
		data[3] = Float.parseFloat(temp[3]);	    

//		if ( scale > max ) { System.out.println("Scale max: " + scale); max = scale; }
//		if ( scale < min ) { System.out.println("Scale min: " + scale); min = scale; }
		
//		if ( row > max ) { System.out.println("row max: " + max); max = row; }
//		if ( row < min ) { System.out.println("row min: " + min); min = row; }
		
		//System.out.print (" " + scale);
		int count = 0;
		
		// in case values are on the same line of row, col, scale, ori...
		for ( int i=4; i<temp.length; i++ ) {
			values[count++] = (byte)( Integer.parseInt(temp[i]) - 128 ); // UNSIGNED BYTES
		}
			    
		// TO DO! Values must end with a endline 
	    while ( count<values.length ) {
	    	temp = br.readLine().split("\\s+");
			for (int i = 0; i < temp.length; i++) {
				//System.out.print(" " + temp[i]);
				if ( !temp[i].equals("") ) values[count++] = (byte) ( Integer.parseInt(temp[i]) - 128 ); // UNSIGNED BYTES
			}
			//System.out.print("\n");
	    }
	}

	public static final double getDistance_Norm(SIFT s1, SIFT s2 ) {
		return Math.sqrt(getSquaredDistance(s1,s2)/maxSQRDistValue);
	}
	
	public static final double getDistance_Norm(SIFT s1, SIFT s2, double maxDist ) {
		double distMax = maxDist*maxDist;
		return Math.sqrt(getSquaredDistance(s1,s2,(int) Math.ceil(distMax*maxSQRDistValue))/maxSQRDistValue);
	}
	
	public static final double getDistanceSqr_Norm(SIFT s1, SIFT s2 ) {
		return getSquaredDistance(s1,s2)/maxSQRDistValue;
	}
	
	public static final double getDistanceSqr_Norm(SIFT s1, SIFT s2, double maxDist ) {
		return getSquaredDistance(s1,s2,(int) Math.ceil(maxDist*maxSQRDistValue))/maxSQRDistValue;
	}
	
	public static final int getSquaredDistance(SIFT s1, SIFT s2) {
		int dist = 0;	    
		int dif = 0;
		
	    for (int i = 0; i < 128; i++) {
	    	dif = (int) s1.values[i] - (int) s2.values[i];
	    	dist += dif * dif;
	    }		
		return dist;
	}
	

	public static final int getSquaredDistance(SIFT s1, SIFT s2, int maxDist ) {
		int dist = 0;	    
		int dif = 0;
	    for (int i = 0; i < 128; i++) {
	    	dif = (int) s1.values[i] - (int) s2.values[i];
	    	dist += dif * dif;
	    	if ( dist > maxDist ) return -dist;
	    }		
		return dist;
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
	
	@Override
	public int compareTo(IFeature arg0) {
		// the bigger the scale, the greater the importance
		SIFT d1 = this;
		SIFT d2 = (SIFT) arg0;
		// this result in reverse order when sorting (from grater to smaller)
		return Double.compare(d2.data[scaleIndex], d1.data[scaleIndex]);

	}

	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		SIFT givenF = (SIFT) obj;
		if ( values.length != givenF.values.length ) return false;
		for ( int i=0; i<values.length; i++ ) {
			if ( values[i] != givenF.values[i] ) return false;
		}
	
		// not used in distance
		for ( int i=0; i<data.length; i++) {
			if ( data[i] != givenF.data[i] ) return false;
		}
		return true;
	}


	@Override
	public AbstractLabel getLabel() {
		return ((ILabeled) linkedGroup).getLabel();
	}

	@Override
	public final float getScale() {
		return data[scaleIndex];
	}

        @Override
        public final float getNormScale() {
            return linkedGroup.getNormScale() * getScale();
        }

	@Override
	public final float getOrientation() {
		return data[oriIndex];
	}

    @Override
	public float[] getXY() {
		if (xy == null) {
			xy = new float[] { data[rowIndex], data[colIndex] };
		}
		return xy;
	}

    @Override
	public synchronized float[] getNormXY() {
		if (normxy == null) {
			float[] tnormxy = new float[2];
			float[] mean = linkedGroup.getMeanXY();
			float scale = linkedGroup.getNormScale();
			tnormxy[0] = (data[rowIndex] - mean[0]) * scale;
			tnormxy[1] = (data[colIndex] - mean[1]) * scale;
			normxy = tnormxy;
		}
		return normxy;
	}

	public Point2D.Double getPoint2D() {
                return new Point2D.Double( data[rowIndex], data[colIndex] );
	}


	public static SIFT getMean(Collection<SIFT> coll) {
		
		if ( coll.size() == 0 ) return null;
		
		long[] tempVec = new long[vLen];
		
		int laplaceTemp = 0;
		
		for ( Iterator<SIFT> it = coll.iterator(); it.hasNext(); ) {
			byte[] currVec = it.next().values;

			for ( int i=0; i<vLen; i++) {
				tempVec[i] += currVec[i];
			}
		}
		
		byte[] newVec = new byte[vLen];	
		for ( int i=0; i<vLen; i++) {
			newVec[i] = (byte) ( Math.round( tempVec[i] / (double) coll.size() ) );
		}
				
		return new SIFT( newVec );
		
	}

//	public int hashCode() {
//		  assert false : "hashCode not designed";
//		  return 42; // any arbitrary constant will do 
//		  }

	public String toString() {
		String tStr = "";
		for (int i=0; i<values.length; i++ ) {
			tStr += " " + values[i];
		}
		tStr+="\n";
		return tStr;
	}

	public boolean hasZeroValues() {
		for ( int i=0; i<vLen; i++) {
			if ( values[i] != 0 ) return false;
		}
		return true;
	}

	
}
