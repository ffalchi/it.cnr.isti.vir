package it.cnr.isti.vir.features;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public class FeaturesSubCollecotr implements IFeaturesCollector {

	private FeaturesCollectorHT[] regionFC = null;
	
	static final float version = (float) 1.0;
	
	public FeaturesSubCollecotr(DataInput str) throws Exception {
		float version = str.readFloat();
		int size = str.readInt();
		FeaturesCollectorHT[] regionFC = new FeaturesCollectorHT[size];

		for(int i=0; i<size; i++) {
			regionFC[i] = new FeaturesCollectorHT(str);
		}
	}
	
	public void writeData(DataOutput str) throws IOException {
		str.writeFloat(version);
		str.writeInt(regionFC.length); //number of regions
		
		for ( int i=0; i<regionFC.length; i++ ) {
			regionFC[i].writeData(str);
		}
	}
	
	public boolean contains(Class[] classes) {
		for ( int i=0; i<regionFC.length; i++ ) {
			if ( !regionFC[i].contains(classes) ) return false;
		}
		return true;
	}

	public FeaturesSubCollecotr(FeaturesCollectorHT[] regionFC) {
		this.regionFC = regionFC;
	}
	

	public FeaturesSubCollecotr(Collection<FeaturesCollectorHT> collection ) {
		regionFC = new FeaturesCollectorHT[collection.size()];
		int index=0;
		for (Iterator<FeaturesCollectorHT> it=collection.iterator(); it.hasNext(); ) {
			regionFC[index]=it.next();
			index++;
		}
	}
	
	public int size() {
		return regionFC.length;
	}
	
	public FeaturesCollectorHT get(int index ) {
		return regionFC[index];
	}
	
	public String toString() {
		String tStr = "";
		for ( int i=0; i<regionFC.length; i++ ) {
			tStr += "-----SubCollection "+ i + " -----\n";
			tStr += regionFC[i];
		}	
		return tStr;
	}

	
	public int hashCode() {
		int hashCode = 1;
		for ( int i=0; i<regionFC.length; i++ ) {
			hashCode = 31*hashCode + regionFC[i].hashCode();
		}

		return hashCode;
	}
	
	public int compareTo(IFeaturesCollector obj) {
		return hashCode()-((FeaturesCollectorHT)obj).hashCode();
	}
	
	@Override
	public IFeaturesCollector getFeature( Class featureClass ) {
		if ( featureClass.equals(this.getClass())) return this;
		return null;
	}

	@Override
	public void add(IFeature f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses)
			throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
		
	}

	@Override
	public Collection<IFeature> getFeatures() {
		return null;
	}

	@Override
	public boolean contains(Class c) throws FeaturesCollectorException{
		throw new FeaturesCollectorException("Method not implemented");
	}

//	@Override
//	public void addAll(FeaturesCollectionInterface givenFC) {
//		regionFC.addAll(givenFC);
//		
//	}
//
//	@Override
//	public FeatureInterface[] getAllFeatures() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
