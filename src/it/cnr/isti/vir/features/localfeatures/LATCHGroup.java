/*******************************************************************************
 * Copyright (c) 2016, Lucia Vadicamo and  Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
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
import it.cnr.isti.vir.util.RandomOperations;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LATCHGroup extends ALocalFeaturesGroup<LATCH> {

	public static final byte version = 0;
	
	public LATCHGroup(LATCH[] arr, AbstractFeaturesCollector fc) {
		super(arr, fc);
	}
	
	public LATCHGroup(LATCH[] arr ) {
		super(arr);
	}

	public LATCHGroup(AbstractFeaturesCollector fc) {
		super(fc);
	}
	
	public LATCHGroup(DataInput in) throws Exception {
		in.readByte(); // version
		int nBytes = in.readInt();
		byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
		lfArr = new LATCH[bBuffer.getInt()];
		for (int i = 0; i < lfArr.length; i++) {
			lfArr[i] = new LATCH(bBuffer);
			lfArr[i].setLinkedGroup(this);
		}
	}
	
	
	public LATCHGroup(ByteBuffer in ) throws IOException {
		byte version = in.get(); 
		int nBytes = in.getInt(); 
		int nLFs = in.getInt();
		lfArr = new LATCH[nLFs];
		for ( int i = 0; i < nLFs; i++ ) {
			lfArr[i] = new LATCH(in);
			lfArr[i].setLinkedGroup(this);
		}
	}

	@Override
	public Class<LATCH> getLocalFeatureClass() {
		return LATCH.class;
	}

	@Override
	public ALocalFeaturesGroup<LATCH> create(LATCH[] arr, AbstractFeaturesCollector fc) {
		return new LATCHGroup( arr, fc);
	}
	@Override
	public byte getSerVersion() {
		return version;
	}
	
	public LATCHGroup(BufferedReader br, AbstractFeaturesCollector fc) throws IOException {
		super(fc);
		String[] temp = br.readLine().split("(\\s)+");
		
		int n = Integer.parseInt(temp[0]);
		int len = Integer.parseInt(temp[1]);
		
		lfArr = new LATCH[n];
	    for (int i = 0; i < n; i++) {
	    	lfArr[i] = new LATCH(br);
	    	lfArr[i].setLinkedGroup(this);
	    }
	}
	
	static final public double getRangePercMatches(
			ALocalFeaturesGroup<LATCH> sg1,
			ALocalFeaturesGroup<LATCH> sg2,
			int range) {
		
		int count = 0;
		for (LATCH f: sg1.lfArr) {
			if ( hasMatchBelowRadius(f, sg2, range)) {
				count++;
			}
		}
		
		return count / (double) sg1.size();
	}
	
	/**
	 * @param s1	LATCH query feature
	 * @param sg	Group of LATCH
	 * @param range  Range
	 * @return	true if it exists at least one match below or equal the range
	 */
	static final public boolean hasMatchBelowRadius(LATCH s1, ALocalFeaturesGroup<LATCH> sg, int range) {
		for (LATCH f:sg.lfArr) {
			if ( LATCH.getDistance(s1, f) <= range) {
				return true;
			}
		}
		
		return false;
	}
	

	public static LATCHGroup getRandom() {
		LATCH[] lfArr = new LATCH[RandomOperations.getInt(1001)];
		
		return new LATCHGroup(lfArr);
	}
	

	
	static final public LATCH getLoweMatch(LATCH s1, ALocalFeaturesGroup<LATCH> sg,
			double conf, int maxD) {
		int dist1 = Integer.MAX_VALUE;
		int dist2 = Integer.MAX_VALUE;
		int dsq = 0;
		LATCH curr, best = null;
		LATCH[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = LATCH.getDistance(s1, curr);
			if (dsq < 0)
				continue;
			if (dsq < dist1) {
				dist2 = dist1;
				dist1 = dsq;
				best = curr;
			} else if (dsq < dist2) {
				dist2 = dsq;
			}
		}

		// System.out.print(bestSIFT.scale + "\t");
		// if ( bestSIFT.scale > 4 ) return null;
		if (dist1 > maxD)
			return null;
		if (dist2 == 0)
			return null;
		/*
		 * Check whether closest distance is less than ratio threshold of
		 * second.
		 */
		if ((double) dist1 / (double) dist2 < conf)
			return best;
		return null;
	}

	static final public LATCH getLoweMatch(LATCH s1, ALocalFeaturesGroup<LATCH> sg,
			double conf) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		LATCH curr, best = null;

		LATCH[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = LATCH.getDistance(s1, curr );
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}

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
	
	static final public double getLowePercMatches(ALocalFeaturesGroup<LATCH> sg1,
			ALocalFeaturesGroup<LATCH> sg2, double conf) {
		return (double) getLoweNMatches(sg1, sg2, conf) / sg1.size();
	}
	
	static final public int getLoweNMatches(ALocalFeaturesGroup<LATCH> sg1,
			ALocalFeaturesGroup<LATCH> sg2, double conf) {
		if (sg2.size() < 2)
			return 0;
		int nMatches = 0;
		LATCH[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			if (LATCHGroup.getLoweMatch(arr[i], sg2, conf) != null)
				nMatches++;
		}

		return nMatches;
	}
	
	static final public LocalFeaturesMatches getLoweMatches(ALocalFeaturesGroup<LATCH> sg1, ALocalFeaturesGroup<LATCH> sg2, double dRatioThr) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		LATCH[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			LATCH match = LATCHGroup.getLoweMatch(arr[i], sg2, dRatioThr );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}


	
	static final public LocalFeaturesMatches getLoweMatches(ALocalFeaturesGroup<LATCH> sg1, ALocalFeaturesGroup<LATCH> sg2, double dRatioThr, final int maxD) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		LATCH[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			LATCH match = LATCHGroup.getLoweMatch(arr[i], sg2, dRatioThr, maxD );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
}
