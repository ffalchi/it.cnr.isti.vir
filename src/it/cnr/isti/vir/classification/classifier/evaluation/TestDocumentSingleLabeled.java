package it.cnr.isti.vir.classification.classifier.evaluation;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

public class TestDocumentSingleLabeled implements Comparable {

	private final AbstractLabel actualLabel;
	private final PredictedLabel predictedLabel;
	private final AbstractID id;
	
	public final AbstractLabel getActualLabel() {
		return actualLabel;
	}

	public final PredictedLabel getPredictedLabel() {
		return predictedLabel;
	}
	
	public TestDocumentSingleLabeled(AbstractLabel actualLabel, PredictedLabel predictedLabel, AbstractID id) {
		super();
		this.actualLabel = actualLabel;
		this.predictedLabel = predictedLabel;
		this.id = id;
	}
	
	public TestDocumentSingleLabeled(ILabeled curr, PredictedLabel predictedLabel) {
		super();
		this.actualLabel = curr.getLabel();
		this.predictedLabel = predictedLabel;
		if ( IHasID.class.isInstance(curr)) {
			this.id = ((IHasID) curr).getID();
		} else {
			this.id = null;
		}
	}

	public boolean correctlyAssigned() {
		if ( actualLabel == null ) {
			if ( predictedLabel.getcLabel() == null ) return true;
			else return false;
		}
		return actualLabel.equals(predictedLabel.getcLabel());
	}
	
	public AbstractID getID() {
		return id;
	}

	@Override
	public int compareTo(Object o) {
		double thisConf = predictedLabel.getConfidence();
		if ( !correctlyAssigned() ) thisConf = -thisConf;
		double thatConf = ((TestDocumentSingleLabeled) o).predictedLabel.getConfidence();
		if ( !((TestDocumentSingleLabeled) o).correctlyAssigned() ) thatConf = -thatConf;
		return Double.compare(thisConf, thatConf);
	}


}
