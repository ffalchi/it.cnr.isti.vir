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
package it.cnr.isti.vir.features.mpeg7;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;
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

public class SAPIRObject implements IFeaturesCollector, IHasID {
	
	protected final static FeatureClassCollector fcc = new FeatureClassCollector(
			ColorLayout.class,
			ColorStructure.class,
			ScalableColor.class,
			EdgeHistogram.class,
			HomogeneousTexture.class );	
	
	private final ColorLayout cl;
	private final ColorStructure cs;
	private final ScalableColor sc;
	private final EdgeHistogram eh;
	private final HomogeneousTexture ht;
	
	private static final byte version = 0;
	
	private final AbstractID id;
	
	private String thmbUrl;
	
//	public SAPIRObject(FeaturesCollection f) {
//		super(f);
//	}
	
	public SAPIRObject( IFeaturesCollector f) throws FeaturesCollectorException {
		cl = (ColorLayout) f.getFeature(ColorLayout.class);
		cs = (ColorStructure) f.getFeature(ColorStructure.class);
		sc = (ScalableColor) f.getFeature(ScalableColor.class);
		eh = (EdgeHistogram) f.getFeature(EdgeHistogram.class);
		ht = (HomogeneousTexture) f.getFeature(HomogeneousTexture.class);
		if ( cl == null ||
			 cs == null ||
			 sc == null ||
			 eh == null ||
			 ht == null ) {
			throw new FeaturesCollectorException("Not all features were found.");
		}
		if ( f instanceof IHasID) {
			this.id = ((IHasID) f).getID();			
		} else {
			this.id = null;
		}
	}

	public SAPIRObject(Integer id, IFeaturesCollector f) throws FeaturesCollectorException {
		cl = (ColorLayout) f.getFeature(ColorLayout.class);
		cs = (ColorStructure) f.getFeature(ColorStructure.class);
		sc = (ScalableColor) f.getFeature(ScalableColor.class);
		eh = (EdgeHistogram) f.getFeature(EdgeHistogram.class);
		ht = (HomogeneousTexture) f.getFeature(HomogeneousTexture.class);
		if ( cl == null ||
				 cs == null ||
				 sc == null ||
				 eh == null ||
				 ht == null ) {
				throw new FeaturesCollectorException("Not all features were found.");
			}
		if ( id != null ) {
			this.id = new IDInteger(id);
		} else {
			this.id = null;
		}
	}

	public SAPIRObject(DataInput in) throws IOException {
		byte version = in.readByte();
		id = IDClasses.readData(in);
//		id = new IDString(in);
		cl = new ColorLayout(in);
		cs = new ColorStructure(in);
		sc = new ScalableColor(in);
		eh = new EdgeHistogram(in);
		ht = new HomogeneousTexture(in);
	}
	
	public SAPIRObject(ByteBuffer in) throws IOException {
		byte version = in.get();
		id = IDClasses.readData(in);
//		id = new IDString(in);
		cl = new ColorLayout(in);
		cs = new ColorStructure(in);
		sc = new ScalableColor(in);
		eh = new EdgeHistogram(in);
		ht = new HomogeneousTexture(in);
	}
	
	
//	public FeaturesClassCollection getFeaturesClassCollection() {
//		return fcc;
//	}

	public String toString() {
		return ""+ id;
	}

	@Override
	public IFeature getFeature(Class featureClass) {
		if ( featureClass.equals(ColorLayout.class)) return cl;
		else if ( featureClass.equals(ColorStructure.class)) return cs;
		else if ( featureClass.equals(ScalableColor.class)) return sc;
		else if ( featureClass.equals(EdgeHistogram.class)) return eh;
		else if ( featureClass.equals(HomogeneousTexture.class)) return ht;
		return null;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		IDClasses.writeData(id, out);
		//id.writeData(out);
		cl.writeData(out);
		cs.writeData(out);
		sc.writeData(out);		
		eh.writeData(out);
		ht.writeData(out);
	}

	@Override
	public AbstractID getID() {
		return id;
	}

	@Override
	public int compareTo(IHasID that) {
		return id.compareTo(((SAPIRObject) that).id);
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		SAPIRObject that = (SAPIRObject) obj;
		
		if ( !this.id.equals(that.id) ) return false;
		if ( !this.cl.equals(that.cl) ) return false;
		if ( !this.cs.equals(that.cs) ) return false;
		if ( !this.sc.equals(that.sc) ) return false;
		if ( !this.eh.equals(that.eh) ) return false;
		if ( !this.ht.equals(that.ht) ) return false;
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
		list.add(cs);
		list.add(eh);
		list.add(ht);
		list.add(sc);
		return list;
	}

	@Override
	public boolean contains(Class c) {
		if ( c.equals(ColorLayout.class)) return true;
		if ( c.equals(ColorStructure.class)) return true;
		if ( c.equals(ScalableColor.class)) return true;
		if ( c.equals(EdgeHistogram.class)) return true;
		if ( c.equals(HomogeneousTexture.class)) return true;
		return false;
	}

	public int dataHashCode() {
		int tempHash = 0;
		if ( sc != null ) tempHash = 31 * tempHash + sc.hashCode() ;
		if ( cl != null ) tempHash = 31 * tempHash + cl.hashCode() ;
		if ( cs != null ) tempHash = 31 * tempHash + cs.hashCode() ;
		if ( eh != null ) tempHash = 31 * tempHash + eh.hashCode() ;
		if ( ht != null ) tempHash = 31 * tempHash + ht.hashCode() ;	
		return tempHash;
	}
	

}
