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
import it.cnr.isti.vir.classification.PredictedLabelWithSimilars;
import it.cnr.isti.vir.classification.classifier.evaluation.TestDocumentSingleLabeled;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.similarity.knn.KNNExecuter;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class LFCollClassifier_Sim implements IClassifier {

	protected KNNExecuter knnIndex;
	protected LFCollClassifier classifier;
	protected int k;
	protected double internalKFactor;
	protected FeaturesCollectorsArchives archives;
	

	public LFCollClassifier_Sim(LFCollClassifier classifier,
								KNNExecuter knnIndex,
								FeaturesCollectorsArchives archives,
								int k,
								double internalKFactor ) {
		this.knnIndex = knnIndex;
		this.classifier = classifier;
		this.k = k;
		this.internalKFactor = internalKFactor;
		this.archives = archives;
	}
	
	@Override
	public PredictedLabelWithSimilars classifyWithSimilars(
			IFeaturesCollector obj) throws ClassifierException {
		try {
			ISimilarityResults tRes = knnIndex.getKNNResults(obj, (int) Math.ceil(k*internalKFactor ) );
			Collection<IFeaturesCollector_Labeled_HasID> res = tRes.getFCs(archives);
			return classifier.classifyWithSimilars(obj, res);
		} catch (Exception e ) {
			return null;
		}
	}
	
	@Override
	public PredictedLabel classify(IFeaturesCollector obj) throws ClassifierException {
		try {
			ISimilarityResults tRes = knnIndex.getKNNResults(obj, (int) Math.ceil(k*internalKFactor ) );
			Collection<IFeaturesCollector_Labeled_HasID> res = tRes.getFCs(archives);
			return classifier.classify(obj, res);
		} catch (Exception e ) {
			return null;
		}		
	}
	
	private static boolean checkMultipleLabels(ISimilarityResults res) {
		AbstractLabel label = null;
		for ( Iterator<ObjectWithDistance> it = res.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			AbstractLabel currLabel = ((ILabeled) curr.getObj()).getLabel();
			if ( label == null ) {
				label = currLabel;
			} else {
				if ( !label.equals(currLabel) ) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Collection<TestDocumentSingleLabeled> classify(
			Collection<IFeaturesCollector_Labeled_HasID> testDocuments)
			throws ClassifierException {
		
		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
		long start = System.currentTimeMillis();
		int count = 0;
		for(Iterator<IFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			IFeaturesCollector_Labeled_HasID curr = it.next();
			count++;
			System.out.print( 	count + " of " + testDocuments.size() + "\t" + curr.getID() );
			long startTime = System.currentTimeMillis();
			
			try {
				ISimilarityResults tRes = knnIndex.getKNNResults(curr, (int) Math.ceil(k*internalKFactor ) );
				PredictedLabel pr = null;
				if ( checkMultipleLabels(tRes) ) {
					Collection<IFeaturesCollector_Labeled_HasID> res = tRes.getFCs(archives);
					pr = classifier.classify(curr, res );					
				} else {
					pr = new PredictedLabel( ((ILabeled) ((ObjectWithDistance) tRes.iterator().next()).getObj()).getLabel(), 1.0);
				}
				testList.add( new TestDocumentSingleLabeled(curr, pr ) );
				long endTime = System.currentTimeMillis();
				if (
						( pr.getcLabel() != null && pr.getcLabel().equals(curr.getLabel() ) )
						||
						( pr.getcLabel() == null && curr.getLabel() == null )
					)
				{
					System.out.print("\tcorrect");
				} else {
					System.out.print("\t*WRONG*");
				}
				System.out.print( 
						"\t" + pr.getcLabel() + "\t[" + curr.getLabel() + "]" + "\t" + pr.getConfidence() + 
						"\t" + (int) ((endTime-start)/(double) count*(testDocuments.size()-count)/1000.0/60.0) + "' rem." +
						"\t" + (endTime-startTime) + " msec" +
						"\n"
					);
			} catch (Exception e ) {
				e.printStackTrace();
				return null;
			}
		}
		return testList;
	}



}
