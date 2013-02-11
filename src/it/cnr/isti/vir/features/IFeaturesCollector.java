package it.cnr.isti.vir.features;

import java.util.Collection;

public interface IFeaturesCollector extends IFeature {

	<T extends IFeature> T getFeature(Class<T> featureClass);
	
	//public IFeature getFeature( Class featureClass );

//	public FeatureInterface[] getAllFeatures();
//	
//	public void addAll( FeaturesCollectionInterface givenFC );
	
	public void add( IFeature f ) throws FeaturesCollectorException ;

	public void discardAllBut(FeatureClassCollector featuresClasses) throws FeaturesCollectorException;

	public Collection<IFeature> getFeatures();
	
	public boolean contains(Class<IFeature> c) throws FeaturesCollectorException;
	
}
