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
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.util.Convertions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class HomogeneousTexture implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	public static boolean preCompute = true;
	
	// option can be "n", "r", "s", "rs", "sr"
	public static final int N_OPTION = 0;
	public static final int R_OPTION = 1;
	public static final int S_OPTION = 2;
	public static final int RS_OPTION= 3;

	private final byte[] origData;

	public final float[] preComputed; 
	
	static final byte version = 2;
	
	public HomogeneousTexture(float[] preComputed) {
		origData = null;
		this.preComputed = preComputed;
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		HomogeneousTexture givenF = (HomogeneousTexture) obj;
		
		if ( origData != null)
			for (int i=0; i<origData.length; i++ )
				if ( origData[i] != givenF.origData[i] ) return false;
		
		if ( preComputed != null)
			for (int i=0; i<preComputed.length; i++ )
				if ( preComputed[i] != givenF.preComputed[i] ) return false;

		
		return true;
	}
	
//	public HomogeneousTexture_new(HomogeneousTexture given) {
//		origData = new byte[62];
//		origData[0] = given.average;
//		origData[1] = given.standardDeviation;
//		for (int i=0; i<30; i++) origData[i+2] = given.energy[i];
//		for (int i=0; i<30; i++) origData[i+2+30] = given.energyDeviation[i];
//		preComputed = null;
//	}
	
	public HomogeneousTexture(ByteBuffer in) throws IOException {
		byte version = in.get();
		if ( version == 0 ) {
			// OLD VERSION FOR READING
			byte average  = in.get();
			byte standardDeviation = in.get();
			boolean energyDev = ( in.get() != 0 );
			if ( energyDev )
				origData = new byte[62];
			else 
				origData = new byte[32];
			origData[0] = average;
			origData[1] = standardDeviation;
			in.get(origData, 2, 30);
			//for (int i=0; i<30; i++) origData[i+2] = str.readByte();
			if ( energyDev ) {
			//	for (int i=0; i<30; i++) origData[i+32] = str.readByte();
				in.get(origData, 32, 30);
			}
			preComputed = null;
		
		} else {
			boolean hasPreComputed = in.get() != 0;
			if ( hasPreComputed ) {
				origData = null;
				preComputed = new float[in.get()];
				for (int i=0; i<preComputed.length; i++) preComputed[i] = in.getFloat();
			}  else {
				preComputed = null;
				origData = new byte[in.get()];
				for (int i=0; i<origData.length; i++) origData[i] = in.get();				
			}
		}		
	}
	
	public HomogeneousTexture(DataInput str) throws IOException {
		byte version = str.readByte();
		if ( version == 0 ) {
			// OLD VERSION FOR READING
			byte average  = str.readByte();
			byte standardDeviation = str.readByte();
			boolean energyDev = str.readBoolean();
			if ( energyDev )
				origData = new byte[62];
			else 
				origData = new byte[32];
			origData[0] = average;
			origData[1] = standardDeviation;
			for (int i=0; i<30; i++) origData[i+2] = str.readByte();
			if ( energyDev ) {
				for (int i=0; i<30; i++) origData[i+32] = str.readByte();		
			}
			preComputed = null;
		
		} else {
			boolean hasPreComputed = str.readBoolean();
			if ( hasPreComputed ) {
				origData = null;
				preComputed = new float[str.readByte()];
				for (int i=0; i<preComputed.length; i++) preComputed[i] = str.readFloat();
			}  else {
				preComputed = null;
				origData = new byte[str.readByte()];
				for (int i=0; i<origData.length; i++) origData[i] = str.readByte();				
			}
		}		
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeBoolean(preComputed!=null);
		
		if ( preComputed != null) {
			str.writeByte(preComputed.length); //30!
			for (int i=0; i<preComputed.length; i++) str.writeFloat(preComputed[i]);	
		} else {
			str.writeByte(origData.length);
			for (int i=0; i<origData.length; i++) str.writeByte(origData[i]);
		}
		
	}
	
	public double getMaxDistance() {
		return 25;
	}
	
	public HomogeneousTexture(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
//		protected void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
			
			boolean averageInit = false;
			boolean standardDeviationInit = false;
			byte average_temp = 0;
			byte standardDeviation_temp = 0;
		
			byte[] energy_temp = null;
			byte[] energyDeviation_temp = null;
			for (int event = xmlr.next();  
		    	event != XMLStreamConstants.END_DOCUMENT;
		    	event = xmlr.next()) {
		    	switch (event) {
		        	case XMLStreamConstants.START_ELEMENT:
		        		String temp = "Average";
		            	if (xmlr.getLocalName().equals(temp) ) { 
		            		average_temp = Convertions.stringToUnsignedByte( xmlr.getElementText() );
		            		averageInit = true;
		            		//average = parseByteInTag(xmlr, temp);
		            	} else {
		            		temp = "StandardDeviation";
			            	if (xmlr.getLocalName().equals(temp) ) { 
			            		standardDeviation_temp = Convertions.stringToUnsignedByte( xmlr.getElementText() );
			            		//standardDeviation = parseByteInTag(xmlr, temp);
			            		standardDeviationInit = true;
			            	} else {
			            		temp = "Energy";
				            	if (xmlr.getLocalName().equals(temp) ) { 
				            		energy_temp = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );
				            		if ( energy_temp.length != 30 ) throw new MPEG7VDFormatException("Error parsing HomogeneousTexture");
				            		//energy = parseBytesInTag(xmlr, temp, 30);	            		
				            	} else {
				            		temp = "EnergyDeviation";
					            	if (xmlr.getLocalName().equals(temp) ) { 
					            		//energyDeviation = parseBytesInTag(xmlr, temp, 30);
					            		energyDeviation_temp = Convertions.stringToUnsignedByteArray( xmlr.getElementText() );
					            		if ( energyDeviation_temp.length != 30 ) throw new MPEG7VDFormatException("Error parsing HomogeneousTexture");
					            	}
				            	}
			            	}
		            	}
		            		
		            	break;
		            case XMLStreamConstants.END_ELEMENT:
		            	if (xmlr.getLocalName().equals("VisualDescriptor") ) { 
		            		if (  	!averageInit					||
		            				!standardDeviationInit			||
		            				energy_temp  == null 			||
		            				energyDeviation_temp  == null  )
		            		
		            			throw new MPEG7VDFormatException("Error parsing HomogeneousTexture");
	            		
		            		
		            		/// PREPARING DATA
	            			byte[] tempData;
		            		if ( energyDeviation_temp == null) {
		            			tempData = new byte[32];
		            		} else {
		            			tempData = new byte[62];
		            		}
		            		tempData[0] = average_temp;
		            		tempData[1] = standardDeviation_temp;
		            		for (int i=0; i<energy_temp.length; i++ )
		            			tempData[i+2] = energy_temp[i];
		            		if ( energyDeviation_temp != null)
		            			for (int i=0; i<energy_temp.length; i++ )
		            				tempData[i+2+30] = energyDeviation_temp[i];
		            		
		            		if ( preCompute ) {
		            			preComputed = getPreComputed(tempData);
		            			origData = null;
		            		} else {
		            			origData = tempData;	 
		            			preComputed = null;
		            		}
		            		
		            		return;
		            	}
		            	break;
		    	} // end switch
		    } // end while
			
			origData=null;
			preComputed=null;
