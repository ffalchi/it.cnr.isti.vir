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

import gnu.trove.list.array.TLongArrayList;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.similarity.ISimilarity;
import it.cnr.isti.vir.similarity.metric.IMetric;
import it.cnr.isti.vir.similarity.pqueues.SimPQueueArr;
import it.cnr.isti.vir.similarity.results.ISimilarityResults;
import it.cnr.isti.vir.util.Log;
import it.cnr.isti.vir.util.ParallelOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FeaturesCollectorsArchive implements Iterable<AbstractFeaturesCollector> {

	public static final long fileID = 0x5a25287d3aL;

	public static final int version = 3;

	private final TLongArrayList positions;
	private final ArrayList<AbstractID> ids;
	// private final HashMap<IID,Long> idOffsetMap;
	private final HashMap<AbstractID, Integer> idPosMap;

	private final RandomAccessFile rndFile;
	private final File f;
	private final File offsetFile;
	private final File idFile;
//	private final FeatureClassCollector featuresClasses;
	private final Constructor fcClassConstructor;
	private final Constructor fcClassConstructor_NIO;

	private final Class<? extends AbstractID> idClass;

	private boolean changed = false;

	private final Class<? extends AbstractFeaturesCollector> fcClass;

//	public FeatureClassCollector getFeaturesClasses() {
//		return featuresClasses;
//	}

	public Class<? extends AbstractFeaturesCollector> getFcClass() {
		return fcClass;
	}

	public int size() {
		return positions.size();
	}

	public Class<? extends AbstractID> getIDClass() {
		return idClass;
	}

	public final AbstractID getID(int i) {
		return ids.get(i);
	}
	

	public FeaturesCollectorsArchive getSameType(File file) throws Exception {
		return new FeaturesCollectorsArchive(file, idClass, fcClass);
	}
	
	
	public static  FeaturesCollectorsArchive create(File file ) throws Exception {
		return new FeaturesCollectorsArchive(file, IDString.class, FeaturesCollectorArr.class );
	}
	
	public static  FeaturesCollectorsArchive create(File file, Class<? extends AbstractID> idclass ) throws Exception {
		return new FeaturesCollectorsArchive(file, idclass, FeaturesCollectorArr.class );
	}

	public static  FeaturesCollectorsArchive create(File file, Class<? extends AbstractID> idclass, Class<? extends AbstractFeaturesCollector> fcClass ) throws Exception {
		return new FeaturesCollectorsArchive(file, idclass, fcClass );
	}

	public FeaturesCollectorsArchive(String fileName ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		this(new File(fileName));
	}
	
	public FeaturesCollectorsArchive(String fileName, boolean readIDs ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		this(new File(fileName), readIDs);
	}

	public FeaturesCollectorsArchive(File file ) throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		this(file, true);
	}

	
	
	/**
	 * @param file	File for the new archive
	 * @param featuresClasses Features
	 * @param idClass Class for the IDs
	 * @param fcClass Class for the Features Collection
	 * @throws Exception
	 */
	public FeaturesCollectorsArchive(
			File file,
			//FeatureClassCollector featuresClasses,
			Class<? extends AbstractID> idClass,
			Class<? extends AbstractFeaturesCollector> fcClass)
					throws Exception {
		
		if ( file.exists() && !file.delete() )
			throw new Exception("Unable to delete file " + file.getAbsolutePath());
		rndFile = new RandomAccessFile(file, "rw");
		this.f = file;
		//this.featuresClasses = featuresClasses;
		this.idClass = idClass;
		this.fcClass = fcClass;
		this.fcClassConstructor = getFCConstructor(fcClass);
		this.fcClassConstructor_NIO = getFCConstructor_NIO(fcClass);
		this.positions = new TLongArrayList();
		this.ids = new ArrayList();
		// this.idOffsetMap = new HashMap();
		this.idPosMap = new HashMap();
		offsetFile = new File(getIDFileName(file));
		idFile = new File(getOffsetFileName(file));

		writeIntro(rndFile, idClass, fcClass);
	}

	public static final void writeIntro(DataOutput out,
			Class<? extends AbstractID> idClass, Class<? extends AbstractFeaturesCollector> fcClass)
			throws Exception {
		out.writeLong(fileID);
		out.writeInt(version);
		FeaturesCollectors.writeClass(fcClass, out);
		//featuresClasses.writeData(out);
		IDClasses.writeClass(idClass, out);
	}

	protected static final Constructor<? extends AbstractFeaturesCollector> getFCConstructor(Class<? extends AbstractFeaturesCollector> c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(DataInput.class);
	}

	protected static final Constructor<? extends AbstractFeaturesCollector> getFCConstructor_NIO(Class<? extends AbstractFeaturesCollector> c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(ByteBuffer.class);
	}
	
	public synchronized void add(AbstractFeature f, AbstractID id ) throws ArchiveException, IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		add(fcClass.getConstructor(AbstractFeature.class, AbstractID.class).newInstance(f, id));
	}
	
	public synchronized void add(AbstractFeaturesCollector fc) throws ArchiveException, IOException {

		
		int currPos = positions.size();
		positions.add(rndFile.length());

		rndFile.seek(rndFile.length());
		
		if (idClass != null) {
			AbstractID id = ((IHasID) fc).getID();
			if (!idClass.isInstance(id)) {
				throw new ArchiveException("Object has a wrong ID class: "
						+ idClass + " requested, " + id.getClass() + " found.");
			}
			ids.add(id);
			
			idPosMap.put(id, currPos);
		}

		if (fcClass == null) {
			FeaturesCollectors.writeData(rndFile, fc);
		} else {
			if (fcClass.isInstance(fc)) {
				fc.writeData(rndFile);
			} else {
				throw new ArchiveException("FeaturesCollector class inserted ("
						+ fc.getClass() + ") diffear from expected (" + fcClass
						+ ")");
			}
		}

		changed = true;
	}

	public final File getfile() {
		return f;
	}
	
	public final AbstractFeaturesCollector[] getAllArray() throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		Collection<AbstractFeaturesCollector> tRes = getAll();
		AbstractFeaturesCollector[] res = new AbstractFeaturesCollector[tRes.size()];
		tRes.toArray(res);
		return res;
	}

	
	public final ArrayList<AbstractFeaturesCollector> getAll() {
		ArrayList<AbstractFeaturesCollector> arr = new ArrayList(size());
		for ( AbstractFeaturesCollector f : this) {
			arr.add(f);
		}
		return arr;
	}

