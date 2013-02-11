package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeaturesCollectorException;
import it.cnr.isti.vir.features.mpeg7.vd.MPEG7VDFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

public interface IObjectsReader<Obj> {

	public Obj getObj() throws Exception;
//	public Obj getObj_OnePerFile() throws IOException, FactoryConfigurationError, MPEG7VDFormatException, XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException, FeaturesCollectorException;
	public void open(BufferedReader br);
	
	
}
