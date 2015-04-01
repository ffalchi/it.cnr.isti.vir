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
	
	static final int version = 0;
	
	public PrincipalComponents getReduced(int nDim) {
		double[][] newEV = Arrays.copyOf(eigenVectors, nDim);
		
		return new PrincipalComponents(newEV, means);
	}
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean, int projDim) {
		this(eigenVectors, mean);
		this.projDim = projDim;
	}
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean) {
		this.eigenVectors = eigenVectors;
		this.means = mean;
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
		
		return	new PrincipalComponents(eigenVectors, means, nEV);		
		
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

	public String toString() {
		String tStr = 
				"PC contains:\n" +
				" - " + means.length + " mean values and " +
				" - " + eigenVectors.length + " eigenvectors of dimensionality " + eigenVectors[0].length + "\n";
		
		
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
