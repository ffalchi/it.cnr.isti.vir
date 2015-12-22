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
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.AbstractFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.localfeatures.SURF;
import it.cnr.isti.vir.geom.AffineTransformation;
import it.cnr.isti.vir.geom.HomographyTransformation;
import it.cnr.isti.vir.geom.RSTTransformation;
import it.cnr.isti.vir.geom.TransformationHypothesis;
import it.cnr.isti.vir.geom.Transformations;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;
import it.cnr.isti.vir.similarity.LoweHoughTransform;
import it.cnr.isti.vir.similarity.SimilarityOptionException;
import it.cnr.isti.vir.similarity.knn.MultipleKNNPQueueID;
import it.cnr.isti.vir.similarity.knn.QueriesOrder1;
import it.cnr.isti.vir.similarity.metric.ILocalFeaturesMetric;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueLowe2NN;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_kNN;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.SplitInGroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Vector;

public class LFCollClassifier<LF extends ALocalFeature>  implements IClassifier, ILFClassifier  {

	public static final boolean rejectUnConsistent = false;
	
	private ILocalFeaturesMetric<LF> sim;
	private final List<LF> coll;
//	private Integer triesMax = 10;
//	private Integer nObjects = 100;
	private boolean pivotedFiltering = false;
	private Integer k = null;
	private final Double lfConfThr ;
	private final boolean weightedSum;
	private static final boolean notSameFCID = true;
	
	private static final boolean sqrConfidence = true;
	private static final boolean absoluteConfidence = false;
	
	private boolean geometryConsistencyCheck = false;
	private Class tr;
	Integer RANSAC_cycles;
	Integer RANSAC_nHoughMaxForRANSAC;
	Double RANSAC_error;
	Double RANSAC_minDist;
	double[] RANSAC_minMaxSR;
	private boolean justCount = false;
//	private LFWords words = null;
//	private int bagOfWordsFilterSize = -1;
//	
//	public void setBagOfWordsFilter(LFWords words, int bOW_k) {
//		this.words = words;
//		bagOfWordsFilterSize = bOW_k;
//	}
	
	
//	public LFCollClassifier(Properties properties) {
//		
//	}
	
	public void setProperties(Properties properties) throws SimilarityOptionException {
		String LFCollClassifier_geometry  = properties.getProperty("LFCollClassifier_geometry");
		if ( LFCollClassifier_geometry != null ) {
			geometryConsistencyCheck = Boolean.parseBoolean(LFCollClassifier_geometry);
			System.out.println("geometryConsistencyCheck set to " + geometryConsistencyCheck);
		}
		
		String LFCollClassifier_justCount  = properties.getProperty("LFCollClassifier_justCount");
		if ( LFCollClassifier_geometry != null ) {
			justCount = Boolean.parseBoolean(LFCollClassifier_justCount);
			System.out.println("justCount set to " + justCount);
		}
		
		String RANSAC_tr = properties.getProperty("LFCollClassifier_geometry");
		String value = properties.getProperty("RANSAC_tr");
		if ( value != null ) {
			if ( value.equals("RST")) {
				tr = RSTTransformation.class;
			} else if ( value.equals("Affine")) {
				tr = AffineTransformation.class;
			} else if ( value.equals("Homography")) {
				tr = HomographyTransformation.class;
			} else {
				throw new SimilarityOptionException("Option " + value + " not found!");
			}
			System.out.println("RANSAC TR: " +  tr );
		}
		
		value = properties.getProperty("RANSAC_cycles");
		if ( value != null) {
			RANSAC_cycles = Integer.parseInt(value);   
			System.out.println("RANSAC cycles: " + RANSAC_cycles);
		}
		value = properties.getProperty("RANSAC_nBackets");
		if ( value != null) {
			RANSAC_nHoughMaxForRANSAC = Integer.parseInt(value);
			System.out.println("RANSAC nHoughMaxForRANSAC: " + RANSAC_nHoughMaxForRANSAC);
		}
		value = properties.getProperty("RANSAC_err");
		if ( value != null) {
			RANSAC_error = Double.parseDouble(value);
			System.out.println("RANSAC errorPerc: " + RANSAC_error);				
		}
		value = properties.getProperty("RANSAC_minDist");
		if ( value != null) {
			RANSAC_minDist = Double.parseDouble(value);
			System.out.println("RANSAC minDist: " + RANSAC_minDist);				
		}
		
		value = properties.getProperty("RANSAC_minSR");
		if ( value != null) {
			RANSAC_minMaxSR = new double[2];
			RANSAC_minMaxSR[0] = Double.parseDouble(value);
			System.out.println("RANSAC_minSR: " + RANSAC_minMaxSR[0]);				
		}
		
		value = properties.getProperty("RANSAC_maxSR");
		if ( value != null) {
			RANSAC_minMaxSR[1] = Double.parseDouble(value);
			System.out.println("RANSAC_maxSR: " + RANSAC_minMaxSR[1]);				
		}
		
		value = properties.getProperty("LFCollClassifier_k");
		if ( value != null) {
			k = Integer.parseInt(value);
			System.out.println("LFCollClassifier k: " + k);				
		}	
		
	}
	
//	private boolean leaveOneOut = false; // to not consider the very same;
//	
//	public void setLeaveOneOut(boolean leaveOneOut ) {
//		this.leaveOneOut = leaveOneOut;
//	}
//	
//	public boolean getLeaveOneOut( ) {
//		return leaveOneOut;
//	}
	
	//private LoweKNNClassifier lfClassifier = new LoweKNNClassifier();
	private AbstractKNNClassifier lfClassifier;
	private Class simPQueueClass;
	
	public boolean isPivotedFiltering() {
		return pivotedFiltering;
	}

	public void setPivotedFiltering(boolean pivotedFiltering) {
		this.pivotedFiltering = pivotedFiltering;
	}

	protected Double getDefaultLFConfThr() {
		if ( sim.getRequestedFeatureClass().equals(SURF.class) ) return  1-0.707;
		else if ( sim.getRequestedFeatureClass().equals(SIFT.class) ) return  1-0.8;
		//else if ( sim.getRequestedFeatureClass().equals(ColorSIFT.class) ) return  1-0.6;
		else  return null;
	}
	
	public MultipleKNNPQueueID createKNNs(Collection coll) {
		return new MultipleKNNPQueueID<LF>(		coll,
												k,
												sim,
												pivotedFiltering,
												new QueriesOrder1(10,100),  // ordering
												null,  						// nRecents
//												true, 						// distET
												false, 						// storeID
												simPQueueClass,
												true
										);
	}
	

	public LFCollClassifier(ILocalFeaturesMetric<LF> sim,
							Collection<AbstractFeaturesCollector_Labeled_HasID> givenColl,
							AbstractKNNClassifier lfClassifier,
							Class simPQueueClass,
							boolean weightedSum ) {
		this(sim, givenColl, 0.0, lfClassifier, simPQueueClass, null, weightedSum);
	}
	
	public LFCollClassifier(ILocalFeaturesMetric<LF> sim,
							Collection<AbstractFeaturesCollector_Labeled_HasID> givenColl,
							AbstractKNNClassifier lfClassifier,
							Class simPQueueClass,
							Integer k,
							boolean weightedSum ) {
		this(sim, givenColl, null, lfClassifier, simPQueueClass, k, weightedSum);
	}
	
