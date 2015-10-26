package it.cnr.isti.vir.util.stats;

import it.cnr.isti.vir.features.localfeatures.evaluation.ILFEval;

public class CumulativeDistribution {
	private final double min;
	private final double step;
	private long count = 0;
	private final long[] occ;
	
	/**
	 * @param min
	 * @param step
	 * @param nStep
	 */
	public CumulativeDistribution(double min, double step, int nStep) {
		this.min = min;
		this.step = step;
		occ = new long[nStep];
	}
	
	public void add(double value) {
		count++;
		
		int index = (int) Math.floor(( value - min ) / step) +1;
		for (int i=index; i<occ.length; i++ ) {
			occ[i]++;
		}

	}
	
	public void add(float[] f ) {
		for (int i=0; i< f.length; i++) {
			this.add(f[i]);
		}
	}
	
	public void add(double[] f ) {
		for (int i=0; i< f.length; i++) {
			this.add(f[i]);
		}
	}
	
	public void add(ILFEval[] eval ) {
		for (int i=0; i< eval.length; i++) {
			this.add(eval[i].getValue());
		}
	}
	
	public double[] getCumulativeDistributions() {
		double[] values = new double[occ.length];
		for (int i=0; i<occ.length; i++ ) {
			values[i] = occ[i]/(double) count;
		}
		return values;
	}
	
	public double[] getCumulativeDistributionsValues() {
		double[] values = new double[occ.length];
		for (int i=0; i<occ.length; i++ ) {
			values[i]=step*i+min;
		}
		return values;
	}
	
	public String toString() {
		String tStr = "";
		for (int i=0; i<occ.length; i++ ) {
			if ( occ[i] > 0 && occ[i] < count )
				tStr +=  (step*i+min) + "\t" + ( occ[i]/(double) count) + "\n";
		}
		return tStr;
	}
}
