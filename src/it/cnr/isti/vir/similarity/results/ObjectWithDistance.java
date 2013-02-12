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
package it.cnr.isti.vir.similarity.results;

import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class ObjectWithDistance<ObjectClass> implements Comparable<ObjectWithDistance<ObjectClass>> {
	
	public ObjectClass obj;
	
	
	public final ObjectClass getObj() {
		return obj;
	}

	public final double getDist() {
		return dist;
	}

	public double dist;
	
	public ObjectWithDistance(ObjectClass obj, double dist) {
		super();
		this.obj = obj;
		this.dist = dist;
	}

	public void reset(ObjectClass obj, double dist) {
		this.obj = obj;
		this.dist = dist;
	}

	public final int compareTo(ObjectWithDistance<ObjectClass> arg0) {
		if ( arg0 == null ) return -1;
		int compare = Double.compare(dist, ((ObjectWithDistance<ObjectClass>) arg0).dist );
		if ( compare != 0 ) return compare;
		
		if ( obj instanceof IHasID ) {
			return ((IHasID) obj).getID().compareTo(((IHasID) arg0.getObj()).getID());
		}
		//System.out.println("Same distance");
		return ((Comparable) obj).compareTo( (Comparable) arg0.obj);
		//int temp = (Comparable) obj.compareTo((Comparable) arg0.hashCode());
		//return temp;
	}
	
//	public String toString() {
//		return  "("+ obj + ", " + dist +")";
//	}
		
	public String toString() {
		return  "("+ ((IHasID) obj).getID() + ", " + dist +")";
	}
	
	public final boolean equals(Object obj) {
		ObjectWithDistance<ObjectClass> givenObj = (ObjectWithDistance<ObjectClass>) obj;
		if ( this == obj) return true;
		if ( this.dist != givenObj.dist ) return false;
		if ( obj.hashCode() != givenObj.hashCode()) return false;
		//if ( !this.obj.equals(givenObj)) return false;
		
		return true;
	}

	public ObjectWithDistance(DataInput in) throws IOException {
		obj = (ObjectClass) IDClasses.readData(in);
		dist = in.readDouble();
	}
	
	public void writeIDData(DataOutput out) throws IOException {
		AbstractID id = null;
		//ID
		if ( AbstractID.class.isInstance(obj))
			id = (AbstractID) obj;	
		else
			id = ((IHasID) obj).getID();
		out.writeInt(IDClasses.getClassID(id.getClass()));
		id.writeData(out);
		//Distance
		out.writeDouble(dist);	
	}

	public ObjectWithDistance<AbstractID> getIDObjectWithDistance() {
		AbstractID id = null;
		//ID
		if ( AbstractID.class.isInstance(obj))
			return (ObjectWithDistance<AbstractID>) this;	
		else {
			id = ((IHasID) obj).getID();
			return new ObjectWithDistance(id, dist);
		}
	}
	
	public int hashCode() {
		  assert false : "hashCode not designed";
		  return 42; // any arbitrary constant will do 
		  }

	
}
