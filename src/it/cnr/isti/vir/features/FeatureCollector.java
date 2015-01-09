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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClasses;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;


public class FeatureCollector extends AbstractFeaturesCollector_Labeled_HasID {

	protected final AbstractFeature f;
	protected AbstractLabel l;
	protected final AbstractID id;
	
	public FeatureCollector(AbstractFeature f ) {
		this(f, null);
	}
	
	public FeatureCollector(AbstractFeature f, AbstractID id) {
		this(f, id, null);
	}
	
	public FeatureCollector(AbstractFeature f, AbstractID id, AbstractLabel l) {
		this.f = f;
		this.l = l;
		this.id = id;
	}
	
	
	
	@Override
	public boolean equals(Object other) {
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof FeatureCollector)) return false;
	    FeatureCollector otherFC = (FeatureCollector) other;
	    if ( id == null ) {
	    	if ( otherFC.id == null ) return true;
	    	else return false;
	    }
	    if ( !id.equals(otherFC.id)) return false;
		return f.equals(otherFC.f);
	}

	@Override
	public int compareTo(IHasID arg0) {
		if ( this == arg0 ) return 0;
		if ( this.id != arg0.getID() ) {
			if ( this.id == null ) return -1;
			if ( arg0.getID() == null ) return 1;
			int tComp = this.id.compareTo( arg0.getID());	
			if ( tComp != 0 ) return tComp;
		}
		// TO DO!
		return 0;
	}
	
	
	@Override
	public <T extends AbstractFeature> T getFeature(Class<T> featureClass) {
		return (T) f;
	}

	@Override
	public void add(AbstractFeature f) throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
	}

	@Override
	public void discard(Class<? extends AbstractFeature> c)
			throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
	}
	
	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses) throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
	}

	public FeatureCollector(ByteBuffer src) throws IOException  {
		id = IDClasses.readData(src);
		l = LabelClasses.readData(src);
		f = FeatureClasses.readData(src);
		f.setLinkedFC(this);
	}
	
	public FeatureCollector(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		l = LabelClasses.readData(in);
		f = FeatureClasses.readData(in);
		f.setLinkedFC(this);
	}
	
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(l, out);
		FeatureClasses.writeData(out, f);
	}

	@Override
	public AbstractLabel getLabel() {
		return l;
	}

	@Override
	public AbstractID getID() {
		return id;
	}
	


	
	public int hashCode() {
		
		int hashCode = f.hashCode();
		
		return hashCode;
	}

	@Override
	public Collection<AbstractFeature> getFeatures() {
		ArrayList t =  new ArrayList(1);
		t.add(f);
		return t;
	}

	@Override
	public boolean contains(Class<AbstractFeature> c) {
		if ( f.getClass().equals(c) ) return true;
		return false;
	}

	@Override
	public void setLabel(AbstractLabel label) {
		l = label;
	}

	@Override
	public AbstractFeaturesCollector createWithSameInfo( AbstractFeature f ) {
		return new FeatureCollector(f, id, l);
	}
	
	

}
