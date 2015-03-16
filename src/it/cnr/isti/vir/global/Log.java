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
package it.cnr.isti.vir.global;

import it.cnr.isti.vir.util.TimeManager;

import java.util.Properties;

public class Log {

	static public boolean debug = true;
	
	static public boolean verbose = true;
	
	static public String indent = " - ";
	
	public static boolean isDebug() {
		return debug;
	}

	public static synchronized void setDebug(boolean debug) {
		Log.debug = debug;
	}

	public static boolean isVerbose() {
		return verbose;
	}

	public static synchronized void setVerbose(boolean verbose) {
		Log.verbose = verbose;
	}

	public static synchronized void info(String str) {
		System.out.println(str);
	}

	public static synchronized void info_indent(String str) {
		System.out.println(indent +str);
	}
	
	public static synchronized void info_noNewLine(String str) {
		System.out.print(str);
	}
	
	public static synchronized void info_verbose(String str) {
		if ( verbose ) System.out.println(str);
	}
	
	public static synchronized void info_verbose_noNewLine(String str) {
		if ( verbose ) System.out.print(str);
	}
	
	/**
	 * @param tm	Initialized time manager
	 * @param curr	Current number of items processed
	 * @param tot	Total number of items to process
	 */
	public static synchronized void info_verbose_progress(TimeManager tm, int curr, int tot) {
		if ( verbose ) {
			if ( tm.hasToOutput() ) {
				Log.info_verbose_indent(tm.getProgressString(curr, tot));
			}
		}
	}
	
	/**
	 * @param tm	Initialized time manager
	 * @param curr	Current number of items processed
	 */
	public static synchronized void info_verbose_progress(TimeManager tm, int curr) {
		if ( verbose ) {
			if ( tm.hasToOutput() ) {
				Log.info_verbose_indent(tm.getProgressString(curr));
			}
		}
	}
	
	public static synchronized void info_verbose_indent(String str) {
		if ( verbose ) {
			System.out.println(indent + str);
		}
	}

	public static synchronized void set(Properties properties) {
		String tVerbose = properties.getProperty("Log.verbose");
		if (tVerbose!=null && tVerbose!="") {
			verbose = Boolean.parseBoolean(tVerbose);
			info("Log.verbose was set to " + verbose);
		}
		
		String tDebug = properties.getProperty("Log.debug");
		if (tDebug!=null && tDebug!="") {
			debug = Boolean.parseBoolean(tDebug);
			info("Log.debug was set to " + debug);
		}
	}

}
