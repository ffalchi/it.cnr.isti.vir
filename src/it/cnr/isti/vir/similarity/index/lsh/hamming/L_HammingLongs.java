package it.cnr.isti.vir.similarity.index.lsh.hamming;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

public class L_HammingLongs {

	public static int nLongs = 64;;
	public static int nBits = nLongs * 64;
	public static int nBytes = nLongs * 8;
	
	
	//TIntArrayList[] bucket;
	
    int h;
    int nh;
    
    G_HammingLongs g;
    
    TLongIntHashMap bSize;
    TLongLongHashMap bucketOffset;
    RandomAccessFile bucketsRAF;
    FileChannel fileChannel;
    //boolean parallel = true;

    File dataFile;
    int size;
    
    public L_HammingLongs(
    		File dataFile,
    		File lFile,
    		int h
    		) throws Exception {

    	this.dataFile = dataFile;
    	this.h = h;
    	
    	size = (int) (dataFile.length() / nBytes);
    	
    	build(lFile);
    	
    	//save(lFile);
	}
    
    
//	class Build implements Runnable {
//
//		private final TimeManager tm;
//	    private final DataInputStream in;
//		private final MutableInt mi;
//		
//		private final long[][] batch = new long[100][nLongs];
//		
//	    Build( DataInputStream in, TimeManager tm, MutableInt i) {
//            this.in = in;
//	    	this.tm = tm;
//	    	this.mi = i;
//        }
//        
//        @Override
//        public void run() {
//        	byte[] bytes = new byte[nBytes];
//        	try {
//        		while (true) {
//        			int ib=0;
//        			int startID=0;
//        			synchronized(in) {
//        				try {
//        					startID=mi.get();
//        					for ( ; ib<batch.length; ib++) {        						
//        						in.readFully(bytes);
//        						LongByteArrayUtil.convToLong(bytes, batch[ib]);
//        						mi.inc();
//        					}
//        				} catch (EOFException err) {
//        					int id = startID;
//        					for ( int i=0; i<ib; i++) {
//        						L_HammingLongs.this.insert(batch[i], id++);
//        					}
//        					return;
//        				}
//        			}
//					int id = startID;
//					for ( int i=0; i<ib; i++) {
//						L_HammingLongs.this.insert(batch[i], id++);
//					}
//        			tm.reportProgress(ib-1);
//        		}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        	
//        }   
//        
//        
//    }
    
    private void build(File file) throws Exception {
    	
    	nh = 1<<h;
    	g=new G_HammingLongs(h, nBits);
    	
    	TIntArrayList[] bucket = new TIntArrayList[nh];
    	
    	TimeManager tm = new TimeManager(size);

   		DataInputStream in =
   				new DataInputStream(
   						new BufferedInputStream(
   								new FileInputStream(dataFile)));
   		
//   		if ( parallel ) {
//    		int nThread = ParallelOptions.reserveNFreeProcessors()+1;
//			Thread[] thread = new Thread[nThread];
//			TIntArrayList[] results = new TIntArrayList[nThread];
//			MutableInt mi = new MutableInt();
//			for ( int ti=0; ti<thread.length; ti++ ) {
//				results[ti] = new TIntArrayList();
//				thread[ti] = new Thread( new Build( in, tm, mi ));
//	        	thread[ti].start();
//			}
//			
//	        for ( int ti=0; ti<thread.length; ti++  ) {
//	        	thread[ti].join();
//	        }
//	        ParallelOptions.free(nThread-1);
//   		} else {
   		
    		byte[] bytes = new byte[nBytes];
    		long[] values = new long[nLongs];
	    	for ( int i=0; i<size; i++ ) {
	    		in.readFully(bytes);
	    		
	    		//insert(g.eval(bytes), i);
	    		LongByteArrayUtil.convToLong(bytes, values);
	    		insert(bucket, g.eval(values), i);
	    		
	    		tm.reportProgress();
    		}
//   		}
   		

   		
    	in.close();
        Log.info("Building index done in: " + tm.getTotalTime_STR());
        
        save(bucket, file);
    }  
    
