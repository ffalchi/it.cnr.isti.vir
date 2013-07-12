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

import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class SURF extends ALocalFeature<SURFGroup> {
	
	private static final double[][] gauss25  = {
			{	0.02350693969273,0.01849121369071,0.01239503121241,0.00708015417522,0.00344628101733,0.00142945847484,0.00050524879060 },
			{	0.02169964028389,0.01706954162243,0.01144205592615,0.00653580605408,0.00318131834134,0.00131955648461,0.00046640341759 },
			{	0.01706954162243,0.01342737701584,0.00900063997939,0.00514124713667,0.00250251364222,0.00103799989504,0.00036688592278 },
			{	0.01144205592615,0.00900063997939,0.00603330940534,0.00344628101733,0.00167748505986,0.00069579213743,0.00024593098864 },
			{	0.00653580605408,0.00514124713667,0.00344628101733,0.00196854695367,0.00095819467066,0.00039744277546,0.00014047800980 },
			{	0.00318131834134,0.00250251364222,0.00167748505986,0.00095819467066,0.00046640341759,0.00019345616757,0.00006837798818 },
			{	0.00131955648461,0.00103799989504,0.00069579213743,0.00039744277546,0.00019345616757,0.00008024231247,0.00002836202103 }
			};
	
	private static final int step = 8;
	public  static double maxSQRDistValue = 2.0;
	
	private final float[] data;
	private final float strength;
	private final byte laplace; // -1 or 1
    
	/*
	private static final int xi = 0;
	private static final int yi = 1;
	private static final int scalei = 2;
	private static final int orii = 4; */
	
	//private static final int strengthi = 3;
	
	//private static final int laplacei = 5;	
	
	//private static final int iVecStart = 6;
	public static final int VECLENGTH = 64;
	//private static final int iVecEnd  = iVecLength + iVecStart;
	
	public  static final int DATABYTESIZE = VECLENGTH*4+4+1;

	//public final float getStrength() { return data[strengthi]; };
	
	public SURF(float[] data, byte laplace, float strength, KeyPoint kp ) {
		this.data = data;
		this.laplace = laplace;
		this.strength = strength;
		this.kp = kp;
	}
	

	@Override
	public int getDataByteSize() {
		return DATABYTESIZE;
	}

	@Override
	public int putDescriptor(byte[] byteArray, int bArrI) {
		int offSet = bArrI;
		offSet = FloatByteArrayUtil.convToBytes(data, byteArray, offSet);
		byteArray[offSet++] = laplace;
		offSet = FloatByteArrayUtil.convToBytes(strength, byteArray, offSet);
		return offSet;
	}
	
	public SURF(DataInput str ) throws IOException {
		super(str);
		
		byte[] tBytes = new byte[VECLENGTH*4];		
		str.readFully(tBytes);
		data = FloatByteArrayUtil.get(tBytes, 0, VECLENGTH);
		laplace = str.readByte();
		strength = str.readFloat();
	}
	
	public SURF(ByteBuffer in ) throws IOException {
		super(in);
		
		data = new float[VECLENGTH];
		for ( int i=0; i<VECLENGTH; i++) {
			data[i] = in.getFloat();
		}
		laplace = in.get();
		strength = in.getFloat();
	}
	
//	public static SURF read_old(ByteBuffer str) throws IOException {
//		
//		float x = str.getFloat();
//		float y = str.getFloat();
//		float scale = str.getFloat();
//		float strength = str.getFloat();
//		float ori = str.getFloat();
//		byte laplace =  (byte) (int) Math.round(str.getFloat());
//		
//		KeyPoint kp = new KeyPoint(x,y,ori,scale);
//		
//		float[] data = new float[VECLENGTH];
//		for ( int i=0; i<VECLENGTH; i++ ) {
//			data[i] = str.getFloat();
//		}
//		
//		return new SURF(data, laplace, strength, kp );
//	}
//	
//	public static SURF read_old(DataInput str) throws IOException {
//
//		float x = str.readFloat();
//		float y = str.readFloat();
//		float scale = str.readFloat();
//		float strength = str.readFloat();
//		float ori = str.readFloat();
//		byte laplace =  (byte) (int) Math.round(str.readFloat());
//		
//		float[] data = new float[VECLENGTH];
//		byte[] byteArray = new byte[VECLENGTH*4];
//		FloatBuffer inFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
//		str.readFully(byteArray);
//		inFloatBuffer.get(data, 0, VECLENGTH);
//		
//		KeyPoint kp = new KeyPoint(x,y,ori,scale);
//        //xy = new float[] { data[0], data[1] };
//		
//		return new SURF(data, laplace, strength, kp);
//	}

    
    public final Class<SURFGroup> getGroupClass() { return SURFGroup.class; };

    
	public SURF(BufferedReader br, SURFGroup group) throws IOException
	{
		linkedGroup = group;
		
		// Load the interest points in Mikolajczyk's format
		String[] temp = br.readLine().split("(\\s)+");
//		assert(temp.length == ivecLength+6);
		
		data = new float[VECLENGTH];
		
		float x = Float.parseFloat(temp[0]);
		float y = Float.parseFloat(temp[1]);
	    
		float scale		= Float.parseFloat(temp[2]);
		strength 	= Float.parseFloat(temp[3]);
		float ori  		= Float.parseFloat(temp[4]);
	    
		kp = new KeyPoint(x,y,ori,scale);
		
		laplace	  = (byte) Integer.parseInt(temp[5]);
	    
//	    ivec = new float[ivecLength];
	    		
		int count = 0;		
		// values are on the same line of row, col, scale, ori...
		for ( int i=6; i<temp.length; i++ ) {
			data[count++] = Float.parseFloat(temp[i]);
		}

	}
	
//	public final int getLaplace() {
//		if ( data[laplacei]== 1 ) return 1;
//		if ( data[laplacei]==-1) return -1;
//		System.err.println("Laplace Error. Value: " + data[laplacei] );
//		return 0;
//	}
	
	/*
	public SURF(float[] tempVec) {
		this.data = tempVec;
		this.linkedGroup=null;

                xy = new float[] { data[0], data[1] };
	}*/

/*
	public SURF(float[] tempVec, SURFGroup linkedGroup) {
		this.data = tempVec;
		this.linkedGroup=linkedGroup;
	}
	*/
	
	public static final double getDistance_Norm_laplace(SURF s1, SURF s2 ) {
		
		return Math.sqrt(getSquaredDistance_laplace(s1,s2)/maxSQRDistValue);
	}
	
	public static final double getDistance_Norm_laplace(SURF s1, SURF s2, double maxDist ) {
		return Math.sqrt(getSquaredDistance_laplace(s1,s2, maxDist)/maxSQRDistValue);
	}
	
	public static final double getSquaredDistance_laplace(SURF s1, SURF s2, double maxDist) {
		if ( s1.laplace != s2.laplace ) return maxSQRDistValue;
		double dist = 0;
	    for (int i = 0; i < VECLENGTH; i++) {
	    	double dif = s1.data[i] - s2.data[i]; dist += dif * dif;
	    	if ( dist > maxDist ) return -dist;
	    }    	
		return dist;
	}
	

	public static final double getSquaredDistance_laplace(SURF s1, SURF s2) {
		
		if ( s1.laplace != s2.laplace ) return maxSQRDistValue;
		
		double dist = 0;
	    double dif = 0;
	    
	    for (int i = 0; i < VECLENGTH; i++) {
	    	dif = s1.data[i] - s2.data[i];
	    	dist += dif * dif;
	    }
		
		return dist;
	}
	
	@Override
	public int compareTo(ALocalFeature<SURFGroup> obj) {
		
		if ( this == obj ) return 0;
		int tComp;
		if ( this.kp != obj.kp ) {
			if ( kp == null ) return -1;
			if ( obj.kp == null ) return 1;
			tComp = this.kp.compareTo( obj.kp);	
			if ( tComp != 0 ) return tComp;
		}
		SURF given = (SURF) obj;
		if ( (tComp=Byte.compare(laplace, given.laplace))!= 0 ) return tComp;
		for ( int i=0; i<data.length; i++ ) {
			tComp = Float.compare(data[i], given.data[i]);
			if ( tComp != 0 ) return tComp;
		}
		if ( (tComp = Float.compare(strength, given.strength)) != 0 ) return tComp;
		return 0;
	}
	
	public static SURF getMean_laplace(Collection<SURF> coll) {
		
		if ( coll.size() == 0 ) return null;
		
		float[] tempVec = new float[VECLENGTH];
		
		int laplaceTemp = 0;
		
		for ( Iterator<SURF> it = coll.iterator(); it.hasNext(); ) {
			SURF curr = it.next();
			laplaceTemp+= (int) curr.laplace;
			for ( int i=0; i<VECLENGTH; i++) {
				tempVec[i] += curr.data[i];
			}
		}
		
		// laplace will be the most frequent -1 or 1 value for laplace
		for ( int i=0; i<VECLENGTH; i++) {
			laplaceTemp = Math.round( (float) tempVec[i] / coll.size() );
		}
		
		if ( laplaceTemp > 0 ) laplaceTemp = 1;
		else laplaceTemp = -1;
		
		return new SURF(tempVec, (byte) laplaceTemp, Float.MIN_VALUE, null);
		
	}
	
	protected float[] getData() {
		return data;
	}


	
//	public int hashCode() {
//		  assert false : "hashCode not designed";
//		  return 42; // any arbitrary constant will do 
//		  }

}
