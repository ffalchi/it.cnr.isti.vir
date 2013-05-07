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

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VLAD implements IFeature {
 
	public IFeaturesCollector linkedFC;
	float[] values;
	
	public int size() {
		return values.length;
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt( values.length);
		for ( int i=0; i<values.length; i++ )
			out.writeFloat(values[i]);		
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		buff.putInt((short) values.length);
		for ( int i=0; i<values.length; i++ )
			buff.putFloat(values[i]);
	}
	
    public VLAD(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public VLAD(ByteBuffer in, IFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		values = new float[size];
		for ( int i=0; i<values.length; i++ ) {
			values[i] = in.getFloat();
		}
		linkedFC = fc;
	}
	
	public VLAD(float[] values) {
		this.values = values;
	}

	public VLAD(DataInput in ) throws Exception {
		this(in, null);
	}
	public VLAD(DataInput in, IFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

//		if ( size == 0 ) return;
		
		values = new float[size];
		for ( int i=0; i<values.length; i++ ) {
			values[i] = in.readFloat();
		}

	}
	
	
    public static final  VLAD getVLAD(SIFTGroup features, LFWords<SIFT> fWords) {
        SIFT[] refs = fWords.getFeatures();
        SIFT[] lf  = features.getLocalFeatures();
        
        int size = refs.length * 128;
        int[] intValues = new int[size];
        for ( int iLF=0; iLF<lf.length; iLF++ ) {
        	byte[] curr = lf[iLF].values;
        	int iW = fWords.getNNIndex(lf[iLF]);
        	int start = iW * 128;
        	int end = start + 128;
        	byte[] ref = refs[iW].values;
        	
        	int j=0;
        	for ( int i=start; i<end; i++, j++ ) {
        		intValues[i] += curr[j] - ref[j];
        	}
        }
 
        
        // Power Normalization 0.5
        float[] values = new float[size];
        //float a = 0.5F;
        for (int i=0; i<size; i++) {        	
        	//values[i] = intValues[i];
        	if ( intValues[i] == 0 ) values[i] = 0.0F;
        	else if ( intValues[i] > 0 )
        		values[i] =   (float) Math.sqrt((double)intValues[i]);
        		//values[i] =   (float) Math.pow((double)intValues[i], a);
        	else 
        		values[i] = - (float) Math.sqrt((double) -intValues[i]);
        		//values[i] = - (float) Math.pow((double) -intValues[i], a);
        }
        
        // L2 Normalization
        double sum2 = 0;
        for (int i=0; i<size; i++) {
        	sum2 += (values[i]*values[i]);
        }
        if ( sum2 > 0.0 ) {
	        double sqrtsum2 = Math.sqrt(sum2);    
	        for (int i=0; i<size; i++) {
	        	values[i] = (float) (values[i] / sqrtsum2 );	        	
	        }        
        }
        
        return new VLAD(values);
    }
    
	public static final VLAD getVLAD(ALocalFeaturesGroup group, LFWords words) {
		if ( group instanceof SIFTGroup ) {
			return getVLAD((SIFTGroup) group, words);
		}
		return null;
	}
	
	

	public static final double getDistance(VLAD s1, VLAD s2, double max ) {
		return getDistance(s1, s2);
	}
	
//	public static final double getDistance(VLAD s1, VLAD s2 ) {
//		if ( s1.size() != s2.size() ) return 1.0;
//		
//		double t = 0;
//		float[] v1 = s1.values;
//		float[] v2 = s2.values;
//		for ( int i=0; i<s1.size(); i++ ) {
//			double temp = v1[i] - v2[i];
//			t += temp * temp; 
//		}
//		
//		return Math.sqrt(t)/100.0;
//	}

	public static final double getDistance(VLAD s1, VLAD s2 ) {
		if ( s1.size() != s2.size() ) return 1.0;
		
		double t = 0;
		float[] v1 = s1.values;
		float[] v2 = s2.values;
		for ( int i=0; i<s1.size(); i++ ) {
			t += v1[i] * v2[i];
		}
		
		return 1.0 - t;
	}	
	
//	public static final double getDistance(VLAD s1, VLAD s2, double max ) {
//		double tMax = max * 4.0;
//		if ( s1.size() != s2.size() ) return 1.0;
//		
//		double t = 0;
//		float[] v1 = s1.values;
//		float[] v2 = s2.values;
//		for ( int i=0; i<s1.size(); i++ ) {
//			double temp = v1[i] - v2[i];
//			t += temp * temp; 
//			
//			if ( t > tMax ) return -t / 4.0;
//		}
//	
//		return t / 4.0;
//	}
//    
//


}
