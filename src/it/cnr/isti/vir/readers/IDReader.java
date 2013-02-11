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
