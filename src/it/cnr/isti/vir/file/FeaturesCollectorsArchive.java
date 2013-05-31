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
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDClasses;
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
import java.util.HashMap;
import java.util.Iterator;

public class FeaturesCollectorsArchive implements Iterable<AbstractFeaturesCollector> {

	public static final long fileID = 0x5a25287d3aL;

	public static final int version = 2;

	private final TLongArrayList positions;
	private final ArrayList<AbstractID> ids;
	// private final HashMap<IID,Long> idOffsetMap;
	private final HashMap<AbstractID, Integer> idPosMap;

	private final RandomAccessFile rndFile;
	private final File f;
	private final File offsetFile;
	private final File idFile;
	private final FeatureClassCollector featuresClasses;
	private final Constructor fcClassConstructor;
	private final Constructor fcClassConstructor_NIO;

	private final Class idClass;

	private boolean changed = false;

	private final Class fcClass;

	public FeatureClassCollector getFeaturesClasses() {
		return featuresClasses;
	}

	public Class getIdClass() {
		return idClass;
	}

	public Class getFcClass() {
		return fcClass;
	}

	public int size() {
		return positions.size();
	}

	public Class getIDClass() {
		return idClass;
	}

	public final AbstractID getID(int i) {
		return ids.get(i);
	}
	

	public FeaturesCollectorsArchive getSameType(File file) throws Exception {
		return new FeaturesCollectorsArchive(file, featuresClasses, idClass, fcClass);
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
			FeatureClassCollector featuresClasses,
			Class<? extends AbstractID> idClass,
			Class<? extends AbstractFeaturesCollector> fcClass)
					throws Exception {
		
		if ( file.exists() && !file.delete() )
			throw new Exception("Unable to delete file " + file.getAbsolutePath());
		rndFile = new RandomAccessFile(file, "rw");
		this.f = file;
		this.featuresClasses = featuresClasses;
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

		writeIntro(rndFile, featuresClasses, idClass, fcClass);
	}

	public static final void writeIntro(DataOutput out,
			FeatureClassCollector featuresClasses, Class idClass, Class fcClass)
			throws Exception {
		out.writeLong(fileID);
		out.writeInt(version);
		FeaturesCollectors.writeClass(fcClass, out);
		featuresClasses.writeData(out);
		IDClasses.writeClass(idClass, out);
	}

	protected static final Constructor getFCConstructor(Class c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(DataInput.class);
	}

	protected static final Constructor getFCConstructor_NIO(Class c)
			throws SecurityException, NoSuchMethodException {
		if (c == null)
			return null;
		return c.getConstructor(ByteBuffer.class);
	}

