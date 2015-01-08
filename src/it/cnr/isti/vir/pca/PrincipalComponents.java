package it.cnr.isti.vir.pca;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.util.ArrayValuesConversion;
import it.cnr.isti.vir.util.math.VectorMath;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Fabrizio Falchi
 *
 */
public class PrincipalComponents {

	double[][] eigenVectors;
	double[] means;
	
	private int nEV; 	// number of EV
	private int oDim;	// original dim
	
	private int nComp = -1;
	
	static final int classID = 0x1A646C05;
	
	static final int version = 0;
	
	public PrincipalComponents(double[][] eigenVectors, double[] mean) {
		this.eigenVectors = eigenVectors;
		this.means = mean;
		nEV = eigenVectors.length;
		oDim = eigenVectors[0].length;
		
		nComp = oDim;
	}
	
	public PrincipalComponents read(File file) throws Exception {
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); 
		
		if ( in.read() != classID ) {
			throw new Exception(file.getAbsolutePath() + " does not contain a PCAProjection");
		}
		
		int givenVersion = in.read();
		
		nEV = in.read();
		oDim = in.read();
		nComp = oDim;
		
		means = new double[oDim];
		for ( int i=0; i<oDim; i++) {
			means[i] = in.readDouble();
		}
		
		eigenVectors = new double[nEV][oDim];
		for ( int ir=0; ir<nEV; ir++) {
			for ( int ic=0; ic<oDim; ic++) {
				eigenVectors[nEV][oDim] = in.readDouble();
			}
		}
		
		return new PrincipalComponents(eigenVectors, means);		
		
	}
	
	public void save(File file) throws IOException {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))); 
		
		out.writeInt(classID);
		
		out.writeInt(version);
		
		out.writeInt(nEV);
		out.writeInt(oDim);
		nComp = oDim;
		
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
	
	public double[] project(IArrayValues given) throws Exception {
		return project( ArrayValuesConversion.getDoubles(given) );
	}
	
	public double[] project(IArrayValues given, int n) throws Exception {
		return project( ArrayValuesConversion.getDoubles(given), n );
	}

	public double[] project(double[] given) {
		return project(given, nComp);
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

}
