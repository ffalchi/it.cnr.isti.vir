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
package it.cnr.isti.vir.util;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.similarity.ISimilarity;

public class SimilarityUtil {

	public static double getMinInterDistance(IFeature[] fc, ISimilarity sim) {
		double minInterDist = Double.MAX_VALUE;
		for(int i1=0; i1<fc.length;i1++) {
			for(int i2=0; i2<fc.length;i2++) {
				if ( i1==i2 ) continue;
				double dist = sim.distance(fc[i1], fc[i2], minInterDist);
				if ( dist>=0 && dist<minInterDist) {
					minInterDist = dist;
				}
			}
		}
		return minInterDist;
	}
	
	public static double getAvgInterDistance(IFeature[] fc, ISimilarity sim) {
		double sumInterDist = 0.0;
		for(int i1=0; i1<fc.length;i1++) {
			for(int i2=0; i2<fc.length;i2++) {
				sumInterDist += sim.distance(fc[i1], fc[i2]);
			}
		}
		return sumInterDist / (fc.length*fc.length);
	}

}
