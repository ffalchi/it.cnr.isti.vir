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
package it.cnr.isti.vir.util;


public class ParallelOptions {
	public static int nProcessors = Runtime.getRuntime().availableProcessors();
	public static int nPInUse = 1;
	
	static {
		System.out.println("Using " + nProcessors + " threads");
	}
	
	public static final synchronized void setNProcessors( int n) {
		nProcessors = n;
	}
	public static final synchronized void set( java.util.Properties properties) {
		String coreStr  = properties.getProperty("core");
		if ( coreStr != null ) {
			nProcessors =  Integer.parseInt(coreStr);
			System.out.println("Using " + nProcessors + " threads");
		}
	}
	
	public static final synchronized int getNFreeProcessors() {
		return getNFreeProcessors(128);
	}
	
	public static final synchronized int getNFreeProcessors(int max) {
		
		int available = nProcessors - nPInUse;
		
		if ( available < 0 ) return 0;
		
		int res = Math.min(available, max);
		
		nPInUse += res;
		
		return res;
	}

	public static final synchronized void free(int n) {
		nPInUse -= n;
 	}
	
	
//	static class Compute_thread implements Runnable {
//        private final int from;
//        private final int to;
//        
//
//        
//        Compute_p_thread(
//        		int from, int to,        		
//
//        		) {
//            this.from = from;
//            this.to = to;
//
//        }
//        
//        @Override
//        public void run() {
//
//        	for(int t=from; t<=to; t++){
//            	
//            }
//        }                
//    }
	
	
//    int threadN = ParallelOptions.getNFreeProcessors()+1;
//    
//    if ( threadN == 1 ) {
//		for ( int i=0; i<n; i++ ) {
//		}
//    } else {
//    	// Parallel
//        Thread[] thread = new Thread[threadN];
//        int[] group = SplitInGroups.split(T, thread.length);
//        int from=0;
//        for ( int i=0; i<group.length; i++ ) {
//        	int curr=group[i];
//        	if ( curr == 0 ) break;
//        	int to=from+curr-1;
//        	thread[i] = new Thread( new Extract_thread(this, from,to, p, x) ) ;
//        	thread[i].start();
//        	from=to+1;
//        }
//        
//        for ( Thread t : thread ) {
//    		if ( t != null ) t.join();
//        }
//        ParallelOptions.free(threadN-1);
//    }
	
}