	public synchronized void add(AbstractFeaturesCollector fc) throws ArchiveException, IOException {

		
		int currPos = positions.size();
		positions.add(rndFile.length());

		rndFile.seek(rndFile.length());
		
		if (idClass != null) {
			AbstractID id = ((IHasID) fc).getID();
			if (!idClass.isInstance(id)) {
				throw new ArchiveException("Object has a wrong ID class: "
						+ idClass + " requeste, " + id.getClass() + " found.");
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
		ArrayList<AbstractFeaturesCollector> tRes = getAll();
		AbstractFeaturesCollector[] res = new AbstractFeaturesCollector[tRes.size()];
		tRes.toArray(res);
		return res;
	}

	public final ArrayList<AbstractFeaturesCollector> getAll() throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		ArrayList<AbstractFeaturesCollector> arr = new ArrayList(size());

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
					arr.add((AbstractFeaturesCollector) fcClassConstructor
							.newInstance(in));
				}

			}
		} else {
			// old IO
			long indexOffSet = in.readLong();

			FeatureClassCollector featuresClasses = new FeatureClassCollector(
					in); // FeaturesCollectors.getClass( file.readInt() );
			Class idClass = IDClasses.readClass(in);

			for (int i = 0; i < ids.size(); i++) {
				arr.add(FeaturesCollectors.readData(in));
			}
		}

		return arr;
	}
	
	public synchronized ISimilarityResults[] getKNN_IDs(AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getKNN(qObj, k, sim, true);
	}

	public synchronized ISimilarityResults[] getKNN(AbstractFeaturesCollector[] qObj, int k, final ISimilarity sim ) throws SecurityException, IllegalArgumentException, IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return getKNN(qObj, k, sim, false);
	}
	
	// For kNN searching
	class kNNThread implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final SimPQueueArr[] knn;
		private final boolean onlyID;
		private final ISimilarity sim;
		private final AbstractFeaturesCollector[] q;

		kNNThread(AbstractFeaturesCollector[] q, ISimilarity sim, SimPQueueArr[] knn, int from, int to, AbstractFeaturesCollector[]  objs, boolean onlyID) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.knn = knn;
			this.onlyID = onlyID;
			this.sim = sim; 
			this.q = q;
		}

		@Override
		public void run() {
			// each query is processed on an independent thread
			for (int iQ = from; iQ<=to; iQ++) {
				//System.out.println(iQ);
				for ( AbstractFeaturesCollector obj : objs ) {
					double dist = sim.distance(q[iQ], obj, knn[iQ].excDistance );
					if ( dist >= 0) {
						if ( onlyID)
							knn[iQ].offer(((IHasID) obj).getID(), dist);
						else 
							knn[iQ].offer(obj, dist);
					}
				}
			}
		}
	}
	
	// For kNN multiple sim searching
	class kNNThread_multiSim implements Runnable {
		private final int from;
		private final int to;
		private final AbstractFeaturesCollector[] objs;
		private final SimPQueueArr[][] knn;
		private final boolean onlyID;
		private final ISimilarity[] sim;
		private final AbstractFeaturesCollector[] q;

		kNNThread_multiSim(AbstractFeaturesCollector[] q, ISimilarity[] sim, SimPQueueArr[][] knn, int from, int to, AbstractFeaturesCollector[]  objs, boolean onlyID) {
			this.from = from;
			this.to = to;
			this.objs = objs;
			this.knn = knn;
			this.onlyID = onlyID;
			this.sim = sim; 
			this.q = q;
		}

		@Override
		public void run() {
			// each query is processed on an independent thread
			for (int iQ = from; iQ<=to; iQ++) {
				//System.out.println(iQ);
				for ( AbstractFeaturesCollector obj : objs ) {
					for ( int iS=0; iS<sim.length;iS++) {
						double dist = sim[iS].distance(q[iQ], obj, knn[iS][iQ].excDistance );
						if ( dist >= 0) {
							if ( onlyID)
								knn[iS][iQ].offer(((IHasID) obj).getID(), dist);
							else 
								knn[iS][iQ].offer(obj, dist);
						}
					}
				}
			}
		}
	}
	
	
	public synchronized void getKNNs(AbstractFeaturesCollector[] qObj, SimPQueueArr[][] kNNQueue, final ISimilarity[] sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		
		if (in.readLong() != fileID) {
			// System.err.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();
		
		final int nObj = positions.size();
		
		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			boolean parallel = true;

			final int parallelBatchSize = 100000;
			
			
			// iterates through multiple batches
			for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
				int batchSize = parallelBatchSize;
				if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
				AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
				
				// reading objects in batch
				for ( int i=0; i<objects.length; i++  ) {
					AbstractFeaturesCollector fc = null;
					if (fcClassConstructor == null) {
						fc = FeaturesCollectors.readData(in);
					} else {
						fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
					}
					objects[i] = fc;
				}
				
				// kNNQueues are performed in parallels
				final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue[0].length / ParallelOptions.nThreads);
				final int nThread = (int) Math.ceil((double) kNNQueue[0].length / nQueriesPerThread);
				int ti = 0;
		        Thread[] thread = new Thread[nThread];
		        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
		        	int to = from+nQueriesPerThread-1;
		        	if ( to >= qObj.length ) to =qObj.length-1;
		        	thread[ti] = new Thread( new kNNThread_multiSim(qObj, sim, kNNQueue, from,to,objects, onlyID) ) ;
		        	thread[ti].start();
		        	ti++;
		        }
		        
		        for ( ti=0; ti<thread.length; ti++ ) {
		        	try {
						thread[ti].join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
				
				Log.info((iObj+parallelBatchSize) + "/" + nObj);
			}
				
		}
	}
	
	
	public synchronized void getKNN(AbstractFeaturesCollector[] qObj, SimPQueueArr[] kNNQueue, final ISimilarity sim, final boolean onlyID)
			throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		
		if (in.readLong() != fileID) {
			// System.err.println("The file does not appear to be a FeatureArchive");
		}

		int fileVersion = in.readInt();
		
		final int nObj = positions.size();
		
		if (fileVersion > 1) {
			Class fcClass = FeaturesCollectors.readClass(in);
			Constructor fcClassConstructor = getFCConstructor(fcClass);
			new FeatureClassCollector(in);
			IDClasses.readClass(in);

			boolean parallel = true;
			if (!parallel) {
				
				for (int iObj = 0; iObj < nObj; iObj++) {
					// while( in.available() != 0) {IFeaturesCollector fc =
					AbstractFeaturesCollector fc = null;
					if (fcClassConstructor == null) {
						fc = FeaturesCollectors.readData(in);
					} else {
						fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
					}

					for (int i = 0; i < kNNQueue.length; i++) {
						double dist = sim.distance(qObj[i], fc,  kNNQueue[i].excDistance );
						if ( dist < 0 ) continue;
						if ( onlyID)
							kNNQueue[i].offer(((IHasID) fc).getID(), dist );
						else 
							kNNQueue[i].offer(fc, dist);
					}

					if ( (iObj+1) % 100 == 0 ) {
						System.out.println( (iObj+1) + " of " + nObj + " processed.");
					}
				}				
			
			} else {
				final int parallelBatchSize = 100000;
				
				
				// iterates through multiple batches
				for (int iObj = 0; iObj < nObj; iObj+=parallelBatchSize ) {
					int batchSize = parallelBatchSize;
					if ( iObj + parallelBatchSize > nObj ) batchSize = nObj-iObj;
					AbstractFeaturesCollector[] objects = new AbstractFeaturesCollector[batchSize];
					
					// reading objects in batch
					for ( int i=0; i<objects.length; i++  ) {
						AbstractFeaturesCollector fc = null;
						if (fcClassConstructor == null) {
							fc = FeaturesCollectors.readData(in);
						} else {
							fc = (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
						}
						objects[i] = fc;
					}
					
					// kNNQueues are performed in parallels
					final int nQueriesPerThread = (int) Math.ceil((double) kNNQueue.length / ParallelOptions.nThreads);
					final int nThread = (int) Math.ceil((double) kNNQueue.length / nQueriesPerThread);
					int ti = 0;
			        Thread[] thread = new Thread[nThread];
			        for ( int from=0; from<qObj.length; from+=nQueriesPerThread) {
			        	int to = from+nQueriesPerThread-1;
			        	if ( to >= qObj.length ) to =qObj.length-1;
			        	thread[ti] = new Thread( new kNNThread(qObj, sim, kNNQueue, from,to,objects, onlyID) ) ;
			        	thread[ti].start();
			        	ti++;
			        }
			        
			        for ( ti=0; ti<thread.length; ti++ ) {
			        	try {
							thread[ti].join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
					
					Log.info((iObj+parallelBatchSize) + "/" + nObj);
				}
				
			}
		}
	}
	
	public synchronized ISimilarityResults[] getKNN(AbstractFeaturesCollector[] qObj, int k,
			final ISimilarity sim, final boolean onlyID) throws IOException, SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		
		SimPQueueArr[] kNNQueue = new SimPQueueArr[qObj.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			kNNQueue[i] = new SimPQueueArr(k);
		}

		getKNN(qObj, kNNQueue, sim, onlyID);

		ISimilarityResults[] res = new ISimilarityResults[kNNQueue.length];
		for (int i = 0; i < kNNQueue.length; i++) {
			res[i] = kNNQueue[i].getResults();
		}

		return res;
	}

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
		in.readInt();
		// Reading classes
		FeaturesCollectors.readClass(in);
		new FeatureClassCollector(in);
		IDClasses.readClass(in);
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
			featuresClasses = new FeatureClassCollector(rndFile);
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

				System.out.println("--> The archive contains " + positions.size());
				System.out.println("--> Features Collector Class: " + fcClass);
				System.out.println("--> Features to consider are: "
						+ featuresClasses);

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

			featuresClasses = new FeatureClassCollector(rndFile); 	// FeaturesCollectors.getClass(
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
			System.out.println("--> Features to consider are: " + featuresClasses);

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
	/*
	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses)
			throws Exception {
		return create(inFile, outFile, featuresClasses, null, null);
	}

	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses, Class idClass)
			throws Exception {
		return create(inFile, outFile, featuresClasses, idClass, null);
	}

	public static final FeaturesCollectorsArchive create(File inFile,
			File outFile, FeatureClassCollector featuresClasses, Class idClass,
			HashSet<AbstractID> ids) throws Exception {
		DataInputStream in = new DataInputStream(new BufferedInputStream(
				new FileInputStream(inFile)));
		FeaturesCollectorsArchiveBuilder builder = new FeaturesCollectorsArchiveBuilder(
				outFile, featuresClasses, idClass);

		while (in.available() != 0) {
			IFeaturesCollector fc = FeaturesCollectors.readData(in);

			if (ids == null || ids.contains(((IHasID) fc).getID()))
				builder.add(fc);

			if (builder.size() % 1000 == 0)
				System.out.println(builder.size());
		}

		builder.close();

		return new FeaturesCollectorsArchive(outFile);
	}
*/
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
		// if ( idClass != null ) {
		// Long tempOffset = idOffsetMap.get(id);
		// if ( tempOffset == null ) {
		// return null;
		// }
		// try {
		// rndFile.seek(tempOffset);
		// if ( fcClassConstructor == null ) {
		// return FeaturesCollectors.readData(rndFile);
		// } else {
		// return (IFeaturesCollector) fcClassConstructor.newInstance(rndFile);
		// }
		// } catch (Exception e) {
		// throw new ArchiveException(e);
		// }
		//
		// } else {
		// throw new
		// ArchiveException("The archive does not contain any ID information");
		// }
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
}
