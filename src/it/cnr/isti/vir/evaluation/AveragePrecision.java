/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.evaluation;

import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class AveragePrecision {

	
	/*
	 * Average Precision
	 * Area below the average precision curve
	 */
	public static double getAveragePrecision(ISimilarityResults results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		ArrayList<AbstractID> ids = new ArrayList();
		for ( Iterator<ObjectWithDistance> it = results.iterator(); it.hasNext(); ) {
		    ObjectWithDistance curr = it.next();
		    AbstractID currID = ((IHasID) curr.getObj()).getID();
		    ids.add(currID);
		  }
		  return getAveragePrecision(ids, positiveIDs, ambiguosIDs, qID);
	}
	
	/*
	 * Average Precision
	 * Area below the average precision curve
	 */
	public static double getAveragePrecision(Collection<AbstractID> results, HashSet positiveIDs, HashSet ambiguosIDs, AbstractID qID ) {
		  double old_recall = 0.0;
		  double old_precision = 1.0;
		  double ap = 0.0;
		  
		  int intersect_size = 0;
		  //int i = 0;
		  int j = 0;
		  for ( Iterator<AbstractID> it = results.iterator(); it.hasNext(); ) {
		    AbstractID currID = it.next();
		    
		    if ( qID != null && currID.equals(qID) ) continue;
		    if ( ambiguosIDs!=null && ambiguosIDs.contains(currID)) continue;
		    if (positiveIDs.contains(currID)) intersect_size++;

		    double recall = intersect_size / (double) positiveIDs.size();
		    double precision = intersect_size / (j + 1.0);

		    ap += (recall - old_recall)*((old_precision + precision)/2.0);

		    old_recall = recall;
		    old_precision = precision;
		    j++;
		    if ( recall == 1.0 ) {
		    	break;
		    }
		  }
		  return ap;
	}
	
}
