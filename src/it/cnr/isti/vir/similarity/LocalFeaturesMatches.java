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

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.geom.Box;
import it.cnr.isti.vir.geom.RSTTransformation;
import it.cnr.isti.vir.geom.AbstractTransformation;
import it.cnr.isti.vir.geom.TransformationHypothesis;
import it.cnr.isti.vir.geom.Transformations;
import it.cnr.isti.vir.util.RandomOperations;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

public class LocalFeaturesMatches {

	private long hashCode;
	
//	int nHoughMaxForRANSAC = 50;
    protected ArrayList<LocalFeatureMatch> matchesColl;

//	public LocalFeaturesMatches(Collection<LocalFeatureMatch> matches) {
//		super();
//		this.matches = matches;
//	}
    public LocalFeatureMatch get(int i) {
    	return matchesColl.get(i);
    }
    
    public ALocalFeaturesGroup getLFGroup() {
        return matchesColl.get(0).lf.getLinkedGroup();
    }

    public ALocalFeaturesGroup getMatchingLFGroup() {
        return matchesColl.get(0).lfMatching.getLinkedGroup();
    }

    public BufferedImage overPrintBox(BufferedImage img, float[][] givenBox, Color color) {

        //BufferedImage outImg = new BufferedImage( img.getWidth(), img.getHeight(), BufferedImage.TYPE_3BYTE_BGR );

        Graphics2D graphics2D = img.createGraphics();

        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.setColor(color);
        int[][] box = getIntBox(givenBox);
        graphics2D.drawLine(box[0][0], box[0][1], box[1][0], box[0][1]);
        graphics2D.drawLine(box[0][0], box[0][1], box[0][0], box[1][1]);
        graphics2D.drawLine(box[1][0], box[1][1], box[1][0], box[0][1]);
        graphics2D.drawLine(box[1][0], box[1][1], box[0][0], box[1][1]);

        graphics2D.dispose();

        return img;
    }

    public float[][] getBox() {

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = 0.0f;
        float maxY = 0.0f;
        for (int i = 0; i < matchesColl.size(); i++) {
            float[] currXY = matchesColl.get(i).getXY();
            if (currXY[0] < minX) {
                minX = currXY[0];
            }
            if (currXY[1] < minY) {
                minY = currXY[1];
            }
            if (currXY[0] > maxX) {
                maxX = currXY[0];
            }
            if (currXY[1] > maxY) {
                maxY = currXY[1];
            }
        }

        float[][] box = new float[2][2];
        box[0][0] = minX;
        box[0][1] = minY;
        box[1][0] = maxX;
        box[1][1] = maxY;

        return box;
    }

    public int[][] getIntBox(float[][] given) {
        int[][] res = new int[2][2];
        for (int i1 = 0; i1 < 2; i1++) {
            for (int i2 = 0; i2 < 2; i2++) {
                res[i1][i2] = (int) given[i1][i2];
            }
        }
        return res;
    }

    public float[][] getMatchingBox() {

        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = 0.0f;
        float maxY = 0.0f;
        for (int i = 0; i < matchesColl.size(); i++) {
            float[] currXY = matchesColl.get(i).getMatchingXY();
            if (currXY[0] < minX) {
                minX = currXY[0];
            }
            if (currXY[1] < minY) {
                minY = currXY[1];
            }
            if (currXY[0] > maxX) {
                maxX = currXY[0];
            }
            if (currXY[1] > maxY) {
                maxY = currXY[1];
            }
        }

        float[][] box = new float[2][2];
        box[0][0] = minX;
        box[0][1] = minY;
        box[1][0] = maxX;
        box[1][1] = maxY;

        return box;
    }

    public ArrayList<LocalFeatureMatch> getInBox(float[][] box) {
        ArrayList<LocalFeatureMatch> res = new ArrayList();
        for (int i = 0; i < matchesColl.size(); i++) {
            LocalFeatureMatch match = matchesColl.get(i);
            float[] currXY = match.getXY();
            if (Box.xyInBox(currXY, box)) {
            }
        }
        return res;
    }

    public ArrayList<LocalFeatureMatch> getMatchesInBox(float[][] box) {
        ArrayList<LocalFeatureMatch> res = new ArrayList();
        for (int i = 0; i < matchesColl.size(); i++) {
            LocalFeatureMatch match = matchesColl.get(i);
            float[] currXY = match.getMatchingXY();
            if (Box.xyInBox(currXY, box)) {
            }
        }
        return res;
    }

    public ArrayList<LocalFeatureMatch> getMatches() {
        return matchesColl;
    }

    public Iterator<LocalFeatureMatch> iterator() {
        return matchesColl.iterator();
    }

