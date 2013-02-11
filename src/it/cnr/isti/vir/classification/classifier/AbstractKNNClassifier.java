package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.classification.PredictedLabelWithSimilars;
import it.cnr.isti.vir.classification.classifier.evaluation.TestDocumentSingleLabeled;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.similarity.knn.KNNExecuter;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public abstract class AbstractKNNClassifier implements IClassifier {
	
	public final KNNExecuter knnExec;
	
	protected int k;
	
	public AbstractKNNClassifier(Properties properties) {
		knnExec = null;
	}
	
	public AbstractKNNClassifier() {
		knnExec = null;
	}
	
	public AbstractKNNClassifier(KNNExecuter knnExec, int k) {
		this.knnExec = knnExec;
		this.k = k;
	}

	public PredictedLabel[] classify(IFeaturesCollector givenObj, int[] ks) throws ClassifierException {
		
		int maxK = -1;
		for (int i=0; i<ks.length; i++ ) {
			if ( ks[i]>maxK ) maxK = ks[i];
		}
		try {
			ISimilarityResults<?> knn = knnExec.getKNNResults(givenObj, maxK);
			//System.out.println("KNN size: " + knn.getSize() + " lastDist: " + knn.getLastDist());
			return singleLabelClassify_MultiK(knn, ks);
		} catch (Exception e) {
			throw new ClassifierException(e);
		}
 		
		
	}
	
	public PredictedLabelWithSimilars classifyWithSimilars(IFeaturesCollector res) throws ClassifierException {
		try {
			ISimilarityResults<?> knn = knnExec.getKNNResults(res, k);
			
			//System.out.println("KNN size: " + knn.getSize() + " lastDist: " + knn.getLastDist());
			return new PredictedLabelWithSimilars(
													singleLabelClassify(knn),
													knn
												);
		} catch (Exception e) {
			throw new ClassifierException(e);
		}
	}
	
		
	@Override
	public PredictedLabel classify(IFeaturesCollector res) throws ClassifierException {
		try {
			ISimilarityResults<?> knn = knnExec.getKNNResults(res, k);
			
			//System.out.println("KNN size: " + knn.getSize() + " lastDist: " + knn.getLastDist());
			return singleLabelClassify(knn);
		} catch (Exception e) {
			throw new ClassifierException(e);
		}
	}

	@Override
	public LinkedList<TestDocumentSingleLabeled> classify(	Collection<IFeaturesCollector_Labeled_HasID> testDocuments) throws ClassifierException {
		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
		for(Iterator<IFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			IFeaturesCollector_Labeled_HasID curr = it.next();
			testList.add( new TestDocumentSingleLabeled(curr, this.classify(curr) ) );
		}
		return testList;
	}
	
	public abstract PredictedLabel[] singleLabelClassify_MultiK(ISimilarityResults<?> knn, int[] ks);
	
	public PredictedLabel singleLabelClassify(ISimilarityResults<?> knn) {
		int[] ks = { knn.size() };
	
		return singleLabelClassify_MultiK(knn, ks )[0];
	}
	
	//public abstract ClassLabel[] singleLabelMultiKClassify(KNNObjects knn);
	
	public String toString() {
		return this.getClass() + " k: " + k + "\n" + "knnExecuter: " + knnExec + "\n";
	}
	
}

