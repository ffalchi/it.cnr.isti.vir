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

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.util.Conversions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//<VisualDescriptor xsi:type="RegionShapeType">
//<MagnitudeOfART>15 10 1 2 5 1 3 3 4 0 1 2 2 1 4 1 0 2 2 1 3 1 0 5 0 1 3 1 0 2 1 0 4 1 1</MagnitudeOfART></VisualDescriptor>



public class RegionShape extends AbstractFeature {

	private static final double[] IQuantTable = {0.001763817, 0.005468893, 0.009438835, 0.013714449, 0.018346760, 0.023400748, 0.028960940, 0.035140141, 0.042093649, 0.050043696, 0.059324478, 0.070472849, 0.084434761, 0.103127662, 0.131506859, 0.192540857};
	
	// compressed version
	final byte[] magnitudeOfART;

	public RegionShape(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
//		protected void parse(XMLStreamReader xmlr) throws XMLStreamException {
			byte[] magnitudeOfART_temp = null;
			for (int event = xmlr.next();  
		    	event != XMLStreamConstants.END_DOCUMENT;
		    	event = xmlr.next()) {
		    	switch (event) {
		        	case XMLStreamConstants.START_ELEMENT:
		            	if (xmlr.getLocalName().equals("MagnitudeOfART") ) { 
		            		magnitudeOfART_temp = Conversions.stringToByteArray(xmlr.getElementText());
		            	}
		            	break;
		            case XMLStreamConstants.END_ELEMENT:
		            	if (xmlr.getLocalName().equals("VisualDescriptor") ) { 
		            		if ( magnitudeOfART_temp.length != 35 ) throw new MPEG7VDFormatException("Error reading RegionShape");
		            		magnitudeOfART = magnitudeOfART_temp;
		            		return;
		            	}
		            	break;
		    	} // end switch
		    } // end while
			magnitudeOfART = magnitudeOfART_temp;
//		}		
	}
	
	static final byte version = 0;
	
	public RegionShape(DataInput str) throws IOException {
		byte version = str.readByte();
		magnitudeOfART = new byte[35];
		for (int i=0; i<magnitudeOfART.length; i++) magnitudeOfART[i] = str.readByte();
	}
	
	public RegionShape(ByteBuffer in) throws IOException {
		byte version = in.get();
		magnitudeOfART = new byte[35];
		for (int i=0; i<magnitudeOfART.length; i++) magnitudeOfART[i] = in.get();
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		for (int i=0; i<magnitudeOfART.length; i++) str.writeByte(magnitudeOfART[i]);
	}
	
	public int hashCode() {
		int tempHashCode = 0;
		
		for (int i=0; i<magnitudeOfART.length; i++) tempHashCode = 31 * tempHashCode + magnitudeOfART[i];
		
		return tempHashCode;
	}


	

	public String toString() {
		String str = "RegionShape";
		str += "  MagnitudeOfART:";
		for (int i=0; i<magnitudeOfART.length; i++ ){
			str += " " + magnitudeOfART[i];
		}
		return str + "\n";
	}

	public boolean equals(AbstractFeature givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		RegionShape vd = (RegionShape) givenVD;

		for (int i=0; i<magnitudeOfART.length; i++ ){
			if ( magnitudeOfART[i] != vd.magnitudeOfART[i] ) return false;
		}


		return true;
	}
	
	public static final double mpeg7XMDistance(AbstractFeature f1, AbstractFeature f2) {
		byte[] coeff1 = ((RegionShape) f1).magnitudeOfART;
		byte[] coeff2 = ((RegionShape) f2).magnitudeOfART;
		double sum = 0;
		for ( int i=0; i<coeff1.length; i++ ) {
			sum += Math.abs( 	IQuantTable[coeff1[i]]
								-
								IQuantTable[coeff2[i]] );
		}
				
		return sum;
	}
	
	
//	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}
}
