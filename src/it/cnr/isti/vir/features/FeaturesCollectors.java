/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.features.mpeg7.SAPIRObject;
import it.cnr.isti.vir.util.ClassIDs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Hashtable;

public class FeaturesCollectors {

	static final Class[] fccIDclass = {
		FeaturesCollectorHT.class,
		FeaturesCollectorHTwithID.class,
		FeaturesCollectorHTwithIDClassified.class,
		SAPIRObject.class,
		FeaturesCollectorArr.class,
		FeatureCollector.class};
	
	static final Hashtable<Class<?>, Integer> idclassFCCHT = ClassIDs.getClassIDsHT(fccIDclass);
	static final Constructor<?>[] constructors 	= ClassIDs.getConstructors(fccIDclass, DataInput.class);
	static final Constructor<?>[] constructorsNIO 	= ClassIDs.getConstructors(fccIDclass, ByteBuffer.class);
	
	public static final Class getClass(int id)  {
		if ( id < 0 ) return null;
		return fccIDclass[id];
	}
	
	public static final Integer getClassID(Class featureClass) {
		Integer id = idclassFCCHT.get(featureClass);
		if ( id == null ) System.err.println("FeatureCollector class " + featureClass.getName() + " not found");
		return id;
	}
	
	public static final IFeaturesCollector readData(DataInput in ) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int fccID = in.readInt();
	
		return (IFeaturesCollector) constructors[fccID].newInstance(in); 
	}

	public static final IFeaturesCollector readData(ByteBuffer buf ) throws IOException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		int fccID = buf.getInt();
		
		return (IFeaturesCollector) constructorsNIO[fccID].newInstance(buf); 
	}
	
	public static final void writeData( DataOutput out, IFeaturesCollector fc) throws IOException {
		out.writeInt( getClassID(fc.getClass()) );
		fc.writeData(out);
	}

	public static Class readClass( DataInput in ) throws IOException {
		return getClass(in.readInt());
	}

	public static void writeClass(Class fcClass, DataOutput out) throws IOException {
		if ( fcClass == null ) out.writeInt(-1);
		else out.writeInt(getClassID(fcClass));		
	}
	
}
