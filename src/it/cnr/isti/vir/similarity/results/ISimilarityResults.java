package it.cnr.isti.vir.similarity.results;

import it.cnr.isti.vir.features.IFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

public interface ISimilarityResults<E> {

	public Iterator<ObjectWithDistance<E>> iterator();
	
	public int size();
	
	public void setQuery(E object);
	
	public E getQuery();
	
	public boolean equalResults(ISimilarityResults<E> that);

	public void writeIDData(DataOutputStream out) throws IOException;

	public ISimilarityResults<E> getResultsIDs();
	
	public ObjectWithDistance<E> getFirst();
	
	public Collection<IFeaturesCollector_Labeled_HasID> getFCs(FeaturesCollectorsArchives archive) throws ArchiveException;
}
