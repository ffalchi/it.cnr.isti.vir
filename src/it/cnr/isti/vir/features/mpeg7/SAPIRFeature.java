package it.cnr.isti.vir.features.mpeg7;

import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.similarity.metric.SAPIRMetric;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SAPIRFeature implements IFeature {

	byte version = 0;
		
	public float[] l1Values = new float[l1ValuesLength];
	//private float[][] l2Values = new float[3][];
	
	//private static final int l2ValuesSize0 = 3;
	private static final int l2ValuesSize1 = 6;
	private static final int l2ValuesSize2 = 3;
	private static final int l2ValuesSize3 = 3;
	
	private static final int l1ValuesN_EH = 150;
	private static final int l1ValuesN_CS = 64;
	private static final int l1ValuesN_HT = 2+HomogeneousTexture.RadialDivision*HomogeneousTexture.AngularDivision*2;
	private static final int l1ValuesN_SC = 64;	
	private static final int l1ValuesN_CL = l2ValuesSize1 + l2ValuesSize2 + l2ValuesSize3;	
	private static final int l1ValuesLength = l1ValuesN_EH + l1ValuesN_CS + l1ValuesN_HT + l1ValuesN_SC + l1ValuesN_CL ;
	

	private static final int clStart = l1ValuesLength - l2ValuesSize1 - l2ValuesSize2 - l2ValuesSize3;
	private static float wCL = (float) SAPIRMetric.wSAPIR[0];
	private static float wCS = (float) SAPIRMetric.wSAPIR[1];
	private static float wEH = (float) SAPIRMetric.wSAPIR[2];
	private static float wHT = (float) SAPIRMetric.wSAPIR[3];
	private static float wSC = (float) SAPIRMetric.wSAPIR[4];
	
	public SAPIRFeature(float[] l1Values) {
		this.l1Values = l1Values;
	}
	
	public SAPIRFeature(SAPIRObject orig) {


		int offset=0;
		
		// EH
  		float[] ehValues = orig.eh.getTotalH();
		for ( int i=0; i<150; i++ ) {
			l1Values[offset++] = wEH * ehValues[i];
		}
	
		// CS
		byte[] csBytes = orig.cs.values;
		for ( int i=0; i<64; i++ ) {
			l1Values[offset++] = wCS * csBytes[i];
		}

		// SC
		short[] scCoeff = orig.sc.coeff;
		for ( int i=0; i<64; i++ ) {
			l1Values[offset++] = wSC * scCoeff[i];
		}

		// HT
		int newOffset = orig.ht.putMPEG7XMDistance_L1Values(l1Values, offset);
		for ( int i=offset; i<newOffset; i++ ) {
			l1Values[i] = wHT * l1Values[i];
		}
		offset = newOffset;

		newOffset = orig.cl.putMPEG7XMDistanceL2Values(l1Values, offset);
		for ( int i=offset; i<newOffset; i++ ) {
			l1Values[i] = wCL * l1Values[i];
		}
		offset = newOffset;
		//CL
		/*
		l2Values[0] = new float[6];
		l2Values[1] = new float[3];
		l2Values[2] = new float[3];
			
  		orig.cl.putMPEG7XMDistanceL2Values(l2Values, 0);
		for ( int i=0; i<l2Values.length; i++) {
			for ( int j=0; j<l2Values[i].length; j++) {
				l2Values[i][j] = wCL * l2Values[i][j];
			}
		}
		*/

		
	}

	public int compareTo(IFeature given) {
		SAPIRFeature that = (SAPIRFeature) given;
		for ( int i=0; i<l1Values.length; i++) {
			int tRes = Float.compare(this.l1Values[i], that.l1Values[i]);
			if ( tRes != 0 ) return tRes;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object other) {
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof SAPIRFeature))return false;
		return compareTo((SAPIRFeature) other) == 0;
	}

	
	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		for ( int i=0; i<l1Values.length; i++) {
			out.writeFloat(l1Values[i]);
		}

	}
	
	public SAPIRFeature(DataInput in) throws IOException {
		byte version = in.readByte();
		for ( int i=0; i<l1Values.length; i++) {
			l1Values[i] = in.readFloat();
		}

	}
	
	public SAPIRFeature(ByteBuffer in) throws IOException {
		byte version = in.get();
		for ( int i=0; i<l1Values.length; i++) {
			l1Values[i] = in.getFloat();
		}
		
	}
	
	public static double mpeg7XMDistance(SAPIRFeature o1, SAPIRFeature o2) {
		double dist = 0;
		
		int i;
	    for ( i = 0; i < clStart; i++) {
	    	dist += Math.abs( (double) o1.l1Values[i] - o2.l1Values[i]);
	    }		
			
	    double acc = 0;
	    for ( ; i < clStart + 6; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);
	    
	    acc = 0;
	    for ( ; i < clStart + 6 + 3; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);
	    
	    acc = 0;
	    for ( ; i < clStart + 6 + 3 + 3; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);
		
		return dist;
	}
	
	public static double mpeg7XMDistance(SAPIRFeature o1, SAPIRFeature o2, double maxDistance) {
		double dist = 0;
				
		int i;
	    for ( i = 0; i < clStart; i++) {
	    	dist += Math.abs( (double)  o1.l1Values[i] - o2.l1Values[i]);
	    }		
	    
	    if ( dist > maxDistance ) return -dist;
	    
	    double acc = 0;
	    for ( ; i < clStart + 6; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);	    
	    if ( dist > maxDistance ) return -dist;
	    
	    acc = 0;
	    for ( ; i < clStart + 6 + 3; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);	    
	    if ( dist > maxDistance ) return -dist;
	    
	    acc = 0;
	    for ( ; i < clStart + 6 +3 +3; i++) {
	    	double diff = o1.l1Values[i] - o2.l1Values[i];
	    	acc += diff * diff;
	    }
	    dist += Math.sqrt(acc);
		
		return dist;
	}
	


	
}
