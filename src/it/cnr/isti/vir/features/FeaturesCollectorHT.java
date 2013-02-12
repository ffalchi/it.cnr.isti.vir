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

import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class FeaturesCollectorHT implements IFeaturesCollector {
	
	// USING HASH
	@SuppressWarnings("unchecked")
	protected Hashtable<Class, IFeature> set = new Hashtable<Class, IFeature>(10);
	
	public FeaturesCollectorHT() {
		super();
	};
	
	public FeaturesCollectorHT(IFeature givenF) {
		if ( IFeaturesCollector.class.isInstance(givenF) ) {
			IFeaturesCollector fc = (IFeaturesCollector) givenF;
			for ( Iterator<IFeature> it = fc.getFeatures().iterator(); it.hasNext(); ) {
				this.add(it.next());
			}
		} else {
			this.add(givenF);
		}
	}
	
	public FeaturesCollectorHT(FeaturesCollectorHT givenFColl ) {
		set = givenFColl.set;
		for(Iterator<IFeature> it = set.values().iterator(); it.hasNext(); ) {
			IFeature curr = it.next();
			if ( curr instanceof AbstractLFGroup ) {
				((AbstractLFGroup) curr).setLinkedFC(this);
			}
		}
	}
		
	static float version = (float) 1.0;
	
	public void discardAllBut(Class c) throws FeaturesCollectorException  {
		Hashtable<Class, IFeature> temp = new Hashtable<Class, IFeature>();
		if ( c == null) {
			set = temp;
			return;
		}
		
		IFeature f = getFeature(c);
		if ( f == null ) throw (new FeaturesCollectorException());
		temp.put(f.getClass(), f);
		
		set = temp;
	}
	
	public void discardAllBut(Class[] c) throws FeaturesCollectorException  {
		discardAllBut(new FeatureClassCollector());
	}
	
	public void discardAllBut(FeatureClassCollector fcc) throws FeaturesCollectorException  {
		Hashtable<Class, IFeature> temp = new Hashtable<Class, IFeature>();
		if ( fcc == null) {
			set = temp;
			return;
		}
		
		for (Iterator<Class> it = fcc.iterator(); it.hasNext(); ) {
			Class curr = it.next();
			IFeature f = getFeature(curr);
			if ( f == null ) throw (new FeaturesCollectorException());
			temp.put(f.getClass(), f);
		}
		set = temp;
	}
	
	public FeaturesCollectorHT(DataInput str) throws IOException {
 		float version = str.readFloat();
		int size = str.readInt();
		for(int i=0; i<size; i++) {
			IFeature f = FeatureClasses.readData(str, this);
			set.put( f.getClass(), f);
		}
	}

	public FeaturesCollectorHT(ByteBuffer src) throws IOException {
 		float version = src.getFloat();
		int size = src.getInt();
		for(int i=0; i<size; i++) {
			IFeature f = FeatureClasses.readData(src, this);
			set.put( f.getClass(), f);
		}
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeFloat(version);
		str.writeInt(set.size()); //number of features
		
		for ( Iterator<IFeature> it = set.values().iterator(); it.hasNext(); ) {
			IFeature curr = it.next();
			str.writeInt(FeatureClasses.getClassID(curr.getClass()));
			curr.writeData(str);
		}
	}
 	
	@SuppressWarnings("unchecked")
	public final IFeature getFeature( Class featureClass ) {
		return set.get( featureClass );
	}
	
	public int getNumberOfFeatures() {
		return set.size();
	}
	
	public final void add( IFeature givenF ) {
		set.put( givenF.getClass(), givenF );
	}	
	
	public final void add( IFeature[] givenF ) {
		for (int i=0; i<givenF.length; i++) {
			set.put( givenF[i].getClass(), givenF[i] );
		}
	}	
		
	public final void addAll( FeaturesCollectorHT givenFC ) {
		if (givenFC == null ) return;
		for ( Enumeration<IFeature> it = givenFC.set.elements(); it.hasMoreElements();  ) 
			add(it.nextElement());
	}		
	
	@SuppressWarnings("unchecked")
	public final boolean contains( Class... classes) {
		for(Class element : classes) {
			if ( !set.containsKey(element) ) return false;
//			if ( element.getClass().equals( FeaturesClassCollection.class ) ) {
//				//we have a sub collection
//				FeaturesCollection fc = (FeaturesClassCollection) element;
//				
//			}
		}
		return true;
	}	
	
	public final boolean containsSubCollection( Class subCollectionClass, Class... classes) {
		FeaturesSubCollecotr fcSubRegions = (FeaturesSubCollecotr) set.get(FeaturesSubRegions.class);
		
		if ( fcSubRegions == null ) return false;
		
		return fcSubRegions.contains(classes);

	}
	
	public String toString() {
		String tStr = "Features number: " + set.values().size() + "\n";
		for ( Iterator<IFeature> it = set.values().iterator(); it.hasNext(); ) {
			tStr += it.next();
		}
		return tStr;
	}

	public boolean equals(Object obj) {
		FeaturesCollectorHT givenFC = (FeaturesCollectorHT) obj;
		if ( this.set.size() != givenFC.set.size() ) return false;
		for ( Iterator<IFeature> it = set.values().iterator(); it.hasNext(); ) {
			IFeature f = it.next();
			IFeature givenF = givenFC.getFeature(f.getClass());
			if ( givenF == null || !givenF.equals(f)) return false;
		}
		return true;
	}
	
	public int hashCode() {
		int hashCode = 1;
		for ( Iterator<IFeature> it = set.values().iterator(); it.hasNext(); ) {
			hashCode = 31*hashCode + it.next().hashCode();
		}
		return hashCode;
	}
	
	public int compareTo(IFeaturesCollector obj) {
		return hashCode()-((FeaturesCollectorHT)obj).hashCode();
	}

	@Override
	public Collection<IFeature> getFeatures() {
		return set.values();
	}

	@Override
	public boolean contains(Class c) {
		return set.containsKey(c);
	}
	
//	static final int nFeatures = 6;
//	static final int DCid = 0;
//	static final int CLid = 1;
//	static final int CSid = 2;
//	static final int EHid = 3;
//	static final int HTid = 4;
//	static final int SCid = 5;
//		
//	final Feature[] f = new Feature[6];
//	
//	protected static final int getClassID( Class c ) {
//		if ( c ==  DominantColor.class ) return DCid;
//		else if ( c == ColorLayout.class ) return CLid;
//		else if ( c == ColorStructure.class ) return CSid;
//		else if ( c == EdgeHistogram.class ) return EHid;
//		else if ( c == HomogeneousTexture.class ) return HTid;
//		else if ( c == ScalableColor.class ) return SCid;
//		return -1;
//	}
//	
//	public final Feature get( Class c ) {
//		int id = getClassID( c );
//		if ( id == -1 ) return null;
//		else return f[id];
//	}
//	
//	public final void put( Feature givenF ) {
//		f[getClassID(givenF.getClass())] = givenF;
//	}
//	
//	public final boolean contains( Class... classes) {
//		for(Class element : classes) {
//			if ( get( element ) == null ) return false;
//		}
//		return true;
//	}
//	
//	public String toString() {
//		String tStr = "";
//		for ( int i=0; i<nFeatures; i++ ) {
//			tStr += f[i];
//		}
//		return tStr;
//	}
}
