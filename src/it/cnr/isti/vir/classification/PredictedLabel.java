package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.Iterator;

public class PredictedLabel {
	protected final AbstractLabel cLabel;
	protected final double confidence;
	protected AbstractID[] mostSimilars = null;
	
	public PredictedLabel(PredictedLabel prLabel) {
		this.cLabel = prLabel.cLabel;
		this.confidence = prLabel.confidence;
	}
	
	public PredictedLabel(AbstractLabel cLabel, final double confidence) {
		this.cLabel = cLabel;
		this.confidence = confidence;
	}

	public AbstractLabel getcLabel() {
		return cLabel;
	}

	public double getConfidence() {
		return confidence;
	}
	
	public String toString() {
		return confidence + "\t" + cLabel;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setSimilars(ISimilarityResults res) {
		mostSimilars = new AbstractID[res.size()];
		
		int i=0;
		for ( Iterator<ObjectWithDistance> it = res.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			mostSimilars[i++] = ((IHasID) curr.getObj()).getID();
		}
	}
	
	public AbstractID[] getSimilars() {
		return mostSimilars;
	}
	
}
