package it.cnr.isti.vir.id;

public interface IIDConverter<IDClass extends AbstractID> {

	
	public IDClass getID(String fileName) throws IDException;
	
	public Class<? extends AbstractID> getIDClass();
}
