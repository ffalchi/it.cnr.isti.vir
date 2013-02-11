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

public final class EdgeHistogram implements IFeature, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	static boolean useTotalHistogram = true;
	
	static final double[][] quantTable = { 
			  {0.010867,0.057915,0.099526,0.144849,0.195573,0.260504,0.358031,0.530128}, 
			  {0.012266,0.069934,0.125879,0.182307,0.243396,0.314563,0.411728,0.564319},
			  {0.004193,0.025852,0.046860,0.068519,0.093286,0.123490,0.161505,0.228960},
			  {0.004174,0.025924,0.046232,0.067163,0.089655,0.115391,0.151904,0.217745},
			  {0.006778,0.051667,0.108650,0.166257,0.224226,0.285691,0.356375,0.450972},
			};
	
	public double getMaxDistance() {
		return 68;
	}
	
	// compressed version
	final byte[] binCounts;
	
	// extended version
	final float[] totHistogram;
	
	static final byte version = 0;
	
	public boolean equals(Object obj)	 {
		if ( this == obj ) return true;
		EdgeHistogram given = (EdgeHistogram) obj;
		if ( ( binCounts == null || given.binCounts == null ) && binCounts != given.binCounts ) return false;
		if ( ( totHistogram == null || given.totHistogram == null ) && totHistogram != given.totHistogram ) return false;
		
		if ( binCounts != null && binCounts.length != given.binCounts.length ) return false;
		
		if ( totHistogram != null && totHistogram.length != given.totHistogram.length )	return false;
		
		if ( totHistogram != null )
			for (int i=0; i<totHistogram.length; i++) 
				if ( totHistogram[i] != given.totHistogram[i] ) return false;;
		
		if ( binCounts != null )
			for (int i=0; i<binCounts.length; i++) 
				if ( binCounts[i] != given.binCounts[i] ) return false;
		
		return true;
	}
	
	public EdgeHistogram(ByteBuffer str) throws IOException {
		byte version = str.get();
		
		boolean useTotHist = (str.get() != 0);
		if ( useTotHist ) {
			totHistogram = new float[80+70];
			for (int i=0; i<totHistogram.length; i++) totHistogram[i] = str.getFloat();
			
			binCounts = null;
		} else { 
			byte[] binCounts_temp = new byte[80];
			//for (int i=0; i<binCounts_temp.length; i++) binCounts_temp[i] = str.readByte();
			str.get(binCounts_temp);
			if ( useTotalHistogram == true ) {
				totHistogram = expandBins( binCounts_temp );
				binCounts = null;
			} else {
				binCounts = binCounts_temp;
				totHistogram = null;
			}
		}
	}
	
	public EdgeHistogram(DataInput str) throws IOException {
		byte version = str.readByte();
		
		boolean useTotHist = str.readBoolean();
		if ( useTotHist ) {
			totHistogram = new float[80+70];
			for (int i=0; i<totHistogram.length; i++) totHistogram[i] = str.readFloat();
			binCounts = null;
		} else { 
			byte[] binCounts_temp = new byte[80];
			for (int i=0; i<binCounts_temp.length; i++) binCounts_temp[i] = str.readByte();
			if ( useTotalHistogram == true ) {
				totHistogram = expandBins( binCounts_temp );
				binCounts = null;
			} else {
				binCounts = binCounts_temp;
				totHistogram = null;
			}
		}
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		if ( totHistogram != null ) {
			str.writeBoolean(true);
			for (int i=0; i<totHistogram.length; i++) str.writeFloat(totHistogram[i]);
		} else {
			str.writeBoolean(false);
			for (int i=0; i<binCounts.length; i++) str.writeByte(binCounts[i]);
		}
	}
	
	public EdgeHistogram(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		//protected void parse(XMLStreamReader xmlr) throws XMLStreamException {
		byte[] binCounts_temp = null;
		float[] totHistogram_temp = null;
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	            	if (xmlr.getLocalName().equals("BinCounts") ) { 
	            		if ( useTotalHistogram ) {
	            			totHistogram_temp = expandBins( Convertions.stringToUnsignedByteArray(xmlr.getElementText() ) );
	            		} else {
	            			binCounts_temp = Convertions.stringToUnsignedByteArray(xmlr.getElementText() );
	            		}
	            	}
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("VisualDescriptor") ) {
	            		
	            		if (	( totHistogram_temp == null && binCounts_temp == null ) ||
	            				( totHistogram_temp == null && binCounts_temp.length != 80 ) ||
	            				( totHistogram_temp.length != 80+70 )
	            			)
	            			throw new MPEG7VDFormatException("Error reading EdgeHistogram");
	            		binCounts = binCounts_temp;
	            		totHistogram = totHistogram_temp;	            		
	            		return;
	            	}
	            	break;
	    	} // end switch
	    } // end while
		//}
		binCounts = binCounts_temp;
		totHistogram = totHistogram_temp;	
	}
	
	public int hashCode() {
		int tempHashCode = 0;
		
		if ( totHistogram == null ) {
			for (int i=0; i<binCounts.length; i++) tempHashCode = 31 * tempHashCode + binCounts[i];
		} else {
			for (int i=0; i<totHistogram.length; i++) {
				long tempLong = Double.doubleToLongBits( totHistogram[i] );
				tempHashCode =+  (int)(tempLong^(tempLong>>>32));
			}
		}
		
		return tempHashCode;
	}


	
	public final static double mpeg7XMDistance(EdgeHistogram d1, EdgeHistogram d2) {
//		EdgeHistogram d1 = (EdgeHistogram) f1; 
//		EdgeHistogram d2 = (EdgeHistogram) f2;
		
		float[] t1 = d1.totHistogram;
		float[] t2 = d2.totHistogram;
		 
		if ( t1 == null ) t1 = expandBins(d1.binCounts);
		if ( t2 == null ) t2 = expandBins(d2.binCounts);
		
		return mpeg7XMDistance_TotalH(t1, t2);
	}
	
	public static final double mpeg7XMDistance_TotalH(float[] tH1, float[] tH2) {
			int	i;
			double	dist, dTemp;
			
			dist = 0.0;
			for(i=0; i < 80+70; i++){
			  // Global(5)+Semi_Global(65)
			  dTemp= (tH1[i] - tH2[i]);
			  if (dTemp < 0.0) dTemp = -dTemp;
			  dist += dTemp;
			}

			return dist;
	}
