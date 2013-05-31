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

import it.cnr.isti.vir.features.FeaturesCollectors;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;

public class FeaturesCollectorsArchiveIterator implements Iterator<AbstractFeaturesCollector>{
	
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
	public AbstractFeaturesCollector next() {
		try {
			if (fcClassConstructor == null) {
				return FeaturesCollectors.readData(in);
			} else {
				return (AbstractFeaturesCollector) fcClassConstructor.newInstance(in);
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
