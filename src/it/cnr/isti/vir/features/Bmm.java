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

import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.file.FeaturesCollectorsArchives;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.global.ParallelOptions;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.MatrixConversion;
import it.cnr.isti.vir.util.MatrixMath;
import it.cnr.isti.vir.util.RandomOperations;
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
import java.util.Iterator;
import java.util.Random;

/**
 * Gaussian Mixture Models (GMM)
 *
 */
public final class Bmm {
	/** observed data vector dimension */
	private int d;
	/** number of Bernoulli */
	private int k;
	/**
	 * Array of mixture weights (dimension: k)
	 *
	 * <p>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?w=(w_1,\dots,w_k),\quad{w_i}\,\mbox{is\,the\,mixture\,weight\,of\,i-th\,Bernoull
	 * i } " border="0"/>
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?\sum_{i=1}^kw_i=1"
	 * border="0"/>
	 * <p>
	 * <img src="http://latex.codecogs.com/gif.latex?w_i\geq0" border="0"/>
	 * </p>
	 */
	private double[] w;
	/**
	 * Parameters of the multivariate Bernoulli (dimension: k*d) <br>
	 * <p>
	 * <img
	 * src="http://latex.codecogs.com/gif.latex?\text{mu}=(\mu_1,\dots,\mu_k)"
	 * border="0"/>
	 */
	private double[] mu;

	/**
	 * Part of the posterior probability that doesn't depends on the observed
	 * data <br>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?{{\log(w_i)}+{\sum_{j=1}^d\left[\log(1-\mu_i^j)}\right
	 * ] } " border="0"/>
	 *
	 */
	private double[] tmp_p;

	// /**
	// * The minimum allowable value for the variances
	// */
	// static final double min_sigma=(float)Math.pow(10, -10); //used in
	// compute_param

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

	/**
	 * Reading Bmm parameter from binary file.<br>
	 * <p>
	 * This constructor is used to read gmm file
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
	 * <td>number of Bernoulli</td>
	 * </tr>
	 * <tr>
	 * <td>w</td>
	 * <td>float*k</td>
	 * <td>mixture weights</td>
	 * </tr>
	 * <tr>
	 * <td>mu</td>
	 * <td>float*k*d</td>
	 * <td>Bernoulli parameters</td>
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
	public Bmm(String filename, ByteOrder byteorder) throws IOException,
	SecurityException, NoSuchMethodException, IllegalArgumentException,
	InstantiationException, IllegalAccessException,
	InvocationTargetException, Exception {

		if (!filename.endsWith(".bmm"))
			throw new Exception("Error opening the file" + filename
					+ "--->Invalid file extension ");

		File file = new File(filename);

		if (!file.exists()) {
			throw new IOException("Error: the file [" + file.getAbsolutePath()
					+ "] was not found");
		}

		Log.info("Opening BMM parameter file: "
				+ file.getAbsolutePath());
		BufferedInputStream bf = new BufferedInputStream(new FileInputStream(
				file));
		Log.info_noNewLine("Reading BMM...");
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
			for (int ind = 0; ind < k; ind++) {
				w[ind] = buffer.getFloat();
			}
			for (int ind = 0; ind < k * d; ind++) {
				mu[ind] = buffer.getFloat();
			}

			setTmp_p();// initialization of the useful quantity temp_p
		} catch (IOException e) {
			System.out.println("");
			System.out
			.println("There was an issue reading from the file: " + e);
			System.exit(0);
		}
		bf.close();

		Log.info(" done");
	}

	/**
	 * Estimation of BMM parameters on a large training set (extracted from the
	 * archives) of local descriptors using the Expectation-Maximization (EM)
	 * algorithm to optimize a Maximum Likelihood (ML) criterion.
	 *
	 * <p>
	 * The ML estimation of a BMM in a non-convex optimization problem, hence,
	 * different initializations might lead to different solutions.<br>
	 *
	 *
	 * @param archives
	 *            archive of ILongBinaryValues
	 * @param learningPointFilename
	 *            name of the file in witch the learning point will be saved
	 * @param fGroupClass
	 *            Local group feature Class
	 * @param nMaxLF
	 *            Max number of local features to consider
	 * @param kval
	 *            number of Bernoulli
	 */
	public Bmm(FeaturesCollectorsArchives archives, Class fGroupClass,
			String learningPointFilename, int nMaxLF, int kval)
					throws SecurityException, Exception {
		this(archives, fGroupClass, FeaturesCollectorsArchive.create(new File(
				learningPointFilename)), nMaxLF, kval);
	}