    public LocalFeaturesMatches(ArrayList<LocalFeatureMatch> matches) {
        matchesColl = matches;
    }

    public LocalFeaturesMatches() {
        super();
        matchesColl = new ArrayList<LocalFeatureMatch>();
    }

    public synchronized void add(LocalFeatureMatch match) {
        matchesColl.add(match);
    }

    public void filter_LoweHoughTransform() {

        if (matchesColl.size() < 3) {
            matchesColl = null;
            return;
        }

        Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(matchesColl);

        int maxSize = 0;
        LocalFeaturesMatches best = null;
        long bestHash = -1;
        for (Iterator<Entry<Long, LocalFeaturesMatches>> it = ht.entrySet().iterator(); it.hasNext();) {
            Entry<Long,LocalFeaturesMatches> curr = it.next();

            if (curr.getValue().size() > maxSize) {
                maxSize = curr.getValue().size();
                best = curr.getValue();
                bestHash = curr.getKey();
            }
        }
//		System.out.println("Best transform: ");
//		System.out.println("--- npoints: " + maxSize);
//		System.out.println("--- scale: " + LoweHoughTransform.getScaleFromHash( bestHash ));
//		System.out.println("--- ori: " 	+ LoweHoughTransform.getOriFromHash( bestHash )
//							+ "\t ("+ ( (180 / Math.PI) * LoweHoughTransform.getOriFromHash( bestHash ) ) + "�)");


//		System.out.println("--- x: " + LoweHoughTransform.getXFromHash( bestHash ));
//		System.out.println("--- y: " + LoweHoughTransform.getYFromHash( bestHash ));



        if (maxSize <= 3) {
            matchesColl = null;
        } else {
            matchesColl = best.getMatches();
        }

    }

    public final int size() {
        if (matchesColl == null) {
            return 0;
        }
        return matchesColl.size();
    }

    public BufferedImage overPrint_onQuery(BufferedImage img, int color) {
        return overPrint_onQuery(img, color, null);
    }

    public BufferedImage overPrint_onModel(BufferedImage img, int color) {
        return overPrint_onModel(img, color, null);
    }

    public BufferedImage overPrint_onQuery(BufferedImage img, int color, AbstractTransformation tr) {
        double sum = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            LocalFeatureMatch curr = it.next();
            float[] xy = null;
            if (tr == null) {
                xy = curr.getXY();
            } else {
                xy = tr.getTransformed(curr.getMatchingXY());
            }
            ALocalFeaturesGroup.overPrint_point(img, xy, color);
        }
        return img;
    }

    public BufferedImage overPrint_onModel(BufferedImage img, int color, AffineTransformation tr) {
        double sum = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            LocalFeatureMatch curr = it.next();
            float[] xy = null;
            if (tr == null) {
                xy = curr.getMatchingXY();
            } else {
                xy = tr.getInvTransformed(curr.getXY());
            }
            ALocalFeaturesGroup.overPrint_point(img, xy, color);
        }
        return img;
    }

