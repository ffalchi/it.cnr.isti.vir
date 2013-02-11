package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.FeaturesCollectorHT;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.evaluation.ILFEval;
import it.cnr.isti.vir.geom.Box;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.RandomOperations;

import java.awt.image.BufferedImage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public abstract class AbstractLFGroup<LF> implements IFeature, ILabeled, IHasID {
	
	protected LF[] lfArr;

	public IFeaturesCollector linkedFC;
	
//	private Point2D.Double maxXY;
//        private Point2D.Double minXY;
        private float[][] box;
        private float[] boxWidthHeight;
        private float[] bofIDF;
		private float[] meanXY;
        private float   avgDistFromMean;
        private static final float sqrt2 = (float) Math.sqrt(2.0);

        public float[] getBofIDF() {
			return bofIDF;
		}

		public void setBofIDF(float[] bofIDF) {
			this.bofIDF = bofIDF;
		}
        
        public synchronized float[][] getBox() {
            if (box == null) {
                initBox();
            }
            return box;
        }
        
        public void resetState() {
        	box = null;
        	boxWidthHeight = null;
        	bofIDF = null;
        	meanXY = null;
        	//avgDistFromMean
        }

        public float[] getMeanXY() {
            if ( meanXY == null ) initNorm();
            return meanXY;
        }

        public float getNormScale() {
            if ( meanXY == null ) initNorm();
            return sqrt2 / avgDistFromMean;
        }

        public int countInBox( float[][] box ) {
            int res = 0;
            ILocalFeature[] arr = (ILocalFeature[]) lfArr;
            for ( int i=0; i<lfArr.length; i++ ) {
                if ( Box.xyInBox(arr[i].getXY(), box )) {
                    res++;
                }
            }
            return res;
        }

    public synchronized void initNorm() {
        if ( meanXY != null ) return;
        float sumX = 0;
        float sumY = 0;
        ILocalFeature[] arr = (ILocalFeature[]) lfArr;
        for (int i = 0; i < lfArr.length; i++) {
            float[] currXY = arr[i].getXY() ;
            sumX += currXY[0];
            sumY += currXY[1];
        }
        float[] tmeanXY = new float[2];
        tmeanXY[0] = sumX / lfArr.length;
        tmeanXY[1] = sumY / lfArr.length;
        
        
        float sumDist = 0;
        for (int i = 0; i < lfArr.length; i++) {
            float[] currXY = arr[i].getXY() ;
            float diffX = currXY[0]-tmeanXY[0];
            float diffY = currXY[1]-tmeanXY[1];
            sumDist += Math.sqrt( diffX * diffX + diffY * diffY );
        }

        avgDistFromMean = sumDist / lfArr.length;
        meanXY = tmeanXY;
    }

	public void initBox() {

		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float maxX = 0.0f;
		float maxY = 0.0f;
		ILocalFeature[] arr = (ILocalFeature[]) lfArr;
		for (int i = 0; i < lfArr.length; i++) {
			float[] currXY = arr[i].getXY();

			if (currXY[0] < minX)
				minX = currXY[0];
			if (currXY[1] < minY)
				minY = currXY[1];
			if (currXY[0] > maxX)
				maxX = currXY[0];
			if (currXY[1] > maxY)
				maxY = currXY[1];
		}
		float[][] t = new float[2][2];
		t[0][0] = minX;
		t[0][1] = minY;
		t[1][0] = maxX;
		t[1][1] = maxY;
		
		box = t;
	}

//        public void initMaxMinXY() {
//		double minX = Double.MAX_VALUE;
//		double minY = Double.MAX_VALUE;
//                double maxX = 0.0;
//		double maxY = 0.0;
//		ILocalFeature[] arr = (ILocalFeature[]) lfArr;
//		for ( int i=0; i<lfArr.length; i++) {
//			Point2D.Double currXY = arr[i].getPoint2D() ;
//			if ( currXY[0] < minX ) minX = currXY[0];
//			if ( currXY[1] < minY ) minY = currXY[1];
//                        if ( currXY[0] > maxX ) maxX = currXY[0];
//			if ( currXY[1] > maxY ) maxY = currXY[1];
//		}
//                this.minXY = new Point2D.Double(minX, minY);
//                this.maxXY = new Point2D.Double(maxX, maxY);
//	}
	
//	public Point2D.Double getMaxXY() {
//		if ( maxXY == null ) initMaxMinXY();
//		return maxXY;
//	}
//
//        public Point2D.Double getMinXY() {
//		if ( minXY == null ) initMaxMinXY();
//		return minXY;
//	}

        public final double getBoxDiagonal() {
        	float[] wh = getBoxWidthHeight();
            return Math.sqrt( wh[0] * wh[0] + wh[1] * wh[1]);
        }

        public final float[] getBoxWidthHeight() {
            if ( boxWidthHeight == null ) {
                if ( box == null ) {
                    initBox();
                }
                boxWidthHeight = new float[2];
                boxWidthHeight[0] = Math.abs(box[1][0] - box[0][0]);
                boxWidthHeight[1] = Math.abs(box[1][1] - box[0][1]);
            }
            return boxWidthHeight;
        }

	public abstract Class getLocalFeatureClass();
	
	static final public byte version = 0;
	
	public abstract void writeData(DataOutput out) throws IOException;
	
	protected ILFEval[] eval = null;
	
	public final ILFEval[] getEval() {
		return eval;
	}
	
	public void setLinkedFC(IFeaturesCollector linkedFC) {
		this.linkedFC = linkedFC;
	}
	
	public IFeaturesCollector getLinkedFC() {
		return linkedFC;
	}
	
	public LF[] getLocalFeatures() {
		return lfArr;
	}
	
//	public LocalFeaturesGroup(LocalFeaturesGroup<LF> lfGroup, IFeatureCollector fc) {
//		this.linkedFC = fc;
//		this.lfArr = lfGroup.lfArr;
//		this.eval = lfGroup.eval;
//	}
	
	public final ILFEval getEval(Object obj) {
		for (int i=0; i<lfArr.length; i++) {
			if ( lfArr[i] == obj ) return eval[i];
		}
		return null;
	}

	public final void setEval(ILFEval[] eval) {
		this.eval = eval;
	}

	protected AbstractLFGroup(IFeaturesCollector fc) {
		this.linkedFC = fc;
//		coll = new ArrayList<LF>(1);
	}
	
	protected AbstractLFGroup(LF[] arr, IFeaturesCollector fc) {
		lfArr = arr;
		this.linkedFC = fc;
	}
	
	public AbstractLFGroup(int initCapacity, IFeaturesCollector fc) {
		lfArr = (LF[]) new ILocalFeature[initCapacity];
		this.linkedFC = fc;
	}

	public Collection getCollection() {
		return Arrays.asList(lfArr);
	}
	
//	final void add(LF localFeature) {
//		coll.add(localFeature);
//	}
	
//	public final Iterator<LF> iterator() {
//		return coll.iterator();
//	}
	
	public final int size() {
		return lfArr.length;
	}
	
	public final LF getFeature(int index) {
		return lfArr[index];
	}
	
	public final void reduceToNMax(int max) {
		if ( lfArr.length < max ) return;
		
		// sorts the local features
		Arrays.sort(lfArr);
		
		// we search for complete lf per scale
		LF last = lfArr[0];
		int n = 0;
		for ( int i=1; i<max; i++ ) {
			LF curr = lfArr[i];
			//if ( ! last.equals(curr) ) {
			if ( ((Comparable) last).compareTo(curr) != 0 ) {
				// if last and curr are not equals for ordering n is updated
				n = i;
			}
			last = curr;
		}
		//n represents the max number of features (n<max) with complete lf per scale
		
		LF[] old = lfArr;
		lfArr = (LF[]) Array.newInstance( old[0].getClass(), n );
		for (int i=0; i<n; i++) {
			lfArr[i] = old[i];
		}
		
	}
	
	public final void removeAll() {
		lfArr = null;
	}
	
	public final void removeEvalBEQ(double thr) {
		LinkedList<LF> list = new LinkedList<LF>();
		for (int i=0; i<lfArr.length; i++ ) {
			if ( eval[i].getValue()>=thr) list.add(lfArr[i]);
		}
		lfArr = (LF[]) list.toArray();
		eval = null;
	}
	

	
	/*
	public static Class getGroupClass(Class c ) {
		if ( c.equals(SIFT.class ) ) return SIFTGroup.class;
		if ( c.equals(ColorSIFT.class ) ) return ColorSIFTGroup.class;
		if ( c.equals(SURF.class ) ) return SURFGroup.class;
		if ( c.equals(RootSIFT.class ) ) return RootSIFTGroup.class;
		return null;
	}*/

	
	public int compareTo(IFeaturesCollector obj) {
		return hashCode()-((FeaturesCollectorHT)obj).hashCode();
	}
	
	public boolean equals(Object obj) {
		if ( this == obj ) return true;
		AbstractLFGroup<LF> givenF = (AbstractLFGroup<LF>) obj;
		if ( this.lfArr.length != givenF.lfArr.length ) return false;
		
		for (int i=0; i<lfArr.length; i++) {
			if ( ! this.lfArr[i].equals( givenF.lfArr[i] ) ) return false;
		}
		return true;
	}
	
	
	protected final void writeEval(DataOutput out) throws IOException {
		out.writeBoolean(false);
		/*
		if ( eval == null ) {
			out.writeBoolean(false);
		} else {
			out.writeBoolean(true);
			LFEvaluationCollection.writeData(out, eval);			
		}*/
	}

	protected final void readEval(DataInput in) throws Exception {
		boolean evalPresent = in.readBoolean();
		/*
		if (evalPresent) {
			eval = LFEvaluationCollection.readDataArr(in);
		}*/
	}
	
	protected final void readEval(ByteBuffer in) throws Exception {
		boolean evalPresent = in.get() != 0;
		/*
		if (evalPresent) {
			eval = LFEvaluationCollection.readDataArr(in);
		}*/
	}
	
	@Override
	public final AbstractLabel getLabel() {
		return ((ILabeled) linkedFC).getLabel();
	}
	
	public final AbstractID getID() {
		return ((IHasID) linkedFC).getID();
	}
	
	@Override
	public int compareTo(IHasID arg0) {
		return getID().compareTo( arg0.getID() );
	}

        public final static void overPrint_point( BufferedImage img, float[] xy, int color ) {
		int x = (int) Math.round(xy[0]);
		int y = (int) Math.round(xy[1]);
		if ( x == -1 ) x = 0;
		if ( y == -1 ) y = 0;

		int ty = y;
		for ( int ix = -5; ix<=5; ix++ ) {
			int tx = x + ix;
			if ( 	tx >= 0 && tx < img.getWidth() &&
					ty >= 0 && ty < img.getHeight()	) {
				img.setRGB(tx, ty, color);
			}
		}
		int tx = x;
		for ( int iy = -5; iy<=5; iy++ ) {
			ty = y + iy;
			if ( 	tx >= 0 && tx < img.getWidth() &&
					ty >= 0 && ty < img.getHeight()	) {
				img.setRGB(tx, ty, color);
			}
		}


	}

        public BufferedImage overPrint( BufferedImage img, int color ) {
		double sum = 0;
                ILocalFeature[] arr = (ILocalFeature[]) lfArr;
		for ( int i=0; i<arr.length; i++ ) {
			ILocalFeature curr = arr[i];
			float[] xy = null;
			xy = curr.getXY();
			overPrint_point(img, xy, color);
		}
		return img;
	}

    abstract public float getMinSize();
        
    public AbstractLFGroup getReducedRandom(float redFactor) {
    	if ( lfArr == null || lfArr.length == 0 )
    		return create ( lfArr, linkedFC);
		ArrayList tRes = new ArrayList<LF>(lfArr.length);
		for (int i = 0; i < lfArr.length; i++) {
			if ( RandomOperations.trueORfalse(redFactor) ) {
				tRes.add(lfArr[i]);
			}
		}
		LF[] newArr = (LF[]) Array.newInstance(lfArr[0].getClass(),tRes.size());
		tRes.toArray(newArr);
		return create(newArr, linkedFC );
	}
    
    public abstract AbstractLFGroup create(LF[] arr, IFeaturesCollector fc);
    
	public AbstractLFGroup getReducedMinSizeByFactor(float lsMinSizeFactor) {
		float minSize = getMinSize();
		return this.getAboveSize( minSize * lsMinSizeFactor);
	}
	
	public abstract  AbstractLFGroup getAboveSize(float minSize);


	public ArrayList getLF( float xMin, float yMin, float xMax, float yMax ) {
		ArrayList<LF> list = new ArrayList(lfArr.length);
		for (int i=0; i<lfArr.length; i++) {
			ILocalFeature curr = (ILocalFeature) lfArr[i];
			float[] xy = curr.getXY();
			if ( 	xy[0] >= xMin && xy[0] <= xMax &&
					xy[1] >= yMin && xy[1] <= yMax ) {
				list.add((LF) curr);
			}
		}
		
		return list;
	}

	
}
