package it.cnr.isti.vir.util;

import it.cnr.isti.vir.global.Log;

import java.io.File;
import java.util.Properties;

public class PropertiesUtils {
	
	public static boolean contains(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return false;
		else return true;
	}

	public static String getAbsolutePath(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return WorkingPath.getAbsolutePath(str);
	}
	
	public static final File getFile(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			throw new Exception(propertyName + " was not found in properties.");
		}
		Log.info_verbose(propertyName + "=" + str);
		return WorkingPath.getFile(str);
	}
	
	public static final File getFile_orNull(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info(propertyName + " was not found in properties. Null will be returned.");
			return null;
		}
		Log.info_verbose(propertyName + "=" + str);
		return WorkingPath.getFile(str);
	}
	
	public static int getInt_maxIfNotExists(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return Integer.MAX_VALUE;
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static boolean getBoolean(Properties prop, String propertyName ) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Boolean.parseBoolean(str);
	}
	
	public static boolean getBoolean(Properties prop, String propertyName, boolean def) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return def;
		Log.info_verbose(propertyName + "=" + str);
		return Boolean.parseBoolean(str);
	}
	
	public static final int getInt(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static final int getInt_orDefault(Properties prop, String propertyName, int def) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info(propertyName + " was not found in properties. Using deafult value " + def);
			return def;
		}
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	
	public static final double getDouble(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Double.parseDouble(str);
	}
	
	public static final double getDouble_orDefault(Properties prop, String propertyName, double def) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info(propertyName + " was not found in properties. Using deafult value " + def);
			return def;
		}
		Log.info_verbose(propertyName + "=" + str);
		return Double.parseDouble(str);
	}
	
	public static final float getFloat(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Float.parseFloat(str);
	}	
	
	public static final Class<?> getClass(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Class.forName(str);
	}

	public static boolean getIfExistsDefFalse(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return false;
		Log.info_verbose(propertyName + "=" + str);
		return Boolean.parseBoolean(str);
	}
	public static boolean getIfExistsDefTrue(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return true;
		Log.info_verbose(propertyName + "=" + str);
		return Boolean.parseBoolean(str);
	}
	
	public static final Object instantiateObject(Properties prop, String propertyName)  throws Exception {
		Class c = getClass(prop, propertyName);
		return c.newInstance();
	}
	
	public static final Object instantiateObjectWithProperties(Properties prop, String propertyName)  throws Exception {
		Class c = getClass(prop, propertyName);
		return c.getConstructor(java.util.Properties.class).newInstance(prop);
	}

}
