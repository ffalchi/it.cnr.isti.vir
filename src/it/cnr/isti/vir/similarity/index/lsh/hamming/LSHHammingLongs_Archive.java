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
import gnu.trove.list.array.TIntArrayList;
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
import it.cnr.isti.vir.util.TimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;





/**
 * @author Fabrizio Falchi
 *
 */
public class LSHHammingLongs_Archive implements IkNNExecuter {

	//private File path;

	public static int nLongs = 64;;
	public static int nBits = nLongs * 64;
	public static int nBytes = nLongs * 8;
	
	int l;
    int h;
    TIntArrayList[][] tables;
    G_HammingInts[] gs;
    
    Class<? extends AbstractFeature> fClass;
    
    FeaturesCollectorsArchive fca;    
    
    boolean parallel = false;

    
    public int getNumberOfBuckets(int b){
        return tables[b].length;
    }
    
//    public final File getDataFile() {
//    	File res =  new File( path.getAbsolutePath() + File.separator + "Data.dat" );
//    	res.getParentFile().mkdirs();
//    	return res;
//    }
    
    public final long getFileChannelPosition(int internalID ) {
    	return nBytes*internalID;
    }
    
//    public LSHHammingLongs( String path, int h, int l) throws ArchiveException, IOException {
//    	
//    	this.path = new File(path);
//    	
//    	data_raf = new RandomAccessFile(getDataFile(), "rw");
//    	
//    	buildIndex();
//    	
//    	
//    }
    
    
    public LSHHammingLongs_Archive( String fcaName, Class<? extends AbstractFeature> fClass,  int h, int l) throws ArchiveException, IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	
    	fca = new FeaturesCollectorsArchive(fcaName, false);
    	
    	this.l=l;
    	this.h=h;
    	
    	this.fca = fca;
    	this.fClass = fClass;
    	
    	buildIndex();
    	    	
    }
    	

    
    /**
     * @param database
     * @param h		number of bits used for hashing
     * @param l		number of G
     * @throws IOException 
     */
