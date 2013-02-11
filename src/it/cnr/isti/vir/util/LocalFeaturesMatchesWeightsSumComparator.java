package it.cnr.isti.vir.util;

import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;


public class LocalFeaturesMatchesWeightsSumComparator implements Comparator<LocalFeaturesMatches> {

	public final int compare(LocalFeaturesMatches o1, LocalFeaturesMatches o2) {

		double w1 = o1.getWeightSum();
		double w2 = o2.getWeightSum();
		if ( w2 > w1 ) return +1;
		if ( w2 < w1 ) return -1;
		return 0;
	}
	
}