	public List<LF> convert(Collection<AbstractFeaturesCollector_Labeled_HasID> givenColl ) {
		ArrayList<LF> coll = new ArrayList<LF> ();
		for ( Iterator<AbstractFeaturesCollector_Labeled_HasID> itfc=givenColl.iterator(); itfc.hasNext(); ) {
			AbstractFeaturesCollector_Labeled_HasID fc = itfc.next();
			//AbstractLFGroup<LF> fg = (AbstractLFGroup<LF>) fc.getFeature(AbstractLFGroup.getGroupClass(sim.getRequestedFeatureClass()));
			ALocalFeaturesGroup<LF> fg =
					(ALocalFeaturesGroup<LF>) fc.getFeature(sim.getRequestedFeatureGroupClass());
			for ( int i=0; i<fg.size(); i++) {
				coll.add( (LF) fg.getFeature(i) );
			}
		}
		//RandomOperations.shuffle(coll);
		return coll; 
	}
	
	public LFCollClassifier(ILocalFeaturesMetric<LF> sim, 
							Collection<AbstractFeaturesCollector_Labeled_HasID> givenColl,
							Double lfConfThr,
							AbstractKNNClassifier lfClassifier,
							Class simPQueueClass,
							Integer k,
							boolean w) {
		this.sim = sim;
		this.k = k;
		;
		weightedSum = w;
		
		this.lfConfThr = lfConfThr;
		this.lfClassifier = lfClassifier;
		this.simPQueueClass = simPQueueClass;
		
		if ( givenColl != null )
			coll = convert(givenColl);
		else 
			coll = null;
		System.out.println("LFCollClassifier created a training collection of "+coll.size()+ " local features.");
//		changeCollection(givenColl);

	}
	
	@Override
	public PredictedLabel classify(AbstractFeaturesCollector fc) throws InterruptedException {
		if ( lfConfThr != null)
			return classify(fc, lfConfThr);
		return classify(fc, this.getDefaultLFConfThr());
	}
	
	
	@Override
	public PredictedLabelWithSimilars classifyWithSimilars(AbstractFeaturesCollector fc) throws ClassifierException, InterruptedException {
		if ( lfConfThr != null)
			return classifyWithSimilars(fc, lfConfThr);
		return classifyWithSimilars(fc, this.getDefaultLFConfThr());
	}
	

	private MultipleKNNPQueueID<LF> getkNNs(AbstractFeaturesCollector fc) throws InterruptedException {
		Class<ALocalFeaturesGroup<?>> c = sim.getRequestedFeatureGroupClass();
		ALocalFeaturesGroup<LF> fg = (ALocalFeaturesGroup<LF>) fc.getFeature(c);
		MultipleKNNPQueueID<LF>  kNNs = createKNNs(fg.getCollection());
		kNNs.offer(coll);
		return kNNs;
//		//AbstractLFGroup<LF> fg = (AbstractLFGroup<LF>) fc.getFeature(AbstractLFGroup.getGroupClass(sim.getRequestedFeatureClass()));
//		ALocalFeaturesGroup<LF> fg = fc.getFeature(sim.getRequestedFeatureGroupClass());
//		final MultipleKNNPQueueID<LF>  kNNs = createKNNs(fg.getCollection());
//
//		final AbstractID fcID;
//		if ( IHasID.class.isInstance(fc) ) {
//			fcID = ((IHasID) fc).getID();
//		} else {
//			fcID = null;
//		}
//
//		for ( Iterator<LF> it = coll.iterator(); it.hasNext(); ) {
//			LF curr = it.next();
//			if ( 	notSameFCID
//					&& fcID != null
//					&& (
//							fcID.equals( ((ALocalFeature) curr).getLinkedGroup().getID() )
//						)) {
//				continue;
//			}
//			kNNs.offer(curr);
//		}
//		
//

	}
	

	// perform the classification only on the given collection
//	private MultipleKNNPQueueID<LF> getkNNs(AbstractFeaturesCollector fc, Collection<AbstractFeaturesCollector_Labeled_HasID> collection) {
//		Class<ALocalFeaturesGroup> c = sim.getRequestedFeatureGroupClass();
//		
//		//AbstractLFGroup<LF> fg = (AbstractLFGroup<LF>) fc.getFeature(AbstractLFGroup.getGroupClass(sim.getRequestedFeatureClass()));
//		ALocalFeaturesGroup<LF> fg = fc.getFeature(c);
//		MultipleKNNPQueueID<LF>  kNNs = createKNNs(fg.getCollection());
//		AbstractID fcID = null;
//		if ( IHasID.class.isInstance(fc) ) {
//			fcID = ((IHasID) fc).getID();
//		}
////		kNNs.offer(coll); tried to speed up did not much
//		for ( Iterator<AbstractFeaturesCollector_Labeled_HasID> itfc=collection.iterator(); itfc.hasNext(); ) {
//			AbstractFeaturesCollector_Labeled_HasID currFC = itfc.next();
//			//AbstractLFGroup<LF> currFG = (AbstractLFGroup<LF>) currFC.getFeature(AbstractLFGroup.getGroupClass(sim.getRequestedFeatureClass()));
//			ALocalFeaturesGroup<LF> currFG = fc.getFeature(c);
//			for ( int i=0; i<currFG.size(); i++) {
//				LF curr = (LF) currFG.getFeature(i);
//				if ( 	notSameFCID
//						&& fcID != null
//						&&  (
//								fcID.equals( ((ALocalFeature) curr).getLinkedGroup().getID() )
//							) ) {
//					//LEAVE ONE OUT ON ID
//					continue;
//				}
//				kNNs.offer(curr);
//			}
//		}
//		return kNNs;
//	}
	//
	private MultipleKNNPQueueID<LF> getkNNs(AbstractFeaturesCollector fc, Collection<AbstractFeaturesCollector_Labeled_HasID> collection) throws InterruptedException {		Class<ALocalFeaturesGroup<?>> c = sim.getRequestedFeatureGroupClass();		ALocalFeaturesGroup<LF> fg = (ALocalFeaturesGroup<LF>) fc.getFeature(c);		MultipleKNNPQueueID<LF>  kNNs = createKNNs(fg.getCollection());		kNNs.offer(convert(collection));		return kNNs;	}
	

	
	@Override
	public PredictedLabel classify(AbstractFeaturesCollector fc, Double lfConfThreshold) throws InterruptedException {
		return classify(getkNNs(fc), lfConfThreshold);
	}
	
	private PredictedLabelWithSimilars classifyWithSimilars(AbstractFeaturesCollector fc, Double lfConfThreshold) throws InterruptedException {
		return classifyWithSimilars(getkNNs(fc), lfConfThreshold);
	}
	
	@Override
	public PredictedLabel[] classify(AbstractFeaturesCollector fc, double[] lfConfThreshold) throws InterruptedException {
		return classify(getkNNs(fc), lfConfThreshold);
	}
	public PredictedLabel classify(AbstractFeaturesCollector fc, Collection training) throws InterruptedException {		return classify(getkNNs(fc, training), lfConfThr);	}		public PredictedLabelWithSimilars classifyWithSimilars(AbstractFeaturesCollector fc, Collection training) throws InterruptedException {		return classifyWithSimilars(getkNNs(fc, training), lfConfThr);	}		public PredictedLabel classify(AbstractFeaturesCollector fc, Double lfConfThreshold, Collection training) throws InterruptedException {		return classify(getkNNs(fc, training), lfConfThreshold);	}
	public PredictedLabel[] classify(AbstractFeaturesCollector fc, double[] lfConfThreshold, Collection training) throws InterruptedException {		return classify(getkNNs(fc, training), lfConfThreshold);	}		public PredictedLabelWithSimilars[] classifyWithSimilars(			AbstractFeaturesCollector fc, double[] lfConfThreshold,			Collection training) throws InterruptedException {		return classifyWithSimilars(getkNNs(fc, training), lfConfThreshold);	}
	
