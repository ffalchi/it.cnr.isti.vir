/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.features.mpeg7.vd;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.util.Conversions;
import it.cnr.isti.vir.util.L1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class ScalableColor implements IFeature, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7435535940279680130L;

	public double getMaxDistance() {
		// TODO Auto-generated method stub
		return 3000;
	}
	
	/**
	 * 
	 */
	
	static final int nOfCoeff = 64;
	public final short[] coeff;
	
	static final byte version = 0;
	
	public ScalableColor(short[] coeff) {
		this.coeff = coeff;
	}
	
	public ScalableColor(DataInput str) throws IOException {
		byte version = str.readByte();
		coeff = new short[str.readByte()];
		for (int i=0; i<coeff.length; i++) coeff[i] = str.readShort();
	}
	
	public ScalableColor(ByteBuffer in) throws IOException {
		byte version = in.get();
		coeff = new short[in.get()];
		for (int i=0; i<coeff.length; i++) coeff[i] = in.getShort();
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeByte(coeff.length);
		for (int i=0; i<coeff.length; i++) str.writeShort(coeff[i]);
	}
		
	public ScalableColor(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		//protected void parse( XMLStreamReader xmlr ) throws XMLStreamException, MPEG7VDFormatException  {
		
		short[] temp = null;
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	            	if (xmlr.getLocalName().equals("Coeff") ) { 
	            		temp = Conversions.stringToShortArray( xmlr.getElementText() );
	            	}
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("VisualDescriptor") ) { 
	            		coeff = temp;
	            		return;
	            	}
	            	break;
	    	} // end switch
	    } // end while
		
		coeff = temp;
	}


	
//	protected void parseCoeff( XMLStreamReader xmlr ) throws XMLStreamException, MPEG7VDFormatException  {
//		for (int event = xmlr.next();  
//	    	event != XMLStreamConstants.END_DOCUMENT;
//	    	event = xmlr.next()) {
//	    	switch (event) {
//	            case XMLStreamConstants.END_ELEMENT:
//	            	if (xmlr.getLocalName().equals("Coeff") ) { 
//	            		return;
//	            	}
//	            	break;
//	            case XMLStreamConstants.CHARACTERS:
//            		//System.out.print("Coef " + xmlr.getText() );
//            		
//            		String text = xmlr.getText();
//            		
//            		coeff = Utilities.stringToShortArray( text );
//            		
//            		if ( coeff.length != nOfCoeff ) {
//            			throw new MPEG7VDFormatException();
//            		}
//            		
//	            	break;
//	    	} // end switch
//	    	
//	    	
//	    } // end while	
//	}

	
	public static final double mpeg7XMDistance(ScalableColor f1, ScalableColor f2) {
		return L1.get(f1.coeff, f2.coeff);
	}

	public String toString() {
		String str = "ScalableColor";
		str += "  Coeff:";
		for (int i=0; i<coeff.length; i++ ){
			str += " " + coeff[i];
		}
		return str + "\n";
	}

	public boolean equals(Object givenVD) {
		
		if ( this == givenVD ) return true;
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		ScalableColor vd = (ScalableColor) givenVD;

		for (int i=0; i<coeff.length; i++ ){
			if ( coeff[i] != vd.coeff[i] ) return false;
		}


		return true;
	}
	
	public int hashCode() {
		int tempHashCode = 0;
		
		for (int i=0; i<coeff.length; i++) tempHashCode = 31 * tempHashCode + coeff[i];
		
		return tempHashCode;
	}
	
//	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}
	
}
