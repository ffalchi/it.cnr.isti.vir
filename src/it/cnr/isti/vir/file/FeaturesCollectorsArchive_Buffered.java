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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.file;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IDString;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Fabrizio
 */
public class FeaturesCollectorsArchive_Buffered {

	private final DataOutputStream out;
	
	private final File f;
	private final Constructor fcClassConstructor;

	private final Class<? extends AbstractID> idClass;
	private final Class<? extends AbstractFeaturesCollector> fcClass;

	File offsetFile;
	File idFile;
	
	int size=0;
	
	private boolean closed = false;
	
	public final int size() {
		return size;
	}
	
	public static  FeaturesCollectorsArchive_Buffered create(File file ) throws Exception {
		return new FeaturesCollectorsArchive_Buffered(file, IDString.class, FeaturesCollectorArr.class );
	}
	
	
	public static  FeaturesCollectorsArchive_Buffered create(
			File file,
			Class<? extends AbstractID> idclass ) throws Exception {
		return new FeaturesCollectorsArchive_Buffered(file, idclass, FeaturesCollectorArr.class );
	}
	
	public static  FeaturesCollectorsArchive_Buffered create(
			String fileName,
			Class<? extends AbstractID> idclass,
			Class<? extends AbstractFeaturesCollector> fcClass ) throws Exception {
		return new FeaturesCollectorsArchive_Buffered(new File(fileName), idclass, fcClass );
	}
	
	public static  FeaturesCollectorsArchive_Buffered create(
			File file,
			Class<? extends AbstractID> idclass,
			Class<? extends AbstractFeaturesCollector> fcClass ) throws Exception {
		return new FeaturesCollectorsArchive_Buffered(file, idclass, fcClass );
	}
	
	public static  FeaturesCollectorsArchive_Buffered createAs(String newFile, FeaturesCollectorsArchive archive ) throws Exception {
		return createAs( new File(newFile), archive);
	}
	
	public static  FeaturesCollectorsArchive_Buffered createAs(String newFile, String origFile ) throws Exception {
		return createAs( new File(newFile), new File(origFile));
	}
	
	public static  FeaturesCollectorsArchive_Buffered createAs(File newFile, File origFile ) throws Exception {
		FeaturesCollectorsArchive archive = new FeaturesCollectorsArchive(origFile, false);
		FeaturesCollectorsArchive_Buffered res =  FeaturesCollectorsArchive_Buffered.createAs(newFile, archive );
		archive.close();
		return res;
	}
	
	public static  FeaturesCollectorsArchive_Buffered createAs(File file, FeaturesCollectorsArchive archive ) throws Exception {
		return new FeaturesCollectorsArchive_Buffered(file, archive.getIDClass(), archive.getFcClass() );
	}
	
	/**
	 * Create an empty archive of FeaturesCollector. FeaturesCollectors are buffered and written to disk while added. 
	 * @param file The file where the archive will be written
	 * @param idClass The class, inherited from {@link it.cnr.isti.vir.id.AbstractID} used to represent the ID 
	 * @param fcClass The class, inherited from {@link it.cnr.isti.vir.id.AbstractLabel} used to represent the class label
	 * @throws Exception TODO
	 */
	public FeaturesCollectorsArchive_Buffered(File file,
			Class<? extends AbstractID> idClass, Class<? extends AbstractFeaturesCollector> fcClass)
			throws Exception {
		this(file, idClass, fcClass, true);
	}
	public FeaturesCollectorsArchive_Buffered(File file,
			Class<? extends AbstractID> idClass, Class<? extends AbstractFeaturesCollector> fcClass, boolean saveIDs)
			throws Exception {
		
		file.delete();
		
		out = new DataOutputStream( 
				new BufferedOutputStream(new FileOutputStream(file))
				);

		this.f = file;
		this.idClass = idClass;
		this.fcClass = fcClass;
		this.fcClassConstructor = FeaturesCollectorsArchive.getFCConstructor(fcClass);

		FeaturesCollectorsArchive.writeIntro(out, idClass,	fcClass);
		
		idFile = new File(FeaturesCollectorsArchive.getIDFileName(file));
		offsetFile = new File(FeaturesCollectorsArchive.getOffsetFileName(file));

	}
	
	public synchronized void add(AbstractFeature f, AbstractID id ) throws ArchiveException, IOException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		add(fcClass.getConstructor(AbstractFeature.class, AbstractID.class).newInstance(f, id));
	}	

	public synchronized void add(AbstractFeaturesCollector fc) throws ArchiveException, IOException {

		if (fcClass == null) {
			FeaturesCollectors.writeData(out, fc);
		} else {
			if (fcClass.isInstance(fc)) {
				fc.writeData(out);
			} else {
				throw new ArchiveException("FeaturesCollector class inserted ("
						+ fc.getClass() + ") differ from expected (" + fcClass
						+ ")");
			}
		}

		size++;
	}


	public void close() throws IOException {
		if ( closed ) return;
		out.close();
		
		closed = true;
	}
	
	@Override
	protected void finalize() throws IOException {
		close();
	}
}
