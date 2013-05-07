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
import it.cnr.isti.vir.id.AbstractID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ConfusionMatrix {

	int[][] confMatrix = null; // [predicted][actual]
	AbstractLabel[] label = null;
	String expDescription ="";
	Double confidenceThreshold = null; 
	
	ArrayList<TestDocumentSingleLabeled> testDocCollection = new ArrayList<TestDocumentSingleLabeled>();
	
	int nullsFPCorr   = 0;
	int nullsFPUncorr = 0;
	
	int noConfCorr_notNull = 0;
	int noConfWrong_notNull= 0;
	
	private ConfusionMatrix(Collection<AbstractLabel> coll) {
		label = new AbstractLabel[coll.size()+1]; //plus one is for null classes
		confMatrix = new int[label.length][label.length];
		
		AbstractLabel[] arr = (AbstractLabel[]) coll.toArray(new AbstractLabel[coll.size()]);
		Arrays.sort(arr);
		
		for ( int i=0; i<arr.length; i++ ) {
			label[i+1]=arr[i];
		}
		
//		int i=1; // 0 is for null class
//		for (Iterator<AbstractLabel> it=coll.iterator(); it.hasNext(); i++ ) {
//			label[i] =it.next();
//		}

	};
	
	public ConfusionMatrix(
			Collection<AbstractLabel> labels,
			Collection<TestDocumentSingleLabeled> coll,
			Double confidenceThreshold) {
		
		this(labels);
		this.confidenceThreshold = confidenceThreshold;
		add(coll);
		
	}
	
	public void addExpDescription(String description) {
		expDescription += description;
	}
	
	public final void add(TestDocumentSingleLabeled doc ){
		testDocCollection.add(doc);
		PredictedLabel pr = doc.getPredictedLabel();
		AbstractLabel ac = doc.getActualLabel();
		int iac = getIndex(ac);
		int ipr = getIndex(pr.getcLabel());
		if ( iac != 0 ) 
			if ( iac == ipr )
				noConfCorr_notNull++;
			else
				noConfWrong_notNull++;
		if ( confidenceThreshold == null || pr.getConfidence() > confidenceThreshold ) {
			confMatrix[ipr][iac]++;
//			if ( ipr == 0) {
//				if ( iac != 0  ) null4ConfThrUncorr++;
//			}
		} else {
			// nulls because of the confidence threshold
			confMatrix[0][iac]++;
			
			// check if FP
			if ( iac != 0) {
				// actual not Null
				if ( iac == ipr ) nullsFPCorr++;
				else nullsFPUncorr++;
			}
		}

 	}
	
	public final void add(Collection<TestDocumentSingleLabeled> coll) {
		for (Iterator<TestDocumentSingleLabeled> it = coll.iterator(); it.hasNext(); ) {
			add(it.next());
		}
	}
	
	public double getNullsActualUncorrectPerc() {
		return (double) nullsFPUncorr/noConfWrong_notNull;
	}
	
	public double getNullsActualCorrectPerc() {
		return (double) nullsFPCorr/noConfCorr_notNull;
	}
	
	private int getIndex(AbstractLabel c) {
		if ( c == null ) return 0;
		for(int i=1; i<label.length; i++ ) {
			if ( label[i].equals(c) ) return i;
		}
		return -1;
	}
	
//	private int getIndex(PredictedLabel c) {
//		return getIndex(c.getcLabel());
//	}
	
	public double getAccuracy() {
		double dSum = getDiagSum();
		if ( dSum == 0) return 0;
		else return dSum / getSum();
	}
	
	protected final long getDiagSum() {
		long sum =0;
		for(int i=0; i<label.length; i++ ) {
			sum += confMatrix[i][i];
		}	
		return sum;
	}
	
	protected final long getSum() {
		long sum =0;
		for(int ir=0; ir<label.length; ir++ ) {
			for (int ic=0; ic<label.length; ic++ ) {
				sum += confMatrix[ir][ic];
			}
		}	
		return sum;
	}
	
	protected final long getSum_notAcNulls() {
		long sum =0;
		for(int ir=0; ir<label.length; ir++ ) {
			for (int ic=1; ic<label.length; ic++ ) {
				sum += confMatrix[ir][ic];
			}
		}	
		return sum;
	}

	public final double getMissedClassPerc() {
		return (double) getMissedClass() / (double) getSum_notAcNulls();
	}
	
	public final double getPredictedNullperc() {
		return (double) getPredictedNulls() / (double) getSum();
	}
	
	public final double getTPNullperc() {
		int positives = 0;
		int tp=confMatrix[0][0];
		for (int ic=0; ic<label.length; ic++ ) {
			positives += confMatrix[ic][0];
		}
		return (double) tp / (double) positives;
	}
	
	
	// true nulls are not considered!
	protected final long getMissedClass() {
		long sum =0;
		for (int ic=1; ic<label.length; ic++ ) {
			sum += confMatrix[0][ic];
		}
	
		return sum;		
	}
	
	// true nulls are not considered!
	protected final long getPredictedNulls() {
		long sum =0;
		for (int ic=0; ic<label.length; ic++ ) {
			sum += confMatrix[0][ic];
		}
	
		return sum;		
	}
	
	protected final long getNotNullsSum() {
		long sum =0;
		for(int ip=1; ip<label.length; ip++ ) {
			for (int ic=1; ic<label.length; ic++ ) {
				sum += confMatrix[ip][ic];
			}
		}	
		return sum;
	}
	
	public String toString() {
//		int[] truePositives  = new int[label.length];
//		int[] falsePositives = new int[label.length];
//		int[] falseNegatives  = new int[label.length];
//		for(int i=0; i<label.length; i++ ) {
//			truePositives[i]=0;
//			falsePositives[i]=0;	
//			falseNegatives[i]=0;
//		}
	
		int first = 0;
		//if ( !nullClassUsed() ) first = 0;
		String tStr = expDescription;
		tStr += "Confusion matrix (actual by column, assigned by row)\n";
		for(int i=first; i<label.length; i++ ) {
			// columns headers (actual)
			tStr += "\t" + label[i];
		}	
		tStr += "\n";
		
		// [actual][predicted]
		for(int ip=first; ip<label.length; ip++ ) {
			// first element in row is the predicted label of the elements in the row
			tStr += label[ip];
			for (int ia=first; ia<label.length; ia++ ) {
				tStr += "\t" + confMatrix[ip][ia];
			}
			tStr += "\n";
		}
	
		tStr += "\nPrecision";
		double[] precisions = getPrecisions();
		for(int i=first; i<label.length; i++ ) {
			tStr +=  "\t" + precisions[i];
		}
		
		tStr += "\nRecall   ";
		double[] recalls = getRecalls();
		for(int i=first; i<label.length; i++ ) {
			tStr +=  "\t" + recalls[i];
		}

		tStr += "\nF1s   ";
		double[] f1s = getF1s();
		for(int i=first; i<label.length; i++ ) {
			tStr +=  "\t" + f1s[i];
		}
		
		tStr += "\nNotClass\t" + this.getPredictedNullperc();
		tStr += "\nNotClassFP\t" + this.getMissedClassPerc();
		tStr += "\nNotClassFP_Unc\t" + this.getNullsActualUncorrectPerc();
		
		tStr += "\nAccuracy\t" + this.getAccuracy();
		tStr += "\nAccuracyOnClassified \t" + this.getAccuracyOnClassified();
		
		tStr += "\nF1MacroAvg\t" + ConfusionMatrix.getMacroAvg(getF1s());
		tStr += "\nF1MacroAvgOnClassified\t" + ConfusionMatrix.getMacroAvgOnClassified(getF1s());
				
		
		//tStr += "\nMicroRecallOnClassified \t"    + this.getMicroRecallOnClassified() + "\n";
		return tStr +"\n";
		
	}
	
	public boolean nullClassUsed() {
		for (int i=0; i<confMatrix[0].length; i++)
			if ( confMatrix[0][i]>0 ) return true;
		for (int i=0; i<confMatrix.length; i++)
			if ( confMatrix[i][0]>0 ) return true;
		return false;
	}
	
	public static double getMacroAvg(double[] measures) {
		double sum = 0;
		for (int i=0; i<measures.length; i++ ) {
			sum+=measures[i];
		}
		return sum/(double) measures.length;
	}
	
	public static double getMacroAvgOnClassified(double[] measures) {
		double sum = 0;
		for (int i=1; i<measures.length; i++ ) {
			sum+=measures[i];
		}
		return sum/(double) (measures.length-1);
	}
	
	
	public double getAccuracyOnClassified() {
		double num = (double) getDiagSum() - confMatrix[0][0];
		if ( num == 0 ) return 0;
		return num / getNotNullsSum();
	}
	
//	public double getMicroRecallOnClassified() {
//		
//		int truePositives  = 0;
//		int falseNegatives = 0;
//		
//		// [actual][predicted]
//		for(int ip=1; ip<label.length; ip++ ) {
//			// first element in row is the predicted label of the elements in the row
//			for (int ia=1; ia<label.length; ia++ ) {
//				if ( ip == ia ) {
//					truePositives += confMatrix[ip][ia]; // ip == ia
//				} else {
//					// ir is the predicted label / ic is the actual label
//					falseNegatives += confMatrix[ip][ia]; // assigned to ip is ia
//				}
//			}
//		}
//		
//		return (double) truePositives / (double) ( truePositives + falseNegatives );
//	}
	
	public double[] getPrecisions() {
		double[] precisions = new double[label.length];		
		int[] truePositives  = new int[label.length];
		int[] falsePositives = new int[label.length];
		for(int i=0; i<label.length; i++ ) {
			truePositives[i]=0;
			falsePositives[i]=0;	
		}
		
		// [actual][predicted]
		for(int ip=0; ip<label.length; ip++ ) {
			// first element in row is the predicted label of the elements in the row
			for (int ia=0; ia<label.length; ia++ ) {
				if ( ip == ia ) {
					truePositives[ip] += confMatrix[ip][ia]; // ip == ia
				} else {
					// ir is the predicted label / ic is the actual label
					falsePositives[ip] += confMatrix[ip][ia]; // assigned to ip is ia
				}
			}
		}
		for(int i=0; i<label.length; i++ ) {
			if ( truePositives[i] + falsePositives[i] == 0) precisions[i] = 1;
			else precisions[i] = (double) truePositives[i] / ( truePositives[i] + falsePositives[i] );
		}
		
		return precisions;
	}
	
	public double[] getF1s() {
		double[] f1s = new double[label.length];
		double[] recalls = getRecalls();
		double[] precisions = getPrecisions();
		for(int i=0; i<label.length; i++ ) {
			double num = 2.0 * recalls[i] * precisions[i] ;
			if ( num == 0 ) f1s[i]= 0;
			else f1s[i]= num / (recalls[i]+precisions[i]);
		} 
		return f1s;
	}
	
	public double[] getRecalls() {
		double[] recalls = new double[label.length];		
		int[] truePositives  = new int[label.length];
		int[] falseNegatives = new int[label.length];
		for(int i=0; i<label.length; i++ ) {
			truePositives[i]=0;
			falseNegatives[i]=0;	
		}
		
		// [actual][predicted]
		for(int ip=0; ip<label.length; ip++ ) {
			// first element in row is the predicted label of the elements in the row
			for (int ia=0; ia<label.length; ia++ ) {
				if ( ip == ia ) {
					truePositives[ip] += confMatrix[ip][ia]; // ip == ia
				} else {
					// ir is the predicted label / ic is the actual label
					falseNegatives[ia] += confMatrix[ip][ia]; // assigned to ip is ia
				}
			}
		}
		for(int i=0; i<label.length; i++ ) {
			if ( truePositives[i] + falseNegatives[i] == 0) recalls[i] = 1;
			else recalls[i] = (double) truePositives[i] / ( truePositives[i] + falseNegatives[i] );
		}
		
		return recalls;
	}	
	
	public static double[] getBestAccuracy(ConfusionMatrix[][] cms, double[] confThrImg, double[] confThrLF) {
		double bestValue = Double.MIN_VALUE;
		int bestICImg = -1;
		int bestICLF = -1;
		for (int iCImg=0; iCImg<confThrImg.length; iCImg++ ) {
			for (int iCLF=0; iCLF<confThrLF.length; iCLF++ ) {
				double currValue = cms[iCImg][iCLF].getAccuracy();
				if ( currValue>bestValue) {
					bestICImg = iCImg;
					bestICLF = iCLF;
					bestValue = currValue;
				}
			}
		}
		double[] res = {bestValue, confThrImg[bestICImg], confThrImg[bestICLF]};
		return res;
	}
	
	public static void printStats(ConfusionMatrix[][] cms, int[] v, String s1, double[] v2, String s2) {
		double[] vd1 = new double[v.length];
		for (int i=0; i<v.length; i++) {
			vd1[i]=v[i];
		}
		printStats(cms, vd1, s1, v2, s2);
	}
	
	public static void printStats(ConfusionMatrix[][] cms, double[] confThrImg, double[] confThrLF) {
		printStats(cms, confThrImg, "confThrImg", confThrLF, "confThrLF");
	}
	
	public static void printStats(ConfusionMatrix[][] cms, double[] v1, String str1, double[] v2, String str2) {

		System.out.println("NotClassifiedPercentage");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getPredictedNullperc()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");

		System.out.println("NullsRecall");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getTPNullperc()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("NullsFalsePositives_overAcNotNulls");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getMissedClassPerc()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("NullsFalsePositives_overAcNotNullsWrong");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getNullsActualUncorrectPerc()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("NullsFalsePositives_overAcNotNullsCorrect");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getNullsActualCorrectPerc()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		/// ALL ///
		
		System.out.println("Precision_MacroAvg");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				ConfusionMatrix curr = cms[iCImg][iCLF];
				System.out.print(getMacroAvg(curr.getPrecisions())+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("Recall_MacroAvg");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				ConfusionMatrix curr = cms[iCImg][iCLF];
				System.out.print(getMacroAvg(curr.getRecalls())+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		double bestMacroF1 = -1;
		int bestMacroF1Index1 = -1;
		int bestMacroF1Index2 = -1;
		System.out.println("F1_MacroAvg");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int i1=0; i1<v1.length; i1++ ) {
			System.out.print(v1[i1]+"\t");
			for (int i2=0; i2<v2.length; i2++ ) {
				ConfusionMatrix currMatrix = cms[i1][i2];
				double curr = getMacroAvg(currMatrix.getF1s());
				System.out.print(curr+"\t");
				if ( curr > bestMacroF1 ) {
					bestMacroF1 = curr;
					bestMacroF1Index1=i1;
					bestMacroF1Index2=i2;
				}
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");

		double bestAcc = -1;
		int bestAccIndex1 = -1;
		int bestAccIndex2 = -1;
		System.out.println("Accuracy");
		for (int i1=0; i1<v2.length; i1++ ) System.out.print("\t"+v2[i1]);
		System.out.print("\t"+str2+"\n");
		for (int i1=0; i1<v1.length; i1++ ) {
			System.out.print(v1[i1]+"\t");
			for (int i2=0; i2<v2.length; i2++ ) {
				double curr = cms[i1][i2].getAccuracy();
				System.out.print(curr+"\t");
				if ( curr > bestAcc ) {
					bestAcc = curr;
					bestAccIndex1=i1;
					bestAccIndex2=i2;
				}
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		/// ON CLASSIFIED ///
		
		System.out.println("Precision_MacroAvg_OnClassified");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				ConfusionMatrix curr = cms[iCImg][iCLF];
				System.out.print(getMacroAvgOnClassified(curr.getPrecisions())+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("Recall_MacroAvg_OnClassified");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				ConfusionMatrix curr = cms[iCImg][iCLF];
				System.out.print(getMacroAvgOnClassified(curr.getRecalls())+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		double bestMacroF1OnC = -1;
		int bestMaF1OnCIndex1 = -1;
		int bestMaF1OnCIndex2 = -1;
		System.out.println("F1_MacroAvg_OnClassified");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				ConfusionMatrix currMatrix = cms[iCImg][iCLF];
				double curr = getMacroAvgOnClassified(currMatrix.getF1s());
				System.out.print(curr+"\t");
				if ( curr > bestMacroF1OnC ) {
					bestMacroF1OnC = curr;
					bestMaF1OnCIndex1=iCImg;
					bestMaF1OnCIndex2=iCLF;
				}
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("Accuracy_OnClassified");
		for (int iCLF=0; iCLF<v2.length; iCLF++ ) System.out.print("\t"+v2[iCLF]);
		System.out.print("\t"+str2+"\n");
		for (int iCImg=0; iCImg<v1.length; iCImg++ ) {
			System.out.print(v1[iCImg]+"\t");
			for (int iCLF=0; iCLF<v2.length; iCLF++ ) {
				System.out.print(cms[iCImg][iCLF].getAccuracyOnClassified()+"\t");
			}
			System.out.print("\n");
		}
		System.out.print(str1+"\n");
		
		System.out.println("Measure     \tValue\tk\tconfThr\tsim");
		System.out.println("Best Acc    \t" +  bestAcc + "\t" + v1[bestAccIndex1] + "\t" + v2[bestAccIndex2]);
		System.out.println("Best MacroF1\t"  +  bestMacroF1 + "\t"  + v1[bestMacroF1Index1] + "\t" + v2[bestMacroF1Index2] );
		System.out.println("Acc    \t"  +  cms[0][0].getAccuracy()+ "\t"  + v1[0] + "\t" + v2[0] );
		System.out.println("MacroF1\t"  +  getMacroAvg(cms[0][0].getF1s())+ "\t"  + v1[0] + "\t" + v2[0] );
		// to be done!
//		System.out.println("Best MacroF1onC\t"  +  bestMacroF1OnC + "\t"  + v1[bestMaF1OnCIndex1] +"\t"+ v2[bestMaF1OnCIndex2]); 
				
	}
	
	public void createHTML( File file, String imagesPath ) throws FileNotFoundException {
		PrintStream out = new PrintStream(new FileOutputStream(file ));
		
		Collections.sort(testDocCollection);
		
		out.println("<HTML><HEAD></HEAD>");
		out.println("<BODY>");
		out.println("Classification test results.<br>");
		out.println("  Negative confidence stands for missclasifeds.<br>");
		out.println("  Results ordered with respect to increasing confidence.<br>");
		out.println("  Only worst 100 classifications reported<br>");
		for ( int i=0; i<testDocCollection.size() && i<100; i++ ) {
			TestDocumentSingleLabeled curr = testDocCollection.get(i);
			AbstractID id = curr.getID();
			double conf = curr.getPredictedLabel().getConfidence();
			if ( !curr.correctlyAssigned() ) conf = -conf;
			String imgFName = imagesPath + id + ".jpg";
			out.print("<A HREF=\""+ imgFName + "\">");
			out.print("<IMG SRC=\""+ imgFName + "\" HEIGHT=150 TITLE=\"" + id +"\">");
			out.println("</A>   ");
			
			AbstractID[] simIDs = curr.getPredictedLabel().getSimilars();
			if (  simIDs != null ) {
				for ( int i2=0; i2<simIDs.length && i2<5; i2++) {
					int height = 50;
					if ( i2 == 0) height = 100;
					imgFName = imagesPath + java.io.File.separator + simIDs[i2] + ".jpg";
					out.println("<A HREF=\""+ imgFName + "\">");
					out.println("<IMG SRC=\""+ imgFName + "\" HEIGHT="+ height +" TITLE=\"" + simIDs[i2] +"\">");
					out.println("</A> ");
				}
			}
			out.println("<br>");
			
//			out.println( id + "<br>");
			out.format("%10.3f ", conf);
			out.println( curr.getPredictedLabel().getcLabel() + " [" + curr.getActualLabel() + "]<br><br>");
		}
		out.println("</BODY>");
	}
	
}