//		}
	}
	


	
	public int hashCode() {
		int tempHashCode = 0;
		
		if ( origData != null )	for (int i=0; i<origData.length; i++) tempHashCode = 31 * tempHashCode + origData[i];
		
		if ( preComputed != null ) for (int i=0; i<preComputed.length; i++) tempHashCode = 31 * tempHashCode + new Float(preComputed[i]).hashCode();
		
		return tempHashCode;
	}

	
	public static final double mpeg7XMDistance(HomogeneousTexture d1, HomogeneousTexture d2) {
		// normal distance
		return HomogeneousTexture.mpeg7XMDistance( d1, d2, 0);
	}

	
	public String toString() {
		String str = "HomogeneousTexture ";
		if ( origData != null ) {
			str += "\n  Average: " + Convertions.unsignedByteToInt(origData[0]);
			str += "\n  StandardDeviation: " + Convertions.unsignedByteToInt(origData[2]);		
			str += "\n  Energy:";
			for (int i=0; i<30; i++ ){
				str += " " + Convertions.unsignedByteToInt(origData[i+2]);
			}
			str += "\n  EnergyDeviation:";
			for (int i=0; i<30; i++ ){
				str += " " + Convertions.unsignedByteToInt(origData[i+32]);
			}		
		} else {
			str += " PRECOMPUTED";
			str += "\n  Average: " + preComputed[0];
			str += "\n  StandardDeviation: " + preComputed[2];		
			str += "\n  Energy:";
			for (int i=0; i<30; i++ ){
				str += " " + preComputed[i+2];
			}
			str += "\n  EnergyDeviation:";
			for (int i=0; i<30; i++ ){
				str += " " + preComputed[i+32];
			}	
		}
		return str + "\n";
	}
	
	public boolean equals(IFeaturesCollector givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		HomogeneousTexture vd = (HomogeneousTexture) givenVD;
		if ( 	(origData == null || vd.origData == null ) 
				&&
				origData != vd.origData
			) 
			return false;
		
		if ( 	(preComputed == null || vd.preComputed == null ) 
				&&
				preComputed != vd.preComputed
			) 
			return false;	
	
		if ( origData != null) for ( int i=0; i<origData.length; i++) if ( origData[i] != vd.origData[i] ) return false;
		if ( preComputed != null) for ( int i=0; i<preComputed.length; i++) if ( preComputed[i] != vd.preComputed[i] ) return false;

		return true;
	}

	// option can be "n", "r", "s", "rs", "sr"
	public static final double mpeg7XMDistance(	HomogeneousTexture d1, HomogeneousTexture d2, int option)
	{
//		HomogeneousTexture d1 = (HomogeneousTexture) f1;
//		HomogeneousTexture d2 = (HomogeneousTexture) f2;
		
		float[] pre1 = d1.preComputed;
		float[] pre2 = d2.preComputed;
		
		if ( pre1== null) pre1=getPreComputed(d1.origData);
		if ( pre2== null) pre2=getPreComputed(d2.origData);
		return mpeg7XMDistance(pre1, pre2, option);
	}
	
	// option can be "n", "r", "s", "rs", "sr"
	public static final float[] getPreComputed( byte[] orig )
	{
		int RefFeature[] = new int[orig.length];
		float fRefFeature[] = new float[orig.length];
		for (int i=0; i< orig.length; i++) RefFeature[i] = Convertions.unsignedByteToInt(orig[i]);

		HT_dequantization(RefFeature, fRefFeature);
		HT_Normalization(fRefFeature);

		return fRefFeature;
	}
	
	
	// option can be "n", "r", "s", "rs", "sr"
	public static final double mpeg7XMDistance( float[] fQueryFeature, float[]  fRefFeature, int option) {
		double temp_distance, distance = 0.0;
		
		boolean useEnergyDeviation = true;
		if ( fQueryFeature.length!=NUMofFEATURE || fRefFeature.length!=NUMofFEATURE  ) useEnergyDeviation = false;
		
		distance =(wdc*Math.abs(fRefFeature[0]-fQueryFeature[0]));
		distance +=(wstd*Math.abs(fRefFeature[1]-fQueryFeature[1]));
		
		if ( option == R_OPTION ) {
		    double min = Double.MAX_VALUE;
		    for (int i=AngularDivision; i>0; i--) {
				temp_distance = 0.0;
				for(int n=0;n<RadialDivision;n++)
					for(int m=0;m<AngularDivision;m++) {
						if (m >= i) {
							temp_distance+=(wm[n]*Math.abs(fRefFeature[n*AngularDivision+m+2-i]-fQueryFeature[n*AngularDivision+m+2]));
							if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[n*AngularDivision+m+30+2-i]-fQueryFeature[n*AngularDivision+m+30+2]));
						}
						else {
							temp_distance+=(wm[n]*Math.abs(fRefFeature[(n+1)*AngularDivision+m+2-i]-fQueryFeature[n*AngularDivision+m+2]));
							if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[(n+1)*AngularDivision+m+30+2-i]-fQueryFeature[n*AngularDivision+m+30+2]));
						}
					}

			if (temp_distance < min) min = temp_distance;
			}

		    distance +=min;
		    return distance;
		}
		
		else if ( option == S_OPTION) {
			int flag = 0;
			if ( useEnergyDeviation ) flag = 1;
			
		    double min = Double.MAX_VALUE;
		    {
		    	int i;
		        for (i=0;i<3;i++) {
		        	temp_distance =0.0;
		        	for(int n=2;n<RadialDivision;n++)
		        		for(int m=0;m<AngularDivision;m++) {
		        			temp_distance+=(wm[n]*Math.abs(fRefFeature[n*AngularDivision+m+2]-fQueryFeature[(n-i)*AngularDivision+m+2]));
		        			if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[n*AngularDivision+m+30+2]-fQueryFeature[(n-i)*AngularDivision+m+30+2]));
			    
		        		}
		        	if (temp_distance < min) min = temp_distance;
		        }
			
			    for (i=1;i<3;i++) {
					temp_distance = 0.0;
					temp_distance =(wdc*Math.abs(fRefFeature[0]-fQueryFeature[0]));
					temp_distance +=(wstd*Math.abs(fRefFeature[1]-fQueryFeature[1]));
					for(int n=2;n<RadialDivision;n++)
						for(int m=0;m<AngularDivision;m++) {
							temp_distance+=(wm[n]*Math.abs(fRefFeature[(n-i)*AngularDivision+m+2]-fQueryFeature[n*AngularDivision+m+2]));
							if ( useEnergyDeviation ) temp_distance+= Math.abs(fRefFeature[(n-i)*AngularDivision+m+30+2]-fQueryFeature[n*AngularDivision+m+30+2]);
						}
	
					if (temp_distance < min)  min = temp_distance;
			    }
			    distance += min;
			    return distance;
		    }
		}

		else if( option  == RS_OPTION )
		{
		    double min = Double.MAX_VALUE;
		    {
		        
			for (int j=0;j<3;j++)
			  for (int i=AngularDivision;i>0;i--)
			    {
			      temp_distance =0.0;
			      for(int n=2;n<RadialDivision;n++)
					for(int m=0;m<AngularDivision;m++) {	
						if (m >= i) {
							temp_distance+=(wm[n]*Math.abs(fRefFeature[n*AngularDivision+m+2-i]-fQueryFeature[(n-j)*AngularDivision+m+2]));
							if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[n*AngularDivision+m+30+2-i]-fQueryFeature[(n-j)*AngularDivision+m+30+2]));
						}
						else {
							temp_distance+=(wm[n]*Math.abs(fRefFeature[(n+1)*AngularDivision+m+2-i]-fQueryFeature[(n-j)*AngularDivision+m+2]));
							if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[(n+1)*AngularDivision+m+30+2-i]-fQueryFeature[(n-j)*AngularDivision+m+30+2]));
						}
					}				
			      if (temp_distance < min) min = temp_distance;
			  }

			for (int j=1; j<3;j++)
			  for (int i=AngularDivision;i>0;i--) {
				   temp_distance =0.0;
				   for(int n=2;n<RadialDivision;n++)
				     for(int m=0;m<AngularDivision;m++) {	
				         if (m >= i) {
						     temp_distance+=(wm[n]*Math.abs(fRefFeature[(n-j)*AngularDivision+m+2-i]-fQueryFeature[n*AngularDivision+m+2]));
							 if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[(n-j)*AngularDivision+m+30+2-i]-fQueryFeature[n*AngularDivision+m+30+2]));
						 }
					     else {
							 temp_distance+=(wm[n]*Math.abs(fRefFeature[(n+1-j)*AngularDivision+m+2-i]-fQueryFeature[n*AngularDivision+m+2]));
							 if ( useEnergyDeviation ) temp_distance+= (wd[n]*Math.abs(fRefFeature[(n+1-j)*AngularDivision+m+30+2-i]-fQueryFeature[n*AngularDivision+m+30+2]));
						 }
					 }

				   if (temp_distance < min) min = temp_distance;
			  }
			distance = min + distance;
			return distance;
		    }
		}

		else
		{
		  return mpeg7XMDistance(fQueryFeature, fRefFeature);
		}
	};
	
	
	public static final double mpeg7XMDistance( float[] fQueryFeature, float[]  fRefFeature ) {
		double distance = 0.0;
		//default option is NULL ==> if (option == NULL)
		for(int n=0;n<RadialDivision;n++)
			for(int m=0;m<AngularDivision;m++) {
		    	distance +=
		    			wm[n] *	Math.abs(fRefFeature[n*AngularDivision+m+2]-fQueryFeature[n*AngularDivision+m+2])
		    			+
		    			wd[n] *	Math.abs(fRefFeature[n*AngularDivision+m+30+2]-fQueryFeature[n*AngularDivision+m+30+2]);
			}			
		return distance;
	}
	static final double dcmin=0.0, dcmax=255.0;
	static final double stdmin=1.309462,stdmax=109.476530;

