package it.cnr.isti.vir.util;

import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.util.Comparator;


public class LocalFeaturesMatchesSizeComparator implements Comparator<LocalFeaturesMatches> {

	public final int compare(LocalFeaturesMatches o1, LocalFeaturesMatches o2) {
		return o2.size() - o1.size();
	}
	
}
