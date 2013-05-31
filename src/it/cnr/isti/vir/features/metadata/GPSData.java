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
package it.cnr.isti.vir.features.metadata;

import it.cnr.isti.vir.features.AbstractFeature;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class GPSData extends AbstractFeature {
	
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
