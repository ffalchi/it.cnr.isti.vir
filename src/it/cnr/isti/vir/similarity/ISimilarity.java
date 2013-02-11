package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;

import java.io.DataInputStream;


public interface ISimilarity<F> {

	public double distance( F f1, F f2);

	public double distance( F f1, F f2, double max);

	public double distance(IFeaturesCollector f1, IFeaturesCollector f2 );
	
	public double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max );
	
	public abstract long getDistCount();
	
	public abstract FeatureClassCollector getRequestedFeaturesClasses();
	
}
