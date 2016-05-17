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
package it.cnr.isti.vir.evaluation;

import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.util.math.Mean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Intersection {

	public static class CustomComparator implements Comparator<ISimilarityResults> {
	    @Override
	    public int compare(ISimilarityResults o1, ISimilarityResults o2) {
	    	AbstractID id1 = null;
	    	AbstractID id2 = null;
	    	if ( o1.getQuery() instanceof AbstractID ) id1 = (AbstractID) o1.getQuery();
	    	else id1 = ((IHasID) o1.getQuery()).getID();
	    	if ( o2.getQuery() instanceof AbstractID ) id2 = (AbstractID) o2.getQuery();
	    	else id2 = ((IHasID) o2.getQuery()).getID();
	        return (id1).compareTo(id2);
	    }
	}
	
	public static double getNormalized(ArrayList<ISimilarityResults> res1, ArrayList<ISimilarityResults> res2 ) throws Exception {
		return getNormalized(res1, res2, res1.size(), res2.size());
	}
	
	public static double getNormalized(ArrayList<ISimilarityResults> res1, ArrayList<ISimilarityResults> res2, int kRes1, int kRes2 ) throws Exception {
	
		return Mean.getMean(getNormalized_all(res1, res2, kRes1, kRes2));
	}
	

	
	public static double[] getNormalized_all(ArrayList<ISimilarityResults> res1, ArrayList<ISimilarityResults> res2, int kRes1, int kRes2 ) throws Exception {
		double[] res = new double[res1.size()];
		if ( res1.size() != res2.size() ) throw new Exception("ErrorOnPosition: gt and res sizes diffear ["+ res1.size() + ","+ res2.size() +"].");
		
		ArrayList<ISimilarityResults> sorted1 = (ArrayList<ISimilarityResults>) res1.clone();
		Collections.sort(sorted1, new CustomComparator());
		ArrayList<ISimilarityResults> sorted2 = (ArrayList<ISimilarityResults>) res2.clone();
		Collections.sort(sorted2, new CustomComparator());
		
		double recall = 0.0;
		
		// for each results list
		for ( int i=0; i<sorted1.size(); i++) {
			res[i] = getNormalized(sorted1.get(i), sorted2.get(i), kRes1, kRes2);					
		}
		
		return res ;
	}
	
	public static double getNormalized(ISimilarityResults res1, ISimilarityResults res2 ) throws Exception {
		return getNormalized(res1, res2, res1.size(), res2.size());
	}

	public static double getNormalized(ISimilarityResults[] res1, ISimilarityResults[] res2 ) throws Exception {
		return getNormalized(res1, res2, res1[0].size(), res2[0].size());
	}
	
	public static double getNormalized(ISimilarityResults[] res1, ISimilarityResults[] res2, int nRes ) throws Exception {
		if ( res1.length != res2.length ) {
			throw new Exception("Results arrays to compare have different length;");
		}
		return getNormalized(res1, res2, res1[0].size(), res2[0].size(), res1.length);
	}
	
	public static double getNormalized(ISimilarityResults[] res1, ISimilarityResults[] res2, int kRes1, int kRes2 ) throws Exception {
		if ( res1.length != res2.length ) {
			throw new Exception("Results arrays to compare have different length;");
		}
		return getNormalized(res1, res2, kRes1, kRes2, res1.length);
	}
	
	public static double getNormalized(ISimilarityResults[] res1, ISimilarityResults[] res2, int kRes1, int kRes2, int nRes ) throws Exception {
		double acc = 0;
		for ( int i=0; i<nRes; i++) {
			acc += getNormalized(res1[i], res2[i], kRes1, kRes2);
		}
		return acc/nRes;
	}
	public static double getNormalized(ISimilarityResults res1, ISimilarityResults res2, int kRes1, int kRes2 ) {
		int count = getN(res1,res2,kRes1,kRes2);
		int min = Math.min(kRes1, kRes2);
		return (double) count / min;
	}
	
	public static int getN(ISimilarityResults approxResults, ISimilarityResults gtResults ) throws Exception {
		return getN(approxResults, gtResults, approxResults.size(), gtResults.size());
	}
	
	public static int getN(ISimilarityResults res1, ISimilarityResults res2, int kRes1, int kRes2 ) {
		int count = 0;
		int iRes1 = 0;
		
		for ( Iterator<ObjectWithDistance> it = res1.iterator(); iRes1<kRes1; iRes1++) {
			ObjectWithDistance currRes1 = it.next();

			// searching same results in gt
			int iRes2 = 0;
			for ( Iterator<ObjectWithDistance> it2 = res2.iterator(); iRes2<kRes2; iRes2++) {
				ObjectWithDistance currRes2 = it2.next();
				if ( currRes1.getID().equals(currRes2.getID())) {
					count++;
					break;
				}
				
			}
			
		}
		return count;
	}
	
	/**
	 * @param approxResults			Approximate Results
	 * @param gtResults				Ground Truth Results
	 * @return The positions of the first result in gtResults (Ground Truth Results) not found in the approxResults
	 * @throws Exception
	 */
	public static int getMinPosMissed(ISimilarityResults approxResults, ISimilarityResults gtResults ) {
		return getMinPosMissed(approxResults, gtResults, approxResults.size(), gtResults.size());
	}
	
	
	/**
	 * @param approxResults			Approximate Results
	 * @param gtResults				Ground Truth Results
	 * @param kApproxResults		The length of approxResults to be considered
	 * @param kGTResults			The length of Ground Truth Results to be considered
	 * @return The positions of the first result in gtResults (Ground Truth Results) not found in the approxResults
	 * If nothing was missed, it returns the position of the first result not reported in the Approximate results, i.e., kApproxResults
	 * @throws Exception
	 */
	public static int getMinPosMissed(ISimilarityResults approxResults, ISimilarityResults gtResults, int kApproxResults, int kGTResults ) {
		int iGTRes = 0;
		for ( Iterator<ObjectWithDistance> it = gtResults.iterator(); iGTRes < kGTResults; iGTRes++) {
			ObjectWithDistance currGTResult = it.next();
			AbstractID currGTID = currGTResult.getID();
			
			// searching currGTID between the approximate results
			boolean found = false;
			int iAppRes = 0;
			for ( Iterator<ObjectWithDistance> it2 = approxResults.iterator(); iAppRes < kApproxResults; iAppRes++ ) {
				ObjectWithDistance currRes2 = it2.next();
				if ( currGTID.equals( currRes2.getID() ) ) {
					found = true;
					break;
				}				
			}
			if ( !found ) return iGTRes;
			
		}
		
		// if notingh was missed, we return the position of the first result not reported in the Approximate results, i.e., kApproxResults 
		return kApproxResults;
	}
	
	
	/**
	 * @param approxResults			Approximate Results
	 * @param gtResults				Ground Truth Results
	 * @return The average positions of the missed results
	 */
	public static double getAvgPosOfMissed(ISimilarityResults res1, ISimilarityResults res2 ) {
		return getAvgPosOfMissed(res1, res2, res1.size(), res2.size());
	}
	
	/**
	 * @param approxResults			Approximate Results
	 * @param gtResults				Ground Truth Results
	 * @param kApproxResults		The length of approxResults to be considered
	 * @param kGTResults			The length of Ground Truth Results to be considered
	 * @return The average positions of the missed results.
	 * If nothing was missed, it returns the position of the first result not reported in the Approximate results, i.e., kApproxResutls
	 */
	public static double getAvgPosOfMissed(ISimilarityResults approxResults, ISimilarityResults gtResults, int kApproxResults, int kGTResults ) {
		long posAcc = 0;
		int missedCount = 0;

		int iGTRes = 0;
		for ( Iterator<ObjectWithDistance> it = gtResults.iterator(); iGTRes < kGTResults; iGTRes++) {
			ObjectWithDistance currGTResult = it.next();
			AbstractID currGTID = currGTResult.getID();
			
			// searching currGTID between the approximate results
			boolean found = false;
			int iAppRes = 0;
			for ( Iterator<ObjectWithDistance> it2 = approxResults.iterator(); iAppRes < kApproxResults; iAppRes++ ) {
				ObjectWithDistance currRes2 = it2.next();
				if ( currGTID.equals( currRes2.getID() ) ) {
					found = true;
					break;
				}				
			}
			if ( !found ) {
				missedCount++;
				posAcc += iGTRes;
			}
			
			
		}
		
		// if nothing was missed, it returns the position of the first result not reported in the Approximate results, i.e., kApproxResutls
		if ( missedCount == 0 ) return kApproxResults;
		
		return posAcc/missedCount;
	}
	
	

}
