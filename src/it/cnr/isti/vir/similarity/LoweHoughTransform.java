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
package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.util.LocalFeaturesMatchesSizeComparator;
import it.cnr.isti.vir.util.LocalFeaturesMatchesWeightsSumComparator;
import it.cnr.isti.vir.util.Trigonometry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class LoweHoughTransform {

	//private final static int hashCodeSpearator  	= (int) Math.pow(2, 10);
	private final static int hashCodeBitsPerComponent 	= 10;
	
	private final static int binMax =  ( (int) Math.pow(2, hashCodeBitsPerComponent) ) -1;
	
	private final static long scaleHashNShift	= 0;
	private final static long oriHashNShift		= hashCodeBitsPerComponent;
	private final static long xHashNShift		= hashCodeBitsPerComponent + oriHashNShift;
	private final static long yHashNShift		= hashCodeBitsPerComponent + xHashNShift;
	
//	private final static long oriHashMultiplier 	= hashCodeSpearator;
//	private final static long xHashMultiplier 		= hashCodeSpearator * oriHashMultiplier;
//	private final static long yHashMultiplier 		= hashCodeSpearator * xHashMultiplier;
	
	
	private final static int LHT_oriBinZero		= 6; 
	public  final static int LHT_oriBinN 		= 2*LHT_oriBinZero;
	private final static int scaleLogRatioMin 	= -64;
	private final static int minRelativeCoordDiff   = binMax / 2;
	
	
	private final static double log2 = Math.log(2);
	
	public final static double LHT_oriBinSize      = 2 * java.lang.Math.PI / LHT_oriBinN; 	// bin size for orientation
	public final static double LHT_scaleLogBinSize = 1.0;
	public final static double LHT_locationBinSize = 1.0; // 0.5; //0.25;

	// maxBinNumbers = 2^5-1
	
//	private final static int LHT_scaleLogBinN	= -2*scaleLogRatioMin;
//	private final static int LHT_locationBinN 	= x;

	
//	private final int hashCode;

		
//		t iScaleRatio = 	getScaleRatioBin(scaleRatio);
		//(int) Math.round( ( scaleLogRatio - scaleLogRatioMin ) / LHT_scaleLogBinSize );
		

	
	public final int getHashCodesLength() {
		return 16;
	}
	
	public static Hashtable<Long, LocalFeaturesMatches> getLoweHoughTransforms_HT( Collection<LocalFeatureMatch> matches ) {
		return getLoweHoughTransforms_HT(matches, false);
	}
	
	public static Hashtable<Long, LocalFeaturesMatches> getLoweHoughTransforms_HT( Collection<LocalFeatureMatch> matches, boolean considerCoordinates ) {
		return getLoweHoughTransforms_HT(matches, false, null);
	}
	
	public static Hashtable<Long, LocalFeaturesMatches> getLoweHoughTransforms_HT( Collection<LocalFeatureMatch> matches, boolean considerCoordinates, double[] minMaxScaleRatio ) {

        if ( matches.isEmpty() ) return null;
        boolean considerScale = true;
        boolean considerOrientation = true;
					
		Hashtable<Long, LocalFeaturesMatches> ht = new Hashtable<Long, LocalFeaturesMatches>();
		
//		LocalFeatureMatch first = matches.iterator().next();
//		float[] boxWidthHeight = first.getMatchingMaxBoxWidthHeight();
 		
		float[] mappedXY = new float[2];
		
		for ( Iterator<LocalFeatureMatch> it = matches.iterator(); it.hasNext(); ) {
			LocalFeatureMatch match = it.next();
			
			float oriDiff      		= match.getOrientationDiff();
			float normScaleRatio   	= match.getNormScaleRatio();
			
			if ( 	minMaxScaleRatio != null 
					&& ( normScaleRatio < minMaxScaleRatio[0] || normScaleRatio > minMaxScaleRatio[1] ) )
					  continue;
							
			int iOriDiff 	=  	getOriDiffBin(oriDiff);			
			int iScaleRatio = 	getScaleRatioBin(normScaleRatio);
			//(int) Math.round( ( scaleLogRatio - scaleLogRatioMin ) / LHT_scaleLogBinSize );
			
			// Consistency checks!!!			
			assert ( iOriDiff    >= 0	|| iOriDiff 	< binMax); // 	System.err.println("ERROR! oriDiff: " + oriDiff);
			assert ( iScaleRatio >= 1	|| iScaleRatio  < binMax); //	System.err.println("ERROR! scaleRatio: " + match.getScaleRatio());

			// double assignment
			for ( int i1=0; i1<=1; i1++) {
				int iS = iScaleRatio + i1;					
				
				for ( int i2=0; i2<=1; i2++) {
					int iO = iOriDiff + i2;
					if ( iO == LHT_oriBinN ) iO = 0; // cycling
//					if ( iO ==  LHT_oriBinN ) iO = 0; 
										
					if ( considerCoordinates ) {
						// CORDINATES
						match.getMatchingNXY_mapped(mappedXY, normScaleRatio, oriDiff);
						
//						double relXDiff = Math.abs(match.xy[0]-mappedXY[0]) / boxWidthHeight[0];
//						double relYDiff = Math.abs(match.xy[1]-mappedXY[1]) / boxWidthHeight[1];
						
//						// xy are already normalized
						float[] nxy = match.getNormXY();
						double relXDiff = (nxy[0]-mappedXY[0]);
						double relYDiff = (nxy[1]-mappedXY[1]);
						
						int iXDiff = (int) Math.round( ( relXDiff + minRelativeCoordDiff) / LHT_locationBinSize );
						int iYDiff = (int) Math.round( ( relYDiff + minRelativeCoordDiff) / LHT_locationBinSize );

						// Consistency checks!!!
						assert ( iXDiff 	>= 0     &&  iXDiff	<= binMax);
//                                                {
//							System.err.println("ERROR! iXDiff: " + iXDiff);
//							match.lfMatching.getNormXY();
//							continue;
//						}
						assert ( iYDiff 	>= 0     && iYDiff	<= binMax );
//                                                {
//							System.err.println("ERROR! iYDiff: " + iYDiff);
//							continue;
//						}
						
						for ( int i3=-1; i3<=0; i3++) {
							int iX = iXDiff + i3;
							
							for ( int i4=-1; i4<=0; i4++) {
								int iY = iYDiff + i4;
								
								int oriToHash = 0;
								int scaleToHash = 0;
								if ( considerOrientation ) oriToHash = iO;
								if ( considerScale ) scaleToHash = iS;
								long hashCode = getHash(iX, iY, oriToHash, scaleToHash);
								
//								long hashCode = iX * xHashMultiplier 	+ iY * yHashMultiplier;
//								if ( considerOrientation ) hashCode += iO * oriHashMultiplier;
//								if ( considerScale ) hashCode += iS;
								
								// adding
								LocalFeaturesMatches currColl = ht.get(hashCode);
								if ( currColl != null ) {
									currColl.add(match);
								}
								else {
									currColl = new LocalFeaturesMatches();
									currColl.setHashCode(hashCode);
									currColl.add(match);
									ht.put(hashCode, currColl);
								}
								
							}
						}
					} else {
						int oriToHash = 0;
						int scaleToHash = 0;
						if ( considerOrientation ) oriToHash = iO;
						if ( considerScale ) scaleToHash = iS;
						long hashCode = getHash(0, 0, oriToHash, scaleToHash);
						
						// adding
						LocalFeaturesMatches currColl = ht.get(hashCode);
						if ( currColl != null ) {
							currColl.add(match);
						}
						else {
							currColl = new LocalFeaturesMatches();
							currColl.setHashCode(hashCode);
							currColl.add(match);
							ht.put(hashCode, currColl);
						}
					}
				}
			}		
			
		}
		
		return ht;
	}
	
	public static LocalFeaturesMatches[] orderHT( Hashtable<Long, LocalFeaturesMatches> ht, Comparator<LocalFeaturesMatches> comp ) {
        // ordering
        Set<Entry<Long, LocalFeaturesMatches>> set = ht.entrySet();
        LocalFeaturesMatches[] ordered = new LocalFeaturesMatches[set.size()];
        int i = 0;
        for (Iterator<Entry<Long, LocalFeaturesMatches>> it = set.iterator(); it.hasNext(); i++) {
            ordered[i] = it.next().getValue();
        }
        Arrays.sort(ordered, comp);

        return ordered;
	}
	
	public static LocalFeaturesMatches[] orderHT( Hashtable<Long, LocalFeaturesMatches> ht ) {
		return orderHT( ht, new LocalFeaturesMatchesSizeComparator() );
	}	
	
	public static LocalFeaturesMatches[] orderHT_weightsSum( Hashtable<Long, LocalFeaturesMatches> ht ) {
		return orderHT( ht, new LocalFeaturesMatchesWeightsSumComparator() );
	}
	
	public static long getHash(int iX, int  iY, int iO, int iS) {
		long hashCode = iX << hashCodeBitsPerComponent;
		hashCode = ( hashCode << hashCodeBitsPerComponent ) + iY;
		hashCode = ( hashCode << hashCodeBitsPerComponent ) + iO;
		hashCode = ( hashCode << hashCodeBitsPerComponent ) + iS;
		return hashCode;
	}
	
	
	public static double getScaleRatioBin_double( double scaleRatio ) {
		return ((Math.log(scaleRatio)/ log2 ) - scaleLogRatioMin) / LHT_scaleLogBinSize;
	}
	
	public static byte getScaleRatioBin( double scaleRatio ) {
		return (byte) Math.round( getScaleRatioBin_double(scaleRatio) );
	}
	
	public static byte getScaleRatioBin_firstOfTwo( double scaleRatio ) {
		// Please note that scaleRatio always positive
		return (byte) getScaleRatioBin_double(scaleRatio);
//		return (byte) Math.floor( getScaleRatioBin_double(scaleRatio) );
	}

    public static byte getScaleBinFromHash(long hash) {
		return (byte) ( ( hash >>> scaleHashNShift ) & (long) binMax );
	}

	public static double getScaleFromHash(long hash) {
		return 	Math.pow(2, getScaleBinFromHash(hash) * LHT_scaleLogBinSize + scaleLogRatioMin) ;
	}
	
	public static double[] getScaleMinMaxFromHash(long hash) {
		double avg = getScaleFromHash(hash);
		double[] res = new double[2];
		res[0] = avg - 0.5*LHT_scaleLogBinSize;
		res[1] = avg + 0.5*LHT_scaleLogBinSize;
		return res;
	}
	
	public static double getOriDiffBin_double(double ori ) {
		return  Trigonometry.getStdRadian(ori) / LHT_oriBinSize;
	}
	
	public static byte getOriDiffBin( double ori ) {
		return (byte) Math.round(getOriDiffBin_double(ori));
	}	
	
	public static byte getOriDiffBin_firstOfTwo( double ori ) {
		byte res = (byte) Math.floor( getOriDiffBin_double(ori));
		if ( res <  0 ) res = (byte) (LHT_oriBinSize-1); // cycling
		assert( res < LHT_oriBinSize );
		return res;		
	}
	
	public static byte getOriBinFromHash(long hash) {
		return (byte) ((hash >>> oriHashNShift) & (long) binMax);
	}

	public static double getOriFromHash(long hash) {		
		return 	getOriBinFromHash(hash) * LHT_oriBinSize;
	}
	
	public static double[] getOriMinMaxFromHash(long hash) {		
		double avg = getOriFromHash(hash);
		double[] res = new double[2];
		res[0] = avg - 0.5*LHT_oriBinSize;
		res[1] = avg + 0.5*LHT_oriBinSize;
		return res;
	}
	
	public static double getXFromHash(long hash) {		
		return ( ( hash >>> xHashNShift ) & (long) binMax ) * LHT_locationBinSize;
	}
	
	public static double getYFromHash(long hash) {		
		return ( ( hash >>> yHashNShift ) & (long) binMax ) * LHT_locationBinSize;
	}
	
}

