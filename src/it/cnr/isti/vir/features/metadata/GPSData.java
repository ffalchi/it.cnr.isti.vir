package it.cnr.isti.vir.features.metadata;

import it.cnr.isti.vir.features.IFeature;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GPSData implements IFeature {
	
	private final float longitude;
	private final float latitude;
	private final float acc;
	
	public GPSData(DataInput in) throws IOException {		
		longitude = in.readFloat();
		latitude = in.readFloat();
		acc = in.readFloat();		
	}
	
	public GPSData(ByteBuffer src) throws IOException {		
		longitude = src.getFloat();
		latitude = src.getFloat();
		acc = src.getFloat();		
	}
	
	public GPSData(float longitude, float latitude, float acc) {
		super();
		this.longitude = longitude;
		this.latitude = latitude;
		this.acc = acc;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		out.writeFloat(longitude);
		out.writeFloat(latitude);
		out.writeFloat(acc);
	}

	public String toString() {
		String tStr = "GPSData: "+longitude+" "+latitude;
		if ( acc >= 0 ) tStr += " "+acc;
		return tStr;
	}
	
	static final public double distance_m( GPSData location1, GPSData location2 )
	{
	      double lat1 = (location1.latitude  * Math.PI / 180);
	      double lon1 = (location1.longitude * Math.PI / 180);
	      double lat2 = (location2.latitude  * Math.PI / 180);
	      double lon2 = (location2.longitude * Math.PI / 180);
	 
	      double dist = Math.acos( Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2) );
	 
	      return dist * 6366710.0; // for meters
	}
	
}