//	public final ArrayList<AbstractFeaturesCollector> getAll() throws IOException,
//			SecurityException, NoSuchMethodException, IllegalArgumentException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		DataInputStream in = new DataInputStream(new BufferedInputStream(
//				new FileInputStream(f)));
//		ArrayList<AbstractFeaturesCollector> arr = new ArrayList(size());
//
//		if (in.readLong() != fileID) {
//			System.err
//					.println("The file does not appear to be a FeatureArchive");
//		}
//
//		int fileVersion = in.readInt();
//
//		if (fileVersion > 1) {
//			Class<? extends AbstractFeaturesCollector> fcClass = FeaturesCollectors.readClass(in);
//			Constructor<? extends AbstractFeaturesCollector> fcClassConstructor = getFCConstructor(fcClass);
//			new FeatureClassCollector(in);
//			IDClasses.readClass(in);
//
//			arr = new ArrayList();
//			while (in.available() != 0) {
//				if (fcClassConstructor == null) {
//					arr.add(FeaturesCollectors.readData(in));
//				} else {
//					arr.add((AbstractFeaturesCollector) fcClassConstructor
//							.newInstance(in));
//				}
//
//			}
//		} else {
//			// old IO
//			long indexOffSet = in.readLong();
//
//			FeatureClassCollector featuresClasses = new FeatureClassCollector(
//					in); // FeaturesCollectors.getClass( file.readInt() );
//			Class<? extends AbstractID> idClass = IDClasses.readClass(in);
//
//			for (int i = 0; i < ids.size(); i++) {
//				arr.add(FeaturesCollectors.readData(in));
//			}
//		}
//
//		return arr;
//	}
	
//	public synchronized ISimilarityResults[] getKNN_IDs(AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		return getKNN(qObj, k, sim, true);
//	}
//
//	public synchronized ISimilarityResults[] getKNN(AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		return getKNN(qObj, k, sim, false);
//	}	
//
//	public ISimilarityResults[] getKNN(AbstractFeaturesCollector[] qObj, int k,
//			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
//			NoSuchMethodException, IllegalArgumentException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		
//	}

