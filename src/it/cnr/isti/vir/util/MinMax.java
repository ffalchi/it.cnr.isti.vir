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

public class MinMax {

	public static int getMin(int[] values) {
	   int min = values[0];
	   for (int i=1; i<values.length; i++ ) {
			int v = values[i];
	        min = Math.min(v, min);
	   }
	   return min;
	}
	
	public static int[] getMinWithPos(int[] values) {
		int min = values[0];
		int minPos = 0;
		for (int i = 1; i < values.length; i++) {
			int v = values[i];
			if (v < min) {
				min = v;
				minPos = i;
			}
		}
		int[] res = new int[2];
		res[0] = min;
		res[1] = minPos;
		return res;
	}

	public static int getMax(int[] values) {
	   int res = values[0];
	   for (int i=1; i<values.length; i++ ) {
			int v = values[i];
	        res = Math.max(v, res);
	   }
	   return res;
	}
	
	public static int[] getMaxWithPos(int[] values) {
		int max = values[0];
		int maxPos = 0;
		for (int i = 1; i < values.length; i++) {
			int v = values[i];
			if (v > max) {
				max = v;
				maxPos = i;
			}
		}
		int[] res = new int[2];
		res[0] = max;
		res[1] = maxPos;
		return res;
	}
	
	public static int[] getMinMax(int[] values) {
		int min = values[0];
		int max = values[0];
		for (int i=1; i<values.length; i++ ) {
			int v = values[i];
			min = Math.min(v, min);
			max = Math.max(v, max);
		}
		int[] res = new int[2];
		res[0] = min;
		res[1] = max;
		return res;
	}
	
	public static int[][] getMinMaxWithPos(int[] values) {
		int min = values[0];
		int minPos = 0;
		int max = values[0];
		int maxPos = 0;
		for (int i=1; i<values.length; i++ ) {
			int v = values[i];
			if ( v>max ) {
				max = v;
				maxPos = i;
			}
			if ( v<min ) {
				min = v;
				minPos = i;
			}
		}
		int[][] res = new int[2][2];
		res[0][0] = min;
		res[0][1] = minPos;
		res[1][0] = max;
		res[1][1] = maxPos;
		return res;
	}
}
