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

import it.cnr.isti.vir.distance.Hamming;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.ILongBinaryValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;
import it.cnr.isti.vir.util.bytes.Primitives;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class VLADHamming extends AbstractFeature implements ILongBinaryValues {
 
	public AbstractFeaturesCollector linkedFC;
	long[] values;
	
	@Override
	public final int getLength() {
		return values.length;
	}
	
	@Override
	public final long[] getElements() {
		return values;
	}
	
//	public int size() {
//		if ( values == null ) return 0;
//		return values.length;
//	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length);
			byte[] b = new byte[Primitives.LONG_NBYTES*values.length];
			LongByteArrayUtil.convToBytes(values, b, 0);
			out.write(b);
		}
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		if ( values == null ) {
			buff.putInt( 0);
		} else {
			buff.putInt( values.length);
			for ( int i=0; i<values.length; i++ )
				buff.putLong(values[i]);
		}
	}
	
    public VLADHamming(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public VLADHamming(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new long[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getLong();
			}
		}	
	}
	
	public VLADHamming(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Primitives.LONG_NBYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = LongByteArrayUtil.get(bytes, 0, size);
		}
    }
	
	public VLADHamming(long[] values) {
		this.values = values;
	}

	public VLADHamming(DataInput in ) throws Exception {
		this(in, null);
	}

    public static final  VLADHamming getVLAD(ALocalFeaturesGroup features, LFWords fWords) throws Exception {
     	ALocalFeature[] refs = (ALocalFeature[]) fWords.getFeatures();
    	ALocalFeature[] lf  = features.getLocalFeatures();
        
        
        if ( ! (refs[0] instanceof ILongBinaryValues) ) {        	
        	throw new Exception ("VLADHamming can't be computed from " + features.getClass() );
        }
        
        int nEleBits = ((ILongBinaryValues) refs[0]).getNBits();
        int nEleLongs = ((ILongBinaryValues) refs[0]).getLength();
        int nRefs = refs.length;
        int size = nRefs * nEleLongs;
        final int nBits = size*Long.SIZE;
        
        long[] values = new long[size];
        int[] bitsAccumulators = new int[nBits];
        int[] refsCount = new int[nRefs];
		if ( lf.length == 0 ) {
			// NO LOCAL FEATURES WERE FOUND!
			for ( int i=0; i<size; ) {
				long[] ref = ((ILongBinaryValues) refs[i/nEleLongs]).getElements();
				for ( int id=0; id<nEleLongs; id++) {
					values[i++] = ~ ref[id];
				}					
			}
		} else {
//			ArrayList<long[]>[] accumulator = new ArrayList[fWords.size()];
//			int initSize = lf.length / fWords.size() * 2;
//			for ( int i=0; i<accumulator.length; i++ ) {
//				accumulator[i] = new ArrayList<long[]>( initSize );
//			}
			for (int iLF = 0; iLF < lf.length; iLF++) {
	
				long[] curr = ((ILongBinaryValues) lf[iLF]).getElements();
				int iW = fWords.getNNIndex(lf[iLF]);
				refsCount[iW]++;
				long[] ref = ((ILongBinaryValues) refs[iW]).getElements();
				
				int iAcc = iW * nEleBits;
				//int end = start + nBits;
	
				// accumulating XORs
				for (int j=0; j<nEleLongs; j++) {
					long temp = curr[j] ^ ref[j];
					
					long mask = 1; 
					for ( int i2=0; i2<Long.SIZE; i2++, iAcc++) {
						// for each bit
						if ( (temp & mask) != 0 ) bitsAccumulators[iAcc]++;
						//else bitsAccumulators[iAcc]--;
						mask = mask << 1;
					}
				}
				
			}
			
			int sum = 0;
			for (int i=0; i<nBits; i++) {
//				bitsAccumulators[i] = (int) Math.sqrt(bitsAccumulators[i]);
				sum += bitsAccumulators[i];
			}
			int thr = sum / bitsAccumulators.length;
//			if ( thr < 0 ) thr = 0;
			
//			int thr = 0;
			//int thr = lf.length / nBits;
			
			int iAcc = 0;
			int iL =0;
			for ( int iW=0; iW<nRefs; iW++ ) {
				//int thr = refsCount[iW] / 2;
				for ( int i=0; i<nEleLongs; i++, iL++ ) {
					long mask = 1; 
					for ( int iB=0; iB<Long.SIZE; iB++ ) {
						if ( bitsAccumulators[iAcc++] > thr ) {
							values[iL] |= mask; 
						}
						mask = mask << 1;
					}
				}
			}
			
		
		}
		
//        // Power Normalization 0.5
//        for (int i=0; i<size; i++) {     
//        	if ( values[i] == 0 ) values[i] = 0.0F;
//        	else if ( values[i] > 0 )
//        		values[i] =   (float) Math.sqrt((double) values[i]);
//        	else 
//        		values[i] = - (float) Math.sqrt((double) -values[i]);
//        }
//	        
//
//		// L2 Normalization
//        double sum2 = 0;
//        for (int i=0; i<size; i++) {
//        	sum2 += (values[i]*values[i]);
//        }
//        
//        if ( sum2 > 0.0 ) {
//	        double sqrtsum2 = Math.sqrt(sum2);    
//	        for (int i=0; i<size; i++) {
//	        	values[i] = (float) (values[i] / sqrtsum2 );	        	
//	        }        
//        } else {
//        	values = null;
//        }
        
        return new VLADHamming(values);
    }
    
//	public static final VLADHamming gVLAD(ALocalFeaturesGroup group, LFWords words) throws Exception {
//		if ( group instanceof SIFTGroup ) {
//			return getVLAD((SIFTGroup) group, words);
//		}
//		return null;
//	}
	
	

	public static final double getDistance(VLADHamming s1, VLADHamming s2, double max ) {
		return getDistance(s1, s2);
	}


	public static final double getDistance(VLADHamming s1, VLADHamming s2 ) {
		return Hamming.distance_norm(s1.values, s2.values );
	}

	@Override
	public int getNBits() {
		return values.length * Long.SIZE;
	}




}
