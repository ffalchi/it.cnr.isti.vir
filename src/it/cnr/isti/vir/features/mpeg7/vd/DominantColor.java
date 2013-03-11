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
import it.cnr.isti.vir.util.Conversions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class DominantColor implements IFeature {

	// RGB
	public static final int numOfBins = 256;
	
	public static final int DCD_SPATIALCOHERENCY_UNINIT = 1023;
	public static final int  DCD_COLORVARIANCE_UNINIT = 1023;
	public static final int  DCD_PERCENTAGE_UNINT = 1023;
	
	private byte spatialCoherency;	// [0-31]
	
	static final byte version = 0;
	
	boolean colorVarianceUsed = true;

	private static final long serialVersionUID = 1L;

	/* Actually they are unsigned
	 * Should be readed using 
	 * (int) byte[i]
	 */
	List<Value> values;	
	
	private class Value {
		
		public byte percentage;			// [0-31]
		public byte colorVariance[] = null;
		private byte index[];
		
/*		public Value(byte percentage, byte[] colorVariance,
				byte[] index) {
			super();
			this.percentage = percentage;
			this.colorVariance = colorVariance;
			this.index = index;
		}
		*/
		public int getIndex(int i) {
			return Conversions.unsignedByteToInt(index[i]);
		}
		
		public boolean equals(Value givenValue) {
			
			if ( this.percentage != givenValue.percentage ) return false;
			if ( this.colorVariance != givenValue.colorVariance ) return false;
			if ( index.length != givenValue.index.length ) return false;
			
			for ( int i=0; i<index.length; i++) {
				if ( index[i] != givenValue.index[i] ) return false;
			}

			return true;
		}
		
		public Value(DataInput str) throws IOException {
			percentage = str.readByte();
			colorVariance = new byte[str.readByte()];
			for ( int i=0; i<colorVariance.length; i++) {
				colorVariance[i] = str.readByte(  );
			}			
			
			index = new byte[str.readByte()];
			for ( int i=0; i<index.length; i++) {
				index[i] = str.readByte(  );
			}
		}
		
		public void writeData(DataOutput str) throws IOException {
			str.writeByte(percentage);
			
			str.writeByte(colorVariance.length);
			for ( int i=0; i<colorVariance.length; i++) {
				str.writeByte( colorVariance[i] );
			}			
			
			str.writeInt(index.length);
			for ( int i=0; i<index.length; i++) {
				str.writeByte( index[i] );
			}		

		}

		public int dataHashCode() {
			int tempHashCode = 0;
			
			for (int i=0; i<colorVariance.length; i++) tempHashCode = 31 * tempHashCode + (int) colorVariance[i];
			for (int i=0; i<index.length; i++) tempHashCode = 31 * tempHashCode + (int) index[i];
			tempHashCode = 31 * (int) percentage;
			
			return tempHashCode;
		}
		
		public String toString() {
			String temp = "";
			
			temp += "  Values:";
			if ( index != null ) 
				for (int i=0; i<index.length; i++) temp += " " + getIndex(i);
			if ( colorVariance != null ) {
				temp += "\t ColorVariance:";
				for (int i=0; i<colorVariance.length; i++) temp += " " +colorVariance[i];
			}
			temp += "\t Percentage: " + percentage;
			temp += "\n";		
			
			return temp;
		}
		
		protected void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
			for (int event = xmlr.next();  
		    	event != XMLStreamConstants.END_DOCUMENT;
		    	event = xmlr.next()) {
		    	switch (event) {
		        	case XMLStreamConstants.START_ELEMENT:
		            	if (xmlr.getLocalName().equals("ColorVariance") ) { 
		            		colorVariance = Conversions.stringToUnsignedByteArray( xmlr.getElementText() );
		            	} else {
		            		if (xmlr.getLocalName().equals("Index") ) { 
		            			index = Conversions.stringToUnsignedByteArray( xmlr.getElementText() );
		            		} else {
			            		if (xmlr.getLocalName().equals("Percentage") ) { 
			            			percentage = (byte) Integer.parseInt( xmlr.getElementText() );
			            		}
			            	}
		            	}
		            	break;		            	
		            case XMLStreamConstants.END_ELEMENT:
		            	if (xmlr.getLocalName().equals("Value") ) { 
		            		return;
		            	}
		            	break;
		    	} // end switch
		    } // end while
			
			if ( colorVarianceUsed && (colorVariance == null) ) throw new MPEG7VDFormatException("Error parsing DominantColor");
		}		
		
				// we should be inside a tag "Value"
		public Value(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
			parse(xmlr);
		}

		public Value(int r, int g, int b) {
			percentage = 31;
			index = new byte[3];
			index[0] = (byte) r;
			index[1] = (byte) g;
			index[2] = (byte) b;
			
			colorVariance = new byte[3];
			colorVariance[0] = 0;
			colorVariance[1] = 0;
			colorVariance[2] = 0;
			
		}

	}
	
	public double getMaxDistance() {
		return 8000;
	}
	
		
	//static final int nOfValues = 64;	
	
	public int hashCode() {
		int tempHashCode = 0;
		
		tempHashCode = 31 * tempHashCode + spatialCoherency;
		
		for ( Iterator<Value> it = this.values.iterator(); it.hasNext(); ) {
			tempHashCode = 31 * tempHashCode + it.next().dataHashCode();
		}
		
		return tempHashCode;
	}
	
	public DominantColor(int r, int g, int b, int spatialCoherency) {
		super();
		this.spatialCoherency = (byte) spatialCoherency;
		colorVarianceUsed = true;
		values = new LinkedList<Value>();
		values.add( new Value(r,g,b) );
	}
	
	public DominantColor(int r, int g, int b) {
		// NB SpatialCoherency = 255 is used for artificial images not using SpatialCoherency
		this(r,g,b,255);	
	}
	
	public DominantColor(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		parse(xmlr);
	}

	public void parse(XMLStreamReader xmlr) throws XMLStreamException, MPEG7VDFormatException {
		values = new LinkedList<Value>();
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:
	            	if (xmlr.getLocalName().equals("SpatialCoherency") ) { 
	            		spatialCoherency = (byte) Integer.parseInt( xmlr.getElementText() );
	            	
	            	} else {
	            		if (xmlr.getLocalName().equals("Value") ) { 
	            			values.add( new Value(xmlr) );
		            	} 
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
	
	public final static double mpeg7XMDistance(IFeature featureInterface, IFeature featureInterface2 ) {
		return mpeg7XMDistance((DominantColor) featureInterface, (DominantColor) featureInterface2, true);
	}
	
	
	public final static  double mpeg7XMDistance(DominantColor d1, DominantColor d2, boolean useSpatialcoher) {
//				
//		DominantColor d1 = (DominantColor) f1;
//		DominantColor d2 = (DominantColor) f2;

		boolean spatialcoher = useSpatialcoher;
		
		// ARTIFICIAL DISTANCE FABRIZIO FALCHI
		// NB SpatialCoherency = -1 is used for artificial images not using SpatialCoherency
		if ( d1.spatialCoherency == -1 || d2.spatialCoherency == -1 ) {
			spatialcoher = false;
			
			// FALCHI workaround
			DominantColor dd0 = null;
			DominantColor dd1  = null;
			if ( d1.spatialCoherency == -1 ) {
				dd0 = d1;
				dd1 = d2;
			} else {
				dd0 = d2;
				dd1 = d1;
			}
			
			double dist = 0;
			
			int[] rgb0 = new int[3];
			int[] rgb1 = new int[3];
			rgb0[0] = dd0.values.get(0).getIndex(0) * 256 / DominantColor.numOfBins;
			rgb0[1] = dd0.values.get(0).getIndex(1) * 256 / DominantColor.numOfBins;
			rgb0[2] = dd0.values.get(0).getIndex(2) * 256 / DominantColor.numOfBins;
			
			double[] luv0 = new double[3];
			double[] luv1 = new double[3];
			
			rgb2luv( rgb0, luv0, 1);
			//System.out.print("LUV: " + luv0[0] + " " + luv0[1] + " " + luv0[2] +"\n");

			double totPerc = 0;
			for (Iterator<Value> it = dd1.values.iterator(); it.hasNext();  ) {
				Value val= it.next();
				rgb1[0] = val.getIndex(0) * 256 / DominantColor.numOfBins;
				rgb1[1] = val.getIndex(1) * 256 / DominantColor.numOfBins;
				rgb1[2] = val.getIndex(2) * 256 / DominantColor.numOfBins;		
				rgb2luv(rgb1, luv1, 1);
				System.out.print("LUV: " + luv1[0] + " " + luv1[1] + " " + luv1[2] +"\n");
				dist += ( (val.percentage + 0.5) / 31.999) * //
						java.lang.Math.sqrt //
						(		0.05 * // Luminance weight
								java.lang.Math.pow( (luv1[0] - luv0[0])/256, 2) + 		//
								java.lang.Math.pow( (luv1[1] - luv0[1])/256, 2) + 		//
								java.lang.Math.pow( (luv1[2] - luv0[2])/256, 2) 		//	 
						) //
						/2.01;
				totPerc += ( (val.percentage + 0.5) / 31.999);
			}			
			
			return dist / totPerc;
			
		}
		// END OF ARTIFICIAL DISTANCE
		
		int i, j, N1, N2;
		int sc1, sc2; //added by LG CIT
		double color1_float[][], color2_float[][], vars1[][], vars2[][];
		double per1_float[], per2_float[];
		double d, dist=0.0, dmax, Td, total;
		boolean varpres1, varpres2;
	
		double VAR_RECL=60.0;
		//constants
		double VAR_RECH=90.0;	
		int Td2=255;
	
		varpres1 = d1.colorVarianceUsed;
		varpres2 = d2.colorVarianceUsed;
		
		N1 = d1.values.size();
		N2 = d2.values.size();
		
		int icnts1[][]= new int[N1][3];
		for(i=0; i < N1; i++)
		{
			icnts1[i] = new int[3];
			for (j=0; j<3; j++)
				icnts1[i][j] = d1.values.get(i).getIndex(j) * 256 / DominantColor.numOfBins;
		}
	
		int icnts2[][]= new int[N2][3];
		for(i=0; i < N2; i++)
		{
			icnts2[i] = new int[3];
			for (j=0; j<3; j++)
				icnts2[i][j] = d2.values.get(i).getIndex(j) * 256 / DominantColor.numOfBins;
		}
	
		per1_float = new double[N1];
		color1_float = new double[N1][3];
	
		per2_float = new double[N2];
		color2_float = new double[N2][3];

		
		for (i=0,total=0.0;i<N1; i++) {
			rgb2luv(icnts1[i], color1_float[i], 3);
			per1_float[i] = ( d1.values.get(i).percentage + 0.5) / 31.9999;
			total += per1_float[i];
		}
		for( i=0; i<N1; i++ )
			per1_float[i] /= total;
		
		for (i=0,total=0.0;i<N2;i++) {
			rgb2luv(icnts2[i], color2_float[i], 3);
			per2_float[i] = ( d2.values.get(i).percentage + 0.5) / 31.9999;
			total += per2_float[i];
		}
		for (i=0;i<N2;i++)
			per2_float[i] /= total;
	
		if( varpres1 && varpres2 ) {
			vars1 = new double[N1][3];
			for(i=0; i<N1; ++i) {
				//vars1[i] = new double[3];
				for (j=0; j<3; j++)
					vars1[i][j] = (d1.values.get(i).colorVariance[j] > 0)?VAR_RECH:VAR_RECL;
			}
			vars2 = new double[N2][3];
			for(i=0; i<N2; ++i) {
				//vars2[i] = new double[3];
				for (j=0; j<3; j++)
					vars2[i][j] = (d2.values.get(i).colorVariance[j] > 0)?VAR_RECH:VAR_RECL;
			}
	
			dist = GetDistanceVariance	(
											per1_float, color1_float, vars1, N1,
											per2_float, color2_float, vars2, N2
										);
	
/*			for(i=0;i<N1;i++)
				delete [] vars1[i];
			delete [] vars1;
			for(i=0;i<N2;i++)
				delete [] vars2[i];
			delete [] vars2;*/
		}
		else {
			Td = java.lang.Math.sqrt(Td2);
	
			dmax = 1.2*Td;
			
			for( i=0; i<N1; i++ )
				dist += java.lang.Math.pow(per1_float[i],2);
			for( i=0; i<N2; i++ ) 
				dist += java.lang.Math.pow(per2_float[i],2);
			for( i=0; i<N1; i++ ) {
				for( j=0; j<N2; j++ ) {
					d = java.lang.Math.sqrt(
								java.lang.Math.pow(color1_float[i][0]-color2_float[j][0],2) +
								java.lang.Math.pow(color1_float[i][1]-color2_float[j][1],2) +
								java.lang.Math.pow(color1_float[i][2]-color2_float[j][2],2)
							);
					if( d < Td ) {
						dist -= 2*(1-d/dmax)*per1_float[i]*per2_float[j];
					}
				}
			}
			dist = java.lang.Math.sqrt( java.lang.Math.abs(dist) );
		}
		
		/*delete [] per1_float;
		for(i=0;i<N1;i++)
			delete [] color1_float[i];
		delete [] color1_float;
		delete [] per2_float;
		for(i=0;i<N2;i++)
			delete [] color2_float[i];
		delete [] color2_float;
		
		for(i=0;i<N1;++i)
			delete [] icnts1[i];
		delete [] icnts1;
		for(i=0;i<N2;++i)
			delete [] icnts2[i];
		delete [] icnts2;*/
	
		if(!spatialcoher) sc1 = sc2 = DCD_SPATIALCOHERENCY_UNINIT;
		else
		{
			sc1 = d1.spatialCoherency;
			sc2 = d2.spatialCoherency;
		}
	
		if (	sc1 != DCD_SPATIALCOHERENCY_UNINIT &&
				sc2 != DCD_SPATIALCOHERENCY_UNINIT )
		{
			double fsc1= (sc1)/31.0, fsc2=(sc2)/31.0;
			dist = 0.3 * dist * java.lang.Math.abs(fsc1 - fsc2) + 0.7 * dist;
		}
		
		if (dist<0) dist=0;
		
		// FOR PRECISION PROBLEMS
		if ( dist<(1/10000000000.0 )) dist=0;
	
		//return dist;
		return (100000000.0 * dist);
	}
	
	// Falchi
/*	public void rgb2luv(int R, int G, int B, double []luv) {
		//http://www.brucelindbloom.com
		
		float rf, gf, bf;
		float r, g, b, X_, Y_, Z_, X, Y, Z, fx, fy, fz, xr, yr, zr;
		float L;
		float eps = 216.f/24389.f;
		float k = 24389.f/27.f;
		 
		float Xr = 0.964221f;  // reference white D50
		float Yr = 1.0f;
		float Zr = 0.825211f;
		 
		// RGB to XYZ
		 
		r = R/255.f; //R 0..1
		g = G/255.f; //G 0..1
		b = B/255.f; //B 0..1
		   
		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r/12;
		else
			r = (float) Math.pow((r+0.055)/1.055,2.4);
		
		if (g <= 0.04045)
			g = g/12;
		else
			g = (float) Math.pow((g+0.055)/1.055,2.4);
		 
		if (b <= 0.04045)
			b = b/12;
		else
			b = (float) Math.pow((b+0.055)/1.055,2.4);
		
		
		X =  0.436052025f*r     + 0.385081593f*g + 0.143087414f *b;
		Y =  0.222491598f*r     + 0.71688606f *g + 0.060621486f *b;
		Z =  0.013929122f*r     + 0.097097002f*g + 0.71418547f  *b;
		
		// XYZ to Luv
		
		float u, v, u_, v_, ur_, vr_;
						
		u_ = 4*X / (X + 15*Y + 3*Z);
		v_ = 9*Y / (X + 15*Y + 3*Z);
				 
		ur_ = 4*Xr / (Xr + 15*Yr + 3*Zr);
		vr_ = 9*Yr / (Xr + 15*Yr + 3*Zr);
		  
		yr = Y/Yr;
		
		if ( yr > eps )
			L =  (float) (116*Math.pow(yr, 1/3.) - 16);
		else
			L = k * yr;
		 
		u = 13*L*(u_ -ur_);
		v = 13*L*(v_ -vr_);
		
		//luv[0] = (int) (2.55*L + .5);
		//luv[1] = (int) (u + .5); 
		//luv[2] = (int) (v + .5);    
		
		//F.Falchi
		luv[0] = 2.55*L;
		luv[1] = u; 
		luv[2] = v;
	} */
	
	
	//--XM Software--------------------------------------------------------------------------
	private final static void rgb2luv(int RGB[], double LUV[], int size)
	{
	  int i;
	  double x,y,X,Y,Z,den,u2,v2,X0,Z0,Y0,u20,v20,r,g,b;

	  X0 = (0.607+0.174+0.201);
	  Y0 = (0.299+0.587+0.114);
	  Z0 = (      0.066+1.117);

	/* Y0 = 1.0 */
	  u20 = 4*X0/(X0+15*Y0+3*Z0);
	  v20 = 9*Y0/(X0+15*Y0+3*Z0);

	  for (i=0;i<size;i+=3)
	  {
	    if (RGB[i]<=20)  r=(double) (8.715e-4*RGB[i]);
	    else r=(double) java.lang.Math.pow((RGB[i]+25.245)/280.245, 2.22);

	    if (RGB[i+1]<=20)  g=(double) (8.715e-4*RGB[i+1]);
	    else g=(double) java.lang.Math.pow((RGB[i+1]+25.245)/280.245, 2.22);

	    if (RGB[i+2]<=20)  b=(double) (8.715e-4*RGB[i+2]);
	    else b=(double) java.lang.Math.pow((RGB[i+2]+25.245)/280.245, 2.22);

	    X = 0.412453*r + 0.357580*g + 0.180423*b;
	    Y = 0.212671*r + 0.715160*g + 0.072169*b;
	    Z = 0.019334*r + 0.119193*g + 0.950227*b;

	    if (X==0.0 && Y==0.0 && Z==0.0)
	    {
	      x=1.0/3.0; y=1.0/3.0;
	    }
	    else
	    {
	      den=X+Y+Z;
	      x=X/den; y=Y/den;
	    }

	    den=-2*x+12*y+3;
	    u2=4*x/den;
	    v2=9*y/den;

	    if (Y>0.008856) LUV[i] = (float) (116*java.lang.Math.pow(Y,1.0/3.0)-16);
	    else LUV[i] = (float) (903.3*Y);
	    LUV[i+1] = (float) (13*LUV[i]*(u2-u20));
	    LUV[i+2] = (float) (13*LUV[i]*(v2-v20));
	  }
	} /* rgb2luv */
	
	private final static double GetDistanceVariance(
	  double per1[], double color1[][], double var1[][], int size1,
	  double per2[], double color2[][], double var2[][], int size2)
	{
		//constant
		double twopi=2.0*3.14159265358979323846;
	
	  int     i1, i2;
	  double  d0, d1, d2, v0, v1, v2, arg1, arg2;
	  double  tmp, val=0.0;
	
	  /* the overall formula is:
	     Integral of ( sum_ij f_i*f_j + sum_ij g_*g_j - 2*sum_ij f_j*g_j ) */
	
	  /* loop for f_i*f_j */
	  for( i1=0; i1<size1; i1++ ) {
	    for( i2=0; i2<size1; i2++ ) {
	      d0 = color1[i1][0] - color1[i2][0];  v0 = var1[i1][0] + var1[i2][0];
	      d1 = color1[i1][1] - color1[i2][1];  v1 = var1[i1][1] + var1[i2][1];
	      d2 = color1[i1][2] - color1[i2][2];  v2 = var1[i1][2] + var1[i2][2];
	      arg1 = (d0*d0/v0 + d1*d1/v1 + d2*d2/v2)/2.0;
	      arg2 = twopi*java.lang.Math.sqrt(twopi*v0*v1*v2);
	      tmp = per1[i1]*per1[i2]*java.lang.Math.exp(-arg1)/arg2;
	      val += tmp;
	    }
	  }
	
	  /* loop for g_i*g_j */
	  for( i1=0; i1<size2; i1++ ) {
	    for( i2=0; i2<size2; i2++ ) {
	      d0 = color2[i1][0] - color2[i2][0];  v0 = var2[i1][0] + var2[i2][0];
	      d1 = color2[i1][1] - color2[i2][1];  v1 = var2[i1][1] + var2[i2][1];
	      d2 = color2[i1][2] - color2[i2][2];  v2 = var2[i1][2] + var2[i2][2];
	      arg1 = (d0*d0/v0 + d1*d1/v1 + d2*d2/v2)/2.0;
	      arg2 = twopi*java.lang.Math.sqrt(twopi*v0*v1*v2);
	      tmp = per2[i1]*per2[i2]*java.lang.Math.exp(-arg1)/arg2;
	      val += tmp;
	    }
	  }
	
	  /* loop for f_i*g_j */
	  for( i1=0; i1<size1; i1++ ) {
	    for( i2=0; i2<size2; i2++ ) {
	      d0 = color1[i1][0] - color2[i2][0];  v0 = var1[i1][0] + var2[i2][0];
	      d1 = color1[i1][1] - color2[i2][1];  v1 = var1[i1][1] + var2[i2][1];
	      d2 = color1[i1][2] - color2[i2][2];  v2 = var1[i1][2] + var2[i2][2];
	      arg1 = (d0*d0/v0 + d1*d1/v1 + d2*d2/v2)/2.0;
	      arg2 = twopi*java.lang.Math.sqrt(twopi*v0*v1*v2);
	      tmp = per1[i1]*per2[i2]*java.lang.Math.exp(-arg1)/arg2;
	      val -= 2.0*tmp;
	    }
	  }
	
	  return val;
	
	} /* GetDistanceVariance */		
	
	
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
//            		values = Utilities.stringToUnsignedByteArray( text );
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
		String temp = "DominantColor\n  SpatialCoherency: ";
		//temp += it.cnr.isti.mpeg7.utilities.Utilities.unsignedByteToInt(spatialCoherency);
		temp += spatialCoherency;
		for ( Iterator<Value> it = this.values.iterator(); it.hasNext(); ) {
			temp += it.next();
		}
		return temp;
	}
	
	public boolean equals(Object obj) {
		if ( this == obj  ) return true;
		
		DominantColor vd = (DominantColor) obj;

		if ( this.spatialCoherency != vd.spatialCoherency ) return false;
		
		if ( this.values.size() != vd.values.size() ) return false;
		
		Iterator<Value> it2 = vd.values.iterator();
		for ( Iterator<Value> it = this.values.iterator(); it.hasNext(); ) {
			if ( ! it.next().equals( it2.next() ) ) return false;
		}

		return true;
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeByte(version);
		str.writeByte(spatialCoherency);
		str.writeShort(this.values.size());
		
		Iterator<Value> it2 = values.iterator();
		for ( Iterator<Value> it = this.values.iterator(); it.hasNext(); ) {
			it.next().writeData(str);
		}	
	}
	
	public DominantColor(DataInput str) throws IOException {
		str.readByte();
		spatialCoherency = str.readByte();
		LinkedList<Value> values = new LinkedList<Value>();
		int valueSize = str.readShort();
		
		for ( int i=0; i<valueSize; i++ ) {
			values.add(new Value(str));
		}	
	}
	
	
}