	public final LinkedList<TestDocumentSingleLabeled>[] classify(AbstractLabel[] acLabel, ISimilarityResults[][] results, double[] lfConfThr ) throws InterruptedException {
//		PredictedClassLabelWithSimilars[] prLabel = new PredictedClassLabelWithSimilars[lfConfThreshold.length];
//		LinkedList<TestDocumentSingleLabeled> res = new LinkedList();
//		for ( int i=0; i<pr.length; i++) {
//			prLabel[i]=classify(kNNs, lfConfThreshold[i]);
//			res.add(new TestDocumentSingleLabeled(acLabel[i], prLabel[i]));
//		}
//		return res;
		
		LinkedList<TestDocumentSingleLabeled>[] testList = new LinkedList[lfConfThr.length];
		for ( int i=0; i<lfConfThr.length; i++)
			testList[i] = new LinkedList<TestDocumentSingleLabeled>();
		int count=0;
		long start = System.currentTimeMillis();
		for(int i=0; i<results.length; i++) {
			count++;
			PredictedLabel[] pr = this.classify(results[i], lfConfThr);
			for (int iCThr=0; iCThr<lfConfThr.length; iCThr++) {
				// TO DO!!
				testList[iCThr].add( new TestDocumentSingleLabeled( acLabel[i], pr[iCThr], null ) );
			}
		}
		return testList;
		
	}

	public final PredictedLabel[] classify(MultipleKNNPQueueID<LF> kNNs, double[] lfConfThreshold ) throws InterruptedException {
//		PredictedClassLabelWithSimilars[] pr = new PredictedClassLabelWithSimilars[lfConfThreshold.length];
//		for ( int i=0; i<pr.length; i++)
//			pr[i]=classify(kNNs, lfConfThreshold[i]);
//		return pr;
		return classify(kNNs.getResults(), lfConfThreshold);
	}
	
	private PredictedLabelWithSimilars[] classifyWithSimilars(
			MultipleKNNPQueueID<LF> getkNNs, double[] lfConfThreshold) throws InterruptedException {
		return classifyWithSimilars(getkNNs.getResults(), lfConfThreshold);
	}
	
	public final PredictedLabel classify(MultipleKNNPQueueID<LF> kNNs, double lfConfThreshold ) throws InterruptedException {
		return classifyWithSimilars(kNNs.getResults(), lfConfThreshold).getPredictedLabelOnly();
	}
	
	private PredictedLabelWithSimilars classifyWithSimilars(MultipleKNNPQueueID<LF> kNNs, Double lfConfThreshold) throws InterruptedException {
		return classifyWithSimilars(kNNs.getResults(), lfConfThreshold);
	}
	

	public final PredictedLabelWithSimilars[] classifyWithSimilars(ISimilarityResults<LF>[] res, double[] lfConfThreshold ) throws InterruptedException {
		PredictedLabelWithSimilars[] pr = new PredictedLabelWithSimilars[lfConfThreshold.length];
		for ( int i=0; i<pr.length; i++)
			pr[i]=classifyWithSimilars(res, lfConfThreshold[i]);
		return pr;
	}
	

	public final PredictedLabel[] classify(ISimilarityResults<LF>[] res, double[] lfConfThreshold ) throws InterruptedException {
		PredictedLabel[] pr = new PredictedLabel[lfConfThreshold.length];
		for ( int i=0; i<pr.length; i++)
			pr[i]=classifyWithSimilars(res, lfConfThreshold[i]).getPredictedLabelOnly();
		return pr;
	}
	
	
	
//	public final PredictedLabel classify(ISimilarityResults[] kNNs, double lfConfThreshold ) {
//		  
//		// --------------- CONSIDERING LABELS ------------------------//
//		HashMap<AbstractLabel, Double> hm = new HashMap<AbstractLabel, Double>(); // FOR
//																			// LABELS
//
//		for (int i = 0; i < kNNs.length; i++) {
//			// for each query interest point
//			
//			// local feature classifier
//			PredictedLabel pCurrLabel = lfClassifier.singleLabelClassify(kNNs[i]);
//			if (pCurrLabel.getConfidence() < lfConfThreshold)
//				continue;
//			AbstractLabel currLabel = pCurrLabel.getcLabel();
//
//			double incValue = 1.0;
//			if (weightedSum) {
//				// standard
//				incValue = pCurrLabel.getConfidence();
//				
//				if ( sqrConfidence )
//			    	 incValue = incValue*incValue;
//			}
//
//			// LABELS
//			Double value = hm.get(currLabel);
//			if (value != null) {
//				value += incValue;
//				hm.put(currLabel, value);
//			} else
//				hm.put(currLabel, incValue);
//
//
//		}
//
//		AbstractLabel best = null;
//		Double bestValue = 0.0;
//
//		// ClassLabel secondBest = null;
//		Double secondBestValue = 0.0;
//
//		Double valueSum = 0.0;
//
//		// searching best label
//		for (Iterator<Entry<AbstractLabel, Double>> it = hm.entrySet().iterator(); it
//				.hasNext();) {
//			Entry<AbstractLabel, Double> curr = it.next();
//			valueSum += curr.getValue();
//			if (curr.getValue() > bestValue) {
//				// secondBest = best;
//				secondBestValue = bestValue;
//				best = curr.getKey();
//				bestValue = curr.getValue();
//			} else if (curr.getValue() > secondBestValue) {
//				// secondBest = curr.getKey();
//				secondBestValue = curr.getValue();
//			}
//		}
//
//
//		// NON COMMITTATA (NUMERI PIU' GRANDI SIGNIFICA PIU' NULL COME
//		// RISPOSTSA)
//		// double bestValueThr = (double) kNNs.size()*0.1*0.3; // confidence di
//		// 0.3 di media
//		// //System.out.println("BestValue: " + bestValue + ", ValueSum: " +
//		// valueSum + ", lfConfThreshold: " + lfConfThreshold +
//		// ", bestValueThr " + bestValueThr + " IPoints: " + kNNs.size());
//		// // check if some lf had a match
//		// if ( bestValue < bestValueThr ) return new
//		// PredictedClassLabelWithSimilars(null, 0.0, new SimilarityResults(v));
//
//		if (valueSum == 0)
//			return new PredictedLabel(null, 0.0);
//
//		// CONFIDENCE FOR THE WHOLE IMAGE
//		double confidence = 0;
//		if (absoluteConfidence) {
//			confidence = bestValue / kNNs.length;
//		} else {
//			confidence = 1.0 - (secondBestValue / bestValue);
//		}
//		  
//		//if ( sqrConfidence ) confidence = Math.sqrt(confidence);
//		
//		return new PredictedLabel(best, confidence);
//		// return new PredictedClassLabelWithSimilars(best, confidence, new
//		// SimilarityResults(v));
//
//	}
	
	
	
	static class ParallelGeomChecks implements Runnable {
		private final int from;
		private final int to;
		private final LocalFeaturesMatches[] arrColl;
		private final SimPQueueLowe2NN[] pQueue;
		private final LFCollClassifier classifier;
		
