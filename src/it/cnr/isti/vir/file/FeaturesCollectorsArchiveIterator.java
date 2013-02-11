package it.cnr.isti.vir.file;

import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.IFeaturesCollector;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;

public class FeaturesCollectorsArchiveIterator implements Iterator<IFeaturesCollector>{
	
	private final DataInputStream in;
	public final Constructor fcClassConstructor;
	
	public FeaturesCollectorsArchiveIterator(File f, Constructor fcClassConstructor) throws IOException {
		
			in = new DataInputStream(
				new BufferedInputStream(
					new FileInputStream(f)));
			this.fcClassConstructor = fcClassConstructor;
			
			FeaturesCollectorsArchive.readHeader(in);
	}

	@Override
	public boolean hasNext() {
		try {
			return ( in.available() != 0 );
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public IFeaturesCollector next() {
		try {
			if (fcClassConstructor == null) {
				return FeaturesCollectors.readData(in);
			} else {
				return (IFeaturesCollector) fcClassConstructor.newInstance(in);
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void remove() {
		 throw new UnsupportedOperationException(); 		
	}

}
