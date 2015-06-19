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
	

	
	/*
	 * Average Precision
	 * Area below the average precision curve
	 */
//	public static double getAveragePrecision(
//			ISimilarityResults results,
//			HashSet positiveIDs,
//			HashSet ambiguosIDs,
//			AbstractID qID ) {
//		
//		ArrayList<AbstractID> ids = new ArrayList<AbstractID>();
//		for ( Iterator<ObjectWithDistance> it = results.iterator(); it.hasNext(); ) {
//		    ObjectWithDistance curr = it.next();
//		    AbstractID currID = ((IHasID) curr.getObj()).getID();
//		    ids.add(currID);
//		}
//		return getAveragePrecision(ids, positiveIDs, ambiguosIDs, qID);
//	}
	
	/*
	 * Average Precision
	 * Area below the average precision curve
	 */
	public static double getAveragePrecision(Collection<AbstractID> results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		return PrecisionRecall.getPrecisionRecall(results, positiveIDs, ambiguosIDs, qID).getAveragePrecision();
		
		//
//		// INRIA & OXFORD
//		double ap = 0.0;
//		
//		double recallStep = 1.0 / positiveIDs.size();
//		int nTP = 0;
//		int rank = 0;
//		for ( Iterator<AbstractID> it = results.iterator(); it.hasNext(); ) {
//		    AbstractID currID = it.next();
//		    
//		    if ( ambiguosIDs!=null && ambiguosIDs.contains(currID)) continue;
//				    
//		    if ( positiveIDs.contains(currID) ) {
//		    	
//		    	double precision_0;
//		    	if ( rank == 0 ) precision_0 = 1.0;
//		    	else precision_0 = nTP / (double) rank;
//		    	double precision_1 = (nTP+1) / (double) (rank+1);
//		    	ap += ( precision_0+precision_1) * recallStep / 2.0;
//		    	nTP++;
//		    } else if ( qID != null && currID.equals(qID) ) continue;		    	
//
//		    rank++;
//		    
//		}
//		
//		return ap;		
		
		// WIKIPEDIA
//		double precisionSum = 0.0;
//		int intersect_size = 0;
//		int count = 0;
//		for ( Iterator<AbstractID> it = results.iterator(); it.hasNext(); ) {
//		    AbstractID currID = it.next();
//		    
//		    if ( ambiguosIDs!=null && ambiguosIDs.contains(currID)) continue;
//		
//		    
//		    if ( positiveIDs.contains(currID) ) {
//		    	intersect_size++;
//		    	count++;
//		    	precisionSum += intersect_size / (double) count;	 
//		    } else if ( qID != null && currID.equals(qID) ) continue;		    	
//		    else {
//		    	count++;
//		    }		       
//		    
//		}
//		
//		return precisionSum / positiveIDs.size();
		    
		
		// DEPRECATED MINE
//		  double old_recall = 0.0;
//		  double old_precision = 1.0;
//		  double ap = 0.0;
//		  
//		  int intersect_size = 0;		  
//		  int j = 1;
//		  for ( Iterator<AbstractID> it = results.iterator(); it.hasNext(); ) {
//		    AbstractID currID = it.next();
//		    
//		    
//		    if ( ambiguosIDs!=null && ambiguosIDs.contains(currID)) continue;
//		    if ( positiveIDs.contains(currID) ) {
//		    	intersect_size++;
//		    } else {
//		    	if ( qID != null && currID.equals(qID) ) continue;
//		    }
//
//		    double recall = intersect_size / (double) positiveIDs.size();
//		    double precision = intersect_size / (double) j ;
//
//		    ap += (recall - old_recall)*((old_precision + precision)/2.0);
//
//		    old_recall = recall;
//		    old_precision = precision;
//		    j++;
//		    if ( recall == 1.0 ) {
//		    	break;
//		    }
//		  }
//		  return ap;
	
	}


	




	
}
