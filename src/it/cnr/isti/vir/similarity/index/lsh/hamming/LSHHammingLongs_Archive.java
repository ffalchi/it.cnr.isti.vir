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

package it.cnr.isti.vir.similarity.index.lsh.hamming;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import it.cnr.isti.vir.distance.Hamming;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClasses;
import it.cnr.isti.vir.features.ILongBinaryValues;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.pqueues.AbstractSimPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_kNN;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_r;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.TimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import org.hamcrest.internal.ArrayIterator;





/**
 * @author Fabrizio Falchi
 *
 */
public class LSHHammingLongs_Archive implements IkNNExecuter {
	
	
	L_HammingLongs[] ls;
    int h;
        
    Class<? extends AbstractFeature> fClass;
    
    FeaturesCollectorsArchive fca;    
    FileChannel fileChannel;
    
    public int maxBucketSize_SEARCH = Integer.MAX_VALUE;
    
    static boolean parallel = true;
//    public final long getFileChannelPosition(int internalID ) {
//    	return nBytes*internalID;
//    }


    public String getLongsFileName() {
    	return fca.getfile().getAbsolutePath() + ".longs";
    }
    
    public void createLongsFile() throws Exception {
    	
    	Log.info("LSH is creating Longs file " + getLongsFileName());
		DataOutputStream out = 
				new DataOutputStream(
						new BufferedOutputStream(
							new FileOutputStream(
									getLongsFileName() )	) );
    	
		TimeManager tm = new TimeManager(fca.size());
    	for ( AbstractFeaturesCollector fc : fca ) {
    		ILongBinaryValues b =  ((ILongBinaryValues) fc.getFeature(fClass));
    		
    		long[] values = b.getValues();
    		
    		for ( long l : values ) {
    			out.writeLong(l);
    		}
    		tm.reportProgress();
    	}
    	
    	out.close();
    }
    
    public LSHHammingLongs_Archive( String fcaName, Class<? extends AbstractFeature> fClass,  int h, int l) throws Exception  {

    	this.fClass = fClass;

    	this.h=h;
    	
    	

    	fca = new FeaturesCollectorsArchive(fcaName, false);
    	
    	if ( !(new File( getLongsFileName() )).exists() ) {
    		createLongsFile();
    	}
    	
    	//rndAccess = new RandomAccessFile(getLongsFileName(), "r");
    	fileChannel = new RandomAccessFile(getLongsFileName(), "r").getChannel();
    	
    	ls = new L_HammingLongs[l];
    	
    	buildIndex();
    	
    	    	
    }

    
    private void buildIndex() throws Exception {
    	
    	for( int i=0;i<ls.length;i++){
    		ls[i] = new L_HammingLongs(new File(getLongsFileName()), getLSHLFile(i), h);
    	}
    	
    }    
    
    public File getLSHFile() {
    	return new File(
    			fca.getfile().getAbsolutePath() + ".lsh" );
    }
    
    public File getLSHLFile(int i) {
    	return new File(
    			fca.getfile().getAbsolutePath() + ".l" + i );
    }
    
	public void save() throws IOException {

		Log.info("Saving LSH for " + fca.getfile().getAbsolutePath());
		DataOutputStream out = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream( getLSHFile() 
								)));

		out.writeInt(FeatureClasses.getClassID(fClass));
		
		out.writeInt(ls.length);
		out.writeInt(h);
//		for (int i = 0; i < ls.length; i++) {
//			ls[i].save(
//					getLSHLFile(i) );
//		}
		out.close();

		Log.info("Saving completed");
	}
	
