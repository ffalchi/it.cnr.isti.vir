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
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VLAD extends AbstractFeature implements IFloatValues {
 
	public AbstractFeaturesCollector linkedFC;
	float[] values;
	
	@Override
	public final float[] getValues() {
		return values;
	}
	
	public int size() {
		if ( values == null ) return 0;
		return values.length;
	}
	
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length);
			byte[] b = new byte[FloatByteArrayUtil.BYTES*values.length];
			FloatByteArrayUtil.convToBytes(values, b, 0);
			out.write(b);
		}
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		if ( values == null ) {
			buff.putInt( 0);
		} else {
			buff.putInt( values.length);
			for ( int i=0; i<values.length; i++ )
				buff.putFloat(values[i]);
		}
	}
	
    public VLAD(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public VLAD(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new float[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getFloat();
			}
		}	
	}
	
	public VLAD(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Float.BYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = FloatByteArrayUtil.get(bytes, 0, size);
		}
    }
	
	public VLAD(float[] values) {
		this.values = values;
	}

	public VLAD(DataInput in ) throws Exception {
		this(in, null);
	}


	
	
    public static final  VLAD getVLAD(ALocalFeaturesGroup features, LFWords fWords) {
    	ALocalFeature[] refs = (ALocalFeature[]) fWords.getFeatures();
    	ALocalFeature[] lf  = features.getLocalFeatures();
        
        boolean bytesValues = ( refs[0] instanceof IByteValues );
        boolean floatValues = ( refs[0] instanceof IFloatValues );
        
        int d = 128;
        if ( bytesValues ) {
        	d = ((IByteValues) refs[0]).getValues().length;
        } else if ( floatValues ) {
        	d = ((IFloatValues) refs[0]).getValues().length;
        }
        int size = refs.length * d;
        	
        float[] values = new float[size];
        
		if (floatValues) {
			
			if ( lf.length == 0 ) {
				// NO LOCAL FEATURES WERE FOUND!
				for ( int i=0; i<size; ) {
					float[] ref = ((IFloatValues) refs[i/d]).getValues();
					for ( int id=0; id<d; id++) {
						values[i++] = -ref[id];
					}					
				}
			}
			
			for (int iLF = 0; iLF < lf.length; iLF++) {

				float[] curr = ((SIFTPCAFloat) lf[iLF]).values;;

				int iW = fWords.getNNIndex(lf[iLF]);
				int start = iW * d;
				int end = start + d;

				float[] ref = ((SIFTPCAFloat) refs[iW]).values;

				int j = 0;
				for (int i = start; i < end; i++, j++) {
					values[i] += curr[j] - ref[j];
				}
				
			}
			
	        // Power Normalization 0.5
	        for (int i=0; i<size; i++) {     
	        	if ( values[i] == 0 ) values[i] = 0.0F;
	        	else if ( values[i] > 0 )
	        		values[i] =   (float) Math.sqrt((double) values[i]);
	        	else 
	        		values[i] = - (float) Math.sqrt((double) -values[i]);
	        }
	        
		} else {
			int[] intValues = new int[size];
			
			if ( lf.length == 0 ) {
				// NO LOCAL FEATURES WERE FOUND!				
				for ( int i=0; i<size; ) {
					byte[] ref = ((IByteValues) refs[i/d]).getValues();
					for ( int id=0; id<d; id++) {
						intValues[i++] = -ref[id];
					}					
				}
			}
			
			for (int iLF = 0; iLF < lf.length; iLF++) {

				byte[] curr = ((IByteValues) lf[iLF]).getValues();

				int iW = fWords.getNNIndex(lf[iLF]);
				int start = iW * d;
				int end = start + d;

				byte[] ref = ((IByteValues) refs[iW]).getValues();

				int j = 0;
				for (int i = start; i < end; i++, j++) {
					intValues[i] += curr[j] - ref[j];
				}
				

			}
			
	        // Power Normalization 0.5
	        for (int i=0; i<size; i++) {     
	        	if ( intValues[i] == 0 ) values[i] = 0.0F;
	        	else if ( intValues[i] > 0 )
	        		values[i] =   (float) Math.sqrt((double) intValues[i]);
	        	else 
	        		values[i] = - (float) Math.sqrt((double) -intValues[i]);
	        }
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
        } else {
        	values = null;
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
		
		float[] v1 = s1.values;
		float[] v2 = s2.values;
		
		if ( v1 == null && v2 == null ) return 0;
		if ( v1 == null || v2 == null ) return 0.5; // TODO !!!
		
		if ( v1.length != v2.length ) return 1.0;
		
//		double t = 0.0;
//		for ( int i=0; i<s1.size(); i++ ) {
//			t += v1[i] * v2[i];
//		}
//		double dist =  (1.0 - t) / 2.0;
//		
//		//if ( dist < 0.0 ) dist = 0.0;
//
//		return dist;
		
		// for dealing with empty VLAD
//		double sum1 = 0.0;
//		double sum2 = 0.0;
//		for ( int i=0; i<v1.length; i++ ) {
//			sum1 += v1[i];
//			sum2 += v2[i];
//		}
//		
//		if ( sum1 == 0.0 && sum2 == 0.0 ) return 0.0; 
//		if ( sum1 == 0.0 || sum2 == 0.0 ) return Math.sqrt(0.5);
		
		//if ( sum1 != 0.0 && (sum1 < .999f || sum1 > 1.001f)) System.out.println(sum1);
		//if ( sum2 != 0.0 && (sum2 < .999f || sum2 > 1.001f)) System.out.println(sum2);
		
		return L2.get(v1, v2)/2.0;
	}




}
