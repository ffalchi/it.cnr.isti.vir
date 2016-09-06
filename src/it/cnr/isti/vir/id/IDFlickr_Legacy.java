/*******************************************************************************
 * Copyright (c), Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IDFlickr_Legacy extends AbstractID  {
	
	public final long id;
	public final long secret;
	public final int serverid;
	public final byte farmid;
	
	private static boolean fullToString = true;
	
	private static final int byteSize = 21;
	
	public IDFlickr_Legacy(long id) {
		this(id, (byte) -1, -1, -1);
	}
	
	public IDFlickr_Legacy(long id, byte farmid, int serverid,  long secret) {
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


	public static final IDFlickr_Legacy[] readArray(DataInput in, int n) throws IOException {
		IDFlickr_Legacy[] arr = new IDFlickr_Legacy[n];
		
		for ( int i=0; i<arr.length; i++ ) {
			arr[i] = new IDFlickr_Legacy(in);
		}
		return arr;
	}

	
	@Override
	public final void writeData(DataOutput out) throws IOException {
		out.writeLong(id);
		out.writeLong(secret);
		out.writeInt(serverid);
		out.write(farmid);		
		
	}
	
	public IDFlickr_Legacy(ByteBuffer in) throws IOException {
		id = in.getLong();
		secret = in.getLong();
		serverid = in.getInt();
		farmid = in.get();
		
	}
	
	public IDFlickr_Legacy(DataInput in) throws IOException {
		id = in.readLong();
		secret = in.readLong();
		serverid = in.readInt();
		farmid = in.readByte();
	}
	
	public String toString() {
		if ( fullToString ) return id + "_" + farmid + "_" + serverid + "_" + Long.toHexString(secret);
		
		else return Long.toString(id);
	}
	
	public String toString_tabbed() {
		return id + "\t" + farmid + "\t" + serverid + "\t" + Long.toHexString(secret);

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
		return this.id == ((IDFlickr_Legacy) obj).id;
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
		if ( !o.getClass().equals(IDFlickr_Legacy.class) )
			return	IDClasses.getClassID(this.getClass()) - IDClasses.getClassID(o.getClass());
		
		long given = ((IDFlickr_Legacy) o).id;
		if ( id == given ) return 0;
		if ( id > given ) return 1;
		return -1;
	}

}