//	double mmax[5][6]=
	static final double mmax[][]=
	{ {18.392888,18.014313,18.002143,18.083845,18.046575,17.962099},
	  {19.368960,18.628248,18.682786,19.785603,18.714615,18.879544},
	  {20.816939,19.093605,20.837982,20.488190,20.763511,19.262577},
	  {22.298871,20.316787,20.659550,21.463502,20.159304,20.280403},
	  {21.516125,19.954733,20.381041,22.129800,20.184864,19.999331}};

//	double mmin[5][6]=
	static final double mmin[][]=
	{{ 6.549734, 8.886816, 8.885367, 6.155831, 8.810013, 8.888925},
	 { 6.999376, 7.859269, 7.592031, 6.754764, 7.807377, 7.635503},
	 { 8.299334, 8.067422, 7.955684, 7.939576, 8.518458, 8.672599},
	 { 9.933642, 9.732479, 9.725933, 9.802238,10.076958,10.428015},
	 {11.704927,11.690975,11.896972,11.996963,11.977944,11.944282}};

//	double dmax[5][6]=
	static final double dmax[][]=	
	{ {21.099482,20.749788,20.786944,20.847705,20.772294,20.747129},
	  {22.658359,21.334119,21.283285,22.621111,21.773690,21.702166},
	  {24.317046,21.618960,24.396872,23.797967,24.329333,21.688523},
	  {25.638742,24.102725,22.687910,25.216958,22.334769,22.234942},
	  {24.692990,22.978804,23.891302,25.244315,24.281915,22.699811}};

