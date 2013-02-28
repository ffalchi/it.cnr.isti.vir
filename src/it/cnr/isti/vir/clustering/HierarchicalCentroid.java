package it.cnr.isti.vir.clustering;

import it.cnr.isti.vir.features.FeatureClasses;
import it.cnr.isti.vir.features.IFeature;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class HierarchicalCentroid<O> {

	byte rawVersion = 0;
	private final O centroid;
	private HierarchicalCentroid<O>[] children;
	private HierarchicalCentroid<O> father;
	
	public HierarchicalCentroid( O centroid, HierarchicalCentroid<O>[] children) {
		this.centroid = centroid;
		this.children = children;
	}
	
	public static HierarchicalCentroid readRoot(DataInput in) throws Exception {
		byte version = in.readByte();
		Class c = FeatureClasses.getClass(in.readInt());
		Constructor constructor = c.getConstructor(DataInput.class);
		return read(in, constructor);
	}
	
	public static HierarchicalCentroid read(DataInput in, Constructor constructor) throws Exception {
		Object centroid = constructor.newInstance(in);
		int nChildren = in.readInt();
		if ( nChildren == 0) {
			// Leaf node
			return new HierarchicalCentroid(centroid, null);
		} else {
			HierarchicalCentroid[] children = new HierarchicalCentroid[nChildren];
			for (int i = 0; i < nChildren; i++) {
				children[i] = read(in, constructor);
			}
			return new HierarchicalCentroid(centroid, children);
		}
	}
	
	public void writeData(DataOutput out) throws IOException {
		if ( isRoot() ) {
			out.writeByte(rawVersion);
			FeatureClasses.getClassID(centroid.getClass());
		}
		((IFeature) centroid).writeData(out);
		out.write(children.length);
		for (HierarchicalCentroid<O> child : children) {
			child.writeData(out);
		}
		return;
	}
	
	public boolean areLeafs() {
		if ( children == null ) {
			return true;
		}
		return false;
	}
	
	public boolean isRoot() {
		if ( father == null ) {
			return true;
		}
		return false;
	}
	
	public HierarchicalCentroid<O> getFather() {
		return father;
	}
	
	public ArrayList<O> getSubTreeLeafs() {
		ArrayList<O> res = new ArrayList<O>();
		this.putSubTreeLeafs(res);
		return res;
	}
	
	public void putSubTreeLeafs(Collection<O> coll) {
		if ( areLeafs() ) {
			coll.addAll(Arrays.asList(centroid));
		} else {
			for (HierarchicalCentroid<O> child : children) {
				child.putSubTreeLeafs(coll);
			}
		}
	}
	
	public HierarchicalCentroid<O> getRoot() {
		if ( isRoot() ) {
			return this;
		} else {
			return father.getRoot();
		}
	}
	
	public int getNSubLevels() {
		if ( children == null ) return 0;
		int lMax = 0;
		for (HierarchicalCentroid<O> child : children) {
			int temp = child.getNSubLevels();
			if ( temp > lMax ) lMax = temp;
		}
		return lMax +1;
	}
	
	public int getLevel() {
		if ( isRoot() ) {
			return 1;
		} else {
			return 1 + father.getLevel();
		}
	}
	
}
