package it.cnr.isti.vir.util;

public class Log {

	static public boolean debug = true;
	
	static public boolean verbose = true;
		
	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		Log.debug = debug;
	}

	public static boolean isVerbose() {
		return verbose;
	}

	public static void setVerbose(boolean verbose) {
		Log.verbose = verbose;
	}

	public static void info(String str) {
		System.out.println(str);
	}
	
	public static void info_verbose(String str) {
		if ( verbose ) System.out.println(str);
	}
}
