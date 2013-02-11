package it.cnr.isti.vir.id;

import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Fabrizio Falchi
 *
 *
 * The AbstractID class declares methods that are fundamental for all the ID classes.
 * AbstractID also implements IHasID interface. Its ID is actually itself.
 */
public abstract class AbstractID implements IHasID {

	public abstract void writeData(DataOutput out) throws IOException;
	
	public final int compareTo(IHasID obj) {
		return this.compareTo( obj.getID());
	}
	
	public abstract int compareTo(AbstractID obj);

	public AbstractID getID() {
		return this;
	}
	
}
