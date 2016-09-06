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

package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.FloatsL2Norm_Bytes;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive_Buffered;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;
import it.cnr.isti.vir.util.math.Normalize;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class PCAProject {

	public static void usage() {
		System.out.println("PCAProject <properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- PCAProject.inArchive=<archive file name>");
		System.out.println("- PCAProject.outArchive=<archive file name>");
		System.out.println("- PCA.PC=<principal components file>");
		System.out.println("- [PCAProject.dim]");
		System.out.println("- [PCAProject.L2Norm]");
		System.out.println("- [PCAProject.Whithening]");
		System.out.println("- [PCAProject.SignedPowerTransform]");
		System.out.println("- [PCAProject.Bytes]");
		System.out.println("- PCA.FeatureClass=<feature class to be projects>");
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length != 1) {
			usage();
		} else {
			Launch.launch(RootSIFTConvert.class.getName(), args[0]);
		}
		
	}
	
	static  class Project implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final FeaturesCollectorsArchive_Buffered out;
		private final PrincipalComponents pc;
		private final Class c;
		private final TimeManager tm;
		private final boolean l2Norm;
		private final boolean whithening;
		private final boolean signedPowerTransform_flag;
		private final double signedPowerTransform;
		private final boolean bytes_flag;
		
		public Project(
				Iterator<AbstractFeaturesCollector> it,
				FeaturesCollectorsArchive_Buffered out,
				PrincipalComponents pc,
				Class c,
				TimeManager tm,
				boolean l2Norm,
				boolean whithening,
				Double signedPowerTransform,
				boolean bytes_flag ) {
			this.it = it;
			this.out = out;
			this.c = c;
			this.tm = tm;
			this.pc = pc;
			this.l2Norm = l2Norm;
			this.whithening = whithening;
			this.bytes_flag = bytes_flag;
			
			
			if ( signedPowerTransform != null) {
				this.signedPowerTransform = signedPowerTransform;
				signedPowerTransform_flag = true;
			} else {
				this.signedPowerTransform = 1.0;
				signedPowerTransform_flag = false;
			}
		}

		@Override
		public void run() {
			AbstractFeaturesCollector fc = null;
			while (true) {
				synchronized ( it ) {
					if ( it.hasNext() ) fc = it.next();
					else break;
				}
				
				
				AbstractFeature currf = fc.getFeature(c);
				float[] temp = pc.project_float( (IArrayValues) currf);
				if ( whithening ) pc.withening(temp);
				
				if ( signedPowerTransform_flag ) {
					Normalize.sPower( temp, signedPowerTransform );
				}				
				
				if ( l2Norm ) Normalize.l2(temp);
				
				AbstractFeaturesCollector newFC = null;
				if ( bytes_flag ) {
					FloatsL2Norm_Bytes projected = new FloatsL2Norm_Bytes(temp);
					newFC = fc.createWithSameInfo(projected);
					
				} else {
					Floats projected = new Floats(temp);
					newFC = fc.createWithSameInfo(projected);
				}
				try {
					out.add(newFC);
					Log.info_verbose_progress(tm, out.size());
				} catch (ArchiveException | IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	static  class Project_LF implements Runnable {
		private final Iterator<AbstractFeaturesCollector> it;
		private final FeaturesCollectorsArchive_Buffered out;
		private final PrincipalComponents pc;
		private final Class<ALocalFeaturesGroup> lfGroupClass;
		private final TimeManager tm;
		private final boolean l2Norm;
		
		public Project_LF(
				Iterator<AbstractFeaturesCollector> it,
				FeaturesCollectorsArchive_Buffered out,
				PrincipalComponents pc,
				Class<ALocalFeaturesGroup> lfGroupClass,
				TimeManager tm,
				boolean l2Norm ) {
			this.it = it;
			this.out = out;
			this.lfGroupClass = lfGroupClass;
			this.tm = tm;
			this.pc = pc;
			this.l2Norm = l2Norm;
		}

		@Override
		public void run() {
			AbstractFeaturesCollector fc = null;
			while (true) {
				synchronized ( it ) {
					if ( it.hasNext() ) fc = it.next();
					else break;
				}
				Log.info_verbose_progress(tm, out.size());
				
				ALocalFeaturesGroup currGroup = fc.getFeature(lfGroupClass);
				ALocalFeature[] currLFs = currGroup.lfArr;
				FloatsLF[] projected = new FloatsLF[currGroup.size()];
				for ( int i=0; i<currLFs.length; i++ ) {
					float[] temp = pc.project_float( (IArrayValues) currLFs[i]);
					if ( l2Norm ) Normalize.l2(temp);
					projected[i] = new FloatsLF(currLFs[i].getKeyPoint(), temp); 
					//projected[i] = new FloatsLF(pc.project_float( (IArrayValues) currLFs[i])); 
				}
				FloatsLFGroup projectedGroup = new FloatsLFGroup(projected);
				AbstractFeaturesCollector newFC = fc.createWithSameInfo(projectedGroup);
				projectedGroup.setLinkedFC(newFC);
				
				try {
					out.add(newFC);
				} catch (ArchiveException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	
	public static void launch(Properties prop) throws Exception {
		
		File inFile  = PropertiesUtils.getFile(prop, "PCAProject.inArchive");
		File outFile = PropertiesUtils.getFile(prop, "PCAProject.outArchive");
		File pcFile  = PropertiesUtils.getFile(prop, "PCA.PC"); 
		Class c = PropertiesUtils.getClass(prop, "PCA.FeatureClass");
		int dim = PropertiesUtils.getInt_orDefault(prop, "PCAProject.dim", -1);
		boolean l2Norm = PropertiesUtils.getBoolean(prop, "PCAProject.L2Norm", false);
		boolean whithening = PropertiesUtils.getBoolean(prop, "PCAProject.Whithening", false);
		Double signedPowerTransform = PropertiesUtils.getDouble_orNull(prop, "PCAProject.SignedPowerTransform" );
		boolean bytes_flag = PropertiesUtils.getBoolean(prop, "PCAProject.Bytes", false);
		
		FeaturesCollectorsArchive in = new FeaturesCollectorsArchive(inFile);
		FeaturesCollectorsArchive_Buffered out = FeaturesCollectorsArchive_Buffered.createAs(outFile, in);

		PrincipalComponents pc = PrincipalComponents.read(pcFile);
		
		Log.info(pc.toString());
		
		pc.setProjDim(dim);
		
		Log.info( in.getInfo() );
		
		int count=0;
		TimeManager tm = new TimeManager();
		tm.setTotNEle(in.size());
		if ( ALocalFeature.class.isAssignableFrom(c)  ) {
			Class<ALocalFeaturesGroup> lfGroupClass = ALocalFeaturesGroup.getGroupClass( c );

			int nThread = ParallelOptions.reserveNFreeProcessors() +1 ;
			
			Thread[] thread = new Thread[nThread];
			Iterator<AbstractFeaturesCollector> it = in.iterator();
			for ( int ti=0; ti<nThread; ti++ ) {
				thread[ti] = new Thread(new Project_LF(it, out, pc, lfGroupClass, tm, l2Norm ));
				thread[ti].start();
			}
		        
	        for ( int ti=0; ti<thread.length && thread[ti] != null; ti++ ) {
	        	try {
					thread[ti].join();
					thread[ti] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		        
	        ParallelOptions.free(nThread-1);
			
		} else {
			int nThread = ParallelOptions.reserveNFreeProcessors() +1 ;
			Thread[] thread = new Thread[nThread];
			Iterator<AbstractFeaturesCollector> it = in.iterator();
			for ( int ti=0; ti<nThread; ti++ ) {
				thread[ti] = new Thread(new Project(it, out, pc, c, tm,l2Norm, whithening, signedPowerTransform, bytes_flag ));
				thread[ti].start();
			}
		        
	        for ( int ti=0; ti<thread.length && thread[ti] != null; ti++ ) {
	        	try {
					thread[ti].join();
					thread[ti] = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	        }
		        
	        ParallelOptions.free(nThread-1);
			
		}
		
		in.close();
		out.close();
		
	}
}