/*	
	public final double mpeg7XMDistancebins(EdgeHistogram givenD) {
		
			double[] Total_EdgeHist_Ref = new double[150];
			// Local(80)+ Global(5)+Semi_Global(65)
			double[] Total_EdgeHist_Query = new double[150];
			
			double[] Local_EdgeHist = new double[80];
			double[] Local_EdgeHist_Query= new double[80];
			

			int	i;
			double	dist, dTemp;

			// to use XM distance function
			for (i=0; i<80; i++)
			{
				Local_EdgeHist[i]		= quantTable[i%5][binCounts[i]];
				Local_EdgeHist_Query[i]	= quantTable[i%5][givenD.binCounts[i]];
			}

			EHD_Make_Global_SemiGlobal(Local_EdgeHist,  Total_EdgeHist_Ref);
			EHD_Make_Global_SemiGlobal(Local_EdgeHist_Query,  Total_EdgeHist_Query);
			
			dist = 0.0;
			for(i=0; i < 80+70; i++){
			  // Global(5)+Semi_Global(65)
			  dTemp= (Total_EdgeHist_Ref[i] - Total_EdgeHist_Query[i]);
			  if (dTemp < 0.0) dTemp = -dTemp;
			  dist += dTemp;
			}

			return dist;
	}
*/
	
	public String toString() {
		String str = "EdgeHistogram";
		if ( binCounts != null ) {
			str += "  BinCounts:";
			for (int i=0; i<binCounts.length; i++ ){
				str += " " + Convertions.unsignedByteToInt(binCounts[i]);
			}
		} else if ( totHistogram != null ) {
			str += "  totalHistogram:";
			for (int i=0; i<totHistogram.length; i++ ){
				str += " " + totHistogram[i];
			}
		}
		return str + "\n";
	}
	
	public boolean equals(IFeature givenVD) {
		
		if ( this.getClass() != givenVD.getClass() ) return false;
		
		EdgeHistogram vd = (EdgeHistogram) givenVD;

		for (int i=0; i<binCounts.length; i++ ){
			if ( binCounts[i] != vd.binCounts[i] ) return false;
		}


		return true;
	}
	
	
	void EHD_Make_Global_SemiGlobal( double[] LocalHistogramOnly, double[] TotalHistogram )
	{
		int i, j;	

		//memcpy( TotalHistogram + 5, LocalHistogramOnly, 80*sizeof(double) );
		for (int l=0; l<80; l++) {
			TotalHistogram[l+5] = LocalHistogramOnly[l];
		}
		
		
		//	 Make Global Histogram Start
		for(i=0; i<5; i++)
		  TotalHistogram[i]=0.0;
		for( j=0; j < 80; j+=5) {
		  for( i=0; i < 5; i++) {
			TotalHistogram[i] += TotalHistogram[5+i+j]; 
		  }
		}  // for( j ) 
		for(i=0; i<5; i++)
		//	 Global *5.
			TotalHistogram[i] = TotalHistogram[i]*5/16.0;

		//	 Make Global Histogram end
		
		
		//	 Make Semi-Global Histogram start
		for(i=85; i <105; i++) {
			j = i-85;
			TotalHistogram[i] =
				(TotalHistogram[5+j]
				+TotalHistogram[5+20+j]
				+TotalHistogram[5+40+j]
				+TotalHistogram[5+60+j])/4.0;
		}
		for(i=105; i < 125; i++) {
			j = i-105;
			TotalHistogram[i] =
				(TotalHistogram[5+20*(j/5)+j%5]
				+TotalHistogram[5+20*(j/5)+j%5+5]
				+TotalHistogram[5+20*(j/5)+j%5+10]
				+TotalHistogram[5+20*(j/5)+j%5+15])/4.0;
		}
		
		//////////////////////////////////////////////////////	/
		//					4 area Semi-Global
		//////////////////////////////////////////////////////	/
		//	  Upper area 2.
		for(i=125; i < 135; i++) {
			j = i-125;    // j = 0 ~ 9
			TotalHistogram[i] =
				(TotalHistogram[5+10*(j/5)+0+j%5]
					   +TotalHistogram[5+10*(j/5)+5+j%5]
					   +TotalHistogram[5+10*(j/5)+20+j%5]
					   +TotalHistogram[5+10*(j/5)+25+j%5])/4.0;
		}
		//	  Down area 2.
		for(i=135; i < 145; i++) {
			j = i-135;    // j = 0 ~ 9
			TotalHistogram[i] =
				(TotalHistogram[5+10*(j/5)+40+j%5]
					   +TotalHistogram[5+10*(j/5)+45+j%5]
					   +TotalHistogram[5+10*(j/5)+60+j%5]
					   +TotalHistogram[5+10*(j/5)+65+j%5])/4.0;
		}
		//	 Center Area 1 
		for(i=145; i < 150; i++) {
			j = i-145;    // j = 0 ~ 9
			TotalHistogram[i] =
				(TotalHistogram[5+25+j%5]
					   +TotalHistogram[5+30+j%5]
					   +TotalHistogram[5+45+j%5]
					   +TotalHistogram[5+50+j%5])/4.0;
		}
		//	 Make Semi-Global Histogram end

	} /* EHD_Make_Global_SemiGlobal */
	
	private static float[] expandBins( byte[] bins )
	{
		int i, j;
		
		float[] tH = new float[150];
		
		//memcpy( TotalHistogram + 5, LocalHistogramOnly, 80*sizeof(double) );
		for (int l=0; l<80; l++) {
			tH[l+5] = (float) quantTable[l%5][Convertions.unsignedByteToInt(bins[l])];
		}
		
		
		//	 Make Global Histogram Start
		for(i=0; i<5; i++)
			tH[i]= (float) 0.0;
		for( j=0; j < 80; j+=5) {
		  for( i=0; i < 5; i++) {
			  tH[i] += tH[5+i+j]; 
		  }
		}  // for( j ) 
		for(i=0; i<5; i++)
		//	 Global *5.
			tH[i] = (float) (tH[i]*5/16.0);

		//	 Make Global Histogram end
		
		
		//	 Make Semi-Global Histogram start
		for(i=85; i <105; i++) {
			j = i-85;
			tH[i] = (float) 
				((tH[5+j]
				+tH[5+20+j]
				+tH[5+40+j]
				+tH[5+60+j])/4.0);
		}
		for(i=105; i < 125; i++) {
			j = i-105;
			tH[i] = (float) 
				((tH[5+20*(j/5)+j%5]
				+tH[5+20*(j/5)+j%5+5]
				+tH[5+20*(j/5)+j%5+10]
				+tH[5+20*(j/5)+j%5+15])/4.0);
		}
		
		//////////////////////////////////////////////////////	/
		//					4 area Semi-Global
		//////////////////////////////////////////////////////	/
		//	  Upper area 2.
		for(i=125; i < 135; i++) {
			j = i-125;    // j = 0 ~ 9
			tH[i] = (float) 
				((tH[5+10*(j/5)+0+j%5]
					   +tH[5+10*(j/5)+5+j%5]
					   +tH[5+10*(j/5)+20+j%5]
					   +tH[5+10*(j/5)+25+j%5])/4.0);
		}
		//	  Down area 2.
		for(i=135; i < 145; i++) {
			j = i-135;    // j = 0 ~ 9
			tH[i] = (float) 
				((tH[5+10*(j/5)+40+j%5]
					   +tH[5+10*(j/5)+45+j%5]
					   +tH[5+10*(j/5)+60+j%5]
					   +tH[5+10*(j/5)+65+j%5])/4.0);
		}
		//	 Center Area 1 
		for(i=145; i < 150; i++) {
			j = i-145;    // j = 0 ~ 9
			tH[i] = (float) 
				((tH[5+25+j%5]
					   +tH[5+30+j%5]
					   +tH[5+45+j%5]
					   +tH[5+50+j%5])/4.0);
		}
		//	 Make Semi-Global Histogram end
		
		return tH;
	} /* EHD_Make_Global_SemiGlobal */
	
//	@Override
//	public final IFeatureCollector getFeature(Class featureClass) {
//		if ( featureClass.equals(this.getClass()) ) return this;
//		return null;
//	}
	
}
