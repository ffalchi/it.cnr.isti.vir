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

import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LabeledID<IDC, LABELC> implements ILabeled, IHasID {

	public final AbstractID id;
	public final AbstractLabel label;
	
	public LabeledID (IDC id, LABELC label ) {
		this.id = (AbstractID) id;
		this.label = (AbstractLabel) label;
	}

	@Override
	public AbstractLabel getLabel() {
		return label;
	}

	@Override
	public int compareTo(IHasID arg0) {
		return id.compareTo( arg0.getID() );
	}
	
	public boolean equals(IHasID arg0) {
		return id.equals( arg0.getID() );
	}

	@Override
	public AbstractID getID() {
		return id;
	}
	
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(label, out);
	}
	
	public LabeledID(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		label = LabelClasses.readData(in);
	}
	
	
}
