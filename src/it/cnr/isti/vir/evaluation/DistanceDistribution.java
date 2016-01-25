/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi and Fabio Carrara(NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.string.Decimals;

/**
 * Wrapper for good and bad distance distributions 
 * @author Fabio Carrara <fabio.carrara@isti.cnr.it>
 */
public class DistanceDistribution {
	double[] distances;
	double[] goodP;
	double[] badP;

	public DistanceDistribution(double[] distances, double[] goodP, double[] badP) {
		this.distances = distances;
		this.goodP = goodP;
		this.badP = badP;
	}

	/**
	 * Calculates the distance distributions for good and bad results.
	 * @param res results of one or more queries
	 * @param groundtruth the groundtruth for each query 
	 * @param nbins the number of equally spaced bins used to calculate the distribution
	 * @return a {@link DistanceDistribution} object
	 */
	public static DistanceDistribution getGoodBadDistanceDistrubution(SimilarityResults[] res,
			HashMap<AbstractID, ArrayList<AbstractID>> groundtruth, int nbins) {
		
		ArrayList<Double> goodResDist = new ArrayList<Double>();
		ArrayList<Double> badResDist = new ArrayList<Double>();
		
		for ( SimilarityResults<?> r : res ) {
			// get query
			AbstractID qID = r.getQueryID();
			// get objects really similar to the query from groundtruth
			ArrayList<AbstractID> gtRes = groundtruth.get(qID);
			HashSet<AbstractID> gtRes_HS = new HashSet<AbstractID>(gtRes);
			// for each result for the query...
			for ( ObjectWithDistance<?> od : r ) {
				AbstractID cID = ((IHasID) od.getObj()).getID();
				if ( qID.equals(cID)) continue;
				// ... add its distance to good distances if object is in the groundtruth for the query ...
				if ( gtRes_HS.contains(cID) ) {
					goodResDist.add( od.dist );
				// ... otherwise add it to bad distances
				} else {
					badResDist.add(od.dist);
					if ( od.dist < 0.01 ) {
						Log.info(cID + " has distance " + od.dist + " from " + qID );
					}
				}
			}
		}
		
		Collections.sort(goodResDist);
		Collections.sort(badResDist);
		Iterator<Double> itGood = goodResDist.iterator();
		Iterator<Double> itBad  = badResDist.iterator();
		Double currGoodD = null;
		Double currBadD = null;
		long goodCount = 0;
		long badCount = 0;
		
		List<Double> distances = new LinkedList<Double>();
		List<Double> goodP = new LinkedList<Double>();
		List<Double> badP = new LinkedList<Double>();
		
		double min = Math.min(goodResDist.get(0), badResDist.get(0));
		double max = Math.max(goodResDist.get(goodResDist.size()-1), badResDist.get(goodResDist.size()-1));
		
		double step = (max - min) / nbins;
		
		for ( double currD = 0; itGood.hasNext() || itBad.hasNext() || currGoodD != null || currBadD != null; ) {
			
			if ( currGoodD != null && currGoodD <= currD ) {
				goodCount++;
				currGoodD = null;
			}
			if ( currBadD != null && currBadD <= currD ) {
				badCount++;
				currBadD = null;
			}
			
			while ( currGoodD == null && itGood.hasNext() ) {
				currGoodD = itGood.next();
				if ( currGoodD <= currD ) {
					goodCount++;
					currGoodD = null;
				}
			}
			while ( currBadD == null && itBad.hasNext() ) {
				currBadD = itBad.next();
				if ( currBadD <= currD ) {
					badCount++;
					currBadD = null;
				}
			}
			
			distances.add(currD);
			goodP.add(goodCount / (double) goodResDist.size());
			badP.add(badCount / (double) badResDist.size());
			
			if ( currGoodD != null )
				currD = currGoodD;
			else {				
				currD += step;
			}
		}
		
		double[] d = new double[distances.size()];
		double[] g = new double[distances.size()];
		double[] b = new double[distances.size()];
		
		Iterator<Double> itDist = distances.iterator();
		itGood = goodP.iterator();
		itBad = badP.iterator();
		int i = 0;
		while (itDist.hasNext()) {
			d[i] = itDist.next();
			g[i] = itGood.next();
			b[i] = itBad.next();
			i++;
		}
		
		return new DistanceDistribution(d,g,b);
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < distances.length; i++) {
			str.append(distances[i]);
			str.append("\t");
			str.append(goodP[i]);
			str.append("\t");
			str.append(badP[i]);
			str.append("\n");
		}
		return str.toString();
	}
}
