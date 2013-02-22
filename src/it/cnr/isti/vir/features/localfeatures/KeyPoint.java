package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.util.FloatByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class KeyPoint {

	private final float[] xy;
	private final float scale;
	private final float ori;
	
	private float[] normxy; 
	
	public final float[] getXY() {
		return xy;
	}

	public final float getScale() {
		return scale;
	}

	public final float getOri() {
		return ori;
	}
	
	static final public int byteSize = 4*4 ;
	
	public KeyPoint(float x, float y, float ori, float scale ) {
		xy = new float[2];
		xy[0] = x;
		xy[1] = y;
		this.ori = ori;
		this.scale = scale;
	}
	
	public KeyPoint(float[] xy, float ori, float scale) {
		this.xy = xy;
		this.ori = ori;
		this.scale = scale;
	}
	
	
	public KeyPoint(DataInput in) throws IOException {
		xy = new float[2];
		
		xy[0] = in.readFloat();
		xy[1] = in.readFloat();
		ori = in.readFloat();
		scale = in.readFloat();
	}
	
	public KeyPoint(ByteBuffer in) {
		xy = new float[2];
		
		xy[0] = in.getFloat();
		xy[1] = in.getFloat();
		ori = in.getFloat();
		scale = in.getFloat();
	}
	
	public void writeData(DataOutput out) throws IOException {
		byte[] res = new byte[byteSize];
		
		putBytes(res, 0);
		
		out.write(res);
	}
	
	public int putBytes(byte[] bArr, int bytesOffset) {
		int bArrI = bytesOffset;
		bArrI = FloatByteArrayUtil.floatArrayToByteArray(xy, bArr, bArrI);
		bArrI = FloatByteArrayUtil.floatToByteArray(ori, bArr, bArrI);
		bArrI = FloatByteArrayUtil.floatToByteArray(scale, bArr, bArrI);
		return bArrI;
	}

	public int getByteSize() {
		return byteSize;
	}

	public static int getSize(KeyPoint kp) {
		return byteSize;
	}
	
	public synchronized float[] getNormXY(ALocalFeaturesGroup linkedGroup) {
		
		if (normxy == null) {
			float[] tnormxy = new float[2];
			float[] mean = linkedGroup.getMeanXY();
			float scale = linkedGroup.getNormScale();
			tnormxy[0] = (xy[0] - mean[0]) * scale;
			tnormxy[1] = (xy[1] - mean[1]) * scale;
			normxy = tnormxy;
		}
		return normxy;
	}
	
	public int compareTo( KeyPoint given) {
		if ( this == given ) return 0;
		// this results in reverse order when sorting (from grater to smaller)
		int tComp = 0;
		if ( (tComp = Float.compare(given.scale, scale))  != 0 ) return tComp;
		if ( (tComp = Float.compare(xy[0], given.xy[0] )) != 0 ) return tComp;
		if ( (tComp = Float.compare(xy[1], given.xy[1] )) != 0 ) return tComp;
		if ( (tComp = Float.compare(ori, given.ori ))     != 0 ) return tComp;
		
		return 0;
	}
	
	@Override
	public final boolean equals(Object obj) {
		if ( obj == null ) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		return 0 == this.compareTo((KeyPoint) obj);
	}
	
	/*
	public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (obj.getClass() != getClass()) return false;
		KeyPoint kp = (KeyPoint) obj;
		
		if ( this.xy[0] != kp.xy[0] ) return false;
		if ( this.xy[1] != kp.xy[1] ) return false;
		if ( this.ori != kp.ori ) return false;
		if ( this.scale != kp.scale ) return false;
		return true;		
	}*/
	
	public String toString() {
		return "{[" + xy[0] + ", " + xy[1] + "], " + ori + ", " + scale + "}";
	}
	
}
