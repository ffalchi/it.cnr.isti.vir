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
package it.cnr.isti.vir.experiments;

import it.cnr.isti.vir.global.GlobalParameters;
import it.cnr.isti.vir.util.NestedProperties;
import it.cnr.isti.vir.util.TeeStream;
import it.cnr.isti.vir.util.string.DateTime;
import it.cnr.isti.vir.util.string.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Fabrizio Falchi
 *
 */
public class Launch {

	
	
	public static void launch( String propertyFileName ) throws Exception {
		
		// Reading properties file.
		Properties properties;

		properties = NestedProperties.load(propertyFileName);
		System.out.println("\nProperties: " + properties + "\n");
		
		String classStr = null;
		//try {
		classStr = properties.getProperty("mainClass");
		
		
		if ( classStr == null ) {
			// Redirecting ERR
			PrintStream stderr = System.err;
			File errFile = new File(propertyFileName + "_" + DateTime.now() + "_err.txt");
			PrintStream errPStream = new PrintStream(errFile);
			System.out.println("Cloning ERR to: " + errFile.getAbsolutePath());
			System.setErr(new TeeStream(System.err, errPStream));
			
			System.err.println("mainClass properties was not found in propertyFileName");
			
			System.setErr(stderr);
			return;
		}
		
		launch(classStr, properties, propertyFileName );		
		
	}
	
	public static void launch(String className, String propertyFileName ) throws Exception {
		Properties properties;
		try {
			properties = NestedProperties.load(propertyFileName);
			System.out.println("\nProperties: " + properties + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		launch( className, properties, propertyFileName);
		
		
		
	}
	
	
	
	
	public static void launch(String className, Properties properties, String propertyFileName )  {
		
		PrintStream stdout = System.out;
		PrintStream stderr = System.err;
		
	    try {

			// Experiment start string
			String dateTime = DateTime.now();
			Long startTime = System.currentTimeMillis();
				
			// Redirecting OUT
			File outFile = new File(propertyFileName + "_" + dateTime + "_out.txt");
			PrintStream outPStream = new PrintStream(outFile);
			System.setOut(new TeeStream(System.out, outPStream));
			System.out.println("Cloning OUT to: " + outFile.getAbsolutePath());
				
			// Redirecting ERR
			File errFile = new File(propertyFileName + "_" + dateTime + "_err.txt");
			PrintStream errPStream = new PrintStream(errFile);
			System.out.println("Cloning ERR to: " + errFile.getAbsolutePath());
			System.setErr(new TeeStream(System.err, errPStream));
			
			System.out.println();	
						
			// Setting global parameters
			GlobalParameters.set(properties);
			
			// Getting class to launch
			Class<?> act = Class.forName(className);
	    	
	    	Class<ILaunchable> c = (Class<ILaunchable>) Class.forName(className);
	        Method m = c.getMethod("launch", Properties.class);
        
	        System.out.println("\n--------------------------------");
	        System.out.format("launching %s with properties file:%n", c.getName() );
	        System.out.println(propertyFileName);
	        System.out.println("DateTime: " + dateTime);
	        System.out.println("--------------------------------");
	        System.out.println();
	        
	        // Launching Experiment
	        m.invoke(null, properties);
	        
	        Long endTime = System.currentTimeMillis();
	        System.out.println("\n--------------------------------");
	        System.out.format("%s with properties file:%n", c.getName() );
	        System.out.println(propertyFileName);
	        System.out.println("ended");
	        System.out.println("DateTime: " + DateTime.now());
	        System.out.println("TotalTime: " +  Time.getString_millis(endTime-startTime) );
	        System.out.println("--------------------------------");
	        
	        
	        // Resetting Streams
	        System.setOut(stdout);
	        System.setErr(stderr);
	        
	        outPStream.close();
	        errPStream.close();
	        
	        BufferedReader br = new BufferedReader(new FileReader(errFile));     
	        if (br.readLine() == null) {
	            br.close();
	            errFile.delete();
	        }
	        
	        // production code should handle these exceptions more gracefully
	    } catch (Exception x) {
	        x.printStackTrace();
	    }
	    
        // Resetting Streams
        System.setOut(stdout);
        System.setErr(stderr);
        
       
        
        
	}
	
	/**
	 * Launch experiments by using the .properties files
	 * reported as arguments.
	 * 
	 * @param args 	the .properties file names
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 */
	public static void main(String[] args) throws Exception {

		// Usage 
		if ( args.length != 1 ) {
			System.out.println("Usage: 		Launch <propertiesFileName>.properties");
			System.out.println("or usage: 	Launch <list of propertiesFileNames>.txt");
			System.out.println();
			System.out.println("Properties must contain:");
			System.out.println("\nmainClass=<class to be lunched>");
			System.out.println();
			System.out.println("List of propertiesFileNames contains a list of properties files with aboslute path or relative path with respect to the list of properties files path");
			System.exit(0);
		}
		
		if ( args[0].endsWith(".properties")) {
			launch(args[0]);
		} else {
			File textFile = new File(args[0]);
			
			for (String line : Files.readAllLines(Paths.get(args[0])) ) {
				
				if ( line.startsWith("#") ) continue;
				if ( line.trim().isEmpty() ) continue;
				
				File currFile = new File(line);
				if ( currFile.isAbsolute() ) {
					launch(line);
				} else {
					launch(textFile.getParent() + File.separator + line);
				}
			}			
		}

	}

}
