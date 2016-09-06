/*******************************************************************************
 * Copyright (c), Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package it.cnr.isti.vir.id;

import java.io.File;


public class ID2FileName_MIRFlickr implements IID2File, IID2URL {

	String suffix;
	String rootPath;
	
	String mapName = "IDURL";
	
	public ID2FileName_MIRFlickr(String rootPath) {
		this(rootPath, ".jpg");
	}
	
	public ID2FileName_MIRFlickr(String rootPath, String suffix) {
		this.rootPath = rootPath;
		this.suffix = suffix;
	}

	public final String getURL(int id) {

		int subdir = (int) (id / 10000);
		return rootPath + File.separator + subdir + File.separator + id + ".jpg";
	}
	
	public final String getURL(IDInteger id) {
		return getURL(id.id);
	}

	@Override
	public String getURL(String id) {
		return getURL(Integer.parseInt(id));
	}

	@Override
	public File getFile(String id) {
		// TODO Auto-generated method stub
		return new File( getURL( Integer.parseInt(id)));
	}
	
}
