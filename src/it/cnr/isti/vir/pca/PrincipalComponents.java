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

package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;
import it.cnr.isti.vir.features.localfeatures.FloatsLFGroup;
import it.cnr.isti.vir.util.ArrayValuesConversion;
import it.cnr.isti.vir.util.math.Normalize;
import it.cnr.isti.vir.util.math.VectorMath;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Fabrizio Falchi
 *
 */
public class PrincipalComponents {

	double[][] eigenVectors;
	double[] means;
	
	double[] eigenValues;
	
	private int nEV; 	// number of EV
	private int oDim;	// original dim
	
	private int projDim;
	
	public int getNPC() {
		return nEV;
	}
	
	public int getProjDim() {
		return projDim;
	}
	
	public double[][] getComponents() {
		return eigenVectors;
	}

	public void setProjDim(int projDim) {
		if (projDim < 0 ) projDim = nEV;
		else this.projDim = projDim;
	}

	static final int classID = 0x1A646C05;
	
	static final int version = 1;
	
	public PrincipalComponents getReduced(int nDim) {
		double[][] newEV = Arrays.copyOf(eigenVectors, nDim);
		
		return new PrincipalComponents(newEV, means, eigenValues);
	}
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean, int projDim) {
		this( eigenVectors, mean, null, projDim);
	}
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean, double[] eigenValues, int projDim) {
		this(eigenVectors, mean, eigenValues);
		this.projDim = projDim;
	}
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean, double[] eigenValues) {
		this.eigenVectors = eigenVectors;
		this.means = mean;
		this.eigenValues = eigenValues;
		nEV = eigenVectors.length;
		oDim = eigenVectors[0].length;
		
		projDim = nEV;
	}
	
	public static PrincipalComponents read(File file) throws Exception {
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); 
		
		if ( in.readInt() != classID ) {
			throw new Exception(file.getAbsolutePath() + " does not contain a PCAProjection");
		}
		
		int givenVersion = in.readInt();
		
		int nEV = in.readInt();
		int oDim = in.readInt();
		int nComp = oDim;
		
		double[] means = new double[oDim];
		for ( int i=0; i<oDim; i++) {
			means[i] = in.readDouble();
		}
		
		double[][] eigenVectors = new double[nEV][oDim];
		for ( int ir=0; ir<nEV; ir++) {
			for ( int ic=0; ic<oDim; ic++) {
				eigenVectors[ir][ic] = in.readDouble();
			}
		}
		
		double[] eigenValues = null;
		if ( version >= 1 ) {
			boolean eigenValues_flag = in.readBoolean();
			
			if ( eigenValues_flag ) {
				eigenValues = new double[nEV];  
				for ( int i=0; i<nEV; i++) {
					eigenValues[i] = in.readDouble();
				}
			}
		}
		
		return	new PrincipalComponents(eigenVectors, means, eigenValues, nEV);		
		
	}
	
	public void save(File file) throws IOException {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
		
		out.writeInt(classID);
		
		out.writeInt(version);
		
		out.writeInt(nEV);
		out.writeInt(oDim);
		projDim = nEV;
		
		for ( int i=0; i<oDim; i++) {
			out.writeDouble(means[i]);
		}
		
		for ( int ir=0; ir<nEV; ir++) {
			for ( int ic=0; ic<oDim; ic++) {
				out.writeDouble(eigenVectors[ir][ic]);
			}
		}
		
		if ( eigenValues == null ) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			for ( int i=0; i<nEV; i++) {
				out.writeDouble(eigenValues[i]);
			}
		}
		
		out.close();
	}
	
	public float[] project_float(IArrayValues given) {
		return project_float( ArrayValuesConversion.getDoubles(given) );
	}
	
	public float[] project_float(IArrayValues given, int n) {
		return project_float( ArrayValuesConversion.getDoubles(given), n );
	}

	public float[] project_float(double[] given) {
		return project_float(given, projDim);
	}
	
	public double[] project(IArrayValues given) throws Exception {
		return project( ArrayValuesConversion.getDoubles(given) );
	}
	
	public double[] project(IArrayValues given, int n) throws Exception {
		return project( ArrayValuesConversion.getDoubles(given), n );
	}

	public double[] project(double[] given) {
		return project(given, projDim);
	}
	
	public float[] project(float[] given) {
		return project(given, projDim);
	}
	
	
	
		
	public final float[] project(float[] given, int nComp) {
		float[] res = new float[nComp];
		
		float[] vNorm = VectorMath.subtraction_float(given, means);
		
		for ( int i=0; i<nComp; i++) {
			res[i] = (float) VectorMath.scalarProduct( vNorm, eigenVectors[i]);
		}
		
		return res;
	}
	
	public final float[] project_float(double[] given, int nComp) {
		float[] res = new float[nComp];
		
		double[] vNorm = VectorMath.subtraction(given, means);
		
		for ( int i=0; i<nComp; i++) {
			res[i] = (float) VectorMath.scalarProduct(eigenVectors[i], vNorm);
		}
		
		return res;
	}
	
	public final double[] project(double[] given, int nComp) {
		double[] res = new double[nComp];
		
		double[] vNorm = VectorMath.subtraction(given, means);
		
		for ( int i=0; i<nComp; i++) {
			res[i] = VectorMath.scalarProduct(eigenVectors[i], vNorm);
		}
		
		return res;
	}
	
	public final double[] projectBack(double[] given ) {
		double[] res = new double[oDim];
		for ( int i=0; i<oDim; i++ ) {
			for ( int j=0; j<given.length; j++ ) {
				res[i] += given[j] * eigenVectors[j][i];
			}
		}
		
		VectorMath.add(res, means);
		
		return res;
	}
	
	public final float[] projectBack_float(double[] given ) {
		float[] res = new float[oDim];
		for ( int i=0; i<oDim; i++ ) {
			for ( int j=0; j<given.length; j++ ) {
				res[i] += given[j] * eigenVectors[j][i];
			}
		}
		
		VectorMath.add(res, means);
		
		return res;
	}
	
	public final void withening(float[] given) {
		
		for ( int i=0; i<given.length; i++ ) {
			given[i] = (float) ( given[i] / Math.sqrt(eigenValues[i]));
		}
		
	}

	public String toString() {
		String tStr = 
				"PC contains:\n" +
				" - " + means.length + " mean values and " +
				" - " + eigenVectors.length + " eigenvectors of dimensionality " + eigenVectors[0].length + "\n";
		if ( eigenValues != null )
				tStr +=" - " + eigenValues.length + " eigenValues \n";
		
		
		//tStr += "eigenVectors:\n" + ToString.getString(eigenVectors);
		//tStr += "means:\n" + ToString.getString(means);
		
		return tStr;
	}
	
//	public FloatsLFGroup project(ALocalFeaturesGroup givenGroup, Boolean l2Norm) throws Exception {
//		
//	}
	
	public final FloatsLFGroup project(ALocalFeaturesGroup givenGroup, Boolean l2Norm) throws Exception {
		FloatsLF[] projected = new FloatsLF[givenGroup.size()];
		ALocalFeature[] arr = givenGroup.lfArr;
		for ( int i=0; i<arr.length; i++ ) {
			float[] tFloat = project_float( (IArrayValues) arr[i]);
			if ( l2Norm == true ) Normalize.l2(tFloat);
			projected[i] = new FloatsLF(tFloat); 
		}
		FloatsLFGroup projectedGroup = new FloatsLFGroup(projected);
		return projectedGroup;
	}
	
}
