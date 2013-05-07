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
package it.cnr.isti.vir.features.mpeg7;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.LireColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.LireEdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.LireScalableColor;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IDInteger;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

public class LireObject implements IFeaturesCollector, IHasID {
	
	protected final static FeatureClassCollector fcc = new FeatureClassCollector(
			LireColorLayout.class,
			LireScalableColor.class,
			LireEdgeHistogram.class );	
	
	private final LireColorLayout cl;
	private final LireScalableColor sc;
	private final LireEdgeHistogram eh;
	
	private static final byte version = 0;
	
	private final AbstractID id;
	
	private String thmbUrl;
	
//	public SAPIRObject(FeaturesCollection f) {
//		super(f);
//	}
	
	public LireObject( IFeaturesCollector f) throws FeaturesCollectorException {
		cl = (LireColorLayout) f.getFeature(LireColorLayout.class);
		sc = (LireScalableColor) f.getFeature(LireScalableColor.class);
		eh = (LireEdgeHistogram) f.getFeature(LireEdgeHistogram.class);
//		System.out.println(cl.toString());
//		System.out.println(sc.toString());
//		System.out.println(eh.toString());
		if ( cl == null ||
			 sc == null ||
			 eh == null ) {
			throw new FeaturesCollectorException("Not all features were found.");
		}
		if ( f instanceof IHasID) {
			this.id = ((IHasID) f).getID();			
		} else {
			this.id = null;
		}
	}

	public LireObject(Integer id, IFeaturesCollector f) throws FeaturesCollectorException {
		cl = (LireColorLayout) f.getFeature(LireColorLayout.class);
		sc = (LireScalableColor) f.getFeature(LireScalableColor.class);
		eh = (LireEdgeHistogram) f.getFeature(LireEdgeHistogram.class);
		if ( cl == null ||
				 sc == null ||
				 eh == null) {
				throw new FeaturesCollectorException("Not all features were found.");
			}
		if ( id != null ) {
			this.id = new IDInteger(id);
		} else {
			this.id = null;
		}
	}

	public LireObject(DataInput in) throws IOException {
		byte version = in.readByte();
		id = IDClasses.readData(in);
//		id = new IDString(in);
		cl = new LireColorLayout(in);
		sc = new LireScalableColor(in);
		eh = new LireEdgeHistogram(in);
	}
	
	public LireObject(ByteBuffer in) throws IOException {
		byte version = in.get();
		id = IDClasses.readData(in);
//		id = new IDString(in);
		cl = new LireColorLayout(in);
		sc = new LireScalableColor(in);
		eh = new LireEdgeHistogram(in);
	}
	
	
//	public FeaturesClassCollection getFeaturesClassCollection() {
//		return fcc;
//	}

	public String toString() {
		return ""+ id;
	}

	@Override
	public IFeature getFeature(Class featureClass) {
		if ( featureClass.equals(LireColorLayout.class)) return cl;
		else if ( featureClass.equals(LireScalableColor.class)) return sc;
		else if ( featureClass.equals(LireEdgeHistogram.class)) return eh;
		return null;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		IDClasses.writeData(id, out);
		//id.writeData(out);
		cl.writeData(out);
		sc.writeData(out);		
		eh.writeData(out);
	}

	@Override
	public AbstractID getID() {
		return id;
	}

	@Override
	public int compareTo(IHasID that) {
		return id.compareTo(((LireObject) that).id);
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		LireObject that = (LireObject) obj;
		
		if ( !this.id.equals(that.id) ) return false;
		if ( !this.cl.equals(that.cl) ) return false;
		if ( !this.sc.equals(that.sc) ) return false;
		if ( !this.eh.equals(that.eh) ) return false;
		return true;
	}

	public String getThmbURL() {
		return thmbUrl;
	}
	
	public void setThmbURL(String thmbURL) {
		this.thmbUrl = thmbURL;
	}

	@Override
	public void add(IFeature f) throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not imlpemented.");		
	}

	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses)
			throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not imlpemented.");
	}

	@Override
	public Collection<IFeature> getFeatures() {
		// TODO Auto-generated method stub
		ArrayList list = new ArrayList(5);
		list.add(cl);
		list.add(eh);
		list.add(sc);
		return list;
	}

	@Override
	public boolean contains(Class c) {
		if ( c.equals(LireColorLayout.class)) return true;
		if ( c.equals(LireScalableColor.class)) return true;
		if ( c.equals(LireEdgeHistogram.class)) return true;
		return false;
	}

	public int dataHashCode() {
		int tempHash = 0;
		if ( sc != null ) tempHash = 31 * tempHash + sc.hashCode() ;
		if ( cl != null ) tempHash = 31 * tempHash + cl.hashCode() ;
		if ( eh != null ) tempHash = 31 * tempHash + eh.hashCode() ;
		return tempHash;
	}
	

}
