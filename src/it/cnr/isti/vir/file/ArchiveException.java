package it.cnr.isti.vir.file;

public class ArchiveException extends Exception {

	public ArchiveException(Exception e) {
		super(e);
	}

	public ArchiveException(String string) {
		super(string);
	}
	
}
