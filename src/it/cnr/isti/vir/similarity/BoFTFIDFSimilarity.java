/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;

import java.util.Properties;

/**
 *
 * @author Fabrizio
 */
public class BoFTFIDFSimilarity implements ISimilarity<BoFLFGroup>, IRequiresIDF {

    private float[] idf = null;
    private static final long dCount = 0;
//    private boolean useIDF = true;
//    
//    public boolean isUseIDF() {
//		return useIDF;
//	}
//
//	public void setUseIDF(boolean useIDF) {
//		this.useIDF = useIDF;
//	}

	public static final FeatureClassCollector reqFeatures = new FeatureClassCollector(BoFLFGroup.class);
    
    public float[] getIdf() {
		return idf;
	}

	public void setIdf(float[] idf) {
		this.idf = idf;
	}

	public BoFTFIDFSimilarity( Properties properties) throws SimilarityOptionException {
		this();
		
//		String value = properties.getProperty("useIDF");
//		if ( value != null ) {
//			useIDF = Boolean.parseBoolean(value);
//			System.out.println("UseIDF was set to " + useIDF);
//		}
	}
	
	public BoFTFIDFSimilarity() {
    	this((float[]) null);
    }
    
    public BoFTFIDFSimilarity(float[] idf) {
        super();
        this.idf = idf;
    }

    public long getDistCount() {
        return dCount;
    }

    public FeatureClassCollector getRequestedFeaturesClasses() {
        return reqFeatures;
    }

    public double distance(BoFLFGroup f1, BoFLFGroup f2) {
    	
    	double sim = 0.0;
    	
//		if ( f1.getID().equals(f2.getID())) {
//			System.out.println("FOUND!");
//		}
    	
    	sim = BoFLFGroup.getSimilarity_cosine(f1, f2, idf, false);

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

