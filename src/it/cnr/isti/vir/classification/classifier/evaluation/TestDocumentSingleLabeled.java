/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
