package it.cnr.isti.vir.file;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class FeaturesCollectorsArchives {

	private FeaturesCollectorsArchive[] archive;
	
	FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return name.endsWith(".dat");
	    }
	};
	
	int size;
	int[] archiveStartIndex;
	
	public FeaturesCollectorsArchives( FeaturesCollectorsArchive[] archive ) {
		this.archive=archive;
		archiveStartIndex = new int[archive.length];
		size = 0;
		for ( int i=0; i<archive.length; i++) {			
			archiveStartIndex[i] = size;
			size += archive[i].size();
		}	
	}
	
	public int getNArchives() {
		return archive.length;
	}
	
	public FeaturesCollectorsArchive getArchive(int i) {
		return archive[i];
	}
	
	public FeaturesCollectorsArchives(File[] archiveFiles) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		archive = new FeaturesCollectorsArchive[archiveFiles.length];
		archiveStartIndex = new int[archiveFiles.length];
		size = 0;
		for ( int i=0; i<archive.length; i++) {			
			archiveStartIndex[i] = size;
			archive[i] = new FeaturesCollectorsArchive(archiveFiles[i]);
			size += archive[i].size();
		}		
	}
	
	public FeaturesCollectorsArchives(File archiveFile) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if ( archiveFile.isDirectory() ) {
			File[] archiveFiles = archiveFile.listFiles(filter);
			
			archive = new FeaturesCollectorsArchive[archiveFiles.length];
			archiveStartIndex = new int[archiveFiles.length];
			size = 0;
			for ( int i=0; i<archive.length; i++) {			
				archiveStartIndex[i] = size;
				archive[i] = new FeaturesCollectorsArchive(archiveFiles[i]);
				size += archive[i].size();
			}
			
		} else {
			archive = new FeaturesCollectorsArchive[1];
			archive[0] = new FeaturesCollectorsArchive(archiveFile);
			archiveStartIndex = new int[1];
			archiveStartIndex[0]=0;
			size += archive[0].size();
		}
	}

	public final IFeaturesCollector get(AbstractID id) throws ArchiveException {
		// TODO
		for ( int i=0; i<archive.length; i++) {
			IFeaturesCollector temp = archive[i].get(id);
			if ( temp != null ) return temp;
		}
		throw new ArchiveException( "ID " + id + " not found in current FeaturesCollectorArchives.");
	}

	public int size() {
		return size;
	}

	private final int getArchiveIndex( int index ) throws ArchiveException {
		for ( int i=archiveStartIndex.length-1; i>=0; i-- ) {
			if ( index>=archiveStartIndex[i] ) {
				return i;
			}
		}
		throw new ArchiveException( "Index " + index + " not found in current FeaturesCollectorArchives.");
	}

	public final AbstractID getID(int index) throws ArchiveException {
		int temp = getArchiveIndex(index);
		return archive[temp].getID(index-archiveStartIndex[temp]);
	}

	public final IFeaturesCollector get(int index) throws ArchiveException {
		int temp = getArchiveIndex(index);
		return archive[temp].get(index-archiveStartIndex[temp]);
	}
	
	public void close() throws IOException {
		for ( int i=archiveStartIndex.length-1; i>=0; i-- ) {
			archive[i].close();
		}
	}
	
	public final ArrayList<IFeaturesCollector> getAll( )  throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ArrayList<IFeaturesCollector> res = new ArrayList(size);
		for ( int i=0; i<archive.length; i++) {
			res.addAll( archive[i].getAll() );
		}
		return res;
	}

	public synchronized ISimilarityResults[] getKNN(IFeaturesCollector[] qObj, int k,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
			
			SimPQueueArr[] kNNQueue = new SimPQueueArr[qObj.length];
			for (int i = 0; i < kNNQueue.length; i++) {
				kNNQueue[i] = new SimPQueueArr(k);
			}

			for ( int i=0; i<archive.length; i++) { 
				archive[i].getKNN(qObj, kNNQueue, sim, onlyID);
			}

			ISimilarityResults[] res = new ISimilarityResults[kNNQueue.length];
			for (int i = 0; i < kNNQueue.length; i++) {
				res[i] = kNNQueue[i].getResults();
			}

			return res;
		}
	
}
