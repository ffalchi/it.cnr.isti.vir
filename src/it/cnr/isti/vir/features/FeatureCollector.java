package it.cnr.isti.vir.features;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.LabelClasses;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IHasID;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;


public class FeatureCollector implements IFeaturesCollector_Labeled_HasID {

	protected final IFeature f;
	protected AbstractLabel l;
	protected final AbstractID id;
	
	public FeatureCollector(IFeature f ) {
		this(f, null);
	}
	
	public FeatureCollector(IFeature f, AbstractID id) {
		this(f, id, null);
	}
	
	public FeatureCollector(IFeature f, AbstractID id, AbstractLabel l) {
		this.f = f;
		this.l = l;
		this.id = id;
	}
	
	
	
	@Override
	public boolean equals(Object other) {
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof FeatureCollector)) return false;
	    FeatureCollector otherFC = (FeatureCollector) other;
	    if ( id == null ) {
	    	if ( otherFC.id == null ) return true;
	    	else return false;
	    }
	    if ( !id.equals(otherFC.id)) return false;
		return f.equals(otherFC.f);
	}

	@Override
	public int compareTo(IHasID arg0) {
		if ( this == arg0 ) return 0;
		if ( this.id != arg0.getID() ) {
			if ( this.id == null ) return -1;
			if ( arg0.getID() == null ) return 1;
			int tComp = this.id.compareTo( arg0.getID());	
			if ( tComp != 0 ) return tComp;
		}
		// TO DO!
		return 0;
	}
	
	
	@Override
	public IFeature getFeature(Class featureClass) {
		return f;
	}

	@Override
	public void add(IFeature f) {
		
	}

	@Override
	public void discardAllBut(FeatureClassCollector featuresClasses) throws FeaturesCollectorException {
		throw new FeaturesCollectorException("Method not implemented");
	}

	public FeatureCollector(ByteBuffer src) throws IOException  {
		id = IDClasses.readData(src);
		l = LabelClasses.readData(src);
		f = FeatureClasses.readData(src, this);
	}
	
	public FeatureCollector(DataInput in) throws IOException {
		id = IDClasses.readData(in);
		l = LabelClasses.readData(in);
		f = FeatureClasses.readData(in, this);
	}
	
	
	@Override
	public void writeData(DataOutput out) throws IOException {
		IDClasses.writeData(id, out);
		LabelClasses.writeData(l, out);
		FeatureClasses.writeData(out, f);
	}

	@Override
	public AbstractLabel getLabel() {
		return l;
	}

	@Override
	public AbstractID getID() {
		return id;
	}
	


	
	public int hashCode() {
		
		int hashCode = f.hashCode();
		
		return hashCode;
	}

	@Override
	public Collection<IFeature> getFeatures() {
		ArrayList t =  new ArrayList(1);
		t.add(f);
		return t;
	}

	@Override
	public boolean contains(Class<IFeature> c) {
		if ( f.getClass().equals(c) ) return true;
		return false;
	}

	@Override
	public void setLabel(AbstractLabel label) {
		l = label;
	}

	
	

}
