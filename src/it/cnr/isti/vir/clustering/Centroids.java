package it.cnr.isti.vir.clustering;

import it.cnr.isti.vir.features.FeatureClasses;
import it.cnr.isti.vir.features.IFeature;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

public class Centroids {

	private byte version = 0;
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
	
	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeByte(FeatureClasses.getClassID(centroids[0].getClass()));
		out.writeInt(centroids.length);
		out.writeUTF(metaData);
		for ( IFeature f : centroids) {
			f.writeData(out);
		}
	}
	
	public static Centroids read(DataInput in) throws Exception {
		byte version = in.readByte();
		Class<IFeature> c = FeatureClasses.getClass(in.readByte());
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
	
}