		ParallelGeomChecks(
				SimPQueueLowe2NN[] pQueue,
				int from,
				int to,
				LocalFeaturesMatches[] arrColl,
				LFCollClassifier classifier) {
			this.from = from;
			this.to = to;
			this.arrColl = arrColl;
			this.pQueue = pQueue;
			this.classifier = classifier;
		}

		@Override
		public void run() {
			for (int iC = from; iC <= to; iC++) {
				// FOR EACH IMAGE MATCHES

				// Hough
				LocalFeaturesMatches curr = arrColl[iC];
				Hashtable<Long, LocalFeaturesMatches> ht =
						LoweHoughTransform.getLoweHoughTransforms_HT(curr.getMatches(), false, classifier.RANSAC_minMaxSR);
				if ( classifier.tr == null ) {
					// ONLY HOUGH
					LocalFeaturesMatches[] temp = LoweHoughTransform.orderHT(ht);
					if ( temp.length == 0 || temp[0].size() <= 1 ) continue;	

					// updating lfConf
					for ( int i=0; i<temp[0].size(); i++ ){
						LocalFeatureMatch match = temp[0].get(i);
						int index = match.getIndex();
						double currDist = match.getWeight();

						// gets only the nearest point
						pQueue[index].offer( match.lfMatching, currDist );

					}

				} else {
					// RANSAC
					int nPoint = Transformations.getNPointsForEstimation(classifier.tr);

					ArrayList<TransformationHypothesis> hyp = curr.getRANSAC(ht, classifier.RANSAC_cycles, classifier.RANSAC_nHoughMaxForRANSAC, classifier.RANSAC_error, classifier.tr, classifier.RANSAC_minDist, true, LFCollClassifier.rejectUnConsistent);
					if ( hyp != null && hyp.size() != 0 ) {
						TransformationHypothesis bestHyp = hyp.get(0);
						ArrayList<LocalFeatureMatch> matches = bestHyp.getMatches().getMatches();
						//							double distance = 1.0 - (double) bestHyp.getMatches().getWeightSum() / kNNs.length;
						//							if ( distance < 0 ) distance = 0.0;
						//							tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));

						int size = matches.size();
						if ( size <= nPoint )
							continue;
						// updating lfConf
						for ( int i=0; i<size; i++ ){
							LocalFeatureMatch match = matches.get(i);
							int index = match.getIndex();
							// WEIGHT 
							double currDist = match.getWeight();
							pQueue[index].offer( match.lfMatching, currDist );
						}
					}
				}
			}
		}
	}                

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public final ISimilarityResults[] geomFilter( ISimilarityResults[] kNNs ) throws InterruptedException {
		HashMap<ALocalFeaturesGroup,LocalFeaturesMatches> hashMap = new HashMap();
			
		ILabeled[] absBest = new ILabeled[kNNs.length];
		double[] absoluteFirstDist = new double[kNNs.length];
		
		ILabeled[] absSecond = new ILabeled[kNNs.length];
		double[] absSecondLabelDist  = new double[kNNs.length];
//		double[] bestLabelsDist   = new double[kNNs.length];
				
		//double[] absoluteLastDist = new double[kNNs.length];
		
//		for ( int i=0; i<kNNs.length; i++) {
//			bestLabelsDist[i] = secondLabelDist[i] = 1.0;
//		}
		SimPQueueLowe2NN[] pQueue = new SimPQueueLowe2NN[kNNs.length];
		for (int i = 0; i < kNNs.length; i++)
			pQueue[i] = new SimPQueueLowe2NN();
		
		ALocalFeaturesGroup queryGroup = ((ALocalFeature) kNNs[0].getQuery()).getLinkedGroup();
		float[] bofIDF = queryGroup.getBofIDF();
//		HashSet noMultiplePointHashSet = new HashSet();
		for (int i = 0; i < kNNs.length; i++) {
			ObjectWithDistance first = kNNs[i].getFirst();
			double firstDist = first.getDist();
			absoluteFirstDist[i] = firstDist;
			absBest[i] = (ILabeled) first.getObj();
			AbstractLabel firstLabel = absBest[i].getLabel();
			
			for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
				ObjectWithDistance curr = it.next();
				ALocalFeature lf = (ALocalFeature) curr.getObj();
				double dist = curr.getDist();
				AbstractLabel currLabel = lf.getLabel();
				if ( currLabel != firstLabel ) {
					absSecond[i] = lf;
					absSecondLabelDist[i] = dist;
					break;
				}
			}
			
			ALocalFeature qLF = (ALocalFeature) kNNs[i].getQuery();
						
//			noMultiplePointHashSet.clear();
			
			// Groups local features results belonging to the same iamge
			for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
				
				ObjectWithDistance curr = it.next();
				ALocalFeature lf = (ALocalFeature) curr.getObj();
				
				// ASSIGN A WEIGHT TO THE MATCH
				double dist = curr.getDist();
				
//				if ( !it.hasNext() ) {
//					absoluteLastDist[i] = dist;
//				}
				AbstractLabel currLabel = lf.getLabel();
//				if ( currLabel != firstLabel ) {
//					break;
//				}
//				double lfConf = 1.0 - ( dist / secondLabelDist );
				
				ALocalFeaturesGroup currGroup = lf.getLinkedGroup();
				
				// to avoid multiple points from same group
//				if ( !noMultiplePointHashSet.add(currGroup) ) continue;
				
//				double currConf = lfConf;
//				if ( bofIDF != null ) currConf *= bofIDF[i];
//				if (sqrConfidence) currConf*=currConf;
				
				LocalFeaturesMatches currMatches = null;
				if ( ( currMatches = hashMap.get(currGroup)) == null ) {
					currMatches = new LocalFeaturesMatches();
					currMatches.add(new LocalFeatureMatch( qLF, lf, dist, i));
					hashMap.put(currGroup, currMatches);
				} else {
					currMatches.add(new LocalFeatureMatch( qLF, lf, dist, i));
				}
				
			}
		}
		Collection<LocalFeaturesMatches> c = hashMap.values();
		

		if ( justCount == true ) {
			// should be equivalent to 1nn Classifier
			for ( int i=0; i<kNNs.length; i++ ) {
				pQueue[i].offer(absBest[i], absoluteFirstDist[i]);
				pQueue[i].offer(absSecond[i], absSecondLabelDist[i]);
			}
			
//			 // updating lfConf
//			 for ( int i=0; i<kNNs.length; i++ ){
//				double firstD = kNNs[i].getFirst().getDist();
//				double secondLabelDist = 0;
//				for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
//					ObjectWithDistance curr = it.next();
//					ILocalFeature lf = (ILocalFeature) curr.getObj();
//					double dist = curr.getDist();
//					AbstractLabel currLabel = lf.getLabel();
//					if ( currLabel != labels[i] ) {
//						secondLabelDist = dist;
//						break;
//					}
//				}
//				lfConf[i] = 1.0 - ( firstD / secondLabelDist );
//			 }	
			
		} else {
			LocalFeaturesMatches[] cArr = new LocalFeaturesMatches[c.size()];
			c.toArray(cArr);
			int threadN = ParallelOptions.reserveNFreeProcessors() +1 ;
	        Thread[] thread = new Thread[threadN];
	        int[] group = SplitInGroups.split(c.size(), thread.length);
	        int from=0;
	        for ( int i=0; i<group.length; i++ ) {
	        	int curr=group[i];
	        	if ( curr == 0 ) break;
	        	int to=from+curr-1;
	        	thread[i] = new Thread( new ParallelGeomChecks(pQueue, from,to,cArr, this) ) ;
	        	thread[i].start();
	        	from=to+1;
	        }
	        
	        for ( Thread t : thread ) {
        		if ( t != null ) t.join();
	        }
			ParallelOptions.free(threadN-1);
//			// FOR EACH IMAGE MATCHES
//			for ( Iterator<LocalFeaturesMatches> it = c.iterator(); it.hasNext();  ) {
//				
//				// Hough
//				LocalFeaturesMatches curr = it.next();
//				Hashtable<Long, LocalFeaturesMatches> ht =
//					LoweHoughTransform.getLoweHoughTransforms_HT(curr.getMatches(), false, RANSAC_minMaxSR);
//				if ( tr == null ) {
//					// ONLY HOUGH
//					LocalFeaturesMatches[] temp = LoweHoughTransform.orderHT(ht);
//					 if ( temp.length == 0 || temp[0].size() <= 1 ) continue;	
//					 
//					 // updating lfConf
//					 for ( int i=0; i<temp[0].size(); i++ ){
//						LocalFeatureMatch match = temp[0].get(i);
//						int index = match.getIndex();
//						double currDist = match.getWeight();
//						
//						// this just get the nearest point
//						pQueue[index].offer( match.lfMatching, currDist );
//						
////						AbstractLabel currLabel = match.lfMatching.getLinkedGroup().getLabel();
////						if ( currDist < bestLabelsDist[index]  ) {
////							// new best found
////							secondLabelDist[index]  = bestLabelsDist[index];
////							bestLabelsDist[index] = currDist;
////							bestLabel[index] = currLabel;
////						} else {
////							if ( currLabel != bestLabel[index]
////							     && currDist < secondLabelDist[index] ) {
////								// updating second
////								secondLabelDist[index] = currDist;
////							}
////						}
//				 	 }
//
////					 double weightSum = 0;
////					 for ( int i=0; i<temp[0].size(); i++ ){
////						 weightSum += temp[0].get(i).getWeight();
////					 }
////					 double distance = 1.0 - (double) temp[0].size() / kNNs.length;
////					 if ( distance < 0 ) distance = 0.0;
////					 tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));
//				} else {
//					// RANSAC
//					int nPoint = Transformations.getNPointsForEstimation(tr);
//					
//					ArrayList<TransformationHypothesis> hyp = curr.getRANSAC(ht, RANSAC_cycles, RANSAC_nHoughMaxForRANSAC, RANSAC_error, tr, RANSAC_minDist, true);
//					if ( hyp != null && hyp.size() != 0 ) {
//						TransformationHypothesis bestHyp = hyp.get(0);
//						ArrayList<LocalFeatureMatch> matches = bestHyp.getMatches().getMatches();
////						double distance = 1.0 - (double) bestHyp.getMatches().getWeightSum() / kNNs.length;
////						if ( distance < 0 ) distance = 0.0;
////						tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));
//						
//						int size = matches.size();
//						if ( size <= nPoint )
//							continue;
//						 // updating lfConf
//						for ( int i=0; i<size; i++ ){
//							LocalFeatureMatch match = matches.get(i);
//							int index = match.getIndex();
//							// WEIGHT 
//							double currDist = match.getWeight();
//							pQueue[index].offer( match.lfMatching, currDist );
//					 	 }
//					}
//				}
//			}
		}
		
