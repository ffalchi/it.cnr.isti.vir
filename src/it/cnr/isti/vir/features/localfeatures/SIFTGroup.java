package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class SIFTGroup extends AbstractLFGroup<SIFT> {

	public SIFTGroup(SIFT[] arr, IFeaturesCollector fc) {
		super(arr, fc);
	}

	public SIFTGroup(IFeaturesCollector fc) {
		super(fc);
	}

	public SIFTGroup(DataInput in) throws Exception {
		this(in, null);
	}


	public static final byte version = 1;

	public SIFTGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.get();
		int size = in.getInt();
		lfArr = new SIFT[size];
		if (size == 0)
			return;

		for (int i = 0; i < size; i++) {
			lfArr[i] = new SIFT(in, this);
		}

		if (version > 0) {
			readEval(in);
		}
	}

	public SIFTGroup(DataInput in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.readByte();
		int size = in.readInt();
		// lfArr = new SIFT[size];
		// if ( size == 0 ) return;

		lfArr = SIFT.getSIFT(in, size, this);

		// for (int i=0; i<size; i++ ) {
		// lfArr[i] = new SIFT(in, this);
		// }
		if (version > 0) {
			readEval(in);
		}
	}

	public boolean checkByteFormatConsistency() {
		byte[] bytes = SIFT.getBytes(lfArr);
		SIFT[] sift = SIFT.getSIFT(bytes, this);
		for (int i = 0; i < lfArr.length; i++) {
			if (!lfArr[i].equals(sift[i])) {
				return false;
			}
		}
		return true;
	}

	public final void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeInt(lfArr.length);

		// if ( !checkByteFormatConsistency() ) {
		// System.err.println("MO' SON CAZZI!!!");
		// }

		// out.writeInt(FeaturesClassCollection.getClassID(list.getFirst().getClass()));
		byte[] bytes = SIFT.getBytes(lfArr);
		out.write(bytes);
		// for (int i=0; i<lfArr.length; i++ ) {
		// lfArr[i].writeData(out);
		// }
		writeEval(out);
	}

	/*
	 * Read keypoints from the given file pointer and return the list of
	 * keypoints. The file format starts with 2 integers giving the total number
	 * of keypoints and the size of descriptor vector for each keypoint
	 * (currently assumed to be 128). Then each keypoint is specified by ...
	 */
	public SIFTGroup(BufferedReader br, IFeaturesCollector fc)
			throws IOException {
		super(fc);
		String[] temp = br.readLine().split("(\\s)+");
		// assert(temp.length == 2);
		int n = Integer.parseInt(temp[0]);
		int len = Integer.parseInt(temp[1]);
		// assert(len == SIFT.vLen);

		this.lfArr = new SIFT[n];
		for (int i = 0; i < n; i++) {
			this.lfArr[i] = new SIFT(br, this);
		}
	}

	// static final public LocalFeaturesGroup<SIFT> readKeys(BufferedReader br)
	// throws IOException
	// {
	// return new SIFTGroup(br);
	// }

	public static final double getMinDistance(SIFTGroup lFGroup1,
			SIFTGroup lFGroup2) {
		double min = Double.MAX_VALUE;
		for (int i1 = 0; i1 < lFGroup1.lfArr.length; i1++) {
			SIFT s1 = lFGroup1.lfArr[i1];
			for (int i2 = 0; i2 < lFGroup2.lfArr.length; i2++) {
				SIFT s2 = lFGroup2.lfArr[i2];
				double currDist = SIFT.getSquaredDistance(s1, s2);
				if (currDist < min)
					min = currDist;
			}
		}
		return min; // TO DO!
	}

	static final public double getLoweFactor_avg(AbstractLFGroup<SIFT> sg1,
			AbstractLFGroup<SIFT> sg2) {
		if (sg2.size() < 2)
			return 0;
		double sum = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			sum += SIFTGroup.getLoweFactor(arr[i], sg2);
		}

		return (double) sum / sg1.size();
	}

	static final public double getLoweFactor(SIFT s1, AbstractLFGroup<SIFT> sg) {
		double distsq1 = Integer.MAX_VALUE;
		double distsq2 = Integer.MAX_VALUE;
		double dsq = 0;
		SIFT curr, best = null;
		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getSquaredDistance(s1, curr);
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}
		if (distsq2 == 0)
			return 1;
		return Math.sqrt(distsq1 / distsq2);
	}

	static final public double getLowePercMatches(AbstractLFGroup<SIFT> sg1,
			AbstractLFGroup<SIFT> sg2, double conf) {
		return (double) getLoweNMatches(sg1, sg2, conf) / sg1.size();
	}

	static final public int getLoweNMatches(AbstractLFGroup<SIFT> sg1,
			AbstractLFGroup<SIFT> sg2, double conf) {
		if (sg2.size() < 2)
			return 0;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i = 0; i < arr.length; i++) {
			if (SIFTGroup.getLoweMatch(arr[i], sg2, conf) != null)
				nMatches++;
		}

		return nMatches;
	}

	// static final public LocalFeaturesMatches
	// getLoweMatches(AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
	// double dRatioThr) {
	// LocalFeaturesMatches matches = new LocalFeaturesMatches();
	// if ( sg2.size() < 2 ) return null;
	// int nMatches = 0;
	// SIFT[] arr = sg1.lfArr;
	// for (int i=0; i<arr.length; i++ ) {
	// SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr );
	// if ( match != null)
	// matches.add( new LocalFeatureMatch( arr[i], match ) );
	// }
	//
	// return matches;
	// }

	static final public LocalFeaturesMatches getLoweMatches(
			AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2) {
		return getLoweMatches(sg1, sg2, 0.8);
	}
	
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2, double dRatioThr) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}	
	
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2, double dRatioThr, final int maxLFDistSq) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr, maxLFDistSq );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}	
	
