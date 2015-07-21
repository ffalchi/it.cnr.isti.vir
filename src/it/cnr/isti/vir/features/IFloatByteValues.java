package it.cnr.isti.vir.features;


/**
 * @author Fabrizio Falchi
 *
 *	Extends IByteValues
 *  Values returned by objects implementing this interface have been obtained
 *  multiplying by 255 and subctrating 128 because.
 *  
 */
public interface IFloatByteValues extends IArrayValues {

	public byte[] getValues();
}
