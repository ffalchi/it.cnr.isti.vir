package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import it.cnr.isti.vir.util.RandomOperations;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class BoFLFGroup extends AbstractLFGroup<BoFLF> {

    public static final byte version = 0;
    protected final LFWords fWords;
    protected double magnitude = -1.0;
    protected double magnitude_TFIDF = -1.0;
    protected double magnitude_IDF = -1.0;
    protected static final double log2 = Math.log(2.0);
	public BoFLFGroup(DataInput in) throws Exception {
		this(in, null);
	}
	
	public int[] getWords() {
		int[] res = new int[lfArr.length];
		
		for ( int i=0; i<lfArr.length; i++ ) {
			res[i] = lfArr[i].bag;
		}
		
		return res;
	}
	
	public int getWordsMax() {
		int max = -1;
		for ( int i=0; i<lfArr.length; i++ ) {
			int temp =  lfArr[i].bag;
			if ( temp > max ) max = temp;
		}
		return max;
	}
	
	public int getWordsMin() {
		int min = Integer.MAX_VALUE;
		for ( int i=0; i<lfArr.length; i++ ) {
			int temp =  lfArr[i].bag;
			if ( temp < min ) min = temp;
		}
		return min;
	}
	
	public BoFLFGroup(DataInput in, IFeaturesCollector fc ) throws Exception {
		super(fc);
		
		fWords = null;
		byte version = in.readByte();
		int size = in.readInt();

//		if ( size == 0 ) return;
		
		lfArr = new BoFLF[size];
		byte[] tArr = new byte[BoFLF.getByteSize() * size];
		in.readFully(tArr);
		ByteBuffer buffer = ByteBuffer.wrap(tArr);
		for ( int i=0; i<size; i++ ) {
			lfArr[i] = new BoFLF(buffer, this);
		}
		
		readEval(in);
	}
    
	public BoFLFGroup(BoFLF[] arr, IFeaturesCollector fc, LFWords words ) throws Exception {
		super(fc);
		fWords = words;
		lfArr = arr;
		Arrays.sort(lfArr);
	}
	
    public BoFLFGroup(ByteBuffer in, LFWords fWords) throws Exception {
        this(in, fWords, null);
    }
    
    public BoFLFGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
        this(in, null, fc);
    }
    
    public BoFLFGroup(ByteBuffer in ) throws Exception {
        this(in, null, null);
    }

    public BoFLFGroup(ByteBuffer in, LFWords fWords, IFeaturesCollector fc) throws Exception {
        super(fc);
        this.fWords = fWords;
        byte version = in.get();
        int size = in.getInt();
        lfArr = new BoFLF[size];
//        if (size == 0) {
//            return;
//        }

        for (int i = 0; i < size; i++) {
            lfArr[i] = new BoFLF(in, this);
        }

        readEval(in);
    }

	public BoFLFGroup(AbstractLFGroup group, LFWords fWords) {
		this( (ILocalFeature[]) group.getLocalFeatures(), fWords);
	}
	
    public BoFLFGroup(ILocalFeature[] features, LFWords fWords) {
        this(features, fWords, null);
    }

    public BoFLFGroup(ILocalFeature[] features, LFWords fWords, IFeaturesCollector fc) {
        super(fc);
        this.fWords = fWords;
        if ( features instanceof BoFLF[] ) {
        	lfArr = (BoFLF[]) features;
        } else {
	        lfArr = new BoFLF[features.length];
	        int[] temp = fWords.getBags(features);
	        for (int i = 0; i < temp.length; i++) {
	            lfArr[i] = new BoFLF(
	                    temp[i],
	                    features[i].getXY(),
	                    features[i].getOrientation(),
	                    features[i].getScale(),
	                    this);
	        }
        }
        Arrays.sort(lfArr);
    }

    public BoFLFGroup(BufferedReader br, IFeaturesCollector fc) throws IOException {
		super(fc);
		ArrayList<BoFLF> arr = new ArrayList();
		
		fWords = null;
		
		String line = br.readLine();
		while ( !line.contains("</") ) {
			BoFLF bof = BoFLF.readTOPSURF(line, this);
			arr.add(bof);
			line = br.readLine();
		}
		lfArr = new BoFLF[arr.size()];
		lfArr = arr.toArray(lfArr);
		Arrays.sort(lfArr);
		
	}

    /*
	public BoFLFGroup(BoF_LF_OriAndScale f) {
		super(null);
		
		int[] bag = f.getBagIndexes();
		float[] ori = f.getOri();
		float[] scale = f.getScale();
		lfArr = new BoFLF[bag.length];
		for(int i=0; i<bag.length; i++) {
			float[] xy = {0.0F, 0.0F};
			lfArr[i] = new BoFLF(bag[i], xy, ori[i], scale[i], this);
		}
		fWords = f.getWords();
		Arrays.sort(lfArr);
	}*/

	@Override
    public Class getLocalFeatureClass() {
        return BoFLF.class;
    }

	
    public final static LocalFeaturesMatches getMatches_TFIDF(BoFLFGroup g1, BoFLFGroup g2, float[] idf ) {
        LocalFeaturesMatches matches = new LocalFeaturesMatches();

        BoFLF[] g1Arr = g1.lfArr;
        BoFLF[] g2Arr = g2.lfArr;
        int i2 = 0;
        for (int i1 = 0; i1 < g1Arr.length; i1++) {

            // searching candidate i2
            while (g2Arr[i2].bag < g1Arr[i1].bag && i2 < g2Arr.length - 1) {
                i2++;
            }

            // for duplicates
            int ti2 = i2;
            while (ti2 < g2Arr.length && g2Arr[ti2].bag == g1Arr[i1].bag) {
            	int index = g2Arr[ti2].bag;
                matches.add(new LocalFeatureMatch(g1Arr[i1], g2Arr[ti2], idf[index], index));
                ti2++;
            }

        }
        return matches;
    }

    
    public final static LocalFeaturesMatches getMatches(BoFLFGroup g1, BoFLFGroup g2) {
        LocalFeaturesMatches matches = new LocalFeaturesMatches();

        BoFLF[] g1Arr = g1.lfArr;
        BoFLF[] g2Arr = g2.lfArr;
        int i2 = 0;
        int lastWord = -1;
        
        final int maxNWordsPerGroup = 16;
        
        if ( g2Arr.length > 0 )
	        for (int i1 = 0; i1 < g1Arr.length; i1++) {
	        	
	        	int currWord = g1Arr[i1].bag;
	        	if ( currWord != lastWord ) {
	
	        		// checking for too many duplicated words 
	        		lastWord = currWord;
	        		
	                // searching candidate i2 [TO VERIFY!!!]
	                while (g2Arr[i2].bag < g1Arr[i1].bag && i2 < g2Arr.length - 1) {
	                    i2++;
	                }
	                
	                int ti1 = i1;
	                int ti2 = i2;                
	                
	                while ( g1Arr[ti1].bag == currWord && ti1 < g1Arr.length - 1) {
	                	ti1++;
	                }
	                while ( g2Arr[ti2].bag == currWord && ti2 < g2Arr.length - 1) {
	                	ti2++;
	                }
	                
	                if ( 	(ti1-i1) > maxNWordsPerGroup
	                		&& (ti2-i2) > maxNWordsPerGroup ) {
	                	i1=ti1;
	                	i2=ti2;
	                	continue;
	                }               
	        		
	        	}
	
	            // for duplicates
	            int ti2 = i2;
	            while (ti2 < g2Arr.length && g2Arr[ti2].bag == g1Arr[i1].bag) {
	                matches.add(new LocalFeatureMatch(g1Arr[i1], g2Arr[ti2]));
	                ti2++;
	            }
	
	        }
        return matches;
    }

    @Override
    public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeInt(lfArr.length);
    	
        int byteSize = lfArr.length * BoFLF.getByteSize();
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
		if ( magnitude > 0 ) return magnitude;
		long res = 0;
		if ( lfArr.length == 0 ) return 0;
		if ( lfArr.length == 1 ) return 1;