    private final void insert(TIntArrayList[] bucket, int key, int id) throws IOException{
        

        TIntArrayList curr = bucket[key];
        if (curr==null) {
           	synchronized ( bucket ) {
           		curr = bucket[key];
           		if ( curr ==  null) {
           			bucket[key]=curr=new TIntArrayList();
           			curr.add(id);
           			return;
           		}
           	}
        }
        curr.add(id);        
    }

    public int getNumberOfBuckets(){
        return nh;
    }

    
	private void save(TIntArrayList[] bucket, File file) throws IOException {

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
		
		out.writeInt(h);
		g.write(out);
		
		//out.writeInt(this.getNOfNotNullBuckets());
		
   		int nOfNotNullBuckets = 0;
   		for ( int i=0; i<nh; i++ ) {
   			if ( bucket[i] != null ) nOfNotNullBuckets ++;
   		}
   		out.writeInt(nOfNotNullBuckets);
		
		for (int i2 = 0; i2 < bucket.length; i2++) {
			if (bucket[i2] != null) {
				out.writeInt(i2);
				out.writeInt(bucket[i2].size());
			}
		}
		out.close();
		
        DataOutputStream out_b =
        		new DataOutputStream(
        				new BufferedOutputStream(
        					new FileOutputStream(
        							new File(file.getAbsolutePath() + ".b"))));
		
		for (int i2 = 0; i2 < bucket.length; i2++) {
			if (bucket[i2] != null) {
				for (int i3 = 0; i3 < bucket[i2].size(); i3++) {
					out_b.writeInt(bucket[i2].get(i3));
				}
			}
		}
		out_b.close();
	}
 
    
    public L_HammingLongs(File file) throws Exception {
    	
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		
    	File bFile = new File(file.getAbsolutePath() + ".b");    	
		bucketsRAF = new RandomAccessFile(bFile, "r");		
		fileChannel = bucketsRAF.getChannel();
		
    	h = in.readInt();
    	nh = 1 << h;
    	g = new G_HammingLongs(in);
    	
    	int nNotNulls = in.readInt();

    	//bSize = new int[nh];
    	//bucketOffset = new long[nh];
    	bSize = new TLongIntHashMap();;
    	bucketOffset = new TLongLongHashMap();
    	long offsetAcc = 0;
    	
    	for ( int i=0; i<nNotNulls; i++ ) {
			int ib = in.readInt();
    		int csize =  in.readInt();
			
    		//bSize[ib] = csize;
    		//bucketOffset[ib] = offsetAcc;
    		
    		bSize.put(ib, csize);
			bucketOffset.put(ib, offsetAcc);
			if (csize != 0) {
				offsetAcc += csize * Integer.BYTES; 
			}
		}
		
		in.close();
		
    }
	
    public final int getNOfNotNullBuckets() {
//    	int res = 0;
//    	for ( int i=0; i<bSize.length; i++ ) {
//        	if (bSize[i] > 0 ) {
//        		res += 1;
//        	}
//        }
    	
    	return bSize.size();
    }
    
    public final synchronized IntBuffer getInBucket(long[] query) throws IOException {
    	int key=g.eval(query);
        
    	int size = bSize.get(key);
    	if ( size <= 0 ) {
    		return null;
    	} 
    	 
    	//bucketsRAF.seek( bucketOffset[key] );
    	
    	IntBuffer ib = fileChannel.map(FileChannel.MapMode.READ_ONLY, bucketOffset.get(key), size*4).asIntBuffer();

//    	IntBuffer intBuffer = IntBuffer.wrap( v );
//    	fc.read(intBuffer);
//    	for ( int i=0; i<v.length; i++) {
//    		v[i] = bucketsRAF.readInt();
//    	}
    	
//    	byte[] bytes = new byte[size*Integer.BYTES];
//   	bucketsRAF.readFully(bytes);
//       	IntByteArrayUtil.convToInt(bytes, v );

        return ib;
    }
    
    
    
}


