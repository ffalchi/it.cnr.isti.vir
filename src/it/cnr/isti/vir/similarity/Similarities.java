package it.cnr.isti.vir.similarity;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public class Similarities {

	public static ISimilarity getSimilarity( String simCN ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getSimilarity(simCN, null);
		
	}
	
	public static ISimilarity getSimilarity( Class<ISimilarity> cSim ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		return getSimilarity(cSim, null);
		
	}
	
	public static ISimilarity getSimilarity( String simCN, Properties prop ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<ISimilarity> similarityClass = (Class<ISimilarity>) Class.forName(simCN);
		return getSimilarity(similarityClass, prop);
	}
	
	public static ISimilarity getSimilarity( Class<ISimilarity> cSim, Properties prop ) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if ( prop == null ) return cSim.newInstance();
		
		return cSim.getConstructor(Properties.class).newInstance(prop);
		
	}
}