//    public LSHHammingInt(BitsObjectWithStringDatabaseMem database,int h, int l) {
//    	
//    	System.out.println("Creating LSHHammingInt " + h + "," + l);
//    	this.database=database;
//    	this.data=database.getRawData();
//    	this.l=l;
//        this.h=h;
//        gs=new G_HammingInts[l];
//        tables= new int[l][][];
//        for(int i=0;i<l;i++){
//            gs[i]=new G_HammingInts(h);
//            tables[i]=new int[1<<h][];
//        }
//        buildIndex();
//    }
    
    private void buildIndex() throws IOException{
    	
    	
    	Log.info("Building index");
    	Log.info("Database size: " + fca.size()) ;
    	
    	this.l=l;
    	this.h=h;
    	gs=new G_HammingInts[l];
    	tables= new TIntArrayList[l][];
    	for( int i=0;i<l;i++){
    		gs[i]=new G_HammingInts(h, nBits);
    		tables[i]=new TIntArrayList[1<<h];
    	}
    	
    	
    	TimeManager tm = new TimeManager(fca.size());
    	int i=0;
        for( AbstractFeaturesCollector fc : fca ){
        	long[] values = ((ILongBinaryValues) fc.getFeature(fClass)).getValues();
            insert(values, i);
            tm.reportProgress();
            i++;
        }
        
        Log.info("Building index done");
    }
    
    
	public void save() throws IOException {

		Log.info("Saving LSH for " + fca.getfile().getAbsolutePath());
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(new File(fca.getfile().getAbsolutePath()
						+ ".lsh"))));

		out.writeInt(FeatureClasses.getClassID(fClass));
		
		out.writeInt(l);
		out.writeInt(h);
		for (int i = 0; i < l; i++) {
			gs[i].write(out);
			for (int i2 = 0; i2 < tables[i].length; i2++) {
				if (tables[i][i2] == null) {
					out.writeInt(0);
				} else {
					out.writeInt(tables[i][i2].size());
					for (int i3 = 0; i3 < tables[i][i2].size(); i3++) {
						out.writeInt(tables[i][i2].get(i3));
					}
				}
			}
		}
		out.close();

		Log.info("Saving completed");
	}
    
	public LSHHammingLongs_Archive(String fcaName ) throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		fca = new FeaturesCollectorsArchive(fcaName, false);

		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(new File(fca.getfile().getAbsolutePath() + ".lsh"))));

		
		fClass = FeatureClasses.getClass(in.readInt());
		
		l = in.readInt();
		h = in.readInt();
		gs = new G_HammingInts[l];
		tables = new TIntArrayList[l][];
		for (int i = 0; i < l; i++) {
			gs[i] = new G_HammingInts(in);
			tables[i] = new TIntArrayList[1 << h];
			for (int i2 = 0; i2 < tables[i].length; i2++) {
				int curr = in.readInt();
				if (curr != 0) {
					tables[i][i2] = new TIntArrayList( curr );
					for (int i3 = 0; i3 < curr; i3++) {
						tables[i][i2].add( in.readInt() );
					}
				}
			}
		}
	}
    
    
    private synchronized final long[] readData(int id ) throws IOException, ArchiveException {
    	AbstractFeaturesCollector fc = fca.get(id);
    	return ((ILongBinaryValues) fc.getFeature(fClass)).getValues();
    }
    
    private final void insert(long[] data, int id) throws IOException{
        //long[] data = new long[nLongs];
    	for(int i=0;i<l;i++){
        	//readData(id, data);
            int key= gs[i].eval(data);
            
            TIntArrayList curr = tables[i][key];
            if (curr==null){
            	curr= new TIntArrayList();
            	tables[i][key]=curr;
            }
            curr.add(id);
        }
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
    
    public ISimilarityResults rangeSearch(AbstractFeaturesCollector fc, double r) throws IOException, InterruptedException  {
    	return search(fc, new SimPQueue_r(r));
    }
    
    public final ISimilarityResults search(AbstractFeaturesCollector fc, AbstractSimPQueue pQueue) throws IOException, InterruptedException  {
		
    	return search( (ILongBinaryValues) fc.getFeature(fClass), pQueue);
    }
    
    public final ISimilarityResults search(AbstractFeature f, AbstractSimPQueue pQueue) throws IOException, InterruptedException  {
		
    	return search( (ILongBinaryValues) f, pQueue);
    }
   
    public final ISimilarityResults search(ILongBinaryValues f, AbstractSimPQueue pQueue) throws IOException, InterruptedException {
    		
    	return search(f.getValues(), pQueue);
    }
    
    
    
	class Search implements Runnable {

		private final TIntHashSet objects;
		private final long[] query;
		private final Iterator<Integer> ls;
	    
		Search(long[] query, Iterator<Integer> ls, TIntArrayList[][] tables, G_HammingInts[] gs, TIntHashSet objects) {
            this.ls = ls;
			this.objects = objects;
            this.query=  query;
        }
        
        @Override
        public void run() {
        	
        	Integer l;
        	
        	while (true) {
            	try {
            		l = ls.next();
            	} catch (NoSuchElementException e) {
            		break;
            	}
            	if ( l == null ) break;
        	
	            int key=gs[l].eval(query);
	            
	            TIntArrayList bucket = tables[l][key];
	            if ( bucket!= null) {
	
	            	for(int iE=0; iE<bucket.size(); iE++ ){
	                    int o=bucket.get(iE);
	                    synchronized(objects) {
		                	objects.add(o);
	                    }
		            }
	            }
        	}

        }                
    }
    
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
        	
        	AbstractFeaturesCollector fc = null;
            while (true) {
           		int oID;
           		synchronized(it) {
           			if ( it.hasNext() == false ) break;
           			oID =it.next();
           		}
                try {
                	fc = fca.get(oID);
				} catch ( ArchiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                int dist=Hamming.distance(query, ((ILongBinaryValues) fc.getFeature(fClass)).getValues()  );
                pQueue.offer(fc, dist);
            }

        }                
    }
	
    public final ISimilarityResults search(long[] query, AbstractSimPQueue pQueue) throws IOException, InterruptedException{
    	int iHashSet = hashSetPool.acquireTIntHashSet();
    	TIntHashSet objects = hashSetPool.getHashSet(iHashSet);

    	ArrayList<Integer> ls = new ArrayList(gs.length);
    	for ( int i=0; i<gs.length; i++) {
    		ls.add(i);
    	}
    	

    	// Parallel
    	if ( parallel ) {
			int nThread = ParallelOptions.reserveNFreeProcessors()+1;
			Thread[] thread = new Thread[nThread];
			for ( int ti=0; ti<thread.length; ti++ ) {
				thread[ti] = new Thread( new Search(query, ls.iterator(), tables, gs, objects) ) ;
	        	thread[ti].start();
			}
			
	        for ( Thread t : thread ) {
	        	t.join();
	        }
	        ParallelOptions.free(nThread-1);
    	} else {
	    	
	        for(int i=0;i<l;i++){
	        	
	            int key=gs[i].eval(query);
	            
	            TIntArrayList bucket = tables[i][key];
	            if ( bucket!= null) {
	
	            	for(int iE=0; iE<bucket.size(); iE++ ){
	                    int o=bucket.get(iE);
		                objects.add(o);
		            }
	            }
	        }
    	}
    	
    	if ( parallel ) {
	        // Parallel
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
    		Log.info_verbose("Candidate set size: " + objects.size());
	       long[] data = new long[nLongs];
	       for(TIntIterator it = objects.iterator(); it.hasNext(); ){
	            int oID=it.next();
	
	            AbstractFeaturesCollector fc = null;
				try {
					fc = fca.get(oID);
				} catch (ArchiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            int dist=Hamming.distance(query, ((ILongBinaryValues) fc.getFeature(fClass)).getValues()  );
	            pQueue.offer(fc, dist);
	
	        }
    	}
    	
        hashSetPool.releaseTIntHashSet(iHashSet);
        
        return pQueue.getResults();
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
    	for ( int i1=0; i1<tables.length; i1++ ) {
        	for ( int i2=0; i2<tables[i1].length; i2++ ) {
        		if ( tables[i1][i2] != null ) {
        			res += 1;
        		}
        	}
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
    	for ( int i=0; i<tables.length; i++ ) {
    		if ( i>0 ) tStr.append(", ");
    		tStr.append( this.getNumberOfBuckets(i) );
    	}
    	tStr.append("\n");
    	tStr.append("- notNullBucks\t" + this.getNOfNotNullBuckets() + "\n");
    	
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
