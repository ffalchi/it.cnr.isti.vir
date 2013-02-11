package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BoFLFGroupSoft extends AbstractLFGroup<BoFLFSoft> {

    public static final byte version = 0;
    protected final LFWords fWords;
    protected int magnitude = -1;
    protected double magnitude_TFIDF = -1.0;
    
	public BoFLFGroupSoft(DataInput in) throws Exception {
		this(in, null);
	}
	
	public BoFLFGroupSoft(DataInput in, IFeaturesCollector fc ) throws Exception {
		super(fc);
		
		fWords = null;
		byte version = in.readByte();
		int size = in.readInt();

		if ( size == 0 ) return;
		
		lfArr = new BoFLFSoft[size];
		int byteSize = in.readInt();
		byte[] tArr = new byte[byteSize];
		in.readFully(tArr);
		ByteBuffer buffer = ByteBuffer.wrap(tArr);
		for ( int i=0; i<size; i++ ) {
			lfArr[i] = new BoFLFSoft(buffer, this);
		}
		
		readEval(in);
	}
    
    public BoFLFGroupSoft(ByteBuffer in, LFWords fWords) throws Exception {
        this(in, fWords, null);
    }
    
    public BoFLFGroupSoft(ByteBuffer in, IFeaturesCollector fc) throws Exception {
        this(in, null, fc);
    }
    
    public BoFLFGroupSoft(ByteBuffer in ) throws Exception {
        this(in, null, null);
    }

    public BoFLFGroupSoft(ByteBuffer in, LFWords fWords, IFeaturesCollector fc) throws Exception {
        super(fc);
        this.fWords = fWords;
        byte version = in.get();
        int size = in.getInt();
        lfArr = new BoFLFSoft[size];
        if (size == 0) {
            return;
        }

        for (int i = 0; i < size; i++) {
            lfArr[i] = new BoFLFSoft(in, this);
        }

        readEval(in);
    }

	public BoFLFGroupSoft(AbstractLFGroup group, LFWords fWords, int k) {
		this( (ILocalFeature[]) group.getLocalFeatures(), fWords, k);
	}
    
    public BoFLFGroupSoft(ILocalFeature[] features, LFWords fWords, int k) {
        this(features, fWords, null, k);
    }

    public BoFLFGroupSoft(ILocalFeature[] features, LFWords fWords, IFeaturesCollector fc, int k) {
        super(fc);
        this.fWords = fWords;
        if ( features instanceof BoFLFSoft[] ) {
        	lfArr = (BoFLFSoft[]) features;
        } else {
        	lfArr = fWords.getBoFLFArrSoft(features, k, this);
        }
//        Arrays.sort(lfArr);
    }

    @Override
    public Class getLocalFeatureClass() {
        return BoFLF.class;
    }

    public final static LocalFeaturesMatches getMatches(BoFLFGroupSoft g1, BoFLFGroupSoft g2) {
    	// TO DO !!
    	
    	
//        LocalFeaturesMatches matches = new LocalFeaturesMatches();
//
//        BoFLF[] g1Arr = g1.lfArr;
//        BoFLF[] g2Arr = g2.lfArr;
//        int i2 = 0;
//        for (int i1 = 0; i1 < g1Arr.length; i1++) {
//
//            // avoiding duplicates
////			if ( i1<g1Arr.length-1 && g1Arr[i1].bag == g1Arr[i1+1].bag) continue;
//
//            // searching candidate i2
//            while (g2Arr[i2].bag < g1Arr[i1].bag && i2 < g2Arr.length - 1) {
//                i2++;
//            }
//
////                        if ( i2 < g2Arr.length && g2Arr[i2].bag == g2Arr[i1].bag ) {
////                            // avoiding duplicates
////                            if ( i2<g2Arr.length-1 && g2Arr[i2].bag == g2Arr[i2+1].bag) continue;
////
////                            matches.add( new LocalFeatureMatch( g1Arr[i1], g2Arr[i2] ) );
////                        }
//
//
//            // for duplicates
//            int ti2 = i2;
//            while (ti2 < g2Arr.length && g2Arr[ti2].bag == g1Arr[i1].bag) {
//                matches.add(new LocalFeatureMatch(g1Arr[i1], g2Arr[ti2]));
//                ti2++;
//            }
//
//        }
//        return matches;
    	return null;
    }

    @Override
    public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeInt(lfArr.length);
    	
		int byteSize = 0;
		for (int i = 0; i < lfArr.length; i++) {
			byteSize += lfArr[i].getByteSize();
		}
		out.writeInt(byteSize);
        byte[] t = new byte[byteSize];
        ByteBuffer buffer = ByteBuffer.wrap(t);
        for (int i = 0; i < lfArr.length; i++) {
            lfArr[i].writeData(buffer);
        }
        out.write(t);
        
        this.writeEval(out);
    }


