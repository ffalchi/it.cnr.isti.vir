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
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.math.VectorMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class AveragePrecision {


	/**
	 * @param res					Array of results (including quueryID)
	 * @param gTruth				HashMap of QueryID and Expected Results
	 * @return
	 */
	public static double getMeanAveragePrecision(
			ISimilarityResults[] res, 
			HashMap<AbstractID,ArrayList<AbstractID>> positives
			) {
		return getMeanAveragePrecision(res, positives, null);
	}
	
	
	public static final double getMeanAveragePrecision(Collection<PrecisionRecall> given) {
		double sum = 0;
		synchronized ( given ) { 
			for ( PrecisionRecall curr : given ) {
				sum += curr.getAveragePrecision();
			}
		}
		
		return sum / given.size();		
	}
	
	
	/**
	 * @param res
	 * @param positives
	 * @param ambiguous
	 * @return
	 */
	public static double[] getAveragePrecisions(
			Collection<ISimilarityResults> res, 
			HashMap<AbstractID,ArrayList<AbstractID>> positives,
			HashMap<AbstractID,ArrayList<AbstractID>> ambiguous
			) {
		ISimilarityResults[] temp = new ISimilarityResults[res.size()];
		return getAveragePrecisions(temp, positives, ambiguous);
		
	}
	
	/**
	 * @param res
	 * @param positives
	 * @param ambiguous
	 * @return
	 */
	public static double[] getAveragePrecisions(
			ISimilarityResults[] res, 
			HashMap<AbstractID,ArrayList<AbstractID>> positives
			) {
		return getAveragePrecisions(res, positives, null);
	}
	
	/**
	 * @param res
	 * @param positives
	 * @param ambiguous
	 * @return
	 */
	public static double[] getAveragePrecisions(
			ISimilarityResults[] res, 
			HashMap<AbstractID,ArrayList<AbstractID>> positives,
			HashMap<AbstractID,ArrayList<AbstractID>> ambiguous
			) {
		
		double[] ap = new double[res.length]; 
		
		// for each result list
		for ( int i=0; i<res.length; i++) {
			// results
			SimilarityResults cRes = (SimilarityResults) res[i];
			
			// query
			AbstractID query = ((IHasID) cRes.getQuery()).getID();
			
			// expected results
			HashSet expectedResults = new HashSet(positives.get(query));
			
			// ambiguous results
			HashSet ambiguousResults = null;
			if ( ambiguous != null ) ambiguousResults = new HashSet(ambiguous.get(query));
			
			PrecisionRecall pr = PrecisionRecall.getPrecisionRecall(cRes, expectedResults, ambiguousResults, query);
			ap[i] = pr.getAveragePrecision();
			//ap[i] = getAveragePrecision(cRes, expectedResults, ambiguousResults, query);
		}
		
		return ap;
		
	}

	/**
	 * @param res
	 * @param positives
	 * @param ambiguous
	 * @return
	 */
	public static double getMeanAveragePrecision(
			ISimilarityResults[] res, 
			HashMap<AbstractID,ArrayList<AbstractID>> positives,
			HashMap<AbstractID,ArrayList<AbstractID>> ambiguous
			) {
		
		double[] ap = 
				getAveragePrecisions(
						res, 
						positives,
						ambiguous
						);
		
		return VectorMath.mean(ap);
		
	}
	

	public static double getAveragePrecision(ISimilarityResults res, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		return PrecisionRecall.getPrecisionRecall(res, positiveIDs, ambiguosIDs, qID).getAveragePrecision();
	}

	public static double getAveragePrecision(Collection<AbstractID> results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		return PrecisionRecall.getPrecisionRecall(results, positiveIDs, ambiguosIDs, qID).getAveragePrecision();
	
	}


	




	
}
