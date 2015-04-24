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
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import it.cnr.isti.vir.util.math.Mean;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class FloatsLFGroup extends ALocalFeaturesGroup<FloatsLF> {

	public static final byte version = 1;
	
	public final byte getSerVersion() {
		return version;
	}
	
	public FloatsLFGroup(FloatsLF[] arr) {
		super(arr);
	}
	
	public FloatsLFGroup(FloatsLF[] arr, AbstractFeaturesCollector fc) {
		super(arr, fc);
	}

	public FloatsLFGroup(AbstractFeaturesCollector fc) {
		super(fc);
	}

	public FloatsLFGroup(DataInput in) throws Exception {
		this(in, null);
	}

	
	public FloatsLFGroup(ByteBuffer in) throws IOException {
		byte version = in.get();
		
		int nBytes = in.getInt();
		int nLFs = in.getInt();
		lfArr = new FloatsLF[nLFs];
		for ( int i = 0; i < nLFs; i++ ) {
			lfArr[i] = new FloatsLF(in);
			lfArr[i].setLinkedGroup(this);
		}		
		
	}

	public FloatsLFGroup(DataInput in, AbstractFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.readByte();


		int nBytes = in.readInt();
		byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
		lfArr = new FloatsLF[bBuffer.getInt()];
		for (int i = 0; i < lfArr.length; i++) {
			this.lfArr[i] = new FloatsLF(bBuffer);
			lfArr[i].setLinkedGroup(this);
		}

	}
	
	
	static final public FloatsLF getL2NN(FloatsLF q, ALocalFeaturesGroup<FloatsLF> g, double sqMaxDist) {
		FloatsLF res = null;
		double resDist = Double.MAX_VALUE;
		for ( FloatsLF curr : g.lfArr ) {
			double sqD = L2.getSquared(q.values, curr.values);
			if ( sqD < sqMaxDist && sqD < resDist ) {
				res = curr;
				resDist = sqD;
			}
		}
		
		return res;
	}
	
	static final public LocalFeaturesMatches getL2NN(ALocalFeaturesGroup<FloatsLF> g1, ALocalFeaturesGroup<FloatsLF> g2, double sqMaxDist) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( g2.size() == 0 ) return null;
		int nMatches = 0;
		for ( FloatsLF curr : g1.lfArr ) {
			FloatsLF match = FloatsLFGroup.getL2NN(curr, g2, sqMaxDist );
			if ( match != null)
				matches.add( new LocalFeatureMatch( curr, match ) );
		}
		
		return matches;
	}

	@Override
	public Class getLocalFeatureClass() {
		return FloatsLF.class;
	}

	@Override
	public ALocalFeaturesGroup create(FloatsLF[] arr, AbstractFeaturesCollector fc) {
		return new FloatsLFGroup( arr, fc);
	}
	
	public static FloatsLF getMean(Collection<FloatsLF> coll) {
		if ( coll.size() == 0 ) return null;
		float[][] v = new float[coll.size()][];
		int i=0;
		for ( Iterator<FloatsLF> it = coll.iterator(); it.hasNext(); ) {
			v[i++] = it.next().values;
		}
				
		return new FloatsLF(Mean.getMean(v));		
	}
}
