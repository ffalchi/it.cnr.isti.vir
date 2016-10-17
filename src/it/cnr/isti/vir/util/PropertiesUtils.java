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
	
	public static String getAbsolutePath_orNull(Properties prop, String propertyName)   {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return null;
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
			Log.info_verbose(propertyName + " was not found in properties. Null will be returned.");
			return null;
		}
		Log.info_verbose(propertyName + "=" + str);
		return WorkingPath.getFile(str);
	}
	
	public static String getString_orDefault(Properties prop, String propertyName, String def) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return def;
		Log.info_verbose(propertyName + "=" + str);
		return str;
	}
	public static int getInt_maxIfNotExists(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return Integer.MAX_VALUE;
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static Integer getInt_nullIfNotExists(Properties prop, String propertyName) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) return null;
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
		if ( str == null ) 	{
			Log.info_verbose(propertyName + " was not found in properties. Using deafult value " + def);
			return def;
		}
		Log.info_verbose(propertyName + "=" + str);
		return Boolean.parseBoolean(str);
	}
	
	public static final int getInt(Properties prop, String propertyName) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) throw new Exception(propertyName + " was not found in properties");
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static final int getInt_orDefault(Properties prop, String propertyName, int def) {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info_verbose(propertyName + " was not found in properties. Using deafult value " + def);
			return def;
		}
		Log.info_verbose(propertyName + "=" + str);
		return Integer.parseInt(str);
	}
	
	public static final Integer getInt_orNull(Properties prop, String propertyName ) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info_verbose(propertyName + " was not found in properties. Using deafult value NULL");
			return null;
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
			Log.info_verbose(propertyName + " was not found in properties. Using deafult value " + def);
			return def;
		}
		Log.info_verbose(propertyName + "=" + str);
		return Double.parseDouble(str);
	}
	
	public static final Double getDouble_orNull(Properties prop, String propertyName ) throws Exception {
		String str = prop.getProperty(propertyName);
		if ( str == null ) {
			Log.info_verbose(propertyName + " was not found in properties. Using deafult value NULL");
			return null;
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
