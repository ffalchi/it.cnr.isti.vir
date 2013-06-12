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

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SIFTGroup extends ALocalFeaturesGroup<SIFT> {

	public static final byte version = 2;
	
	public final byte getSerVersion() {
		return version;
	}
	
	public SIFTGroup(SIFT[] arr) {
		super(arr);
	}
	
	public SIFTGroup(SIFT[] arr, AbstractFeaturesCollector fc) {
		super(arr, fc);
	}

	public SIFTGroup(AbstractFeaturesCollector fc) {
		super(fc);
	}

	public SIFTGroup(DataInput in) throws Exception {
		this(in, null);
	}

	
	public SIFTGroup(ByteBuffer in) throws IOException {
		byte version = in.get();
		if ( version >= 2 ) {
			// VERSION 2
			int nBytes = in.getInt();
			int nLFs = in.getInt();
			lfArr = new SIFT[nLFs];
			for ( int i = 0; i < nLFs; i++ ) {
				lfArr[i] = new SIFT(in);
				lfArr[i].setLinkedGroup(this);
			}
			
		} else {		
			// VERSION 0 and 1
			int size = in.getInt();
			lfArr = new SIFT[size];
			if (size == 0)
				return;

			for (int i = 0; i < size; i++) {
				lfArr[i] = SIFT.read_old(in);
				lfArr[i].setLinkedGroup(this);
			}

			if (version > 0) {
				// VERSION 1
				readEval(in);
			}
		}
		
	}

	public SIFTGroup(DataInput in, AbstractFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.readByte();
		if ( version >= 2 ) {
			// VERSION 2
			int nBytes = in.readInt();
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
			lfArr = new SIFT[bBuffer.getInt()];
			for (int i = 0; i < lfArr.length; i++) {
				this.lfArr[i] = new SIFT(bBuffer);
				lfArr[i].setLinkedGroup(this);
			}
		} else {
			// VERSION 0 and 1
			int size = in.readInt();
	
			lfArr = SIFT.getSIFT_old(in, size, this);
	
			if (version > 0) {
				readEval(in);
			}
		}
	}
	
	/*
	 * For parsing text files.
	 * Read keypoints from the given file pointer and return the list of
	 * keypoints. The file format starts with 2 integers giving the total number
	 * of keypoints and the size of descriptor vector for each keypoint
	 * (currently assumed to be 128). Then each keypoint is specified by ...
	 */
	public SIFTGroup(BufferedReader br, AbstractFeaturesCollector fc) throws IOException {
		super(fc);
		String[] temp = br.readLine().split("(\\s)+");
		// assert(temp.length == 2);
		int n = Integer.parseInt(temp[0]);
		int len = Integer.parseInt(temp[1]);
		// assert(len == SIFT.vLen);

		this.lfArr = new SIFT[n];
		for (int i = 0; i < n; i++) {
			this.lfArr[i] = new SIFT(br);
			lfArr[i].setLinkedGroup(this);
		}
	}


	public static final double getMinDistance(SIFTGroup lFGroup1,
			SIFTGroup lFGroup2) {
		double min = Double.MAX_VALUE;
		for (int i1 = 0; i1 < lFGroup1.lfArr.length; i1++) {
			SIFT s1 = lFGroup1.lfArr[i1];
			for (int i2 = 0; i2 < lFGroup2.lfArr.length; i2++) {
				SIFT s2 = lFGroup2.lfArr[i2];
				double currDist = SIFT.getL2SquaredDistance(s1, s2);
				if (currDist < min)
					min = currDist;
			}
		}
		return min;
	}
	
	/*
	public boolean checkByteFormatConsistency() {
		byte[] bytes = SIFT.getBytes(lfArr);
		SIFT[] sift = SIFT.getSIFT(bytes, this);
		for (int i = 0; i < lfArr.length; i++) {
			if (!lfArr[i].equals(sift[i])) {
				return false;
			}
		}
		return true;
	}
*/

	static final public double getLoweFactor_avg(ALocalFeaturesGroup<SIFT> sg1,
			ALocalFeaturesGroup<SIFT> sg2) {
		if (sg2.size() < 2)
			return 0;
		double sum = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			sum += SIFTGroup.getLoweFactor(arr[i], sg2);
		}

		return (double) sum / sg1.size();
	}

	static final public double getLoweFactor(SIFT s1, ALocalFeaturesGroup<SIFT> sg) {
		double distsq1 = Integer.MAX_VALUE;
		double distsq2 = Integer.MAX_VALUE;
		double dsq = 0;
		SIFT curr, best = null;
		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getL2SquaredDistance(s1, curr);
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}
		if (distsq2 == 0)
			return 1;
		return Math.sqrt(distsq1 / distsq2);
	}

	static final public double getLowePercMatches(ALocalFeaturesGroup<SIFT> sg1,
			ALocalFeaturesGroup<SIFT> sg2, double conf) {
		return (double) getLoweNMatches(sg1, sg2, conf) / sg1.size();
	}

	static final public int getLoweNMatches(ALocalFeaturesGroup<SIFT> sg1,
			ALocalFeaturesGroup<SIFT> sg2, double conf) {
		if (sg2.size() < 2)
			return 0;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			if (SIFTGroup.getLoweMatch(arr[i], sg2, conf) != null)
				nMatches++;
		}

		return nMatches;
	}

	// static final public LocalFeaturesMatches
	// getLoweMatches(AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
	// double dRatioThr) {
	// LocalFeaturesMatches matches = new LocalFeaturesMatches();
	// if ( sg2.size() < 2 ) return null;
	// int nMatches = 0;
	// SIFT[] arr = sg1.lfArr;
	// for (int i=0; i<arr.length; i++ ) {
	// SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr );
	// if ( match != null)
	// matches.add( new LocalFeatureMatch( arr[i], match ) );
	// }
	//
	// return matches;
	// }

	static final public LocalFeaturesMatches getLoweMatches(
			ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2) {
		return getLoweMatches(sg1, sg2, 0.8);
	}
	
	static final public LocalFeaturesMatches getLoweMatches(ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2, double dRatioThr) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}	
	
	static final public LocalFeaturesMatches getLoweMatches(ALocalFeaturesGroup<SIFT> sg1, ALocalFeaturesGroup<SIFT> sg2, double dRatioThr, final int maxLFDistSq) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr, maxLFDistSq );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}	
	
