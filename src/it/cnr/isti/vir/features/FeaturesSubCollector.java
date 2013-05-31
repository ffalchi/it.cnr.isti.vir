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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;

public class FeaturesSubCollector extends AbstractFeaturesCollector {

	private AbstractFeaturesCollector[] regionFC = null;
	
	static final float version = (float) 1.0;
	
	
	public FeaturesSubCollector(ByteBuffer in) throws Exception {
		float version = in.getFloat();
		int size = in.getInt();
		AbstractFeaturesCollector[] regionFC = new FeaturesCollectorArr[size];

		for(int i=0; i<size; i++) {
			regionFC[i] = new FeaturesCollectorArr(in);
		}
	}
	
	public FeaturesSubCollector(DataInput str) throws Exception {
		float version = str.readFloat();
		int size = str.readInt();
		AbstractFeaturesCollector[] regionFC = new FeaturesCollectorArr[size];

		for(int i=0; i<size; i++) {
			regionFC[i] = new FeaturesCollectorArr(str);
		}
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeFloat(version);
		str.writeInt(regionFC.length); //number of regions
		
		for ( int i=0; i<regionFC.length; i++ ) {
			regionFC[i].writeData(str);
		}
	}
	
	public boolean contains(Class[] classes) {
		for ( int i=0; i<regionFC.length; i++ ) {
			if ( !regionFC[i].contains(classes) ) return false;
		}
		return true;
	}

	public FeaturesSubCollector(AbstractFeaturesCollector[] regionFC) {
		this.regionFC = regionFC;
	}
	

	public FeaturesSubCollector(Collection<AbstractFeaturesCollector> collection ) {
		regionFC = new AbstractFeaturesCollector[collection.size()];
		int index=0;
		for (Iterator<AbstractFeaturesCollector> it=collection.iterator(); it.hasNext(); ) {
			regionFC[index]=it.next();
			index++;
		}
	}
	
	public int size() {
		return regionFC.length;
	}
	
	public AbstractFeaturesCollector get(int index ) {
		return regionFC[index];
	}
	
	public String toString() {
		String tStr = "";
		for ( int i=0; i<regionFC.length; i++ ) {
			tStr += "-----SubCollection "+ i + " -----\n";
			tStr += regionFC[i];
		}	
		return tStr;
	}

	
	public int hashCode() {
		int hashCode = 1;
		for ( int i=0; i<regionFC.length; i++ ) {
			hashCode = 31*hashCode + regionFC[i].hashCode();
		}

		return hashCode;
	}
	
	public int compareTo(AbstractFeaturesCollector obj) {
		return hashCode()-((AbstractFeaturesCollector)obj).hashCode();
	}
	
	@Override
	public <T extends AbstractFeature> T getFeature( Class<T> featureClass ) {
		if ( featureClass.equals(this.getClass())) return (T) this;
		return null;
	}

	@Override
	public void add(AbstractFeature f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses)
			throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
		
	}

	@Override
	public Collection<AbstractFeature> getFeatures() {
		return null;
	}

	@Override
	public boolean contains(Class<AbstractFeature> c) {
		if ( regionFC == null || regionFC.length == 0) return false;
		for ( AbstractFeaturesCollector fc : regionFC) {
			if ( !fc.contains(c)) return false;
		}		
		return true;
	}
	
}
