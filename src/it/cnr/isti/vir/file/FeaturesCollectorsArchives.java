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
package it.cnr.isti.vir.file;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDInteger;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.index.FeaturesCollectorsArchiveSearch;
import it.cnr.isti.vir.similarity.index.FeaturesCollectorsArchiveSearch_multiSim;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueDMax;
import it.cnr.isti.vir.similarity.results.SimilarityResults;
import it.cnr.isti.vir.util.RandomOperations;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class FeaturesCollectorsArchives {

	private FeaturesCollectorsArchive[] archive;
	
	static public FilenameFilter filter = new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return name.endsWith(".dat");
	    }
	};
	
	int size;
	int[] archiveStartIndex;
	
	
	public static FeaturesCollectorsArchives getFromSingleArchive(String archiveFName) throws SecurityException, IllegalArgumentException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		return new FeaturesCollectorsArchives( new FeaturesCollectorsArchive( archiveFName ) );
	}
	
	/**
	 * @param archiveDirName
	 * @param readIDs			specify if IDs are needed
	 */
	public FeaturesCollectorsArchives(String archiveDirName, boolean readIDs) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		this(new File(archiveDirName), readIDs);
	}

	public FeaturesCollectorsArchives(FeaturesCollectorsArchive archiveFiles) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		this.archive = new FeaturesCollectorsArchive[1];
		archive[0] = archiveFiles;
		archiveStartIndex = new int[archive.length];
		size = 0;
		for ( int i=0; i<archive.length; i++) {			
			archiveStartIndex[i] = size;
			size += archive[i].size();
		}	
	}
	
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
		this(archiveFiles, true);
	}
	
	public FeaturesCollectorsArchives(File[] archiveFiles, boolean readIDs) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		archive = new FeaturesCollectorsArchive[archiveFiles.length];
		archiveStartIndex = new int[archiveFiles.length];
		size = 0;
		for ( int i=0; i<archive.length; i++) {			
			archiveStartIndex[i] = size;
			archive[i] = new FeaturesCollectorsArchive(archiveFiles[i], readIDs);
			size += archive[i].size();
		}		
	}
	
	public FeaturesCollectorsArchives(String[] archiveFiles) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		this(archiveFiles, true);
	}
	
	public FeaturesCollectorsArchives(String[] archiveFiles, boolean readIDs) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		archive = new FeaturesCollectorsArchive[archiveFiles.length];
		archiveStartIndex = new int[archiveFiles.length];
		size = 0;
		for ( int i=0; i<archive.length; i++) {			
			archiveStartIndex[i] = size;
			archive[i] = new FeaturesCollectorsArchive(archiveFiles[i], readIDs);
			size += archive[i].size();
		}		
	}
	
	public FeaturesCollectorsArchives(File archiveFile) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		this(archiveFile, true);
	}
	
	public FeaturesCollectorsArchives(File archiveFile, boolean readIDs) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		if ( archiveFile.isDirectory() ) {
			File[] archiveFiles = archiveFile.listFiles(filter);
			
			archive = new FeaturesCollectorsArchive[archiveFiles.length];
			archiveStartIndex = new int[archiveFiles.length];
			size = 0;
			for ( int i=0; i<archive.length; i++) {			
				archiveStartIndex[i] = size;
				archive[i] = new FeaturesCollectorsArchive(archiveFiles[i], readIDs);
				size += archive[i].size();
			}
			
		} else {
			archive = new FeaturesCollectorsArchive[1];
			archive[0] = new FeaturesCollectorsArchive(archiveFile, readIDs);
			archiveStartIndex = new int[1];
			archiveStartIndex[0]=0;
			size += archive[0].size();
		}
	}

	public final AbstractFeaturesCollector get(AbstractID id) throws ArchiveException {
		// TODO
		for ( int i=0; i<archive.length; i++) {
			AbstractFeaturesCollector temp = archive[i].get(id);
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
	
	public final AbstractFeaturesCollector getRandom() throws ArchiveException {
		return get(RandomOperations.getInt(this.size));
	}

	public final AbstractFeaturesCollector get(int index) throws ArchiveException {
		int temp = getArchiveIndex(index);
		return archive[temp].get(index-archiveStartIndex[temp]);
	}
	
	public void close() throws IOException {
		for ( int i=archiveStartIndex.length-1; i>=0; i-- ) {
			archive[i].close();
		}
	}
	
	public final ArrayList<AbstractFeaturesCollector> getAll( )  throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		ArrayList<AbstractFeaturesCollector> res = new ArrayList(size);
		for ( int i=0; i<archive.length; i++) {
			res.addAll( archive[i].getAll() );
		}
		return res;
	}

	public int[] getIntIDs() throws IOException {
		int[] res = new int[size()];
		int iRes = 0;
		Log.info("Getting integer ids");
		for (int i = 0; i < archive.length; i++) {
			
			AbstractID[] ids = archive[i].getIDs();
			for ( int iID=0; iID<ids.length; iID++) {
				res[iRes++] = ((IDInteger) ids[iID]).id;
			}
			Log.info(iRes + "/" + size());
		}
		return res;
	}
	
	public synchronized SimilarityResults[] getKNN(Collection<AbstractFeaturesCollector> qObj,
			int k, final ISimilarity sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {
		AbstractFeaturesCollector[] tArr = new AbstractFeaturesCollector[qObj.size()];
		qObj.toArray(tArr);
		return getKNN(tArr, k, sim, onlyID);
	}
	
	public synchronized SimilarityResults[] getRange(Collection<AbstractFeaturesCollector> qObj,
			double range, final ISimilarity sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {
		AbstractFeaturesCollector[] tArr = new AbstractFeaturesCollector[qObj.size()];
		qObj.toArray(tArr);
		return getRange(tArr, range, sim, onlyID);
	}
	
	public synchronized SimilarityResults[] getKNN(AbstractFeaturesCollector[] qObj,
			int k, final ISimilarity sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {

		SimPQueueDMax[] kNNQueue = new SimPQueueDMax[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueDMax(k);
		}

		for (int i = 0; i < archive.length; i++) {
			Log.info_verbose("... searching in archive " + archive[i].getfile());
			FeaturesCollectorsArchiveSearch search = new FeaturesCollectorsArchiveSearch(archive[i]);
			search.search(qObj, kNNQueue, sim, onlyID);
		}

		SimilarityResults[] res = new SimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();
			res[i].setQuery(qObj[i]);
		}

		return res;
	}
	
	public synchronized SimilarityResults[] getRange(AbstractFeaturesCollector[] qObj,
			double range, final ISimilarity sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {

		SimPQueueDMax[] kNNQueue = new SimPQueueDMax[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueDMax(range);
		}

		for (int i = 0; i < archive.length; i++) {
			Log.info_verbose("... searching in archive " + archive[i].getfile());
			FeaturesCollectorsArchiveSearch search = new FeaturesCollectorsArchiveSearch(archive[i]);
			search.search(qObj, kNNQueue, sim, onlyID);
		}

		SimilarityResults[] res = new SimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();
			res[i].setQuery(qObj[i]);
		}

		return res;
	}
	
	public synchronized SimilarityResults[][] getKNN_multiSim(AbstractFeaturesCollector[] qObj,
			int k, final ISimilarity[] sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, InterruptedException {

		SimPQueueDMax[][] kNNQueue = new SimPQueueDMax[sim.length][qObj.length];
		for (int iS = 0; iS < sim.length; iS++) {
			for (int i = 0; i < qObj.length; i++) {
				kNNQueue[iS][i] = new SimPQueueDMax(k);
			}
		}

		for (int i = 0; i < archive.length; i++) {
			Log.info_verbose("... searching in archive " + archive[i].getfile());
			FeaturesCollectorsArchiveSearch_multiSim search = new FeaturesCollectorsArchiveSearch_multiSim(archive[i]);
			
			search.getKNNs(qObj, kNNQueue, sim, onlyID);
		}

		SimilarityResults[][] res = new SimilarityResults[sim.length][qObj.length];
		for (int iS = 0; iS < sim.length; iS++) {			
			for (int i = 0; i < qObj.length; i++) {
				res[iS][i] = kNNQueue[iS][i].getResults();
			}
		}

		return res;
	}
	
}
