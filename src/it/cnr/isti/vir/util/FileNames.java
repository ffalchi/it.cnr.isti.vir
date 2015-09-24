package it.cnr.isti.vir.util;

import it.cnr.isti.vir.id.IDException;

import java.io.File;

public class FileNames {

	public static String getFileNameWithoutExtension(String fileName) throws IDException {
        File tmpFile = new File(fileName);
        String tmpStr = tmpFile.getName();
        int whereDot = tmpStr.lastIndexOf('.');
        if ( whereDot < 0 ) return tmpStr; 
        if (0 < whereDot && whereDot <= tmpStr.length() - 2 ) {
            return tmpStr.substring(0, whereDot);
            //extension = filename.substring(whereDot+1);
        }    

        throw new IDException(	"Unable to extract ID from : " + fileName );
    }
	
//	public static String getFileNameWithoutExtension(String fileName) throws IDException {
//		String[] arr = fileName.split(File.separator);
//		
//		if ( arr == null || arr.length < 0 ) {
//			throw new IDException(
//					"Unabel to extract ID from : " + fileName );
//		}
//		
//		String name = arr[arr.length-1];
//		
//		int pointIndex = name.lastIndexOf('.');
//		
//		if ( pointIndex < 0 ) return name;
//		
//		return name.substring(0, pointIndex);
//	}
	
	
}