//		for ( int i=0; i<kNNs.length; i++ ) {
//			if ( pQueue[i].size() == 0 ) {
//				// no objects after filtering... standard LFw
////				pQueue[i].offer(absBest[i], absoluteFirstDist[i]);
////				pQueue[i].offer(absSecond[i], absSecondLabelDist[i]);
//			} else if ( pQueue[i].size() == 1 ) {
//				// just best.. we consider second best the absolute last with second label
//				if ( absSecond[i] != null )
//					pQueue[i].offer(new LabeledObject(null, absSecond[i].getLabel()), absoluteLastDist[i]);
//			}
//		}
		
		ISimilarityResults[] res = new ISimilarityResults[kNNs.length];
		for ( int i=0; i<kNNs.length; i++ ) {
			res[i] = pQueue[i].getResults();
			res[i].setQuery(kNNs[i].getQuery());
		}
		return res;
		
////		Collections.sort(tRes);
//		PredictedLabel[] prLabel= new PredictedLabel[kNNs.length];
//		for ( int i=0; i<prLabel.length; i++ ) {
//			double conf = 1.0 - bestLabelsDist[i] / secondLabelDist[i];
////			double conf = absoluteFirstDist[i] / bestLabelsDist[i];
//			if ( bofIDF != null ) conf*=bofIDF[i];
//			if (sqrConfidence) conf*=conf;			
//			prLabel[i] = new PredictedLabel(bestLabel[i], conf );
//		}
//		return prLabel;
	}
	
	public final PredictedLabel[] getPredictedLabels_geom( ISimilarityResults[] kNNs ) {
		
		HashMap<ALocalFeaturesGroup,LocalFeaturesMatches> hashMap = new HashMap();
		
		AbstractLabel[] labels = new AbstractLabel[kNNs.length];
		
		ALocalFeaturesGroup queryGroup = ((ALocalFeature) kNNs[0].getQuery()).getLinkedGroup();
		float[] bofIDF = queryGroup.getBofIDF();
//		if ( ((ILocalFeature) kNNs[0].getQuery()).getLinkedGroup().getID().equals(new IDString("2971367630"))) {
//			System.err.println("FOUND!");
//		}
		
		// for each LF in the query we have ISimilarityResults
		for (int i = 0; i < kNNs.length; i++) {
			ObjectWithDistance first = kNNs[i].getFirst();
			double firstDist = first.getDist();
			AbstractLabel firstLabel = ((ALocalFeature) first.getObj()).getLabel();
			labels[i] = firstLabel;
			Double secondLabelDist = null;
			
			
			for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
				ObjectWithDistance curr = it.next();
				ALocalFeature lf = (ALocalFeature) curr.getObj();
				double dist = curr.getDist();
				AbstractLabel currLabel = lf.getLabel();
				if ( currLabel != firstLabel ) {
					secondLabelDist = dist;
					break;
				}
			}
			
			ALocalFeature qLF = (ALocalFeature) kNNs[i].getQuery();
			for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
				ObjectWithDistance curr = it.next();
				ALocalFeature lf = (ALocalFeature) curr.getObj();
				double dist = curr.getDist();
				AbstractLabel currLabel = lf.getLabel();
				if ( currLabel != firstLabel ) {
					break;
				}
				double lfConf = 1.0 - ( dist / secondLabelDist );
				
				// TO DO !!!!
//				if ( lfConf < lfConfThreshold ) continue;
				
				ALocalFeaturesGroup currGroup = lf.getLinkedGroup();
				
				
				double currConf = lfConf;
				if ( bofIDF != null ) currConf *= bofIDF[i];
				if (sqrConfidence) currConf*=currConf;
				LocalFeaturesMatches currMatches = null;
				if ( ( currMatches = hashMap.get(currGroup)) == null ) {
					currMatches = new LocalFeaturesMatches();
					currMatches.add(new LocalFeatureMatch( qLF, lf, currConf, i));
					hashMap.put(currGroup, currMatches);
				} else {
					currMatches.add(new LocalFeatureMatch( qLF, lf, currConf, i));
				}
				
			}
		}
		
		Collection<LocalFeaturesMatches> c = hashMap.values();
		
		double[] lfConf = new double[kNNs.length];
		if ( justCount == true ) {
//			for ( Iterator<LocalFeaturesMatches> it = c.iterator(); it.hasNext();  ) {
//				LocalFeaturesMatches curr = it.next();
//				double distance = 1.0 - (double) curr.getWeightSum() / kNNs.length;
//				if ( distance < 0 ) distance = 0.0;
//				tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));
//			}
			
			 // updating lfConf
			 for ( int i=0; i<kNNs.length; i++ ){
				double firstD = kNNs[i].getFirst().getDist();
				double secondLabelDist = 0;
				for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
					ObjectWithDistance curr = it.next();
					ALocalFeature lf = (ALocalFeature) curr.getObj();
					double dist = curr.getDist();
					AbstractLabel currLabel = lf.getLabel();
					if ( currLabel != labels[i] ) {
						secondLabelDist = dist;
						break;
					}
				}
				lfConf[i] = 1.0 - ( firstD / secondLabelDist );
			 }	
			
		} else {
			
			for ( Iterator<LocalFeaturesMatches> it = c.iterator(); it.hasNext();  ) {
				LocalFeaturesMatches curr = it.next();
				Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(curr.getMatches(), false);
				if ( tr == null ) {
					// ONLY HOUGH
					LocalFeaturesMatches[] temp = LoweHoughTransform.orderHT_weightsSum(ht);
					 
					
					 // updating lfConf
					 for ( int i=0; i<temp[0].size(); i++ ){
						LocalFeatureMatch match = temp[0].get(i);
						int index = match.getIndex();
						double currConf = match.getWeight();
						if ( lfConf[index] < currConf ) {
							lfConf[index] = currConf;
						}
				 	 }

//					 double weightSum = 0;
//					 for ( int i=0; i<temp[0].size(); i++ ){
//						 weightSum += temp[0].get(i).getWeight();
//					 }
//					 double distance = 1.0 - (double) temp[0].size() / kNNs.length;
//					 if ( distance < 0 ) distance = 0.0;
//					 tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));
				} else {
					// RANSAC					
					ArrayList<TransformationHypothesis> hyp = curr.getRANSAC(ht, RANSAC_cycles, RANSAC_nHoughMaxForRANSAC, RANSAC_error, tr, RANSAC_minDist, true, LFCollClassifier.rejectUnConsistent);
					if ( hyp != null && hyp.size() != 0 ) {
						TransformationHypothesis bestHyp = hyp.get(0);
						ArrayList<LocalFeatureMatch> matches = bestHyp.getMatches().getMatches();
//						double distance = 1.0 - (double) bestHyp.getMatches().getWeightSum() / kNNs.length;
//						if ( distance < 0 ) distance = 0.0;
//						tRes.add(new ObjectWithDistance( curr.getMatchingLFGroup(), distance));
						
						 // updating lfConf
						 for ( int i=0; i<matches.size(); i++ ){
							LocalFeatureMatch match = matches.get(i);
							int index = match.getIndex();
							double currConf = match.getWeight();
							if ( lfConf[index] < currConf ) {
								lfConf[index] = currConf;
							}
					 	 }	
					}
				}
			}
		}
		
		PredictedLabel[] prLabel= new PredictedLabel[kNNs.length];
		for ( int i=0; i<prLabel.length; i++ ) {
			prLabel[i] = new PredictedLabel(labels[i], lfConf[i]);
		}
		return prLabel;
		
