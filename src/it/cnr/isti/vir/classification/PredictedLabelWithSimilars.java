package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.similarity.results.ISimilarityResults;

public class PredictedLabelWithSimilars extends PredictedLabel {

	private final ISimilarityResults<?> res;
	
//	public PredictedClassLabelWithSimilars(ClassLabel cLabel, double confidence) {
//		super(cLabel, confidence);
//		res = null;
//	}

	public PredictedLabelWithSimilars(AbstractLabel cLabel, double confidence, ISimilarityResults<?> res) {
		super(cLabel, confidence);
		this.res = res;
	}
	
	public PredictedLabelWithSimilars(PredictedLabel prLabel, ISimilarityResults<?> res) {
		super(prLabel);
		this.res = res;
	}
	
	public final ISimilarityResults<?> getResults() {
		return res;
	}

	public PredictedLabel getPredictedLabelOnly() {
		return new PredictedLabel(cLabel, confidence);
	}
	
}
