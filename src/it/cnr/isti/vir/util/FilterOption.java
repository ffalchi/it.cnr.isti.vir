package it.cnr.isti.vir.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Madan Kumar K
 * 
 */
public class FilterOption implements FilenameFilter {
	private String extension;

	public FilterOption(String extension) {
		this.extension = extension;
	}

	@Override
	public boolean accept(File dir, String name) {
		return (name.endsWith(extension));
	}
}
