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
import it.cnr.isti.vir.features.FeaturesCollectorHT;
import it.cnr.isti.vir.features.FeaturesSubRegions;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.DominantColor;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;
import it.cnr.isti.vir.features.mpeg7.vd.RegionShape;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class MPEG7VDs {
	static final FeatureClassCollector mpeg7FCC = new FeatureClassCollector(	ColorLayout.class,
																					ColorStructure.class,
																					DominantColor.class,
																					ScalableColor.class,
																					EdgeHistogram.class,
																					HomogeneousTexture.class );	
	
	public static FeaturesCollectorHT getFeaturesCollection(File file) throws XMLStreamException, MPEG7VDFormatException, FactoryConfigurationError, FileNotFoundException, FeaturesCollectorException {
		BufferedReader br = new BufferedReader( new FileReader(file) );
		return getFeaturesCollection( XMLInputFactory.newInstance().createXMLStreamReader(br));
	}

	
	public static FeaturesCollectorHT getFeaturesCollection(File file, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses  ) throws XMLStreamException, MPEG7VDFormatException, FactoryConfigurationError, FileNotFoundException, FeaturesCollectorException {
		BufferedReader br = new BufferedReader( new FileReader(file) );
		return getFeaturesCollection( XMLInputFactory.newInstance().createXMLStreamReader(br), fClasses, regionFClasses);
	}
	
	
	public static FeaturesCollectorHT getFeaturesCollection( StringBuffer sb, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses )  throws XMLStreamException, FactoryConfigurationError, MPEG7VDFormatException, FeaturesCollectorException {
		return  getFeaturesCollection( 	XMLInputFactory.newInstance().createXMLStreamReader( new ByteArrayInputStream( sb.toString().getBytes() )),
										fClasses,
										regionFClasses );
	}
	
	public static FeaturesCollectorHT getFeaturesCollection( StringBuffer sb, FeatureClassCollector fClasses )  throws XMLStreamException, FactoryConfigurationError, MPEG7VDFormatException, FeaturesCollectorException {
		return  getFeaturesCollection( 	XMLInputFactory.newInstance().createXMLStreamReader( new ByteArrayInputStream( sb.toString().getBytes() )),
										fClasses,
										null );
	}
	
	public static FeaturesCollectorHT getFeaturesCollection( XMLStreamReader xmlr  ) throws XMLStreamException, MPEG7VDFormatException, FeaturesCollectorException {
		return getFeaturesCollection( xmlr, null, null );
	}
	
	static private boolean toRead( Class c, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses, boolean subRegion ) {
		//all features are read if not fClasses are specified
		if ( fClasses == null ) return true;
		
		if ( subRegion == true  ) {
			// we are checking for features in a subregion
			
			// if fClasses has been specified it must contain FeatureSubRegions
			if ( fClasses == null || fClasses.contains(FeaturesSubRegions.class ))
				// if regionFClasses has been specified it must contains the specified feature
				return  regionFClasses == null || regionFClasses.contains(c) ;
			
			return false;
		}
		
		return  fClasses == null || fClasses.contains(c);
	}
	
	protected static FeaturesCollectorHT getFeaturesCollection( XMLStreamReader xmlr, FeatureClassCollector fClasses, FeatureClassCollector regionFClasses ) throws XMLStreamException, MPEG7VDFormatException, FeaturesCollectorException {
		
		FeaturesCollectorHT mainFC = new FeaturesCollectorHT();
		FeaturesCollectorHT currFC = mainFC; 
		LinkedList<FeaturesCollectorHT> regions = new LinkedList<FeaturesCollectorHT>();
		
		boolean subRegion=false;
		for (int event = xmlr.next();  
	    	event != XMLStreamConstants.END_DOCUMENT;
	    	event = xmlr.next()) {
	    	switch (event) {
	        	case XMLStreamConstants.START_ELEMENT:

	        		if (xmlr.getLocalName().equals("VisualDescriptor") ) {
	                    for (int i = 0; i < xmlr.getAttributeCount(); i++) {
	                        // Get attribute name
	                        String localName = xmlr.getAttributeLocalName(i);
	                        if (localName.equals("type")) {
		                        if (localName.equals("type")) {
		                        		String value = xmlr.getAttributeValue(i);
			                        	
			                        	if ( value.equals("ScalableColorType") && toRead(ScalableColor.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new ScalableColor( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("ColorStructureType") && toRead(ColorStructure.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new ColorStructure( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("ColorLayoutType") && toRead(ColorLayout.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new ColorLayout( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("EdgeHistogramType") && toRead(EdgeHistogram.class, fClasses, regionFClasses, subRegion ) ) {
			                        		//eh = new EdgeHistogram_Big( xmlr );
			                        		currFC.add( new EdgeHistogram( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("HomogeneousTextureType") && toRead(HomogeneousTexture.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new HomogeneousTexture( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("DominantColorType") && toRead(DominantColor.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new DominantColor( xmlr ) );
			                        		break;
			                        	} else if ( value.equals("RegionShapeType") && toRead(RegionShape.class, fClasses, regionFClasses, subRegion ) ) {
			                        		currFC.add( new RegionShape( xmlr ) );
			                        		break;
			                        	}
			                        }
	                        }
	                     }	            			
       			
	        		} else if (xmlr.getLocalName().equals("StillRegion") ) { 
	        			//We are inside a SubRegion
	        			subRegion = true;
	        			currFC = new FeaturesCollectorHT();
	        		}
	            	break;
	            case XMLStreamConstants.END_ELEMENT:
	            	if (xmlr.getLocalName().equals("Image") ) { 
	            		//System.out.print(".");
//	            		System.out.println(toString());
	            		
	            		// ID !!!!!!!!!!!!!!!!!!!!
	            		//if ( id != null && !id.isInitialized() ) throw new MPEG7VDFormatException();
	            		// adding Regions
	            		if ( regions.size() > 0 )
	            			mainFC.add(new FeaturesSubRegions( regions ));
	            				
	            		// checking features in mainFC
	            		if ( fClasses != null &&  !FeatureClassCollector.getIntersection(fClasses,mpeg7FCC).areIn(mainFC) ) {
	            			System.err.println("Not all the features were found.");
	            			return null;
	            		}
	            		
	            		// checking if regions were requested
	            		if ( fClasses != null &&  fClasses.contains(FeaturesSubRegions.class)) {
	            			FeaturesSubRegions subRegions = (FeaturesSubRegions) mainFC.getFeature(FeaturesSubRegions.class);
	            			if ( subRegions == null || subRegions.size() < 1) {
	            				System.err.println("Regions were not found.");
	            				return null;				
	            			}
	            			
	            			// checking features in regions
	            			if ( regionFClasses != null ) {
	            				if ( !FeatureClassCollector.getIntersection(regionFClasses,mpeg7FCC).areIn((FeaturesSubRegions) mainFC.getFeature(FeaturesSubRegions.class)) ) {
	            					System.err.println("Not all the features were found in subregions.");
	            					return null;
	            				}
	            			}
	            		}
	            		
	            		
	            		return currFC;
	            	} else if (xmlr.getLocalName().equals("StillRegion") ) {
	            		//if ( regionFClasses == null ) return currFC;
	            		subRegion = false;
	            		regions.add(currFC);
	            		currFC = mainFC;
	            	}
	            	break;
	            case XMLStreamConstants.CHARACTERS:
	
	            	break;
	            case XMLStreamConstants.CDATA:
	
	            	break;
	    	} // end switch
	    	
	    } // end for
		
		System.err.println("Image tag not found!!");
		return null;
	}
	
}
