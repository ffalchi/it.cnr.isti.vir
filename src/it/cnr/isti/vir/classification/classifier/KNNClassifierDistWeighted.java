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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class KNNClassifierDistWeighted extends AbstractKNNClassifier  {
	
	public KNNClassifierDistWeighted() {
	}
	
	public KNNClassifierDistWeighted(IkNNExecuter knnExec, int k) {
		super(knnExec, k);
	}
	
	public static PredictedLabel getPredictedClassLabel(ISimilarityResults knn, int k) {
		HashMap<AbstractLabel, Double> hm = new HashMap<AbstractLabel, Double>();
		int count =0;
		
		if ( knn.size() == 0 )
			return new PredictedLabel(null, 0.0);

		AbstractLabel firstLabel = ( (ILabeled) ((ObjectWithDistance) knn.iterator().next()).obj).getLabel();
		for ( Iterator<ObjectWithDistance> it = knn.iterator(); it.hasNext() && count < k; count++ ) {
			ObjectWithDistance curr = it.next();
			AbstractLabel currLabel = ( (ILabeled) curr.obj ).getLabel();
			double currSimilarity = 1.0-curr.dist;
			if ( curr.dist>1.0 || curr.dist <0 ) System.err.println("ERROR!!! KNNClassifierDistWeighted found distance: "+curr.dist);
			Double value = hm.get( currLabel );
			if ( value != null ) {
				value += currSimilarity; 	// WEIGHTED
				//value += 1.0;				// BINARY
				hm.put( currLabel, value );
			}
			else hm.put( currLabel, currSimilarity );
		}
		
		AbstractLabel best = null;
		Double bestValue = 0.0;
		
		AbstractLabel secondBest = null;
		Double secondBestValue = 0.0;
		
		Double valueSum = 0.0;
		
		
		//searching best
		for (Iterator<Entry<AbstractLabel, Double>> it = hm.entrySet().iterator(); it.hasNext(); ) {
			Entry<AbstractLabel, Double> curr = it.next();
			valueSum += curr.getValue();
			if ( curr.getValue() > bestValue ) {
				secondBest = best;
				secondBestValue = bestValue;
				best = curr.getKey();
				bestValue = curr.getValue();				
			} else if ( curr.getValue() > secondBestValue ) {
				secondBest = curr.getKey();
				secondBestValue = curr.getValue();
			}
		}
		
		/* DEPRECATED!
		if ( secondBestValue == bestValue ) {
			if ( secondBest.equals(firstLabel) ) {
				secondBest = best;
				best = firstLabel;
			}
		}
		*/
		// checks if some lf had a match
		if ( valueSum == 0 ) {
			return new PredictedLabel(null, 0.0);
		}
		
		// CONFIDENCE FOR THE WHOLE IMAGE
			// fist/second		
//				double confidence = 1.0 - ((double) secondBestValue / (double) bestValue);//kNNs.size();
			// absolute
//				double confidence = bestValue / valueSum;
			// absolute binary
		
		double relConfidence = 1.0 - ((double) secondBestValue / (double) bestValue);//kNNs.size();
		double bestResConf = 0;
		for ( Iterator<ObjectWithDistance> it = knn.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			AbstractLabel currLabel = ( (ILabeled) curr.obj ).getLabel();
			if ( currLabel.equals(best)) {
				bestResConf = 1.0 - curr.dist;
				break;
			}
		}
		
//		double confidence =  2 * ( Math.pow(bestResConf,2) * relConfidence ) / ( bestResConf + relConfidence );
		double confidence = bestResConf;
		PredictedLabel prLabel = new PredictedLabel(best, confidence);
		prLabel.setSimilars(knn);
		return prLabel;
		
	}
	
	public PredictedLabel[] singleLabelClassify_MultiK(ISimilarityResults knn, int[] ks) {
		PredictedLabel[] cls = new PredictedLabel[ks.length];

		for (int i=0; i<ks.length; i++ ) {
//			if ( ks[i] > knn.size() ) {
//				System.err.println("MultiKSingleLabelClassify error. Ks[i]>knn.size: " + ks[i] + ">" +  knn.size());
//				return null;
//			}
			if ( ks[i] <= 0 ) {
				System.err.println("MultiKSingleLabelClassify error. Ks[i]= " + ks[i]);
				return null;
			}
			
			cls[i] = getPredictedClassLabel(knn, ks[i]);
		}
		
		
		
		return cls;
		
	}	

//	public double getAbsoluteConfidence(ClassLabel givenLabel, SimilarityResultsInterface knn, int k) {
//
//		double sum = 0;
//		double givenLabelSum = 0;
//		int count =0;
//		for ( Iterator<ObjectWithDistance> it = knn.iterator(); it.hasNext() && count < k; count++) {
//			//if ( hm.isEmpty() ) it.next(); //workaround per saltare il primo!!!! (ATTENZIONE!!!)
//			ObjectWithDistance curr = it.next();
//			ClassLabel currLabel = ((ILabeled) curr.obj).getLabel();
//			
////			assert(curr.dist<=1.0);
//			double currSimilarity = 1.0-curr.dist;
//			sum += currSimilarity;
//			
//			if ( currLabel.equals(givenLabel) ) {
//				givenLabelSum += currSimilarity;
//			}
//		}
//
//		return givenLabelSum/sum;
//		
//	}
		
//		HashMap<ClassLabel,Double> hm = new HashMap<ClassLabel,Double>();
//		
//		for ( Iterator<ObjectWithDistance<FeaturesCollection>> it = knn.iterator(); it.hasNext(); ) {
//			//if ( hm.isEmpty() ) it.next(); //workaround per saltare il primo!!!! (ATTENZIONE!!!)
//			ObjectWithDistance curr = it.next();
//			ClassLabel currLabel = ((HasLabel) curr.obj).getLabel();
//			assert(curr.dist<=1.0);
//			Double currSimilarity = 1.0-curr.dist;
//			Double value = hm.get(currLabel);
//			
//			if ( value != null ) {
//				hm.put( currLabel, currSimilarity + value);
//			} else {
//				hm.put( currLabel, currSimilarity);
//			}
//		}
//		
//		Entry<ClassLabel,Double> best = null;
//		
//		for ( Iterator<Entry<ClassLabel,Double>> it = hm.entrySet().iterator(); it.hasNext(); ) {
//			Entry<ClassLabel,Double> curr = it.next();
//			if ( best == null || curr.getValue() > best.getValue() ) {
//				best = curr;
//			}
//		}
//		
//		return best.getKey();
//	}

}
