package it.cnr.isti.vir.similarity.knn;

import java.util.ArrayList;

public interface IQueriesOrdering {
	
	public ArrayList<Integer> getOrder(double[][] intDist);
	
}
