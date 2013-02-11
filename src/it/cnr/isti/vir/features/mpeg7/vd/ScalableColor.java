package it.cnr.isti.vir.features.mpeg7.vd;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.util.Convertions;

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
	final short[] coeff;
	
	static final byte version = 0;
	
	public ScalableColor(DataInput str) throws IOException {
		byte version = str.readByte();
		coeff = new short[str.readByte()];
		for (int i=0; i<coeff.length; i++) coeff[i] = str.readShort();
	}
	
	public ScalableColor(ByteBuffer in) throws IOException {
		byte version = in.get();
		coeff = new short[in.get()];
		for (int i=0; i<coeff.length; i++) coeff[i] = in.getShort();
		//in.asShortBuffer().get(coeff);
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
	            		temp = Convertions.stringToShortArray( xmlr.getElementText() );
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
		short[] coeff1 = f1.coeff;
		short[] coeff2 = f2.coeff;
		int sum = 0;
		assert(coeff1.length == coeff2.length);
		for ( int i=0; i<coeff1.length; i++ ) {
			sum += Math.abs( coeff1[i] - coeff2[i] );
		}
				
		return sum;
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