//		this.orderByBags();
		int lastBag = lfArr[0].bag;
		int currCount = 1;
		for(int i = 1; i < lfArr.length ; i++) {
			int currBag = lfArr[i].bag;
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
		//System.out.print(this.getID()+"\t");
		if ( lfArr.length == 0 ) return 0.0;
//		if ( lfArr.length == 1 ) return 1.0;
//		this.orderByBags();
		int lastBag = lfArr[0].bag;
		int lastCount = 1;
		for(int i = 1; i < lfArr.length ; i++) {
			int currBag = lfArr[i].bag;
			if ( currBag == lastBag ) {
				lastCount++; 
			} else {
				double tfidf1 =
						getTF(lastCount, lfArr.length)	 	// TF
						* idf[lastBag];						// IDF
	            res += tfidf1 * tfidf1;
	            //System.out.print("["+lastBag+", "+getTF(lastCount, lfArr.length)+"]\t");
	            lastBag = currBag;
	            lastCount = 1;
			}			
		}
		
		// last
		double tfidf1 = getTF( lastCount, lfArr.length )	// TF
						* idf[lastBag];						// IDF
		
		res += tfidf1 * tfidf1;
		//System.out.print("["+lastBag+", "+getTF( lastCount, lfArr.length )+"]\n");
		magnitude_TFIDF = Math.sqrt(res);
		return magnitude_TFIDF;
	}
	
	
	public final double getMagnitude_IDF( float[] idf ) {
		if ( magnitude_IDF > 0 ) return magnitude_IDF;
		float res = 0;
		if ( lfArr.length == 0 ) return 0.0;
		if ( lfArr.length == 1 ) return 1.0;
//		this.orderByBags();
		int lastBag = lfArr[0].bag;
		int lastCount = 1;
		for(int i = 1; i < lfArr.length ; i++) {
			int currBag = lfArr[i].bag;
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
    
	public String toString() {
		String tStr = "";
		for ( int i=0; i<lfArr.length; i++ ) {
			tStr += lfArr[i];
		}
		return tStr;
	}

    public final static double getSimilarity_cosine(BoFLFGroup g1, BoFLFGroup g2 ) {
    	return getSimilarity_cosine(g1, g2, null, false );
    }

    public final static double getSimilarity_cosine(BoFLFGroup g1, BoFLFGroup g2, float[] idf ) {
    	return getSimilarity_cosine(g1, g2, idf, false);
    }
    
	public final static double getSimilarity_cosine(BoFLFGroup g1, BoFLFGroup g2, float[] idf, boolean noTF ) {
        double num = 0;
        
        BoFLF[] g1Arr = g1.lfArr;
        BoFLF[] g2Arr = g2.lfArr;
        
        if ( g1.size() == 0 || g2.size() == 0 ) return 0;
        
        /* REMOVED!!! */
    	//double sizeRatio =  g1.size() / (float) g2.size();
    	//if ( sizeRatio < 0.1 || sizeRatio > 10.0 ) return 0.0;
        
        int i2 = 0;
        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
            int count1 = 1;
            int currBag = g1Arr[i1].bag;

            // counting occurences (TF)
            while( i1<g1Arr.length-1 && currBag == g1Arr[i1+1].bag ) {
                i1++;
                count1++;
            }

            // searching candidate i2
            while (g2Arr[i2].bag < currBag && i2 < g2Arr.length - 1) {
            	i2++;
            }

            // counting co-occurences (TF)
            int count2 = 0;
            while ( i2 < g2Arr.length && g2Arr[i2].bag == currBag ) {
                i2++;
                count2++;
            }

            if ( count2 != 0 ) {
            	if ( idf != null ) {
            		if ( noTF ) {

		                num += (double) idf[currBag] * idf[currBag];
            			
            		} else {
		                double tfidf1 = getTF(count1, g1Arr.length) 	// TF
		                				* idf[currBag];					// IDF
		
		            	double tfidf2 = getTF(count2, g2Arr.length)		// TF
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
	
	
	public final static double getSimilarity_SED(BoFLFGroup g1, BoFLFGroup g2, float[] idf ) {
        double value = 1.0;
        
        BoFLF[] g1Arr = g1.lfArr;
        BoFLF[] g2Arr = g2.lfArr;
        
        if ( g1.size() == 0 || g2.size() == 0 ) return 0;
        
        double idfSum1 = 0;
        double idfSum2 = 0;
        if ( idf != null ) {
        	for (int i1 = 0; i1 < g1Arr.length; i1++) {
        		idfSum1 += idf[g1Arr[i1].bag];
        	}
        	for (int i2 = 0; i2 < g2Arr.length; i2++) {
        		idfSum2 += idf[g2Arr[i2].bag];
        	}
        }
        /* REMOVED!!! */
    	/*double sizeRatio =  g1.size() / (float) g2.size();
    	if ( sizeRatio < 0.1 || sizeRatio > 10.0 ) return 0.0;
        */
        int i2 = 0;
        for (int i1 = 0; i1 < g1Arr.length && i2 < g2Arr.length; i1++) {
            int count1 = 1;
            int currBag = g1Arr[i1].bag;

            // counting occurences (TF)
            while( i1<g1Arr.length-1 && currBag == g1Arr[i1+1].bag ) {
                i1++;
                count1++;
            }

            // searching candidate i2
            while (g2Arr[i2].bag < currBag && i2 < g2Arr.length - 1) {
            	i2++;
            }

            // counting co-occurences (TF)
            int count2 = 0;
            while ( i2 < g2Arr.length && g2Arr[i2].bag == currBag ) {
                i2++;
                count2++;
            }

            /*
            if ( count2 != 0 ) {
            	double a;
            	double c;
            	if ( idf != null ) {
            		a = (double) count1 * idf[currBag] / idfSum1;
            		c = (double) count2 * idf[currBag] / idfSum2;
            	} else {
            		a = (double) count1 / g1Arr.length;
            		c = (double) count2 / g2Arr.length;
            	}
            	double ac = a+c;
            	value += (
            					a * Math.log(a) / Math.log(2.0)
            					+
            					c * Math.log(c) / Math.log(2.0)
            			 ) -
            			 ac * Math.log( ac/2.0) / Math.log(2.0)
            			 + ac;
            	
            }*/
            
            if ( count2 != 0 ) {
            	double a;
            	double c;
            	if ( idf != null ) {
            		a =
            				(double) count1 / idfSum1	// TF
            				* idf[currBag];				// IDF
            				
            		c = 
            				(double) count2 / idfSum2	// TF
            				* idf[currBag];				// IDF

            	} else {
            		a = (double) count1 / g1Arr.length;
            		c = (double) count2 / g2Arr.length;
            	}
            	double ac = a+c;
            	value *= Math.pow(Math.pow(a/(ac), a/(ac))*Math.pow(c/(ac), c/(ac)),ac/2.0);
            	
            	
            }

        }
        
        double result = 2.0 - 2.0 * value;
        if ( result > 1.0 ) System.out.println("Value " + result);
        if ( result < 0.0 ) System.out.println("Value " + result);
        	
        	// return 2.0 * value - 1.0;
        return 2.0 - 2.0 * value;
    }	
	
	public static float getTF( int count, int size) {
		return (float) count / size;
		//return 1.0F + (float) Math.log((float) count);
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

	
	public BoFLFGroup getAboveSize(float minSize) {
		ArrayList<BoFLF> newArr = new ArrayList<BoFLF>(lfArr.length);
		for ( int i=0; i<lfArr.length; i++ ) {
			if ( lfArr[i].getScale() >= minSize )
				newArr.add(lfArr[i]);
		}
		BoFLF[] nArr = new BoFLF[newArr.size()];
		//System.out.println("Was " + lfArr.length + " reduced to " + nArr.length);
		return new BoFLFGroup(newArr.toArray(nArr), null, linkedFC);
	}

	@Override
	public AbstractLFGroup create(BoFLF[] arr, IFeaturesCollector fc) {
		return new BoFLFGroup( arr, fWords, fc);
	}
	
    public final int getDistinctWordsCount() {
    	int count = 0;
    	Arrays.sort(lfArr);
    	
    	int last = -1;
    	for (int i = 0; i < lfArr.length; i++) {
    		if ( lfArr[i].bag!=last ) {
    			count++;
    			last = lfArr[i].bag;
    		}
    	}
    	return count;
    }

	public BoFLFGroup getReduceByBag(boolean[] bagsToRetain) {
        
        ArrayList<BoFLF> newArr = new ArrayList<BoFLF>(lfArr.length);
    	for(int ilf=0; ilf<lfArr.length; ilf++) {
    		if ( bagsToRetain[lfArr[ilf].bag]) {
        			newArr.add(lfArr[ilf]);
       		}
        }

		BoFLF[] nArr = new BoFLF[newArr.size()];
		//System.out.println("Was " + lfArr.length + " reduced to " + nArr.length);
		return new BoFLFGroup(newArr.toArray(nArr), null, linkedFC);
	}
	
	public BoFLFGroup getReduceByTF(float factor ) {
		if ( lfArr.length <= 1 ) return this;
		
		float[] eval = new float[lfArr.length];
		Arrays.fill(eval, 0);
		int lastBag = lfArr[0].bag;
		int count = 1;
		for (int i = 1; i < lfArr.length; i++) {
			if ( lfArr[i].bag == lastBag ) {
				count ++;
			} else {
				// assign count to evalutaions
				for ( int j=i-count; j<i; j++ ) {
					eval[j] = count;
				}
				count=1;
				lastBag = lfArr[i].bag;
			}
		}
		// last
		for ( int j=lfArr.length-count; j<lfArr.length; j++ ) {
			eval[j] = count;
		}	
       
        
		return getReduced_Eval(eval, factor);
	}
	
//	public BoFLFGroup getReduceByTF(float factor ) {
//		if ( lfArr.length <= 1 ) return this;
//		// last lfArr is greatest word
//		int[] tOcc = new int[lfArr[lfArr.length-1].bag+1];
//		
//		Arrays.fill(tOcc, 0);
//		
//		int last = -1;
//    	int lastCount = 0;
//    	// evaluating TFIDF
//    	for (int i = 0; i < lfArr.length; i++) {
//    		tOcc[lfArr[i].bag]++;
//    	}
//   	
//    	int[] bag = new int[tOcc.length];
//		for (int i=0; i<bag.length; i++){
//			bag[i]=i;
//		}
//		
//        //Bubble (biggest tfIDF first
//        for (int i = 0; i < tOcc.length; i++) {
//            for (int j = i; j < tOcc.length; j++) {
//                if (tOcc[i] < tOcc[j]) {
//                    int intTemp = bag[i];
//                    bag[i] = bag[j];
//                    bag[j] = intTemp;
//
//                    intTemp = tOcc[i];
//                    tOcc[i] = tOcc[j];
//                    tOcc[j] = intTemp;
//                }
//            }
//        }
//        // bag is now ordered with respect to tfIDF
//        
//        int targetSize = (int) Math.round(lfArr.length*factor);
//        boolean[] bagTrueFalse = new boolean[tOcc.length];
//        Arrays.fill(bagTrueFalse, Boolean.FALSE);
//        int tempSize = 0;
//        for( int i=0; i<tOcc.length && tempSize < targetSize; i++ ) {
//        	tempSize += tOcc[i];
//        	bagTrueFalse[bag[i]]=true;
//        }
//        
//        return getReduceByBag(bagTrueFalse);
//	}
	
	public BoFLFGroup getReducedByScale(float factor ) {
		if ( lfArr.length <= 1 ) return this;
		
		float[] eval = new float[lfArr.length];
		for ( int j=0; j<lfArr.length; j++ ) {
			eval[j] = lfArr[j].scale;
		}	
        
		return getReduced_Eval(eval, factor);
	}
	
	public BoFLFGroup getReduceByTFScale(float factor ) {
		if ( lfArr.length <= 1 ) return this;
		
		float[] eval = new float[lfArr.length];
		Arrays.fill(eval, 0);
		int lastBag = lfArr[0].bag;
		int count = 1;
		float scaleSum = lfArr[0].scale;
		for (int i = 1; i < lfArr.length; i++) {
			if ( lfArr[i].bag == lastBag ) {
				count ++;
				scaleSum += lfArr[i].scale;
			} else {
				// assign count to evalutaions
				for ( int j=i-count; j<i; j++ ) {
					eval[j] = scaleSum;
				}
				count=1;
				scaleSum = lfArr[i].scale;
				lastBag = lfArr[i].bag;
			}
		}
		// last
		for ( int j=lfArr.length-count; j<lfArr.length; j++ ) {
			eval[j] = scaleSum;
		}	
       
        
		return getReduced_Eval(eval, factor);
	}
	
	/*
	 * get reduced by factor considering eval
	 */
	public BoFLFGroup getReduced_Eval(float[] eval, float factor) {
		BoFLF[] tLFArr = (BoFLF[]) Arrays.copyOf(lfArr, lfArr.length);
    	//pre random reordering
		RandomOperations.reorderArrays(tLFArr, eval);
        //Bubble biggest eval first
        for (int i = 0; i < eval.length; i++) {
            for (int j = i; j < eval.length; j++) {
                if (eval[i] < eval[j]) {
                	BoFLF temp = tLFArr[i];
                    tLFArr[i] = tLFArr[j];
                    tLFArr[j] = temp;

                    float intTemp = eval[i];
                    eval[i] = eval[j];
                    eval[j] = intTemp;
                }
            }
        }
        // bag is now ordered with respect to eval and random
        int targetSize = (int) Math.round(lfArr.length*factor);

        // we want to avoid random selection of same eval LF
        float lastDistinctEval = eval[0];
        int lastDistinctEvalIndex = 0;
        for (int i = 1; i < eval.length; i++) {
        	if ( eval[i]==eval[0] ) {
        		lastDistinctEvalIndex=i;
        	} else {
        		break;
        	}
        }
        if ( lastDistinctEvalIndex == eval.length-1 )
        	return new BoFLFGroup( Arrays.copyOf(tLFArr, tLFArr.length), null, linkedFC);
        else {
        	float currEval = eval[lastDistinctEvalIndex+1];
        	for (int i = lastDistinctEvalIndex+2; i < eval.length && i < targetSize; i++) {
            	if ( eval[i] != currEval ) {
            		lastDistinctEval = currEval;
            		lastDistinctEvalIndex = i-1;
            		currEval = eval[i];
            	}
            }
        }
        
        return new BoFLFGroup( Arrays.copyOf(tLFArr, lastDistinctEvalIndex+1), null, linkedFC);
	}
	
	
	public BoFLFGroup getReducedByTFID(float factor, float[] idf ) {
		if ( lfArr.length <= 1 ) return this;
		
		float[] eval = new float[lfArr.length];
		Arrays.fill(eval, 0);
		int lastBag = lfArr[0].bag;
		int count = 1;
		for (int i = 1; i < lfArr.length; i++) {
			if ( lfArr[i].bag == lastBag ) {
				count ++;
			} else {
				// assign count to evalutaions
				for ( int j=i-count; j<i; j++ ) {
					eval[j] = (float) count / lfArr.length * idf[lastBag];
				}
				count=1;
				lastBag = lfArr[i].bag;
			}
		}
		// last
		for ( int j=lfArr.length-count; j<lfArr.length; j++ ) {
			eval[j] = (float) count / lfArr.length * idf[lastBag];
		}	
       
        
		return getReduced_Eval(eval, factor);
	}
	
//	public BoFLFGroup getReduceByTFID(float factor, float[] idf) {
//		float[] tfIDF = new float[idf.length];
//		int[] bagCount = new int[idf.length];
//		Arrays.fill(bagCount, 0);
//		int last = -1;
//    	int lastCount = 0;
//    	// evaluating TFIDF
//    	for (int i = 0; i < lfArr.length; i++) {
//    		bagCount[lfArr[i].bag]++;
//    		if ( lfArr[i].bag==last ) {
//    			lastCount ++;
//    		} else {
//    		
//    			if ( last > 0)
//    				tfIDF[last]=(float) lastCount / lfArr.length * idf[last];
//    			last = lfArr[i].bag;
//    			lastCount = 1;
//    		}
//    	}
//   	
//    	int[] bag = new int[idf.length];
//		for (int i=0; i<bag.length; i++){
//			bag[i]=i;
//		}
//		
//        //Bubble (biggest tfIDF first
//        for (int i = 0; i < tfIDF.length; i++) {
//            for (int j = i; j < tfIDF.length; j++) {
//                if (tfIDF[i] < tfIDF[j]) {
//                    int intTemp = bag[i];
//                    bag[i] = bag[j];
//                    bag[j] = intTemp;
//
//                    float floatTemp = tfIDF[i];
//                    tfIDF[i] = tfIDF[j];
//                    tfIDF[j] = floatTemp;
//                }
//            }
//        }
//        // bag is now ordered with respect to tfIDF
//        
//        int targetSize = (int) Math.round(lfArr.length*factor);
//        boolean[] bagTrueFalse = new boolean[tfIDF.length];
//        Arrays.fill(bagTrueFalse, Boolean.FALSE);
//        int tempSize = 0;
//        for( int i=0; i<tfIDF.length && tempSize < targetSize; i++ ) {
//        	tempSize += bagCount[bag[i]];
//        	bagTrueFalse[bag[i]]=true;
//        }
//        
//        return getReduceByBag(bagTrueFalse);
//	}
    
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
