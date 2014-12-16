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
package it.cnr.isti.vir.features.bof;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClasses;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.BoFLF;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.features.localfeatures.BoFLFSoft;
import it.cnr.isti.vir.features.localfeatures.BoFLFSoftGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.similarity.ILFSimilarity;
import it.cnr.isti.vir.similarity.SimilarityClasses;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_kNN;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.util.RandomOperations;
import it.cnr.isti.vir.util.SplitInGroups;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.PriorityQueue;

public class LFWords<F extends AbstractFeature> {

	protected F[] fArr;
	protected final ILFSimilarity<F> sim;
	protected final Class<ALocalFeaturesGroup> featureGroupClass;
	protected final byte version = 2;
	protected float[] eval = null;

	protected F[] pArr;
	protected float[][] pDistances;
	
	protected int surfLaplace1Index = -1;
	protected int orderedAsPivots = 0;
	//protected MTreeIndex index;
	
	protected static final String miFileRefObjFileName = "refObjects.dat";
	protected static final String miFileLFWordsFileName = "lfwords.dat";

	//protected MI_File miFile = null;
//	protected Integer miFile_ref_objects_num = null;
//	protected Integer miFile_ki = null;
//	protected Integer miFile_kis = null;
	protected int size = -1;
	
	public F[] getFeatures() {
		return fArr;
	}

	public float[] getIDF(Collection coll  ) throws ArchiveException {
		float[] res = new float[size];
		int[] t = new int[size];
		for (Iterator it = coll.iterator(); it.hasNext(); ) {
			AbstractFeaturesCollector curr = (AbstractFeaturesCollector) it.next();
			BoFLFGroup currBoFGroup = (BoFLFGroup) curr.getFeature(BoFLFGroup.class);
			if ( currBoFGroup != null ) {
	//			currBoF.orderByBags();
				BoFLF[] arr = currBoFGroup.getLocalFeatures();
				int last = -1;
				for (int ib = 0; ib < arr.length; ib++) {
					int currBag = arr[ib].bag;
					if ( currBag == last)
						continue;
					t[ currBag ]++;
					last = currBag ;
				}
			} else {
				BoF currBof = curr.getFeature(BoF.class);
				int[] bags  = currBof.bag;
				int last = -1;
				for (int ib = 0; ib < bags.length; ib++) {
					int currBag = bags[ib];
					if ( currBag == last)
						continue;
					t[ currBag ]++;
					last = currBag ;
				}
			}
		}

		float size = coll.size();
		for (int i = 0; i < res.length; i++) {
			res[i] = (float) Math.log(size / (float) ( 1 + t[i] )) + 1.0F;
		}
		return res;
	}
	
	public float[] getIDF(FeaturesCollectorsArchive archive ) throws ArchiveException {
		return getIDF( new FeaturesCollectorsArchives( new FeaturesCollectorsArchive[]{archive} ) );
	}
	
	public float[] getIDF(FeaturesCollectorsArchives archives ) throws ArchiveException {
		return getIDF( archives, size );
	}
	
	public static float[] getIDF(FeaturesCollectorsArchive archive, int n ) throws ArchiveException {
		return getIDF( new FeaturesCollectorsArchives( new FeaturesCollectorsArchive[]{archive}), n );
	}
	
	public static float[] getIDF_inferringNWords(FeaturesCollectorsArchives archives ) throws ArchiveException {
	
		int n = getNumberOfWords(archives);
		
		return getIDF(archives, n);
	}
	
	public static int getNumberOfWords(FeaturesCollectorsArchives archives ) throws ArchiveException {
		int maxIDWord = -1;
		for ( int i=0; i<archives.getNArchives(); i++) {
			FeaturesCollectorsArchive archive = archives.getArchive(i);
			for (AbstractFeaturesCollector curr : archive  ) {
				BoFLFGroup currBoF = (BoFLFGroup) curr.getFeature(BoFLFGroup.class);
				BoFLF[] arr = currBoF.getLocalFeatures();
				for (int ib = 0; ib < arr.length; ib++) {
					int currBag = arr[ib].bag;
					if ( currBag > maxIDWord)
						maxIDWord = currBag;
				}
			}
		}
		return maxIDWord+1;
	}
	
	public static float[] getIDF(FeaturesCollectorsArchives archives, int n ) throws ArchiveException {
		float[] res = new float[n];
		int[] t = new int[n];
		
		for ( int i=0; i<archives.getNArchives(); i++) {
			FeaturesCollectorsArchive archive = archives.getArchive(i);
			for (AbstractFeaturesCollector curr : archive  ) {
	//			System.out.println(iA + " " + archives.getID(iA));
				BoFLFGroup currBoF = (BoFLFGroup) curr.getFeature(BoFLFGroup.class);
	//			currBoF.orderByBags();
				BoFLF[] arr = currBoF.getLocalFeatures();
				int last = -1;
				for (int ib = 0; ib < arr.length; ib++) {
					int currBag = arr[ib].bag;
					if ( currBag == last)
						continue;
					t[ currBag ]++;
					last = currBag ;
				}
			}
		}

		float size = archives.size();
		for (int i = 0; i < res.length; i++) {
			res[i] = (float) Math.log(size / (float) ( 1 + t[i] )) + 1.0F;
		}
		return res;
	}
	
