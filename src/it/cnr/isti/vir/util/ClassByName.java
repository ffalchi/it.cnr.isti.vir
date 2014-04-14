package it.cnr.isti.vir.util;

import it.cnr.isti.vir.similarity.ISimilarity;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public  class ClassByName {

	public static final Object getObject(String option, Properties properties) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		return getObject(Class.forName(properties.getProperty("similarity")), properties);
	}
	
	public static final Object getObject(Class c, Properties properties) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return c.getConstructor(java.util.Properties.class).newInstance(properties);
	}
}
