package it.cnr.isti.vir.similarity.index;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.knn.KNNPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

public class ResultsReordering {

	
	public static ISimilarityResults reorder(
			ISimilarityResults res,
			FeaturesCollectorsArchive fca,
			ISimilarity sim ) throws ArchiveException {
		return reorder(res, fca, sim, 1.0, null, null);
	}	
	
	public static ISimilarityResults reorder(
			ISimilarityResults res,
			FeaturesCollectorsArchive fca,
			ISimilarity sim,
			Double minDistance,
			Double exitDistance,
			Integer maxNResults ) throws ArchiveException {
		
		int count = 0;
		
		int actualK = res.size();
		if ( maxNResults != null ) actualK = maxNResults;
		
		AbstractFeaturesCollector query = (AbstractFeaturesCollector) res.getQuery();
		
		double actualMinDist = Double.MAX_VALUE;
		if ( minDistance != null ) actualMinDist = minDistance;
		SimPQueueArr pQueue = new SimPQueueArr(actualK);
		KNNPQueue knn = new KNNPQueue(pQueue, sim, query);
		
		// ORB Reordering
		for ( Object curr : res ) {
			AbstractID currID = ((IHasID) ((ObjectWithDistance)curr).obj).getID();
			AbstractFeaturesCollector fc = fca.get(currID);
			
			double dist = sim.distance(query, fc);
			if ( dist < actualMinDist ) knn.offer(fc, dist);
			
			if ( maxNResults != null && count++ > actualK) {
				break;
			}
		}
		
		return knn.getResults();
	}
	
}
