/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
