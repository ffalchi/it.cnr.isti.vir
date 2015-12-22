/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.classification;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


public class IntegerLabel extends AbstractLabel {
	
	public final int id;
	
	public IntegerLabel(Integer id) {
		this.id = id;
	}
	
	protected static String getString(int id) {
		return Integer.toString(id);
	}
	
	public IntegerLabel(ByteBuffer in) throws IOException {
		id = in.getInt();
	}
	
	public IntegerLabel(DataInput in) throws IOException {
		id = in.readInt();
	}
	
	@Override
	protected Comparable<Integer> getLabel() {
		return id;
	}

	@Override
	public final void writeData(DataOutput out) throws IOException {
		out.writeInt(id);
	}
	
	@Override
	public boolean equals(Object that) {
		if ( that == null) return false;
		return this.id == ((IntegerLabel) that).id;
		//return this.id.equals( ((IntegerLabel) that).id );
	}

	@Override
	public int compareTo(AbstractLabel that) {
		return this.id - ((IntegerLabel) that).id;
		//return this.id.compareTo(((IntegerLabel) o).id);
	}
	
	public final int hashCode() {
		return  new Integer(id).hashCode();	
	}

}
