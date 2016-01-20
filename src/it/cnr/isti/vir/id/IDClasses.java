/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.id;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.Hashtable;

public class IDClasses  {
	
	public enum IDClasses_ID {
		IDINTEGER(IDInteger.class, (byte) 0),
		IDLONG(IDLong.class, (byte) 1),
		IDSTRING(IDString.class, (byte) 2),
		IDFLICKR(IDFlickr.class, (byte) 3);
		
		private static final byte idMax = 3;
		
		private Class<? extends AbstractID> c;
		private byte id;

		private IDClasses_ID(Class<? extends AbstractID> c, byte id) {
			this.c = (Class<? extends AbstractID>) c;
			this.id = id;
		}
	}
	
	static final Hashtable<Class<?>, Byte> idclassIdentifiersHT = new Hashtable<Class<?>, Byte>();
	static Constructor<?>[] constructors;
	static Constructor<?>[] constructors_NIO;	
	static Class<? extends AbstractID>[] identifiersIDclass; 
	
	
	static {
		
		IDClasses_ID[] values = IDClasses_ID.values();
		
		identifiersIDclass = new Class[IDClasses_ID.idMax+1];
		constructors = new Constructor[IDClasses_ID.idMax+1];
		constructors_NIO = new Constructor[IDClasses_ID.idMax+1];	
		
		for(IDClasses_ID id : values) {
			identifiersIDclass[id.id] = id.c;
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
			idclassIdentifiersHT.put(id.c, id.id);			
		}
		
	}
		
	public static final Class<? extends AbstractID> getClass(int id) {
		if ( id == -1 ) return null;
		//if ( it >= identifiersIDclass.length ) throw new Exception("Feature class id not found");
		return identifiersIDclass[id];
	}
	
	public static final byte getClassID(Class<? extends AbstractID> idClass) {
		if ( idClass == null ) return -1;
		Byte id = idclassIdentifiersHT.get(idClass);
		//if ( id == null ) throw new Exception("Feature class not found");
		return id;
	}
	
	public static final AbstractID readData(DataInput in ) throws IOException {
		int idInt = in.readByte();
		
		if ( idInt == -1 ) return null;
		
		try {
			return (AbstractID) constructors[idInt].newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final AbstractID readData(ByteBuffer in ) throws IOException {
	
		int idInt = in.get();
		
		if ( idInt == -1 ) return null;
		
		try {
			return (AbstractID) constructors_NIO[idInt].newInstance(in);//idClass.getConstructor(DataInput.class).newInstance(in);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final void writeData(AbstractID id, DataOutput out) throws IOException {
		if ( id == null ) {
			out.writeByte( (byte)  -1 );
		} else {
			out.writeByte( getClassID(id.getClass()) );		
			id.writeData(out);
		}
	}
	
	public static Class<? extends AbstractID> readClass_Int(DataInput in) throws IOException {
		return getClass(in.readInt());
	}
	
	public static Class<? extends AbstractID> readClass(DataInput in) throws IOException {
		return getClass( in.readByte());
	}

	public static void writeClass_Int(Class<? extends AbstractID> c, DataOutput out) throws IOException {
		out.writeInt(getClassID(c));
	}
	
	public static void writeClass(Class<? extends AbstractID> c, DataOutput out) throws IOException {
		out.writeInt(getClassID(c));
	}
	
	public static final AbstractID[] readArray(DataInput in, int n, Class<? extends AbstractID> idClass) throws IOException {
		if ( idClass.equals(IDString.class) ) {
			return IDString.readArray(in, n);
		}
		if ( idClass.equals(IDInteger.class) ) {
			return IDInteger.readArray(in, n);
		}
		if ( idClass.equals(IDLong.class) ) {
			return IDLong.readArray(in, n);
		}
		throw new IOException("idClass not found");
	}
}
