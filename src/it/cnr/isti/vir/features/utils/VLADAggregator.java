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
package it.cnr.isti.vir.features.utils;

import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.RootSIFTGroup;
import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.features.localfeatures.SIFTGroup;
import it.cnr.isti.vir.features.localfeatures.VLAD;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.PropertiesUtils;

import java.io.File;
import java.util.Properties;

public class VLADAggregator {

	
	private final String className = "VLADAggregator";
	
	private Class<? extends ALocalFeaturesGroup> lfGroupClass;
	
	private LFWords ref;
	
	private PrincipalComponents lfPC = null;
	private PrincipalComponents vladPC = null;
	
	private boolean useRootSIFT = true;
	
	private boolean l2Norm = false;
	
	private boolean intranorm = false;
	
	public  VLADAggregator(Properties prop ) throws Exception{
		
		useRootSIFT = PropertiesUtils.getBoolean(prop, className+".useRootSIFT", true);
		File dictionary_file = PropertiesUtils.getFile(prop, className+".centroids");
		
		File lfPC_file = PropertiesUtils.getFile_orNull(prop, className+".LF_PC");
		int lfPC_n = PropertiesUtils.getInt_orDefault(prop, className+".LF_PC_n", -1);
		l2Norm = PropertiesUtils.getBoolean(prop, className+".LF_PCA_L2Norm", false);
		
		File vladPC_file = PropertiesUtils.getFile_orNull(prop, className+".VLADPC");
		int vladPC_n = PropertiesUtils.getInt_orDefault(prop, className+".VLADPC_n", -1);
		
		
		ref = new LFWords(dictionary_file);
		Log.info_verbose(className + ": Number of references for VLAD: " + ref.size());
		
		lfGroupClass =  (Class<? extends ALocalFeaturesGroup>) ref.getLocalFeaturesGroupClass();
		Log.info_verbose(className + ": Local Features Group Class: " + lfGroupClass);
		
		if (lfPC_file != null ) {
			Log.info_verbose(className + ": Local Features Principal Components found.");
			lfPC = PrincipalComponents.read(lfPC_file);
			
			
			if (lfPC_n > 0) {
				lfPC.setProjDim(lfPC_n);				
			}
			Log.info_verbose(className + ": Local Features Principal Components: " + lfPC.getProjDim());
			
			lfGroupClass = (Class<? extends ALocalFeaturesGroup>) PropertiesUtils.getClass(prop, className+".lfGroupClass");
			
		} else {
			Log.info_verbose(className + ": Local Features Principal Components not found");
		}
		
		
		
		if (vladPC_file != null ) {
			Log.info_verbose(className + ": VLAD Principal Components found.");
			vladPC = PrincipalComponents.read(vladPC_file);
			if (PropertiesUtils.contains(prop, className+".VLADPC_n")) {
				
				vladPC.setProjDim(vladPC_n);
			}
			Log.info_verbose(className + ": VLAD Principal Components: " + vladPC.getProjDim());
			
		} else {
			Log.info_verbose(className + ": VLAD Principal Components not found");
		}
	}
	
	public AbstractFeature get(AbstractFeaturesCollector fc) throws Exception {
		if ( vladPC == null ) {
			return getVLAD(fc);
		} else {
			return getVLADPCA(fc);
		}
	}
		
	public Floats getVLADPCA(AbstractFeaturesCollector fc) throws Exception {
		ALocalFeaturesGroup group = null;
		if ( lfGroupClass.equals(RootSIFTGroup.class) || useRootSIFT ) {
			RootSIFTGroup rootSIFT = fc.getFeature(RootSIFTGroup.class);
			if ( rootSIFT == null ) {
				SIFTGroup sifts = fc.getFeature(SIFTGroup.class);
				rootSIFT = new RootSIFTGroup(sifts, null);
			}
			group = rootSIFT;
		} else {
			group = fc.getFeature(lfGroupClass);
		}
		
		return getVLADPCA(group);
	}
	
	public Floats getVLADPCA(ALocalFeaturesGroup group) throws Exception {
		if ( vladPC == null ) {
			throw new Exception(className + "_ getVLADPCA() can not be execute with VLAD Principal Components set to null");
		}	

		if ( lfGroupClass.equals(RootSIFTGroup.class) || useRootSIFT ) {
			group = new RootSIFTGroup((SIFTGroup) group, null);
		}
		
		// Projection of local features
		if ( lfPC != null) {
			group = lfPC.project(group, l2Norm);
		}
		
		VLAD vlad = VLAD.getVLAD(group, ref, intranorm);
		
		
		return new Floats(vladPC.project_float( (IArrayValues) vlad));
	}
	
	public VLAD getVLAD(AbstractFeaturesCollector fc) throws Exception {
		ALocalFeaturesGroup group = null;
		if ( lfGroupClass.equals(RootSIFTGroup.class) || ( useRootSIFT && lfGroupClass.equals(SIFT.class))) {
			RootSIFTGroup rootSIFT = fc.getFeature(RootSIFTGroup.class);
			if ( rootSIFT == null ) {
				SIFTGroup sifts = fc.getFeature(SIFTGroup.class);
				rootSIFT = new RootSIFTGroup(sifts, null);
			}
			group = rootSIFT;
		} else {
			group = fc.getFeature(lfGroupClass);
		}
		
		return getVLAD(group);
	}
	
	public VLAD getVLAD(ALocalFeaturesGroup group) throws Exception {
		if ( lfPC != null) {
			return VLAD.getVLAD(lfPC.project(group, l2Norm), ref, intranorm);	 
		} else {
			return VLAD.getVLAD(group, ref, intranorm);	
		}
		
		
		
	}
}

