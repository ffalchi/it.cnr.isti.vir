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
package it.cnr.isti.vir.features;

import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.file.ArchiveException;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.util.MatrixConversion;
import it.cnr.isti.vir.util.MatrixMath;
import it.cnr.isti.vir.util.SplitInGroups;
import it.cnr.isti.vir.util.math.VectorMath;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Gaussian Mixture Models (GMM)
 */
public final class Gmm {
	/** vector dimension */
	private int d;
	/** number of Gaussians */
	private int k;
	/**
	 * Array of mixture weights (dimension: k)
	 *
	 * <p>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?w=(w_1,\dots,w_k),\quad{w_i}\,\mbox{is\,the\,mixture\,weight\,of\,i
	 * - t h \ , G a u s s i a n } " border="0"/>
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?\sum_{i=1}^kw_i=1"
	 * border="0"/>
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?w_i\geq0" border="0"/>
	 * </p>
	 */
	private double[] w;
	/**
	 * Means of the mixture (dimension: k*d) <br>
	 * <p>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?\text{mu}=(\mu_1,\dots,\mu_k),\quad{\mu_i}\,\mbox{is\,the\,mean\,vector\,of\,i
	 * - t h \ , G a u s s i a n } " border="0"/>
	 */
	private double[] mu;
	/**
	 * Variances of the mixture (dimension: k*d) <br>
	 * <p>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?\text{sigma}=(\sigma_1,\dots,\sigma_k),\quad{\sigma_i}\,\mbox{is\,the\,diagonal\,of\,i-th\,Gaussian\,covar
	 * i a n c e \ , m a t r i x } " border="0"/>
	 *
	 */
	private double[] sigma;
	/**
	 *
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?{-\frac{d}{2}\log(2\pi)}+{\log(w_i)}-{\frac{1}{2}\sum_{j=1}^d\left[\dfrac{(\mu_i^j)^2}{\sigma_i^j}+\log(\sigm
	 * a _ i ^ j ) \ r i g h t ] } " border="0"/>
	 *
	 */
	private double[] tmp_p;
	/**
	 * The minimum allowable value for the variances
	 */
	static final double min_sigma = (float) Math.pow(10, -10); // used in compute_param

	public int getD() {
		return d;
	}

	public int getK() {
		return k;
	}

	public double[] getW() {
		return w;
	}

	public double[] getMu() {
		return mu;
	}

	public double[] getSigma() {
		return sigma;
	}

