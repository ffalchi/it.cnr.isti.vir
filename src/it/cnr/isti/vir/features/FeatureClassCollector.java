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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Fabrizio Falchi
 *
 * 
 *
 */
public class FeatureClassCollector implements Cloneable {

	private HashSet<Class> hSet = new HashSet<Class>(10);
	
	public final int size() {
		return hSet.size();
	}
	
	public final void writeData(DataOutput out) throws IOException {
		out.writeInt(hSet.size());
		for ( Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			out.writeInt( FeatureClasses.getClassID(it.next()) );
		}
	}
	
	public FeatureClassCollector(DataInput in) throws IOException {
		int size = in.readInt();
		for ( int i=0; i<size; i++) {
			hSet.add( FeatureClasses.getClass(in.readInt( )) );
		}
	}
	
	public FeatureClassCollector(FeatureClassCollector given) {
		for (Iterator<Class> it = given.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
	}
	
	public final FeatureClassCollector clone() {
		return new FeatureClassCollector(this);
	}
	
	public FeatureClassCollector( Class... featureClasses ) {
		add(featureClasses);
	}
	
	@SuppressWarnings("unchecked")
	public final FeatureClassCollector add( Class... featureClasses ) {
		for(Class element : featureClasses) hSet.add(element);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public final void addAll( FeatureClassCollector fcc) {
		for (Iterator<Class> it = fcc.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
	}
	
	@SuppressWarnings("unchecked")
	public final boolean contains( Class featureClass ) {
		return hSet.contains(featureClass);
	}
	
	@SuppressWarnings("unchecked")
	public final boolean areIn( IFeaturesCollector f ) throws FeaturesCollectorException {
		for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			if ( !f.contains(it.next() ) ) return false;
		}
		return true;
	}
	
	public final Collection<Class> missingIn( IFeaturesCollector f ) throws FeaturesCollectorException {
		ArrayList<Class> list = new ArrayList(10);
		for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			Class curr = it.next();
			if ( !f.contains( curr ) ) {
				list.add(curr);
			}
		}
		return list;
	} 
	
	@SuppressWarnings("unchecked")
	public final boolean areIn( FeaturesSubCollecotr f ) {
		for (int i=0; i<f.size(); i++ ) {
			for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
				if ( !f.get(i).contains(it.next()) ) return false;
			}
		}
		return true;
	}
	
	public Iterator<Class> iterator() {
		return hSet.iterator();
	}
	
	public static final FeatureClassCollector getIntersection(FeatureClassCollector fc1, FeatureClassCollector fc2 ) {
		FeatureClassCollector fcc = new FeatureClassCollector();
		for (Iterator<Class> it=fc1.hSet.iterator(); it.hasNext(); ) {
			Class currClass = it.next();
			if ( fc2.contains(currClass)) fcc.add(currClass);
		}		
		
		return fcc;
	}
	
	public final Class getRequestedClass() {
		if ( hSet.size() != 1 ) return null;
		return hSet.iterator().next();
	}
	
	public String toString() {
		String tStr = "";
		for (Iterator<Class> it=hSet.iterator(); it.hasNext(); ) {
			tStr+= " " +it.next();
		}
		return tStr;
	}
	
}
