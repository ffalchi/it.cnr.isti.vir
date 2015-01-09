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

import java.util.Collection;

public abstract class AbstractFeaturesCollector extends AbstractFeature {

	public abstract <T extends AbstractFeature> T getFeature(Class<T> featureClass);
	
	//public IFeature getFeature( Class featureClass );

//	public FeatureInterface[] getAllFeatures();
//	
//	public void addAll( FeaturesCollectionInterface givenFC );
	
	public abstract void add( AbstractFeature f ) throws FeaturesCollectorException ;

	public abstract void discardAllBut(FeatureClassCollector featuresClasses) throws FeaturesCollectorException;
	
	public abstract void discard(Class<? extends AbstractFeature> c)  throws FeaturesCollectorException;
	
	public abstract Collection<AbstractFeature> getFeatures();
	
	public abstract boolean contains(Class<AbstractFeature> c);
	
	public boolean contains(Class<AbstractFeature>[] cArr) {
		for ( Class<AbstractFeature> c : cArr) {
			if ( !contains(c)) return false;
		}
		return true;
	}
	
	/**
	 * Creates a new empty Features Collector of the same type
	 * 
	 * @return
	 */
	public AbstractFeaturesCollector getEmptyWithSameInfo( ) {
		return createWithSameInfo(null);
	};

	/**
	 * Creates a new Features Collector of the same type adding the given feature
	 * 
	 * @param f
	 * @return
	 */
	public abstract AbstractFeaturesCollector createWithSameInfo(AbstractFeature f);
}
