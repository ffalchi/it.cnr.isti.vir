package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.similarity.LocalFeatureMatch;
import it.cnr.isti.vir.similarity.LocalFeaturesMatches;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class SURFGroup extends AbstractLFGroup<SURF> {
	
	public static final byte version = 2;	
	
	public SURFGroup(SURF[] arr, IFeaturesCollector fc) {
		super(arr, fc );
	}
	
	public SURFGroup(DataInput in) throws Exception {
		this(in, null);
	}
	
	public SURFGroup(DataInput in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.readByte();
		int size = in.readInt();
		
		lfArr = new SURF[size];
		if ( size == 0 ) return;
//		for (int i=0; i<size; i++ ) {
//			lfArr[i]= new SURF(in, this);
//		}
		
		int allSURFSize = SURF.dataSize * size;
		byte[] byteArray = new byte[4*allSURFSize];
		FloatBuffer inFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
		in.readFully(byteArray);
		for (int i=0; i<lfArr.length; i++ ) {
			float[] tempData = new float[SURF.dataSize];
			inFloatBuffer.get(tempData, 0, SURF.dataSize);
			lfArr[i] = new SURF(tempData, this);
		}
		
		
		if ( version > 0 ) {
			readEval(in);
		}
		
	}

	public void writeData(DataOutput out) throws IOException {
		out.writeByte(version);
		out.writeInt(lfArr.length);
		
		int allSURFSize = SURF.dataSize * lfArr.length;
		byte[] byteArray = new byte[allSURFSize*4];
		FloatBuffer outFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
		for (int i=0; i<lfArr.length; i++ ) {
			outFloatBuffer.put(lfArr[i].getData(), 0, SURF.dataSize);
		}
		out.write(byteArray, 0, byteArray.length);

//		//out.writeInt(FeaturesClassCollection.getClassID(list.getFirst().getClass()));
//		for (int i=0; i<lfArr.length; i++ ) {
//			lfArr[i].writeData(out);
//		}
		writeEval(out);
	}
	
	public SURFGroup(IFeaturesCollector fc) {
		super(fc);
	}
	
	public SURFGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
		super(fc);
		byte version = in.get();
		int size = in.getInt();
		lfArr = new SURF[size];
		if ( size == 0 ) return;
		
		for ( int i=0; i<size; i++) {
			lfArr[i] = new SURF(in, this);
		}
		
		if ( version > 0 ) {
			readEval(in);
		}
	}
	
	public SURFGroup(BufferedReader br, IFeaturesCollector fc) throws IOException {
		super(fc);
		String[] temp = br.readLine().split("(\\s)+");
		int len = Integer.parseInt(temp[0]);
		
		temp = br.readLine().split("(\\s)+");
		int n = Integer.parseInt(temp[0]);
		
		lfArr = new SURF[n];
	    for (int i = 0; i < n; i++) {
	    	lfArr[i] = new SURF(br, this);
	    }
	}

	static final public double getLowePercMatches(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2, double conf) {
		return (double) getLoweNMatches(sg1, sg2, conf)/sg1.size();
	}
	
	static final public int getLoweNMatches(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2, double conf) {
		if ( sg2.size() < 2 ) return 0;
		int nMatches = 0;
		SURF[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			if ( SURFGroup.getSURFMatch(arr[i], sg2, conf) != null ) nMatches++;
		}
		
		return nMatches;
	}
	
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2 ) {
		return getLoweMatches(sg1, sg2, 0.8 );
	}
	
