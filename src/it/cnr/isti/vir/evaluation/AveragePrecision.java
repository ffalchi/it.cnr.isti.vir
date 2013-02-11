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
