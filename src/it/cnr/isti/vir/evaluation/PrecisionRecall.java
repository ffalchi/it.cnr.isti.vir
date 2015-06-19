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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class PrecisionRecall {

	ArrayList<PrecisionRecallPoint> points = new ArrayList<PrecisionRecallPoint>();
	
	PrecisionRecall() {
		
	}
	
	public double getPrecisionAtRecall(double recall) {
		double lastPrecision = 1.0;
		for (PrecisionRecallPoint point : points) {
			if ( point.recall >= recall ) return (point.precision +  lastPrecision) / 2.0;
			lastPrecision = point.precision;
		}
		return 0;
	}
	
	public void add(PrecisionRecallPoint point) {
		points.add(point);
		Collections.sort(points);
	}
	
	public void add(double precision, double recall) {
		points.add(new PrecisionRecallPoint(precision, recall));
	}
	
	private class PrecisionRecallPoint implements Comparable{
		double precision;
		double recall;
	
	
		public PrecisionRecallPoint(double precision, double recall) {
			super();
			this.precision = precision;
			this.recall = recall;
		}

		@Override
		public int compareTo(Object o) {
			int t = Double.compare(recall, ((PrecisionRecallPoint) o).recall);
			if ( t != 0 ) return t;
			return -Double.compare(precision, ((PrecisionRecallPoint) o).precision);
		}
	}

	/**
	 * @param res					Array of results (including quueryID)
	 * @param gTruth				HashMap of QueryID and Expected Results
	 * @return
	 */
	public static Collection<PrecisionRecall> getPrecisionRecalls(
			ISimilarityResults[] res,  
			HashMap<AbstractID,ArrayList<AbstractID>> positive ) {
		return getPrecisionRecalls(res, positive, null);
	}
	
	public static Collection<PrecisionRecall> getPrecisionRecalls(
			ISimilarityResults[] res,  
			HashMap<AbstractID,ArrayList<AbstractID>> positive,
			HashMap<AbstractID,ArrayList<AbstractID>> ambiguous ) {
		
		ArrayList<PrecisionRecall> prs = new ArrayList<PrecisionRecall>(res.length);
		
		double apSum = 0; 
		
		// for each results list
		for ( int i=0; i<res.length; i++) {
			// results
			SimilarityResults cRes = (SimilarityResults) res[i];
			
			// query
			AbstractID query = ((IHasID) cRes.getQuery()).getID();
			
			// expected results
			HashSet positiveHS = new HashSet(positive.get(query));
			HashSet ambiguousHS = null;
			if ( ambiguous != null ) {
				ambiguousHS = new HashSet(ambiguous.get(query));
			}
			
			prs.add( PrecisionRecall.getPrecisionRecall(cRes, positiveHS, ambiguousHS, query) );
			
		}
		
		return prs;
	}
	

	public static PrecisionRecall getPrecisionRecall(ISimilarityResults results, HashSet positiveIDs, AbstractID qID ) {
		return getPrecisionRecall(results, positiveIDs, null, qID);
	}
	
	public static PrecisionRecall getPrecisionRecall(ISimilarityResults results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		ArrayList<AbstractID> ids = new ArrayList();
		for ( Iterator<ObjectWithDistance> it = results.iterator(); it.hasNext(); ) {
		    ObjectWithDistance curr = it.next();
		    AbstractID currID = ((IHasID) curr.getObj()).getID();
		    ids.add(currID);
		  }
		  return getPrecisionRecall(ids, positiveIDs, ambiguosIDs, qID);
	}
	
	public static PrecisionRecall getPrecisionRecall(Collection<AbstractID> results, HashSet positiveIDs, AbstractID qID ) {
		return getPrecisionRecall(results, positiveIDs, null, qID);
	}
	
	public static  PrecisionRecall getPrecisionRecall(Collection<AbstractID> results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		
		  double old_recall = 0.0;
		  double old_precision = 1.0;
		  
		  int intersect_size = 0;
		  int j = 0;
		  
		  PrecisionRecall res = new PrecisionRecall();

		  for ( Iterator<AbstractID> it = results.iterator(); it.hasNext(); ) {
		    AbstractID currID = it.next();
		    
		    
		    if ( ambiguosIDs!=null && ambiguosIDs.contains(currID)) continue;
		    
		    if (positiveIDs.contains(currID)) intersect_size++;
		    else if ( qID != null && currID.equals(qID) ) continue;
		    
		    // Recall and precision are updated for each result
		    double recall = intersect_size / (double) positiveIDs.size();
		    double precision = intersect_size / (j + 1.0);

		    if ( recall != old_recall || precision != old_precision )
		    	res.add(precision, recall);
		    
		    j++;
		    
		    old_recall = recall;
		    old_precision = precision;
		    		    
		    if ( recall == 1.0 ) {
		    	break;
		    }
		    
		  }
		  return res;
	}
	
	public String toString() {
		String tStr = "";
		for ( PrecisionRecallPoint p : points ) {
			tStr += p.recall +"\t"+ p.precision + "\n";
		}
		return tStr;
	}
	

	
	/**
	 * @param prs			Collection of PrecisionRecall
	 * @param nSampling		Number of recall sampling
	 * @return
	 */
	public static PrecisionRecall getAvg(Collection<PrecisionRecall> prs, int nSampling) {
		PrecisionRecall res = new PrecisionRecall();

		for ( int i=0; i<nSampling; i++ ) {
			double currRecall = i/(double) (nSampling-1);
			
			double precisionSum = 0;
			for ( PrecisionRecall pr : prs ) {
				precisionSum += pr.getPrecisionAtRecall(currRecall);
			}
			
			double avgPrecision = precisionSum / prs.size();
			
			res.add(avgPrecision, currRecall);
				
		} 
		return res;
	}

	
	
	public double getAveragePrecision() {
		  PrecisionRecallPoint before = new PrecisionRecallPoint(1.0,0);
		  
		  double ap = 0;
		  for ( PrecisionRecallPoint pr : points ) {
			  ap +=   (pr.recall - before.recall)
					  *
					  (pr.precision + before.precision)
					  / 2.0;
			  if ( pr.recall == 1.0 ) break;
			  before = pr;
		  }
		  		  
		  return ap;
	}
}
