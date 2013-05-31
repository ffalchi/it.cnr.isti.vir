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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.ParallelOptions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class Permutation extends AbstractFeature {

	
	byte version = 2;
	// RO ids ordered
	int[] ordRO = null;
	int nRO;
	// RO ids array with relative position
	int[] roPosition = null;
	
	public int getNRO(){
		return nRO;
	}

	
	public Permutation(AbstractFeaturesCollector obj, AbstractFeature[] ro, ISimilarity sim  ) {
		this(obj, ro, sim, ro.length, true);
	}

	public Permutation(AbstractFeaturesCollector obj, AbstractFeature[] ro, ISimilarity sim, boolean roPosition ) {
		this(obj, ro, sim, ro.length, roPosition);
	}
	
	public Permutation(AbstractFeaturesCollector obj, AbstractFeature[] ro, ISimilarity sim, int pLength, boolean roPosition) {
		
		nRO = ro.length;
		SimPQueueArr<Integer> pQueue = new SimPQueueArr<Integer>(pLength);
		
		for ( int i=0; i<ro.length; i++) {
			pQueue.offer(i, sim.distance(obj, ro[i]));
		}
		
		SimilarityResults res = pQueue.getResults();
		ordRO = new int[pLength];
		int i=0; 
		for ( Iterator<ObjectWithDistance<Integer>> it =  res.iterator(); it.hasNext(); ) {
			ObjectWithDistance<Integer> curr = it.next();
			ordRO[i++] = curr.getObj();
		}
		
		if ( roPosition) this.convertToPositions();
		
	}
	
    public Permutation(ByteBuffer in) throws IOException  {
    	byte inVersion = in.get(); // version
    	byte type = in.get();
    	int size = in.getInt();
    	if ( type == 0) {
    		if ( inVersion >= 2 ) nRO = in.getInt();
			ordRO = new int[size];
			if ( nRO < Short.MAX_VALUE && inVersion >= 2 ) {
				for ( int i=0; i<ordRO.length; i++) {
					ordRO[i] = in.getShort();
				}
			} else {
				for ( int i=0; i<ordRO.length; i++) {
					ordRO[i] = in.getInt();
				}
			}
    	} else if (type != 0) {
    		nRO = size;
    		roPosition = new int[nRO];
    		if ( nRO < Short.MAX_VALUE && inVersion >= 2 ) {
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = in.getShort();
    			}
    		} else {
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = in.getInt();
    			}
    		}
    	} else {
    		throw new IOException("Not recognized type in Permutation reading");
    	}
    }
	
	
	public Permutation(DataInput in ) throws IOException {
		byte inVersion = in.readByte(); // version
		byte type = in.readByte();
		int size = in.readInt();
		if ( type == 0) {
			if ( inVersion >= 2 ) nRO = in.readInt();
			ordRO = new int[size];
			if ( nRO < Short.MAX_VALUE && inVersion >= 2 ) {
				byte[] arr = new byte[nRO*2];
    			in.readFully(arr);
    			ByteBuffer buffer = ByteBuffer.wrap(arr);
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = buffer.getShort();
    			}
			} else {
				byte[] arr = new byte[nRO*4];
    			in.readFully(arr);
    			ByteBuffer buffer = ByteBuffer.wrap(arr);
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = buffer.getInt();
    			}
			}
    	} else if (type != 0 ) {
    		nRO = size;
    		roPosition = new int[nRO];
    		if ( nRO < Short.MAX_VALUE && inVersion >= 2 ) {
    			byte[] arr = new byte[nRO*2];
    			in.readFully(arr);
    			ByteBuffer buffer = ByteBuffer.wrap(arr);
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = buffer.getShort();
    			}
    		} else {
    			byte[] arr = new byte[nRO*4];
    			in.readFully(arr);
    			ByteBuffer buffer = ByteBuffer.wrap(arr);
    			for ( int i=0; i<nRO; i++) {
    				roPosition[i] = buffer.getInt();
    			}
    		}
    	} else {
    		throw new IOException("Not recognized type in Permutation reading");
    	}
	}

	
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		if ( ordRO != null ) {
			out.writeByte( 0);
			out.writeInt(ordRO.length);
			out.writeInt(nRO);
			if ( nRO < Short.MAX_VALUE ) {
				for ( int i=0; i<ordRO.length; i++) {
					out.writeShort(ordRO[i]);				
				}
			} else {
				for ( int i=0; i<ordRO.length; i++) {
					out.writeInt(ordRO[i]);				
				}
			}
		} else {
			out.writeByte(1);
			out.writeInt(roPosition.length);
			if ( roPosition.length < Short.MAX_VALUE ) {
				for ( int pos : roPosition ) {
					out.writeShort(pos);
				}
			} else {
				for ( int pos : roPosition ) {
					out.writeInt(pos);
				}
			}
		}
	}
	

	public synchronized void convertToOrdered() {
		
		if ( ordRO != null ) return;
		
		ordRO = convertToOrdRO(roPosition);
		roPosition = null;
	}
	
	public synchronized void convertToPositions() {
		
		if ( roPosition != null ) return;
		
		roPosition = convertToOrdROPositions(ordRO, nRO);
		ordRO = null;
	}
	
	public int[] getROPositions() {
		if ( roPosition == null ) {
			convertToPositions();
		}
		return roPosition;
	}
	
	
	public static int[] convertToOrdROPositions(int[] ordRO, int nRO) {
//		for ( int i=0; i<ordRO.length; i++) {
//			if ( ordRO[i] > max ) {
//				max = ordRO[i];
//			}
//		}
		
		int[] res = new int[nRO];
		if ( ordRO.length != res.length ) Arrays.fill(res, -1);
		for ( int i=0; i<ordRO.length; i++) {
			res[ordRO[i]] = i;
		}
		return res;
	}
	
	public static int[] convertToOrdRO(int[] roPosition) {
		
		// conunting occurences
		int count = 0; 
		for ( int i=0; i<roPosition.length; i++) {
			if ( roPosition[i] >= 0 ) {
				count++;
			}
		}
		
		
		int[] res = new int[count++];		
		count = 0;
		for ( int i=0; i<roPosition.length; i++) {
			if ( roPosition[i] >= 0 ) {
				res[roPosition[i]] = i;
			}
		}
		
		return res;
	}
	
	
	// For kNN searching
	static class GetAllThread implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final AbstractFeaturesCollector[] ro;
		private final ISimilarity sim;
		private final Permutation[] res;

		public GetAllThread(AbstractFeaturesCollector[] ro, ISimilarity sim, AbstractFeaturesCollector[]  objs, int from, int to, Permutation[] res ) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.ro = ro;
			this.sim = sim; 
			this.res = res;
			
		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				if ( objs[iO] == null) res[iO] = null;
				else {
					res[iO] = new Permutation(objs[iO], ro, sim);
				}
			}
		}
	}

	
	public static Permutation[] getAll( AbstractFeaturesCollector[] objs, AbstractFeaturesCollector[] ro, ISimilarity sim) {
		Permutation[] perm = new Permutation[objs.length];
		
		// kNNQueues are performed in parallels
		final int nObjsPerThread = (int) Math.ceil((double) objs.length / ParallelOptions.nThreads);
		final int nThread = (int) Math.ceil((double) objs.length / nObjsPerThread);
		int ti = 0;
        Thread[] thread = new Thread[nThread];
        for ( int from=0; from<objs.length; from+=nObjsPerThread) {
        	int to = from+nObjsPerThread-1;
        	if ( to >= objs.length ) to =objs.length-1;
        	thread[ti] = new Thread( new GetAllThread(ro, sim, objs, from,to, perm) ) ;
        	thread[ti].start();
        	ti++;
        }
        
        for ( ti=0; ti<thread.length; ti++ ) {
        	try {
				thread[ti].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		
		return perm;
	}
	
	// For kNN searching
	public static class PermThread implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final AbstractFeaturesCollector[] ro;
		private final ISimilarity sim;
		private final AbstractFeaturesCollector[] res;

		public PermThread(AbstractFeaturesCollector[] ro, ISimilarity sim, AbstractFeaturesCollector[]  objs, int from, int to, AbstractFeaturesCollector[] res ) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.ro = ro;
			this.sim = sim; 
			this.res = res;
			
		}

		@Override
		public void run() {
			for (int iO = from; iO<=to; iO++) {
				if ( objs[iO] == null) res[iO] = null;
				else {
					if ( objs[iO] instanceof IHasID) {
						res[iO] = new FeatureCollector(new Permutation(objs[iO], ro, sim), ((IHasID) objs[iO]).getID());
					} else {
						res[iO] = new FeatureCollector(new Permutation(objs[iO], ro, sim));
					}
					
				}
			}
		}
	}
	
	public static void featuresCollectorsArchiveConvert(File inFile, File outFile, AbstractFeaturesCollector[] ro, ISimilarity sim) throws Exception {
		FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(inFile, false);
		Log.info("FeaturesCollectorsArchiveConvert is creating new FCArchive: " + outFile.getAbsolutePath());
		FeaturesCollectorsArchive_Buffered outArchive = new FeaturesCollectorsArchive_Buffered(outFile, new FeatureClassCollector(Permutation.class), inArchive.getIDClass(), FeatureCollector.class);
		
		int batchSize = 10000;
		AbstractFeaturesCollector[] objs = new AbstractFeaturesCollector[batchSize] ;
		AbstractFeaturesCollector[] tResults = new AbstractFeaturesCollector[batchSize] ;
		Iterator<AbstractFeaturesCollector> it = inArchive.iterator();
		int count =0;
		while( it.hasNext() ) {
			
			// reading objects in batch
			for ( int i=0; i<objs.length; i++  ) {
				AbstractFeaturesCollector fc = null;
				if ( it.hasNext() ) {
					objs[i] = it.next();
				} else {
					objs[i] = null;
				}				
			}
			
			// kNNQueues are performed in parallels
			final int nObjsPerThread = (int) Math.ceil((double) objs.length / ParallelOptions.nThreads);
			final int nThread = (int) Math.ceil((double) objs.length / nObjsPerThread);
			int ti = 0;
	        Thread[] thread = new Thread[nThread];
	        for ( int from=0; from<objs.length; from+=nObjsPerThread) {
	        	int to = from+nObjsPerThread-1;
	        	if ( to >= objs.length ) to =objs.length-1;
	        	thread[ti] = new Thread( new PermThread(ro, sim, objs, from,to, tResults) ) ;
	        	thread[ti].start();
	        	ti++;
	        }
	        
	        for ( ti=0; ti<thread.length; ti++ ) {
	        	try {
					thread[ti].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		
			
			
			for ( int i=0; i<tResults.length && tResults[i]!=null; i++) {
				outArchive.add(tResults[i]);
				count++;
			}
			Log.info(count + "/" + inArchive.size());
		}	
		outArchive.close();
	}
	
	public void removeRO(int roID) {
		if ( ordRO == null ) this.convertToOrdered();
		
		int pos = 0;
		for ( pos=0; pos<ordRO.length && ordRO[pos] != roID; pos++);
		if ( pos==ordRO.length) return;
		int[] newOrdRO = new int[ordRO.length-1];
		System.arraycopy(ordRO, 0, newOrdRO, 0, pos);
		System.arraycopy(ordRO, pos+1, newOrdRO, pos, ordRO.length-(pos+1));
		ordRO = newOrdRO;
	}
	
	
	public void reduceToNRO(int newNRO) {
		if ( newNRO >= nRO ) return;
		if ( ordRO == null ) this.convertToOrdered();
		 
		int destPos = 0;
		
		for (int origPos = 0; origPos<ordRO.length; origPos++) {
			if (ordRO[origPos]<newNRO) {
				ordRO[destPos++]=ordRO[origPos];
			}
		}
		
		ordRO = Arrays.copyOf(ordRO, destPos);
	}
	  

//	public void addToRoOcc(int[] occ, int permLenght, HashSet<Integer> excluding) {
//		if ( ordRO != null)
//			this.convertToOrdered();
//		int k=0;
//		for ( int i=0; k<permLenght;i++) {
//			if ( !excluding.contains(ordRO[i])) {
//				occ[ordRO[i]]++;
//				k++;
//			}
//		}
//	}
	 
	public void addToROOcc(int[] occ, int permLenght, int excluded ) {
		if ( ordRO == null ) this.convertToOrdered();
		int k=0;
		for ( int i=0; k<permLenght;i++) {
			if ( ordRO[i] != excluded ) {
				occ[ordRO[i]]++;
				k++;
			}
		}
		
	}
	

	
	public void addToROOcc(int[] occ, int permLenght) {
		if ( ordRO != null) {
			for ( int i=0; i<permLenght;i++) {
				occ[ordRO[i]]++;
			}
		} else {
			for ( int i=0; i<roPosition.length;i++) {
				int pos = roPosition[i];
				if ( pos < permLenght ) occ[i]++;
			}
		} 
	}
	
	public void addToROPosOcc(int[][] occ, int permLenght, int excluded ) {
		if ( ordRO == null ) this.convertToOrdered();
		int k=0;
		for ( int i=0; k<permLenght;i++) {
			if ( ordRO[i] != excluded ) {
				occ[ordRO[i]][k]++;
				k++;
			}
		}
	}
	
	public void addToROPosOcc(int[][] occ, int permLenght) {
		if ( ordRO != null) {
			for ( int i=0; i<permLenght;i++) {
				occ[ordRO[i]][i]++;
			}
		} else {
			for ( int ro=0; ro<roPosition.length;ro++) {
				int pos = roPosition[ro];
				if ( pos < permLenght ) occ[ro][pos]++;
			}
		} 
	}
	

	
//	public static int[] getRoOcc(Permutation[] perm, int permLenght, HashSet<Integer> excluding ) {
//		int[] sum = new int[perm[0].nRO];
//		for (int i=0; i<perm.length; i++) {
//			perm[i].addToRoOcc(sum, permLenght, excluding );
//		}
//		return sum;
//	}
	
	public boolean contains(int roID) {
		if ( ordRO != null) {
			for ( int i=0; i<ordRO.length;i++) {
				if ( ordRO[i] == roID) return true;
			}
		} else {
			return roPosition[roID] > 0;
		}
		return false;
	}
	
	/**
	 * @param roID
	 * @param maxLength max length of the permutation
	 * @return
	 */
	public boolean contains(int roID, int maxLength) {
		if ( ordRO != null) {
			for ( int i=0; i<maxLength;i++) {
				if ( ordRO[i] == roID) return true;
			}
			return false;
		} else {
			int temp = roPosition[roID];
			if ( temp > 0 && temp < maxLength ) return true;
			return false;
		}
	}
	
	public int getPermHash() {
		if ( ordRO == null) return java.util.Arrays.hashCode( convertToOrdRO(roPosition)); 
		return java.util.Arrays.hashCode(ordRO);
	}
	
	public int getPermHashWithout(int RO) {
		if ( ordRO == null) return java.util.Arrays.hashCode( convertToOrdRO(roPosition)); 
		return java.util.Arrays.hashCode(ordRO);
	}

	public int[] getOrdRO(int permLength, Integer excluded) {
		if ( ordRO == null) this.convertToOrdered(); 
		int[] res = new int[permLength];
		int iRes = 0;
		for(int i=0; iRes<permLength; i++) {
			int curr = ordRO[i];
			if ( excluded != null && curr == excluded) continue;
			res[iRes++]= curr;
		}
		return res;
	}
	
}