//	public synchronized void getKNNs(AbstractFeaturesCollector[] qObj, SimPQueueArr[][] kNNQueue, final ISimilarity[] sim, final boolean onlyID)
//			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
//
//
//		FeaturesCollectorsArchiveSearch_multiSim search = new FeaturesCollectorsArchiveSearch_multiSim(this);
//		search.getKNNs(qObj, kNNQueue, sim, onlyID);
//				
//	}
//
//	public synchronized void getKNN(AbstractFeaturesCollector[] qObj, SimPQueueArr[] kNNQueue, final ISimilarity sim, final boolean onlyID)
//		throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
//			FeaturesCollectorsArchiveSearch search = new FeaturesCollectorsArchiveSearch(this);
//			search.getKNN(qObj, kNNQueue, sim, onlyID);
//	}
	

	
//	public synchronized void getKNN(AbstractFeaturesCollector[] qObj, SimPQueueArr[] kNNQueue, final ISimilarity sim, final boolean onlyID)
//			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
//
//		boolean parallel = true;
//		if (!parallel) {
//			int iObj = 0;
//			for (AbstractFeaturesCollector fc : this) {
//
//				for (int i = 0; i < kNNQueue.length; i++) {
//					double dist = sim.distance(qObj[i], fc,  kNNQueue[i].excDistance );
//					if ( dist < 0 ) continue;
//					if ( onlyID)
//						kNNQueue[i].offer(((IHasID) fc).getID(), dist );
//					else 
//						kNNQueue[i].offer(fc, dist);
//				}
//
//				if ( (iObj+1) % 100 == 0 ) {
//					System.out.println( (iObj+1) + " of " + this.size() + " processed.");
//				}
//				iObj++;
//			}				
//		
//		} else {
//			final int parallelBatchSize = 100000;
//			
//			int nObj = this.size();
//			Iterator<AbstractFeaturesCollector> it = this.iterator();
//			// iterates through multiple batches
//			for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
//				
//				int batchSize = parallelBatchSize;
//				if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
//				AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
//				
//				// reading objects in batch
//				for ( int i=0; i<objects.length; i++  ) {
//					objects[i] = it.next();
//					iObj++;
//				}
//				
//				// kNNQueues are performed in parallels
//				final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / ParallelOptions.nThreads);
//				final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
//				int ti = 0;
//		        Thread[] thread = new Thread[nThread];
//		        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
//		        	int to = from+nQueriesPerThread-1;
//		        	if ( to >= qObj.length ) to =qObj.length-1;
//		        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from,to,objects, onlyID) ) ;
//		        	thread[ti].start();
//		        	ti++;
//		        }
//		        
//    for ( Thread t : thread ) {
//		if ( t != null ) t.join();
//    }
//				
//				Log.info((iObj+objects.length) + "/" + nObj);
//			}
//		}
//				
//
//	}
	
	
//	public synchronized void getKNN(AbstractFeaturesCollector[] qObj, SimPQueueArr[] kNNQueue, final ISimilarity sim, final boolean onlyID)
//			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
//		DataInputStream in = new DataInputStream(new BufferedInputStream(
//				new FileInputStream(f)));
//		
//		if (in.readLong() != fileID) {
//			// System.err.println("The file does not appear to be a FeatureArchive");
//		}
//
//		int fileVersion = in.readInt();
//		
//		final int nObj = positions.size();
//		
//		if (fileVersion > 1) {
//			Class fcClass = FeaturesCollectors.readClass(in);
//			Constructor fcClassConstructor = getFCConstructor(fcClass);
//			new FeatureClassCollector(in);
//			IDClasses.readClass(in);
//
//			boolean parallel = true;
//			if (!parallel) {
//				
//				for (int iObj = 0; iObj < nObj; iObj++) {
//					// while( in.available() != 0) {IFeaturesCollector fc =
//					AbstractFeaturesCollector fc = null;
//					if (fcClassConstructor == null) {
//						fc = FeaturesCollectors.readData(in);
//					} else {
//						fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
//					}
//
//					for (int i = 0; i < kNNQueue.length; i++) {
//						double dist = sim.distance(qObj[i], fc,  kNNQueue[i].excDistance );
//						if ( dist < 0 ) continue;
//						if ( onlyID)
//							kNNQueue[i].offer(((IHasID) fc).getID(), dist );
//						else 
//							kNNQueue[i].offer(fc, dist);
//					}
//
//					if ( (iObj+1) % 100 == 0 ) {
//						System.out.println( (iObj+1) + " of " + nObj + " processed.");
//					}
//				}				
//			
//			} else {
//				final int parallelBatchSize = 100000;
//				
//				
//				// iterates through multiple batches
//				for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
//					int batchSize = parallelBatchSize;
//					if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
//					AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
//					
//					// reading objects in batch
//					for ( int i=0; i<objects.length; i++  ) {
//						AbstractFeaturesCollector fc = null;
//						if (fcClassConstructor == null) {
//							fc = FeaturesCollectors.readData(in);
//						} else {
//							fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
//						}
//						objects[i] = fc;
//					}
//					
//					// kNNQueues are performed in parallels
//					final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / ParallelOptions.nThreads);
//					final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
//					int ti = 0;
//			        Thread[] thread = new Thread[nThread];
//			        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
//			        	int to = from+nQueriesPerThread-1;
//			        	if ( to >= qObj.length ) to =qObj.length-1;
//			        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from,to,objects, onlyID) ) ;
//			        	thread[ti].start();
//			        	ti++;
//			        }
//			        
//				    for ( Thread t : thread ) {
//						if ( t != null ) t.join();
//				    }
//					
//					Log.info((iObj+parallelBatchSize) + "/" + nObj);
//				}
//				
//			}
//		}
//	}
	
