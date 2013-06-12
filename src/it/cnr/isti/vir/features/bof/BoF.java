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
package it.cnr.isti.vir.features.bof;

import gnu.trove.list.array.TIntArrayList;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.bytes.IntByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BoF extends AbstractFeature {

    protected double magnitude = -1.0;
    protected double magnitude_TFIDF = -1.0;
    protected double magnitude_IDF = -1.0;
	
	protected final LFWords fWords;
	public static byte version = 0;
	int[] bag;
	
	boolean ordered = false;

	
	public BoF(DataInput in ) throws IOException {
		this(in, null);
	}
	
	public BoF(ByteBuffer in ) throws IOException {
		this(in, null);
	}
	
	public final void orderByBags() {

        if ( this.ordered == true ) return;

        //Bubble
        for (int i = 0; i < bag.length; i++) {
            for (int j = i; j < bag.length; j++) {
                if (bag[i] > bag[j]) {
                    int intTemp = bag[i];
                    bag[i] = bag[j];
                    bag[j] = intTemp;
                }
            }
        }

        this.ordered = true;
	}
	
    
    public final int getDistinctWordsCount() {
    	int count = 0;
    	this.orderByBags();
    	int last = -1;
    	for (int i = 0; i < bag.length; i++) {
    		if ( bag[i]!=last ) {
    			count++;
    			last = bag[i];
    		}
    	}
    	return count;
    }
	
	public BoF(AbstractFeaturesCollector fcc, LFWords fWords) {
		this( (ALocalFeaturesGroup) fcc.getFeature(fWords.getLocalFeaturesGroupClass()), fWords);
	}
	
	public BoF(ALocalFeaturesGroup group, LFWords fWords) {
		this( (ALocalFeature[]) group.getLocalFeatures(), fWords);
	}
	
	public BoF(ALocalFeature[] features, LFWords fWords) {
		this.fWords = fWords;
		bag = fWords.getBags(features);
		//bag = fWords.getWeightedBags(features,  1);
		//bag = fWords.getWeightedBags_Spearman(features,4);
		//removeByOccThreshold(2);
	}
	
	public void removeByRandomSelection(float redFactor) {
		TIntArrayList tRes = new TIntArrayList(bag.length);
		for (int i = 0; i < bag.length; i++) {
			if ( RandomOperations.trueORfalse(redFactor) ) {
				tRes.add(bag[i]);
			}
		}
		bag = tRes.toArray();
	}
	
	public void removeByOccThreshold(int threshold) {
		this.orderByBags();
		int i=0;
		TIntArrayList tRes = new TIntArrayList(bag.length);
		while ( i < bag.length ) {
			int currBag = bag[i++];
			int occ=1;
			while ( i < bag.length && bag[i] == currBag ) {
				occ++;
				i++;
			}
			if ( occ > threshold ) {
				// rounding
				int newOcc = Math.round( (float) occ / threshold );
				for ( int j=0; j<newOcc; j++ ) {
					tRes.add(currBag);
				}
			}
		}
		bag = tRes.toArray();
	}
	
	public void reduceByFactor(int factor) {
		this.orderByBags();
		int i=0;
		int realFactor = (int) Math.sqrt(factor);
		TIntArrayList tRes = new TIntArrayList(bag.length);
		while ( i < bag.length ) {
			int currBag = bag[i++];
			int occ=1;
			while ( i < bag.length && bag[i] == currBag ) {
				occ++;
				i++;
			}
			if ( occ > realFactor ) {
				int newOcc = (occ + realFactor / 2) / realFactor;
				for ( int j=0; j<newOcc; j++ ) {
					tRes.add(currBag);
				}
			}
		}
		bag = tRes.toArray();
	}
	
    public BoF(ByteBuffer in, LFWords fWords) throws IOException  {

        this.fWords = fWords;
        byte version = in.get();
        int wordsHashCode = in.getInt();
        int size = in.getInt();
		if ( fWords != null ) {
			if ( size != fWords.size() ) {
				throw new IOException("found BagOfWords of size " + size + " for FeaturesAsWords of size " + fWords.size());
			} else  if ( wordsHashCode != 0 && wordsHashCode != fWords.hashCode() ) {
				throw new IOException("readed BagOfWords hashCode " + wordsHashCode + " for FeaturesAsWords of size " + fWords.hashCode());
			}
		}
		bag = new int[size];
        for (int i = 0; i < size; i++) {
            bag[i] = in.getInt();
        }

        //readEval(in);
    }
	
	
	public BoF(DataInput in, LFWords fWords ) throws IOException {
		this.fWords = fWords;
		
		byte version = in.readByte();
		int wordsHashCode = in.readInt();
		int size = in.readInt();
		if ( fWords != null ) {
			if ( size != fWords.size() ) {
				throw new IOException("found BagOfWords of size " + size + " for FeaturesAsWords of size " + fWords.size());
			} else  if ( wordsHashCode != 0 && wordsHashCode != fWords.hashCode() ) {
				throw new IOException("readed BagOfWords hashCode " + wordsHashCode + " for FeaturesAsWords of size " + fWords.hashCode());
			}
		}
		
		byte[] byteArr = null;
		byteArr = new byte[size*4];

		in.readFully(byteArr);
		int offset = 0;
		
		bag =IntByteArrayUtil.get(byteArr, offset, size);
		offset += 4*size;
		/*
		ori = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, offset, size);
		offset += 8*size;
		
		scale = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, offset, size);
		*/
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		orderByBags();
		
		int byteSize = 1+4+4+4*bag.length;
		
		byte[] res = new byte[byteSize];
		int offset = 0;
		res[0]=version;
		offset+=1;
		if ( fWords != null )
			IntByteArrayUtil.convToBytes(fWords.hashCode(), res, offset);
		else 
			IntByteArrayUtil.convToBytes(0, res, offset);
		offset+=4;
		
		IntByteArrayUtil.convToBytes(bag.length, res, offset);
		offset+=4;
		
		IntByteArrayUtil.convToBytes(bag, res, offset);
		offset+=4*bag.length;
		/*
		FloatByteArrayUtil.floatArrayToByteArray(ori, 	res, offset);
		offset+=8*ori.length;
		
		FloatByteArrayUtil.floatArrayToByteArray(scale, res, offset);
		offset+=8*scale.length;
		*/
		out.write(res);
		
	}
	

    public final static double getSimilarity_cosine(BoF g1, BoF g2 ) {
    	return getSimilarity_cosine(g1, g2, null, false );
    }

    public final static double getSimilarity_cosine(BoF g1, BoF g2, float[] idf ) {
    	return getSimilarity_cosine(g1, g2, idf, false);
    }
    
    public int size() {
    	if ( bag == null ) return 0;
    	return bag.length;
    }
	public final static double getSimilarity_cosine(BoF g1, BoF g2, float[] idf, boolean noTF ) {
        double num = 0;
        
        if ( g1.size() == 0 || g2.size() == 0 ) return 0;
        
        int[] g1Arr = g1.bag;
        int[] g2Arr = g2.bag;
        
        double sizeRatio =  g1.size() / g2.size();
    	if ( sizeRatio < 0.1 || sizeRatio > 1.0 ) return 0.0;
        
        int i2 = 0;
        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
            int count1 = 1;
            int currBag = g1Arr[i1];

            // counting occurences
            while( i1<g1Arr.length-1 && currBag == g1Arr[i1+1] ) {
                i1++;
                count1++;
            }

            // searching candidate i2
            while (g2Arr[i2] < currBag && i2 < g2Arr.length - 1) {
            	i2++;
            }

            // counting co-occurences
            int count2 = 0;
            while ( i2 < g2Arr.length && g2Arr[i2] == currBag ) {
                i2++;
                count2++;
            }

            if ( count2 != 0 ) {
            	if ( idf != null ) {
            		if ( noTF ) {

		                num += (double) idf[currBag] * idf[currBag];
            			
            		} else {
		                double tfidf1 = (double) count1 / g1Arr.length 	// TF
		                				* idf[currBag];					// IDF
		
		            	double tfidf2 = (double) count2 / g2Arr.length	// TF
										* idf[currBag];					// IDF
	
		                num += (double) tfidf1 * tfidf2;
            		}
            	} else {
            		num += (double) count1 * count2;
            	}
            }

        }

        if ( idf != null ) {
        	if ( noTF ) {
        		return num / ( g1.getMagnitude_IDF(idf) * g2.getMagnitude_IDF(idf)  ) ;
        	} else {
        		return num / ( g1.getMagnitude_TFIDF(idf) * g2.getMagnitude_TFIDF(idf)  ) ;
        	}
        }
        return num / ( g1.getMagnitude() * g2.getMagnitude()  ) ;

    }
	

	public double getMagnitude() {
		if ( magnitude > 0 ) return magnitude;
		long res = 0;
		if ( bag.length == 0 ) return 0;
		
//		this.orderByBags();
		int lastBag = bag[0];
		int currCount = 1;
		for(int i = 1; i < bag.length ; i++) {
			int currBag = bag[i];
			if ( currBag == lastBag ) {
				currCount++; 
			} else {
				lastBag = currBag;
				res += currCount * currCount;
				currCount = 1;
			}			
		}
		res += currCount * currCount;
		magnitude = Math.sqrt(res);
		return magnitude;
	}
	
	public final double getMagnitude_TFIDF( float[] idf ) {
		if ( magnitude_TFIDF > 0 ) return magnitude_TFIDF;
		float res = 0;
		if ( bag.length == 0 ) return 0.0;
		
//		this.orderByBags();
		int lastBag = bag[0];
		int lastCount = 1;
		for(int i = 1; i < bag.length ; i++) {
			int currBag = bag[i];
			if ( currBag == lastBag ) {
				lastCount++; 
			} else {
				double tfidf1 = (double) lastCount / bag.length 	// TF
								* idf[lastBag];						// IDF
	            res += tfidf1 * tfidf1;
	            lastBag = currBag;
	            lastCount = 1;
			}			
		}
		
		// last
		double tfidf1 = (double) lastCount / bag.length 	// TF
						* idf[lastBag];						// IDF
		res += tfidf1 * tfidf1;
		
		magnitude_TFIDF = Math.sqrt(res);
		return magnitude_TFIDF;
	}
	
	
	public final double getMagnitude_IDF( float[] idf ) {
		if ( magnitude_IDF > 0 ) return magnitude_IDF;
		float res = 0;
		if ( bag.length == 0 ) return 0.0;
		if ( bag.length == 1 ) return 1.0;
//		this.orderByBags();
		int lastBag = bag[0];
		int lastCount = 1;
		for(int i = 1; i < bag.length ; i++) {
			int currBag = bag[i];
			if ( currBag == lastBag ) {
				lastCount++; 
			} else {
				res += idf[lastBag] * idf[lastBag];
	            lastBag = currBag;
	            lastCount = 1;
			}			
		}
		
		// last
		res += idf[lastBag] * idf[lastBag];
		
		magnitude_IDF = Math.sqrt(res);
		return magnitude_IDF;
	}
	
}
