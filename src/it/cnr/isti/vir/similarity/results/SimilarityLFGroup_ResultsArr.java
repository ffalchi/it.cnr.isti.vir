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
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.features.localfeatures.AbstractLFGroup;
import it.cnr.isti.vir.features.localfeatures.ILocalFeature;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

// To use only with LFw removing
public class SimilarityLFGroup_ResultsArr implements ISimilarityResults<AbstractLFGroup> {

	protected final ObjectWithDistance<AbstractLFGroup>[] lfGroup;
//	protected final ClassLabel[] label;
	protected int nObj;
	protected AbstractLFGroup excludedGroup = null;
	
	public AbstractLFGroup getExcludedGroup() {
		return excludedGroup;
	}
	
	public void setExcludedGroup(AbstractLFGroup excludedGroup) {
		this.excludedGroup = excludedGroup;
	}
	
	public SimilarityLFGroup_ResultsArr(ISimilarityResults res) {
		nObj = res.size();
		lfGroup = new ObjectWithDistance[nObj];
//		label = new ClassLabel[nObj];
		int i=0;
		for ( Iterator<ObjectWithDistance> it=res.iterator(); it.hasNext();) {
			ObjectWithDistance curr = it.next();
			lfGroup[i]=new ObjectWithDistance(((ILocalFeature)curr.obj).getLinkedGroup(),curr.dist);
//			label[i]=lfGroup[i].obj.getLabel();			
			i++;
		}
	}

	private class SimilarityResultsIterator implements Iterator<ObjectWithDistance<AbstractLFGroup>>{

		private int next = -1;
 
        private SimilarityResultsIterator(){
        	this.setNext();
        	
        }
        
        private final void setNext() {
        	next++;
        	while ( next < nObj && lfGroup[next].obj==excludedGroup) {
        		next++;
        	}
        }
 
        public final boolean hasNext(){
            return next < nObj;
        }
 
        public final ObjectWithDistance<AbstractLFGroup> next(){
            int curr = next;
            setNext();
            return lfGroup[curr];
        }
 
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }

	@Override
	public boolean equalResults(
		ISimilarityResults<AbstractLFGroup> that) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final ObjectWithDistance<AbstractLFGroup> getFirst() {
		int first=0;
    	while ( first < nObj && lfGroup[first].obj==excludedGroup) {
    		first++;
    	}
    	return lfGroup[first];
	}

	@Override
	public ISimilarityResults getResultsIDs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<ObjectWithDistance<AbstractLFGroup>> iterator() {
		// TODO Auto-generated method stub
		return new SimilarityResultsIterator();
	}

	@Override
	public void setQuery(AbstractLFGroup object) {
	}

	@Override
	public int size() {
		return lfGroup.length;
	}

	@Override
	public void writeIDData(DataOutputStream out) throws IOException {
		// TODO Auto-generated method stub		
	}  
	
	public void removeLFByGroup(AbstractLFGroup toRemoveGroup) {
		// note that only the first (and hopefully the only) one is removed
		int toRemove = -1;
		for ( int i=0; i<nObj; i++) {
			if ( lfGroup[i].obj==toRemoveGroup ) {
				toRemove = i;
				break;
			}
		}
		if ( toRemove != -1 ) {
			for ( int i=toRemove; i<nObj-1; i++) {
				lfGroup[i]=lfGroup[i+1];
			}
			nObj--;
			lfGroup[nObj]=null;
		}
		
	}
	
	public PredictedLabel getLoweKNNPrClassLabel() {
		int best = 0;
		while ( best < nObj && lfGroup[best].obj==excludedGroup) {
			best++;
    	}		
		for ( int i=best+1; i<nObj;i++) {
//			if ( !label[best].equals(label[i]) ) {
//				// second label found
//				return new PredictedClassLabel(label[best], 1.0-(lfGroup[best].getDist() / lfGroup[i].getDist()));
//			}
			AbstractLabel bestLabel = lfGroup[best].obj.getLabel();
			AbstractLabel iLabel = lfGroup[i].obj.getLabel();
			if ( bestLabel != iLabel && !bestLabel.equals(iLabel) ) {
				// second label found
				return new PredictedLabel(bestLabel, 1.0-(lfGroup[best].getDist() / lfGroup[i].getDist()));
			}
		}
		return new PredictedLabel(lfGroup[best].obj.getLabel(), 1.0);		
	}

	@Override
	public Collection<IFeaturesCollector_Labeled_HasID> getFCs(
			FeaturesCollectorsArchives archives) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractLFGroup getQuery() {
		// TODO Auto-generated method stub
		return null;
	}
		
}
