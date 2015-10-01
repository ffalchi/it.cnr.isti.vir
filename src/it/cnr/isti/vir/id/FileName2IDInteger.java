/**
 * 
 */
package it.cnr.isti.vir.id;

import it.cnr.isti.vir.util.FileNames;

/**
 * @author Fabrizio Falchi
 *
 */
public class FileName2IDInteger implements IIDConverter<IDInteger> {

	@Override
	public IDInteger getID(String fileName) throws IDException {
		return new IDInteger(
				FileNames.getFileNameWithoutExtension(fileName)
			);
	}
	

	public int getID_Int(String fileName) throws IDException {
		return Integer.parseInt(
				FileNames.getFileNameWithoutExtension(fileName)
			);
	}

	@Override
	public Class<? extends AbstractID> getIDClass() {
		return IDInteger.class;
	}

}
