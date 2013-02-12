/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
