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
package it.cnr.isti.vir.classification;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;

public class LabelClasses {
	
	private enum LabelClasses_ID {
		STRINGLABEL(StringLabel.class, (byte) 0);
		
		private static final byte idMax = 0;
		
		private Class<? extends AbstractLabel> c;
		private byte id;

		private LabelClasses_ID(Class<? extends AbstractLabel> c, byte id) {
			this.c = (Class<? extends AbstractLabel>) c;
			this.id = id;
		}
	}
	
	static Constructor<?>[] constructors;
	static Constructor<?>[] constructors_NIO;	
	static Class<? extends AbstractLabel>[] labelClass; 
		
	static {
		
		LabelClasses_ID[] values = LabelClasses_ID.values();
		
		labelClass = new Class[LabelClasses_ID.idMax+1];
		constructors = new Constructor[LabelClasses_ID.idMax+1];
		constructors_NIO = new Constructor[LabelClasses_ID.idMax+1];	
		
		for(LabelClasses_ID id : values) {
			labelClass[id.id] = id.c;
			try {
				constructors_NIO[id.id]=id.c.getConstructor(ByteBuffer.class);
			} catch (NoSuchMethodException | SecurityException e) {
				constructors_NIO[id.id]=null;
				System.err.println(id.c + " has not a constructor with parameters:");
			}
			try {
				constructors[id.id]=id.c.getConstructor(DataInput.class);
			} catch (NoSuchMethodException | SecurityException e) {
				constructors[id.id]=null;
				System.err.println(id.c + " has not a constructor with parameters:");
			}
		
		}
		
	}
	
	
	public static final Class<?> getClass(int id) throws LabelClassException {
		return labelClass[id];
	}
	
	public static final int getClassID_safe(Class<?> featureClass)  {
		for ( int i=0; i<labelClass.length; i++ ) {
			if ( featureClass.equals(labelClass[i]))
				return i;
		}
		return -1;		
	}
	
	public static final int getClassID(Class<?> featureClass) throws LabelClassException {
		int id = getClassID_safe(featureClass);
		if ( id<0)
			throw new LabelClassException("FeatureClass not found");
		return id;
	}
	
	public static final AbstractLabel readData(DataInput in ) throws IOException {
		int clID = in.readByte();
		
		if ( clID == -1 ) return null;
		
		try {
			return (AbstractLabel) constructors[clID].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final AbstractLabel readData(ByteBuffer in ) throws IOException {
		int clID = in.get();
		
		if ( clID == -1 ) return null;
		
		try {
			return (AbstractLabel) constructors_NIO[clID].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
	}

	public static final void writeData(AbstractLabel cl, DataOutput out) throws IOException {
		if ( cl == null ) {
			out.writeByte((byte) -1);
			return;
		}
		
		Class<? extends AbstractLabel> c = cl.getClass();
		for ( int i=0; i<labelClass.length; i++ ) {
			if ( c.equals(labelClass[i])) {
				out.writeByte((byte) i);
				cl.writeData( out );
				return;
			}			
		}	
		throw new IOException("ClassLabel not found");
	}

	
}