//		if ( tRes.size() == 0 ) {
//			return new PredictedLabelWithSimilars(null, 0.0, new SimilarityResults(tRes));
//		}
//		
//		ObjectWithDistance<AbstractLFGroup> best = tRes.get(0);
//		ObjectWithDistance<AbstractLFGroup> secondBest = null;
//		AbstractLabel bestLabel = best.getObj().getLabel();
//		for (int i=1; i<tRes.size(); i++) {
//			ObjectWithDistance<AbstractLFGroup> curr = tRes.get(i);
//			if ( !curr.getObj().getLabel().equals(bestLabel) ) {
//				secondBest = curr;
//				break;
//			}
//		}
//		double confidence = 1.0;
//		if ( secondBest != null ) {
//			confidence = 1.0 - (1.0-secondBest.getDist()) / (1.0 - best.getDist());
//		}
//
//		return new PredictedLabelWithSimilars(bestLabel, confidence, new SimilarityResults(tRes));
	}
	
	public final PredictedLabel[] getPredictedLabels( ISimilarityResults[] kNNs ) {
		PredictedLabel[] prLabel= new PredictedLabel[kNNs.length];
		// for each LF in the query we have ISimilarityResults
		for (int i = 0; i < kNNs.length; i++) {
			prLabel[i] = lfClassifier.singleLabelClassify(kNNs[i]);
		}
		return prLabel;
	}
	
	
	public final PredictedLabelWithSimilars classifyWithSimilars_sim(ISimilarityResults[] kNNs, double lfConfThreshold) {
		HashMap<ALocalFeaturesGroup,LocalFeaturesMatches> hashMap = new HashMap();
		
		//ArrayList<ObjectWithDistance> tRes = new ArrayList();
		SimPQueueDMax pQueue = new SimPQueueDMax();
		
		ALocalFeaturesGroup queryGroup = ((ALocalFeature) kNNs[0].getQuery()).getLinkedGroup();
//		float[] bofIDF = queryGroup.getBofIDF();

		for (int i = 0; i < kNNs.length; i++) {
			ObjectWithDistance first = kNNs[i].getFirst();
			double firstDist = first.getDist();
			AbstractLabel firstLabel = ((ALocalFeature) first.getObj()).getLabel();
			ALocalFeature qLF = (ALocalFeature) kNNs[i].getQuery();
			for ( Iterator<ObjectWithDistance> it = kNNs[i].iterator(); it.hasNext(); ) {
				ObjectWithDistance curr = it.next();
				ALocalFeature lf = (ALocalFeature) curr.getObj();
				double dist = curr.getDist();
				AbstractLabel currLabel = lf.getLabel();
				
				ALocalFeaturesGroup currGroup = lf.getLinkedGroup();
				
//				double currConf = lfConf;
//				if ( bofIDF != null ) currConf *= bofIDF[i];
//				if (sqrConfidence) currConf*=currConf;
				
				double currConf = dist;
				
				LocalFeaturesMatches currMatches = null;
				if ( ( currMatches = hashMap.get(currGroup)) == null ) {
					currMatches = new LocalFeaturesMatches();
					currMatches.add(new LocalFeatureMatch( qLF, lf, currConf, i));
					hashMap.put(currGroup, currMatches);
				} else {
					currMatches.add(new LocalFeatureMatch( qLF, lf, currConf, i));
				}
				
			}
		}
		Collection<LocalFeaturesMatches> c = hashMap.values();
		

		if ( justCount == true ) {
			
		} else {
			// Hough
			for ( Iterator<LocalFeaturesMatches> it = c.iterator(); it.hasNext();  ) {
				LocalFeaturesMatches curr = it.next();
				Hashtable<Long, LocalFeaturesMatches> ht = LoweHoughTransform.getLoweHoughTransforms_HT(curr.getMatches(), false);
				if ( tr == null ) {
					LocalFeaturesMatches[] temp = LoweHoughTransform.orderHT(ht);
					 					
//					 double weightSum = 0;
//					 for ( int i=0; i<temp[0].size(); i++ ){
//						 weightSum += temp[0].get(i).getWeight();
//					 }
					 double distance = 1.0 - (double) temp[0].size() / kNNs.length;
					 if ( distance < 0 ) distance = 0.0;
					 pQueue.offer( curr.getMatchingLFGroup(), distance*distance );
				} else {
				
					ArrayList<TransformationHypothesis> hyp = curr.getRANSAC(ht, RANSAC_cycles, RANSAC_nHoughMaxForRANSAC, RANSAC_error, tr, RANSAC_minDist, true, rejectUnConsistent);
					if ( hyp != null && hyp.size() != 0 ) {
						TransformationHypothesis bestHyp = hyp.get(0);
						ArrayList<LocalFeatureMatch> matches = bestHyp.getMatches().getMatches();
						double distance = 1.0 - (double) matches.size() / kNNs.length;
						if ( distance < 0 ) distance = 0.0;
						pQueue.offer( curr.getMatchingLFGroup(), distance*distance );
					}
				}
			}
		}
//		PredictedLabel predicted =  KNNClassifierDistWeighted.getPredictedClassLabel( pQueue.getResults(), 1);
		PredictedLabel predicted =  KNNClassifierDistWeighted.getPredictedClassLabel( pQueue.getResults(), pQueue.size());
		return new PredictedLabelWithSimilars(predicted, pQueue.getResults() );
		
//		ISimilarityResults res = pQueue.getResultsAndEmpty();
//
//		ObjectWithDistance first = res.getFirst();
//		AbstractLFGroup firstGroup = (AbstractLFGroup) first.getObj();
//		return new PredictedLabelWithSimilars(firstGroup.getLabel(), first.getDist(), res);
	}
	
	public final PredictedLabelWithSimilars classifyWithSimilars(ISimilarityResults[] kNN, double lfConfThreshold ) throws InterruptedException {

		PredictedLabel[] prLabel= null;
		
//		if ( true ) return this.classifyWithSimilars_sim( kNNs, lfConfThreshold ); 
		ISimilarityResults[] filteredkNN = kNN;
		
		if (geometryConsistencyCheck)
			if (simPQueueClass.equals(SimPQueue_kNN.class)) {
				filteredkNN = geomFilter(kNN);
				prLabel = getPredictedLabels(filteredkNN);
				
//				prLabel = getPredictedLabels_geomKnn(kNNs);
			} else {
				prLabel = getPredictedLabels_geom(kNN);
			}
		else
			prLabel = getPredictedLabels(kNN);
		
		// --------------- CONSIDERING LABELS ------------------------//
		HashMap<AbstractLabel, Double> hm = new HashMap<AbstractLabel, Double>();

		// --------------- Searching similars ------------------------//
		HashMap<ALocalFeaturesGroup, Double> hmGroups = new HashMap<ALocalFeaturesGroup, Double>();
		
		ALocalFeaturesGroup queryGroup = ((ALocalFeature) filteredkNN[0].getQuery()).getLinkedGroup();
		float[] idf = queryGroup.getBofIDF();
		
		// for each LF in the query we have ISimilarityResults
		for (int i = 0; i < prLabel.length; i++) {
			PredictedLabel pCurrLabel = prLabel[i];
			
			if (pCurrLabel.getConfidence() < lfConfThreshold) continue;
			AbstractLabel currLabel = pCurrLabel.getcLabel();
			

			// if ( currLabel != null ) {
			double incValue = 1.0;
			if (weightedSum) {
				incValue = pCurrLabel.getConfidence();
				if ( idf != null ) {
					incValue = incValue * idf[i];
				}
				else if (sqrConfidence)
					incValue = incValue * incValue;
			}

			// LABELS
			Double value = hm.get(currLabel);
			if (value != null) {
				value += incValue;
				hm.put(currLabel, value);
			} else
				hm.put(currLabel, incValue);

			ObjectWithDistance first = filteredkNN[i].getFirst();
			if ( first == null ) continue;
			//////////////////// TO DO !!!!!!!!!!!!!!!!!!!!!!!!!!!!
			ALocalFeaturesGroup<LF> currGroup = ((ALocalFeature) first.getObj()).getLinkedGroup();
			
			// SIMILARS
			value = hmGroups.get(currGroup);
			if (value != null) {
				value += incValue;
				hmGroups.put(currGroup, value);
			} else
				hmGroups.put(currGroup, incValue);

		}

		AbstractLabel best = null;
		Double bestValue = 0.0;

		// ClassLabel secondBest = null;
		Double secondBestValue = 0.0;

		Double valueSum = 0.0;

		// searching best label
		for (Iterator<Entry<AbstractLabel, Double>> it = hm.entrySet()
				.iterator(); it.hasNext();) {
			Entry<AbstractLabel, Double> curr = it.next();
			valueSum += curr.getValue();
			if (curr.getValue() > bestValue) {
				// secondBest = best;
				secondBestValue = bestValue;
				best = curr.getKey();
				bestValue = curr.getValue();
			} else if (curr.getValue() > secondBestValue) {
				// secondBest = curr.getKey();
				secondBestValue = curr.getValue();
			}
		}

		// searching nearest neighbors groups
		Vector<ObjectWithDistance<ALocalFeaturesGroup>> v = new Vector(hmGroups.size());
		int i = 0;
		for (Iterator<Entry<ALocalFeaturesGroup, Double>> it = hmGroups.entrySet()
				.iterator(); it.hasNext(); i++) {
			Entry<ALocalFeaturesGroup, Double> curr = it.next();
			// filtering not best class
			if (curr.getKey().getLabel().equals(best)) {
				// as distance we use 1-curr.getValue()/kNNs.size() < 1
				v.add(new ObjectWithDistance<ALocalFeaturesGroup>(curr.getKey(), 1
						- curr.getValue() / filteredkNN.length));
			}
		}
		Collections.sort(v); // sorting

		// NON COMMITTATA (NUMERI PIU' GRANDI SIGNIFICA PIU' NULL COME
		// RISPOSTSA)
		// double bestValueThr = (double) kNNs.size()*0.1*0.3; // confidence di
		// 0.3 di media
		// //System.out.println("BestValue: " + bestValue + ", ValueSum: " +
		// valueSum + ", lfConfThreshold: " + lfConfThreshold +
		// ", bestValueThr " + bestValueThr + " IPoints: " + kNNs.size());
		// // check if some lf had a match
		// if ( bestValue < bestValueThr ) return new
		// PredictedClassLabelWithSimilars(null, 0.0, new SimilarityResults(v));

		if (valueSum == 0)
			return new PredictedLabelWithSimilars(null, 0.0, new SimilarityResults(v));

		// CONFIDENCE FOR THE WHOLE IMAGE
		double confidence = 0;
		if (absoluteConfidence) {
			confidence = bestValue / filteredkNN.length;
		} else {
			confidence = 1.0 - (secondBestValue / bestValue);
		}
		// if ( sqrConfidence ) confidence = Math.sqrt(confidence);

		return new PredictedLabelWithSimilars(best, confidence,	new SimilarityResults(v));
		// return new PredictedClassLabelWithSimilars(best, confidence, new
		// SimilarityResults(v));

	}
	
