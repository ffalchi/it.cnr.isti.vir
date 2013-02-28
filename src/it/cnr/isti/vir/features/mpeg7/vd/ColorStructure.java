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

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.util.Convertions;
import it.cnr.isti.vir.util.L1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class ColorStructure implements IFeature, java.io.Serializable {

	

	public double getMaxDistance() {
		// TODO Auto-generated method stub
		return 40*255;
	}
	
	private static final long serialVersionUID = 1L;

	/* Actually they are unsigned
	 * Should be readed using 
	 * (int) byte[i]
	 */
	public final byte[] values;	
	
	static final int nOfValues = 64;	
	
	static final byte version = 1;
	
	public ColorStructure(byte[] values) {
		this.values = values;
	}
	
	public ColorStructure(DataInput str) throws IOException {
		byte version = str.readByte();
		values = new byte[str.readByte()];
		if ( version < 1 ) for (int i=0; i<values.length; i++) values[i] = (byte) (((int) str.readByte()) -128);
		else for (int i=0; i<values.length; i++) values[i] = str.readByte();
	}
	
	public ColorStructure(ByteBuffer src) throws IOException {
		byte version = src.get();
		values = new byte[src.get()];
		if ( version < 1 ) for (int i=0; i<values.length; i++) values[i] = (byte) (((int) src.get()) -128);
		else for (int i=0; i<values.length; i++) values[i] = src.get();
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeByte(values.length);
		for (int i=0; i<values.length; i++) str.writeByte(values[i]);
	}
	
	public int hashCode() {
		int tempHashCode = 0;
		
		for (int i=0; i<values.length; i++) tempHashCode = 31 * tempHashCode + values[i];
		
		return tempHashCode;
	}
	
	public ColorStructure(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		byte[] tValues = null;
		for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
			switch (event) {
			case XMLStreamConstants.START_ELEMENT:
				if (xmlr.getLocalName().equals("Values")) {
					tValues = Convertions.stringToUnsignedByteArray(xmlr
							.getElementText());
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				if (xmlr.getLocalName().equals("VisualDescriptor")) {
					values = tValues;
					return;
				}
				break;
			} // end switch
		} // end while	
		values = tValues;
	}

	public static final double mpeg7XMDistance(ColorStructure f1, ColorStructure f2) {
		return L1.get(f1.values, f2.values);
	}
/*
	public void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {

	}
	*/
//	protected void parseValues( XMLStreamReader xmlr ) throws XMLStreamException, MPEG7VDFormatException  {
//		for (int event = xmlr.next();  
//	    	event != XMLStreamConstants.END_DOCUMENT;
//	    	event = xmlr.next()) {
//	    	switch (event) {
//	            case XMLStreamConstants.END_ELEMENT:
//	            	if (xmlr.getLocalName().equals("Values") ) { 
//	            		return;
//	            	}
//	            	break;
//	            case XMLStreamConstants.CHARACTERS:
//            		//System.out.print("Coef " + xmlr.getText() );
//            		
//            		String text = xmlr.getText();
//            		
//            		values = Conversions.stringToUnsignedByteArray( text );
//            		
//            		if ( values.length != nOfValues ) {
//            			throw new MPEG7VDFormatException();
//            		}
//            		
//	            	break;
//	    	} // end switch
//	    	
//	    	
//	    } // end while	
//	}
	
	public String toString() {
		String str = "ColorStructure";
		str += "  Values:";
		for (int i=0; i<values.length; i++ ){
			str += " " + Convertions.unsignedByteToInt(values[i]);
		}
		return str + "\n";
	}
	
	public boolean equals(IFeature givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		ColorStructure vd = (ColorStructure) givenVD;

		for (int i=0; i<values.length; i++ ){
			if ( values[i] != vd.values[i] ) return false;
		}


		return true;
	}
	
//	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}
	

	public boolean equals(Object givenVD) {
		
		if ( this == givenVD ) return true;
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		ColorStructure vd = (ColorStructure) givenVD;
		if ( values.length != vd.values.length ) return false;
		for (int i=0; i<values.length; i++ ){
			if ( values[i] != vd.values[i] ) return false;
		}


		return true;
	}
	
}
