package it.cnr.isti.vir.features.mpeg7.vd;

import it.cnr.isti.vir.features.IFeaturesCollector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class VisualDescriptor implements java.io.Serializable, IFeaturesCollector {
	
	private static final long serialVersionUID = 1271629156053143017L;
	
	public abstract double getMaxDistance();
	
	public VisualDescriptor( XMLStreamReader xmlr  ) {};

	public VisualDescriptor() {};
	
	protected abstract void parse( XMLStreamReader xmlr ) throws XMLStreamException, MPEG7VDFormatException;
	
	public abstract double distance( VisualDescriptor givenD );
	
//	protected ID docID = null;
//
//	public final ID getDocID() {
//		return docID;
//	}
//
//	public final void setDocID(ID docID) {
//		this.docID = docID;
//	}
	
	public abstract boolean equals( VisualDescriptor vd );
	
	public abstract String toString();
	
	
	public abstract int dataHashCode();
//	protected byte parseByteInTag( XMLStreamReader xmlr, String tagName ) throws XMLStreamException, MPEG7VDFormatException  {
//		byte result = 0;
//		boolean found = false;
//		for (int event = xmlr.next();  
//	    	event != XMLStreamConstants.END_DOCUMENT;
//	    	event = xmlr.next()) {
//	    	switch (event) {
//	            case XMLStreamConstants.END_ELEMENT:
//	            	if (xmlr.getLocalName().equals( tagName) ) { 
//	            		if ( !found ) throw new MPEG7VDFormatException();
//	            		return result;
//	            	}
//	            	break;
//	            case XMLStreamConstants.CHARACTERS:
//            		//System.out.print("Coef " + xmlr.getText() );
//            		
//            		result = (byte) Integer.parseInt( xmlr.getText() );
//            		
//            		found = true;
//            		
//	            	break;
//	    	} // end switch   		    	
//	    } // end while	
//		
//		throw new MPEG7VDFormatException();
//	}
//	
//	protected byte[] parseBytesInTag( XMLStreamReader xmlr, String tagName, int n ) throws XMLStreamException, MPEG7VDFormatException  {
//		byte[] result = null;
//
//		for (int event = xmlr.next();  
//	    	event != XMLStreamConstants.END_DOCUMENT;
//	    	event = xmlr.next()) {
//	    	switch (event) {
//	            case XMLStreamConstants.END_ELEMENT:
//	            	if (xmlr.getLocalName().equals( tagName) ) { 
//	            		if ( result == null ) throw new MPEG7VDFormatException();
//	            		return result;
//	            	}
//	            	break;
//	            case XMLStreamConstants.CHARACTERS:
//            		//System.out.print("Coef " + xmlr.getText() );
//            		
//	            	result = Utilities.stringToUnsignedByteArray( xmlr.getText() );            		
//            		
//            		if ( result.length != n )  throw new MPEG7VDFormatException();
//            		
//	            	break;
//	    	} // end switch   		    	
//	    } // end while	
//		
//		throw new MPEG7VDFormatException();
//	}
}