	/*
	public float[] getIDF_byClass( FeaturesCollectorsArchives archives, HashMap<IDString, AbstractLabel> idClass ) throws ArchiveException {
		
//		HashMap<AbstractLabel,ArrayList<BoFLFGroup>> hMap = new HashMap();
		HashMap<AbstractLabel, MutableInt> classesHMap = new HashMap();
//		ArrayList<AbstractLabel> classesList = new ArrayList();
		// getting classes
		for (int iA=0; iA<archives.size(); iA++ ) {
			IFeaturesCollector curr = archives.get(iA);
//			AbstractLabel currLabel = ((ILabeled) curr).getLabel(); 
			AbstractLabel currLabel = idClass.get(((IHasID) curr).getID() ); 
			MutableInt occ = classesHMap.get(currLabel);
			if ( occ != null ) {
				occ.inc(); 
			} else {
				classesHMap.put( currLabel, new MutableInt(1) ); 
			}
			
		}
		int nClasses =  classesHMap.size();
		AbstractLabel[] label = new AbstractLabel[nClasses];
		int[] classOcc = new int[nClasses];
		int i=0; 
		for ( Iterator<AbstractLabel> it = classesHMap.keySet().iterator(); it.hasNext(); i++) {
			label[i] = it.next();
			MutableInt occ = classesHMap.get(label[i]);
			classOcc[i] = occ.get();
			System.out.println(label[i] + "\t" + classOcc[i]);
			// resetting value to i
			occ.set(i);
		}
		
		int[][] occ = new int[size][nClasses]; 
		
		
		for (int iA=0; iA<archives.size(); iA++ ) {
			IFeaturesCollector curr = archives.get(iA);
//			AbstractLabel currLabel = ((ILabeled) curr).getLabel(); 
			AbstractLabel currLabel = idClass.get(((IHasID) curr).getID() ); 
			int curriClass = classesHMap.get( currLabel ).get();
			BoFLFGroup currBoF = (BoFLFGroup) curr.getFeature(BoFLFGroup.class);
			BoFLF[] lf = (BoFLF[]) currBoF.getLocalFeatures();
			for ( int iLF=0; iLF<lf.length; iLF++) {
				occ[lf[iLF].bag][curriClass]++;
			}
		}
		
		int nDocs = archives.size();
		int nFeatures = 0;
		int[] occBag = new int[size];
		for (int iBag=0; iBag<occ.length; iBag++ ) {
			for ( int iC=0; iC<nClasses; iC++) {
				occBag[iBag] += occ[iBag][iC];
			}
			nFeatures += occBag[iBag];
		}

		System.out.println("Total number of features " + nFeatures + " [avg=" + (double) nFeatures/archives.size() + "]");
		
		float[] res = new float[size];
		for (int iBag=0; iBag<occ.length; iBag++ ) {
			for ( int iC=0; iC<nClasses; iC++) {
				int curr = occ[iBag][iC];
				if ( curr != 0 ) {
					res[iBag] += (float)
						(  (double ) 
							curr  / nDocs
							* Math.log (
									(double) curr * nDocs /
									( occBag[iBag] * classOcc[iC])
							)
						)
					;
				}
			}
		}

		return res;
	}
	*/
	/*
	public float[] getIDF_MutualInformation( FeaturesCollectorsArchives archives ) throws ArchiveException {
		float[] res = new float[size];
		int[] t = new int[size];
		
//		HashMap<AbstractLabel,ArrayList<BoFLFGroup>> hMap = new HashMap();
		HashMap<AbstractLabel, MutableInt> classesHMap = new HashMap();
//		ArrayList<AbstractLabel> classesList = new ArrayList();
		// getting classes
		for (int iA=0; iA<archives.size(); iA++ ) {
			IFeaturesCollector curr = archives.get(iA);
			AbstractLabel currLabel = ((ILabeled) curr).getLabel(); 
			MutableInt occ = classesHMap.get(currLabel);
			if ( occ != null ) {
				occ.inc(); 
			} else {
				classesHMap.put( currLabel, new MutableInt(1) ); 
			}
			
		}
		int nClasses =  classesHMap.size();
		AbstractLabel[] label = new AbstractLabel[nClasses];
		int[] classOcc = new int[nClasses];
		int i=0; 
		for ( Iterator<AbstractLabel> it = classesHMap.keySet().iterator(); it.hasNext(); i++) {
			label[i] = it.next();
			MutableInt occ = classesHMap.get(label[i]);
			classOcc[i] += occ.get();
			System.out.println(label[i] + "\t" + classOcc[i]);
			// resetting value to i
			occ.set(i);
		}
		int[][] occ = new int[size][nClasses]; 
		
		
		for (int iA=0; iA<archives.size(); iA++ ) {
			IFeaturesCollector curr = archives.get(iA);
			AbstractLabel currLabel = ((ILabeled) curr).getLabel(); 
			int curriClass = classesHMap.get( currLabel ).get();
			BoFLFGroup currBoF = (BoFLFGroup) curr.getFeature(BoFLFGroup.class);
			BoFLF[] lf = (BoFLF[]) currBoF.getLocalFeatures();
			for ( int iLF=0; iLF<lf.length; iLF++) {
				occ[lf[iLF].bag][curriClass]++;
			}
		}

		return null;
	}
	*/
	
	
	/*
     *
     */
//	public float[] getIDF_BoFLFOS(Collection<IFeaturesCollector> fcs) {
//		float[] res = new float[size];
//		int[] t = new int[size];
//		for (Iterator<IFeaturesCollector> it = fcs.iterator(); it.hasNext();) {
//			IFeaturesCollector curr = it.next();
//			BoF_LF_OriAndScale currBoF = (BoF_LF_OriAndScale) curr
//					.getFeature(BoF_LF_OriAndScale.class);
//			currBoF.orderByBags();
//			int[] bags = currBoF.getBagIndexes();
//
//			int last = -1;
//			for (int i = 0; i < bags.length; i++) {
//				if (bags[i] == last)
//					continue;
//				t[bags[i]]++;
//				last = bags[i];
//			}
//		}
//
//		float size = fcs.size();
//		for (int i = 0; i < res.length; i++) {
//			res[i] = (float) Math.log(size / t[i]);
//		}
//		return res;
//	}
	
	public void assignEvalIDF(FeaturesCollectorsArchives archives ) throws ArchiveException {
		eval = getIDF(archives );
	}
	
//	public void assignEvalIDF(Collection<IFeaturesCollector> fcs, Class fClass) {
//		eval = getIDF_BoFLFOS(fcs);
//	}

	public LFWords(F[] arr, ILFSimilarity<F> sim) {
		this.sim = sim;
		featureGroupClass = sim.getRequestedFeatureGroupClass();
		this.fArr = arr;
		size = arr.length;
	}

	public Class<ALocalFeaturesGroup> getLocalFeaturesGroupClass() {
		return featureGroupClass;
	}

	public LFWords(File inWordsFile) throws Exception {
		this(new DataInputStream(new BufferedInputStream(new FileInputStream(
				inWordsFile))));
	}