//    public Transformation filter_RANSAC( Hashtable<Long, ArrayList<LocalFeatureMatch>> ht, int cycles, int nHoughMaxForRANSAC, double error ) {
//           ArrayList<TransformationHypothesis> ts = this.getRANSAC(ht, cycles, nHoughMaxForRANSAC, error, HomographyTransformation.class);
//           matchesColl = ts.get(0).getMatches().getMatches();
//           return ts.get(0).getTr();
//    }
//    public Transformation filter_RANSAC( int cycles, int nHoughMaxForRANSAC, double error) {
//        return filter_RANSAC( LoweHoughTransform.getLoweHoughTransforms_HT(matchesColl), cycles, nHoughMaxForRANSAC, error);
//    }
//	public Transformation filter_RANSAC( Class trClass, Hashtable<Long, ArrayList<LocalFeatureMatch>> ht, int cycles, int nHoughMaxForRANSAC, double error ) {
//		if ( matchesColl == null || matchesColl.size() < 3 ) {
//			matchesColl = null;
//			return null;
//		}
//
//		Transformation bestTr = null;
//		ArrayList<LocalFeatureMatch> bestFilteredMatches = null;
//
//		double[][] pDest = new double[3][];
//		double[][] pSrc  = new double[3][];
//
//                double errorSquare = error*error;
//
//		// ordering
//		Set<Entry<Long, ArrayList<LocalFeatureMatch>>> set = ht.entrySet();
//		ArrayList<LocalFeatureMatch>[] ordered = new ArrayList[set.size()];
//		int i=0;
//		for(Iterator<Entry<Long, ArrayList<LocalFeatureMatch>>> it = set.iterator(); it.hasNext(); i++){
//			ordered[i]= it.next().getValue();
//		}
//		CollectionSizeComparator comp = new CollectionSizeComparator();
//		Arrays.sort(ordered, comp );
//
//		// for each Hough bucket
//                for ( i=0; i<ordered.length && i<nHoughMaxForRANSAC; i++ ) {
//			// for each HoughTransform group
//			ArrayList<LocalFeatureMatch> currMatches = ordered[i];
//			//Entry<Long, ArrayList<LocalFeatureMatch>> curr = it.next();
//			//ArrayList<LocalFeatureMatch> currMatches = curr.getValue();
//
//			if ( currMatches.size() <= 3 ) break; // next ones would be <3 too
//
//			// N.B.: just currMatches!!!!!!!!!
//			if ( bestFilteredMatches != null && currMatches.size() <= bestFilteredMatches.size() ) continue;
//
//			int size = currMatches.size();
//			double total = (double) size * ( size-1.0) * (size-2.0);
//			double prob = 1.0;
//			if ( cycles < total ) {
//				prob = (double) cycles / total;
//
//				for (int count=0; count<cycles; count++ ) {
//
//					LocalFeatureMatch m1 = currMatches.get(RandomOperations.getInt(0, size-1));
//					pDest[0]  = m1.getXY();
//					pSrc[0]   = m1.getMatchingXY();
//
//					LocalFeatureMatch m2 = currMatches.get(RandomOperations.getInt(0, size-1));
//					pDest[1] = m2.getXY();
//					pSrc[1]  = m2.getMatchingXY();
//
//					LocalFeatureMatch m3 = currMatches.get(RandomOperations.getInt(0, size-1));
//					pDest[2] = m3.getXY();
//					pSrc[2]  = m3.getMatchingXY();
//
//				// | Model P | = | A | * | imgP | + | t |
//					// searching for AffineTransformation that maps imgSrc in imgDest
//					Transformation t = Transformations.getTransformation( trClass, pSrc, pDest );
//					if ( t == null ) continue;
//		//						System.out.println("Curr ori: " + t.getOri());
//					// N.B.: just currMatches!!!!!!!!!
//					ArrayList<LocalFeatureMatch> tempFiltered = currMatches;
//					if ( currMatches.size() > 3 ) tempFiltered = getFiltered_L2(currMatches, t, errorSquare);
//
//					if (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size() ) {
//						bestFilteredMatches = tempFiltered;
//						bestTr = t;
//					}
//				}
//
//			} else {
//				for ( int i1=0; i1<currMatches.size()-2; i1++) {
//					LocalFeatureMatch m1 = currMatches.get(i1);
//					pDest[0]  = m1.getXY();
//					pSrc[0] = m1.getMatchingXY();
//					for ( int i2=i1+1; i2<currMatches.size()-1; i2++) {
//						LocalFeatureMatch m2 = currMatches.get(i2);
//						pDest[1]   = m2.getXY();
//						pSrc[1] = m2.getMatchingXY();
//						for ( int i3=i2+1; i3<currMatches.size(); i3++) {
////							if ( ! RandomOperations.trueORfalse(prob) ) continue;
//
//							LocalFeatureMatch m3 = currMatches.get(i3);
//							pDest[2] = m3.getXY();
//							pSrc[2]  = m3.getMatchingXY();
//
//							// | Model P | = | A | * | imgP | + | t |
//							// searching for AffineTransformation that maps imgSrc in imgDest
//							Transformation t = Transformations.getTransformation( trClass, pSrc, pDest );
//							if ( t == null ) continue;
////							System.out.println("Curr ori: " + t.getOri());
//							// N.B.: just currMatches!!!!!!!!!
//							ArrayList<LocalFeatureMatch> tempFiltered = currMatches;
//							if ( currMatches.size() > 3 ) tempFiltered = getFiltered_L2(currMatches, t, errorSquare);
//
//							if (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size() ) {
//								bestFilteredMatches = tempFiltered;
//								bestTr = t;
//							}
//						}
//					}
//
//				}
//			}
//		}
//
//		if ( bestFilteredMatches == null || bestFilteredMatches.size() < 3 ) matchesColl = null;
//		else matchesColl = bestFilteredMatches;
//
//
//		return bestTr;
////		System.out.println("Best AffineTransformation (npoints: " + bestFilteredMatches.size() + ")");
////		System.out.println(best.toString());
//
//	}
    public int nextIDs(int[] currIDs, int nObjects) {
        int lastModified = currIDs.length - 1;
        currIDs[lastModified]++;
        if (currIDs[lastModified] == nObjects) {
            // we have to search the first to reset
            for (int i = lastModified - 1; i >= 0; i--) {
                if (currIDs[i] < nObjects - currIDs.length + i) {
                    currIDs[i]++;
                    for (int i2 = i + 1; i2 < currIDs.length; i2++) {
                        currIDs[i2] = currIDs[i2 - 1] + 1;
                    }
                    return i;
                }
            }
            return -1;
        }
        return lastModified;
    }
    
    public static ArrayList<TransformationHypothesis>
		getRANSAC(	ArrayList<TransformationHypothesis> trs,
					int cycles_deprecated,
					int nTrMaxForRANSAC,
					double error,
					Class trClass,
					boolean onlyFirst,
					LocalFeaturesMatches globalMatches ) {
    	
        int nPoints = Transformations.getNPointsForEstimation(trClass);
		int cycles = cycles_deprecated;
//        int cycles = (int) getNHypothesizes( 0.25, nPoints ) + 1;
		
        int maxNPointsFound = 0;
        if ( !onlyFirst ) {
        	maxNPointsFound = -1;
        }
        
        ArrayList<TransformationHypothesis> res = new ArrayList<TransformationHypothesis>();

//        if (matchesColl == null || matchesColl.size() <= nPoints) {
//            matchesColl = null;
//            return null;
//        }

        double errorSquare = error * error;

        float[][] pDest = new float[nPoints][];
        float[][] pSrc = new float[nPoints][];

		for (int i = 0; i < trs.size() && i < nTrMaxForRANSAC; i++) {
			
			// for each HoughTransform group
			LocalFeaturesMatches currMatches = trs.get(i).getMatches();
			if (currMatches.size() < nPoints + 1
				|| currMatches.size() < maxNPointsFound ) {
				break; // next ones would be <= nPoints too
			}

			LocalFeaturesMatches bestFilteredMatches = null;

			// counting all possible combinations
			int size = currMatches.size();
			long num = 1;
			long den = 1;
			for (int n = 0; n < nPoints; n++) {
				num *= size - n;
				den *= n + 1;
			}
			long total = num / den;

			// prob = 1.0;
			// if (cycles < total) {
			AbstractTransformation bestTr = null;

			if (cycles > total / 2) {
				int[] index = new int[nPoints];
				for (int count = 0; count < total; count++) {
					for (int ti = 0; ti < index.length; ti++) {
						LocalFeatureMatch m = currMatches.get(index[ti]);
						pSrc[ti] = m.getMatchingNormXY();
						pDest[ti] = m.getNormXY();
					}
					
					// searching for Transformation that maps imgSrc in imgDest
					AbstractTransformation t = Transformations.getTransformation(trClass, pSrc, pDest);
					if (t == null) continue;
					LocalFeaturesMatches tempFiltered = getFiltered_L2(globalMatches, t, errorSquare, maxNPointsFound);
	
					if (tempFiltered.size() > nPoints && (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size())) {
						bestFilteredMatches = tempFiltered;
						bestTr = t;
					}
				}

			} else {
				int[] index = new int[nPoints];
				for (int count = 0; count < cycles; count++) {
					boolean rejected = false;
					for (int n = 0; !rejected && n < nPoints; n++) {
						boolean duplicated;
						do {
							duplicated = false;
							index[n] = RandomOperations.getInt(0, size - 1);
							for (int ti = 0; ti < n; ti++) {
								if (index[n] == index[ti]) {
									duplicated = true;
									break;
								}
							}
						} while (duplicated);
						LocalFeatureMatch m = currMatches.get(index[n]);
						pSrc[n] = m.getMatchingNormXY();
						pDest[n] = m.getNormXY();
					}
					// searching for Transformation that maps imgSrc in imgDest
					AbstractTransformation t = Transformations.getTransformation(trClass, pSrc, pDest);
					if (t == null) continue;
					LocalFeaturesMatches tempFiltered = getFiltered_L2(globalMatches, t, errorSquare);

					if (tempFiltered.size() > nPoints && (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size())) {
						bestFilteredMatches = tempFiltered;
						bestTr = t;
					}
				}

			}
			if (bestFilteredMatches != null) {
				if ( maxNPointsFound >= 0 ) {
					// this is searching onlyl for best Hypothesis
					if ( bestFilteredMatches.size() > maxNPointsFound ) {
						maxNPointsFound = bestFilteredMatches.size();
						res.add(new TransformationHypothesis(bestTr, bestFilteredMatches));
					}
				} else {
					res.add(new TransformationHypothesis(bestTr, bestFilteredMatches));
				}
				
				if ( bestFilteredMatches.size() == currMatches.size() ) {
					// all points are inliers
					break;
				}
			}
		}


        Collections.sort(res);
        return res;

    }

    public ArrayList<TransformationHypothesis>
    	getRANSAC(	Hashtable<Long, LocalFeaturesMatches> ht,
    				int cycles_deprecated,
    				int nHoughMaxForRANSAC,
    				double error,
    				Class trClass,
    				double minDist,
    				boolean onlyFirst) {
    	
        int nPoints = Transformations.getNPointsForEstimation(trClass);
		int cycles = cycles_deprecated;
//        int cycles = (int) getNHypothesizes( 0.25, nPoints ) + 1;
		
        int maxNPointsFound = 0;
        if ( !onlyFirst ) {
        	maxNPointsFound = -1;
        }
        
        ArrayList<TransformationHypothesis> res = new ArrayList<TransformationHypothesis>();
        if (matchesColl == null || matchesColl.size() <= nPoints) {
            matchesColl = null;
            return null;
        }

        double errorSquare = error * error;

        float[][] pDest = new float[nPoints][];
        float[][] pSrc = new float[nPoints][];

        LocalFeaturesMatches[] ordered = LoweHoughTransform.orderHT(ht);

		for (int i = 0; i < ordered.length && i < nHoughMaxForRANSAC; i++) {
			
			// for each HoughTransform group
			LocalFeaturesMatches currMatches = ordered[i];
			if (currMatches.size() < nPoints
				|| currMatches.size() < maxNPointsFound ) {
				break; // next ones would be <= nPoints too
			}

			LocalFeaturesMatches bestFilteredMatches = null;

			byte scaleBin = currMatches.getScaleBinFromHash();
			byte oriBin = currMatches.getOriBinFromHash();

			// counting all possible combinations
			int size = currMatches.size();
			int num = 1;
			int den = 1;
			for (int n = 0; n < nPoints; n++) {
				num *= size - n;
				den *= n + 1;
			}
			int total = num / den;

			// prob = 1.0;
			// if (cycles < total) {
			AbstractTransformation bestTr = null;


			int actualCount = 0;

			if (cycles > total / 2) {
				int[] index = new int[nPoints];
				for (int count = 0; count < total; count++) {
					// we can check all
					boolean rejected = false;
					
					if ( count == 0 ) {
						for ( int ti=0; ti<index.length; ti++ ) {
							index[ti] = ti;
						}
						// initializing
						for (int ti = 0; ti < index.length; ti++) {
							LocalFeatureMatch m = currMatches.get(index[ti]);
							pSrc[ti] = m.getMatchingNormXY();
							pDest[ti] = m.getNormXY();
							rejected = check(pSrc, pDest, ti, minDist, scaleBin, oriBin);
						}
					} else {
	
						int lastModified = nextIDs(index, size);
						if (lastModified < 0) continue;

						for (int ti = lastModified; ti < index.length; ti++) {
							LocalFeatureMatch m = currMatches.get(index[ti]);
							pSrc[ti] = m.getMatchingNormXY();
							pDest[ti] = m.getNormXY();
							rejected = check(pSrc, pDest, ti, minDist, scaleBin, oriBin);
							if (rejected)
								break;
						}

					}
									
					
					if (rejected)
						continue;
	
					actualCount++;
					// searching for Transformation that maps imgSrc in imgDest
					AbstractTransformation t = Transformations.getTransformation(trClass, pSrc, pDest);
					if (t == null)
						continue;
					LocalFeaturesMatches tempFiltered = getFiltered_L2(currMatches, t, errorSquare, maxNPointsFound);
	
					if (tempFiltered.size() > nPoints && (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size())) {
						bestFilteredMatches = tempFiltered;
						bestTr = t;
					}
				}

			} else {
				int[] index = new int[nPoints];
				for (int count = 0; count < cycles; count++) {
					boolean rejected = false;
					for (int n = 0; !rejected && n < nPoints; n++) {
						boolean duplicated;
						do {
							duplicated = false;
							index[n] = RandomOperations.getInt(0, size - 1);
							for (int ti = 0; ti < n; ti++) {
								if (index[n] == index[ti]) {
									duplicated = true;
									break;
								}
							}
						} while (duplicated);
						LocalFeatureMatch m = currMatches.get(index[n]);
						pSrc[n] = m.getMatchingNormXY();
						pDest[n] = m.getNormXY();
						rejected = check(pSrc, pDest, n, minDist, scaleBin, oriBin);
					}
					if (rejected) continue;

					actualCount++;
					// searching for Transformation that maps imgSrc in imgDest
					AbstractTransformation t = Transformations.getTransformation(trClass, pSrc, pDest);
					if (t == null) continue;
					LocalFeaturesMatches tempFiltered = getFiltered_L2(currMatches, t, errorSquare);

					if (tempFiltered.size() > nPoints && (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size())) {
						bestFilteredMatches = tempFiltered;
						bestTr = t;
					}
				}

			}
			if (bestFilteredMatches != null) {
				if ( maxNPointsFound >= 0 ) {
					// this is searching onlyl for best Hypothesis
					if ( bestFilteredMatches.size() > maxNPointsFound ) {
						maxNPointsFound = bestFilteredMatches.size();
						res.add(new TransformationHypothesis(bestTr, bestFilteredMatches));
					}
				} else {
					res.add(new TransformationHypothesis(bestTr, bestFilteredMatches));
				}
				
				if ( bestFilteredMatches.size() == currMatches.size() ) {
					// all points are inliers
					break;
				}
			}
		}

//            } else {
//                int[] currIDs = new int[nPoints];
//                for (int n = 0; n < nPoints; n++) {
//                    currIDs[n] = n;
//                    LocalFeatureMatch m = currMatches.get(currIDs[n]);
//                    pDest[n] = m.getNormXY();
//                    pSrc[n] = m.getMatchingNormXY();
//                }
//
//                for (int count = 0; count < total; count++) {
//                    if (count > 0) {
//                        int firstChanged = nextIDs(currIDs, size);
//                        for (int n = firstChanged; n < nPoints; n++) {
//                            LocalFeatureMatch m = currMatches.get(currIDs[n]);
//                            pDest[n] = m.getNormXY();
//                            pSrc[n] = m.getMatchingNormXY();
//
//                        }
//                    }
//
//                    // | Model P | = | A | * | imgP | + | t |
//                    // searching for AffineTransformation that maps imgSrc in imgDest
//                    Transformation t = Transformations.getTransformation(trClass, pSrc, pDest);
//                    if (t == null) {
//                        continue;
//                    }
//                    // System.out.println("Curr ori: " + t.getOri());
//                    // N.B.: just currMatches!!!!!!!!!
//                    ArrayList<LocalFeatureMatch> tempFiltered = getFiltered_L2(currMatches, t, errorSquare);
//
//                    if (bestFilteredMatches == null || tempFiltered.size() > bestFilteredMatches.size()) {
//                        bestFilteredMatches = tempFiltered;
//                        bestTr = t;
//                    }
//                }
//            }

        Collections.sort(res);
        return res;
//		System.out.println("Best AffineTransformation (npoints: " + bestFilteredMatches.size() + ")");
//		System.out.println(best.toString());

    }

    private static boolean check(float[][] pSrc, float[][] pDest, int n, double minDist, byte scaleBin, byte oriBin) {
    	for (int iDup = 0; iDup < n; iDup++) {
            if (    (   Math.abs(pDest[iDup][0] - pDest[n][0]) 		< minDist
                        &&  Math.abs(pDest[iDup][1] - pDest[n][1]) 	< minDist )
                    ||
                    (   Math.abs(pSrc[iDup][0] - pSrc[n][0]) 		< minDist
                        &&  Math.abs(pSrc[iDup][1] - pSrc[n][1]) 	< minDist )
                ) {
                return true;
            }

    		double[] scaleRot = RSTTransformation.getScaleAndRot(pSrc[iDup], pSrc[n], pDest[iDup], pDest[n]);
            if ( scaleConsistency( scaleBin, scaleRot[0] ) ) return true;
            if ( oriConsistency( oriBin, scaleRot[1] ) ) return true;
        }
        return false;
	}

	private static boolean scaleConsistency(byte scaleBin, double scale ) {

		byte currScaleBin_first = LoweHoughTransform.getScaleRatioBin_firstOfTwo(scale);
		if (currScaleBin_first != scaleBin&& currScaleBin_first + 1 != scaleBin) {
			return true;
		}
		return false;
	}
    
    private static boolean oriConsistency(byte oriBin, double ori ) {
	    byte currOriBin_first = LoweHoughTransform.getOriDiffBin_firstOfTwo( ori );
	    if (    currOriBin_first != oriBin
	            && currOriBin_first+1 != oriBin
	            // in case of cycling
	            && ( oriBin != 0 || currOriBin_first != LoweHoughTransform.LHT_oriBinN )
	            ) {
			return true;
		}
		return false;
    }

	//    private final static ArrayList<LocalFeatureMatch> getFiltered_L1(ArrayList<LocalFeatureMatch> matches, Transformation t, double error) {
