package it.cnr.isti.vir.features.localfeatures.evaluation;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClassException;

import java.io.DataOutput;
import java.io.IOException;

public interface ILFEval {

	public double getValue();
	
	public double getValue(AbstractLabel cLabel );
	
	public void writeData(DataOutput out) throws IOException ;
}
