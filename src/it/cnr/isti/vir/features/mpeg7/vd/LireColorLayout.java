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

public final class LireColorLayout extends AbstractFeature {

	private static final long serialVersionUID = 1L;	
	
//	final byte yDCCoeff;
//	final byte cbDCCoeff;
//	final byte crDCCoeff;
//	final byte[] yACCoeff;
//	final byte[] cbACCoeff;
//	final byte[] crACCoeff;
	
	static final byte version = 0;
	

	public LireColorLayout(ByteBuffer src) throws IOException {
		double version = src.get();
		
		YCoeff = new byte[src.get()];
		src.get(YCoeff);
		
		CbCoeff = new byte[src.get()];
		src.get(CbCoeff);
		
		CrCoeff = new byte[src.get()];
		src.get(CrCoeff);
	}
	
	public LireColorLayout(DataInput str) throws IOException {
		double version = str.readByte();
		
		YCoeff = new byte[str.readByte()];
		for (int i=0; i<YCoeff.length; i++) {
			YCoeff[i] = str.readByte();
		}		
		
		CbCoeff = new byte[str.readByte()];
		for (int i=0; i<CbCoeff.length; i++) {
			CbCoeff[i] = str.readByte();
		}
		
		CrCoeff = new byte[str.readByte()];
		for (int i=0; i<CrCoeff.length; i++) {
			CrCoeff[i] = str.readByte();
		}
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		LireColorLayout given = (LireColorLayout) obj;
		
		
		if ( YCoeff.length != given.getYCoeff().length) return false;
		for (int i=0; i<YCoeff.length; i++) {
			if ( YCoeff[i] != given.getYCoeff()[i] ) return false;
		}		
		
		if ( CbCoeff.length != given.CbCoeff.length) return false;
		for (int i=0; i<CbCoeff.length; i++) {
			if ( CbCoeff[i] != given.getCbCoeff()[i] ) return false;
		}
		
		if ( CrCoeff.length != given.CrCoeff.length) return false;
		for (int i=0; i<CrCoeff.length; i++) {
			if ( CrCoeff[i] != given.getCrCoeff()[i] ) return false;
		}
		
		return true;
	}
	
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeByte((byte) YCoeff.length);
		for (int i=0; i<YCoeff.length; i++) {
			str.writeByte(YCoeff[i]);
		}		
		
		str.writeByte((byte) CbCoeff.length);
		for (int i=0; i<CbCoeff.length; i++) {
			str.writeByte(CbCoeff[i]);
		}	
		