	/**
	 * Estimation of BMM parameters on a large training set (extracted from the
	 * archives) of local descriptors using the Expectation-Maximization (EM)
	 * algorithm to optimize a Maximum Likelihood (ML) criterion.
	 *
	 * <p>
	 * The ML estimation of a BMM in a non-convex optimization problem, hence,
	 * different initializations might lead to different solutions.<br>
	 *
	 *
	 * @param archives
	 *            archive of ILongBinaryValues
	 * @param learningPoint
	 *            archive in witch the learning point will be saved
	 * @param fGroupClass
	 *            Local group feature Class
	 * @param nMaxLF
	 *            Max number of local features to consider
	 * @param kval
	 *            number of Bernoulli
	 */

	public Bmm(FeaturesCollectorsArchives archives, Class fGroupClass,
			FeaturesCollectorsArchive learningPoint, int nMaxLF, int kval)
					throws Exception {
		Log.info("Archive contains " + archives.size()+ " features collectors.");

		int count = 0;
		// Counting total number of Local Features
		int nLFs = 0;
		if (fGroupClass != null) {
			for (int iA = 0; iA < archives.getNArchives(); iA++) {
				for (Iterator<AbstractFeaturesCollector> it = archives
						.getArchive(iA).iterator(); it.hasNext();) {
					AbstractFeaturesCollector fc = it.next();
					if (fc == null)
						throw new Exception("IFeaturesCollector null!");
					ALocalFeaturesGroup group = ((ALocalFeaturesGroup) fc
							.getFeature(fGroupClass));
					nLFs += group.size();
					if (++count % 1000 == 0) {
						Log.info_verbose(count + " " + nLFs);
					}
				}
			}
		} else {
			nLFs = archives.size();
		}

		double lfSelectionRate = (double) nMaxLF / nLFs;
		double fcSelectionRate = 1.0;
		Log.info("Archives contain " + nLFs + " local features.");
		Log.info("Max number of local features to consider:" + nMaxLF);
		Log.info("Local Features selection rate: " + lfSelectionRate);
		ArrayList<AbstractFeature> list = new ArrayList<AbstractFeature>();

		int lfCount = 0;
		int fcCount = 0;

		for (int iObj = 0; iObj < archives.size(); iObj++) {
			if (RandomOperations.trueORfalse(fcSelectionRate)) {
				AbstractFeaturesCollector fc = archives.get(iObj);
				AbstractID id = ((IHasID) fc).getID();

				if (fGroupClass != null) {
					ArrayList<ALocalFeature> selectedPoint = new ArrayList<ALocalFeature>();
					ALocalFeaturesGroup group = ((ALocalFeaturesGroup) fc
							.getFeature(fGroupClass));
					fcCount++;

					for (int i = 0; i < group.size(); i++) {
						lfCount++;
						if (RandomOperations.trueORfalse(lfSelectionRate)) {
							ALocalFeature lf = group.getFeature(i);
							list.add(lf.getUnlinked());
							selectedPoint.add(lf.getUnlinked());
						}

					}

					if (!selectedPoint.isEmpty()) {

						ALocalFeature[] lfarr = selectedPoint
								.toArray(new ALocalFeature[1]);
						group.lfArr = lfarr;
						FeaturesCollectorArr fcSelectedPoints = new FeaturesCollectorArr(
								group, id);
						learningPoint.add(fcSelectedPoints);
					}
				} else {
					fcCount++;
					lfCount++;

					if (RandomOperations.trueORfalse(lfSelectionRate)) {
						list.add(fc);
						learningPoint.add(fc);
					}

				}
			}

			if ((iObj + 1) % 10000 == 0) {
				Log.info_verbose(" --> " + list.size() + " lfs read out of "
						+ lfCount + " found in " + fcCount + " docs out of "
						+ (iObj + 1) + "/" + archives.size() + ".");
			}
		}
		Log.info_verbose(" --> " + list.size()
				+ " local features read out of " + lfCount + " found in "
				+ fcCount + " fcCount out of " + archives.size() + ".");

		learningPoint.close();
		bmm_extimation_EM(learningPoint, kval);

	}

