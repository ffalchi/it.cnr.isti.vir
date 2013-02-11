package it.cnr.isti.vir.classification;

import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LabeledID<IDC, LABELC> implements ILabeled, IHasID {

	public final AbstractID id;
	public final AbstractLabel label;
	
	public LabeledID (IDC id, LABELC label ) {
		this.id = (AbstractID) id;
		this.label = (AbstractLabel) label;
	}

	@Override
	public AbstractLabel getLabel() {
		return label;
	}

	@Override
	public int compareTo(IHasID arg0) {
		return id.compareTo( arg0.getID() );
	}
	
	public boolean equals(IHasID arg0) {
		return id.equals( arg0.getID() );
	}

	@Override
	public AbstractID getID() {
		return id;
	}
	
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(label, out);
	}
	
	public LabeledID(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		label = LabelClasses.readData(in);
	}
	
	
}
