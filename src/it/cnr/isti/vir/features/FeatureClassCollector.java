package it.cnr.isti.vir.features;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Fabrizio Falchi
 *
 * 
 *
 */
public class FeatureClassCollector implements Cloneable {

	private HashSet<Class> hSet = new HashSet<Class>(10);
	
	public final int size() {
		return hSet.size();
	}
	
	public final void writeData(DataOutput out) throws IOException {
		out.writeInt(hSet.size());
		for ( Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			out.writeInt( FeatureClasses.getClassID(it.next()) );
		}
	}
	
	public FeatureClassCollector(DataInput in) throws IOException {
		int size = in.readInt();
		for ( int i=0; i<size; i++) {
			hSet.add( FeatureClasses.getClass(in.readInt( )) );
		}
	}
	
	public FeatureClassCollector(FeatureClassCollector given) {
		for (Iterator<Class> it = given.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
	}
	
	public final FeatureClassCollector clone() {
		return new FeatureClassCollector(this);
	}
	
	public FeatureClassCollector( Class... featureClasses ) {
		add(featureClasses);
	}
	
	@SuppressWarnings("unchecked")
	public final FeatureClassCollector add( Class... featureClasses ) {
		for(Class element : featureClasses) hSet.add(element);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public final void addAll( FeatureClassCollector fcc) {
		for (Iterator<Class> it = fcc.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
	}
	
	@SuppressWarnings("unchecked")
	public final boolean contains( Class featureClass ) {
		return hSet.contains(featureClass);
	}
	
	@SuppressWarnings("unchecked")
	public final boolean areIn( IFeaturesCollector f ) throws FeaturesCollectorException {
		for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			if ( !f.contains(it.next() ) ) return false;
		}
		return true;
	}
	
	public final Collection<Class> missingIn( IFeaturesCollector f ) throws FeaturesCollectorException {
		ArrayList<Class> list = new ArrayList(10);
		for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
			Class curr = it.next();
			if ( !f.contains( curr ) ) {
				list.add(curr);
			}
		}
		return list;
	} 
	
	@SuppressWarnings("unchecked")
	public final boolean areIn( FeaturesSubCollecotr f ) {
		for (int i=0; i<f.size(); i++ ) {
			for (Iterator<Class> it = hSet.iterator(); it.hasNext(); ) {
				if ( !f.get(i).contains(it.next()) ) return false;
			}
		}
		return true;
	}
	
	public Iterator<Class> iterator() {
		return hSet.iterator();
	}
	
	public static final FeatureClassCollector getIntersection(FeatureClassCollector fc1, FeatureClassCollector fc2 ) {
		FeatureClassCollector fcc = new FeatureClassCollector();
		for (Iterator<Class> it=fc1.hSet.iterator(); it.hasNext(); ) {
			Class currClass = it.next();
			if ( fc2.contains(currClass)) fcc.add(currClass);
		}		
		
		return fcc;
	}
	
	public final Class getRequestedClass() {
		if ( hSet.size() != 1 ) return null;
		return hSet.iterator().next();
	}
	
	public String toString() {
		String tStr = "";
		for (Iterator<Class> it=hSet.iterator(); it.hasNext(); ) {
			tStr+= " " +it.next();
		}
		return tStr;
	}
	
}
