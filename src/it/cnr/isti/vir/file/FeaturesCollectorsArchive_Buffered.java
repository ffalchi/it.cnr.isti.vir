/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cnr.isti.vir.file;

import gnu.trove.list.array.TLongArrayList;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

/**
 *
 * @author Fabrizio
 */
public class FeaturesCollectorsArchive_Buffered {

	private final TLongArrayList positions;
	private final ArrayList<AbstractID> ids;

	private final DataOutputStream out;
	//private final RandomAccessFile rndFile;
	private final File f;
	private final FeatureClassCollector featuresClasses;
	private final Constructor fcClassConstructor;

	private final Class<? extends AbstractID> idClass;
	private final Class<? extends IFeaturesCollector> fcClass;

	File offsetFile;
	File idFile;
	
	public FeaturesCollectorsArchive_Buffered(File file,
			FeatureClassCollector featuresClasses, Class<? extends AbstractID> idClass, Class<? extends IFeaturesCollector> fcClass)
			throws Exception {
		
		file.delete();
		
		out = new DataOutputStream( 
				new BufferedOutputStream(new FileOutputStream(file))
				);
		//rndFile = new RandomAccessFile(file, "rw");
		this.f = file;
		this.featuresClasses = featuresClasses;
		this.idClass = idClass;
		this.fcClass = fcClass;
		this.fcClassConstructor = FeaturesCollectorsArchive.getFCConstructor(fcClass);

		FeaturesCollectorsArchive.writeIntro(out, featuresClasses, idClass,	fcClass);
		
		positions = new TLongArrayList();
		ids = new ArrayList();

		offsetFile = new File(FeaturesCollectorsArchive.getIDFileName(file));
		idFile = new File(FeaturesCollectorsArchive.getOffsetFileName(file));

	}

	public void add(IFeaturesCollector fc) throws ArchiveException, IOException {

		int currPos = positions.size();
		positions.add(out.size());
		
		if (idClass != null) {
			AbstractID id = ((IHasID) fc).getID();
			if (!idClass.isInstance(id)) {
				throw new ArchiveException("Objecct has a wrong ID class: "
						+ idClass + " requeste, " + id.getClass() + " found.");
			}
			ids.add(id);			
		}

		if (fcClass == null) {
			FeaturesCollectors.writeData(out, fc);
		} else {
			if (fcClass.isInstance(fc)) {
				fc.writeData(out);
			} else {
				throw new ArchiveException("FeaturesCollector class inserted ("
						+ fc.getClass() + ") diffear from expected (" + fcClass
						+ ")");
			}
		}

	}


	public void close() throws IOException {
		out.close();
		FeaturesCollectorsArchive.createIndexFiles(offsetFile, idFile, positions, ids);		
	}
}
