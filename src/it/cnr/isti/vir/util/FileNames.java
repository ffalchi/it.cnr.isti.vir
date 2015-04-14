package it.cnr.isti.vir.util;

import java.io.File;

public class FileNames {

	public static String getFileNameWithoutExtension(String fileName) {
        File tmpFile = new File(fileName);
        String tmpStr = tmpFile.getName();
        int whereDot = tmpStr.lastIndexOf('.');
        if ( whereDot < 0 ) return tmpStr; 
        if (0 < whereDot && whereDot <= tmpStr.length() - 2 ) {
            return tmpStr.substring(0, whereDot);
            //extension = filename.substring(whereDot+1);
        }    
        return "";
    }
}
