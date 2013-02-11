package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.classification.classifier.evaluation.TestDocumentSingleLabeled;
import it.cnr.isti.vir.features.IFeaturesCollector;

import java.util.Collection;

public interface ILFClassifier extends IClassifier {

	public PredictedLabel classify(IFeaturesCollector obj, Double lfConfThreshold);
	
	public PredictedLabel[] classify(IFeaturesCollector obj, double[] lfConfThreshold);

	public Collection<TestDocumentSingleLabeled> classify(Collection<?> testDocuments, Double lfConfThreshold);
	
	public Collection<TestDocumentSingleLabeled>[] classify(Collection<?> testDocuments, double[] lfConfThreshold);

	Collection<TestDocumentSingleLabeled>[] classify(Collection<?> testDocuments,	double[] lfConfThr, Collection<?> trainingDocuments);
	
}