/*
	static final public LocalFeaturesMatches getLoweMatches(
			AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
			double dRatioThr, final int maxLFDistSq) {

		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if (sg2.size() < 2)	return null;
		int nMatches = 0;

		final AbstractLFGroup<SIFT> sg2Final = sg2;
		final double dRatioThrFinal = dRatioThr;
		final SIFT[] arr = sg1.lfArr;

		// For parallel
		final int size = arr.length;
		final int nObjPerThread = (int) Math.ceil(size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO < size; iO += nObjPerThread) {
			arrList.add(iO);
		}

		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer p) {
				int max = p + nObjPerThread;
				if (max > size)
					max = size;

				for (int i = p; i < max; i++) {
					SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2Final,
							dRatioThrFinal, maxLFDistSq);
					if (match != null)
						matches.add(new LocalFeatureMatch(arr[i], match));
				}

				return null;
			}
		});

		return matches;
	}
	
*/
	
	
/*
	static final public LocalFeaturesMatches getLoweMatches(
			AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
			double dRatioThr) {
		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if (sg2.size() < 2)
			return null;
		int nMatches = 0;

		final AbstractLFGroup<SIFT> sg2Final = sg2;
		final double dRatioThrFinal = dRatioThr;
		final SIFT[] arr = sg1.lfArr;

		// For parallel
		final int size = arr.length;
		final int nObjPerThread = (int) Math.ceil(size
				/ ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO < size; iO += nObjPerThread) {
			arrList.add(iO);
		}

		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer p) {
				int max = p + nObjPerThread;
				if (max > size)
					max = size;

				for (int i = p; i < max; i++) {
					SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2Final,
							dRatioThrFinal);
					if (match != null)
						matches.add(new LocalFeatureMatch(arr[i], match));
				}

				return null;
			}
		});

		return matches;
	}
*/
	static final public SIFT getLoweMatch(SIFT s1, ALocalFeaturesGroup<SIFT> sg,
			double conf, int maxFDsq) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		SIFT curr, best = null;
		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getL2SquaredDistance(s1, curr, distsq2);
			if (dsq < 0)
				continue;
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}

		// System.out.print(bestSIFT.scale + "\t");
		// if ( bestSIFT.scale > 4 ) return null;
		if (distsq1 > maxFDsq)
			return null;
		if (distsq2 == 0)
			return null;
		/*
		 * Check whether closest distance is less than ratio threshold of
		 * second.
		 */
		if ((double) distsq1 / (double) distsq2 < conf)
			return best;
		return null;
	}

	static final public SIFT getLoweMatch(SIFT s1, ALocalFeaturesGroup<SIFT> sg,
			double conf) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		SIFT curr, best = null;

		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getL2SquaredDistance(s1, curr, distsq2);
			if (dsq < 0)
				continue;
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}

		// System.out.print(bestSIFT.scale + "\t");
		// if ( bestSIFT.scale > 4 ) return null;
		if (distsq2 == 0)
			return null;
		/*
		 * Check whether closest distance is less than ratio threshold of
		 * second.
		 */
		if ((double) distsq1 / (double) distsq2 < conf)
			return best;
		return null;
	}


	@Override
	public Class getLocalFeatureClass() {
		return SIFT.class;
	}


	@Override
	public ALocalFeaturesGroup create(SIFT[] arr, AbstractFeaturesCollector fc) {
		return new SIFTGroup( arr, fc);
	}
}