	public LFWords(String inWordsFileName) throws Exception {
		this(new DataInputStream(new BufferedInputStream(new FileInputStream(
				inWordsFileName))));
	}
	
	public LFWords(int size ) {
		sim = null;
		featureGroupClass = null;
		fArr = null;
		this.size = size;
	}

	public LFWords(DataInput in) throws Exception {
		byte version = in.readByte();

		sim = (ILFSimilarity) SimilarityClasses.read(in);
		featureGroupClass = sim.getRequestedFeatureGroupClass();

		Class<F> c = FeatureClasses.getClass(in.readInt());
		int hashCode = in.readInt();
		           size = in.readInt();

		fArr = (F[]) Array.newInstance(c, size);
		for (int i = 0; i < size; i++) {
			fArr[i] = (F) c.getConstructor(DataInput.class).newInstance(in);
		}

		boolean hasEval = in.readBoolean();
		if (hasEval) {
			int evalSize = in.readInt();
			eval = new float[size];
			if ( version > 1) {
				for (int i = 0; i < size; i++) {
					eval[i] = in.readFloat();
				}
			} else {
				for (int i = 0; i < size; i++) {
					eval[i] = (float) in.readDouble();
				}				
			}
		} else {
			eval = null;
		}

		if (version >= 1) {

			orderedAsPivots = in.readInt();
			surfLaplace1Index = in.readInt();

			// boolean hasInterDistnaces = in.readBoolean();
			// if ( hasInterDistnaces ) {
			//				
			// int elements = (size*size-size)/2;
			//				
			// byte[] byteArray = new byte[elements*8];
			// DoubleBuffer inDoubleBuffer =
			// ByteBuffer.wrap(byteArray).asDoubleBuffer();
			// in.readFully(byteArray);
			// interDistances = new double[size][];
			// for (int i=1; i<size; i++) {
			// // interDistances[i] = new double[i];
			// interDistances[i] = new double[i];
			// inDoubleBuffer.get(interDistances[i]);
			// }
			// }
		}

		if (this.hashCode() != hashCode) {
			// TO DO
		}
	}

	public void save(File file) throws IOException {
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream ( new FileOutputStream(file) ));
		writeData(out);
		out.close();
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);

		SimilarityClasses.write(sim, out);
		
		int temp = 0;
		while ( fArr[temp] == null ) temp++;
		out.writeInt(FeatureClasses.getClassID(((AbstractFeature) fArr[temp]).getClass()));
		out.writeInt(hashCode());

		out.writeInt(size);
		for (int i = 0; i < size; i++) {
			((AbstractFeature) fArr[i]).writeData(out);
		}

		if (eval != null) {
			out.writeBoolean(true);
			out.writeInt(eval.length);
			for (int i = 0; i < eval.length; i++) {
				out.writeFloat(eval[i]);
			}
		} else {
			out.writeBoolean(false);
		}

		out.writeInt(orderedAsPivots);
		out.writeInt(surfLaplace1Index);

		// out.writeBoolean(interDistances!=null);
		// if ( interDistances!=null ) {
		// int size = size;
		// int elements = (size*size-size)/2;
		//			
		// byte[] byteArray = new byte[elements*8];
		// DoubleBuffer outDoubleBuffer =
		// ByteBuffer.wrap(byteArray).asDoubleBuffer();
		//						
		// for (int i=1; i<size; i++) {
		// outDoubleBuffer.put(interDistances[i], 0, interDistances[i].length);
		// }
		// out.write(byteArray);
		// }

	}

	public int hashCode() {
		return 0;
	}

	public int size() {
		return size;
	}

	// public BagOfWords getBagOfWords(F[] features) {
	// return new BagOfWords(getHistogram(features), this);
	// }


//	public final byte[] getWeightedOccHistogram_byte(F[] features) {
//		double[] temp = getWeightedOccurences(features);
//
//		byte[] res = new byte[temp.length];
//		for (int i = 0; i < temp.length; i++) {
//			res[i] = (byte) (Math.round(temp[i] * 255) - 128);
//		}
//		return res;
//	}

	
	public final byte[] getOccHistogram_byte(F[] features) {
		int[] temp = getOccurences(features);

		byte[] res = new byte[temp.length];
		for (int i = 0; i < temp.length; i++) {
			res[i] = (byte) (Math.round((double) temp[i] / features.length
					* 255) - 128);
		}
		return res;
	}
	
	

	public final int[] getOccHistogram(F[] features) {
		return getHistogram(getOccurences(features), features.length);
	}
	
/*
	public final int[] getWeightedHistogram(F[] features) {
		double[] temp = getWeightedOccurences(features);
		// int[] temp = getOccurences(features);
		int[] res = new int[temp.length];
		for (int i = 0; i < temp.length; i++) {
			res[i] = (int) Math.round(temp[i] * Integer.MAX_VALUE);
		}
		return res;
	}
*/
	/*
	public final byte[] getWeightedHistogram_byte(F[] features) {
		double[] temp = getWeightedOccurences(features);

		byte[] res = new byte[temp.length];
		for (int i = 0; i < temp.length; i++) {
			res[i] = (byte) (Math.round(temp[i] * 255) - 128);
		}
		return res;
	}
*/
	protected final int[] getHistogram(int[] arr, int sumValue) {
		// Normalization
		double normFactor = (double) Integer.MAX_VALUE / sumValue;
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (int) Math.round(normFactor * arr[i]);
		}
		return arr;
	}

	
	public final int[] getOccurences(F[] features) {
		int[] hist = new int[size];
		for (int i1 = 0; i1 < features.length; i1++) {
			hist[getNNIndex(features[i1])]++;
		}

		return hist;
	}


	public final BoFLFSoft[] getBoFLFSoftArr_noPivotedFiltering(final F[] features, final int k, final BoFLFSoftGroup group) {
		
		BoFLFSoft[] bag = new BoFLFSoft[features.length];
		for (int i1=0; i1<features.length; i1++) {
			bag[i1] = getBoFLFSoft(features[i1], k, group);
		}
		return bag;
	}
	
	// PARALLEL !!!
 
