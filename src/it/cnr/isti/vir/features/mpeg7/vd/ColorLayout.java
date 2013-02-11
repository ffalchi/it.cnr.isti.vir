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

public final class ColorLayout implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;	
	
	final byte yDCCoeff;
	final byte cbDCCoeff;
	final byte crDCCoeff;
	final byte[] yACCoeff;
	final byte[] cbACCoeff;
	final byte[] crACCoeff;
	
	static final byte version = 0;
	
	static final int m_weight[][] = {
				{ 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1 },
				{ 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1 },
				{ 4, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
						1, 1, 1, 1, 1, 1, 1, 1 }
				
				};

	public ColorLayout(ByteBuffer src) throws IOException {
		double version = src.get();
		yDCCoeff = src.get();
		cbDCCoeff = src.get();
		crDCCoeff = src.get();
		
		yACCoeff = new byte[src.get()];
		src.get(yACCoeff);
		
		cbACCoeff = new byte[src.get()];
		src.get(cbACCoeff);
		
		crACCoeff = new byte[src.get()];
		src.get(crACCoeff);
	}
	
	public ColorLayout(DataInput str) throws IOException {
		double version = str.readByte();
		yDCCoeff = str.readByte();
		cbDCCoeff = str.readByte();
		crDCCoeff = str.readByte();
		
		yACCoeff = new byte[str.readByte()];
		for (int i=0; i<yACCoeff.length; i++) {
			yACCoeff[i] = str.readByte();
		}		
		
		cbACCoeff = new byte[str.readByte()];
		for (int i=0; i<cbACCoeff.length; i++) {
			cbACCoeff[i] = str.readByte();
		}
		
		crACCoeff = new byte[str.readByte()];
		for (int i=0; i<crACCoeff.length; i++) {
			crACCoeff[i] = str.readByte();
		}
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		ColorLayout given = (ColorLayout) obj;
		
		if ( 	yDCCoeff 	!= given.yDCCoeff ||
				cbDCCoeff 	!= given.cbDCCoeff ||
				crDCCoeff 	!= given.crDCCoeff )
			return false;
		
		if ( yACCoeff.length != given.yACCoeff.length) return false;
		for (int i=0; i<yACCoeff.length; i++) {
			yACCoeff[i] = given.yACCoeff[i];
		}		
		
		if ( cbACCoeff.length != given.cbACCoeff.length) return false;
		for (int i=0; i<cbACCoeff.length; i++) {
			cbACCoeff[i] = given.cbACCoeff[i];
		}
		
		if ( crACCoeff.length != given.crACCoeff.length) return false;
		for (int i=0; i<crACCoeff.length; i++) {
			crACCoeff[i] = given.crACCoeff[i];
		}
		
		return true;
	}
	
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeByte(yDCCoeff);
		str.writeByte(cbDCCoeff);
		str.writeByte(crDCCoeff);
		
		str.writeByte((byte) yACCoeff.length);
		for (int i=0; i<yACCoeff.length; i++) {
			str.writeByte(yACCoeff[i]);
		}		
		
		str.writeByte((byte) cbACCoeff.length);
		for (int i=0; i<cbACCoeff.length; i++) {
			str.writeByte(cbACCoeff[i]);
		}	
		
		str.writeByte((byte) crACCoeff.length);
		for (int i=0; i<crACCoeff.length; i++) {
			str.writeByte(crACCoeff[i]);
		}	
	}
	
	
	public ColorLayout(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
	//public void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		
		byte yDCCoeff_temp = Byte.MIN_VALUE;
		byte cbDCCoeff_temp = Byte.MIN_VALUE;
		byte crDCCoeff_temp = Byte.MIN_VALUE;
		byte[] yACCoeff_temp = null;
		byte[] cbACCoeff_temp = null;
		byte[] crACCoeff_temp = null;
		
		for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr
				.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	        		String temp = "YDCCoeff";
	            	if (xmlr.getLocalName().equals(temp ) ) { 
	            		yDCCoeff_temp = (byte) Integer.parseInt( xmlr.getElementText() );
	            	} else {
	            		temp = "CbDCCoeff";
		            	if (xmlr.getLocalName().equals( temp ) ) { 
		            		cbDCCoeff_temp = (byte) Integer.parseInt( xmlr.getElementText() );
		            	} else {
		            		temp = "CrDCCoeff";
			            	if (xmlr.getLocalName().equals( temp ) ) { 
			            		crDCCoeff_temp = (byte) Integer.parseInt( xmlr.getElementText() );		            		
			            	} else {
			            		temp = "YACCoeff5";
				            	if (xmlr.getLocalName().equals( temp ) ) { 
				            		yACCoeff_temp = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );
				            		if ( yACCoeff_temp.length != 5 ) throw new MPEG7VDFormatException("Error parsing ColorLayout");
				            	} else {
				            		temp = "CbACCoeff2";
					            	if (xmlr.getLocalName().equals( temp ) ) { 
					            		cbACCoeff_temp = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );		            		
					            		if ( cbACCoeff_temp.length != 2 ) throw new MPEG7VDFormatException("Error parsing ColorLayout");
					            	} else {
					            		temp = "CrACCoeff2";
						            	if (xmlr.getLocalName().equals( temp ) ) { 
						            		crACCoeff_temp = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );		            		
						            		if ( crACCoeff_temp.length != 2 ) throw new MPEG7VDFormatException("Error parsing ColorLayout");
						            	}
					            	}
				            	}
			            	}
		            	}
	            	}
	            		
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("VisualDescriptor") ) { 
	            		
	            		if (  	yDCCoeff_temp  == Byte.MIN_VALUE 	||
	            				cbDCCoeff_temp == Byte.MIN_VALUE 	||
	            				crDCCoeff_temp == Byte.MIN_VALUE 	||
	            				yACCoeff_temp  == null 	||
	            				cbACCoeff_temp == null   ||
	            				crACCoeff_temp == null   || 
	            				cbACCoeff_temp.length != crACCoeff_temp.length )
	            		
	            			throw new MPEG7VDFormatException("Error parsing ColorLayout");
	            		
	            		yDCCoeff  = yDCCoeff_temp;
	            		cbDCCoeff = cbDCCoeff_temp;
	            		crDCCoeff = crDCCoeff_temp;
	            		yACCoeff  = yACCoeff_temp;
	            		cbACCoeff = cbACCoeff_temp;
	            		crACCoeff = crACCoeff_temp;
	            		return;
	            	}
	            	break;
	    	} // end switch
		} // end while
		
		yDCCoeff  = yDCCoeff_temp;
		cbDCCoeff = cbDCCoeff_temp;
		crDCCoeff = crDCCoeff_temp;
		yACCoeff  = yACCoeff_temp;
		cbACCoeff = cbACCoeff_temp;
		crACCoeff = crACCoeff_temp;
		
	}

	public int hashCode() {
		int tempHashCode = 0;
		tempHashCode = 31 * tempHashCode + yDCCoeff;
		tempHashCode = 31 * tempHashCode + cbDCCoeff;		
		tempHashCode = 31 * tempHashCode + crDCCoeff;
		
		for (int i=0; i<yACCoeff.length; i++) tempHashCode = 31 * tempHashCode + yACCoeff[i];
		for (int i=0; i<cbACCoeff.length; i++) tempHashCode = 31 * tempHashCode + cbACCoeff[i];
		for (int i=0; i<crACCoeff.length; i++) tempHashCode = 31 * tempHashCode + crACCoeff[i];
		
		return tempHashCode;
	}
	
	public final static double mpeg7XMDistance(ColorLayout d1, ColorLayout d2) {
		
//		ColorLayout d1 = (ColorLayout) featureInterface;
//		ColorLayout d2 = (ColorLayout) featureInterface2;
		
		int sum[] = new int[3];
		int diff;
		int NY1, NY2, NC1, NC2, NY, NC;
		int j;


		/*
		 * int m_weight[3][64]; m_weight[0][0]=3;
		 * m_weight[0][1]=m_weight[0][2]=3; m_weight[1][0]=2;
		 * m_weight[1][1]=m_weight[1][2]=2; m_weight[2][0]=4;
		 * m_weight[2][1]=m_weight[2][2]=2;
		 * 
		 * //m_weight[0][0]=2; m_weight[0][1]=m_weight[0][2]=2;
		 * //m_weight[1][0]=2; m_weight[1][1]=m_weight[1][2]=1;
		 * //m_weight[2][0]=4; m_weight[2][1]=m_weight[2][2]=2; for(int i=0; i<3;
		 * i++){ for(int j=3; j<64; j++) m_weight[i][j]=1; }
		 */

		NY1 = d1.yACCoeff.length;
		NY2 = d2.yACCoeff.length;

		NC1 = d1.cbACCoeff.length;
		NC2 = d2.cbACCoeff.length;

		NY = (NY1 < NY2) ? NY1 : NY2;
		NC = (NC1 < NC2) ? NC1 : NC2;

		diff = (d1.yDCCoeff - d2.yDCCoeff);
		sum[0] = m_weight[0][0] * diff * diff;
		for (j = 0; j < NY; j++) {
			diff = (d1.yACCoeff[j] - d2.yACCoeff[j]);
			sum[0] += (m_weight[0][j + 1] * diff * diff);
		}

		diff = (d1.cbDCCoeff - d2.cbDCCoeff);
		sum[1] = m_weight[1][0] * diff * diff;
		for (j = 0; j < NC; j++) {
			diff = (d1.cbACCoeff[j] - d2.cbACCoeff[j]);
			sum[1] += (m_weight[1][j + 1] * diff * diff);
		}

		diff = (d1.crDCCoeff - d2.crDCCoeff);
		sum[2] = m_weight[2][0] * diff * diff;
		for (j = 0; j < NC; j++) {
			diff = (d1.crACCoeff[j] - d2.crACCoeff[j]);
			sum[2] += (m_weight[2][j + 1] * diff * diff);
		}

		return Math.sqrt(sum[0] * 1.0) + Math.sqrt(sum[1] * 1.0)
				+ Math.sqrt(sum[2] * 1.0);
	} /* D_Distances_ColorLayoutD */	
	
	@Override
	public String toString() {
		
		String str = "ColorLayout";
		
		str += "\n  YDCCoeff: " + Convertions.unsignedByteToInt(yDCCoeff);
		str += "\n  CbDCCoeff: " + Convertions.unsignedByteToInt(cbDCCoeff);
		str += "\n  CrDCCoeff: " + Convertions.unsignedByteToInt(crDCCoeff);		
		
		str += "\n  YACCoeff:";
		for (int i=0; i<yACCoeff.length; i++ ){
			str += " " + Convertions.unsignedByteToInt(yACCoeff[i]);
		}
		
		str += "\n  CbACCoeff:";
		for (int i=0; i<cbACCoeff.length; i++ ){
			str += " " + Convertions.unsignedByteToInt(cbACCoeff[i]);
		}
		
		str += "\n  CrACCoeff:";
		for (int i=0; i<crACCoeff.length; i++ ){
			str += " " + Convertions.unsignedByteToInt(crACCoeff[i]);
		}		
				
	
		return str + "\n";
	}

	public boolean equals(IFeature givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		ColorLayout vd = (ColorLayout) givenVD;
		
		if ( yDCCoeff  != vd.yDCCoeff  ) return false;
		if ( cbDCCoeff != vd.cbDCCoeff ) return false;
		if ( crDCCoeff != vd.crDCCoeff ) return false;
		for (int i=0; i<yACCoeff.length; i++ ){
			if ( yACCoeff[i] != vd.yACCoeff[i] ) return false;
		}
		for (int i=0; i<cbACCoeff.length; i++ ){
			if ( cbACCoeff[i] != vd.cbACCoeff[i] ) return false;
		}		
		for (int i=0; i<crACCoeff.length; i++ ){
			if ( crACCoeff[i] != vd.crACCoeff[i] ) return false;
		}			
		return true;
	}

	public double getMaxDistance() 
	{
		// TODO Auto-generated method stub		
		return 3000;
	}
//
//	@Override
//	public IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}
	
	

	
}
