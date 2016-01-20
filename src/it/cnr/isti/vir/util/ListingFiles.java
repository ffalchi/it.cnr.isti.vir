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
package it.cnr.isti.vir.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

public class ListingFiles {
	
	
	
	public static File[] listFilesAsArray(
			File directory,
			FilenameFilter filter,
			boolean recurse)
	{
		Collection<File> files = listFiles(directory, filter, recurse);
		
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}
	
	public static ArrayList<File> listFiles(
			String dir,
			String ext,
			boolean recurse)
	{
		return listFiles(
				new File(dir),
				getFilenameFilter(ext),
				recurse);
	}
	
	public static ArrayList<File> listFiles(
			File directory,
			String ext,
			boolean recurse)
	{
		return listFiles( directory, getFilenameFilter(ext), recurse);
	}

	public static ArrayList<File> listFiles(
			File directory,
			FilenameFilter filter,
			boolean recurse)
	{

		ArrayList<File> files = new ArrayList<File>();
		

		File[] entries = directory.listFiles();
		
		if ( entries == null ) return files;

		for (File entry : entries)
		{


			if (filter == null || filter.accept(directory, entry.getName()))
			{
				files.add(entry);
			}

			if (recurse && entry.isDirectory())
			{
				files.addAll(listFiles(entry, filter, recurse));
			}
		}

		return files;		
	}
	
	public static FilenameFilter getFilenameFilter(String ext ) {
		final String end = "." + ext;
		return new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(end)) {
					return true;
				} else {
					return false;
				}
			}
		};
	}
}
