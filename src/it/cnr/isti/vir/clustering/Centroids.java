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
	
	public void writeData(String fName) throws IOException {
		writeData( new DataOutputStream( new BufferedOutputStream ( new FileOutputStream(fName) )));
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
