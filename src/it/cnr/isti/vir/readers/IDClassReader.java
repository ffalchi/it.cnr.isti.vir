package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.StringLabel;
import it.cnr.isti.vir.id.IDLong;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.AbstractID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class IDClassReader {


	public static final HashMap<AbstractID, AbstractLabel> readLongString(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		HashMap<AbstractID, AbstractLabel> idClass 	= new HashMap<AbstractID, AbstractLabel>();
		HashMap<String, AbstractLabel> strCLabel	= new HashMap<String, AbstractLabel>();
		
		while ((line = br.readLine()) != null) {

			String[] temp = line.split("(\\s)+");

			// Integer id = Integer.parseInt(temp[0]);
			try {
				// long id = Long.parseLong(temp[0]);
				// String c = temp[1];
				IDLong id = new IDLong(Long.parseLong(temp[0]));
				AbstractLabel cLabel = strCLabel.get(temp[1]);
				if ( cLabel == null && !temp[1].equals("null")) {
					cLabel = new StringLabel(temp[1]);
					strCLabel.put(temp[1], cLabel);
				}
				idClass.put(id, cLabel);
				// System.out.println(id +"\t"+ c);
			} catch (NumberFormatException e) {

				System.out.println("error reading: " + line);
				e.printStackTrace();
			}
		}

		System.out.println(idClass.size() + " IDs assigned to "	+ strCLabel.size() + " classes were found.");
		
		return idClass;
		
	}
	
	public static final HashMap<AbstractID, AbstractLabel> readIDStringString(File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		HashMap<AbstractID, AbstractLabel> idClass 	= new HashMap<AbstractID, AbstractLabel>();
		HashMap<String, AbstractLabel> strCLabel	= new HashMap<String, AbstractLabel>();
		
		while ((line = br.readLine()) != null) {
			if ( line.equals("")) continue;
			String[] temp = line.split("(\\s)+");

			// Integer id = Integer.parseInt(temp[0]);
			try {
				// long id = Long.parseLong(temp[0]);
				// String c = temp[1];
				IDString id = new IDString( temp[0] );
				AbstractLabel cLabel = strCLabel.get(temp[1]);
				if ( cLabel == null && !temp[1].equals("null")) {
					cLabel = new StringLabel(temp[1]);
					strCLabel.put(temp[1], cLabel);
				}
				idClass.put(id, cLabel);
				// System.out.println(id +"\t"+ c);
			} catch (NumberFormatException e) {

				System.out.println("error reading: " + line);
				e.printStackTrace();
			}
		}

		System.out.println(idClass.size() + " IDs assigned to "	+ strCLabel.size() + " classes were found.");
		
		return idClass;
		
	}
}
