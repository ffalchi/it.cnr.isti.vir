/*******************************************************************************
 * Copyright (c) 2013, Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.distance;


/**
 * @author Lucia Vadicamo
 *  Jensen Shannon divergence is not metric
 * The square-root of the JSDiv is metric
 * 
 */

public class JensenShannonDistance {


	/**
	 * compute the square-root of the J-S Divergence (logs are taken to base two so that the outcome is bounded in [0,1])
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static final double get(float[]f1, float[]f2) {
				return Math.sqrt(getSquared(f1,f2)); 
	}
	public static final double get(float[]f1, float[]f2,double maxDist) {
		double dist=Math.sqrt(getSquared(f1,f2));
		if (dist>maxDist)
			return  dist;
		else 
			return -dist;
				
}

	/**
	 * compute the square-root of the J-S Divergence (logs are taken to base two so that the outcome is bounded in [0,1])
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static double get(double[] f1, double[] f2) {
		return Math.sqrt(getSquared(f1,f2));  
	}
	
	/**
	 * compute the J-S Divergence (logs are taken to base two so that the outcome is bounded in [0,1])
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static final double getSquared(float[]f1, float[]f2) {
		double dist = 0;
		for (int i = 0; i < f1.length; i++) {
			double f1_i = f1[i];
			if (f1_i != 0) {
				double f2_i = f2[i];
				if (f2_i != 0) {
					double sum = f1_i + f2_i;
					double logsum = Math.log(sum);
					dist += sum * logsum;

					dist -= f1_i * (Math.log(f1_i));
					dist -= f2_i * (Math.log(f2_i));
				}
			}
		}
		return 1 - dist/(2* Math.log(2));
	}

	/**
	 * compute the J-S Divergence (logs are taken to base two so that the outcome is bounded in [0,1])
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static double getSquared(double[] f1, double[] f2) {
		double dist = 0;
		for (int i = 0; i < f1.length; i++) {
			double f1_i = f1[i];
			if (f1_i != 0) {
				double f2_i = f2[i];
				if (f2_i != 0) {
					double sum = f1_i + f2_i;
					double logsum = Math.log(sum);
					dist += sum * logsum;

					dist -= f1_i * (Math.log(f1_i));
					dist -= f2_i * (Math.log(f2_i));
				}
			}
		}
		return 1 - dist/(2* Math.log(2));
	}

}
