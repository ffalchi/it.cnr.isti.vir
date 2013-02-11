package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class IDString extends AbstractID {
	
	public final String id;
	
	public IDString(String id) {
		this.id = id;
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt	( id.length());
		out.writeChars	( id);	
	}
	
	public IDString(ByteBuffer in) throws IOException {
		int size = in.getInt();
		
		char[] chars = new char[size];
		for ( int i=0; i<chars.length; i++ ) {
			chars[i] = in.getChar();
		}
		
		id = new String(chars);
	}
	
	public IDString(DataInput in) throws IOException {
		int size = in.readInt();
		
		char[] chars = new char[size];
		byte[] byteArray = new byte[size*2];
		CharBuffer inBuffer = ByteBuffer.wrap(byteArray).asCharBuffer();
		in.readFully(byteArray);
		inBuffer.get(chars, 0, size);
		
		id = new String(chars);
	}
	
	public static final IDString[] readArray(DataInput in, int n) throws IOException {
		IDString[] arr = new IDString[n];
		for ( int i=0; i<arr.length; i++ ) {
			arr[i] = new IDString(in);
		}
		return arr;
	}
	
	public boolean equals(Object obj) {
		return this.id.equals(((IDString) obj).id);
	}
	
	public final int hashCode() {
		return  id.hashCode();	
	}
	
	public String toString() {
		return id;
	}
	

	
	@Override
	public int compareTo(AbstractID o) {
		// if ID classes differ the class id is used for comparing
		if ( !o.getClass().equals(IDString.class) )
			return	IDClasses.getClassID(this.getClass()) - IDClasses.getClassID(o.getClass());
		
		return id.compareTo(((IDString) o).id);
	}

}
