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
package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.id.IDLong;
import it.cnr.isti.vir.id.IDString;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class IDReader {

	public final static LinkedList<IDLong> readIDLongs(File file, boolean readNulls) throws IOException {
		LinkedList<IDLong> list = new LinkedList<IDLong>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = br.readLine()) != null) {
			if ( line == "" ) continue;
			String[] temp = line.split("(\\s)+");
			if ( !readNulls && temp.length == 2 ) {
				if ( temp[1].equals("null") ) {
					continue;
				}
			}
			try {
				list.add(new IDLong(Long.parseLong(temp[0])));
			} catch (NumberFormatException e) {
				System.out.println("error reading: " + line);
				e.printStackTrace();
			}
		}

		System.out.println(list.size() + " IDs read.");

		return list;
	}
	
	public final static LinkedList<IDString> readIDString(File file, boolean readNulls) throws IOException {
		LinkedList<IDString> list = new LinkedList<IDString>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		while ((line = br.readLine()) != null) {
			if ( line == "" ) continue;
			String[] temp = line.split("(\\t)+");
			if ( temp.length == 2 ) {
				if ( !readNulls && temp[1].equals("null") ) {
					continue;
				}
			}
			
			try {
				list.add(new IDString(temp[0]));
			} catch (NumberFormatException e) {
				System.out.println("error reading: " + line);
				e.printStackTrace();
			}
		}

		System.out.println(list.size() + " IDs read.");

		return list;
	}

	public final static HashSet<IDLong> readIDLongsHS(File file, boolean readNulls) throws IOException {
		if ( file == null ) return null;
		LinkedList<IDLong> list = readIDLongs(file, readNulls);
		HashSet<IDLong> hSet = new HashSet<IDLong>();
		for (Iterator<IDLong> it = list.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
		return hSet;
	}

	public final static HashSet<IDString> readIDStringHS(File file, boolean readNulls) throws IOException {
		if ( file == null ) return null;
		LinkedList<IDString> list = readIDString(file, readNulls);
		HashSet<IDString> hSet = new HashSet<IDString>();
		for (Iterator<IDString> it = list.iterator(); it.hasNext(); ) {
			hSet.add(it.next());
		}
		return hSet;
	}
	
}
