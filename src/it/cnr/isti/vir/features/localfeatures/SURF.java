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

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Iterator;

public class SURF implements ILocalFeature {
	
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
        private final float[] xy;
        private float[] normxy;

	private static final int xi = 0;
	private static final int yi = 1;
	private static final int scalei = 2;
	private static final int strengthi = 3;
	private static final int orii = 4;
	private static final int laplacei = 5;
	private static final int iVecStart = 6;
	private static final int iVecLength = 64;
	private static final int iVecEnd  = iVecLength + iVecStart;
	public static final int dataSize = iVecEnd;

    // x, y value of the interest point
//	private final float x, y;
//	public final float getX() { return data[xi]; };
//	public final float getY() { return data[yi]; };

    @Override
	public float[] getXY() {
		return xy;
	}



    @Override
	public float[] getNormXY() {
		if (normxy == null) {
			float[] tnormxy = new float[2];
			float[] mean = linkedGroup.getMeanXY();
			float scale = linkedGroup.getNormScale();
			tnormxy[0] = (xy[0] - mean[0]) * scale;
			tnormxy[1] = (xy[1] - mean[1]) * scale;
			normxy = tnormxy;
		}
		
		return normxy;
	}

    // detected scale
//	private final float scale;
	public final float getScale() { return data[scalei]; };


        public final float getNormScale() {
            return linkedGroup.getNormScale() * getScale();
        }

    // strength of the interest point
//	private final float strength;
	public final float getStrength() { return data[strengthi]; };
    // orientation
//	private final float ori;
	public final float getOrientation() { return data[orii]; };
	
    // sign of Laplacian
	//private final int laplace; // stored in data[5]
	
    // descriptor
//	private final float[] ivec;�
	// ivec is now a vector of float from data[6] to the end i.e. ivecLength+6;

	
//	static final byte version = 1;
	
	//static final double maxSQDist = Double.MAX_VALUE;
	
	private final SURFGroup linkedGroup;
	
	@Override
	public AbstractLabel getLabel() {
		return ((ILabeled) linkedGroup).getLabel();
	}
	
//	public SURF(int laplace, float[] ivec) {
//	    this.laplace = laplace;
//	    this.ivec = ivec;
//	    
//	    // TO DO !!!
//		this.x = Float.MIN_VALUE;
//		this.y = Float.MIN_VALUE;
//	    this.scale = Float.MIN_VALUE;
//	    this.strength = Float.MIN_VALUE;
//	    this.ori = Float.MIN_VALUE;
//	    
//	    linkedGroup = null;
//	}
	
	
	public SURFGroup getLinkedGroup() {
		return linkedGroup;
	}
	
	public SURF(DataInput str ) throws IOException {
		this(str, null);
	}
	
	public SURF(ByteBuffer str ) throws IOException {
		this(str, null);
	}
	
	public SURF(ByteBuffer str, SURFGroup group) throws IOException {
		linkedGroup = group;
		
//		byte version = str.readByte();

		data = new float[dataSize];
		for ( int i=0; i<dataSize; i++ ) {
			data[i] = str.getFloat();
		}
        xy = new float[] { data[0], data[1] };
	}
	
	public SURF(DataInput str, SURFGroup group) throws IOException {
		linkedGroup = group;
		
//		byte version = str.readByte();

		data = new float[dataSize];
		byte[] byteArray = new byte[dataSize*4];
		FloatBuffer inFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
		str.readFully(byteArray);
		inFloatBuffer.get(data, 0, dataSize);
        xy = new float[] { data[0], data[1] };
	}
	
	public final void writeData(DataOutput str) throws IOException {
//		str.writeByte(version);
//		for (int i=0; i<data.length; i++) str.writeFloat(data[i]);
		
		byte[] byteArray = new byte[dataSize*4];
		FloatBuffer outFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
		outFloatBuffer.put(data, 0, data.length);
		str.write(byteArray);
		
//		str.writeFloat(x);
//		str.writeFloat(y);
//		str.writeFloat(scale);
//		str.writeFloat(ori);
//		str.writeFloat(strength);
//		str.writeInt(laplace);
//		
//		str.writeInt(ivec.length);
//		for (int i=0; i<ivec.length; i++) str.writeFloat(ivec[i]);
	}
    
    public final Class getGroupClass() { return SURFGroup.class; };
   
///////////// OLD VERSION
  
//	public SURF(BufferedReader br, SURFGroup group) throws IOException
//	{
//		linkedGroup = group;
//		
//		// Load the interest points in Mikolajczyk's format
//		String[] temp = br.readLine().split("(\\s)+");
//		assert(temp.length == ivecLength+6);
//		
//		
//		x = (float) Double.parseDouble(temp[0]);
//	    y = (float) Double.parseDouble(temp[1]);
//	    
//		// circular regions with diameter 5 x scale
//	    double a = Double.parseDouble(temp[2]);
//	    double b = Double.parseDouble(temp[3]);
//	    double c = Double.parseDouble(temp[4]);
//	    
//		float det = (float) ( Math.sqrt((a-c)*(a-c) + 4.0*b*b));
//		float e1 =  (float) ( 0.5*(a+c + det));
//		float e2 =  (float) ( 0.5*(a+c - det));
//		float l1 =  (float) ( (1.0/Math.sqrt(e1)));
//		float l2 =  (float) ( (1.0/Math.sqrt(e2)));
//		float sc =  (float) ( Math.sqrt( l1*l2 ));
//	    
//	    this.scale = (float) (sc/2.5);
//	    
//	    this.laplace = Integer.parseInt(temp[5]);
//	    
//	    ivec = new float[ivecLength];
//	    		
//		int count = 0;
//		
//		// values are on the same line of row, col, scale, ori...
//		for ( int i=6; i<temp.length; i++ ) {
//			ivec[count++] = Float.parseFloat(temp[i]);
//		}
//	}
    