	/**
	 * Estimation of BMM parameters on a large training set of local descriptors
	 * using the Expectation-Maximization (EM) algorithm to optimize a Maximum
	 * Likelihood (ML) criterion.
	 *
	 * <p>
	 * The ML estimation of a BMM in a non-convex optimization problem, hence,
	 * different initializations might lead to different solutions.<br>
	 *
	 * @param learningPoint
	 *            sample used in EM algorithm (archive of ILongBinaryValues)
	 * @param kval
	 *            number of Bernoulli
	 * 
	 *
	 */

	public Bmm(FeaturesCollectorsArchive learningPoint, int kval)
			throws Exception {
		bmm_extimation_EM(learningPoint, kval);
	}

	public void bmm_extimation_EM(FeaturesCollectorsArchive learningPoint,
			int kval) throws Exception {
		d = 0;

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
		d=learningPointMatrixTemp[0].length;
		int T=learningPointMatrixTemp.length;

		// BMM initialization

		k = kval;
		// initialize means
		int mu_dim = d * k;
		mu = new double[mu_dim];
		float mu_min = 0.25f;
		float mu_max = 0.751f;
		float diff = mu_max - mu_min;
		//		for (int i = 0; i < k; i++) {
		//			int i_col = i * d;
		//			double musum=0;
		//			for (int j = 0; j < d; j++) {
		//				int ji = i_col + j;
		//				mu[ji]=new Random().nextFloat() * (diff) + mu_min;
		//				musum+=mu[ji];
		//			}
		//			for (int j = 0; j < d; j++) {
		//				int ji = i_col + j;
		//				mu[ji]/=musum;
		//			}
		//		}
		//		
		for (int i1 = 0; i1 < mu_dim; i1++) {
			mu[i1] = new Random().nextFloat() * (diff) + mu_min;
		}

		// initialize weights
		w = new double[k];
		float wi = 1.0f / k;
		Arrays.fill(w, wi);

		// start EM algorithm
		double eps=0.05;
		int iter_tot = 0;
		double old_key = -Double.MAX_VALUE;
		double key = Double.MIN_VALUE;
		double[] mu_old=new double[k*d];
		Arrays.fill(mu_old, 2);
		int niter = 500;
		double[][] p;// posterior probabilities
		// p[i][t] is the soft assignment of descriptor
		// x_t=learningPointMatrix[t] to i-th Bernoulli, i.e. conditional
		// probability of x_t given i
		// p[i][t] can also viewed as the responsibility that Bernoulli i takes
		// for "explaining" the observation x_t
		for (int iter = 0; iter < niter; iter++) {
			// E-step: evaluate the posterior probabilities using the current
			// parameter values

			p = compute_p(learningPointMatrixTemp);

			handle_empty(learningPointMatrixTemp, p);


			key =VectorMath.sum(mu);

			double muDiff=0;
			for (int i = 0; i < mu.length; i++) {
				double dif=mu[i]-mu_old[i];
				muDiff+=dif*dif;
			}
			muDiff=Math.sqrt(muDiff);
			double muSumDiff=Math.abs(key - old_key);
			Log.info_verbose("iter n." + iter + " mu_sumDiff : " +muSumDiff+  "  --    norm(mu_new-mu_old): "+muDiff);
			// the EM algorithm is deemed to have converged when the change in
			// the log likelihood function, or alternatively in the parameters,
			// falls below some threshold.
			//if (Math.abs(key - old_key) < eps*Math.abs(key) || Math.abs(key - old_key)<2.22E-16d)
			if ( muDiff<eps && muSumDiff<eps) {//deltaLogL<eps*Math.abs(key) ||
				Log.info_verbose("muDiff &&  muSumDiff<"+eps);
				Log.info_verbose("mu_sum "+key);
				break;
			}
			mu_old=mu;
			old_key = key;

			// M-step: re-estimate the parameters using the current posterior
			// probabilities
			compute_params(learningPointMatrixTemp, p);
			iter_tot++;
			tmp_p = null;
		}

	}