		str.writeByte((byte) CrCoeff.length);
		for (int i=0; i<CrCoeff.length; i++) {
			str.writeByte(CrCoeff[i]);
		}	
	}
	
    public byte[] YCoeff;
    public byte[] CbCoeff;
    public byte[] CrCoeff;
    protected int numCCoeff = 28, numYCoeff = 64;
	
	
	public LireColorLayout(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		for (int event = xmlr.getEventType(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr
				.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	        		if (xmlr.getLocalName().equals("ColorLayout") ) {
	        			 String[] coeffs = xmlr.getElementText().split("z");
	        			 String[] y = coeffs[0].split(" ");
	        			 String[] cb = coeffs[1].split(" ");
	        			 String[] cr = coeffs[2].split(" ");
	        			 
	        			 numYCoeff = y.length;
        		        numCCoeff = Math.min(cb.length, cr.length);

        		        YCoeff = new byte[numYCoeff];
        		        CbCoeff = new byte[numCCoeff];
        		        CrCoeff = new byte[numCCoeff];

        		        for (int i = 0; i < numYCoeff; i++) {
        		            YCoeff[i] = (byte) Integer.parseInt(y[i]);
        		        }
        		        for (int i = 0; i < numCCoeff; i++) {
        		            CbCoeff[i] = (byte) Integer.parseInt(cb[i]);
        		            CrCoeff[i] = (byte) Integer.parseInt(cr[i]);
        		        }
        		        return;
	            	}
	            		
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	break;
	    	} // end switch
		} // end while
	}

	public int hashCode() {
		int tempHashCode = 0;
		for (int i=0; i<YCoeff.length; i++) tempHashCode = 31 * tempHashCode + YCoeff[i];
		for (int i=0; i<CbCoeff.length; i++) tempHashCode = 31 * tempHashCode + CbCoeff[i];
		for (int i=0; i<CrCoeff.length; i++) tempHashCode = 31 * tempHashCode + CrCoeff[i];
		
		return tempHashCode;
	}
	
    public byte[] getYCoeff() {
        return YCoeff;
    }

    public byte[] getCbCoeff() {
        return CbCoeff;
    }

    public byte[] getCrCoeff() {
        return CrCoeff;
    }
	
    public final static double mpeg7XMDistance(LireColorLayout d1, LireColorLayout d2) {
        int numYCoeff1, numYCoeff2, CCoeff1, CCoeff2, YCoeff, CCoeff;

        //Numbers of the Coefficients of two descriptor values.
        numYCoeff1 = d1.getYCoeff().length;
        numYCoeff2 = d2.getYCoeff().length;
        CCoeff1 = d1.getCbCoeff().length;
        CCoeff2 = d2.getCbCoeff().length;

        //take the minimal Coeff-number
        YCoeff = Math.min(numYCoeff1, numYCoeff2);
        CCoeff = Math.min(CCoeff1, CCoeff2);

        setWeightingValues();

        int j;
        int[] sum = new int[3];
        int diff;
        sum[0] = 0;

        for (j = 0; j < YCoeff; j++) {
            diff = (d1.getYCoeff()[j] - d2.getYCoeff()[j]);
            sum[0] += (weightMatrix[0][j] * diff * diff);
        }

        sum[1] = 0;
        for (j = 0; j < CCoeff; j++) {
            diff = (d1.getCbCoeff()[j] - d2.getCbCoeff()[j]);
            sum[1] += (weightMatrix[1][j] * diff * diff);
        }

        sum[2] = 0;
        for (j = 0; j < CCoeff; j++) {
            diff = (d1.getCrCoeff()[j] - d1.getCrCoeff()[j]);
            sum[2] += (weightMatrix[2][j] * diff * diff);
        }

        //returns the distance between the two desciptor values

        return Math.sqrt(sum[0] * 1.0) + Math.sqrt(sum[1] * 1.0) + Math.sqrt(sum[2] * 1.0);
    }
	
    protected static int[][] weightMatrix = new int[3][64];
	
    private static void setWeightingValues() {
        weightMatrix[0][0] = 2;
        weightMatrix[0][1] = weightMatrix[0][2] = 2;
        weightMatrix[1][0] = 2;
        weightMatrix[1][1] = weightMatrix[1][2] = 1;
        weightMatrix[2][0] = 4;
        weightMatrix[2][1] = weightMatrix[2][2] = 2;

        for (int i = 0; i < 3; i++) {
            for (int j = 3; j < 64; j++)
                weightMatrix[i][j] = 1;
        }
    }
	
	
	@Override
	public String toString() {
		
		String str = "ColorLayout";
		
		str += "\n  YCoeff:";
		for (int i=0; i<YCoeff.length; i++ ){
			str += " " + Conversions.unsignedByteToInt(YCoeff[i]);
		}
		
		str += "\n  CbCoeff:";
		for (int i=0; i<CbCoeff.length; i++ ){
			str += " " + Conversions.unsignedByteToInt(CbCoeff[i]);
		}
		
		str += "\n  CrCoeff:";
		for (int i=0; i<CrCoeff.length; i++ ){
			str += " " + Conversions.unsignedByteToInt(CrCoeff[i]);
		}		
				
		return str + "\n";
	}

	public boolean equals(AbstractFeature givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		LireColorLayout vd = (LireColorLayout) givenVD;
		
		if ( YCoeff.length != vd.getYCoeff().length) return false;
		for (int i=0; i<YCoeff.length; i++) {
			if ( YCoeff[i] != vd.getYCoeff()[i] ) return false;
		}		
		
		if ( CbCoeff.length != vd.CbCoeff.length) return false;
		for (int i=0; i<CbCoeff.length; i++) {
			if ( CbCoeff[i] != vd.getCbCoeff()[i] ) return false;
		}
		
		if ( CrCoeff.length != vd.CrCoeff.length) return false;
		for (int i=0; i<CrCoeff.length; i++) {
			if ( CrCoeff[i] != vd.getCrCoeff()[i] ) return false;
		}			
		return true;
	}

	public double getMaxDistance() 
	{
		// TODO Auto-generated method stub		
		return 3000;
	}
}
