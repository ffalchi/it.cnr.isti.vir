/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityLFGroup_ResultsArr;

import java.util.Iterator;


public class SimRatioKNNClassifier extends AbstractKNNClassifier  {
	
	public SimRatioKNNClassifier() {
		super();
	}
	
	public SimRatioKNNClassifier(IkNNExecuter knnExec) {
		super(knnExec, 1);
	}

	

	@Override
	public PredictedLabel singleLabelClassify(ISimilarityResults knn ) {
		if ( SimilarityLFGroup_ResultsArr.class.isInstance(knn)) {
			return ((SimilarityLFGroup_ResultsArr) knn).getLoweKNNPrClassLabel();
		}
		if ( knn == null || knn.size() == 0 ) return new PredictedLabel(null, 0.0);
		
		Iterator<ObjectWithDistance> it = knn.iterator();
		ObjectWithDistance best = it.next();
//		if ( best == null ) return new PredictedLabel(null, 0.0);
		AbstractLabel bestLabel = ((ILabeled) best.getObj()).getLabel();
		if ( knn.size() == 1 ) return new PredictedLabel(bestLabel, 0.0);
		for ( int count=0; it.hasNext();) {
			ObjectWithDistance curr = it.next();
			
//			if ( curr == null ) break;
			
//			ILabeled second = (ILabeled) curr.getObj();		
//			if ( second == null ) {
//				// a second label does not exist
//				return new PredictedClassLabel(bestLabel, 1.0);
//			}
			AbstractLabel currLabel = null;
			ILabeled obj = ((ILabeled) curr.getObj());
			if ( obj != null )
				currLabel = obj.getLabel();	
						
			if ( !bestLabel.equals(currLabel) ) {
				// second label found
				return new PredictedLabel(bestLabel, 1.0-(best.getDist() / curr.getDist()));
			}
		}
		return new PredictedLabel(bestLabel, 1.0);		
	}
	
	@Override
	public PredictedLabel[] singleLabelClassify_MultiK(ISimilarityResults knn, int[] ks) {
		PredictedLabel[] cls = new PredictedLabel[ks.length];

		for (int i=0; i<ks.length; i++ ) {
			if ( ks[i] > knn.size() ) {
				System.err.println("MultiKSingleLabelClassify error. Ks[i]>knn.size: " + ks[i] + ">" +  knn.size());
				return null;
			}
			if ( ks[i] <= 0 ) {
				System.err.println("MultiKSingleLabelClassify error. Ks[i]= " + ks[i]);
				return null;
			}
			Iterator<ObjectWithDistance> it = knn.iterator();
			ObjectWithDistance best = it.next();
			AbstractLabel bestLabel = ((ILabeled) best.getObj()).getLabel();
			for ( int count=0; it.hasNext() && count < ks[i]; count++) {
				ObjectWithDistance curr = it.next();
				AbstractLabel currLabel = ((ILabeled) curr.getObj()).getLabel();
				if ( !bestLabel.equals(currLabel) ) {
					cls[i] =  new PredictedLabel(bestLabel, 1.0-(best.getDist() / curr.getDist()));
				}
				
			}
		}	
		
		return cls;
	}

}