//	double dmin[5][6]=
	static final double dmin[][]=		
	{{ 9.052970,11.754891,11.781252, 8.649997,11.674788,11.738701},
	 { 9.275178,10.386329,10.066189, 8.914539,10.292868,10.152977},
	 {10.368594,10.196313,10.211122,10.112823,10.648101,10.801070},
	 {11.737487,11.560674,11.551509,11.608201,11.897524,12.246614},
	 {13.303207,13.314553,13.450340,13.605001,13.547492,13.435994}};	
	
//	---yjyu - 010217
//	----------------------------------------------------------------------------
	private static final void HT_dequantization(int[] intFeature, float[] floatFeature)
	{
 
		int n,m;
		double dcstep,stdstep,mstep,dstep;

		dcstep=(dcmax-dcmin)/Quant_level;
		floatFeature[0]= (float) (dcmin+intFeature[0]*dcstep);

		stdstep=(stdmax-stdmin)/Quant_level;
		floatFeature[1]= (float) (stdmin+intFeature[1]*stdstep);

		for(n=0;n<RadialDivision;n++)
			for(m=0;m<AngularDivision;m++)
			{
				mstep=(mmax[n][m]-mmin[n][m])/Quant_level;
				floatFeature[n*AngularDivision+m+2]= (float) (mmin[n][m]+intFeature[n*AngularDivision+m+2]*mstep);
			}
		
		if (intFeature.length > 32) {
			//ENERGY DEVIATION
			for(n=0;n<RadialDivision;n++)
				for(m=0;m<AngularDivision;m++)
				{
					dstep=(dmax[n][m]-dmin[n][m])/Quant_level;
					floatFeature[n*AngularDivision+m+32]= (float) (dmin[n][m]+intFeature[n*AngularDivision+m+32]*dstep);
				}
		}
		
	}

	
