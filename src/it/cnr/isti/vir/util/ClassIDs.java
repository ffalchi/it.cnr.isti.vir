package it.cnr.isti.vir.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Hashtable;



/**
 * @author Fabrizio Falchi
 *
 */
public class ClassIDs {


	public static final Hashtable<Class<?>, Integer> getClassIDsHT(Class<?>[] classes) {
		
		Hashtable<Class<?>, Integer> temp = new Hashtable<Class<?>, Integer> (2*classes.length);
		
		for ( int i=0; i<classes.length; i++) {
			if ( classes[i] != null)
				temp.put( classes[i], i);
		}

		return temp;
	}
	
	public static final Constructor<?>[] getConstructors(Class<?>[] classes, Class<?>... parameterTypes ) {
		Constructor<?>[] temp = new Constructor[classes.length];
		for ( int i=0; i<classes.length; i++) {
			try {
				if ( classes[i] != null) temp[i]=classes[i].getConstructor(parameterTypes);
			} catch ( NoSuchMethodException e ) {
				System.err.println(classes[i] + " has not a constructor with parameters:");
				for (int ip=0; ip<parameterTypes.length; ip++) {
					System.err.println("\t" + parameterTypes[ip] );
				}
				temp[i]= null;
			}
		}
		return temp;
	}
}
	

