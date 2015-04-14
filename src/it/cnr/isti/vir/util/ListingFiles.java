package it.cnr.isti.vir.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

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
			File directory,
			FilenameFilter filter,
			boolean recurse)
	{
		// List of files / directories
		ArrayList<File> files = new ArrayList<File>();
		
		// Get files / directories in the directory
		File[] entries = directory.listFiles();
		
		if ( entries == null ) return files;
		// Go over entries
		for (File entry : entries)
		{

			// If there is no filter or the filter accepts the 
			// file / directory, add it to the list
			if (filter == null || filter.accept(directory, entry.getName()))
			{
				files.add(entry);
			}
			
			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory())
			{
				files.addAll(listFiles(entry, filter, recurse));
			}
		}
		
		// Return collection of files
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
