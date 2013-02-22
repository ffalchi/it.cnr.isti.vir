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
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class FeaturesCollectorArr implements IFeaturesCollector_Labeled_HasID {

	public AbstractID id;
	
	public AbstractLabel cl;
	
	private IFeature[] feature;

	public FeaturesCollectorArr(IFeature f, AbstractID id, AbstractLabel cl) {
		this.id=id;
		this.cl=cl;
		int size = 1;
		feature = new IFeature[1];
		feature[0]=f;
		if ( feature[0] instanceof ALocalFeaturesGroup ) {
			((ALocalFeaturesGroup) feature[0]).setLinkedFC(this);
		}
	}
	
	public FeaturesCollectorArr(Collection<IFeature> coll, AbstractID id, AbstractLabel cl) {
		this.id=id;
		this.cl=cl;
		int size = coll.size();
		feature = new IFeature[size];
		int i = 0;
		for ( Iterator<IFeature> it=coll.iterator(); it.hasNext(); i++) {
			IFeature curr = it.next();
			feature[i]=curr;
			if ( feature[i] instanceof ALocalFeaturesGroup ) {
				((ALocalFeaturesGroup) feature[i]).setLinkedFC(this);
			}
		}
		
	}
	
	public FeaturesCollectorArr(ByteBuffer src) throws IOException  {
		id = IDClasses.readData(src);
		cl = LabelClasses.readData(src);
		int size = src.get();
		feature = new IFeature[size];
		for ( int i=0; i<feature.length; i++) {
			feature[i] = FeatureClasses.readData(src, this);
//			if ( feature[i] instanceof LocalFeaturesGroup ) {
//				((LocalFeaturesGroup) feature[i]).setLinkedFC(this);
////			}
//			if ( feature[i] == null) {
//				System.err.println("Null Feature read.");
//			}
		}
	}
	
	public FeaturesCollectorArr(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		cl = LabelClasses.readData(in);
		int size = in.readByte();
		feature = new IFeature[size];
		for ( int i=0; i<feature.length; i++) {
			feature[i] = FeatureClasses.readData(in, this);
//			if ( feature[i] instanceof LocalFeaturesGroup ) {
//				((LocalFeaturesGroup) feature[i]).setLinkedFC(this);
////			}
//			if ( feature[i] == null) {
//				System.err.println("Null Feature read.");
//			}
		}
	}
		
	@Override
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(cl, out);
		out.writeByte( (byte) feature.length);
		for ( int i=0; i<feature.length; i++) {
			FeatureClasses.writeData(out, feature[i]);
		}
	}

	@Override
	public int compareTo(IHasID obj) { 
		if ( obj.getClass().equals(FeaturesCollectorArr.class) && id.equals((obj.getID())))
				return hashCode()-((FeaturesCollectorArr) obj).hashCode();
		
		return id.compareTo( obj.getID());
	}

	@Override
	public final AbstractLabel getLabel() {
		return cl;
	}

	@Override
	public final AbstractID getID() {
		return id;
	}

	@Override
	public IFeature getFeature(Class featureClass) {
		for ( int i=0; i<feature.length; i++) {
			if ( feature[i] == null ) {
				System.err.println("Feature null!!!!");
				continue;
			}
			if ( featureClass.equals(feature[i].getClass())) {
				return feature[i];
			}
		}
		return null;
	}

	@Override
	public void add(IFeature f) {
			
		if ( feature == null ) {
			feature = new IFeature[1];
			feature[0] = f;
		} else {
			
			int size = feature.length+1;
			IFeature[] temp = new IFeature[size];
			for (int i=0; i<feature.length; i++) {
				temp[i]=feature[i];
			}
			temp[size-1]=f;
			
			feature = temp;
		}
		
		if ( f instanceof ALocalFeaturesGroup ) {
			((ALocalFeaturesGroup) f).setLinkedFC(this);
		}
	}

	@Override
	public void discardAllBut( 	FeatureClassCollector featuresClasses )
								throws FeaturesCollectorException {
		if ( featuresClasses == null ) {
			feature = null;
			return;
		}
		int size = featuresClasses.size();
		IFeature[] temp = new IFeature[size];
		int count = 0;
		for ( int i=0; i<feature.length; i++) {
			if ( featuresClasses.contains(feature[i].getClass())) {
				temp[count++]=feature[i];
			}
		}
		if ( count != size ) {
			throw new FeaturesCollectorException("Not all the requested features were found");
		}
		feature = temp;
	}
	
	public boolean substitute( IFeature f ) {
		Class c = f.getClass();
		for ( int i=0; i<feature.length; i++) {
			if ( feature[i].getClass().equals(c)) {
				feature[i] = f;
				return true;
			}
		}
		return false;
	}
	
	public void discard( Class c ) {
		int cIndex = -1;
		for ( int i=0; i<feature.length && cIndex <0; i++) {
			if ( feature[i].getClass().equals(c)) {
				cIndex=i;
			}
		}
		if ( cIndex <0 ) return;
		IFeature[] newArr = new IFeature[feature.length-1];
		int j=0;
		for ( int i=0; i<feature.length; i++) {
			if ( i == cIndex) continue;
			newArr[j++]=feature[i];
		}
		
		feature = newArr;
	}

	@Override
	public Collection<IFeature> getFeatures() {
		return Arrays.asList(feature);
	}

	@Override
	public boolean contains(Class c) {
		for ( int i=0; i<feature.length; i++) {
			if ( feature[i].getClass().equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(Object obj) {
		if ( obj == null ) return false;
		if ( this == obj ) return true;
		FeaturesCollectorArr that = (FeaturesCollectorArr) obj;
		if ( this.id != that.id && this.id != null && !this.id.equals(that.id)) return false;
		if ( this.cl != that.cl && this.cl != null && !this.cl.equals(that.cl)) return false;
		if ( this.feature.length != that.feature.length )return false;
		for ( int i=0; i<feature.length; i++) {
			if ( !feature[i].equals(that.feature[i])) return false;
		}
		return true;
	}
	
	public int hashCode() {
		int hashCode = 1;
		for ( int i=0; i<feature.length; i++) {
			hashCode = 31*hashCode + feature[i].hashCode();
		}
		return hashCode;
	}

	public final void setID(AbstractID newInstance) {
		this.id = newInstance;		
	}

	@Override
	public void setLabel(AbstractLabel label) {
		cl = label;
	}
	
	public String toString() {
		String tStr = "";
		for ( int i=0; i<feature.length; i++) {
			tStr += feature[i].getClass() + "\n";
			tStr += feature[i] + "\n"; 
		}
		return tStr;
	}


	
	
}
