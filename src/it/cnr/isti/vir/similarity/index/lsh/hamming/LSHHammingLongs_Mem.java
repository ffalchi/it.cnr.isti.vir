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
import it.cnr.isti.vir.features.ILongBinaryValues;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.knn.IkNNExecuter;
import it.cnr.isti.vir.similarity.pqueues.AbstractSimPQueue;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_kNN;
import it.cnr.isti.vir.similarity.pqueues.SimPQueue_r;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.util.TimeManager;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;





/**
 * @author Fabrizio Falchi
 *
 */
public class LSHHammingLongs_Mem implements IkNNExecuter {


	public static int nLongs = 64;;
	public static int nBits = nLongs + 64;
	
	int l;
    int h;
    TIntArrayList[][] tables;

    Class<? extends AbstractFeature> fClass;
    
    //BitsObjectWithStringDatabaseMem database;
    long[] data;
    
    AbstractID[] id;
    
    G_HammingLongs[] gs;
    
    public int getNumberOfBuckets(int b){
        return tables[b].length;
    }
    
    public LSHHammingLongs_Mem( FeaturesCollectorsArchive fca, Class<? extends AbstractFeature> fClass,  int h, int l) throws ArchiveException {
    	
    	/// TO DO!!!!
    	// long[] firstValues = ((ILongBinaryValues) fca.get(0).getFeature(fClass)).getValues();
    	
    	this.fClass = fClass;
    	
    	data = new long[AbstractBitsObject.nLongs*fca.size()];
    	
    	id = new AbstractID[fca.size()];
    	
    	int iID = 0;
    	int i=0;
    	
    	Log.info("LSHHammingLongs is reading data in " + fca.getfile().getAbsolutePath());
    	TimeManager tm = new TimeManager(fca.size());
    	for ( AbstractFeaturesCollector f : fca ) {
    		
    		id[iID++] = ((IHasID) f).getID();
    		
    		long[] curr = ((ILongBinaryValues) f.getFeature(fClass)).getValues();
    		
    		for ( int j=0; j<AbstractBitsObject.nLongs; j++ ) {
    			data[i++] = curr[j];
    		}
    		tm.reportProgress();
    	}
    	Log.info("LSHHammingLongs data read ended.");
    	
    
    	this.l=l;
    	this.h=h;
    	gs=new G_HammingLongs[l];
    	tables= new TIntArrayList[l][];
    	for( i=0;i<l;i++){
    		gs[i]=new G_HammingLongs(h, nBits);
    		tables[i]=new TIntArrayList[1<<h];
    	}
    	buildIndex();
    	
    }
    	

    
    /**
     * @param database
     * @param h		number of bits used for hashing
     * @param l		number of G
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
    
    private void buildIndex(){
    	Log.info("Building index");
    	Log.info("Database size: " + id.length) ;
    	
    	TimeManager tm = new TimeManager(id.length);
        for(int o =0; o<id.length; o++){
            insert(o);
            tm.reportProgress();
        }
        
        Log.info("Building index done");
    }
    
    private void insert(int id){
        for(int i=0;i<l;i++){
            int key=
            		gs[i].eval(data, id*AbstractBitsObject.nLongs);
            
            TIntArrayList curr = tables[i][key];
            if (curr==null){
            	curr= new TIntArrayList();
            	tables[i][key]=curr;
            }
            curr.add(id);
        }
    }
	
    static TIntHashSetPool hashSetPool = new TIntHashSetPool(8,30);
      
    public ISimilarityResults getKNNResults(AbstractFeaturesCollector fc, int k){
    	return search( fc.getFeature(fClass), new SimPQueue_kNN(k));
    }
    
	@Override
	public ISimilarityResults getKNNResults(AbstractFeature qObj, int k)
			throws Exception {
		return search(qObj, new SimPQueue_kNN(k));
	}
    
    public ISimilarityResults rangeSearch(AbstractFeaturesCollector fc, double r){
    	return search( fc.getFeature(fClass), new SimPQueue_r(r));
    }
    
    public final ISimilarityResults search(AbstractFeaturesCollector fc, AbstractSimPQueue pQueue) {
		
    	return search( (ILongBinaryValues) fc.getFeature(fClass), pQueue);
    }
    
    public final ISimilarityResults search(AbstractFeature f, AbstractSimPQueue pQueue) {
		
    	return search( (ILongBinaryValues) f, pQueue);
    }
   
    public final ISimilarityResults search(ILongBinaryValues f, AbstractSimPQueue pQueue) {
    		
    	return search(f.getValues(), pQueue);
    }
    
    public final ISimilarityResults search(long[] query, AbstractSimPQueue pQueue){
    	int iHashSet = hashSetPool.acquireTIntHashSet();
    	TIntHashSet objects = hashSetPool.getHashSet(iHashSet);


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

        for(TIntIterator it = objects.iterator(); it.hasNext(); ){
            int oId=it.next();

            int o_offset = oId*AbstractBitsObject.nLongs;
            int dist=Hamming.distance_offset(query, data, o_offset, nLongs);
           	pQueue.offer(id[oId], dist);

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
    
    public void save(String fileName) throws IOException {
        DataOutputStream out =
        		new DataOutputStream(
        				new BufferedOutputStream(
        					new FileOutputStream(
        							new File(fileName))));
    
    	out.writeInt(l);
    	out.writeInt(h);
    	for(int i=0;i<l;i++){
            gs[i].write(out);
            for ( int i2=0; i2<tables[i].length; i2++) {
            	if ( tables[i][i2] == null ) {
            		out.writeInt(0);
            	} else {
            		out.writeInt(tables[i][i2].size());
            		for( int i3=0; i3<tables[i][i2].size(); i3++) {
            			out.writeInt(tables[i][i2].get(i3));
            		}
            	}
            }
        }
    	out.close();
    }
    
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
    
    public int getDataSizeInBytes() {
    	return this.data.length * 8;
    }
    
    public int getNObjs() {
    	return this.data.length * 8 / nLongs;
    }
    
    public String toString() {
    	StringBuilder tStr = new StringBuilder();
    	
    	tStr.append("- buckets\n");
    	for ( int i=0; i<tables.length; i++ ) {
    		
    		tStr.append(gs[i]);
    		
    		int sum = 0;
        	for ( int j=0; j<tables[i].length; j++ ) {
        		
        		if (tables[i][j]==null) continue;
        		tStr.append(tables[i][j].size() + " ");
        		sum+=tables[i][j].size();
        		
        		
        	}
        	tStr.append("\t[" + sum + "]\n");
        	
        	
    	}
    	
    	tStr.append( "- nBuckets:\t" );
    	for ( int i=0; i<tables.length; i++ ) {
    		if ( i>0 ) tStr.append(", ");
    		tStr.append( this.getNumberOfBuckets(i) );
    	}
    	tStr.append("\n");
    	tStr.append("- notNullBucks\t" + this.getNOfNotNullBuckets() + "\n");
    	tStr.append("- sizeInBytes\t" + this.getDataSizeInBytes() + "\n");
    	tStr.append("- nObjects\t" + this.getNObjs() + "\n");
    	

    	return tStr.toString();
    }


}
