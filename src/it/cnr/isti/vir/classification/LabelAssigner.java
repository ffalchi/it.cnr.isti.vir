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

import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.AbstractFeaturesCollector_Labeled_HasID;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.readers.IDClassReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class LabelAssigner {


	/**
	 * @param objects		documents to which the labels will be assigned
	 * @param file			id\tlabel\n file
	 * @return
	 * @throws IOException
	 */
	public static final LinkedList<AbstractFeaturesCollector_Labeled_HasID> assignStringClass_IDString(Collection<AbstractFeaturesCollector_Labeled_HasID> objects, File file)
			throws IOException {

		return assignStringClass(objects, IDClassReader.readIDStringString(file));
	}

	/**
	 * @param objects		documents to which the labels will be assigned
	 * @param file			id\tlabel\n file
	 * @return
	 * @throws IOException
	 */
	public static final LinkedList<AbstractFeaturesCollector_Labeled_HasID> assignStringClass_IDLong(Collection<AbstractFeaturesCollector_Labeled_HasID> objects, File file)
			throws IOException {

		return assignStringClass(objects, IDClassReader.readLongString(file));
	}

	public static final LinkedList<AbstractFeaturesCollector_Labeled_HasID> assignStringClass(Collection<AbstractFeaturesCollector_Labeled_HasID> objects, HashMap<AbstractID, AbstractLabel> idClass) {
		LinkedList<AbstractFeaturesCollector_Labeled_HasID> list = new LinkedList<AbstractFeaturesCollector_Labeled_HasID>();
		for (Iterator<AbstractFeaturesCollector_Labeled_HasID> it = objects.iterator(); it
				.hasNext();) {
			AbstractFeaturesCollector_Labeled_HasID currObj = it.next();
			AbstractLabel cLabel = (AbstractLabel) idClass.get(currObj.getID());

			if (cLabel == null && !idClass.containsKey(currObj.getID()))
				System.err.println("No class found for object "
						+ currObj.getID());
			list.add(new FeaturesCollectorArr(currObj.getFeatures(), currObj.getID(), cLabel));
		}

		return list;
	}

	public static final HashMap<String, LinkedList<Long>> readStringLong(
			File file) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(file));

		String line = null;

		HashMap<String, LinkedList<Long>> classID = new HashMap<String, LinkedList<Long>>();
		HashSet<Long> ids = new HashSet<Long>();

		while ((line = br.readLine()) != null) {

			String[] temp = line.split("(\\s)+");
			Long id = Long.parseLong(temp[0]);
			if (ids.contains(id))
				System.out.println("ID " + id + " is replicated.");
			ids.add(id);
			String label = temp[1];
			try {
				LinkedList<Long> currList = null;
				if ((currList = classID.get(temp[1])) != null) {
					currList.add(id);
				} else {
					currList = new LinkedList<Long>();
					currList.add(id);
					classID.put(label, currList);
				}
				// System.out.println(id +"\t"+ c);
			} catch (NumberFormatException e) {

				System.out.println("error reading: " + line);
				e.printStackTrace();
			}
		}

		System.out.println(ids.size() + " IDs assigned to " + classID.size()
				+ " classes were found.");

		return classID;

	}

}
