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

public class Sort {

	public static final <T> void swap (T[] a, int i, int j) {
		  T t = a[i];
		  a[i] = a[j];
		  a[j] = t;
	}
	
	public static final void swap (int[] a, int i, int j) {
		  int t = a[i];
		  a[i] = a[j];
		  a[j] = t;
	}
	
	public static final void swap (float[] a, int i, int j) {
		float t = a[i];
		  a[i] = a[j];
		  a[j] = t;
	}
	
	public final static void sortAscending(int[] values, int[] other ) {
       
        
    	int n = values.length;
    	do {
    		int	newn = 0;
    		for (int i = 1; i<n; i++) {
    			if (values[i - 1] > values[i]) {
    				swap(values, i - 1, i);
    				swap(other, i - 1, i);
    				newn = i;
    			};
    		};
    		n = newn;
    	} while (n != 0);
        
	}
	
	public final static void sortAscending(int[] values, float[] other1, float[] other2 ) {
        
    	int n = values.length;
    	do {
    		int	newn = 0;
    		for (int i = 1; i<n; i++) {
    			if (values[i - 1] < values[i]) {
    				swap(values, i - 1, i);
    				swap(other1, i - 1, i);
    				swap(other2, i - 1, i);
    				newn = i;
    			};
    		};
    		n = newn;
    	} while (n != 0);
	}
	
	public final static void sortDescending(float[] values, Object[] other ) {
        
    	int n = values.length;
    	do {
    		int	newn = 0;
    		for (int i = 1; i<n; i++) {
    			if (values[i - 1] < values[i]) {
    				swap(values, i - 1, i);
    				swap(other, i - 1, i);
    				newn = i;
    			};
    		};
    		n = newn;
    	} while (n != 0);
	}
	
	public final static void sortDescending(int[] values, Object[] other ) {
      
	  	int n = values.length;
	  	do {
	  		int	newn = 0;
	  		for (int i = 1; i<n; i++) {
	  			if (values[i - 1] < values[i]) {
	  				swap(values, i - 1, i);
	  				swap(other, i - 1, i);
	  				newn = i;
	  			};
	  		};
	  		n = newn;
	  	} while (n != 0);
	}
	
	public final static void sortDescending(int[] values, Object[] other1, Object[] other2) {
	      
	  	int n = values.length;
	  	do {
	  		int	newn = 0;
	  		for (int i = 1; i<n; i++) {
	  			if (values[i - 1] < values[i]) {
	  				swap(values, i - 1, i);
	  				swap(other1, i - 1, i);
	  				swap(other2, i - 1, i);
	  				newn = i;
	  			};
	  		};
	  		n = newn;
	  	} while (n != 0);
	}
	
	
	public final static void sortDescending(float[] values, int[] other ) {
		
        
    	int n = values.length;
    	do {
    		int	newn = 0;
    		for (int i = 1; i<n; i++) {
    			if (values[i - 1] < values[i]) {
    				swap(values, i - 1, i);
    				swap(other, i - 1, i);
    				newn = i;
    			};
    		};
    		n = newn;
    	} while (n != 0);
        
	}
	
	
	public final static void sortDescending(float[] values, int[] other, float[] other2 ) {
        
    	int n = values.length;
    	do {
    		int	newn = 0;
    		for (int i = 1; i<n; i++) {
    			if (values[i - 1] < values[i]) {
    				swap(values, i - 1, i);
    				swap(other, i - 1, i);
    				swap(other2, i - 1, i);
    				newn = i;
    			};
    		};
    		n = newn;
    	} while (n != 0);
        
	}
}
