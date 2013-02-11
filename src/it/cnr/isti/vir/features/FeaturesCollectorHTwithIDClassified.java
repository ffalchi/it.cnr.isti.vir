package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClasses;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FeaturesCollectorHTwithIDClassified extends FeaturesCollectorHTwithID implements IFeaturesCollector_Labeled_HasID {

	private AbstractLabel c;
	
	static float version = (float) 1.0;
	
	public FeaturesCollectorHTwithIDClassified(ByteBuffer src) throws Exception {
		super(src);
		float version = src.getFloat();
		c = LabelClasses.readData(src);
	}
	
	public FeaturesCollectorHTwithIDClassified(DataInput in) throws Exception {
		super(in);
		float version = in.readFloat();
		c = LabelClasses.readData(in);
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		super.writeData(out);
		out.writeFloat(version);
		LabelClasses.writeData(c, out);
	}
	
	public FeaturesCollectorHTwithIDClassified(AbstractID id, FeaturesCollectorHT f) {
		super(id, f);
		c = null;
	}
		
	public FeaturesCollectorHTwithIDClassified(FeaturesCollectorHTwithID object, AbstractLabel c) {
		super(object);
		this.c = c;
	}
	
	public FeaturesCollectorHTwithIDClassified(IFeaturesCollector object, AbstractLabel c) {
		super(object);
		this.c = c;
	}

	public final AbstractLabel getLabel() {
		return c;
	}

	@Override
	public void setLabel(AbstractLabel label) {
		c = label;
	}
}