//	public synchronized ISimilarityResults getKNN(AbstractFeaturesCollector qObj, int k,
//			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
//			NoSuchMethodException, IllegalArgumentException,
//			InstantiationException, IllegalAccessException,
//			InvocationTargetException {
//		
//		AbstractFeaturesCollector[] qObjs = {qObj};
//		return getKNN( qObjs, k, sim, onlyID)[0];
//	}

	public static final ArrayList<AbstractFeaturesCollector> getAll(File file)
			throws IOException, SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		ArrayList<AbstractFeaturesCollector> arr = null;

		if (in.readLong() != fileID) {
			System.err
					.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();

		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			arr = new ArrayList();
			while (in.available() != 0) {
				if (fcClassConstructor == null) {
					arr.add(FeaturesCollectors.readData(in));
				} else {
					arr.add((AbstractFeaturesCollector) fcClassConstructor.newInstance(in));
				}

			}
		} else {
			// old IO
			long indexOffSet = in.readLong();

			FeatureClassCollector featuresClasses = new FeatureClassCollector(
					in); // FeaturesCollectors.getClass( file.readInt() );
			Class idClass = IDClasses.readClass(in);

			RandomAccessFile rndFile = new RandomAccessFile(file, "rw");
			rndFile.seek(indexOffSet);
			int size = rndFile.readInt();
			arr = new ArrayList(size);

			System.out.println("--> The archive contains " + size);

			for (int i = 0; i < size; i++) {
				arr.add(FeaturesCollectors.readData(in));
			}
		}

		return arr;
	}

	public final AbstractID getIdAt(int i) {
		return ids.get(i);
	}
	
	static final public void readHeader(DataInputStream in) throws IOException {
		
		in.readLong();
		int version = in.readInt();
		// Reading classes
		FeaturesCollectors.readClass(in);
		if ( version < 3)
			new FeatureClassCollector(in);
		IDClasses.readClass(in);
	}
	
	public FeaturesCollectorsArchive open(File file, boolean readIDs) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		return new FeaturesCollectorsArchive(file, readIDs);
	}
	
	public static FeaturesCollectorsArchive open(File file ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
		return new FeaturesCollectorsArchive(file );
	}

	
	public FeaturesCollectorsArchive(File file, boolean readIDs) throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {

		System.out.println("Opening FeaturesCollectorsArchive: " + file.getAbsolutePath());
		if ( !file.exists()) {
			throw new IOException("The file ["+ file.getAbsolutePath() +"] was not found");
		}
		offsetFile = new File(getIDFileName(file));
		idFile = new File(getOffsetFileName(file));
		this.f = file;
		rndFile = new RandomAccessFile(file, "rw");

		if (rndFile.readLong() != fileID) {
			throw new IOException("The file ["+ file.getAbsolutePath() +"] does not appear to be a FeatureArchive");
		}

		int fileVersion = rndFile.readInt();

		if (fileVersion > 1) {

			// Reading classes
			fcClass = FeaturesCollectors.readClass(rndFile);
			fcClassConstructor = getFCConstructor(fcClass);
			fcClassConstructor_NIO = getFCConstructor_NIO(fcClass);
			
			if ( fileVersion == 2 ) {
				// legacy
				//featuresClasses = new FeatureClassCollector(rndFile);
				new FeatureClassCollector(rndFile);
			} else {
				// nothing is written
			}
			idClass = IDClasses.readClass(rndFile);

			
			
			// OFFSETE & ID FILES 
			if (!offsetFile.exists() || !idFile.exists()
					|| file.lastModified() > offsetFile.lastModified()
					|| file.lastModified() > idFile.lastModified()) {

				if (!offsetFile.exists())
					System.out.println("Offsets file not found.");
				else if (file.lastModified() > offsetFile.lastModified())
					System.out.println("offsetFile file out of date will be delete and rebuilt");
				if (!idFile.exists())
					System.out.println("IDs file not found.");
				else if (file.lastModified() > idFile.lastModified())
					System.out.println("IDs file file out of date will be delete and rebuilt");
				
				
				// Reading all
				System.out.print("Analysing binary file... ");
				positions = new TLongArrayList();
				ids = new ArrayList();
				long offset = 0;
				// reading
				while ((offset = rndFile.getFilePointer()) < rndFile.length()) {

					positions.add(offset);

					AbstractFeaturesCollector fc = null;
					if (fcClassConstructor == null) {
						fc = FeaturesCollectors.readData(rndFile);
					} else {
						fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(rndFile);
					}

					ids.add(((IHasID) fc).getID());
					
					if ( positions.size() % 10000 == 0 ) System.out.println(positions.size());
				}
				System.out.println("done");

				

				System.out.print("Creating IDs HashTable... ");
				// idOffsetMap = new HashMap(2*positions.size());
				// for ( int i=0; i<positions.size(); i++ ) {
				// idOffsetMap.put(ids.get(i), positions.get(i));
				// }

				idPosMap = new HashMap(2 * positions.size());
				for (int i = 0; i < positions.size(); i++) {
					idPosMap.put(ids.get(i), i);
				}
				System.out.println("done");

				createIndexFiles();
			} else {

				// READING INDEX FILE
				RandomAccessFile inOffset = new RandomAccessFile(offsetFile,"r");
				// RandomAccessFile inId = new RandomAccessFile( idFile, "r" );

				int size = (int) (inOffset.length() / 8);
				System.out.println("Archive " + file.getAbsolutePath()
						+ " contains " + size + " objects.");

				// Reading offsets
				long[] tempPositions = new long[size];
				byte[] byteArray = new byte[size * 8];
				LongBuffer inLongBuffer = ByteBuffer.wrap(byteArray).asLongBuffer();
				inOffset.readFully(byteArray);
				inLongBuffer.get(tempPositions, 0, size);
				positions = new TLongArrayList(tempPositions);

				DataInputStream idInput = new DataInputStream(new BufferedInputStream(new FileInputStream(idFile)));

				// Reading ids
				if (idClass != null) {
					if ( readIDs ) {
						// idOffsetMap = new HashMap(2*size);
						idPosMap = new HashMap(2 * size);
	
						System.out.print("Reading IDs... ");
						ids = new ArrayList(Arrays.asList(IDClasses.readArray(idInput, size, idClass)));
						System.out.println("done");
	
						System.out.print("Creating IDs HashTable... ");
						size = positions.size();
						for (int i = 0; i < size; i++) {
							// idOffsetMap.put(ids.get(i), positions.get(i));
							idPosMap.put(ids.get(i), i);
						}
						System.out.println("done");
					} else {
						ids = null;
						idPosMap = null;
					}
				} else {
					System.out.println("--> The archive does not contains IDs");
					// no IDs
					ids = null;
					// idOffsetMap = null;
					idPosMap = null;
				}

			}

		} else {

			// VERSION 1
			fcClass = null;
			fcClassConstructor = null;
			fcClassConstructor_NIO = null;
			// old IO
			long indexOffSet = rndFile.readLong();

			new FeatureClassCollector(rndFile); 	// FeaturesCollectors.getClass(
																	//	file.readInt()
																	// );
			// featureCollectorConstructor =
			// featuresCollectorClass.getConstructor(DataInput.class);

			idClass = IDClasses.readClass(rndFile);

			// file.seek(file.length()-8);
			// long indexOffSet = file.readLong();
			rndFile.seek(indexOffSet);
			int size = rndFile.readInt();
			System.out.println("--> The archive contains " + size);
			System.out.println("--> Features Collector Class: " + fcClass);
			//System.out.println("--> Features to consider are: " + featuresClasses);

			// Reading offsets
			long[] tempPositions = new long[size];
			byte[] byteArray = new byte[size * 8];
			LongBuffer inLongBuffer = ByteBuffer.wrap(byteArray).asLongBuffer();
			rndFile.readFully(byteArray);
			inLongBuffer.get(tempPositions, 0, size);
			positions = new TLongArrayList(tempPositions);

			// Reading ids
			if (idClass != null) {
				// idOffsetMap = new HashMap(2*size);
				idPosMap = new HashMap(2 * size);

				System.out.print("Reading IDs... ");
				DataInputStream idInput = new DataInputStream(new BufferedInputStream(new FileInputStream(idFile)));
				ids = new ArrayList(Arrays.asList(IDClasses.readArray(idInput, size, idClass)));
				System.out.println("done");

				System.out.print("Creating IDs HashTable... ");
				size = positions.size();
				for (int i = 0; i < size; i++) {
					// idOffsetMap.put(ids.get(i), positions.get(i));
					idPosMap.put(ids.get(i), i);
				}
				System.out.println("done");
			} else {
				System.out.println("--> The archive does not contains IDs");
				// no IDs
				ids = null;
				// idOffsetMap = null;
				idPosMap = null;
			}
		}
		System.out.println(getInfo());
	}

	public static final String getIDFileName(File file) {
		return file.getAbsolutePath() + ".ids";
	}

	public static final String getOffsetFileName(File file) {
		return file.getAbsolutePath() + ".offs";
	}

	public void createIndexFiles() throws IOException {
		createIndexFiles(offsetFile, idFile, positions, ids);
	}
	
	public static void createIndexFiles(File offsetFile, File idFile, TLongArrayList positions, ArrayList<AbstractID> ids) throws IOException {

		offsetFile.delete();
		idFile.delete();

		DataOutputStream outOffset = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(offsetFile)));
		DataOutputStream outIDs = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(idFile)));

		for (int i = 0; i < positions.size(); i++) {
			outOffset.writeLong(positions.get(i));
			ids.get(i).writeData(outIDs);
		}

		outOffset.close();
		outIDs.close();

	}

	public synchronized AbstractFeaturesCollector get(int i) throws ArchiveException {
		try {
			//System.out.println("i: " + i + ", offset: " + positions.get(i));
			rndFile.seek(positions.get(i));

			byte[] arr = null;
			if (i < positions.size() - 1)
				arr = new byte[(int) (positions.get(i + 1) - positions.get(i))];
			else
				arr = new byte[(int) (rndFile.length() - positions.get(i))];


			if (fcClassConstructor_NIO == null) {
				//return FeaturesCollectors.readData(buf);

				 return FeaturesCollectors.readData(rndFile);

			} else {
				ByteBuffer buf = ByteBuffer.wrap(arr);
				rndFile.read(arr);
				return (AbstractFeaturesCollector) fcClassConstructor_NIO.newInstance(buf);

				// return (IFeaturesCollector)
				// fcClassConstructor.newInstance(rndFile);
			}
		} catch (Exception e) {
			throw new ArchiveException(e);
		}
		// return featureConstructor.newInstance(file);

	}

	public final Long getOffset(int i) {
		return positions.get(i);
	}

	public final Long getOffset(AbstractID id) {
		// return idOffsetMap.get(id);
		return getOffset(idPosMap.get(id));
	}

	public synchronized final boolean contains(AbstractID id) throws ArchiveException {
		// return idOffsetMap.get(id) != null;
		return idPosMap.get(id) != null;
	}

	public synchronized final AbstractFeaturesCollector get(AbstractID id)
			throws ArchiveException {
		Integer i = idPosMap.get(id);
		if (i == null)
			return null;
		return get(i);
	}

	public void close() throws IOException {
		if (changed) {
			createIndexFiles();
		}
		rndFile.close();
	}

	@Override
	public Iterator<AbstractFeaturesCollector> iterator() {
		try {
			return new FeaturesCollectorsArchiveIterator(f, fcClassConstructor );
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public AbstractID[] getIDs() throws IOException {
		if ( ids != null ) {
			AbstractID[] res = new AbstractID[ids.size()];
			ids.toArray(res);
			return res;
		}
		DataInputStream idInput = new DataInputStream(new BufferedInputStream(new FileInputStream(idFile)));
		return IDClasses.readArray(idInput, size(), idClass);
		
	}
	
	
	// For kNN searching
	class InterDistances implements Runnable {
		private final int i1;
		private final AbstractFeaturesCollector[] objs;
		private final ISimilarity sim;
		private final double[][] d;
		
		InterDistances(AbstractFeaturesCollector[] objs, ISimilarity sim, double[][] d, int i1 ) {
			this.i1 = i1;
			this.objs = objs;
			this.sim = sim; 
			this.d = d;
		}

		@Override
		public void run() {
			AbstractFeaturesCollector obj1 = objs[i1];
			int di2=0;
			for ( int i2=i1; i2<objs.length; i2++) {
				d[i1][di2++] = sim.distance(obj1, objs[i2]);
			}
		}
	}
	
	public synchronized void writeInterdistances(File outFile, final IMetric sim ) throws IOException, ArchiveException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		
		int size = size();
		long sizeSqr = (long) size*size;
		int nThread = ParallelOptions.nThreads;
		Log.info_verbose("N of distances to evaluate: " + sizeSqr);
		Log.info_verbose("Outfile final size: " + sizeSqr*8 / 1024 /1024+ " MegaBytes.");
		
		AbstractFeaturesCollector[] obj = new AbstractFeaturesCollector[size];
		Log.info("Reading all objects");
		this.getAll().toArray(obj);
		Log.info("Reading all objects DONE");
		
		Log.info("Allocating memory for distances");
		double[][] dists = new double[size][]; 
		for ( int i=0; i<dists.length; i++) {
			dists[i] = new double[size-i];
		}
		Log.info("Allocating memory for distances done");
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
		
		long start = System.currentTimeMillis();
		
		Thread[] thread = new Thread[nThread];
		for ( int i1=0; i1<size; ) {
			int oldi1 = i1;
			for ( int ti=0; ti<nThread && i1<size; ti++ ) {
				thread[ti] = new Thread( new InterDistances(obj, sim, dists, i1++) ) ;
				thread[ti].start();
			}
	        
	        for ( int ti=0; ti<thread.length && thread[ti] != null; ti++ ) {
	        	try {
					thread[ti].join();
					thread[ti] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
	       
	        // saving output
	        for ( int ii1=oldi1; ii1<i1; ii1++ ) {	        	
	        	//  the first part of the row is taken for symmetry
	        	for ( int i2=0; i2<ii1; i2++) {
	        		out.writeDouble(dists[i2][ii1-i2]);
	        	}
	        	// second part of the row
	        	for ( int i2=ii1; i2<size; i2++) {
	        		out.writeDouble(dists[ii1][i2-ii1]);
	        	}
	        }
			
	        double avgTime = (double) (System.currentTimeMillis() - start) / ( i1 * size) ;
			Log.info_verbose((i1)*size + " done. Extimated time to finish: " + (avgTime*(size-i1)*size)/1000/60+ " min");
		}		
		out.close();
		/*
		System.out.println("Testing:");
		RandomAccessFile rnd = new RandomAccessFile(outFile, "r");
		for (int i=0; i<100000; i++) {
			int i1 = RandomOperations.getInt(0, size-1);
			int i2 = RandomOperations.getInt(0, size-1);
			rnd.seek((long) (i1*size+i2)*8);
			double saved = rnd.readDouble();
			double actual = sim.distance(get(i1), get(i2));
			if ( saved != actual) {
				System.err.println("Saved distance (" + saved+") and actual (" + actual+")+ diffear");
			}
		}
		System.out.println("Testing ended");*/
		
	}
	
	public String getInfo() {
		String tStr ="Archive " + f.getAbsolutePath() + "\n";
		tStr += "--> contains " + positions.size() + "\n";
		tStr += "--> has features collector: " + fcClass + "\n";
		tStr += "--> has ID: " + idClass + "\n";
		return tStr;
	}
}
