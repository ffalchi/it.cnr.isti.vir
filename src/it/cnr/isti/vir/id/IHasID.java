package it.cnr.isti.vir.id;

/**
 * @author Fabrizio Falchi
 *
 */
public interface IHasID extends Comparable<IHasID> {

	/**
	 * @return the ID member that is used to identify the object.
	 */
	public AbstractID getID();
}