	/**
	 * Reading Gmm parameter from binary file.<br>
	 * <p>
	 * This constructor is used to read gmm file such the one given in <a
	 * href="url">
	 * http://lear.inrialpes.fr/src/inria_fisher/inria_fisher_data_v1.tgz</a>
	 * <p>
	 * Information are stored in raw; there is no header. You need to know the
	 * byte order used when the data was stored.
	 * <p>
	 * <br>
	 * floats and ints take 4 bytes: <br>
	 * <br>
	 * <table border="1">
	 * <tr>
	 * <th>field</th>
	 * <th>type</th>
	 * <th>description</th>
	 * </tr>
	 * <tr>
	 * <td>d</td>
	 * <td>int
	 * <td>vector dimension</td>
	 * </tr>
	 * <tr>
	 * <td>k</td>
	 * <td>float*dim</td>
	 * <td>number of Gaussian</td>
	 * </tr>
	 * <tr>
	 * <td>w</td>
	 * <td>float*k</td>
	 * <td>mixture weights</td>
	 * </tr>
	 * <tr>
	 * <td>mu</td>
	 * <td>float*k*d</td>
	 * <td>means of the mixture</td>
	 * </tr>
	 * <tr>
	 * <td>sigma</td>
	 * <td>float*k*d</td>
	 * <td>diagonal elements of the covariance matrices</td>
	 * </tr>
	 * </table>
	 *
	 * @param filename
	 * @param byteorder
	 *            ByteOrder.LITTLE_ENDIAN or ByteOrder.BIG_ENDIAN
	 * @throws java.io.IOException
	 * @throws java.lang.NoSuchMethodException
	 * @throws java.lang.InstantiationException
	 * @throws java.lang.IllegalAccessException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public Gmm(String filename, ByteOrder byteorder) throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, Exception {

		if (!filename.endsWith(".gmm"))
			throw new Exception("Error opening the file" + filename+ "--->Invalid file extension ");

		File file = new File(filename);

		if (!file.exists()) {
			throw new IOException("Error: the file [" + file.getAbsolutePath()+ "] was not found");
		}

		System.out.println("Opening GMM parameter file: "+ file.getAbsolutePath());
		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
		System.out.print("Reading GMM...");
		try {
			int dim = bf.available();
			byte[] data = new byte[dim];
			bf.read(data);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			buffer.order(byteorder);
			d = buffer.getInt();
			k = buffer.getInt();
			w = new double[k];
			mu = new double[k * d];
			sigma = new double[k * d];
			for (int ind = 0; ind < k; ind++) {
				w[ind] = buffer.getFloat();
			}
			for (int ind = 0; ind < k * d; ind++) {
				mu[ind] = buffer.getFloat();
				sigma[ind] = buffer.getFloat(4 * (2 + k + k * d + ind));
			}

			setTmp_p();// initialization of the useful quantity temp_p
		} catch (IOException e) {
			System.out.println("");
			System.out.println("There was an issue reading from the file: " + e);
			System.exit(0);
		}
		bf.close();

		System.out.println(" done");
	}

	/**
	 * Estimation of GMM parameters on a large training set of local descriptors
	 * using the Expectation-Maximization (EM) algorithm to optimize a Maximum
	 * Likelihood (ML) criterion.
	 *
	 * <p>
	 * The ML estimation of a GMM in a non-convex optimization problem for more
	 * than one Gaussian. Hence, different initializations might lead to
	 * different solutions.<br>
	 * It is therefore common to run the K-means algorithm in order to find a
	 * suitable initialization for the GMM that is subsequently adapted using
	 * EM.
	 * <p>
	 * Too small values of the variance can lead to instabilities in the
	 * Gaussian computations, so the variance of each Gaussian is enforced to be
	 * no smaller then min_sigma=1E-{10}.
	 * 
	 * @param learningPoint
	 *            sample used in EM algorithm
	 * @param centroid
	 *            values used for initialization of GMM means
	 * @param sig
	 *            value used for initialization of covariance matrices
	 * @throws java.lang.NoSuchMethodException
	 * @throws java.lang.InstantiationException
	 * @throws java.lang.reflect.InvocationTargetException
	 * @throws java.lang.IllegalAccessException
	 * @throws java.io.IOException
	 * @throws it.cnr.isti.vir.file.ArchiveException
	 *
	 */
	public Gmm(FeaturesCollectorsArchive learningPoint, LFWords<AbstractFeature> centroid,
			float sig) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IOException,
			ArchiveException, Exception {

		d = 0;
		// ArrayList<float[]> arr = new ArrayList();
		ArrayList arrValues = new ArrayList();// new

		for (AbstractFeaturesCollector curr : learningPoint) {// reading archive
			Collection<AbstractFeature> afColl = curr.getFeatures();
			if (!afColl.isEmpty()) {
				Class<? extends AbstractFeature> fClass = afColl.iterator().next().getClass();

				if ((ALocalFeaturesGroup.class).isAssignableFrom(fClass)) {// group case
					ALocalFeaturesGroup group = ((ALocalFeaturesGroup) curr.getFeature(fClass));
					arrValues.addAll(group.getCollection());

				} else {
					// not group (no tested)
					if ((IArrayValues.class).isAssignableFrom(fClass))
						arrValues.addAll(afColl);
					else
						throw new Exception("Error: this feature"
								+ fClass.getName() + " is not yet supported");
				}
			}

		}
		double[][] learningPointMatrixTemp = MatrixConversion.getDoubles(arrValues);
//		int T = learningPointMatrixTemp.length;
		// int T = arr.size();
		// double[][] learningPointMatrixTemp = new double[T][d]; // T= sample
		// size
		//
		// for (int t = 0; t < T; t++) {
		// for (int i = 0; i < d; i++) {
		// float[] temp = arr.get(t);
		// learningPointMatrixTemp[t][i] = temp[i];
		// }
		// }
		// //arr = null;

		// GMM initialization

		AbstractFeature[] abs_centroidArr = centroid.getFeatures();
		Class<? extends AbstractFeature> cClass = (centroid.getFeatures()[1]).getClass();
		if ((IArrayValues.class).isAssignableFrom(cClass)) {
			k = abs_centroidArr.length;
			// initialize means
			
			double[][] dvalues = MatrixConversion.getDoubles(Arrays.asList((IArrayValues[]) abs_centroidArr));
			d=dvalues[1].length;
			mu = new double[d * k];
			for (int i = 0; i < k; i++) {
				int i_col = i * d;
				for (int j = 0; j < d; j++)
					mu[i_col + j] = dvalues[i][j];
			}
		} else
			throw new Exception("Error: this centroid feature"
					+ cClass.getName() + " is not yet supported");
		
		// initialize weights
		w = new double[k];
		float wi = 1.0f / k;
		Arrays.fill(w, wi);

		// initialize sigma (diagonal covariance matrices)
		sigma = new double[d * k];
		Arrays.fill(sigma, sig);
		System.out.println("Sigma at initialization= " + sig);

		// start EM algorithm

//		int iter_tot = 0;
		double old_key = 666;
		double key = 666;
		int niter = 1000;
		double[][] p;// posterior probabilities
		// p[i][t] is the soft assignment of descriptor
		// x_t=learningPointMatrix[t] to i-th Gaussian, i.e. conditional
		// probability of x_t given i
		// p[i][t] can also viewed as the responsibility that Gaussian i takes
		// for "explaining" the observation x_t
		for (int iter = 0; iter < niter; iter++) {
			// E-step: evaluate the posterior probabilities using the current
			// parameter values
			p = compute_p(learningPointMatrixTemp); // to do: threaded version

			handle_empty(learningPointMatrixTemp, p);

			// M-step: re-estimate the parameters using the current posterior
			// probabilities
			compute_params(learningPointMatrixTemp, p);

//			iter_tot++;
			// the EM algorithm is deemed to have converged when the change in
			// the log likelihood function, or alternatively in the parameters,
			// falls below some threshold.

			old_key = key;
			key = VectorMath.sum(mu);

			System.out.println("Sum(mu) iter n." + iter + ": " + old_key+ " --> " + key);
			if (Math.abs(key - old_key) < 0.05)// if(key==old_key)
				break;
			tmp_p = null;
		}

	}
	 
