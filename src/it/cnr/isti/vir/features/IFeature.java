package it.cnr.isti.vir.features;

import java.io.DataOutput;
import java.io.IOException;

public interface IFeature {

	public void writeData(DataOutput out) throws IOException;

}
