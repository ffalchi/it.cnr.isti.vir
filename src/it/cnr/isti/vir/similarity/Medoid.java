package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.AbstractFeature;

import java.util.Collection;
import java.util.Iterator;

public class Medoid {

	
	public static AbstractFeature getMedoid(Collection<AbstractFeature> cluster, ISimilarity sim ) {
		double minSum = Double.MAX_VALUE;
		AbstractFeature best = null;
		for (Iterator<AbstractFeature> it1 = cluster.iterator(); it1.hasNext(); ) {
			AbstractFeature candidate = it1.next();
			double candidateSum = 0;
			for (Iterator<AbstractFeature> it2 = cluster.iterator(); it2.hasNext(); ) {
				AbstractFeature curr = it2.next();
				candidateSum += sim.distance(candidate, curr);
				if ( candidateSum > minSum ) break;
			}
			if ( candidateSum < minSum ) {
				best = candidate;
				minSum = candidateSum;
			}
		}
		return best;
		
	}
}
