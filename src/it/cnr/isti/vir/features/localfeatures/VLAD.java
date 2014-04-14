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

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VLAD extends AbstractFeature {
 
	public AbstractFeaturesCollector linkedFC;
	float[] values;
	
	public int size() {
		return values.length;
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt( values.length);
        byte[] b = new byte[Float.BYTES*values.length];
        FloatByteArrayUtil.convToBytes(values, b, 0);
        out.write(b);
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		buff.putInt( values.length);
		for ( int i=0; i<values.length; i++ )
			buff.putFloat(values[i]);
	}
	
    public VLAD(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public VLAD(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
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
	public VLAD(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

//		if ( size == 0 ) return;
		
//		values = new float[size];
//		for ( int i=0; i<values.length; i++ ) {
//			values[i] = in.readFloat();
//		}

        
        int nBytes = Float.BYTES*size;
        byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		values = FloatByteArrayUtil.get(bytes, 0, size);
    }

	
	
    public static final  VLAD getVLAD(ALocalFeaturesGroup features, LFWords fWords) {
    	ALocalFeature[] refs = (ALocalFeature[]) fWords.getFeatures();
    	ALocalFeature[] lf  = features.getLocalFeatures();
        
        boolean siftFlag = ( refs[0] instanceof SIFT );
        boolean siftpcaFlag = ( refs[0] instanceof SIFTPCA );
        boolean siftpcafloatFlag = ( refs[0] instanceof SIFTPCAFloat );
        boolean rootsiftFlag = ( refs[0] instanceof RootSIFT );
        
        int d = 128;
        if ( siftpcaFlag ) {
        	d = ((SIFTPCA) refs[0]).values.length;
        } else if ( siftpcafloatFlag ) {
        	d = ((SIFTPCAFloat) refs[0]).values.length;
        }
        int size = refs.length * d;
        int[] intValues = new int[size];
        
        
		if (siftpcafloatFlag) {
			for (int iLF = 0; iLF < lf.length; iLF++) {

				float[] curr = ((SIFTPCAFloat) lf[iLF]).values;;

				int iW = fWords.getNNIndex(lf[iLF]);
				int start = iW * d;
				int end = start + d;

				float[] ref = ((SIFTPCAFloat) refs[iW]).values;

				int j = 0;
				for (int i = start; i < end; i++, j++) {
					intValues[i] += curr[j] - ref[j];
				}
				
			}
		} else {
			for (int iLF = 0; iLF < lf.length; iLF++) {

				byte[] curr = null;
				if (siftFlag) {
					curr = ((SIFT) lf[iLF]).values;
				} else if (siftpcaFlag) {
					curr = ((SIFTPCA) lf[iLF]).values;
				} else if (rootsiftFlag) {
					curr = ((RootSIFT) lf[iLF]).values;
				}

				int iW = fWords.getNNIndex(lf[iLF]);
				int start = iW * d;
				int end = start + d;

				byte[] ref = null;
				if (siftFlag) {
					ref = ((SIFT) refs[iW]).values;
				} else if (siftpcaFlag) {
					ref = ((SIFTPCA) refs[iW]).values;
				} else if (rootsiftFlag) {
					ref = ((RootSIFT) refs[iW]).values;
				}

				int j = 0;
				for (int i = start; i < end; i++, j++) {
					intValues[i] += curr[j] - ref[j];
				}
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
    
	public static final VLAD gVLAD(ALocalFeaturesGroup group, LFWords words) {
		if ( group instanceof SIFTGroup ) {
			return getVLAD((SIFTGroup) group, words);
		}
		return null;
	}
	
	

	public static final double getDistance(VLAD s1, VLAD s2, double max ) {
		return getDistance(s1, s2);
	}


	public static final double getDistance(VLAD s1, VLAD s2 ) {
		if ( s1.size() != s2.size() ) return 1.0;
		
		double t = 0.0;
		float[] v1 = s1.values;
		float[] v2 = s2.values;
		for ( int i=0; i<s1.size(); i++ ) {
			t += v1[i] * v2[i];
		}
		double dist =  (1.0 - t) / 2.0;
		
		if ( dist < 0.0 ) dist = 0.0;
		return dist;
	}

}
