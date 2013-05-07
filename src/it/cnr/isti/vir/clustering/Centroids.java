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
package it.cnr.isti.vir.clustering;

import it.cnr.isti.vir.features.FeatureClasses;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.IHasID;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

public class Centroids {

	private byte version = 1;
	private IFeature[] centroids;
	private String metaData = "";
	
	public Centroids(IFeature[] centroids, String metadata) {
		this.centroids = centroids;
		if ( metadata != null ) {
			this.metaData = metadata;
		}
	}
	
	public Centroids(IFeature[] centroids) {
		this (centroids, null);
	}
	
	public IFeature[] getCentroids() {
		return centroids;
	}
	
	public void writeData(String fName) throws IOException {
		DataOutputStream out = new DataOutputStream( new BufferedOutputStream ( new FileOutputStream(fName) ));
		writeData( out);
		out.close();
	}
	
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		Class c = centroids[0].getClass();
		if ( centroids[0] instanceof IFeaturesCollector ) {
			out.writeBoolean(true);
			out.writeByte(FeaturesCollectors.getClassID(c));
		} else {
			out.writeBoolean(false);
			out.writeByte(FeatureClasses.getClassID(c));
		}
		out.writeInt(centroids.length);
		out.writeUTF(metaData);
		for ( IFeature f : centroids) {
			f.writeData(out);
		}
	}
	

	public static Centroids read(DataInput in) throws Exception {
		byte version = in.readByte();
		boolean readingFC = false;
		if ( version > 0 ) {
			readingFC = in.readBoolean();
		}
		Class<IFeature> c = null;
		if ( readingFC ) {
			c = FeaturesCollectors.getClass(in.readByte());
		} else {
			c = FeatureClasses.getClass(in.readByte());
		}
		Constructor<IFeature> constructor = c.getConstructor(DataInput.class);
		int size = in.readInt();
		String metaData = in.readUTF();
		IFeature[] centroids = (IFeature[]) Array.newInstance(c, size);
		
		for (int i = 0; i < centroids.length; i++) {
			centroids[i] = constructor.newInstance(in);
		}

		return new Centroids(centroids, metaData);
	}
	
	public static Centroids read(String inWordsFileName) throws Exception {
		return read(new DataInputStream(new BufferedInputStream(new FileInputStream(inWordsFileName))));
	}
	
	public String toString() {
		String tStr = "";
		tStr += centroids.length + " centroids of class " + centroids[0].getClass() + "\n";
		tStr += "metadata: "+ metaData;
		
		return tStr;		
	}
	
	public void writeIDs( String fName ) throws IOException {
		BufferedWriter out = new BufferedWriter ( new FileWriter(fName) );
		out.write( getIDs());
		out.close();
	}
	
	public String getIDs() {
		String tStr = "";
		for (int i = 0; i < centroids.length; i++) {
			tStr += ((IHasID) centroids[i]).getID() + "\n";
		}
		return tStr;
	}
	
}
