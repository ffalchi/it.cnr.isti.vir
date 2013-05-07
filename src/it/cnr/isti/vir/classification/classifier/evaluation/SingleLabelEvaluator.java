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
package it.cnr.isti.vir.classification.classifier.evaluation;


import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.classification.classifier.AbstractKNNClassifier;
import it.cnr.isti.vir.classification.classifier.ClassifierException;
import it.cnr.isti.vir.classification.classifier.IClassifier;
import it.cnr.isti.vir.classification.classifier.ILFClassifier;
import it.cnr.isti.vir.classification.classifier.KNNClassifierDistWeighted;
import it.cnr.isti.vir.classification.classifier.LFCollClassifier;
import it.cnr.isti.vir.features.FeaturesCollectorHTwithIDClassified;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class SingleLabelEvaluator {
	
	
	/**
	 * This is the most general evaluator.
	 * The collection of possible labels is generated from the test set.
	 * 
	 * @param 	testDocuments	documents belonging to the Test Set
	 * @param 	classifier		the Classifier to be tested
	 * @return 					a Confusion Matrix for the current text
	 * @throws ClassifierException
	 */
	public static ConfusionMatrix getConfusionMatrix(
			Collection<IFeaturesCollector_Labeled_HasID> testDocuments,
			IClassifier classifier ) throws ClassifierException
	{
		HashSet<AbstractLabel> temp = new HashSet();
		
		for ( Iterator<IFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			temp.add( it.next().getLabel() );			
		}
		
		AbstractLabel[] arr = new AbstractLabel[temp.size()];
		return getConfusionMatrix(testDocuments, classifier, Arrays.asList(  temp.toArray(arr) ) );
	}
	
	// this is for LF classifier
	public static ConfusionMatrix getConfusionMatrix(
			Collection<IFeaturesCollector_Labeled_HasID> testDocuments,
			IClassifier classifier,
			Collection<AbstractLabel> labelColl ) throws ClassifierException
	{
		
		Collection<TestDocumentSingleLabeled> testList = classifier.classify(testDocuments );
		
		ConfusionMatrix cm =  new ConfusionMatrix(labelColl, testList, 0.0 );

		return cm;
	}
	
//	/*
//	 *  @testDocuments	test documents to be classified
//	 *  @classifier		the classifier to be used
//	 *  @labelColl		complete label collection
//	 *  @queues
//	 *  @exclusionList
//	 *  @lfConfThr
//	 */
//	public static LabelStatsColl getLabelStatsColl(
//							Collection<FeaturesCollectorHTwithIDClassified> testDocuments,
//							LFCollClassifier classifier,
//							Collection<ClassLabel> labelColl,
//							MultipleKNNPQueueID queues,
//							HashSet<ID> exclusionList,
//							double lfConfThr) {
//		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
//		MultipleKNNPQueueID tempQueues = new MultipleKNNPQueueID(queues, exclusionList);
//		for(Iterator<FeaturesCollectorHTwithIDClassified> it = testDocuments.iterator(); it.hasNext(); ) {
//			FeaturesCollectorHTwithIDClassified curr = it.next();
//			testList.add( new TestDocumentSingleLabeled(curr, classifier.classify(tempQueues, lfConfThr) ) );
//		}
//		
//		return getLabelStatsColl(testList, labelColl);
//	}
	
	
//	/*
//	 *  @testDocuments	test documents to be classified
//	 *  @classifier		the classifier to be used
//	 *  @labelColl		complete label collection
//	 */
//	public static LabelStatsColl getLabelStatsColl(Collection<FeaturesCollectorHTwithIDClassified> testDocuments, IClassifier classifier, Collection<ClassLabel> labelColl) {
//		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
//		for(Iterator<FeaturesCollectorHTwithIDClassified> it = testDocuments.iterator(); it.hasNext(); ) {
//			FeaturesCollectorHTwithIDClassified curr = it.next();
//			testList.add( new TestDocumentSingleLabeled(curr, classifier.classify(curr) ) );
//		}
//		
//		return getLabelStatsColl(testList, labelColl);
//	}
	
//	public static ConfusionMatrix getConfusionMatrix(
//			Collection<FeaturesCollectorHTwithIDClassified> testDocuments,
//			IClassifier classifier,
//			Collection<ClassLabel> labelColl,
//			Double confidenceThreshold) {
//		
//		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
//		//LinkedList<TestDocumentSingleLabeled> testNullList = new LinkedList<TestDocumentSingleLabeled>();
//		for(Iterator<FeaturesCollectorHTwithIDClassified> it = testDocuments.iterator(); it.hasNext(); ) {
//			FeaturesCollectorHTwithIDClassified curr = it.next();
//
//			long startTime = System.currentTimeMillis();
//			PredictedClassLabel predictedLabel = classifier.classify(curr);
//			long endTime = System.currentTimeMillis();
//			testList.add( new TestDocumentSingleLabeled(curr, predictedLabel ) );
//			
//			System.out.print( 	curr.getID() + "\t" +
//								testList.size() + // + " of " + testDocuments.size() +
//								"\t" + (endTime-startTime) + " msec" + 
//								"\t" +  curr.getLabel() +								
//								"\t" + predictedLabel.getcLabel() + 
//								"\t" + predictedLabel.getConfidence() +							
//								"\n"
//								);
//		}
//		//System.out.println(testNullList.size() + " were not classified");
//		return new ConfusionMatrix(labelColl, testList, confidenceThreshold);
//	
//	}
	
	public static ConfusionMatrix[][] getConfusionMatrix(
			AbstractLabel[] acLabels,
			LFCollClassifier classifier,
			Collection<AbstractLabel> labelColl,
			ISimilarityResults[][] res,
			double[] confThrImg,
			double[] confThrLF ) {
		
		ConfusionMatrix[][] cm = new ConfusionMatrix[confThrImg.length][confThrLF.length];

		Collection<TestDocumentSingleLabeled>[] testList = classifier.classify(acLabels, res, confThrLF);
		
		for (int iCImg=0; iCImg<confThrImg.length; iCImg++ ) {
			for (int iCLF=0; iCLF<confThrLF.length; iCLF++ ) {
			
			cm[iCImg][iCLF] = new ConfusionMatrix(labelColl, testList[iCLF], confThrImg[iCImg]);
			cm[iCImg][iCLF].addExpDescription( 	classifier + 
												" confThrImg=" + confThrImg[iCImg] +
												" confThrLF="  + confThrLF[iCLF] );
			}
		}
		
		return cm;
	}

	// this is for LF classifier
	public static ConfusionMatrix[][] getConfusionMatrix(
			Collection<IFeaturesCollector_Labeled_HasID> testDocuments,
			ILFClassifier classifier,
			Collection<AbstractLabel> labelColl,
			double[] confThrImg,
			double[] confThrLF ) {
		
		ConfusionMatrix[][] cm = new ConfusionMatrix[confThrImg.length][confThrLF.length];

		Collection<TestDocumentSingleLabeled>[] testList = classifier.classify(testDocuments, confThrLF);
		
		for (int iCImg=0; iCImg<confThrImg.length; iCImg++ ) {
			for (int iCLF=0; iCLF<confThrLF.length; iCLF++ ) {
			
			cm[iCImg][iCLF] = new ConfusionMatrix(labelColl, testList[iCLF], confThrImg[iCImg]);
			cm[iCImg][iCLF].addExpDescription( 	classifier + 
												" confThrImg=" + confThrImg[iCImg] +
												" confThrLF="  + confThrLF[iCLF] );
			}
		}
		
		return cm;
	}
	
	public static ConfusionMatrix[] getConfusionMatrix(
			Collection<FeaturesCollectorHTwithIDClassified> testDocuments,
			AbstractKNNClassifier classifier,
			int[] ks,
			Collection<AbstractLabel> labelColl,
			Double confidenceThreshold) throws ClassifierException {
		
		ConfusionMatrix[] confMatrixes = new ConfusionMatrix[ks.length];
		
		LinkedList<TestDocumentSingleLabeled>[] testList = new LinkedList[ks.length];
		for (int i=0; i<ks.length; i++ ) {
			testList[i]= new LinkedList<TestDocumentSingleLabeled>();
		}
		
		for(Iterator<FeaturesCollectorHTwithIDClassified> it = testDocuments.iterator(); it.hasNext(); ) {
			FeaturesCollectorHTwithIDClassified curr = it.next();
			PredictedLabel[] labels = classifier.classify(curr, ks);
			
			for (int i=0; i<ks.length; i++ ) {
				testList[i].add( new TestDocumentSingleLabeled(curr, labels[i] ) );
			}			
		}
		
		for (int i=0; i<ks.length; i++ ) {
			confMatrixes[i] = new ConfusionMatrix(labelColl, testList[i], confidenceThreshold);
			confMatrixes[i].addExpDescription( "k="+ks[i]+" "+classifier);
		}
			
		return confMatrixes;
	
	}

	// second [k][confThrImg]
	public static ConfusionMatrix[][] getConfusionMatrix(
			LinkedList testDocuments,
			KNNClassifierDistWeighted classifier,
			int[] ks,
			Collection<AbstractLabel> labelColl,
			double[] confThrIMG) throws ClassifierException {
		
		ConfusionMatrix[][] confMatrixes = new ConfusionMatrix[ks.length][confThrIMG.length];
		
		LinkedList<TestDocumentSingleLabeled>[] testList = new LinkedList[ks.length];
		for (int i=0; i<ks.length; i++ ) {
			testList[i]= new LinkedList<TestDocumentSingleLabeled>();
		}
		
		long start = System.currentTimeMillis();
		long lastTime = start;
		int count=0;
		int correctCount =0;
		 
		System.out.println("Online results for k=1");
		for(Iterator<IFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			
			IFeaturesCollector_Labeled_HasID curr = it.next();
			PredictedLabel[] pr = classifier.classify(curr, ks);
			
			// searching correct
			int correctI = -1;
			for ( int i=0; i<pr.length; i++ ) {
				if ( 	( pr[i].getcLabel() != null && pr[i].getcLabel().equals(curr.getLabel() ) )
						||
						( pr[i].getcLabel() == null && curr.getLabel() == null ) ) {
					correctI = i + 1;
					break;
				}					
			}
			long endTime = System.currentTimeMillis();
			count++;
			double avg = (double) (endTime-start) / count;
			System.out.print(count + "/" + testDocuments.size()); //+ "    avgTime: " + Math.round(avg*1000.0)/1000  + " msec    remaining: " +  Math.round(avg * (testDocuments.size()-count)/1000/60) + "'" );
			
			if (
					( pr[0].getcLabel() != null && pr[0].getcLabel().equals(curr.getLabel() ) )
					||
					( pr[0].getcLabel() == null && curr.getLabel() == null )
				)
			{
				System.out.print("\tcorrect");
				correctCount++;
			} else {
				System.out.print("\t*WRONG*");
			}
			
			System.out.format("\t%.3f", (double) correctCount / count);
			System.out.print("\t" + curr.getID() );
			//System.out.print( "\t" + pr[0].getConfidence() );
			System.out.format("\t%.3f", pr[0].getConfidence());               //  -->  "3.142"
			
			
			System.out.print(
				"\t" + pr[0].getcLabel() + "\t( " + curr.getLabel() + " )" +
				"\t[ " + correctI + " / " + pr.length + " ]" +
				"\t" + (endTime-lastTime)/1000.0 + " \"" +
				" " + (int) (((endTime-start)/(double) count*(testDocuments.size()-count)/1000.0/60.0)) + "' rem.\n"
			);
			lastTime = endTime;
			

			for (int i=0; i<ks.length; i++ ) {
				testList[i].add( new TestDocumentSingleLabeled(curr, pr[i] ) );
			}
		}
		
		for (int i=0; i<ks.length; i++ ) {
			for (int iC=0; iC<confThrIMG.length; iC++) {
				confMatrixes[i][iC] = new ConfusionMatrix(labelColl, testList[i], confThrIMG[iC]);
				confMatrixes[i][iC].addExpDescription( "k="+ks[i]+" confThr: "+ confThrIMG[iC] +" "+classifier);
			}
		}
			
		return confMatrixes;
	}
	
	
	
//	/*
//	 *  @testList	classified test documents
//	 *  @labelColl	complete label collection
//	 */
//	private static LabelStatsColl getLabelStatsColl(LinkedList<TestDocumentSingleLabeled> testList, Collection<ClassLabel> labelColl) {
//		LabelStatsColl coll = new LabelStatsColl(labelColl);
//		for(Iterator<TestDocumentSingleLabeled> it=testList.iterator(); it.hasNext(); ) {
//			coll.add(it.next());
//		}
//		return coll;
//	}
	
//	public static String getAll(Collection<FeatureCollWithIDClassified> testDocuments, Classifier classifier, Collection<ClassLabel> labelColl) {
//		
//		String tStr = "Classifier: " + classifier.toString();
//		
//		return tStr + getAll(classifier.classify(testDocuments), labelColl);
//	}
	

//	/*
//	 *  @testList	classified test documents
//	 *  @labelColl	complete label collection
//	 */
//	private static String getAll(LinkedList<TestDocumentClassified> testList, Collection<ClassLabel> labelColl) {
//		return getLabelStatsColl(testList, labelColl).toString();
//	}
	
//	public static double getErrorRate(Collection<FeatureCollWithIDClassified> testDocuments, Classifier classifier ) {
//	LinkedList<TestDocumentClassified> testList = new LinkedList<TestDocumentClassified>();
//	for(Iterator<FeatureCollWithIDClassified> it = testDocuments.iterator(); it.hasNext(); ) {
//		FeatureCollWithIDClassified curr = it.next();
//		testList.add( new TestDocumentClassified(curr, classifier.classify(curr) ) );
//	}
//	
//	return getErrorRate(testList);
//}
	
//	public static double getErrorRate(Collection<TestDocumentClassified> testDocColl ) {
//	
//	int errorCount = 0;
//	
//	for(Iterator<TestDocumentClassified> it=testDocColl.iterator(); it.hasNext(); ) {
//		if ( !it.next().correctlyAssigned() ) errorCount++;
//	}
//	
//	return errorCount/(double) testDocColl.size();		
//}
	
}
