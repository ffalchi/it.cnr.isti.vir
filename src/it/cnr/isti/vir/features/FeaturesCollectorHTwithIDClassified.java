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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClasses;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FeaturesCollectorHTwithIDClassified extends FeaturesCollectorHTwithID implements IFeaturesCollector_Labeled_HasID {

	private AbstractLabel c;
	
	static float version = (float) 1.0;
	
	public FeaturesCollectorHTwithIDClassified(ByteBuffer src) throws Exception {
		super(src);
		float version = src.getFloat();
		c = LabelClasses.readData(src);
	}
	
	public FeaturesCollectorHTwithIDClassified(DataInput in) throws Exception {
		super(in);
		float version = in.readFloat();
		c = LabelClasses.readData(in);
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		super.writeData(out);
		out.writeFloat(version);
		LabelClasses.writeData(c, out);
	}
	
	public FeaturesCollectorHTwithIDClassified(AbstractID id, FeaturesCollectorHT f) {
		super(id, f);
		c = null;
	}
		
	public FeaturesCollectorHTwithIDClassified(FeaturesCollectorHTwithID object, AbstractLabel c) {
		super(object);
		this.c = c;
	}
	
	public FeaturesCollectorHTwithIDClassified(IFeaturesCollector object, AbstractLabel c) {
		super(object);
		this.c = c;
	}

	public final AbstractLabel getLabel() {
		return c;
	}

	@Override
	public void setLabel(AbstractLabel label) {
		c = label;
	}
}