//        float[] xyRelDiff = new float[2];
//
//        ArrayList<LocalFeatureMatch> good = new ArrayList<LocalFeatureMatch>();
//        for (Iterator<LocalFeatureMatch> it = matches.iterator(); it.hasNext();) {
//            LocalFeatureMatch match = it.next();
//
//            // transform queryLF in trainingSpace
//            LocalFeatureMatch.getXYDiff(t.getTransformed(match.matchingxy), match.xy, xyRelDiff);
//
//            // normalizing considering source (query) max box
//            float[] norm = match.getMaxBoxWidthHeight();
//            xyRelDiff[0] = 1 / norm[0];
//            xyRelDiff[1] = 1 / norm[1];
//
//            if (Math.abs(xyRelDiff[0]) > error
//                    || Math.abs(xyRelDiff[1]) > error
//                    || Math.abs(xyRelDiff[0]) < -error
//                    || Math.abs(xyRelDiff[1]) < -error) {
//                continue;
//            }
//
//            good.add(match);
//        }
//
//        return good;
//    }
    private final static LocalFeaturesMatches getFiltered_L2(LocalFeaturesMatches matches, AbstractTransformation t, double errorSquare) {
    	return getFiltered_L2(matches, t, errorSquare, -1);
    }
    private final static LocalFeaturesMatches getFiltered_L2(LocalFeaturesMatches matches, AbstractTransformation t, double errorSquare, int minPoints) {
        float[] xyRelDiff = new float[2];
        int maxPossible = matches.size();
        ArrayList<LocalFeatureMatch> good = new ArrayList<LocalFeatureMatch>();
        for (Iterator<LocalFeatureMatch> it = matches.iterator(); it.hasNext() && maxPossible > minPoints; ) {
            LocalFeatureMatch match = it.next();

            // transform queryLF in trainingSpace
//            LocalFeatureMatch.getXYDiff(t.getTransformed(match.matchingxy), match.xy, xyRelDiff);
            LocalFeatureMatch.getXYDiff(t.getTransformed(match.matchingnxy), match.nxy, xyRelDiff);

            // Euclidean is costly
            if (xyRelDiff[0] * xyRelDiff[0] + xyRelDiff[1] * xyRelDiff[1] <= errorSquare) {
                good.add(match);
            } else {
            	maxPossible--;
            }
            
            if ( maxPossible <=  minPoints ) 
            	break;
        }

        return new LocalFeaturesMatches(good);
    }

    // | u  |		| m1	m2 |	| x |		| tx |
    // | v	|	=   | m3	m4 |	| y |	+	| ty |
    //
    //
    // | x1 y1  0  0 1 0 |	| m1 |		 		| u1 |
    // |  0  0 x1 y1 0 1 |  | m2 |				| v1 |
    // | x2 y2  0  0 1 0 |	| m2 |		= 		| u2 |
    // |  0  0 x2 y2 0 1 |  | m3 | 				| v2 |
    // | x3 y3  0  0 1 0 |	| tx |		 		| u3 |
    // |  0  0 x3 y3 0 1 |  | ty |				| v3 |
