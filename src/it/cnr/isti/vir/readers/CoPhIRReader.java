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
package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.mpeg7.SAPIRObject;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public class CoPhIRReader implements IObjectsReader<SAPIRObject> { //implements MetricObjectsWithIDReader<Integer>  {
	
	protected BufferedReader br = null;
	
	final static private FeatureClassCollector fClass = new FeatureClassCollector(
			ColorLayout.class,
			ColorStructure.class,
			ScalableColor.class,
			EdgeHistogram.class,
			HomogeneousTexture.class );
	
	final static private Pattern p = Pattern.compile("<photo id=\"(.*)\" secret=\"(.*)\" server=\"(.*)\" farm=\"(\\d*)\"");
	final static private Pattern pImageID = Pattern.compile("<Image id=\"(\\-?\\d*)\"");
	
	protected GZIPInputStream gzIn = null;
	
	public CoPhIRReader() { };
	
	public final void open(BufferedReader br) {
		this.br = br;
	}
	
	public CoPhIRReader(FileReader fileR) {
		br = new BufferedReader( fileR );
	}
	
	public CoPhIRReader(InputStreamReader inSR) {
		br = new BufferedReader(inSR);
	}
	
	public CoPhIRReader(String filename) throws FileNotFoundException {
		br = new BufferedReader(new FileReader(filename));
	}
	
	public CoPhIRReader(GZIPInputStream tarGZInputStream) throws FileNotFoundException {
		br = new BufferedReader( new InputStreamReader( tarGZInputStream ) );
	}
	
	public final SAPIRObject getObj() throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, FeaturesCollectorException {
		return getObj(br);
	}
	
	public final static SAPIRObject getObj(BufferedReader br) throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, FeaturesCollectorException {
		
		Integer id = null;
		String thmbURL = null;
		String line = null;
		StringBuffer sb = new StringBuffer ();
		
		//sb = new StringBuffer ();
		//sb.delete(0, sb.length());
		//sb.append("<SapirMMObject>");
		
		while ( (line = br.readLine()) != null ) {
			
	    	// This removes <photo> content keeping the ID
	    	if ( line.matches("^<photo.*") ) {	
	    		// Getting the integer ID
	    		
	    		Matcher m = p.matcher(line);
	    		m.find();
	    		if (id != null) {
	    			System.err.println("Error reading: " + id + "\n" +  sb);
	    		}
	    		id = Integer.parseInt(m.group(1));
	    		String secret = m.group(2);
	    		String server = m.group(3);
	    		String farm = m.group(4);
	    		
	    		thmbURL = "http://farm"+farm+".static.flickr.com/"+server+"/"+id+"_"+secret+"_t.jpg";
	    		//"http://farm1.static.flickr.com/144/328919634_8ea13f3a2c_t.jpg"
	    		
	    		
	    		/*	    		
	    		//sb.append(line);
	    		while ( !line.matches("^</photo>.*")) {
	    			line = br.readLine();
	    		}
	    		*/
	    		//sb.append(line);
	    		continue;
	    	}
	    	
	    	if ( line.contains("<Mpeg7")) {
	    		//System.out.print(".");
	    		sb.append(line);
	    		if ( id == null && line.contains("<Image id=")) {
					// try in <Image id= 
					Matcher m = pImageID.matcher(line);
		    		m.find();
		    		id = Integer.parseInt(m.group(1));
				}
	    		while ( !line.contains("</Mpeg7>")) {
	    			line = br.readLine();
	    			sb.append(line);
	    			if ( id == null && line.contains("<Image id=")) {
						// try in <Image id= 
						Matcher m = pImageID.matcher(line);
			    		m.find();
			    		id = Integer.parseInt(m.group(1));
					}
	    		}
				try {
					//System.out.println(sb);
					
					SAPIRObject res =  new SAPIRObject(id, MPEG7VDs.getFeaturesCollection(sb, fClass));
					res.setThmbURL(thmbURL);
					return res;
				} catch (XMLStreamException e) {
					e.printStackTrace();
					System.err.println("Error parsing:" + sb.toString() + "\n" + sb);
					throw e;
				}
	    	}

	    }
		
		return null;
		
	}

	
	
	
	
}
