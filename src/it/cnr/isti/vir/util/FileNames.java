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