	/**
	 * Compute
	 * <p>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?{-\frac{d}{2}\log(2\pi)}+{\log(w_i)}-{\frac{1}{2}\sum_{j=1}^d\left[\dfrac{(\mu_i^j)^2}{\sigma_i^j}+\log(\sigm
	 * a _ i ^ j ) \ r i g h t ] } " border="0"/>
	 */
	public void setTmp_p() {
		// compute part of log(wi*pi(xt))
		tmp_p = new double[k];
		double log2pi = (-d / 2.0) * Math.log(2 * Math.PI);

		for (int i = 0; i < k; i++) {
			double tmp_sum = 0;
			int i_col = i * d;
			for (int j = 0; j < d; j++) {
				int ji = i_col + j;
				double mu_ji = mu[ji];
				double sigma_ji = sigma[ji];
				tmp_sum += Math.log(sigma_ji) + (mu_ji * mu_ji) / sigma_ji;
			}
			tmp_p[i] = log2pi + Math.log(w[i]) - 0.5 * tmp_sum;
		}
	}

	static class Compute_p_thread implements Runnable {
		private final int from;
		private final int to;
		private final double[][] p;
		private final double[][] x;
		private final int d;
		private final double[] mu;
		private final double[] sigma;
		private final double[] tmp_p;
		private final int k;

