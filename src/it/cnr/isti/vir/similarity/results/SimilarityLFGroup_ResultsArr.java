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
package it.cnr.isti.vir.similarity.results;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.PredictedLabel;
import it.cnr.isti.vir.features.AbstractFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.id.AbstractID;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

// To use only with LFw removing
public class SimilarityLFGroup_ResultsArr implements ISimilarityResults<ALocalFeaturesGroup> {

	protected final ObjectWithDistance<ALocalFeaturesGroup>[] lfGroup;
//	protected final ClassLabel[] label;
	protected int nObj;
	protected ALocalFeaturesGroup excludedGroup = null;
	
	public ALocalFeaturesGroup getExcludedGroup() {
		return excludedGroup;
	}
	
	public void setExcludedGroup(ALocalFeaturesGroup excludedGroup) {
		this.excludedGroup = excludedGroup;
	}
	
	public SimilarityLFGroup_ResultsArr(ISimilarityResults res) {
		nObj = res.size();
		lfGroup = new ObjectWithDistance[nObj];
//		label = new ClassLabel[nObj];
		int i=0;
		for ( Iterator<ObjectWithDistance> it=res.iterator(); it.hasNext();) {
			ObjectWithDistance curr = it.next();
			lfGroup[i]=new ObjectWithDistance(((ALocalFeature)curr.obj).getLinkedGroup(),curr.dist);
//			label[i]=lfGroup[i].obj.getLabel();			
			i++;
		}
	}

	private class SimilarityResultsIterator implements Iterator<ObjectWithDistance<ALocalFeaturesGroup>>{

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
 
        public final ObjectWithDistance<ALocalFeaturesGroup> next(){
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
		ISimilarityResults<ALocalFeaturesGroup> that) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final ObjectWithDistance<ALocalFeaturesGroup> getFirst() {
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
	public Iterator<ObjectWithDistance<ALocalFeaturesGroup>> iterator() {
		// TODO Auto-generated method stub
		return new SimilarityResultsIterator();
	}

	@Override
	public void setQuery(ALocalFeaturesGroup object) {
	}

	@Override
	public int size() {
		return lfGroup.length;
	}

	@Override
	public void writeIDData(DataOutputStream out) throws IOException {
		// TODO Auto-generated method stub		
	}  
	
	public void removeLFByGroup(ALocalFeaturesGroup toRemoveGroup) {
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
	public Collection<AbstractFeaturesCollector_Labeled_HasID> getFCs(
			FeaturesCollectorsArchives archives) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ALocalFeaturesGroup getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractID getQuery_ID() {
		// TODO Auto-generated method stub
		return null;
	}
		
}
