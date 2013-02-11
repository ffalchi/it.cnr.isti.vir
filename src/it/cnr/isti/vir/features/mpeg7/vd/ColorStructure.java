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
	byte[] values;	
	
	static final int nOfValues = 64;	
	
	static final byte version = 1;
	
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
		parse(xmlr);		
	}

	public static final double mpeg7XMDistance(ColorStructure f1, ColorStructure f2) {
		
		byte[] values1 = f1.values;
		byte[] values2 = f2.values;
 		
		int sum = 0;
		
		assert (values1.length==values2.length);
		for ( int i=0; i<values1.length; i++ ) {
			sum += Math.abs( 	values1[i] - values2[i]); // for the difference we do not need convertion
			//sum += Math.abs( 	Convertions.unsignedByteToInt(values1[i]) - Convertions.unsignedByteToInt(values2[i] ));
		}				
		return sum; /// 255.0;
	}

	public void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	            	if (xmlr.getLocalName().equals("Values") ) { 
	            		values = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );
	            	}
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("VisualDescriptor") ) { 
	            		return;
	            	}
	            	break;
	    	} // end switch
	    } // end while
	}
	
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