	public SURF(BufferedReader br, SURFGroup group) throws IOException
	{
		linkedGroup = group;
		
		// Load the interest points in Mikolajczyk's format
		String[] temp = br.readLine().split("(\\s)+");
//		assert(temp.length == ivecLength+6);
		
		data = new float[dataSize];
		
		data[xi] = Float.parseFloat(temp[0]);
		data[yi] = Float.parseFloat(temp[1]);
	    
//	    float tScale = Float.parseFloat(temp[2]);
//	    scale 		= tScale*tScale; // it is not clear why!!!!!
		data[scalei]		= Float.parseFloat(temp[2]);
		data[strengthi] 	= Float.parseFloat(temp[3]);
		data[orii]  		= Float.parseFloat(temp[4]);
	    
		data[laplacei]  = Integer.parseInt(temp[5]);
	    
//	    ivec = new float[ivecLength];
	    		
		int count = iVecStart;
		
		// values are on the same line of row, col, scale, ori...
		for ( int i=6; i<temp.length; i++ ) {
			data[count++] = Float.parseFloat(temp[i]);
		}

                xy = new float[] { data[0], data[1] };
	}
	
	public final int getLaplace() {
		if ( data[laplacei]==1 ) return 1;
		if ( data[laplacei]==-1) return -1;
		System.err.println("Laplace Error. Value: " + data[laplacei] );
		return 0;
	}
	
	public SURF(float[] tempVec) {
		this.data = tempVec;
		this.linkedGroup=null;

                xy = new float[] { data[0], data[1] };
	}
	
	@Override
	public ILocalFeature getUnlinked() {
		return new SURF(this.data);
	}

	public SURF(float[] tempVec, SURFGroup linkedGroup) {
		this.data = tempVec;
		this.linkedGroup=linkedGroup;
        xy = new float[] { data[0], data[1] };
	}
	
	
	public static final double getDistance_Norm_laplace(SURF s1, SURF s2 ) {
		
		return Math.sqrt(getSquaredDistance_laplace(s1,s2)/maxSQRDistValue);
	}
	
	public static final double getDistance_Norm_laplace(SURF s1, SURF s2, double maxDist ) {
		return Math.sqrt(getSquaredDistance_laplace(s1,s2, maxDist)/maxSQRDistValue);
	}
	
	public static final double getSquaredDistance_laplace(SURF s1, SURF s2, double maxDist) {
		if ( s1.data[laplacei] != s2.data[laplacei] ) return maxSQRDistValue;
		double dist = 0;
	    for (int i = iVecStart; i < iVecEnd; i++) {
	    	double dif = s1.data[i] - s2.data[i]; dist += dif * dif;
	    	if ( dist > maxDist ) return -dist;
	    }    	
		return dist;
	}
	

	public static final double getSquaredDistance_laplace(SURF s1, SURF s2) {
		
		if ( s1.data[laplacei] != s2.data[laplacei] ) return maxSQRDistValue;
		
		double dist = 0;
	    double dif = 0;
	    
	    for (int i = iVecStart; i < iVecEnd; i++) {
	    	dif = s1.data[i] - s2.data[i];
	    	dist += dif * dif;
	    }
		
		return dist;
	}
	
	@Override
	public int compareTo(IFeature arg0) {
		SURF d1 = this;
		SURF d2 = (SURF) arg0;
		// Greater Scale First
		return Double.compare(d2.data[scalei], d1.data[scalei]);
	}
	
	
	
	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
////		if ( featureClass.equals(this.getClass()) ) return this;
////		return null;
//		return this;
//	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		SURF givenF = (SURF) obj;
		if ( data.length != givenF.data.length ) return false;
		for ( int i=0; i < dataSize; i++) {
			if ( data[i] != givenF.data[i] ) return false;
		}

		return true;
	}


//	@Override
//	public double getX() {
//		return x;
//	}
//
//	@Override
//	public double getY() {
//		return y;
//	}

        public Point2D.Double getPoint2D() {
                return new Point2D.Double( data[0], data[1] );
	}

	public static SURF getMean_laplace(Collection<SURF> coll) {
		
		if ( coll.size() == 0 ) return null;
		
		float[] tempVec = new float[dataSize];
		
		int laplaceTemp = 0;
		
		for ( Iterator<SURF> it = coll.iterator(); it.hasNext(); ) {
			SURF curr = it.next();
//			float[] scurrData = curr.data;
			laplaceTemp+= (int) curr.data[laplacei];
			for ( int i=iVecStart; i<iVecEnd; i++) {
				tempVec[i] += curr.data[i];
			}
		}
		
		// laplace will be the most frequent -1 or 1 value for laplace
		for ( int i=iVecStart; i<iVecEnd; i++) {
			tempVec[i] = tempVec[i] / (float) coll.size();
		}
		
		if ( laplaceTemp > 0 ) tempVec[laplacei] = 1;
		else tempVec[laplacei] = -1;
//		tempVec[laplacei] = Math.round( (double) laplaceTemp / coll.size());
		
//	    // TO DO !!!
		tempVec[xi] = Float.MIN_VALUE;
		tempVec[yi] = Float.MIN_VALUE;
		tempVec[scalei] = Float.MIN_VALUE;
		tempVec[strengthi] = Float.MIN_VALUE;
		tempVec[orii] = Float.MIN_VALUE;
//	    
//	    linkedGroup = null;
		
		return new SURF(tempVec);
		
	}
	
	protected float[] getData() {
		return data;
	}

	
//	public int hashCode() {
//		  assert false : "hashCode not designed";
//		  return 42; // any arbitrary constant will do 
//		  }

}
