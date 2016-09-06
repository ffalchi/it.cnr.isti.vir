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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.similarity.index.lsh.hamming;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Amato
 */

public class H_HammingLongs  {
    
	int b;
	
	int resBit;
	
	int long_pos;
    long long_mask;
        
    int byte_pos;
    byte byte_mask;
    
    public H_HammingLongs(int b, int res){
        this.b = b;
        this.resBit= res;
        
        setMasks();
    }
    
    private void setMasks() {
        long_pos=(b/64);        
        long_mask=1L<<(64-b%64);
        
        byte_pos=(b/8);        
        byte_mask=(byte) (1<<(8-b%8));
    }
    
    public H_HammingLongs(DataInputStream in) throws IOException {
		resBit = in.readInt();
		b = in.readInt();
		setMasks();
	}
    
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(resBit);
		out.writeInt(b);	
	}

	public final int eval(long[] data) {
    	return ((data[long_pos]&long_mask) == 0L ? 0 : resBit);
    }
    
    public final int eval(long[] data, int offset) {
    	return (data[offset+long_pos]&long_mask) == 0L ? 0 : resBit;
    }
    
    public final int getBit() {
    	return b;
    }



}
