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
