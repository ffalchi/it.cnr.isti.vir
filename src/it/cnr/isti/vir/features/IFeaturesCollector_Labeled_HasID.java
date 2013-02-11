package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.id.IHasID;

public interface IFeaturesCollector_Labeled_HasID extends IFeaturesCollector, ILabeled, IHasID {
	
	public void setLabel(AbstractLabel label);
}
