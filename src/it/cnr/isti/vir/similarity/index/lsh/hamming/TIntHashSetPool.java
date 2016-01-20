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

import gnu.trove.set.hash.TIntHashSet;

public class TIntHashSetPool {

    private final TIntHashSet[] hashSetPool;
    private final boolean[] hashSetInUse;
    private final int init;
    public TIntHashSetPool(int poolSize, int init) {
    	hashSetPool= new TIntHashSet[poolSize];
    	hashSetInUse = new boolean[poolSize];
    	this.init = init;
    }
    
	public final synchronized int acquireTIntHashSet() {
		while (true) {
			for (int i = 0; i < hashSetPool.length; i++) {
				if (hashSetInUse[i] == false) {
					hashSetInUse[i] = true;
					if ( hashSetPool[i] == null ) {
						hashSetPool[i] = new TIntHashSet(init);
					} else {
						hashSetPool[i].clear();
					}
					return i;
				}
			}
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
   
   public final synchronized void releaseTIntHashSet(int i) {
		hashSetInUse[i] = false;
		notify();
   }
   
   public final TIntHashSet getHashSet(int i) {
	   return hashSetPool[i];
   }

}