	/**
	 * Compute part of the posterior probability that doesn't depends on the
	 * observed data <br>
	 * <img src=
	 * "http://latex.codecogs.com/gif.latex?{{\log(w_i)}+{\sum_{j=1}^d\left[\log(1-\mu_i^j)}\right
	 * ] } " border="0"/>
	 */
	public void setTmp_p() {
		// compute part of log(wi*pi(xt))
		tmp_p = new double[k];

		for (int i = 0; i < k; i++) {
			double tmp_sum = 0;
			int i_col = i * d;
			for (int j = 0; j < d; j++) {
				int ji = i_col + j;
				double mu_ji = mu[ji];
				tmp_sum += Math.log(1 - mu_ji);
			}
			tmp_p[i] = Math.log(w[i]) + tmp_sum;
		}
	}

	static class Compute_p_thread implements Runnable {
		private final int from;
		private final int to;
		private final double[][] p;
		private final double[][] x;
		private final int d;
		private final double[] mu;
		private final double[] tmp_p;
		private final int k;

		Compute_p_thread(Bmm luncher, int from, int to, double[][] p,
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
			this.tmp_p = luncher.tmp_p;

		}

		@Override
		public void run() {

			for (int t = from; t <= to; t++) {

				double[] pt = p[t];
				double[] xt = x[t];

				compute_p_elem(pt, xt, mu, tmp_p, d, k);
			}
		}
	}

	private static final void compute_p_elem(double[] pt, double[] xt,
			double[] mu, double[] tmp_p, int d, int k) {
		double tmp_sum = 0;
		for (int j = 0; j < d; j++) {
			double x_tj = xt[j];
			double mu_j = mu[j];
			tmp_sum += Math.log(mu_j / (1 - mu_j)) * x_tj;
		}
		double tmp_logSum = pt[0] = tmp_p[0] + tmp_sum;

		// case i>0
		for (int i = 1; i < k; i++) {
			int i_col = i * d;
			tmp_sum = 0;
			for (int j = 0; j < d; j++) {
				double x_tj = xt[j];
				double mu_ij = mu[i_col + j];
				tmp_sum += Math.log(mu_ij / (1 - mu_ij)) * x_tj;
			}

			pt[i] = tmp_p[i] + tmp_sum;
			tmp_logSum = log_sum(tmp_logSum, pt[i]);
		}

		for (int i = 0; i < k; i++) {
			//			double val= pt[i] - tmp_logSum; 
			//			if(val<-32) pt[i]=0.0f;
			//			else
			//				pt[i] = Math.exp(val);

			pt[i] = Math.exp(pt[i] - tmp_logSum);
		}
		//		double contol = VectorMath.sum(pt);

	}

