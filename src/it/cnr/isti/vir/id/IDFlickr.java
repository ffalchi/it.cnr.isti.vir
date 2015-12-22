package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IDFlickr extends AbstractID {
	
	public final long id;
	public final long secret;
	public final int serverid;
	public final byte farmid;
	
	private static boolean fullToString = true;
	
	
	public IDFlickr(long id, byte farmid, int serverid,  long secret) {
		this.id = id;
		
		this.secret = secret;
		this.farmid = farmid;
		this.serverid = serverid;
	}
	
//	public IDFlickr(String[] strArr) {
//		id
//		
//		serverid = Integer.parseInt(strArr[17]);
//		farmid = (byte) Integer.parseInt(strArr[18]);
//		secret = Long.parseLong(strArr[19], 16);
//	}




	
	@Override
	public final void writeData(DataOutput out) throws IOException {
		out.writeLong(id);
		out.writeLong(secret);
		out.writeInt(serverid);
		out.write(farmid);		
		
	}
	
	public IDFlickr(ByteBuffer in) throws IOException {
		id = in.getLong();
		secret = in.getLong();
		serverid = in.getInt();
		farmid = in.get();
		
	}
	
	public IDFlickr(DataInput in) throws IOException {
		id = in.readLong();
		secret = in.readLong();
		serverid = in.readInt();
		farmid = in.readByte();
	}
	
	public String toString() {
		if ( fullToString ) return id + "_" + farmid + "_" + serverid + "_" + Long.toHexString(secret);
		
		else return Long.toString(id);
	}
	
//	public String getURL_s(long id) {
//		return
//    		  "https://farm" +
//    		  farmid + ".staticflickr.com/" +
//    		  serverid + "/" +
//    		  id + "_" +
//    		  Long.toHexString(secret) + "_" + 
//    		  "s" +".jpg";
//	}
	
	//https://c2.staticflickr.com/4/3576/3522678715_00ec3ff794_b.jpg
	public String getURL_q(long id) {
		return
    		  "https://c2.staticflickr.com/" +
    		  farmid + "/" +
    		  serverid + "/" +
    		  id + "_" +
    		  String.format("%010x", secret) + //Long.toHexString(secret) + "_" + 
    		  "q" +".jpg";
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

}