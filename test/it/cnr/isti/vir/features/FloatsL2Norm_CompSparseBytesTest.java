/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi and Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
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

import static org.junit.Assert.assertTrue;
import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.util.RandomOperations;

import org.junit.Test;

public class FloatsL2Norm_CompSparseBytesTest {

	@Test
	public void test() {
		double nonZeroProb = 0.2;
		int length = 4096;
		
		for ( int ic=0; ic<100; ic++ ) {
			byte[] origBytes = new byte[length];
			
			for ( int i=0; i<origBytes.length; i++ ) {
				if ( RandomOperations.trueORfalse(0.2) ) {
					origBytes[i] = RandomOperations.getByte();
				} else {
					origBytes[i] = -128;
				}
			}
			
			byte[] comp = FloatsL2Norm_CompSparseBytes.getComp(origBytes);
			
			assertTrue(FloatsL2Norm_CompSparseBytes.getCompSize(origBytes) == comp.length);
			
			assertTrue(FloatsL2Norm_CompSparseBytes.getDecompSize(comp) == length);
			
			byte[] deComp = FloatsL2Norm_CompSparseBytes.getDecomp(comp);
			
			assertTrue(origBytes.length == deComp.length);
			
			for ( int i=0; i<origBytes.length; i++ ) {
				assertTrue( origBytes[i] == deComp[i] );
			}
			

		}
	}

	
	
}