	/**
	 * Compute posterior probabilities:<br>
	 * p[t][i] is the probability that the low-level descriptor x_t is assigned
	 * to i-th Gaussian <br>
	 * p[t][i] can also viewed as the responsibility that Gaussian i takes for
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
			for (int t = 0; t < T; t++) {
				compute_p_elem(p[t], x[t], mu, tmp_p, d, k);
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
				thread[i] = new Thread(new Compute_p_thread(this, from, to, p,
						x));
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

		long nzero = MatrixMath.count_occurrences(p, 0.0f);// long
		// nzero=MatrixMath.count_occurrences(p,
		// 0.0);
		Log.info_verbose("number of 0 posterior probabilities(float): "
				+ nzero + "/(" + k * T + ")=" + nzero * 100.0 / (k * T) + "%");

		float[] w_tmp = new float[k];
		for (int t = 0; t < T; t++) {
			double[] pt = p[t];
			for (int i = 0; i < k; i++)
				w_tmp[i] += p[t][i];
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
				Log.info_verbose("initialMu " + i + " is empty..");
				int i2 = i;
				boolean control = true;
				for (int j = 0; j < k - 1; j++) {// jump case
					// i2=(i+k*generator)%k=i;
					i2 = (i2 + generator) % k;
					if (w_tmp[i2] > 0) {
						control = false;
						break;
					}

				}
				if (control)
					throw new Exception(
							"could not find initialMu to split, very bad input data!");

				int i2_col = i2 * d;
				double maxVar = mu[i2_col] * (1 - mu[i2_col]);
				int split_dim = 0;// splitting dimension (the one with highest
				// variance)
				for (int j = 1; j < d; j++) {
					double var_ji2 = mu[i2_col + j] * (1 - mu[i2_col + j]);
					if (var_ji2 > maxVar) {
						maxVar = var_ji2;
						split_dim = j;
					}
				}

				// transfer almost half of the point from i2 -->i
				int nnz = 0;// total number of transferable points
				int nt = 0;// number of transferred point
				double mu_maxVar = mu[i2_col + split_dim];
				for (int t = 0; t < T; t++) {
					double[] pt = p[t];
					if (pt[i2] > 0) {
						nnz++;
						if (x[t][split_dim] < mu_maxVar) {
							pt[i] = pt[i2];
							pt[i2] = 0;
							nt++;
						}
					}
				}

				Log.info_verbose("split" + i2 + " at dim " + split_dim
						+ " (theoretical variance " + mu[i2_col + split_dim]
								* (1 - mu[i2_col + split_dim]) + "). Transferred" + nt
								+ "/" + nnz + " pts)");
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
		private final double[] w;
		private final double[] tmp_p;
		private final int k;
		private final int T;
		private final Bmm luncher;
		private Integer nz = 0;

		Compute_params_thread(Bmm luncher, int from, int to, double[][] p,
				double[][] x, double[] mu_old, Integer nz) {
			this.from = from;
			this.to = to;
			this.luncher = luncher;
			this.p = p;
			this.x = x;
			this.d = luncher.d;
			this.k = luncher.k;
			this.mu = luncher.mu;
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
					wtmp += pti;// contribution to gaussian weight-->number of
					// points assigned to i-th Gaussian

					for (int j = 0; j < d; j++) {
						int ji = i_col + j;
						// contribution to mu
						mu[ji] += (float) (xt[j] * pti);
						// //contribution to sigma
						// double diff=xt[j]-mu_old[ji];
						// sigma[ji]+=(float) pti*diff*diff;
					}
				}// end sum on t

				w[i] = (float) wtmp;

				for (int j = 0; j < d; j++) {
					int ji = i_col + j;
					mu[ji] /= w[i];// w[i]!=0 only when compute_params is
					// invoked after handle_emplty, otherwise
					// infinity number could occur ..
					if (mu[ji] < 0.10f) {
						mu[ji] = 0.10;
						nz++;
					} else {
						if (mu[ji] > 0.90f) {
							mu[ji] = 0.90;
							nz++;
						}
					}
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

		Integer nz = 0;
		for (Thread t : thread) {
			if (t != null)
				t.join();
		}
		ParallelOptions.free(threadN - 1);

		for (Integer temp : nz_arr) {
			if(temp!=null)
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
			Log.info_verbose("WARN "
					+ nz
					+ " mu elements are too close to the extrema 0 and 1. The values are now setted between 0.1 and 0.9");

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
		out.close();

	}

}