//	public void filter_LeastSquares(Class tr, double errorPerc) {
//
//		//filter_LoweHoughTransform();
//
//		if ( matchesColl == null || matchesColl.size() < 3 ) {
//			matchesColl = null;
//			return;
//		}
//
//		Transformation t = Transformations.getTransformation(tr, getQueryPointsXY(), getMatchingPointsXY() );
//
//
//		matchesColl = getFiltered_L2(matchesColl, t, errorPercSquare );
//
//		if ( matchesColl.size() < 3 ) {
//			matchesColl = null;
//		}
//
//
//	}
    public final float[][] getMatchingPointsXY() {
        float[][] res = new float[matchesColl.size()][];
        int i = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext(); i++) {
            res[i] = it.next().getMatchingXY();
        }
        return res;
    }

    public final float[][] getQueryPointsXY() {
        float[][] res = new float[matchesColl.size()][];
        int i = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext(); i++) {
            res[i] = it.next().getXY();
        }
        return res;
    }

    // get Affine transform
//	public final AffineTransformation getAffineTransformation(double[][] imgP, double[][] modelP) {
//		// in Jama solve returns exact or Least Squares solution
//		return AffineTransformation.getAffineTransformation_LeastSquares(imgP, modelP);
//	}
//	public final AffineTransformation getAffineTransformation_LeastSquares(ArrayList<LocalFeatureMatch> matches) {
//		double[][] imgP   = new double[matches.size()][];
//		double[][] modelP = new double[matches.size()][];
//		
//		int i=0;
//		for ( Iterator<LocalFeatureMatch> it = matches.iterator(); it.hasNext(); i++){ 
//			LocalFeatureMatch curr = it.next();
//			imgP[i] = curr.getXY();
//			modelP[i] = curr.getMatchingXY();
//		}
//		
//		return getAffineTransformation_LeastSquares( imgP, modelP);
//	}
    
    public double getWeightSum() {
        double sum = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            sum += it.next().getWeight();
        }
        return sum;
    }
    
    // get least squares affine transform
    public float getAvgOri() {
        double sum = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            sum += it.next().getOrientationDiff();
        }
        return (float) (sum / matchesColl.size());
    }

    public float getAvgScale() {
        double sum = 0;
        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            sum += it.next().getScaleRatio();
        }
        return (float) (sum / matchesColl.size());
    }

    public String toString() {
        String tStr = "LFMatches:\n";

        tStr += "--- size: " + matchesColl.size() + "\n";

        float ori = getAvgOri();
        tStr += "--- avgOri: " + ori + "\t"
                + ((180 / Math.PI) * ori) + "\n";

        float scale = getAvgScale();
        tStr += "--- avgScale: " + scale + "\n";

        float[] t = getAvgTrDiff(scale, ori);
        tStr += "--- avgDiff: " + t[0] + " " + t[1] + "\n";

        float[][] box = getMatchingBox();
        tStr += "--- queryMatchingBox: "
                + "(" + box[0][0] + ", " + box[0][1] + ")" + " - "
                + "(" + box[1][0] + ", " + box[1][1] + ")"
                + "\n";

        box = getBox();
        tStr += "--- trainingMatchingBox: "
                + "(" + box[0][0] + ", " + box[0][1] + ")" + " - "
                + "(" + box[1][0] + ", " + box[1][1] + ")"
                + "\n";

        return tStr;
    }

    private float[] tr(float scale, float angle, float[] xy) {
        float[] res = {
            (float) (scale * (xy[0] * Math.cos(angle) - xy[1] * Math.sin(angle))),
            (float) (scale * (xy[0] * Math.sin(angle) + xy[1] * Math.cos(angle)))
        };
        return res;
    }

    private float[] getAvgTrDiff(float scale, float angle) {
        float[] res = new float[2];

        for (Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext();) {
            LocalFeatureMatch match = it.next();
            float[] currQ = match.xy;
            float[] currM = match.matchingxy;
            float[] t = tr(scale, angle, currM);
            res[0] += currQ[0] - t[0];
            res[1] += currQ[1] - t[1];
        }

        res[0] = res[0] / (float) matchesColl.size();
        res[1] = res[1] / (float) matchesColl.size();

        return res;
    }
