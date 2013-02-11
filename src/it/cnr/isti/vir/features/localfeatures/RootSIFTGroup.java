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

public class RootSIFTGroup extends AbstractLFGroup<RootSIFT>  {

	public RootSIFTGroup(RootSIFT[] arr, IFeaturesCollector fc) {
		super(arr, fc);
	}
	
	public RootSIFTGroup(IFeaturesCollector fc) {
		super(fc);
	}
	
	public RootSIFTGroup(DataInput in) throws Exception {
		this(in, null);
	}
	
	public static final byte version = 1;	
	
	public RootSIFTGroup(SIFTGroup siftGroup, IFeaturesCollector fc  ) {
		super(null);
		int size = siftGroup.lfArr.length;
		SIFT[] siftArr = siftGroup.lfArr;
		lfArr = new RootSIFT[siftArr.length];
		for ( int i=0; i<size; i++) {
			lfArr[i] = new RootSIFT(siftArr[i], this);
		}
	}
	
	public RootSIFTGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.get();
		int size = in.getInt();
		lfArr = new RootSIFT[size];
		if ( size == 0 ) return;
		
		for ( int i=0; i<size; i++) {
			lfArr[i] = new RootSIFT(in, this);
		}
		
		if ( version > 0 ) {
			readEval(in);
		}
	}
	
	public RootSIFTGroup(DataInput in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.readByte();
		int size = in.readInt();
		//lfArr = new RootSIFT[size];
		//if ( size == 0 ) return;
		
		lfArr = RootSIFT.getRootSIFT(in, size, this);
		
//		for (int i=0; i<size; i++ ) {
//			lfArr[i] = new RootSIFT(in, this);
//		}
		if ( version > 0 ) {
			readEval(in);
		}
	}
	

	public boolean checkByteFormatConsistency() {
		byte[] bytes = RootSIFT.getBytes(lfArr);
		RootSIFT[] rootSIFT  = RootSIFT.getRootSIFT(bytes, this);
		for ( int i=0; i<lfArr.length; i++ ) {
			if ( !lfArr[i].equals(rootSIFT[i])) {
				return false;
			}
		}
		return true;
	}
	
	public final void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeInt(lfArr.length);
		
//		if ( !checkByteFormatConsistency() ) {
//			System.err.println("MO' SON CAZZI!!!");
//		}
		
		//out.writeInt(FeaturesClassCollection.getClassID(list.getFirst().getClass()));
		byte[] bytes = RootSIFT.getBytes(lfArr);
		out.write(bytes);
//		for (int i=0; i<lfArr.length; i++ ) {
//			lfArr[i].writeData(out);
//		}
		writeEval(out);
	}
	
	/* Read keypoints from the given file pointer and return the list of
	   keypoints.  The file format starts with 2 integers giving the total
	   number of keypoints and the size of descriptor vector for each
	   keypoint (currently assumed to be 128). Then each keypoint is
	   specified by ...
	*/
	public RootSIFTGroup(BufferedReader br, IFeaturesCollector fc) throws IOException {
		super(fc);
		String[] temp = br.readLine().split("(\\s)+");
		//assert(temp.length == 2);
		int n = Integer.parseInt(temp[0]);
		int len = Integer.parseInt(temp[1]);
		//assert(len == RootSIFT.vLen);
				
		this.lfArr = new RootSIFT[n];
	    for (int i = 0; i < n; i++) {
	    	this.lfArr[i] = new RootSIFT(br, this);
	    }
	}

