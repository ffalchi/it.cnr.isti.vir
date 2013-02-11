package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.classification.PredictedLabelWithSimilars;
import it.cnr.isti.vir.classification.classifier.evaluation.TestDocumentSingleLabeled;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;

import java.util.Collection;

public interface IClassifier {

	public PredictedLabel classify(IFeaturesCollector obj) throws ClassifierException;
	
	public PredictedLabelWithSimilars classifyWithSimilars(IFeaturesCollector obj) throws ClassifierException;

	public Collection<TestDocumentSingleLabeled> classify(
			Collection<IFeaturesCollector_Labeled_HasID> testDocuments) throws ClassifierException;
	
}
