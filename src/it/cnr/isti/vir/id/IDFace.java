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
import java.nio.CharBuffer;

public class IDFace extends AbstractID {


	public final String id;
	public final float minX;
	public final float minY;
	public final float maxX;
	public final float maxY;
	
	public final int inImageID;
	
	public IDFace(String id) {
		this(id, -1, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	public IDFace(String id, int inImageID) {
		this(id, inImageID, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
	}
	
	public IDFace(String id,float minX, float minY, float maxX, float maxY) {
		this(id, -1, minX, minY, maxX, maxY);
	}
	
	/**
	 * The rectangle of the face in the image is expressed in float.
	 * Min and max x and y are reported as relative position in the image
	 * in order to be invariant to resize of the image.
	 * Thus if x=300 pixels and image has a width of 600, x sould be 0.5 
	 * 
	 * @param id	The ID of the whole image
	 * @param inImageID	Optional ID for the face in the image
	 * @param minX	
	 * @param minY
	 * @param maxX
	 * @param maxY
	 */
	public IDFace(String id, int inImageID, float minX, float minY, float maxX, float maxY) {
		this.id = id;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.inImageID = inImageID;
		
	}


	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeInt	( id.length());
		out.writeChars	( id );
		out.writeInt( inImageID);
		out.writeFloat(minX);
		out.writeFloat(minY);
		out.writeFloat(maxX);
		out.writeFloat(maxY);
		
	}
	
	public IDFace(ByteBuffer in) throws IOException {
		int size = in.getInt();
		
		char[] chars = new char[size];
		for ( int i=0; i<chars.length; i++ ) {
			chars[i] = in.getChar();
		}
		
		id = new String(chars);
		
		inImageID = in.getInt();
		minX = in.getFloat();
		minY = in.getFloat();
		maxX = in.getFloat();
		maxY = in.getFloat();
	}
	
	public IDFace(DataInput in) throws IOException {
		int size = in.readInt();
		
		char[] chars = new char[size];
		byte[] byteArray = new byte[size*2];
		CharBuffer inBuffer = ByteBuffer.wrap(byteArray).asCharBuffer();
		in.readFully(byteArray);
		inBuffer.get(chars, 0, size);
		
		id = new String(chars);
		
		inImageID = in.readInt();
		minX = in.readFloat();
		minY = in.readFloat();
		maxX = in.readFloat();
		maxY = in.readFloat();
	}
	
	public static final IDFace[] readArray(DataInput in, int n) throws IOException {
		IDFace[] arr = new IDFace[n];
		for ( int i=0; i<arr.length; i++ ) {
			arr[i] = new IDFace(in);
		}
		return arr;
	}
	
	public boolean equals(Object obj) {
		return this.id.equals(((IDFace) obj).id) && this.inImageID==((IDFace) obj).inImageID;
	}
	
	public final int hashCode() {
		return  id.hashCode()*31+Integer.hashCode(inImageID);	
	}
	
	public String toString() {
		if (inImageID < 0) return "" + id;
		else return id + "_" + inImageID;
	}
		
	@Override
	public int compareTo(AbstractID o) {
		// if ID classes differ the class id is used for comparing
		if ( !o.getClass().equals(IDFace.class) )
			return	IDClasses.getClassID(this.getClass()) - IDClasses.getClassID(o.getClass());
		
		int temp = id.compareTo(((IDFace) o).id);
		if ( temp == 0 ) return Integer.compare(this.inImageID,((IDFace)o).inImageID);
		else return temp;
	}
	
	
	public String getId() {
		return id;
	}

	public float getMinX() {
		return minX;
	}

	public float getMinY() {
		return minY;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMaxY() {
		return maxY;
	}

	public int getInImageID() {
		return inImageID;
	}
}