//	static final public LocalFeaturesGroup<RootSIFT> readKeys(BufferedReader br) throws IOException
//	{
//		return new RootSIFTGroup(br);
//	}
	
	
	
	
	public static final double getMinDistance(RootSIFTGroup lFGroup1, RootSIFTGroup lFGroup2) {
		double min = Double.MAX_VALUE;
		for (int i1=0; i1<lFGroup1.lfArr.length; i1++ ) {
			RootSIFT s1 = lFGroup1.lfArr[i1];
			for (int i2=0; i2<lFGroup2.lfArr.length; i2++ ) {
				RootSIFT s2 = lFGroup2.lfArr[i2];
				double currDist = RootSIFT.getSquaredDistance(s1, s2);
				if ( currDist < min ) min = currDist;
			}
		}
		return min; // TO DO!
	}

	
	static final public double getLoweFactor_avg(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2) {
		if ( sg2.size() < 2 ) return 0;
		double sum = 0;
		RootSIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			sum += RootSIFTGroup.getLoweFactor(arr[i], sg2);
		}
		
		return (double) sum/sg1.size();
	}

	static final public double getLoweFactor(RootSIFT s1, AbstractLFGroup<RootSIFT> sg) {		
		double distsq1 = Integer.MAX_VALUE;
		double distsq2 = Integer.MAX_VALUE;
		double dsq = 0;	
		RootSIFT curr, best = null;
		RootSIFT[] arr = sg.lfArr;		
		for (int i=0; i<arr.length; i++ ) {
			curr = arr[i];
			dsq = RootSIFT.getSquaredDistance(s1, curr);
	        if (dsq < distsq1) {
	        	distsq2 = distsq1;
	        	distsq1 = dsq;
	        	best = curr;
	        } else if (dsq < distsq2) {
	        	distsq2 = dsq;
	        }
		}
		if ( distsq2 == 0 ) return 1;
	    return Math.sqrt(distsq1/distsq2);	
	}
	
	static final public double getLowePercMatches(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2, double conf) {
		return (double) getLoweNMatches(sg1, sg2, conf) / sg1.size();
	}
	
	static final public int getLoweNMatches(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2, double conf) {
		if ( sg2.size() < 2 ) return 0;
		int nMatches = 0;
		RootSIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			if ( RootSIFTGroup.getLoweMatch(arr[i], sg2, conf) != null ) nMatches++;
		}
		
		return nMatches;
	}
	

	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2 ) {
		return getLoweMatches(sg1, sg2, 0.8 );
	}
	
	
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2, double dRatioThr) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		RootSIFT[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			RootSIFT match = RootSIFTGroup.getLoweMatch(arr[i], sg2, dRatioThr );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}
	/*
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<RootSIFT> sg1, AbstractLFGroup<RootSIFT> sg2, double dRatioThr) {
		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		
		final AbstractLFGroup<RootSIFT> sg2Final = sg2;
		final double dRatioThrFinal = dRatioThr;
		final RootSIFT[] arr = sg1.lfArr;
		
		// For parallel
		final int size = arr.length;
		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
		ArrayList<Integer> arrList = new ArrayList(size);
		for (int iO = 0; iO<size; iO+=nObjPerThread) {
			arrList.add(iO);
		}
		
		Parallel.forEach(arrList, new Function<Integer, Void>() {
			public Void apply(Integer p) {
					int max = p+nObjPerThread;
					if ( max > size )
						max = size;
					
					for (int i=p; i<max; i++ ) {
						RootSIFT match = RootSIFTGroup.getLoweMatch(arr[i], sg2Final, dRatioThrFinal );
						if ( match != null)
							matches.add( new LocalFeatureMatch( arr[i], match ) );
					}
					
					return null;
				}
		});
			
		return matches;
	}	
	*/
	static final public RootSIFT getLoweMatch(RootSIFT s1, AbstractLFGroup<RootSIFT> sg, double conf, int maxFDsq) {		
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;	
		RootSIFT curr, best = null;
		RootSIFT[] arr = sg.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			curr = arr[i];
			dsq = RootSIFT.getSquaredDistance(s1, curr, distsq2);
			if ( dsq < 0 ) continue;
	        if (dsq < distsq1) {
	        	distsq2 = distsq1;
	        	distsq1 = dsq;
	        	best = curr;
	        } else if (dsq < distsq2) {
	        	distsq2 = dsq;
	        }
		}
		
		//System.out.print(bestRootSIFT.scale + "\t");
		//if ( bestRootSIFT.scale > 4 ) return null;
		if ( distsq1 > maxFDsq ) return null;
		if ( distsq2 == 0 ) return null;
		/* Check whether closest distance is less than ratio threshold of second. */
		if ((double) distsq1 / (double) distsq2 < conf) return best;
	    return null;
	}
	
	public static final  RootSIFT getLoweMatch(RootSIFT s1, AbstractLFGroup<RootSIFT> sg, double conf) {		
		int distsq1 = Integer.MAX_VALUE;
		int distsq2 = Integer.MAX_VALUE;
		int dsq = 0;	
		RootSIFT curr, best = null;
		
		RootSIFT[] arr = sg.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			curr = arr[i];
			dsq = RootSIFT.getSquaredDistance(s1, curr, distsq2);
			if ( dsq < 0 ) continue;
	        if (dsq < distsq1) {
	        	distsq2 = distsq1;
	        	distsq1 = dsq;
	        	best = curr;
	        } else if (dsq < distsq2) {
	        	distsq2 = dsq;
	        }
		}
		
		//System.out.print(bestRootSIFT.scale + "\t");
		//if ( bestRootSIFT.scale > 4 ) return null;
		if ( distsq2 == 0 ) return null;
		/* Check whether closest distance is less than ratio threshold of second. */
		if ((double) distsq1 / (double) distsq2 < conf) return best;
	    return null;
	}
	
	public final void filterScale(double scale ) {
		
		LinkedList<RootSIFT> okList = new LinkedList();
		for (int i=0; i<lfArr.length; i++ ) {
			RootSIFT f = lfArr[i];
			if ( f.getScale()>scale ) okList.add(f);
		}
		System.out.println("Removing " + (lfArr.length-okList.size()) / (double) lfArr.length + " of points." );
		lfArr = new RootSIFT[okList.size()];
		int i=0;
		for ( Iterator<RootSIFT> it1 = okList.iterator(); it1.hasNext(); ) {
			lfArr[i++] = it1.next();
		}
	}

	@Override
	public Class getLocalFeatureClass() {
		return RootSIFT.class;
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

	
	public RootSIFTGroup getAboveSize(float minSize) {
		ArrayList<RootSIFT> newArr = new ArrayList<RootSIFT>(lfArr.length);
		for ( int i=0; i<lfArr.length; i++ ) {
			if ( lfArr[i].data[RootSIFT.scaleIndex] >= minSize )
				newArr.add(lfArr[i]);
		}
		RootSIFT[] nArr = new RootSIFT[newArr.size()];
		//System.out.println("Was " + lfArr.length + " reduced to " + nArr.length);
		return new RootSIFTGroup(newArr.toArray(nArr), linkedFC);
	}

	@Override
	public AbstractLFGroup create(RootSIFT[] arr, IFeaturesCollector fc) {
		return new RootSIFTGroup( arr, fc);
	}
}
