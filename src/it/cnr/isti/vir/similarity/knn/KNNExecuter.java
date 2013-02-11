package it.cnr.isti.vir.similarity.knn;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;


public interface KNNExecuter {
	
	public ISimilarityResults getKNNResults(IFeaturesCollector qObj, int k) throws Exception;
	
	//public SimilarityResultsInterface<ID> getKNNResultsIDs(FeaturesCollectionInterface qObj, int k);
	
	//public KNNIDs<ID> getKNNIDs(FeaturesCollectionWithID qObj, int k);
	
}