//	public final PredictedClassLabelWithSimilars classify(MultipleKNNPQueueID<LF> kNNs,	double lfConfThreshold ) {
//		
//		// --------------- CONSIDERING LABELS ------------------------//
//		HashMap<ClassLabel, Double> hm = new HashMap<ClassLabel, Double>(); // FOR LABELS
//		
//		// --------------- Searching similars ------------------------//
//		HashMap<LocalFeaturesGroup, Double> hmGroups = new HashMap<LocalFeaturesGroup, Double>(); // FOR LABELS
//		
//		for ( int i=0; i<kNNs.size(); i++) {
//			PredictedClassLabel pCurrLabel = classify(kNNs, i);
//			if ( pCurrLabel.getConfidence()<lfConfThreshold ) continue;
//			ClassLabel currLabel = pCurrLabel.getcLabel();
//			
//			//-------------------- Searching similars --------------------------//
//			LocalFeaturesGroup<LF> currGroup = ((LocalFeature) kNNs.get(i).getFirstObject()).getLinkedGroup();
//		
////			if ( currLabel != null ) {
//				double incValue = 1.0;
//				if ( weightedSum ) {
//					incValue = pCurrLabel.getConfidence();
//				}
//				
//				// LABELS
//				Double value = hm.get(currLabel);
//				if ( value != null ) {
//					value += incValue;
//					hm.put(currLabel, value);
//				}
//				else hm.put(currLabel, incValue);
//				
//				// SIMILARS
//				value = hmGroups.get(currLabel);
//				if ( value != null ) {
//					value += incValue;
//					hmGroups.put(currGroup, value);
//				}
//				else hmGroups.put(currGroup, incValue);
//				
////			}
//				
//				
//		}
//		
//		ClassLabel best = null;
//		Double bestValue = 0.0;
//		
//		//ClassLabel secondBest = null;
//		Double secondBestValue = 0.0;
//		
//		Double valueSum = 0.0;
//		
//		//searching best label
//		for (Iterator<Entry<ClassLabel, Double>> it = hm.entrySet().iterator(); it.hasNext(); ) {
//			Entry<ClassLabel, Double> curr = it.next();
//			valueSum += curr.getValue();
//			if ( curr.getValue() > bestValue ) {
//				//secondBest = best;
//				secondBestValue = bestValue;
//				best = curr.getKey();
//				bestValue = curr.getValue();				
//			} else if ( curr.getValue() > secondBestValue ) {
//				//secondBest = curr.getKey();
//				secondBestValue = curr.getValue();
//			}
//		}
//
//		//searching nearest neighbors groups
//		Vector<ObjectWithDistance<LocalFeaturesGroup>> v = new Vector(hmGroups.size());
//		int i=0;
//		for (Iterator<Entry<LocalFeaturesGroup, Double>> it = hmGroups.entrySet().iterator(); it.hasNext(); i++) {
//			Entry<LocalFeaturesGroup, Double> curr = it.next();
//			// filtering not best class
//			if ( curr.getKey().getLabel().equals(best) ) {
//				// 	as distance we use 1-curr.getValue()/kNNs.size() < 1
//				v.add( new ObjectWithDistance<LocalFeaturesGroup>(curr.getKey(), 1-curr.getValue()/kNNs.size()) );
//			}
//		}
//		Collections.sort(v); // sorting
//		
//		// check if some lf had a match
//		if ( valueSum == 0 ) return new PredictedClassLabelWithSimilars(null, 0.0, new SimilarityResults(v));
//		
//		// CONFIDENCE FOR THE WHOLE IMAGE
//		double confidence = 1.0 - (secondBestValue / bestValue);
//		
//		return new PredictedClassLabelWithSimilars(best, confidence, new SimilarityResults(v));
//		
//	}

	@Override
	public LinkedList<TestDocumentSingleLabeled> classify(Collection testDocuments, Double lfConfThreshold) throws InterruptedException {
		double thr = this.getDefaultLFConfThr();
		if ( lfConfThreshold != null ) thr = lfConfThreshold;
		LinkedList<TestDocumentSingleLabeled> testList = new LinkedList<TestDocumentSingleLabeled>();
		long start = System.currentTimeMillis();
		int count = 0;
		for(Iterator<AbstractFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector_Labeled_HasID curr = it.next();
			count++;
			System.out.print( 	count + " of " + testDocuments.size() + "\t" + curr.getID() );
			long startTime = System.currentTimeMillis();
			PredictedLabel pr = this.classify(curr, thr);
			testList.add( new TestDocumentSingleLabeled(curr, pr ) );
			long endTime = System.currentTimeMillis();
			if ( curr.getLabel().equals(pr.getcLabel()) ) {
				System.out.print("correct ");
			} else {
				System.out.print("*WRONG* ");
			}
			System.out.print( 
				"\t" + pr.getcLabel() + "\t[" + curr.getLabel() + "]" + "\t" + pr.getConfidence() + 
				"\t" + (int) ((endTime-start)/(double) count*(testDocuments.size()-count)/1000.0/60.0) + "' rem." +
				"\t" + (endTime-startTime) + " msec" +
				"\n"
				);
		}
		return testList;
	}

	
	@Override
	public Collection<TestDocumentSingleLabeled>[] classify( Collection testDocuments, double[] lfConfThr, Collection trainingDocuments) throws InterruptedException {
		
		LinkedList<TestDocumentSingleLabeled>[] testList = new LinkedList[lfConfThr.length];
		for ( int i=0; i<lfConfThr.length; i++)
			testList[i] = new LinkedList<TestDocumentSingleLabeled>();
		int count=0;
		long start = System.currentTimeMillis();
		int correct = 0;
		for(Iterator<AbstractFeaturesCollector_Labeled_HasID> it = testDocuments.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector_Labeled_HasID curr = it.next();
			count++;
			System.out.print( count + " of " + testDocuments.size() + "\t" + curr.getID() + "\t" );
			long startTime = System.currentTimeMillis();
			PredictedLabel[] pr = null;
			if ( trainingDocuments != null ){
				pr = this.classify(curr, lfConfThr, trainingDocuments);
			} else {
				pr = this.classify(curr, lfConfThr );
			}
			long endTime = System.currentTimeMillis();
//			ObjectWithDistance<LF> bestRes = pr[0].getResults().getFirst();
			if (
					( pr[0].getcLabel() != null && pr[0].getcLabel().equals(curr.getLabel() ) )
					||
					( pr[0].getcLabel() == null && curr.getLabel() == null )
				)
			{
				System.out.print("correct ");
				correct++;
			} else {
				System.out.print("*WRONG* ");
			}
			System.out.format(" %.3f ", (double) correct / count);
			System.out.print( 
				"\t" + pr[0].getcLabel() + "\t" + curr.getLabel() + "\t" + pr[0].getConfidence() + "\t[confThr " +  lfConfThr[0]+"]" +
//				"\t{" + ((AbstractLFGroup) bestRes.obj).getID() + ", " +
//					"\t" + bestRes.dist + ", " +
//					"\t" +  ((AbstractLFGroup) bestRes.obj).getLabel() +
				"}" +
				"\t" + (endTime-startTime) + " msec" +
				"\t" + (int) (((endTime-start)/(double) count*(testDocuments.size()-count)/1000.0/60.0)) + "' rem.\n"
			);
			for (int i=0; i<lfConfThr.length; i++) {
				testList[i].add( new TestDocumentSingleLabeled(curr, pr[i] ) );
			}
		}
		return testList;
	}
	
	@Override
	public Collection<TestDocumentSingleLabeled>[] classify( Collection testDocuments, double[] lfConfThr) throws InterruptedException {
		return classify(testDocuments, lfConfThr, null);
	}
	
	@Override
	public LinkedList<TestDocumentSingleLabeled> classify(Collection<AbstractFeaturesCollector_Labeled_HasID> testDocuments) throws InterruptedException {
		return classify(testDocuments, (Double) null);
	}

}
