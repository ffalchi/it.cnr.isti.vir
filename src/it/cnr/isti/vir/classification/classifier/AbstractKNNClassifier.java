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

import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.classification.PredictedLabelWithSimilars;
import it.cnr.isti.vir.classification.classifier.evaluation.TestDocumentSingleLabeled;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.AbstractFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

public abstract class AbstractKNNClassifier implements IClassifier {
	
	public final IkNNExecuter knnExec;
	
	protected int k;
	
	public AbstractKNNClassifier(Properties properties) {
		knnExec = null;
	}
	
	public AbstractKNNClassifier() {
		knnExec = null;
	}
	
	public AbstractKNNClassifier(IkNNExecuter knnExec, int k) {
		this.knnExec = knnExec;
		this.k = k;
	}

	public PredictedLabel[] classify(AbstractFeaturesCollector givenObj, int[] ks) throws ClassifierException {
		
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
	
	public PredictedLabelWithSimilars classifyWithSimilars(AbstractFeaturesCollector res) throws ClassifierException {
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
	public PredictedLabel classify(AbstractFeaturesCollector res) throws ClassifierException {
		try {
			ISimilarityResults<?> knn = knnExec.getKNNResults(res, k);
			
			//System.out.println("KNN size: " + knn.getSize() + " lastDist: " + knn.getLastDist());
			return singleLabelClassify(knn);
		} catch (Exception e) {
			throw new ClassifierException(e);
		}
	}

	@Override
	public LinkedList<TestDocumentSingleLabeled> classify(	Collection<AbstractFeaturesCollector_Labeled_HasID> testDocuments) throws ClassifierException {
		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
		for(Iterator<AbstractFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector_Labeled_HasID curr = it.next();
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

