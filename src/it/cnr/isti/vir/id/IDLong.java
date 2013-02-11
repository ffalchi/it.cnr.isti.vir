package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;

public class IDLong  extends AbstractID {
	
	public final long id;
	
	public IDLong(long id) {
		this.id = id;
	}
	
	public IDLong(String str) {
		id = Long.parseLong(str);
	}

	public static final IDLong[] readArray(DataInput in, int n) throws IOException {
		IDLong[] arr = new IDLong[n];
		
		long[] temp = new long[n];
		byte[] byteArray = new byte[n*8];
		LongBuffer inBuffer = ByteBuffer.wrap(byteArray).asLongBuffer();
		in.readFully(byteArray);
		inBuffer.get(temp, 0, n);
		
		for ( int i=0; i<arr.length; i++ ) {
			arr[i] = new IDLong(temp[i]);
		}
		return arr;
	}

	@Override
	public int compareTo(AbstractID o) {
		// if ID classes differ the class id is used for comparing
		if ( !o.getClass().equals(IDLong.class) )
			return	IDClasses.getClassID(this.getClass()) - IDClasses.getClassID(o.getClass());
		
		long given = ((IDLong) o).id;
		if ( id == given ) return 0;
		if ( id > given ) return 1;
		return -1;
	}
	
	@Override
	public final void writeData(DataOutput out) throws IOException {
		out.writeLong(id);		
	}
	
	public IDLong(ByteBuffer in) throws IOException {
		id = in.getLong();
	}
	
	public IDLong(DataInput in) throws IOException {
		id = in.readLong();
	}
	
	public String toString() {
		return Long.toString(id);
	}
	
	public boolean equals(Object obj) {
		return this.id == ((IDLong) obj).id;
	}
	
	
	public final int hashCode() {
		return  (int)( id ^ (id >>> 32) );	
	}

	@Override
	public final AbstractID getID() {
		return this;
	}
}