//	public long[] getValues(int i) throws Exception {
//		
//		byte[] bytes = new byte[nBytes];
//		rndAccess.readFully(bytes);
//		return LongByteArrayUtil.getArr(bytes, 0, nLongs);
//	}
    
	public LSHHammingLongs_Archive(String fcaName ) throws Exception {
		
		fca = new FeaturesCollectorsArchive(fcaName, false);

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(new File(fca.getfile().getAbsolutePath() + ".lsh"))));

    	//rndAccess = new RandomAccessFile(getLongsFileName(), "r");
		fileChannel = new RandomAccessFile(getLongsFileName(), "r").getChannel();
		
		fClass = FeatureClasses.getClass(in.readInt());
		
		int l = in.readInt();
		h = in.readInt();
		ls = new L_HammingLongs[l];
		for (int i = 0; i < l; i++) {
			ls[i] = new L_HammingLongs(getLSHLFile(i));			
		}
		in.close();
	}
    
    
    private synchronized final long[] readData(int id ) throws IOException, ArchiveException {
    	AbstractFeaturesCollector fc = fca.get(id);
    	return ((ILongBinaryValues) fc.getFeature(fClass)).getValues();
    }
	
    static TIntHashSetPool hashSetPool = new TIntHashSetPool(8,30);
      

	public ISimilarityResults getKNNResults(AbstractFeaturesCollector fc, int k)
			throws Exception {
		return search((ILongBinaryValues) fc.getFeature(fClass), new SimPQueue_kNN(k));
	}
    
	@Override
	public ISimilarityResults getKNNResults(AbstractFeature qObj, int k) throws Exception {
		return search(qObj, new SimPQueue_kNN(k));
	}
    
    public ISimilarityResults rangeSearch(AbstractFeaturesCollector fc, double r) throws Exception  {
    	return search(fc, new SimPQueue_r(r));
    }
    
    public final ISimilarityResults search(AbstractFeaturesCollector fc, AbstractSimPQueue pQueue) throws Exception  {
		
    	return search( (ILongBinaryValues) fc.getFeature(fClass), pQueue);
    }
    
    public final ISimilarityResults search(AbstractFeature f, AbstractSimPQueue pQueue) throws Exception  {
		
    	return search( (ILongBinaryValues) f, pQueue);
    }
   
    public final ISimilarityResults search(ILongBinaryValues f, AbstractSimPQueue pQueue) throws Exception {
    		
    	return search(f.getValues(), pQueue);
    }    
    
	class Search implements Runnable {

		private final TIntHashSet results;
		private final long[] query;
		private final ArrayIterator ls;
	    
		Search(long[] query, ArrayIterator ls, TIntHashSet results ) {
            this.ls = ls;
			this.results = results;
            this.query=  query;
        }
        
        @Override
        public void run() {
        	
        	L_HammingLongs l;
        	
        	while (true) {
            	try {
            		l = (L_HammingLongs) ls.next();
            	} catch (ArrayIndexOutOfBoundsException e) {
            		break;
            	}
            	if ( l == null ) break;
        	
		        try {
		        	IntBuffer ib = l.getInBucket(query);
		        	if ( ib != null ) {
		        		if ( ib.capacity() > maxBucketSize_SEARCH ) continue;
			        	synchronized ( results ) {
			        		while ( ib.hasRemaining() )
			        			results.add(ib.get());
			        	}
		        	}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}

        }                
    }
    
//	class Evaluate implements Runnable {
//
//		private final TIntIterator it;
//		private final long[] query;
//		private final AbstractSimPQueue pQueue;
//	    
//		Evaluate(long[] query, TIntIterator it, AbstractSimPQueue pQueue ) {
//            this.it = it;
//            this.pQueue = pQueue;
//            this.query=  query;
//        }
//        
//        @Override
//        public void run() {
//        	byte[] bytes = new byte[L_HammingLongs.nBytes];
//        	long[] v = new long[L_HammingLongs.nLongs];
//            while (true) {
//           		int oID;
//           		synchronized(it) {
//           			if ( it.hasNext() == false ) break;
//           			oID =it.next();
//                    try {
//                    	rndAccess.seek(oID*(long) L_HammingLongs.nBytes);
//                    	rndAccess.readFully(bytes);
//						LongByteArrayUtil.convToLong(bytes, v);
//    				} catch ( Exception e) {
//    					// TODO Auto-generated catch block
//    					e.printStackTrace();
//    				}
//           		}
//
//                int dist=Hamming.distance(query, v  );
//                pQueue.offer(oID, dist);
//            }
//
//        }                
//    }
	
	class Evaluate implements Runnable {

		private final TIntIterator it;
		private final long[] query;
		private final AbstractSimPQueue pQueue;
	    
		Evaluate(long[] query, TIntIterator it, AbstractSimPQueue pQueue ) {
            this.it = it;
            this.pQueue = pQueue;
            this.query=  query;
        }
        
        @Override
        public void run() {
        	
    		ByteBuffer bb = ByteBuffer.allocateDirect(L_HammingLongs.nBytes);
        	LongBuffer lb = bb.asLongBuffer();;
        	FileChannel fc = LSHHammingLongs_Archive.this.fileChannel;
            while (true) {
           		int oID;
           		synchronized(it) {
           			if ( it.hasNext() == false ) break;
           			oID =it.next();
           		}
                try {
                	fc.read(bb, oID*(long) L_HammingLongs.nBytes);
				} catch ( Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
           		
			    bb.flip();   
				int dist=Hamming.distance(query, lb  );
				if ( dist < pQueue.excDistance )
					pQueue.offer(oID, dist);
			    lb.clear();
            }

        }                
    }
	
    public final ISimilarityResults search(long[] query, AbstractSimPQueue pQueue) throws Exception{
    	int iHashSet = hashSetPool.acquireTIntHashSet();
    	TIntHashSet objects = hashSetPool.getHashSet(iHashSet);
    	
    	if ( parallel ) {
    		ArrayIterator it  = new ArrayIterator(ls);
			int nThread = ParallelOptions.reserveNFreeProcessors()+1;
			Thread[] thread = new Thread[nThread];
			
			for ( int ti=0; ti<thread.length; ti++ ) {
				thread[ti] = new Thread( new Search(query, it, objects) ) ;
	        	thread[ti].start();
			}
			
	        for ( Thread t : thread ) {
	        	t.join();
	        }
			
	        ParallelOptions.free(nThread-1);
    	} else {
    		for(L_HammingLongs l: ls){
    			IntBuffer ib = l.getInBucket(query);
    			if ( ib == null || ib.capacity() > maxBucketSize_SEARCH ) continue;
        		while ( ib.hasRemaining() )
        			objects.add(ib.get());
   			}
    	}
    	
    	System.out.print(objects.size() + "\t");
    	if ( parallel ) {
	        TIntIterator it = objects.iterator();
			int nThread = ParallelOptions.reserveNFreeProcessors()+1;
			Thread[] thread = new Thread[nThread];
			for ( int ti=0; ti<thread.length; ti++ ) {
				thread[ti] = new Thread( new Evaluate(query, it, pQueue) ) ;
	        	thread[ti].start();
			}
			
	        for ( Thread t : thread ) {
	        	t.join();
	        }
	        ParallelOptions.free(nThread-1);
	        
	        
    	} else {
	    	
//	    	byte[] bytes = new byte[L_HammingLongs.nBytes];
//        	long[] v = new long[L_HammingLongs.nLongs];
    		
    		ByteBuffer bb = ByteBuffer.allocateDirect(L_HammingLongs.nBytes);
        	LongBuffer lb = bb.asLongBuffer();
        	FileChannel fc = LSHHammingLongs_Archive.this.fileChannel;
	    	
	    	for(TIntIterator it = objects.iterator(); it.hasNext(); ){
		        int oID=it.next();

			    try {
                	fc.read(bb, oID*(long) L_HammingLongs.nBytes);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
			    bb.flip();   
				int dist=Hamming.distance(query, lb  );
			    pQueue.offer(oID, dist);
			    lb.clear();
			    
		    }
	    	
    	}
    	hashSetPool.releaseTIntHashSet(iHashSet);
    	
    	ISimilarityResults res = pQueue.getResults();
    	ObjectWithDistance[] od = new ObjectWithDistance[res.size()];
    	int i=0;
   		for ( Iterator<ObjectWithDistance> it=res.iterator(); it != null && it.hasNext(); ) {
   			ObjectWithDistance c = it.next();
   			od[i++] = new ObjectWithDistance(
   						fca.get((int) c.obj).getID(), 
   						c.dist
 					);
   	  	}
      
        
        return new SimilarityResults(od);
    }
    
//    private static int current=0;
//    public Collection<Result> range_poll_l(BitsObject objectQuery, double r){
//    	ArrayList<Result> res=new ArrayList<Result>(10);
//        int key=gs[current].eval(objectQuery);
//        int[] objects = tables[current][key];
//        current++;
//        current%=gs.length;
//        if(objects!=null)
//        	for(int iE=0; iE<objects.length; iE++ ){
//        		int o_offset = objects[iE]*AbstractBitsObject.nLongs;
//        		int dist=Hamming.distance(objectQuery.data, data, o_offset, BitsObject.nLongs);
//                if(dist<=r){
//                	Result resObj = new Result(objects[iE], dist );
//                	res.add(resObj);
//                }
//        }
//        Collections.sort(res);
//        return res;
//    }
    
    public final int getNOfNotNullBuckets() {
    	int res = 0;
    	for ( int i=0; i<ls.length; i++ ) {
        	res += ls[i].getNOfNotNullBuckets();
    	}
    	return res;
    }
    
//    public void save(String fileName) throws IOException {
//        DataOutputStream out =
//        		new DataOutputStream(
//        				new BufferedOutputStream(
//        					new FileOutputStream(
//        							new File(fileName))));
//    
//        
//        out.writeInt(FeatureClasses.getClassID(fClass));
//        
//    	out.writeInt(l);
//    	out.writeInt(h);
//    	for(int i=0;i<l;i++){
//            gs[i].write(out);
//            for ( int i2=0; i2<tables[i].length; i2++) {
//            	if ( tables[i][i2] == null ) {
//            		out.writeInt(0);
//            	} else {
//            		out.writeInt(tables[i][i2].size());
//            		for( int i3=0; i3<tables[i][i2].size(); i3++) {
//            			out.writeInt(tables[i][i2].get(i3));
//            		}
//            	}
//            }
//        }
//    	out.close();
//    }
    
//    public static boolean savedExists(String indexDirectory_p) throws IOException {
//    	File file = new File(indexDirectory_p+"/lsh.dat");
//    	return file.exists();
//    }
    
//    public LSHHammingInt(String indexDirectory_p) throws IOException {
//    	database = new BitsObjectWithStringDatabaseMem(indexDirectory_p);
//    	data=database.getRawData();
//        DataInputStream in =
//        		new DataInputStream(
//        				new BufferedInputStream(
//        					new FileInputStream(
//        							new File(indexDirectory_p+"/lsh.dat"))));
//    	
//    	l = in.readInt();
//    	h = in.readInt();
//        gs=new G_HammingInts[l];
//        tables= new int[l][][];
//        for(int i=0;i<l;i++){
//            gs[i]=new G_HammingInts(in);
//            tables[i]=new int[1<<h][];
//            for ( int i2=0; i2<tables[i].length; i2++) {
//            	int curr = in.readInt();
//            	if ( curr != 0 ) {
//            		tables[i][i2] = new int[curr];
//            		for( int i3=0; i3<curr; i3++) {
//            			tables[i][i2][i3] = in.readInt();
//            		}
//            	}
//            }
//        }
//    }
    
//    public long getDataSizeInBytes() throws IOException {
//    	//return this.data.length * 8;
//    	return data_raf.length();
//    }
//    
//    public int getNObjs() throws IOException {
//    	return (int) (data_raf.length() / nLongs);
//    }
    
    public String toString()  {
    	StringBuilder tStr = new StringBuilder();
    	
//    	tStr.append("- buckets\n");
//    	for ( int i=0; i<tables.length; i++ ) {
//    		
//    		tStr.append(gs[i]);
//    		
//    		int sum = 0;
//        	for ( int j=0; j<tables[i].length; j++ ) {
//        		
//        		if (tables[i][j]==null) continue;
//        		tStr.append(tables[i][j].size() + " ");
//        		sum+=tables[i][j].size();
//        		
//        		
//        	}
//        	tStr.append("\t[" + sum + "]\n");
//        	
//        	
//    	}
    	
    	tStr.append( "- nBuckets:\t" );
    	for ( int i=0; i<ls.length; i++ ) {
    		if ( i>0 ) tStr.append(", ");
    		tStr.append( ls[i].getNumberOfBuckets() );
    	}
    	tStr.append("\n");
    	
    	long nNotNulls = 0;
    	int maxN = -1;
    	int maxID1 = -1;
    	long maxID2 = -1;
    	long sum =0;
    	for ( int i1=0; i1<ls.length; i1++ ) {
        	//for ( int i2=0; i2<ls[i1].nh; i2++ ) {
        	for ( TLongIntIterator it = ls[i1].bSize.iterator(); it.hasNext();  ) {	
        		it.advance();
        		int csize = it.value();
        		if ( csize > 0 ) {
        			nNotNulls += 1;
            		int size = csize;
            		sum += size;
            		if ( size > maxN ) {
            			maxN=size;
            			maxID1=i1;
            			maxID2=it.key();
            		}
        		}

        	}
    	}
    	
    	
    	tStr.append("- nObjs\t" + (sum / (double) ls.length) + "\n");
    	
    	tStr.append("- notNullBuckets\t" + nNotNulls + "\n");
    	
    	tStr.append("- avgNotNullBucketSize\t" + sum/(double) nNotNulls + "\n");
    	
    	tStr.append("- biggestBucket\t" + maxN + "\t" + "(" + maxID1 + "," + maxID2 +")");
    
    	//    	try {
//			tStr.append("- sizeInBytes\t" + this.getDataSizeInBytes() + "\n");
//			tStr.append("- nObjects\t" + this.getNObjs() + "\n");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	

    	return tStr.toString();
    }

	public void close() throws IOException {
		fca.close();
		
	}


}
