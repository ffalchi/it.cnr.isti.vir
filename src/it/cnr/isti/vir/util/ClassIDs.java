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

import it.cnr.isti.vir.features.IFeature;

import java.lang.reflect.Constructor;
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
	