		Compute_p_thread(Gmm luncher, int from, int to, double[][] p,
				double[][] x
		// int d, int k,
		// float[] mu, float[] sigma, double[] tmp_p
		) {
			this.from = from;
			this.to = to;
			this.p = p;
			this.x = x;
			this.d = luncher.d;
			this.k = luncher.k;
			this.mu = luncher.mu;
			this.sigma = luncher.sigma;
			this.tmp_p = luncher.tmp_p;

		}

		@Override
		public void run() {

			for (int t = from; t <= to; t++) {

				double[] pt = p[t];
				double[] xt = x[t];

				compute_p_elem(pt, xt, mu, sigma, tmp_p, d, k);
			}
		}
	}

	private static final void compute_p_elem(double[] pt, double[] xt,
			double[] mu, double[] sigma, double[] tmp_p, int d, int k) {
		double tmp_sum = 0;
		for (int j = 0; j < d; j++) {
			double x_tj = xt[j];
			tmp_sum += (0.5 * x_tj - mu[j]) * x_tj / sigma[j];
		}
		double tmp_logSum = pt[0] = tmp_p[0] - tmp_sum;

		// case i>0
		for (int i = 1; i < k; i++) {
			int i_col = i * d;
			tmp_sum = 0;
			for (int j = 0; j < d; j++) {
				double x_jt = xt[j];
				tmp_sum += (0.5 * x_jt - mu[i_col + j]) * x_jt/ sigma[i_col + j];
			}

			pt[i] = tmp_p[i] - tmp_sum;
			tmp_logSum = log_sum(tmp_logSum, pt[i]);
		}

		for (int i = 0; i < k; i++) {
			pt[i] = Math.exp(pt[i] - tmp_logSum);
		}
	}

	/**
	 * Compute posterior probabilities:<br>
	 * p[i][t] is the probability that the low-level descriptor x_t is assigned
	 * to i-th Gaussian <br>
	 * p[i][t] can also viewed as the responsibility that Gaussian i takes for
	 * "explaining" the observation x_t
	 * 
	 * @param x
	 *            sample points, components are stored in columns
	 * @return posterior probabilities matrix
	 * @throws Exception
	 */
	public final double[][] compute_p(double[][] x) throws Exception {
		int T = x.length; // sample size
		double[][] p = new double[T][k];
		if (x[0].length != d)
			throw new Exception("Error: Invalid input dimensionality");

		if (tmp_p == null)
			setTmp_p();

		// for each
		int threadN = ParallelOptions.reserveNFreeProcessors() + 1;

		if (threadN == 1) {
			// Serial
			for (int t = 0; t <= T; t++) {
				compute_p_elem(p[t], x[t], mu, sigma, tmp_p, d, k);
			}
		} else {
			// Parallel
			Thread[] thread = new Thread[threadN];
			int[] group = SplitInGroups.split(T, thread.length);
			int from = 0;
			for (int i = 0; i < group.length; i++) {
				int curr = group[i];
				if (curr == 0)
					break;
				int to = from + curr - 1;
				thread[i] = new Thread(new Compute_p_thread(this, from, to, p,x));
				thread[i].start();
				from = to + 1;
			}

			for (Thread t : thread) {
				if (t != null)
					t.join();
			}
			ParallelOptions.free(threadN - 1);
		}

		return p;

	}

	/**
	 * Compute log(a+b) given log(a) and log(b)
	 * 
	 * @param log_a
	 * @param log_b
	 * @return
	 */
	static final double log_sum(double log_a, double log_b) {
		if (log_a < log_b)
			return (log_b + Math.log(1 + Math.exp(log_a - log_b)));
		else
			return (log_a + Math.log(1 + Math.exp(log_b - log_a)));
	}

