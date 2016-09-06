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
	public final static void sortAscending(float[] values, int[] other ) {
       
        
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
