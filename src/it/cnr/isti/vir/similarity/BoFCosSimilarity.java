/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;

/**
 *
 * @author Fabrizio
 */
public class BoFCosSimilarity implements ISimilarity<BoFLFGroup> {

    private static final long dCount = 0;
    
    public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BoFLFGroup.class);
    
    public BoFCosSimilarity( ) {
        super();
    }

    public long getDistCount() {
        return dCount;
    }

    public FeatureClassCollector getRequestedFeaturesClasses() {
        return reqFeatures;
    }

    public double distance(BoFLFGroup f1, BoFLFGroup f2) {
   	
    	double sim = BoFLFGroup.getSimilarity_cosine(f1, f2 );
    	if ( sim > 1.0 ) {
    		//System.err.println("BoFLFGroup similarity > 1.0 : " + sim);
    		sim = 1.0;
    	}
    	return 1.0 - sim;
    }

    public double distance(BoFLFGroup f1, BoFLFGroup f2, double max) {
        return distance(f1, f2);
    }

    public double distance(IFeaturesCollector f1, IFeaturesCollector f2) {
        return distance((BoFLFGroup) f1.getFeature(BoFLFGroup.class), (BoFLFGroup) f2.getFeature(BoFLFGroup.class));
    }

    public double distance(IFeaturesCollector f1, IFeaturesCollector f2, double max) {
        return distance((BoFLFGroup) f1.getFeature(BoFLFGroup.class), (BoFLFGroup) f2.getFeature(BoFLFGroup.class));
    }

}