	/**
	 *
	 * @param x
	 *            sample points matrix, vector components are stored in columns
	 * @param p
	 *            posterior probabilities matrix
	 * @throws Exception
	 */
	private void handle_empty(double[][] x, double[][] p) throws Exception {
		int T = x.length;

		long nzero = MatrixMath.count_occurrences(p, 0.0f);// long nzero=MatrixMath.count_occurrences(p,0.0);
		System.out.println("number of 0 posterior probabilities(float): "+ nzero + "/(" + k * T + ")=" + nzero * 100.0 / (k * T) + "%");

		float[] w_tmp = new float[k];
		for (int t = 0; t < T; t++) {
			double[] pt = p[t];
			for (int i = 0; i < k; i++)
				w_tmp[i] += pt[i];
		}
		// w_tmp[i]= probability for the observations X to have been generated
		// by the i-th Gaussian
		// If there exists some index i such that w_tmp[i]=0, then the posterior
		// probabilities will be redistributed

		int bigprime = 1000003;// need bigprime>k

		int generator = bigprime % k;// generator of cyclic group Z/kZ-->
										// {j*generator (mod k),
										// j=0,..,k-1}={0,1,2,..,k-1}

		for (int i = 0; i < k; i++)
			if (w_tmp[i] == 0.0f) {
				System.out.println("centroid " + i + " is empty..");
				int i2 = i;
				boolean control = true;
				for (int j = 0; j < k - 1; j++) {// jump case i2=(i+k*generator)%k=i;
					i2 = (i2 + generator) % k;
					if (w_tmp[i2] > 0) {
						control = false;
						break;
					}

				}
				if (control)
					throw new Exception(
							"could not find centroid to split, very bad input data!");

				int i2_col = i2 * d;
				double val = sigma[i2_col];
				int split_dim = 0;// splitting dimension (the one with highest variance)
				for (int j = 1; j < d; j++) {
					double sigma_ji2 = sigma[i2_col + j];
					if (sigma_ji2 > val) {
						val = sigma_ji2;
						split_dim = j;
					}
				}
				// transfer almost half of the point from i2 -->i
				int nnz = 0;// total number of transferable points
				int nt = 0;// number of transferred point
				double mu_val = mu[i2_col + split_dim];
				for (int t = 0; t < T; t++) {
					double[] pt = p[t];
					if (pt[i2] > 0) {
						nnz++;
						if (x[t][split_dim] < mu_val) {
							pt[i] = pt[i2];
							pt[i2] = 0;
							nt++;
						}
					}
				}

				System.out.println("split" + i2 + " at dim " + split_dim
						+ " (variance " + sigma[i2_col + split_dim]
						+ "). Transferred" + nt + "/" + nnz + " pts)");
				w_tmp[i2] = -1;// no future splitting
			}

	}

	static class Compute_params_thread implements Runnable {
		private final int from;
		private final int to;
		private final double[][] p;
		private final double[][] x;
		private final int d;
		private final double[] mu;
		private final double[] mu_old;
		private final double[] sigma;
		private final double[] w;
		private final double[] tmp_p;
		private final int k;
		private final int T;
		private final Gmm luncher;
		private Integer nz = 0;

		Compute_params_thread(Gmm luncher, int from, int to, double[][] p,
				double[][] x, double[] mu_old, Integer nz) {
			this.from = from;
			this.to = to;
			this.luncher = luncher;
			this.p = p;
			this.x = x;
			this.d = luncher.d;
			this.k = luncher.k;
			this.mu = luncher.mu;
			this.sigma = luncher.sigma;
			this.tmp_p = luncher.tmp_p;
			this.T = x.length;
			this.mu_old = mu_old;
			this.w = luncher.w;
			this.nz = nz;
		}

		@Override
		public void run() {
			for (int i = from; i <= to; i++) {
				double wtmp = 0;
				int i_col = i * d;
				for (int t = 0; t < T; t++) {
					double pti = p[t][i];
					double[] xt = x[t];
					wtmp += pti;// contribution to gaussian weight-->number of points assigned to i-th Gaussian
					for (int j = 0; j < d; j++) {
						int ji = i_col + j;
						// contribution to mu
						mu[ji] += (float) (xt[j] * pti);
						// contribution to sigma
						double diff = xt[j] - mu_old[ji];
						sigma[ji] += (float) pti * diff * diff;
					}
				}// end sum on t

				// handle too small sigma value
				for (int j = 0; j < d; j++) {
					int ji = i_col + j;
					if (sigma[ji] < min_sigma) {
						sigma[ji] = min_sigma;
						nz++;
					}
				}

				w[i] = (float) wtmp;

				for (int j = 0; j < d; j++) {
					int ji = i_col + j;
					mu[ji] /= w[i];// w[i]!=0 only when compute_params is
									// invoked after handle_emplty, otherwise
									// infinity number could occur ..
					sigma[ji] /= w[i];
				}
			}
		}
	}