//	private double[][] getModMBox() {
//		double[][] res = { {Double.MAX_VALUE, Double.MAX_VALUE}, {0.0, 0.0} };
//		double[] min = res[0];
//		double[] max = res[1];
//		for(Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext(); ) {
//			double[] curr = it.next().getMatchingXY();
//			if ( curr[0] < min[0] ) min[0] = curr[0];
//			if ( curr[1] < min[1] ) min[1] = curr[1];
//			if ( curr[0] > max[0] ) max[0] = curr[0];
//			if ( curr[1] > max[1] ) max[1] = curr[1];
//		};
//
//		return res;
//	}
//
//	private double[][] getQueMBox() {
//		double[][] res = { {Double.MAX_VALUE, Double.MAX_VALUE}, {0.0, 0.0} };
//		double[] min = res[0];
//		double[] max = res[1];
//		for(Iterator<LocalFeatureMatch> it = matchesColl.iterator(); it.hasNext(); ) {
//			double[] curr = it.next().getXY();
//			if ( curr[0] < min[0] ) min[0] = curr[0];
//			if ( curr[1] < min[1] ) min[1] = curr[1];
//			if ( curr[0] > max[0] ) max[0] = curr[0];
//			if ( curr[1] > max[1] ) max[1] = curr[1];
//		};
//
//		return res;
//
//	}

	public void setHashCode(long hashCode) {
		this.hashCode = hashCode;
	}
	
	public long getHashCode() {
		return hashCode;
	}

	public byte getScaleBinFromHash() {
		return LoweHoughTransform.getScaleBinFromHash(hashCode);
	}

	public byte getOriBinFromHash() {
		return LoweHoughTransform.getOriBinFromHash(hashCode);
	}
	
	/* N to assure probability p of at least one sample (containing s points) being all inliers.
	 * "pg" is probability that point is an inlier
	 * Typically p = 0.99 but can be dynamically adjusted
	 */
	public double getNHypothesizes(double pg, int reqNPoints, double desiredProb) {
		return 	Math.log(1 - pg) / Math.log( 1 - Math.pow( pg , reqNPoints) );
	}
	public double getNHypothesizes(double pg, int reqNPoint) {
		return getNHypothesizes(pg, reqNPoint, 0.99);
	}
	public double getNHypothesizes_fast(double pg, int reqNPoints) {
		return Math.pow( 1 / pg, reqNPoints);
	}
	
}
