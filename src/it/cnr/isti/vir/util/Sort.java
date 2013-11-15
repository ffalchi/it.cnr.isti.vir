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
		
//        for (int i = 0; i < values.length; i++) {
//            for (int j = i+1; j < values.length; j++) {
//                if (values[i] > values[j]) {
//                    int intTemp = values[i];
//                    values[i] = values[j];
//                    values[j] = intTemp;
//                    
//                    intTemp = other[i];
//                    other[i] = other[j];
//                    other[j] = intTemp;
//                }
//            }
//        }
        
        
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
		
//        for (int i = 0; i < values.length; i++) {
//            for (int j = i+1; j < values.length; j++) {
//                if (values[i] > values[j]) {
//                    int intTemp = values[i];
//                    values[i] = values[j];
//                    values[j] = intTemp;
//                    
//                    float temp = other1[i];
//                    other1[i] = other1[j];
//                    other1[j] = temp;
//                    
//                    temp = other2[i];
//                    other2[i] = other2[j];
//                    other2[j] = temp;
//                }
//            }
//        }
        
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
		
//        for (int i = 0; i < values.length; i++) {
//            for (int j = i+1; j < values.length; j++) {
//                if (values[i] < values[j]) {
//                	float temp = values[i];
//                    values[i] = values[j];
//                    values[j] = temp;
//                    
//                    Object objTemp = other[i];
//                    other[i] = other[j];
//                    other[j] = objTemp;
//                }
//            }
//        }
        
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
	
	
	public final static void sortDescending(float[] values, int[] other ) {
		
//        for (int i = 0; i < values.length; i++) {
//            for (int j = i+1; j < values.length; j++) {
//                if (values[i] < values[j]) {
//                	float temp = values[i];
//                    values[i] = values[j];
//                    values[j] = temp;
//                    
//                    int objTemp = other[i];
//                    other[i] = other[j];
//                    other[j] = objTemp;
//                }
//            }
//        }  
        
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
		
//        for (int i = 0; i < values.length; i++) {
//            for (int j = i+1; j < values.length; j++) {
//                if (values[i] < values[j]) {
//                	float temp = values[i];
//                    values[i] = values[j];
//                    values[j] = temp;
//                    
//                    int objTemp = other[i];
//                    other[i] = other[j];
//                    other[j] = objTemp;
//                    
//                    float obj2Temp = other2[i];
//                    other2[i] = other2[j];
//                    other2[j] = obj2Temp;
//                }
//            }
//        } 
        
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
