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

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;
import it.cnr.isti.vir.features.localfeatures.ILocalFeature;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IDInteger;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class SimilarityResults<E> implements ISimilarityResults<E> {

	protected final Collection<ObjectWithDistance<E>> coll;
	protected E query;
	
	protected AbstractLFGroup excludedGroup = null;
	
	public AbstractLFGroup getExcludedGroup() {
		return excludedGroup;
	}

	public void setExcludedGroup(AbstractLFGroup excludedGroup) {
		this.excludedGroup = excludedGroup;
	}

	private class SimilarityResultsIterator<E> implements Iterator<E>{
		 
//        private OneWayNode curPos;
//        private int curModCount;
		private E next;
		private Iterator<E> internalIterator;
 
        private SimilarityResultsIterator(Iterator<E> givenIt){
        	internalIterator = givenIt;
        	this.setNext();
        }
        
        private void setNext() {
        	if (!internalIterator.hasNext()) {
        		next = null;
        		return;
        	}
        	next = internalIterator.next();
        	while ( ((ILocalFeature) ((ObjectWithDistance) next).getObj()).getLinkedGroup() == excludedGroup ) {
        		if (!internalIterator.hasNext()) {
        			next = null;
        			return;
        		}
            	next = internalIterator.next();
        	}
        }
 
        public boolean hasNext(){
            return next != null;
        }
 
        public E next(){
            if (! this.hasNext() )
                throw new IllegalStateException();
//            if (this.curModCount != modCount)
//                throw new ConcurrentModificationException()
            E data = next;
            setNext();
            return data;
        }
 
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }  
	
	public SimilarityResults( E query, int initSize) {
		this.query = query;
		coll = new ArrayList<ObjectWithDistance<E>>(initSize);
	}
	
	public SimilarityResults(int initSize) {
		this(null, initSize);
	}
	
	public SimilarityResults(Collection<ObjectWithDistance<E>> given ) {
		this(null, given);
	}
	
	public SimilarityResults(ObjectWithDistance<E>[] given ) {
		this(null, given);
	}
	
	public SimilarityResults(ObjectWithDistance<E> given ) {
		this.query = null;
		coll = new ArrayList<ObjectWithDistance<E>>(1);
		coll.add(given);
	}
	
	public SimilarityResults(E query, Collection<ObjectWithDistance<E>> given ) {
		this.query = query;
		coll = given;
	}
	
	public SimilarityResults(E query, ObjectWithDistance<E>[] given ) {
		this.query = query;
		if ( given == null ) {
			coll = null;
		} else {
			coll = new ArrayList<ObjectWithDistance<E>>(given.length);
			for (int i=0; i<given.length; i++)
				coll.add( given[i]);
		}	
	}
	
//	public SimilarityResults(E query) {
//		this.query = query;
//		coll = new LinkedList<ObjectWithDistance<E>>();
//	}
//	
	
	public void setQuery(E query ) {
		this.query = query;
	}
	
	public E getQuery() {
		return query;
	}
	
	public AbstractID getQueryID() {
		AbstractID id = null;
		if ( AbstractID.class.isInstance(query))
			id = (AbstractID) query;	
		else
			id = ((IHasID) query).getID();
		return id;
	}
	

		
	public Iterator<ObjectWithDistance<E>> iterator() {
		if ( excludedGroup == null ) return coll.iterator();
		//System.out.println("Results Size " + coll.size());
		return new SimilarityResultsIterator(coll.iterator());
	}

	@Override
	public int size() {
		if ( coll == null ) return 0;
		return coll.size();
	}
	
	@Override
	public boolean  equalResults(ISimilarityResults<E> that) {
		if ( !this.query.equals(((SimilarityResults) that).query)) return false;
		if ( this.size() != that.size() ) return false;
		Iterator<ObjectWithDistance<E>> itThis = this.iterator();
		for ( Iterator<ObjectWithDistance<E>> itThat = that.iterator(); itThat.hasNext(); ) {
			if ( ! itThis.next().equals(itThat.next()) ) return false;
		}
		return true;
	}


	@Override
	public void writeIDData(DataOutputStream out) throws IOException {
		// QUERY ID
		AbstractID id = null;
		if ( AbstractID.class.isInstance(query))
			id = (AbstractID) query;	
		else
			id = ((IHasID) query).getID();
		out.writeInt(IDClasses.getClassID(id.getClass()));
		id.writeData(out);
		
		// RESULTS 
		out.writeInt(coll.size());
		for (Iterator<ObjectWithDistance<E>> itThis = this.iterator(); itThis.hasNext(); ) {
			ObjectWithDistance<E> obj = itThis.next();
			obj.writeIDData(out);
		}
	}
	
	public SimilarityResults(DataInputStream in ) throws IOException {
		int idType = in.readInt();
		// work around for old Cophir
		if ( idType != 1001 ) { 
			throw new IOException("IDType error!");
		}
		query = (E) new IDInteger(in);
		//query = (E) IDClasses.readData(in);
		int size = in.readInt();
		coll = new ArrayList(size);
		
		for (int i=0; i<size; i++) {
			coll.add(new ObjectWithDistance(in));
		}
	}
	
	public static SimilarityResults[] readArray(DataInputStream in ) throws IOException {
		LinkedList<SimilarityResults> list = new LinkedList<SimilarityResults>();
		while ( in.available() > 0) {
			list.add( new SimilarityResults(in) );
		} 
		
		SimilarityResults[] arr = new SimilarityResults[list.size()];
		int i=0;
		for ( Iterator<SimilarityResults> it = list.iterator(); it.hasNext(); i++ ) {
			arr[i]=it.next();
		}
		return arr;
	}

	@Override
	public ISimilarityResults getResultsIDs() {
		if ( AbstractID.class.isInstance(query) ) return this;
		
		// RESULTS 
		LinkedList list = new LinkedList();
		for (Iterator<ObjectWithDistance<E>> itThis = coll.iterator(); itThis.hasNext(); ) {
			ObjectWithDistance<E> obj = itThis.next();
			list.add(obj.getIDObjectWithDistance());
		}
				
		SimilarityResults resIDs = new SimilarityResults(list);
		resIDs.setQuery(((IHasID)query).getID());
		return resIDs;
	}

	public SimilarityResults getRemovingLFByGroup(AbstractLFGroup toRemoveGroup) {
		SimilarityResults res = new SimilarityResults(this.size());
		for (Iterator<ObjectWithDistance<E>> itThis = coll.iterator(); itThis.hasNext(); ) {
			ObjectWithDistance<E> obj = itThis.next();
			if ( ! (toRemoveGroup == ((ILocalFeature) obj.obj).getLinkedGroup()) ) {
				res.coll.add(obj);
			}
		}		
		
		return res;
	}

	@Override
	public ObjectWithDistance<E> getFirst() {
		if ( coll == null || coll.size() == 0 ) return null;
		return coll.iterator().next();
	}
	
	
	public String toString() {
		//String tStr = query.toString() + "\t";
		String tStr = "";
		if ( query != null ) {
			AbstractID id = ((IHasID) query).getID();
			if ( id != null ) tStr = id.toString() + "\t";
		}
		
		for (Iterator<ObjectWithDistance<E>> itThis = this.iterator(); itThis.hasNext(); ) {
			tStr += itThis.next().toString() +"\t";
		}
		return tStr;
	}
	
	public String getHtmlTableRow_Images(String preFix, String postFix ) {
		return getHtmlTableRow_Images( preFix, postFix, null, null);
	}
	
	public String getHtmlTableRow_Images(String preFix, String postFix, Integer subStringInt, Integer height) {
		String tStr = "<tr>\n";
		if ( height == null) height = 100;
		if ( subStringInt == null )
			tStr += "<td>"+ ((IHasID)query).getID()+ "<br>" + "<img src=\"" + preFix + ((IHasID)query).getID()+ postFix + "\" height=\""+height+"\"></td>";
		else
			tStr += "<td>"+ ((IHasID)query).getID() + "<br>" +  "<img src=\"" + preFix + ((IHasID)query).getID().toString().substring(0, subStringInt)+ "/"  + ((IHasID)query).getID()+ postFix + "\" height=\""+height+"\"></td>";
		for (Iterator<ObjectWithDistance<E>> itThis = this.iterator(); itThis.hasNext(); ) {
			ObjectWithDistance<E> curr = itThis.next();
			AbstractID id = ((IHasID)curr.obj).getID();
			if ( subStringInt == null ) {
				tStr += "<td>" + String.format("%.3f", curr.dist) + "<br>" +  "<img src=\"" + preFix + id + postFix + "\" title=\""+((IHasID)curr.obj).getID() + " d ";
				tStr += String.format("%.3f", curr.dist);
				tStr += "\" height=\""+height+"\"></td>";
			} else 
				tStr += "<td>" + String.format("%.3f", curr.dist) + "<br>" +  "<img src=\"" + preFix + id.toString().substring(0, subStringInt) + "/" + id + postFix + "\" title=\""+((IHasID)curr.obj).getID() + " d " + String.format("%.3f", curr.dist) + "\" height=\""+height+"\"></td>";
		}
		
		return tStr + "</tr>";
	}


	@Override
	public Collection<IFeaturesCollector_Labeled_HasID> getFCs( FeaturesCollectorsArchives archives ) throws ArchiveException {
		ArrayList<IFeaturesCollector_Labeled_HasID> res = new ArrayList();
		for (Iterator<ObjectWithDistance<E>> itThis = this.iterator(); itThis.hasNext(); ) {
			ObjectWithDistance<E> curr = itThis.next();
			AbstractID id = ((IHasID) curr.obj).getID();
			AbstractLabel label = null;
			if ( curr.obj instanceof ILabeled )
				label = ((ILabeled) curr.obj).getLabel();
			
			IFeaturesCollector temp = (IFeaturesCollector) archives.get( id );
			FeaturesCollectorArr currFC = new FeaturesCollectorArr( temp.getFeatures(), id, label );
			
			res.add( currFC );
						
		}
		return res;
	}
	
}
