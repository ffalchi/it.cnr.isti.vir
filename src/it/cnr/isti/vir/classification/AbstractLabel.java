package it.cnr.isti.vir.classification;

import java.io.DataOutput;
import java.io.IOException;

public abstract class AbstractLabel implements Comparable<AbstractLabel> {
	
	
	public int hashCode() {
		return getLabel().hashCode();
	}
	
	public abstract void writeData(DataOutput out) throws IOException;
	
	@SuppressWarnings("rawtypes")
	protected abstract Comparable getLabel();
	
	public String toString() {
		return getLabel().toString();
	}
	
	public boolean equals(Object that) {
		if ( this == that ) return true;
		if ( that == null ) return false;
		if ( getClass() != that.getClass()) return false;
		return this.getLabel().equals(((AbstractLabel) that).getLabel());		
	}
	
}