//	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2, double dRatioThr) {
//		// Searching points of sg1 between the points of sg2
//		final LocalFeaturesMatches matches = new LocalFeaturesMatches();
//		if ( sg2.size() < 2 ) return null;
//		int nMatches = 0;
//		
//		final AbstractLFGroup<SURF> sg2Final = sg2;
//		final double dRatioThrFinal = dRatioThr;
//		final SURF[] arr = sg1.lfArr;
//		
//		
//		// For parallel
//		final int size = arr.length;
//		final int nObjPerThread = (int) Math.ceil( size / ParallelOptions.nThreads);
//		ArrayList<Integer> arrList = new ArrayList(size);
//		for (int iO = 0; iO<size; iO+=nObjPerThread) {
//			arrList.add(iO);
//		}
//		
//		Parallel.forEach(arrList, new Function<Integer, Void>() {
//			public Void apply(Integer p) {
//					int max = p+nObjPerThread;
//					if ( max > size )
//						max = size;
//					
//					for (int i=p; i<max; i++ ) {
//						SURF match = SURFGroup.getSURFMatch(arr[i], sg2Final, dRatioThrFinal );
//						if ( match != null)
//							matches.add( new LocalFeatureMatch( arr[i], match ) );
//					}
//					
//					return null;
//				}
//		});
//			
//		return matches;
//	}	
	
	static final public LocalFeaturesMatches getLoweMatches(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2, double dRatioThr) {
		LocalFeaturesMatches matches = new LocalFeaturesMatches();
		if ( sg2.size() < 2 ) return null;
		int nMatches = 0;
		SURF[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			SURF match = SURFGroup.getSURFMatch(arr[i], sg2, dRatioThr );
			if ( match != null)
				matches.add( new LocalFeatureMatch( arr[i], match ) );
		}
		
		return matches;
	}	
	
	static final public double getLoweFactor_avg(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2) {
		if ( sg2.size() < 2 ) return 0;
		double sum = 0;
		SURF[] arr = sg1.lfArr;
		for (int i=0; i<arr.length; i++ ) {
			sum += SURFGroup.getLoweFactor(arr[i], sg2);
		}
		
		return sum /arr.length;
	}

	static final public double getLoweFactor(SURF s1, AbstractLFGroup<SURF> sg) {		
		double distsq1 = Double.MAX_VALUE;
		double distsq2 = Double.MAX_VALUE;
		double dsq = 0;	
		SURF curr, best = null;
		SURF[] arr = sg.lfArr;		
		for (int i=0; i<arr.length; i++ ) {
			curr = arr[i];
			dsq = SURF.getSquaredDistance_laplace(s1, curr);
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
	
	static final public SURF getSURFMatch(SURF s1, AbstractLFGroup<SURF> sg, double thr) {		
		double distsq1 = Double.MAX_VALUE;
		double distsq2 = Double.MAX_VALUE;
		double dsq = 0;	
		SURF curr, best = null;
		SURF[] arr = sg.lfArr;		
		for (int i=0; i<arr.length; i++ ) {
			curr = arr[i];
			dsq = SURF.getSquaredDistance_laplace(s1, curr);
	        if (dsq < distsq1) {
	        	distsq2 = distsq1;
	        	distsq1 = dsq;
	        	best = curr;
	        } else if (dsq < distsq2) {
	        	distsq2 = dsq;
	        }
		}
		if ( distsq2 == 0 ) return null;
		
	    if ((double) distsq1 / (double) distsq2 < thr) return best;
	    return null;
	}
	
//	static final public boolean doesMatch(double distsq1, double distsq2 ) {
//		return distsq1 < 0.5 * distsq2;
//	}
	
	public static final double getMinDistance(SURFGroup lFGroup1, SURFGroup lFGroup2) {
		double min = Double.MAX_VALUE;
		for (int i1=0; i1<lFGroup1.lfArr.length; i1++ ) {
			SURF s1 = lFGroup1.lfArr[i1];
			for (int i2=0; i2<lFGroup2.lfArr.length; i2++ ) {
				SURF s2 = lFGroup2.lfArr[i2];
				double currDist = SURF.getSquaredDistance_laplace(s1, s2);
				if ( currDist < min ) min = currDist;
			}
		}
		return min; // TO DO!
	}

	public final void filterScale(double scale ) {
		// remove all features below scale
		LinkedList<SURF> okList = new LinkedList();
		for (int i=0; i<lfArr.length; i++ ) {
			SURF f = lfArr[i];
			if ( f.getScale()>scale ) okList.add(f);
		}
		System.out.println("Removing " + (lfArr.length-okList.size()) / (double) lfArr.length + " of points." );
		lfArr = new SURF[okList.size()];
		int i=0;
		for ( Iterator<SURF> it1 = okList.iterator(); it1.hasNext(); ) {
			lfArr[i++] = it1.next();
		}
	}
	
	static final public double getLowePercMatches_w(AbstractLFGroup<SURF> sg1, AbstractLFGroup<SURF> sg2, double sqrConf) {
		if ( sg2.size() < 2 ) return 0;
		double wmatches = 0;
		SURF[] arr = sg1.lfArr;
		double scaleSum = 0;
		for (int i=0; i<arr.length; i++ ) {
			scaleSum += arr[i].getScale();
			if ( SURFGroup.getSURFMatch(arr[i], sg2, sqrConf) != null ) wmatches+=arr[i].getScale();
		}
		
		return wmatches / scaleSum;
	}

	@Override
	public Class getLocalFeatureClass() {
		return SURF.class;
	}
	
	public float getMinSize() {
		float minSize = Float.MAX_VALUE;
		for (int i = 0; i < lfArr.length; i++) {
			float curr = lfArr[i].getScale();
			if (curr < minSize)
				minSize = curr;
		}
		return minSize;
	}

	
	public SURFGroup getAboveSize(float minSize) {
		ArrayList<SURF> newArr = new ArrayList<SURF>(lfArr.length);
		for ( int i=0; i<lfArr.length; i++ ) {
			if ( lfArr[i].getScale() >= minSize )
				newArr.add(lfArr[i]);
		}
		SURF[] nArr = new SURF[newArr.size()];
		//System.out.println("Was " + lfArr.length + " reduced to " + nArr.length);
		return new SURFGroup(newArr.toArray(nArr), linkedFC);
	}

	@Override
	public AbstractLFGroup create(SURF[] arr, IFeaturesCollector fc) {
		return new SURFGroup( arr, fc);
	}
}
