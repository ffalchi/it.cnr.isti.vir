/**
 * 
 */
package it.cnr.isti.vir.id;

import it.cnr.isti.vir.util.FileNames;

/**
 * @author Fabrizio Falchi
 *
 */
public class FileName2IDLong implements IIDConverter<IDLong> {

	@Override
	public IDLong getID(String fileName) throws IDException {
		return new IDLong(
				FileNames.getFileNameWithoutExtension(fileName)
			);
	}
	

	public Long getID_Long(String fileName) throws IDException {
		return Long.parseLong(
				FileNames.getFileNameWithoutExtension(fileName)
			);
	}

	@Override
	public Class<? extends AbstractID> getIDClass() {
		return IDLong.class;
	}

}
