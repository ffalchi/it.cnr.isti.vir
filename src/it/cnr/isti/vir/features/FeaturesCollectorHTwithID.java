package it.cnr.isti.vir.features;

import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FeaturesCollectorHTwithID extends FeaturesCollectorHT implements IHasID {
	
	//public final FeaturesCollection f;	
	public AbstractID id;
	
	static float version = (float) 1.0;
	
	public FeaturesCollectorHTwithID(ByteBuffer src) throws IOException {
		super(src);
		float version = src.getFloat();
		id = IDClasses.readData(src);
	}
	
	public FeaturesCollectorHTwithID(DataInput str) throws IOException {
		super(str);
		float version = str.readFloat();
		id = IDClasses.readData(str);
	}
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		super.writeData(out);
		out.writeFloat(version);
		out.writeInt(IDClasses.getClassID(id.getClass()));
		id.writeData(out);
//		if ( id.getClass().equals(Integer.class) ) 	str.writeInt( (Integer) id);
//		else if ( id.getClass().equals(Long.class) ) 	str.writeLong( (Long) id);
//		else if ( id.getClass().equals(String.class))  {
//			str.writeInt(((String) id).length());
//			str.writeChars((String) id);
//		}
	}
	
	public FeaturesCollectorHTwithID(AbstractID id, FeaturesCollectorHT f) {
		super(f);
		this.id = id;
	}
	
	
	public FeaturesCollectorHTwithID(FeaturesCollectorHTwithID object) {
		super(object);
		this.id = object.id;
	}
	
	public FeaturesCollectorHTwithID(IFeaturesCollector object) {
		super(object);
		this.id = ((IHasID) object).getID();
	}
	
	public AbstractID getID(){
		return id;
	}
	
//	public FcIdObject(FeaturesCollection f) {
//		this( null, f);		
//	}

	
	public String toString() {
		if ( id == null ) return super.toString();
		return "ID: " + id + "\n" + super.toString();
	}

	@Override
	public int compareTo(IHasID o) {
		
		return id.compareTo( o.getID());
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		FeaturesCollectorHTwithID given = (FeaturesCollectorHTwithID) obj;
		if ( !this.id.equals(given.id)) return false;
		return super.equals(obj);
	}

	
//	@Override
//	public FeaturesCollection getFeaturesCollection() {
//		return f;
//	}
}
