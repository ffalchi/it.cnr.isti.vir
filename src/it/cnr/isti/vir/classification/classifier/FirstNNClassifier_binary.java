package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.Iterator;

public class FirstNNClassifier_binary extends AbstractKNNClassifier {
	
	public FirstNNClassifier_binary() {
		super();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public PredictedLabel singleLabelClassify(ISimilarityResults knn ) {
		Iterator<ObjectWithDistance> it = knn.iterator();
		ObjectWithDistance best = it.next();
		return new PredictedLabel(((ILabeled) best.getObj()).getLabel(), 1.0);
	}
	
	@Override
	public PredictedLabel[] singleLabelClassify_MultiK(ISimilarityResults<?> knn, int[] ks) {
		PredictedLabel[] cls = new PredictedLabel[ks.length];
		cls[0] = singleLabelClassify(knn);
		for (int i=0; i<ks.length; i++ ) {
			cls[i] = cls[0];
		}	
		
		return cls;
	}
	
}
