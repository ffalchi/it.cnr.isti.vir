package it.cnr.isti.vir.classification.classifier;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.similarity.knn.KNNExecuter;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.similarity.results.ObjectWithDistance;
import it.cnr.isti.vir.similarity.results.SimilarityLFGroup_ResultsArr;

import java.util.Iterator;


public class SimRatioKNNClassifier extends AbstractKNNClassifier  {
	
	public SimRatioKNNClassifier() {
		super();
	}
	
	public SimRatioKNNClassifier(KNNExecuter knnExec) {
		super(knnExec, 1);
	}

	

	@Override
	public PredictedLabel singleLabelClassify(ISimilarityResults knn ) {
		if ( SimilarityLFGroup_ResultsArr.class.isInstance(knn)) {
			return ((SimilarityLFGroup_ResultsArr) knn).getLoweKNNPrClassLabel();
		}
		if ( knn == null || knn.size() == 0 ) return new PredictedLabel(null, 0.0);
		
		Iterator<ObjectWithDistance> it = knn.iterator();
		ObjectWithDistance best = it.next();
//		if ( best == null ) return new PredictedLabel(null, 0.0);
		AbstractLabel bestLabel = ((ILabeled) best.getObj()).getLabel();
		if ( knn.size() == 1 ) return new PredictedLabel(bestLabel, 0.0);
		for ( int count=0; it.hasNext();) {
			ObjectWithDistance curr = it.next();
			
//			if ( curr == null ) break;
			
//			ILabeled second = (ILabeled) curr.getObj();		
//			if ( second == null ) {
//				// a second label does not exist
//				return new PredictedClassLabel(bestLabel, 1.0);
//			}
			AbstractLabel currLabel = null;
			ILabeled obj = ((ILabeled) curr.getObj());
			if ( obj != null )
				currLabel = obj.getLabel();	
						
			if ( !bestLabel.equals(currLabel) ) {
				// second label found
				return new PredictedLabel(bestLabel, 1.0-(best.getDist() / curr.getDist()));
			}
		}
		return new PredictedLabel(bestLabel, 1.0);		
	}
	
	@Override
	public PredictedLabel[] singleLabelClassify_MultiK(ISimilarityResults knn, int[] ks) {
		PredictedLabel[] cls = new PredictedLabel[ks.length];

		for (int i=0; i<ks.length; i++ ) {
			if ( ks[i] > knn.size() ) {
				System.err.println("MultiKSingleLabelClassify error. Ks[i]>knn.size: " + ks[i] + ">" +  knn.size());
				return null;
			}
			if ( ks[i] <= 0 ) {
				System.err.println("MultiKSingleLabelClassify error. Ks[i]= " + ks[i]);
				return null;
			}
			Iterator<ObjectWithDistance> it = knn.iterator();
			ObjectWithDistance best = it.next();
			AbstractLabel bestLabel = ((ILabeled) best.getObj()).getLabel();
			for ( int count=0; it.hasNext() && count < ks[i]; count++) {
				ObjectWithDistance curr = it.next();
				AbstractLabel currLabel = ((ILabeled) curr.getObj()).getLabel();
				if ( !bestLabel.equals(currLabel) ) {
					cls[i] =  new PredictedLabel(bestLabel, 1.0-(best.getDist() / curr.getDist()));
				}
				
			}
		}	
		
		return cls;
	}

}
