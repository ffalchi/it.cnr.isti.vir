package it.cnr.isti.vir.features.mpeg7.vd;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.util.Convertions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

//<VisualDescriptor xsi:type="RegionShapeType">
//<MagnitudeOfART>15 10 1 2 5 1 3 3 4 0 1 2 2 1 4 1 0 2 2 1 3 1 0 5 0 1 3 1 0 2 1 0 4 1 1</MagnitudeOfART></VisualDescriptor>



public class RegionShape implements IFeature {

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
		            		magnitudeOfART_temp = Convertions.stringToByteArray(xmlr.getElementText());
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

	public boolean equals(IFeaturesCollector givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		RegionShape vd = (RegionShape) givenVD;

		for (int i=0; i<magnitudeOfART.length; i++ ){
			if ( magnitudeOfART[i] != vd.magnitudeOfART[i] ) return false;
		}


		return true;
	}
	
	public static final double mpeg7XMDistance(IFeature f1, IFeature f2) {
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