//    public final static double getSimilarity_cosine(BoFLFGroup g1, BoFLFGroup g2 ) {
//
//        double num = 0;
//
//        BoFLF[] g1Arr = g1.lfArr;
//        BoFLF[] g2Arr = g2.lfArr;
//
//        int i2 = 0;
//        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
//            int count1 = 1;
//            int currBag = g1Arr[i1].bag;
//            
//            // counting occurences
//            while( i1<g1Arr.length-1 && currBag == g1Arr[i1+1].bag ) {
//                i1++;
//                count1++;
//            }
//
//            // searching candidate i2
//            while (g2Arr[i2].bag < currBag && i2 < g2Arr.length - 1) {
//                i2++;
//            }
//
//            // counting occurences
//            int count2 = 0;
//            while ( i2 < g2Arr.length && g2Arr[i2].bag == currBag ) {
//                i2++;
//                count2++;
//            }
//            
//            if ( count2 == 0 ) continue;
//
//            num += (double) count1 * count2;
//
//        }
//
//        return num /  ( g1.getMagnitude() * g2.getMagnitude() ) ;
//        
//    }
    
	public double getMagnitude() {
		
//		int res = 0;
//		if ( lfArr.length == 0 ) return 0;
////		this.orderByBags();
//		int lastBag = lfArr[0].bag;
//		int currCount = 1;
//		for(int i = 1; i < lfArr.length ; i++) {
//			int currBag = lfArr[i].bag;
//			if ( currBag == lastBag ) {
//				currCount++; 
//			} else {
//				lastBag = currBag;
//				res += currCount * currCount;
//				currCount = 1;
//			}			
//		}
//		return Math.sqrt(res);
		return 0.0;
	}
	
	public final double getMagnitude_TFIDF( float[] idf ) {
		// TO DO !!
		
//		if ( magnitude_TFIDF > 0 ) return magnitude_TFIDF;
//		float res = 0;
//		if ( lfArr.length == 0 ) return 0;
////		this.orderByBags();
//		int lastBag = lfArr[0].bag;
//		int lastCount = 1;
//		for(int i = 1; i < lfArr.length ; i++) {
//			int currBag = lfArr[i].bag;
//			if ( currBag == lastBag ) {
//				lastCount++; 
//			} else {
//				double tfidf1 = (double) lastCount / lfArr.length 	// TF
//								* idf[lastBag];						// IDF
//	            res += tfidf1 * tfidf1;
//	            lastBag = currBag;
//	            lastCount = 1;
//			}			
//		}
//		magnitude_TFIDF = Math.sqrt(res);
//		return magnitude_TFIDF;
		return 0.0;
	}
    

    public final static double getSimilarity_cosine(BoFLFGroupSoft g1, BoFLFGroupSoft g2 ) {
    	return getSimilarity_cosine(g1, g2, null );
    }

	public final static double getSimilarity_cosine(BoFLFGroupSoft g1, BoFLFGroupSoft g2, float[] idf ) {
		
//		double num = 0;
//
//        BoFLFSoft[] g1Arr = g1.lfArr;
//        BoFLFSoft[] g2Arr = g2.lfArr;
//
//        int i2 = 0;
//        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
//            int count1 = 1;
//            int currBag = g1Arr[i1].bag;
//
//            // counting occurences
//            while( i1<g1Arr.length-1 && currBag == g1Arr[i1+1].bag ) {
//                i1++;
//                count1++;
//            }
//
//            // searching candidate i2
//            while (g2Arr[i2].bag < currBag && i2 < g2Arr.length - 1) {
//            	i2++;
//            }
//
//            // counting co-occurences
//            int count2 = 0;
//            while ( i2 < g2Arr.length && g2Arr[i2].bag == currBag ) {
//                i2++;
//                count2++;
//            }
//
//            if ( count2 != 0 ) {
//            	if ( idf != null ) {
//	                double tfidf1 = (double) count1 / g1Arr.length 	// TF
//	                				* idf[currBag];					// IDF
//	
//	            	double tfidf2 = (double) count2 / g2Arr.length	// TF
//									* idf[currBag];					// IDF
//
//	                num += (double) tfidf1 * tfidf2;
//            	} else {
//            		num += (double) count1 * count2;
//            	}
//            }
//
//        }
//
//        if ( idf != null )
//        	return num / ( g1.getMagnitude_TFIDF(idf) * g2.getMagnitude_TFIDF(idf)  ) ;
//
//        return num / ( g1.getMagnitude() * g2.getMagnitude()  ) ;
		
		return 0.0;

    }
	
	
	public float getMinSize() {
		float minSize = Float.MAX_VALUE;
		for (int i = 0; i < lfArr.length; i++) {
			float curr = lfArr[i].getScale();
			if (curr < minSize)
				minSize = curr;
		}
		return minSize;
	}

	
	public BoFLFGroupSoft getAboveSize(float minSize) {
		
		return new BoFLFGroupSoft(this, minSize);
	}
	
	public BoFLFGroupSoft(BoFLFGroupSoft orig, float minSize) {
		super(orig.linkedFC);
		ArrayList<BoFLFSoft> newArr = new ArrayList<BoFLFSoft>(lfArr.length);
		for ( int i=0; i<orig.lfArr.length; i++ ) {
			if ( orig.lfArr[i].getScale() >= minSize )
				newArr.add(orig.lfArr[i]);
		}
		lfArr = new BoFLFSoft[newArr.size()];
		//linkedFC = orig.linkedFC;
	    fWords = orig.fWords;
	    magnitude = -1;
	    magnitude_TFIDF = -1.0;
		
	}

	@Override
	public AbstractLFGroup create(BoFLFSoft[] arr, IFeaturesCollector fc) {
		return new BoFLFGroupSoft( arr, fWords, fc, -1 );
	}

