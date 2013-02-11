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