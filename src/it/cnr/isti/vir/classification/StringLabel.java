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
package it.cnr.isti.vir.classification;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class StringLabel extends AbstractLabel {
	
	// satatic members
	protected static final ArrayList<String> idStrArr = new ArrayList<String> ();
	protected static final HashMap<String, Integer> strIDHM = new HashMap<String, Integer>();
	protected static int lastID = -1;
		
	// string id
	public final int id;
	
	
	protected static Integer getID(String str) {
		return strIDHM.get(str);
	}
	
	public static int getNStringLabel() {
		return lastID+1;
	}
	
	protected static int getIDAdding(String str){
		Integer id = getID(str);
		if ( id == null ) {
			id = ++lastID;
			idStrArr.add(id, str);
			strIDHM.put(str, id);			
		}
		return id;
	}
	
	protected static String getString(int id) {
		return idStrArr.get(id);
	}
	
	public StringLabel(ByteBuffer in) throws IOException {
		int size = in.getInt();
		char[] chars = new char[size];
		for(int i=0; i<size; i++) {
			chars[i]=in.getChar();
		}
		
		id = getIDAdding(new String(chars));
		//str = new String(chars);
	}
	
	public StringLabel(DataInput in) throws IOException {
		int size = in.readInt();
		char[] chars = new char[size];
//		for(int i=0; i<size; i++) {
//			chars[i]=in.readChar();
//		}

		byte[] byteArray = new byte[size*2];
		CharBuffer inCharBuffer = ByteBuffer.wrap(byteArray).asCharBuffer();
		in.readFully(byteArray);
		inCharBuffer.get(chars, 0, size);
		
		id = getIDAdding(new String(chars));
		//str = new String(chars);
	}
	
	
	public StringLabel(String str) {
		id = getIDAdding(str);
	}

	@Override
	protected Comparable<String> getLabel() {
		return getString(id);
	}
	
	

//	@Override
//	public int compareTo(Object o) {
//		return id - ((IDString) o).id;
//	}
	
	
	public static HashSet<AbstractLabel> readLabelsFromIDLabelFile(File file) throws IOException {
		HashSet<AbstractLabel> classes = new HashSet<AbstractLabel>();
		
		BufferedReader br = new BufferedReader( new FileReader( file ));
		
		String line = null;
		while ( (line = br.readLine()) != null  ) {
			if ( line.equals("") ) continue; 
			String[] temp =line.split("(\\s)+");
			if (!temp[1].equals("null"))
				classes.add(new StringLabel(temp[1]));
		}
		
		return classes; 

	}
	
	public static HashSet<AbstractLabel> readLabelsFromIDLabelFile_withNulls(File file) throws IOException {
		HashSet<AbstractLabel> classes = new HashSet<AbstractLabel>();
		
		BufferedReader br = new BufferedReader( new FileReader( file ));
		
		String line = null;
		while ( (line = br.readLine()) != null  ) {
			if ( line.equals("") ) continue; 
			String[] temp =line.split("(\\s)+");
			if (!temp[1].equals("null"))
				classes.add(new StringLabel(temp[1]));
			else 
				classes.add(new StringLabel("null"));
		}
		
		return classes; 

	}

	@Override
	public final void writeData(DataOutput out) throws IOException {
		String str = getString(id);
		out.writeInt(str.length());
		out.writeChars(str);		
	}
	
	@Override
	public boolean equals(Object that) {
		if ( that == null) return false;
		return this.id == ((StringLabel) that).id;
	}

	@Override
	public int compareTo(AbstractLabel o) {
		return this.toString().compareTo(((StringLabel) o).toString());
	}
	
	public final int hashCode() {
		return  (new Integer(id)).hashCode();	
	}

}
