package it.cnr.isti.vir.features;

import it.cnr.isti.vir.features.bof.BoF;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.features.localfeatures.RootSIFT;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SURF;
import it.cnr.isti.vir.features.localfeatures.SURFGroup;
import it.cnr.isti.vir.features.localfeatures.VLAD;
import it.cnr.isti.vir.features.metadata.GPSData;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.DominantColor;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.RegionShape;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;
import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class FeatureClasses {

	static final Class[] idsFeatures = {
		null, //FeaturesCollectorHT.class,
		null, //FeaturesCollectorHTwithID.class,
		null, //FeaturesCollectorHTwithIDClassified.class,
		FeaturesSubCollecotr.class,
		FeaturesSubRegions.class,		
		ScalableColor.class,
		ColorStructure.class,
		ColorLayout.class,
		DominantColor.class,
		EdgeHistogram.class,
		HomogeneousTexture.class,
		RegionShape.class,
		SIFT.class,
		null, //ColorSIFT.class,
		SURF.class,
		SIFTGroup.class,
		null, //ColorSIFTGroup.class,
		SURFGroup.class,		
		null, //BoF_LF.class,
		null, //BoF_LF_bytes.class,
		null, //BoF_LF_OriAndScale.class,
		BoFLFGroup.class,
		null, //BoFLFGroupSoft.class,
		GPSData.class,
		RootSIFTGroup.class,
		RootSIFT.class,
		BoF.class,
		VLAD.class
	};
	
	static final Hashtable<Class<?>, Integer> featuresIDsHT = ClassIDs.getClassIDsHT(idsFeatures);
	static final Constructor<?>[] constructors  = ClassIDs.getConstructors(idsFeatures, DataInput.class);
	static final Constructor<?>[] constructors2 = ClassIDs.getConstructors(idsFeatures, DataInput.class, IFeaturesCollector.class);
	static final Constructor<?>[] constructors_NIO  = ClassIDs.getConstructors(idsFeatures, ByteBuffer.class);
	static final Constructor<?>[] constructors2_NIO = ClassIDs.getConstructors(idsFeatures, ByteBuffer.class, IFeaturesCollector.class);
	

	public static final void writeData(DataOutput out, IFeature fc ) throws IOException {
		out.writeByte(getClassID(fc.getClass()));
		fc.writeData(out);
	}
	
	public static final Hashtable<Integer, Class> getIDsFeaturesHT() {
		Hashtable<Integer, Class> ht = new Hashtable<Integer, Class>();
		for ( Iterator<Map.Entry<Class<?>, Integer>> it = featuresIDsHT.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Class<?>, Integer> curr = it.next();
			ht.put(curr.getValue(), curr.getKey());
		}
		return ht; 
	}
	
	public static final Class getClass(int id)  {
		return idsFeatures[id];
	}
	
	public static final byte getClassID(Class featureClass) {
		Integer id = featuresIDsHT.get(featureClass);
		if ( id == null ) {
			System.err.println("Feature class " + featureClass.getName() + "not found");
			return -1;
		}
//		if ( id == null ) throw new Exception("Feature class not found");
		return id.byteValue();
	}
	
	
	
	public static final IFeature readData(DataInput in ) throws IOException {
		return readData(in, null);
	}
	
	public static final IFeature readData(ByteBuffer in, IFeaturesCollector fc ) throws IOException {
		byte id = in.get();

		try {
			if ( constructors2_NIO[id] != null )
				return (IFeature) constructors2_NIO[id].newInstance(in, fc);
			if ( constructors_NIO[id] != null )
				return (IFeature) constructors_NIO[id].newInstance(in);
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		return null;
		
	}
	
	public static final IFeature readData(DataInput in, IFeaturesCollector fc ) throws IOException {
		byte id = in.readByte();

		try {
			if ( constructors2[id] != null )
				return (IFeature) constructors2[id].newInstance(in, fc);
			if ( constructors[id] != null )
				return (IFeature) constructors[id].newInstance(in);
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		return null;
		
	}
}