/*
	static final public LocalFeaturesMatches getLoweMatches(
			AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
			double dRatioThr, final int maxLFDistSq) {

		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if (sg2.size() < 2)	return null;
		int nMatches = 0;

		final AbstractLFGroup<SIFT> sg2Final = sg2;
		final double dRatioThrFinal = dRatioThr;
		final SIFT[] arr = sg1.lfArr;

		// For parallel
		final int size = arr.length;
		final int nObjPerThread = (int) Math.ceil(size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO < size; iO += nObjPerThread) {
			arrList.add(iO);
		}

		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer p) {
				int max = p + nObjPerThread;
				if (max > size)
					max = size;

				for (int i = p; i < max; i++) {
					SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2Final,
							dRatioThrFinal, maxLFDistSq);
					if (match != null)
						matches.add(new LocalFeatureMatch(arr[i], match));
				}

				return null;
			}
		});

		return matches;
	}
	
*/
	
	
/*
	static final public LocalFeaturesMatches getLoweMatches(
			AbstractLFGroup<SIFT> sg1, AbstractLFGroup<SIFT> sg2,
			double dRatioThr) {
		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if (sg2.size() < 2)
			return null;
		int nMatches = 0;

		final AbstractLFGroup<SIFT> sg2Final = sg2;
		final double dRatioThrFinal = dRatioThr;
		final SIFT[] arr = sg1.lfArr;

		// For parallel
		final int size = arr.length;
		final int nObjPerThread = (int) Math.ceil(size
				/ ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO < size; iO += nObjPerThread) {
			arrList.add(iO);
		}

		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer p) {
				int max = p + nObjPerThread;
				if (max > size)
					max = size;

				for (int i = p; i < max; i++) {
					SIFT match = SIFTGroup.getLoweMatch(arr[i], sg2Final,
							dRatioThrFinal);
					if (match != null)
						matches.add(new LocalFeatureMatch(arr[i], match));
				}

				return null;
			}
		});

		return matches;
	}
*/
	static final public SIFT getLoweMatch(SIFT s1, AbstractLFGroup<SIFT> sg,
			double conf, int maxFDsq) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		SIFT curr, best = null;
		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getSquaredDistance(s1, curr, distsq2);
			if (dsq < 0)
				continue;
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}

		// System.out.print(bestSIFT.scale + "\t");
		// if ( bestSIFT.scale > 4 ) return null;
		if (distsq1 > maxFDsq)
			return null;
		if (distsq2 == 0)
			return null;
		/*
		 * Check whether closest distance is less than ratio threshold of
		 * second.
		 */
		if ((double) distsq1 / (double) distsq2 < conf)
			return best;
		return null;
	}

	static final public SIFT getLoweMatch(SIFT s1, AbstractLFGroup<SIFT> sg,
			double conf) {
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;
		SIFT curr, best = null;

		SIFT[] arr = sg.lfArr;
		for (int i = 0; i < arr.length; i++) {
			curr = arr[i];
			dsq = SIFT.getSquaredDistance(s1, curr, distsq2);
			if (dsq < 0)
				continue;
			if (dsq < distsq1) {
				distsq2 = distsq1;
				distsq1 = dsq;
				best = curr;
			} else if (dsq < distsq2) {
				distsq2 = dsq;
			}
		}

		// System.out.print(bestSIFT.scale + "\t");
		// if ( bestSIFT.scale > 4 ) return null;
		if (distsq2 == 0)
			return null;
		/*
		 * Check whether closest distance is less than ratio threshold of
		 * second.
		 */
		if ((double) distsq1 / (double) distsq2 < conf)
			return best;
		return null;
	}

	public final void filterScale(double scale) {

		LinkedList<SIFT> okList = new LinkedList();
		for (int i = 0; i < lfArr.length; i++) {
			SIFT f = lfArr[i];
			if (f.getScale() > scale)
				okList.add(f);
		}
		System.out.println("Removing " + (lfArr.length - okList.size())
				/ (double) lfArr.length + " of points.");
		lfArr = new SIFT[okList.size()];
		int i = 0;
		for (Iterator<SIFT> it1 = okList.iterator(); it1.hasNext();) {
			lfArr[i++] = it1.next();
		}
	}

	@Override
	public Class getLocalFeatureClass() {
		return SIFT.class;
	}

	public float getMinSize() {
		float minSize = Float.MAX_VALUE;
		for (int i = 0; i < lfArr.length; i++) {
			float curr = lfArr[i].data[SIFT.scaleIndex];
			if (curr < minSize)
				minSize = curr;
		}
		return minSize;
	}

	
	public SIFTGroup getAboveSize(float minSize) {
		ArrayList<SIFT> newArr = new ArrayList<SIFT>(lfArr.length);
		for ( int i=0; i<lfArr.length; i++ ) {
			if ( lfArr[i].data[SIFT.scaleIndex] >= minSize )
				newArr.add(lfArr[i]);
		}
		SIFT[] nArr = new SIFT[newArr.size()];
		SIFTGroup res = new SIFTGroup(newArr.toArray(nArr), linkedFC);
		System.out.println("Was " + this.size() + " reduced to " + res.size() );
		return res;
	}

	@Override
	public AbstractLFGroup create(SIFT[] arr, IFeaturesCollector fc) {
		return new SIFTGroup( arr, fc);
	}
}
