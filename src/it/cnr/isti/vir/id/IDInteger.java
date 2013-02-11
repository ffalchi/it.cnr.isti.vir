package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class IDInteger extends AbstractID {
	
	public final int id;
	
	public IDInteger(Integer id) {
		this.id = id;
	}
	
	@Override
	public final AbstractID getID() {
		return this;
	}
	
	@Override
	public final int compareTo(AbstractID objID) {
		// if ID classes differ the class id is used for comparing
		if ( !objID.getClass().equals(IDInteger.class) )
			return	IDClasses.getClassID(this.getClass()) - IDClasses.getClassID(objID.getClass());
		
		return id-(((IDInteger) objID).id);
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt(id);		
	}
	
	public IDInteger(ByteBuffer in) throws IOException {
		id = in.getInt();
	}
	
	public IDInteger(DataInput in) throws IOException {
		id = in.readInt();
	}
	
	public static final IDInteger[] readArray(DataInput in, int n) throws IOException {
		IDInteger[] arr = new IDInteger[n];
		
		int[] temp = new int[n];
		byte[] byteArray = new byte[n*4];
		IntBuffer inBuffer = ByteBuffer.wrap(byteArray).asIntBuffer();
		in.readFully(byteArray);
		inBuffer.get(temp, 0, n);
		
		for ( int i=0; i<arr.length; i++ ) {
			arr[i] = new IDInteger(temp[i]);
		}
		return arr;
	}
	
	public String toString() {
		return Integer.toString(id);
	}
	
	public final boolean equals(Object obj) {
		return this.id == ((IDInteger) obj).id;
	}
	
	public final int hashCode() {
		return  id;	
	}
/*
	@Override
	public final AbstractID getID() {
		return this;
	}
	*/
}