//	----------------------------------------------------------------------------
	private static final void HT_Normalization( float[] feature)
	{
		int n,m;

		feature[0]/=dcnorm;
		feature[1]/=stdnorm;

		for(n=0;n<RadialDivision;n++)
			for(m=0;m<AngularDivision;m++)
				feature[n*AngularDivision+m+2]/=mmean[n][m];
		for(n=0;n<RadialDivision;n++)
			for(m=0;m<AngularDivision;m++)
				feature[n*AngularDivision+m+32]/=dmean[n][m];
	}
	
//	public final int getAverage() {
//		return Convertions.unsignedByteToInt(origData[0]);
//	}
//	
//	public final int getStandardDeviation() {
//		return Convertions.unsignedByteToInt(origData[1]);
//	}
//	
//	public final int[] getEnergy() {
//		return Convertions.unsignedByteArrayToIntArray(energy);
//	}
//
//	public final int[] getEnergyDeviation() {
//		return Convertions.unsignedByteArrayToIntArray(energyDeviation);
//	}
	
//	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}

	public static void setPreCompute(boolean given) {
		preCompute = given;
		
	}
	

	protected static final int Nray				= 128	;		// Num of ray
	protected static final int Nview			= 180	;	// Num of view
	protected static final int NUMofFEATURE		= 62	;
	protected static final int Quant_level		= 255	;
	protected static final int RadialDivision  	= 5		;
	protected static final int RadialDivision2 	= 3		;
	protected static final int AngularDivision 	= 6 	;

	protected static final double wm[]={0.42,1.00,1.00,0.08,1.00};
	protected static final double wd[]={0.32,1.00,1.00,1.00,1.00};

	protected static final double wdc=0.28;
	protected static final double wstd=0.22;

	static final double dcnorm=122.331353;
	static final double stdnorm=51.314701;
	
//	double mmean[RadialDivision][AngularDivision] =
	static final double mmean[][] =	
	{{13.948462, 15.067986, 15.077915, 13.865536, 15.031283, 15.145633},
	 {15.557970, 15.172251, 15.357618, 15.166167, 15.414601, 15.414378},
	 {17.212408, 16.173027, 16.742651, 16.913837, 16.911480, 16.582123},
	 {17.911104, 16.761711, 17.065447, 17.867548, 17.250889, 17.050728},
	 {17.942741, 16.891190, 17.101770, 18.032434, 17.295305, 17.202160}};
	
//	double dmean[RadialDivision][AngularDivision]=
	static final double dmean[][]=		
	{{16.544933, 17.845844, 17.849176, 16.484509, 17.803377, 17.928810},
	 {18.054886, 17.617800, 17.862095, 17.627794, 17.935352, 17.887453},
	 {19.771456, 18.512341, 19.240444, 19.410559, 19.373478, 18.962496},
	 {20.192045, 18.763544, 19.202494, 20.098207, 19.399082, 19.032280},
	 {19.857040, 18.514065, 18.831860, 19.984838, 18.971045, 18.863575}};	
}