/*
	public final BoFLFSoft[] getBoFLFSoftArr_noPivotedFiltering(final F[] features, final int k, final BoFLFSoftGroup group) {
				
		final BoFLFSoft[] bag = new BoFLFSoft[features.length];

		// For parallel
		final int size = features.length;
		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(features.length);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}
//		final F[] finalF = (F[]) features;
		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer i) {
					int max = i+nObjPerThread;
					if ( max > size )
						max = size;
					for (int iO = i; iO<max; iO++) {
						bag[iO] = getBoFLFSoft(features[iO], k, group);
						
					}
					return null;
				}
		});
		
		

		
		return bag;
	}
*/
	
	// NON PARALLEL
/*	 public final int[] getBags_noPivotedFiltering(F[] features) {
		 int[] bag = new int[features.length];
		 for (int i1=0; i1<features.length; i1++) {
			 bag[i1]=getNNIndex(features[i1]);
		 }
		 return bag;
	 }
	*/ 
	// PARALLEL !!!	
	
	static class GetBags implements Runnable {
		private final int[] bag;
		private final AbstractFeature[] features;
        private final int from;
        private final int to;
        private final LFWords words;
        
		GetBags(LFWords words, int from, int to, int[] bag, AbstractFeature[] features) {
			this.bag = bag;
			this.features = features;
			this.from = from;
			this.to = to;
			this.words = words;
			
        }
        
        @Override
        public void run() {
        	for (int i = from; i <= to; i++) {
        		bag[i] = words.getNNIndex(features[i]);
        	}
        }                
    }
	
	public final int[] getBags_noPivotedFiltering(F[] features) {
				
		final int[] bag = new int[features.length];
		
		int threadN = ParallelOptions.getNFreeProcessors() +1 ;
		Thread[] thread = new Thread[threadN];
        int[] group = SplitInGroups.split(features.length, thread.length);
        int from=0;
        for ( int i=0; i<group.length; i++ ) {
        	int curr=group[i];
        	if ( curr == 0 ) break;
        	int to=from+curr-1;
        	thread[i] = new Thread( new GetBags(this, from,to,bag, features) ) ;
        	thread[i].start();
        	from=to+1;
        }
        
		
		for (Thread t : thread) {
			if (t != null) {
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		ParallelOptions.free(threadN - 1);
		
		return bag;
	}
	
	 /*
	public final int[] getBags_SURF_noPivotedFiltering(F[] features) {
		
		
		final int[] bag = new int[features.length];
		
		// For parallel
		final int size = features.length;
		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(features.length);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}
		final F[] finalF = features;
		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer i) {
					int max = i+nObjPerThread;
					if ( max > size )
						max = size;
					for (int iO = i; iO<max; iO++) {
						bag[iO] = getNNIndex_SURF(finalF[iO]);
					}
					return null;
				}
		});


		return bag;
	}*/

//	public final int[] getBags_SURF_noPivotedFiltering(F[] features) {
//		int[] bag = new int[features.length];
//		for (int i1 = 0; i1 < features.length; i1++) {
//			bag[i1] = getNNIndex_SURF(features[i1]);
//		}
//		return bag;
//	}

	 /*
	public final int[] getBagsMIFile(F[] queries) {
        final int amp=100; //we sort k*amp obejcts

        //Let's play with parameters
        miFile.setKs(16);
        miFile.setMaxPosDiff(8);
        
        final int[] res = new int[queries.length];

		// For parallel
		final int size = queries.length;
		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(queries.length);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}
		final F[] finalF = queries;
		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer i) {
					int max = i+nObjPerThread;
					if ( max > size )
						max = size;
					for (int iO = i; iO<max; iO++) {
						DatasetObject qObj = null;
						if ( SIFT.class.isInstance( finalF[i] ) ) {
			        		qObj = new MIFileSIFT( 0, (SIFT) finalF[iO] );
			        	} else {
			        		// TO DO !!!
			        	}
//						TreeMap<Double, Object> miFileRes=miFile.kNN(qObj, 1);
//						res[iO] = (Integer) miFileRes.firstEntry().getValue();
						TreeMap<Double,Object> miFileRes=miFile.kNNRetrieveAndSort(qObj, 1, amp);
			        	res[iO] = ((MIFileSIFT) miFileRes.firstEntry().getValue()).getInternalId();
					}
					return null;
			}

		});
        
        return res;
	}*/
	
	 
	public final BoFLFSoft[] getBoFLFArrSoft(F[] features, int k, BoFLFSoftGroup group) {
		
		BoFLFSoft[] res = null;
//		if (sim.getClass().equals(SURFMetric.class)) {
//			res = getBoFLFSoft_SURF_noPivotedFiltering( (ILocalFeature[]) features, k, group);
//		} else {
			res = getBoFLFSoftArr_noPivotedFiltering( features, k, group);
//		}
		
		// CHECKS!
		int[] firstBags = getBags_noPivotedFiltering(features);

		for ( int i=0; i<res.length; i++ ) {
			if ( firstBags[i] != res[i].bag[0] ) {
				System.err.print("ERROR in soft assignment:");
				System.err.println(" " + res[i].bag[0] + " instead of " + firstBags[i]);
			}
		}

		return res;
	}
	
	
	public final int[] getBags(F[] features)  {

		return getBags_noPivotedFiltering(features);
	}
	
	public void setRandomPivots(int n) {
		ArrayList<Integer> rnd = RandomOperations.getOrderedIntegersAL(size);
		pArr = createFArr(n);
		for ( int i=0; i<n; i++ ) {
			pArr[i] = fArr[rnd.get(i)];
		}
		evaluatePivotedDistances();
	}
	
	private F[] createFArr(int n) {
		int i=0;
		for (  i=0; fArr[i] == null; i++);
		return (F[]) Array.newInstance(fArr[i].getClass(), n);
	}

	public void evaluatePivotedDistances() {
		int pSize = pArr.length;
		System.out.println("LFWords: evaluating pivoted distances.");
		pDistances = new float[pSize][size];
		for ( int i=0; i<pSize; i++) {
//			pDistances[i] = new float[size];
			for ( int j=0; j<size; j++ ) {
				pDistances[i][j] = (float) sim.distance(pArr[i], fArr[j]);
			}
		}
		System.out.println("... done");
		                        
	}
	
	/*
	public final int[] getWeightedBags(F[] features,  final int multiplier) {
		final int[] bags = new int[features.length*multiplier];
		// For parallel
				final int size = features.length;
				final int nObjPerThread = (int) Math.ceil( (double) size / ParallelOptions.nThreads);
				ArrayList<Integer> arrList = new ArrayList(features.length);
				for (int iO = 0; iO<size; iO+=nObjPerThread) {
					arrList.add(iO);
				}
				final F[] finalF = features;
				Parallel.forEach(arrList, new Function<Integer, Void>() {
					public Void apply(Integer i) {
							int max = i+nObjPerThread;
							if ( max > size )
								max = size;
							for (int iO = i; iO<max; iO++) {
								getWeightedBag(finalF[iO], bags, multiplier, iO*multiplier);
							}
							return null;
						}
				});
				
		return bags;
	}
	
	
*/
	/*
	public final int[] getWeightedBags_Spearman(F[] features,  final int k) {
		final int wordsMultiplier = (k+1)*k/2;
		final int[] bags = new int[features.length*wordsMultiplier];
		// For parallel
				final int size = features.length;
				final int nObjPerThread = (int) Math.ceil( (double) size / ParallelOptions.nThreads);
				ArrayList<Integer> arrList = new ArrayList(features.length);
				for (int iO = 0; iO<size; iO+=nObjPerThread) {
					arrList.add(iO);
				}
				final F[] finalF = features;
				Parallel.forEach(arrList, new Function<Integer, Void>() {
					public Void apply(Integer i) {
							int max = i+nObjPerThread;
							if ( max > size )
								max = size;
							for (int iO = i; iO<max; iO++) {
								getWeightedBag_Spearman(finalF[iO], bags, k, iO*wordsMultiplier);
							}
							return null;
						}
				});
				
		return bags;
	}
	*/

	// assign a bag to each feature
	/*
	public final int[] getBags_PivotedFiltering(F[] features) {
		
		final int[] bag = new int[features.length];
		
		// For parallel
		final int size = features.length;
		final int nObjPerThread = (int) Math.ceil( (double) size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(features.length);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}
		final F[] finalF = features;
		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer i) {
					int max = i+nObjPerThread;
					if ( max > size )
						max = size;
					float[] pDist = new float[pArr.length];
					
//					PriorityQueue<ObjectWithDistance<Integer>> pQueue = new PriorityQueue<ObjectWithDistance<Integer>>(size, new DecreasingComparator());
					
					for (int iO = i; iO<max; iO++) {
//						pQueue.clear();
//						bag[iO] = getBag_PivotedFiltering(finalF[iO], pDist, pQueue);
						bag[iO] = getBag_PivotedFiltering(finalF[iO], pDist);

//						int temp = getNNIndex(finalF[iO]);
//						if ( bag[iO] != temp ) {
//							System.err.println("ERROR in pivoted filtering: " + bag[iO] + "\t" + temp);
//						}
					}
					return null;
				}
		});
		
		return bag;
	}*/
		
	public final int getBag_PivotedFiltering(F feature, float[] pDist, PriorityQueue<ObjectWithDistance<Integer>> pQueue ) {		

		for (int i = 0; i < pArr.length; i++) {
			pDist[i] = (float) sim.distance(feature, pArr[i]);
		}
		
        
        int nnIndex = -1;
		double nnDist = Double.MAX_VALUE;
		
		// 10% of the dataset is scanned without pivots
		int nNotPivoted = size / 10;
		// first 
		for (int iWord = 0; iWord < nNotPivoted; iWord++) {
			double dist = sim.distance(feature, fArr[iWord], nnDist);
			if (dist >= 0 && dist < nnDist) {
				if (dist < nnDist) {
					nnIndex = iWord;
					nnDist = dist;
				}
			}
		}
		
		
		for (int i=nNotPivoted; i<size; i++) {
			double lbDist = 0;
			// Pivoted lower bound
			for (int iPiv = 0; iPiv < pArr.length; iPiv++) {
				// d(iF,iWord) >= | d(iF,piv) - d(piv,iWord) |
				float diff = pDist[iPiv] - pDistances[iPiv][i];
				diff = Math.abs(diff);
				if (lbDist < diff ) {
					lbDist = diff;
				}
			}
			if ( lbDist <nnDist )
				pQueue.add(new ObjectWithDistance((Integer) i, lbDist));
		}
      

		
		ObjectWithDistance<Integer> curr = null;
		while ( ( curr = pQueue.peek() )!= null
				&&
				nnDist > curr.getDist() 
				) {
			int currId = curr.getObj();
			double dist = sim.distance(feature, fArr[currId], nnDist);
			if (dist >= 0 && dist < nnDist) {
				nnIndex = currId;
				nnDist = dist;
			}
		}
		
        return nnIndex;
	}
	
		
	// assign a bag to each feature
	public final int getBag_PivotedFiltering(F feature, float[] tDist) {		

		for (int i = 0; i < pArr.length; i++) {
			tDist[i] = (float) sim.distance(feature, pArr[i]);
		}
		
		int nnIndex = 0;
		double nnDist = sim.distance(feature, fArr[0]);

		// 1% of the dataset is scanned without pivots
		int nNotPivoted = size / 100;
		// first 
		for (int iWord = 0; iWord < nNotPivoted; iWord++) {
			double dist = sim.distance(feature, fArr[iWord], nnDist);
			if (dist >= 0 && dist < nnDist) {
				if (dist < nnDist) {
					nnIndex = iWord;
					nnDist = dist;
				}
			}
		}
		
		for (int iWord = nNotPivoted; iWord < size; iWord++) {

			boolean filtered = false;

			// Pivoted filtering
			for (int iPiv = 0; iPiv < pArr.length; iPiv++) {
				// d(iF,iWord) >= | d(iF,piv) - d(piv,iWord) |
				double diff = tDist[iPiv] - pDistances[iPiv][iWord];
				if (nnDist < diff || nnDist < -diff) {
					filtered = true;
					break;
				}
			}

			if (!filtered) {
				double dist = sim.distance(feature, fArr[iWord], nnDist);
				if (dist >= 0 && dist < nnDist) {
					nnIndex = iWord;
					nnDist = dist;
				}
			}
		}


		return nnIndex;
	}
//
//	public final int[] getBags_PivotedFiltering_Complete(F[] features) {
//		int[] assignedBag = new int[features.length];
//		double[] tDist = new double[size];
//		int[] iDist = new int[size];
//
//		if (interDistances == null)
//			evaluateInterDistances();
//		for (int iF = 0; iF < features.length; iF++) {
//			int start = 0;
//			int end = size;
//			if (surfLaplace1Index > 0) {
//				if (((SURF) features[iF]).getLaplace() == 1) {
//					start = surfLaplace1Index;
//					end = size;
//				} else {
//					start = 0;
//					end = surfLaplace1Index;
//				}
//			}
//			int nnIndex = start;
//			int evalDistances = 0;
//			double nnDist = sim.distance(features[iF], fArr[start]);
//			// if ( nnDist < 1.0 ) { // 1.0 is requested because SURF distance
//			// report 1.0 for infinity
//			tDist[evalDistances] = nnDist;
//			iDist[evalDistances] = start;
//			evalDistances++;
//			// }
//			for (int iWord = start + 1; iWord < end; iWord++) {
//
//				boolean filtered = false;
//
//				// // Pivoted filtering
//				for (int iEval = 0; iEval < evalDistances; iEval++) {
//					double pivotedDistance = interDistances[iWord][iDist[iEval]];
//
//					// 1.0 is requested because SURF distance report 1.0 for
//					// infinity
//					// if ( pivotedDistance >= 1.0 ) {
//					// continue;
//					// }
//
//					// d(iF,iWord) >= | d(iF,piv) - d(piv,iWord) |
//					// = | d(iF,iDist[iEval]) - d(iDist[iEval],iWord) |
//					// = | tDist[iEval] - pivotedDistance |
//					double diff = tDist[iEval] - pivotedDistance;
//					if (nnDist < diff || nnDist < -diff) {
//						filtered = true;
//						break;
//					}
//				}
//
//				if (!filtered) {
//					// double dist = sim.distance(features[iF], fArr[iWord],
//					// nnDist );
//					double dist = sim.distance(features[iF], fArr[iWord]);
//					// if ( dist >= 0 && dist < 1.0 ) { // 1.0 is requested
//					// because SURF distance report 1.0 for infinity
//					if (dist >= 0) {
//						tDist[evalDistances] = dist;
//						iDist[evalDistances] = iWord;
//						evalDistances++;
//						if (dist < nnDist) {
//							nnIndex = iWord;
//							nnDist = dist;
//						}
//					}
//				}
//			}
//			// assigning word
//			assignedBag[iF] = nnIndex;
//		}
//
//		return assignedBag;
//	}
	public final void getWeightedBag_Spearman(F feature, int[] bRes, int k, int sIndex ) {
		// Serching kNN
		SimPQueue_kNN queue = new SimPQueue_kNN(k);
		for (int i = 0; i < size; i++) {
			double dist = sim.distance(feature, fArr[i], queue.excDistance);
			if ( dist >= 0 )
				queue.offer(i, dist);
		}
		
		// Getting the resutls
		//int[] bags = new int[k];
		ISimilarityResults res = queue.getResults();
		int i=0;
		int j=sIndex;
		//float avgDist = 0;
		for ( Iterator<ObjectWithDistance> it = res.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			int bag = (Integer) curr.getObj();
			int occ = k-i;
			for(int iOcc=0; iOcc<occ; iOcc++) {
				bRes[j++] = bag;
			}
			i++;
		}	

	}
	
	public final void getWeightedBag(F feature, int[] bRes, int k, int sIndex ) {
		
		// Serching kNN
		SimPQueue_kNN queue = new SimPQueue_kNN(k);
		for (int i = 0; i < size; i++) {
			double dist = sim.distance(feature, fArr[i], queue.excDistance);
			if ( dist >= 0 )
				queue.offer(i, dist);
		}
		
		// Getting the resutls
		int[] bags = new int[k];
		float[] d = new float[k];
		ISimilarityResults res = queue.getResults();
		int i=0;
		//float avgDist = 0;
		for ( Iterator<ObjectWithDistance> it = res.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			bags[i] = (Integer) curr.getObj();
			d[i] = (float) curr.getDist();
			//avgDist += d[i] = (float) curr.getDist();
			i++;
		}
		
		// fOcc = -(multiplier-1)/(1-thr)*x+(multiplier-1)/(1-thr)+1
		
		//(multiplier-1)/(1-thr)
	    //float a = ((k-1)/(1.0F-0.8F*0.8F));
		float a = ((k-1)/(1.0F-0.8F*0.8F));
		// trascoding
		int j=sIndex;
		int jMax =sIndex+k;
		
		//avgDist /= (float) k;
		for(i=0; i<bags.length-1 && j<jMax; i++) {
			int occ = Math.round( -a*d[i]/d[i+1]+a+1.0F);
			// one assignment is mandatory
			bRes[j++] = bags[i];
			for(int iOcc=1; iOcc<occ && j<jMax; iOcc++) {
				bRes[j++] = bags[i];
			}
		}
		if ( j<jMax ) {
			bRes[j++] = bags[bags.length-1];
		}
		
		/*
		if ( d[0] == 0 ) {
			for(int iOcc=0; i<k; iOcc++) {
				bRes[j++] = bags[i];
			}
		} else for(i=0; i<bags.length-1; i++) {
			
			//int occ = Math.round( avgDist / d[i] );
			
			// one assignment is mandatory
			bRes[j++] = bags[i];
			for(int iOcc=1; i<occ && j<k; iOcc++) {
				bRes[j++] = bags[i];
			}
		}*/		

	}
	
	public final BoFLFSoft getBoFLFSoft(F feature, int k, BoFLFSoftGroup group) {
		
		SimPQueue_kNN queue = new SimPQueue_kNN(k);
		for (int i = 0; i < size; i++) {
			double dist = sim.distance(feature, fArr[i], queue.excDistance);
			if ( dist >= 0 )
				queue.offer(i, dist);
		}
		int[] bags = new int[k];
		float[] ds = new float[k];
		ISimilarityResults results = queue.getResults();
		int i=0;
		for ( Iterator<ObjectWithDistance> it = results.iterator(); it.hasNext(); ) {
			ObjectWithDistance curr = it.next();
			bags[i] = (Integer) curr.getObj();
			ds[i] = (float) curr.getDist();
			i++;
		}
		ALocalFeature lf = (ALocalFeature) feature;
		BoFLFSoft res =  new BoFLFSoft(
				lf.getKeyPoint(),
                bags,
                ds);
		res.setLinkedGroup(group);
		
		return res;
//		} else {
//			FeaturesCollector_SingleWithIDClassified nn = (FeaturesCollector_SingleWithIDClassified) index
//					.getNN(new FeaturesCollector_SingleWithIDClassified(
//							(IFeature) feature));
//			return ((IDInteger) nn.getID()).id;
//		}

	}

	public final int getNNIndex(F feature) {
		double nnDist = sim.distance(feature, fArr[0]);
		int nnIndex = 0;
		for (int i2 = 1; i2 < size; i2++) {
			double temp = sim.distance(feature, fArr[i2], nnDist);
			if (temp >= 0 && temp < nnDist) {
				nnIndex = i2;
				nnDist = temp;
			}
		}
		return nnIndex;
	}

	/*
	public final int getNNIndex_SURF(F feature) {
		if (index == null) {

			int start = 0;

			int end = size;
			if (surfLaplace1Index > 0) {
				if (((SURF) feature).getLaplace() == 1) {
					start = surfLaplace1Index;
					end = size;
				} else {
					start = 0;
					end = surfLaplace1Index;
				}
			}

			int nnIndex = start;
			double nnDist = sim.distance(feature, fArr[start]);
			for (int i2 = start + 1; i2 < end; i2++) {
				double temp = sim.distance(feature, fArr[i2], nnDist);
				if (temp >= 0 && temp < nnDist) {
					nnIndex = i2;
					nnDist = temp;
				}
			}

			return nnIndex;

		} else {
			FeaturesCollector_SingleWithIDClassified nn = (FeaturesCollector_SingleWithIDClassified) index
					.getNN(new FeaturesCollector_SingleWithIDClassified(
							(IFeature) feature));
			return ((IDInteger) nn.getID()).id;
		}
	}*/

	
//	public final double[] getWeightedOccurences(F[] features) {
//		double[] hist = new double[size];
//		double scaleSum = 0;
//		for (int i1 = 0; i1 < features.length; i1++) {
//			double currScale = ((ILocalFeature) features[i1]).getScale();
//			scaleSum += currScale;
//			// System.out.print("\t"+currScale);
//
//			// adding
//			hist[getNNIndex(features[i1])] += currScale;
//		}
//		// System.out.println("\n");
//
//		// Normalization
//		for (int i = 0; i < hist.length; i++) {
//			hist[i] = hist[i] / scaleSum;
//		}
//		return hist;
//	}

	public void assignEvaluation(long[] given) {
		eval = new float[given.length];
		for (int i = 0; i < eval.length; i++)
			eval[i] = given[i];
	}

	public void assignEvaluation(float[] given) {
		eval = new float[given.length];
		for (int i = 0; i < eval.length; i++)
			eval[i] = given[i];
	}
	
	public void assignEvaluation(double[] given) {
		eval = new float[given.length];
		for (int i = 0; i < eval.length; i++)
			eval[i] = (float) given[i];
	}

	public String toString() {
		String temp = "";
		
		for ( int i=0; i<fArr.length; i++ ) {
			temp += i + "\t" + fArr[i] + "\n";
		}
		
		temp += "eval:";
		if ( eval != null)
			for ( int i=0; i<eval.length; i++)
				temp += "\t" + eval[i];
		
		temp += "\n";

		return temp;
	}

	public void reduce(int nMax, boolean worstsAndBests) {
		if (size < nMax)
			return;
		Class cF = fArr[0].getClass();

		for (int c = 0; c < (size - nMax); c++) {
			int curr = -1;

			if (c % 2 == 0 && worstsAndBests) {
				double currEval = Double.MIN_VALUE;
				// find actual best
				for (int i = 0; i < size; i++) {
					if (fArr[i] != null && eval[i] >= currEval) {
						curr = i;
						currEval = eval[i];
					}
				}
			} else {
				double currEval = Double.MAX_VALUE;
				// find actual wors
				for (int i = 0; i < size; i++) {
					if (fArr[i] != null && eval[i] <= currEval) {
						curr = i;
						currEval = eval[i];
					}
				}
			}
			fArr[curr] = null;
		}

		F[] newArr = (F[]) Array.newInstance(cF, nMax);
		float[] newEval = new float[nMax];
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (fArr[i] != null) {
				newArr[count] = fArr[i];
				newEval[count] = eval[i];
				count++;
			}
		}
		fArr = newArr;
		size = fArr.length;
		eval = newEval;
	}

	public void resetEval() {
		eval = null;

	}
	
	public void removeZeros() {
		int removed = 0;
		for (int i1 = 0; i1+removed < size; i1++) {
			fArr[i1] =fArr[i1+removed];
			while ( ((SIFT) fArr[i1]).hasZeroValues() ) {
				removed++;
				fArr[i1] =fArr[i1+removed];
			}
		}
		
		SIFT[] temp = new SIFT[size - removed];
		for (int i1 = 0; i1< temp.length; i1++) {
			temp[i1]= (SIFT) fArr[i1];
		}
		fArr = (F[]) temp;
		size = fArr.length;
	}
	/*
	public void createMIFile(File indexDirectory, int refObjsN, int ki, int ks ) throws Exception {
	
        if(!indexDirectory.exists()) indexDirectory.mkdirs();

        ReferenceObjects ros=new ReferenceObjects();
        ros.initializeEmpty(refObjsN);

        // getting random
        ArrayList<Integer> rnd = RandomOperations.getRandomOrderedIntegers(size);
        
        // adding reference objects
        if ( SIFT[].class.isInstance( fArr ) ) {
	        for(int i=0;i<refObjsN;i++){
	        	int id = rnd.get(i);
	        	ros.add( new MIFileSIFT( i, (SIFT) fArr[ id ] ) );
	        }
        } else {
        	// TO DO!!!
        }
        String referenceObjectsFileName = 
        	indexDirectory.getAbsolutePath() + java.io.File.pathSeparatorChar + miFileRefObjFileName; 
        ros.save( referenceObjectsFileName );
		

		// Create and/or open the MI_File
		MI_File mi_file = new MI_File(refObjsN, ki, ks, indexDirectory.getAbsolutePath() ,	referenceObjectsFileName);
			// Let's begin bulk load
		mi_file.beginBulkLoad();
		// now we insert the objects
		for ( int i=0; i<size; i++ ) {
			MIFileSIFT obj = null;
			if ( SIFT[].class.isInstance( fArr ) ) {
				obj = new MIFileSIFT(i, (SIFT) fArr[i]);
			} else {
				// TO DO!!!
			}
			mi_file.bulkInsert(obj);
		}	
		// now we can end bulk load
		mi_file.endBulkLoad();
		// We can also close the MI_File
		mi_file.close();
		
		File miFileLFWordsFile = new File(
        	indexDirectory.getAbsolutePath() + java.io.File.pathSeparatorChar + miFileLFWordsFileName ); 
        this.save(miFileLFWordsFile);		
		
	}
*/
	/*
	public void reorderSURFWords() {
		if (sim.getClass().equals(SURFMetric.class)) {
			// first laplace == 0 than others
			Collection<Integer> lapL0 = new ArrayList(size);
			for (int i1 = 0; i1 < size; i1++) {
				if (((SURF) fArr[i1]).getLaplace() == -1) {
					lapL0.add(i1);
				}
			}
			Reordering.reorder(lapL0, fArr);
			if (eval != null)
				Reordering.reorder(lapL0, eval);
			surfLaplace1Index = lapL0.size();
		}
	}
	*/
/*
	static public LFWords readMIFile( File indexDirectory, int ki, int ks ) throws FileNotFoundException, IOException, IllegalArgumentException, SecurityException, BoF_ReadingException, InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException {
		File miFileLFWordsFile = new File(
	        	indexDirectory.getAbsolutePath() + java.io.File.pathSeparatorChar + miFileLFWordsFileName ); 
	    LFWords res = new LFWords(miFileLFWordsFile);
	    
	    // getting ref_objects_num
	    DatasetObject t = null;
	    File tFile = new File(indexDirectory.getAbsolutePath() + java.io.File.pathSeparatorChar + miFileRefObjFileName );
        ObjectInputStream ros_file=new ObjectInputStream(new FileInputStream(tFile));
        ReferenceObjects ro=(ReferenceObjects)ros_file.readObject();
        int ref_objects_num = ro.size();
	    
	    MI_File mi_file = new MI_File(ref_objects_num, ki, ks, indexDirectory.getAbsolutePath(), tFile.getAbsolutePath() );
	    
        //Let's play with parameters
        mi_file.setKs(ks);
        mi_file.setMaxPosDiff(15);
        
        res.setMIFile(mi_file);
        
        return res;
	}*/
/*
	private void setMIFile(MI_File mi_file) {
		this.miFile = mi_file;		
	}*/
	/*
	public void setPivots(Integer nTries, Integer nObjects,	Integer nPivots, int nSet) {
		
		int[] realID = RandomOperations.getRandomOrderedInts(size);
		
		System.out.println("Evaluating interdistances between selected " + nSet + " objects");

		float[][] interDistances = new float[nSet][nSet];

		System.out.println("--> inter distances evaluation.");
		for (int i1 = 1; i1 < nSet; i1++) {
			for (int i2 = 0; i2 < nSet; i2++) {
				// interDistances[i1][i2] = sim.distance(fArr[i1], fArr[i2]);
				interDistances[i1][i2] = (float) sim.distance(fArr[realID[i1]], fArr[realID[i2]]);
			}
		}
		System.out.println("--> inter distances evaluation done.");

		System.out.println("--> searching pivots.");
		ArrayList<Integer> pivots = Pivots.search(nTries, nObjects,	interDistances, nPivots);

		pArr = createFArr(nPivots);
		for ( int i=0; i<nPivots; i++ ) {
			pArr[i] = fArr[realID[pivots.get(i)]];
		}
		evaluatePivotedDistances();
	}
	*/
//	public void reorderAsPivots(Integer nTries, Integer nObjects, Integer nPivots) {
//		System.out.println("Reordering words as pivots");
//		int size = size;
//		interDistances = new double[size][];
//		for (int i = 1; i < size; i++) {
//			// interDistances[i] = new double[i];
//			interDistances[i] = new double[i];
//		}
//
//		System.out.println("--> inter distances evaluation.");
//		for (int i1 = 1; i1 < size; i1++) {
//			for (int i2 = 0; i2 < interDistances[i1].length; i2++) {
//				// interDistances[i1][i2] = sim.distance(fArr[i1], fArr[i2]);
//				interDistances[i1][i2] = sim.distance(fArr[i1], fArr[i2]);
//			}
//		}
//		System.out.println("--> inter distances evaluation done.");
//
//		System.out.println("--> inter distances reordering as pivots.");
//		Collection<Integer> order = Pivots.reordering(nTries, nObjects,
//				interDistances, nPivots);
//
//		orderedAsPivots = order.size();
//
//		Reordering.reorder(order, fArr);
//		// Reordering.reorderTrMatrix(order, interDistances);
//		if (eval != null)
//			Reordering.reorder(order, eval);
//
//		reorderSURFWords();
//
//	}
	
	/*
	public void createIndex() throws Exception {
		System.out.print("Creating MTreeIndex for words...");
		ArrayList<FeaturesCollector_SingleWithIDClassified> toIndex = new ArrayList();
		for (int i = 0; i < size; i++) {
			toIndex.add(new FeaturesCollector_SingleWithIDClassified(
					(IFeature) fArr[i], new IDInteger(i)));
		}
		index = new MTreeIndex((Metric<F>) sim, toIndex, 1024, 256);

		System.out.println(" done");
	}
	*/

}
