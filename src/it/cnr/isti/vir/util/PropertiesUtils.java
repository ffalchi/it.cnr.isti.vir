package it.cnr.isti.vir.util;

import it.cnr.isti.vir.global.Log;

import java.io.File;
import java.util.Properties;

public class PropertiesUtils {
	
	public static final File getFile(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return WorkingPath.getFile(str);
	}
	
	public static final int getInt(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static final double getDouble(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Double.parseDouble(str);
	}
	
	public static final float getFloat(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Float.parseFloat(str);
	}	
	
	
	
}
