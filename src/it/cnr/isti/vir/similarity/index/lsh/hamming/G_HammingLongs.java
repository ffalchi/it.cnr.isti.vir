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

import it.cnr.isti.vir.util.RandomOperations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class G_HammingLongs {

	
    /**
     * Single bit hashing
     */
	final H_HammingLongs[] hs;
    
    /**
     * Number of bits used for hashing
     */
    final int h;

	int[] long_pos;
    long[] long_mask;
    
	int[] byte_pos;
    byte[] byte_mask;
    
    private void fillArrays() {
    	long_pos = new int[hs.length];
    	long_mask = new long[hs.length];
    	
    	byte_pos = new int[hs.length];
    	byte_mask = new byte[hs.length];
    	
    	for(int i=0;i<h;i++) {
    		long_pos[i] = hs[i].long_pos;
    		long_mask[i] = hs[i].long_mask;
    		
    		byte_pos[i] = hs[i].byte_pos;
    		byte_mask[i] = hs[i].byte_mask;
    	}
    }
    
    public G_HammingLongs(int h_p, int nBits){
        h=h_p;
        hs=new H_HammingLongs[h];

        
        int[] b = RandomOperations.getDistinctInts(h, nBits);
        for(int i=0;i<h;i++){
            hs[i]=new H_HammingLongs(b[i], 1<<i);
        }
        fillArrays();
    }
    
    public G_HammingLongs(DataInputStream in) throws IOException {
		h = in.readInt();
		hs=new H_HammingLongs[h];
		for(int i=0;i<h;i++){
	        hs[i] = new H_HammingLongs(in);
		}
		fillArrays();
	}

	public void write(DataOutputStream out) throws IOException {
		out.writeInt(h);
		for(int i=0;i<h;i++){
	        hs[i].write(out);
		}
	}
	
//	public final int eval(LongBuffer lb) {
//		long[] temp = new long[lb.capacity()];
//		lb.get(temp);
//		return eval(temp);
//	}
    
	public final int eval(long[] data) {
    	int res = 0;
    	for(int i=0;i<h;i++){    		
    		res=res|((data[long_pos[i]]&long_mask[i]) == 0L ? 0 : 1<<i);
    	}
    	return res;
    }
	
	public final int eval(byte[] data) {
    	int res = 0;
    	for(int i=0;i<h;i++){    		
    		res=res|((data[byte_pos[i]]&byte_mask[i]) == 0L ? 0 : 1<<i);
    	}
    	return res;
    }
    
    public final int eval(long[] data, int offset) {
    	int res = 0;
    	for(int i=0;i<h;i++){
    		res=res|((data[offset+long_pos[i]]&long_mask[i]) == 0L ? 0 : 1<<i);
    	}
    	return res;
    }

    public String toString() {
    	StringBuilder tStr = new StringBuilder();
    	
    	tStr.append("   " + "h[" + h  + "]: " );
    	for ( int i=0; i<hs.length; i++) {
    		tStr.append( hs[i].long_mask + "/" +  hs[i].long_pos + " ");
    	}
    	tStr.append("\n");
    	
    	return tStr.toString();
    			
    }
    
}
