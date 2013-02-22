package it.cnr.isti.vir.pca;

public class PrincipalComponent implements Comparable<PrincipalComponent> {

	public double eigenValue;
	public double[] eigenVector;

	public PrincipalComponent(double eigenValue, double[] eigenVector) {
		this.eigenValue = eigenValue;
		this.eigenVector = eigenVector;
	}

	public int compareTo(PrincipalComponent o) {
		int ret = 0;
		if (eigenValue > o.eigenValue) {
			ret = -1;
		} else if (eigenValue < o.eigenValue) {
			ret = 1;
		}
		return ret;
	}

	
	public String toString() {
		String tStr =  "Eigenvalue: " + eigenValue + ", eigenvector: [";
		for ( double e : eigenVector) {
			tStr += e + " ";
		}
		tStr +=  "]";
		return tStr;
	}
}