//	public final static double getSimilarity_cosineHough(BoFLFGroup g1, BoFLFGroup g2, float[] idf) {
//        double num = 0;
//
//        BoFLF[] g1Arr = g1.lfArr;
//        BoFLF[] g2Arr = g2.lfArr;
//        
//        TLongHashSet hashSet = new TLongHashSet();
//        TLongIntHashMap hashCount1Map = new TLongIntHashMap();
//        TLongIntHashMap hashCount2Map = new TLongIntHashMap();
//        TLongDoubleHashMap hashMap = new TLongDoubleHashMap();
//        
//        int lastBag = -1;
//        int i2 = 0;
//        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
//            int currBag = g1Arr[i1].bag;
//            if ( lastBag != currBag) {
//            	
//            	for ( TLongIntIterator it = hashCount1Map.iterator(); it.hasNext(); ) {
//            		it.advance();
//            		long hash  = it.key();
//            		int count1 = it.value();
//            		int count2 = hashCount2Map.get(hash);
//            		if ( count2 != 0 ) {
//            			double tNum;
//						// hash set was modified
//		               	if ( idf != null ) {
//							double tfidf1 = (double) count1 / g1Arr.length // TF
//									* idf[currBag]; // IDF
//		
//							double tfidf2 = (double) count2 / g2Arr.length // TF
//									* idf[currBag]; // IDF
//		
//							tNum = (double) tfidf1 * tfidf2;
//						} else {
//							tNum = (double) count1 * count2;
//						}
//		               	hashMap.adjustOrPutValue(hash, tNum, tNum);
//            		} else {
//            			System.err.println("Possible error!");
//            		}
//            	}              	
//            	
//            	//resetting counts
//               	hashCount1Map.clear();  
//               	hashCount2Map.clear();               	
//            }
//            
//            // searching candidate i2
//            while (g2Arr[i2].bag < currBag && i2 < g2Arr.length - 1) {
//            	i2++;
//            }
//
//            // to consider this i1 only once
//            hashSet.clear();
//            // co-occurences
//            int ti2 = i2;
//            while ( ti2 < g2Arr.length && g2Arr[ti2].bag == currBag ) {
//                
//    			int iOriDiff 	=  	LoweHoughTransform.getOriDiffBin(Trigonometry.getStdRadian(g2Arr[ti2].ori-g1Arr[i1].ori));
//    			int iScaleRatio = 	LoweHoughTransform.getScaleRatioBin(g2Arr[ti2].scale/g1Arr[i1].scale);
//
//    			long hashCode = LoweHoughTransform.getHash(0, 0, iOriDiff, iScaleRatio);
//
//    			if ( hashSet.add(hashCode) ) {
//    				hashCount1Map.adjustOrPutValue(hashCode, 1, 1);
//    			}
//    			
//            	
//            	ti2++;
//            }
//            
//
//        }
//        
//        double maxValue = 0;
//        for ( TLongDoubleIterator it = hashMap.iterator(); it.hasNext(); ) {
//        	it.advance();
//        	if ( maxValue < it.value() ) {
//        		maxValue = it.value();
//        	}
//        }
//
//        if ( idf != null )
//        	return maxValue / ( g1.getMagnitude_TFIDF(idf) * g2.getMagnitude_TFIDF(idf)  ) ;
//        
//        return maxValue / ( g1.getMagnitude() * g2.getMagnitude()  ) ;
//        
//    }

}