	/**
	 * Re-estimate GMM parameters given the current posterior probabilities
	 * 
	 * @param x
	 *            sample points matrix, vector components are stored in columns
	 * @param p
	 *            posterior probabilities matrix
	 * @throws Exception
	 */
	private void compute_params(double[][] x, double[][] p) throws Exception {
		int T = x.length;

		double[] mu_old = mu;
		mu = new double[k * d];
		sigma = new double[k * d];

		// for each gaussian
		int threadN = ParallelOptions.reserveNFreeProcessors() + 1;
		Thread[] thread = new Thread[threadN];
		Integer[] nz_arr = new Integer[threadN];
		int[] group = SplitInGroups.split(k, thread.length);
		int from = 0;
		for (int i = 0; i < group.length; i++) {
			nz_arr[i] = new Integer(0);
			int curr = group[i];
			if (curr == 0)
				break;
			int to = from + curr - 1;
			thread[i] = new Thread(new Compute_params_thread(this, from, to, p,
					x, mu_old, nz_arr[i]));
			thread[i].start();
			from = to + 1;
		}

		int nz = 0;
		for (Thread t : thread) {
			if (t != null)
				t.join();
		}
		ParallelOptions.free(threadN - 1);

		for (Integer temp : nz_arr) {
			nz += temp;
		}

		float mu_sum = 0;
		float w_norm = 0;

		for (int i = 0; i < w.length; i++) {
			w_norm += Math.abs(w[i]);// norm1
		}

		for (int i = 0; i < mu.length; i++) {
			mu_sum += mu[i];
		}

		if (nz != 0)
			System.out.println("WARN " + nz
					+ " sigma diagonals are too small (set to " + min_sigma+ ")");

		if (Float.isInfinite(mu_sum))
			throw new Exception("Infinity number occurs");

		// float w_normsqr=0;
		for (int i = 0; i < k; i++) {
			w[i] /= w_norm;
			// w_normsqr+=w[i]*w[i];
		}
		// double imfac=k*w_normsqr;
		// System.out.println("imfac="+imfac);

	}

	/**
	 * Write Gmm parameter binary file.<br>
	 * Information are stored in raw; there is no header.
	 * <p>
	 * <br>
	 * floats and ints take 4 bytes: <br>
	 * <br>
	 * <table border="1">
	 * <tr>
	 * <th>field</th>
	 * <th>type</th>
	 * <th>description</th>
	 * </tr>
	 * <tr>
	 * <td>d</td>
	 * <td>int
	 * <td>vector dimension</td>
	 * </tr>
	 * <tr>
	 * <td>k</td>
	 * <td>float*dim</td>
	 * <td>number of Gaussians</td>
	 * </tr>
	 * <tr>
	 * <td>w</td>
	 * <td>float*k</td>
	 * <td>mixture weights</td>
	 * </tr>
	 * <tr>
	 * <td>mu</td>
	 * <td>float*k*d</td>
	 * <td>means of the mixture</td>
	 * </tr>
	 * <tr>
	 * <td>sigma</td>
	 * <td>float*k*d</td>
	 * <td>diagonal elements of the covariance matrices</td>
	 * </tr>
	 * </table>
	 * 
	 * @param filename
	 * @throws java.io.FileNotFoundException
	 * @throws java.io.IOException
	 */
	public final void writeData(String filename) throws FileNotFoundException,
			IOException {

		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(filename)));
		out.writeInt(d);
		out.writeInt(k);
		for (int i = 0; i < k; i++)
			out.writeFloat((float) w[i]);
		for (int l = 0; l < k * d; l++)
			out.writeFloat((float) mu[l]);
		for (int l = 0; l < k * d; l++)
			out.writeFloat((float) sigma[l]);
		out.close();

	}

}