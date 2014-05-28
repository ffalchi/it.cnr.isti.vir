package it.cnr.isti.vir.util.math;


public class Norm {

	public static final double l1(double[] values) {
		double sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += Math.abs(values[i]);
        }
        return sum;
	}
	
	public static final float l1(float[] values) {
        float sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += Math.abs(values[i]);
        }
        return sum;
	}

	public static final int l1(int[] values) {
        int sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += Math.abs(values[i]);
        }
        return sum;
	}
	
	public static final double l2(double[] values) {
		double sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += (values[i]*values[i]);
        }
        return Math.sqrt(sum);
	}
	
	public static final float l2(float[] values) {
        float sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += (values[i]*values[i]);
        }
        return (float) Math.sqrt(sum);
	}
	
	public static final double l2(int[] values) {
		double sum = 0;
        for (int i=0; i<values.length; i++) {
        	sum += (values[i]*values[i]);
        }
        return (float) Math.sqrt(sum);
	}
}
