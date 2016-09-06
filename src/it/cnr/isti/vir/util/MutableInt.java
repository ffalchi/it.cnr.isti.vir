package it.cnr.isti.vir.util;

public final class MutableInt {
    public int value;
    
    public MutableInt() {
    	value = 0;
    }
    
    public synchronized void inc() {
    	value++;
    }
    
    public synchronized int get() {
    	return value;
    }
    
    public synchronized String toString() {
    	return Integer.toString( value );
    }
}
