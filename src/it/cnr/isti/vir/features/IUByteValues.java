package it.cnr.isti.vir.features;

/**
 * @author Fabrizio Falchi
 *
 *	Extends IByteValues
 *  Values returned by objects implementing this interface have been obtained
 *  subctrating 128 because they were actually unsigned.
 *  
 */
public interface IUByteValues extends IArrayValues {

	public byte[] getValues();
}
