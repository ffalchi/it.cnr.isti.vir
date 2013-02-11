package it.cnr.isti.vir.features.localfeatures;

	import it.cnr.isti.vir.classification.AbstractLabel;
	import it.cnr.isti.vir.classification.ILabeled;
	import it.cnr.isti.vir.features.IFeature;
	import it.cnr.isti.vir.util.FloatByteArrayUtil;

	import java.awt.geom.Point2D;
	import java.io.BufferedReader;
	import java.io.DataInput;
	import java.io.DataOutput;
	import java.io.IOException;
	import java.nio.ByteBuffer;
	import java.util.Collection;
	import java.util.Iterator;

	public class RootSIFT implements ILocalFeature  {
	    
		public static long distances = 0;
		public static long hashSavedDistances = 0;
		
		private static double max = Double.MIN_VALUE;
		private static double min = Double.MAX_VALUE;
		
		private static final double maxSQRDistValue = 255 * 255 * 128;
		
		// the greater the scale, the more the importance
		
		/* 	from experiments:
		 *  SCALE: 	0.71 	and 	88 	were found
		 *  ORI: 	1.29	and			were found
		 *  ROW and COL are in pixels
		 */	
		
//		final float row, col;             	/* Subpixel location of keypoint. */
//		final float scale, ori;           	/* Scale and orientation (range [-PI,PI]) */

		static final int dataLen = 4;  
		static final int vLen = 128;
		static final int byteSize = vLen + dataLen * 8; 

		
		final float[] data;
		final static int colIndex 	= 0;
		final static int rowIndex 	= 1;
		final static int scaleIndex  = 2;
		final static int oriIndex 	= 3;
		
		final byte[] values;     			/* Vector of descriptor values (- 128 for storing in java byte) */

	    private float[] xy;
	    private float[] normxy;

	    private final static float sqrt2 = (float) Math.sqrt(2.0);
//		static final byte version = 0;
		
		private RootSIFTGroup linkedGroup;
		
		public byte[] getValues() {
			return values;
		}
		
		
		public RootSIFTGroup getLinkedGroup() {
			return linkedGroup;
		}
		
		public RootSIFT getUnlinked() {
			return new RootSIFT(values);
		}
		
		private RootSIFT(byte[] values) {
			this.values = values;
			data = new float[4];
			data[rowIndex]	 = Float.MIN_VALUE;
			data[colIndex]	 = Float.MIN_VALUE;
			data[scaleIndex] = Float.MIN_VALUE;
			data[oriIndex]	 = Float.MIN_VALUE;
			linkedGroup = null;
		}

		public RootSIFT(DataInput str ) throws IOException {
			this(str, null);
		}

		public RootSIFT(ByteBuffer src ) throws IOException {
			this(src, null);
		}
		
		public RootSIFT(ByteBuffer src, RootSIFTGroup group) throws IOException {
			
			linkedGroup = group;
			
			data = new float[dataLen];
			double temp = 0;
			for ( int i=0; i<dataLen; i++ ) {
				data[i] = src.getFloat();
				temp = src.getFloat();			
			}
					
			values = new byte[vLen];			
			for ( int iValues=0; iValues<vLen; iValues++ ) {
				values[iValues] = src.get();
			}

		}
		
		public RootSIFT(DataInput str, RootSIFTGroup group) throws IOException {
			
			linkedGroup = group;
			
			byte[] byteArr = new byte[byteSize];
			str.readFully(byteArr);
			data = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, 0, dataLen);
			
			values = new byte[vLen];			
			for ( int iValues=0; iValues<vLen; iValues++ ) {
				values[iValues]=byteArr[dataLen*8+iValues];
			}

		}
		
		private RootSIFT(float[] data, byte[] values, RootSIFTGroup group) {
			this.data = data;
			this.values = values;
			this.linkedGroup = group;
		}
		
		public static RootSIFT[] getRootSIFT(DataInput in, int nSIFT, RootSIFTGroup group) throws IOException {
			byte[] byteArr = new byte[nSIFT*byteSize];
			in.readFully(byteArr);
			return getRootSIFT(byteArr, group);
		}
		
		public static final RootSIFT[] getRootSIFT(byte[] byteArr, RootSIFTGroup group ){
			int size = byteArr.length / byteSize;
			RootSIFT[] res = new RootSIFT[size];
			for (int i=0, currIndex = 0; i<size; i++, currIndex += byteSize) {
				
				float[] tData = FloatByteArrayUtil.byteArrayToFloatArray(byteArr, currIndex, dataLen); 	// TO DO !!!
							
				byte[] tValues = new byte[vLen];			
				for ( int iValues=0; iValues<vLen; iValues++ ) {
					tValues[iValues]=byteArr[currIndex+dataLen*8+iValues];
				}
				
				res[i] = new RootSIFT(tData, tValues, group);
			}
			return res;
		}
			
		public final static byte[] getBytes(RootSIFT[] sift) {
			byte[] res = new byte[sift.length*byteSize];
			for (int i=0, currIndex = 0; i<sift.length; i++, currIndex += byteSize) {
				
				FloatByteArrayUtil.floatArrayToByteArray(sift[i].data, res, currIndex);
				
				for ( int iV=0; iV<vLen; iV++ ) {
					res[currIndex+dataLen*8+iV]=sift[i].values[iV];
				}
				
			}
			return res;
		}
		
		public final void writeData(DataOutput str) throws IOException {
//			str.writeByte(version);
//			
//			byte[] byteArray = new byte[data.length*4];
//			FloatBuffer outFloatBuffer = ByteBuffer.wrap(byteArray).asFloatBuffer();
//			outFloatBuffer.put(data, 0, data.length);
//			str.write(byteArray);
//			
//			str.writeInt(values.length);
//			
//			str.write(values);	
			
			byte[] res = new byte[byteSize];
			FloatByteArrayUtil.floatArrayToByteArray(data, res, 0);
			
			for ( int iV=0; iV<vLen; iV++ ) {
				res[dataLen*8+iV]=values[iV];
			}
			
			str.write(res);
			
		}
		
		public final Class getGroupClass() { return RootSIFTGroup.class; };
		
		/* .. each keypoint is
		   specified by 4 floating point numbers giving subpixel row and
		   column location, scale, and orientation (in radians from -PI to
		   PI).  Then the descriptor vector for each keypoint is given as a
		   list of integers in range [0,255].
		*/
		public RootSIFT(BufferedReader br, RootSIFTGroup group) throws IOException {
			linkedGroup = group;
			values = new byte[128];
			
			// First line parsing
		    String[] temp = br.readLine().split("(\\s)+");
		    
		    data = new float[4];
			data[0] = Float.parseFloat(temp[0]);
			data[1] = Float.parseFloat(temp[1]);
			data[2] = Float.parseFloat(temp[2]);
			data[3] = Float.parseFloat(temp[3]);	    

//			if ( scale > max ) { System.out.println("Scale max: " + scale); max = scale; }
//			if ( scale < min ) { System.out.println("Scale min: " + scale); min = scale; }
			
//			if ( row > max ) { System.out.println("row max: " + max); max = row; }
//			if ( row < min ) { System.out.println("row min: " + min); min = row; }
			
			//System.out.print (" " + scale);
			int count = 0;
			
			
			// ROOT SIFT
			
			// evaluating sum
			int sum = 0;
			
			// in case values are on the same line of row, col, scale, ori...
			for ( int i=4; i<temp.length; i++ ) {
				int value = Integer.parseInt(temp[i]);
				sum += value;
				values[count++] = (byte)( value  - 128 ); // UNSIGNED BYTES
			}
			
			// TO DO! Values must end with a endline 
		    while ( count<values.length ) {
		    	temp = br.readLine().split("\\s+");
				for (int i = 0; i < temp.length; i++) {
					//System.out.print(" " + temp[i]);
					if ( !temp[i].equals("") )  {
						int value = Integer.parseInt(temp[i]);
						sum += value;
						values[count++] = (byte) ( value - 128 ); // UNSIGNED BYTES
					}
				}
				//System.out.print("\n");
		    }
			
			if ( sum > 0 ) {
				// rooting sift
				for ( int i=0; i<values.length; i++ ) {
					int value = values[i] + 128;
					value = getRootSIFTValue(value, sum);
					values[i] = (byte)( value  - 128 );
				}
			}
				    

		}

		public final static byte[] getRootSIFTValues( byte[] siftValues ) {
			int sum = 0;
			for ( int i=0; i<siftValues.length; i++ ) {
				sum += siftValues[i];
			}
			sum += 128*128;
			
			byte[] values = new byte[128];
			for ( int i=0; i<siftValues.length; i++ ) {
				values[i] = (byte) (getRootSIFTValue( siftValues[i]+128, sum)-128);
			}
			
			return values;
			
		}
		
		public final static int getRootSIFTValue( int value, int sum ) {
			return (int)
					(
							Math.sqrt( (float) value / sum ) 	// between 0 and 1
							* 255						// to have between 0 and 255
					);
		}
		
		public RootSIFT(SIFT sift, RootSIFTGroup givenLinkedGroup) {
			linkedGroup = givenLinkedGroup;
			values = new byte[128];
		    
		    data = new float[4];
		    for ( int i=0; i<data.length; i++ ) 
		    	data[i] = sift.data[i];

		    for ( int i=0; i<values.length; i++ )
		    	values[i] = sift.values[i];
		}	


		public static final double getDistance_Norm(RootSIFT s1, RootSIFT s2 ) {
			return Math.sqrt(getSquaredDistance(s1,s2)/maxSQRDistValue);
		}
		
		public static final double getDistance_Norm(RootSIFT s1, RootSIFT s2, double maxDist ) {
			double distMax = maxDist*maxDist;
			return Math.sqrt(getSquaredDistance(s1,s2,(int) Math.ceil(distMax*maxSQRDistValue))/maxSQRDistValue);
		}
		
		public static final double getDistanceSqr_Norm(RootSIFT s1, RootSIFT s2 ) {
			return getSquaredDistance(s1,s2)/maxSQRDistValue;
		}
		
		public static final double getDistanceSqr_Norm(RootSIFT s1, RootSIFT s2, double maxDist ) {
			return getSquaredDistance(s1,s2,(int) Math.ceil(maxDist*maxSQRDistValue))/maxSQRDistValue;
		}
		
		public static final int getSquaredDistance(RootSIFT s1, RootSIFT s2) {
			int dist = 0;	    
			int dif = 0;
			
		    for (int i = 0; i < 128; i++) {
		    	dif = (int) s1.values[i] - (int) s2.values[i];
		    	dist += dif * dif;
		    }		
			return dist;
		}
		

		public static final int getSquaredDistance(RootSIFT s1, RootSIFT s2, int maxDist ) {
			int dist = 0;	    
			int dif = 0;
		    for (int i = 0; i < 128; i++) {
		    	dif = (int) s1.values[i] - (int) s2.values[i];
		    	dist += dif * dif;
		    	if ( dist > maxDist ) return -dist;
		    }		
			return dist;
		}
		
//		private static final int step=32;
//		public static final int getSquaredDistance(SIFT s1, SIFT s2, int maxDist ) {
//			int dist = 0;
//		    
//			int dif = 0;
//			int i=0;
//			while (i<127) {			
//				
//				for (int j=0; j<step; j++, i++) {
//					//dif = ((int) s1.values[i] & 0xFF) - ((int) s2.values[i] & 0xFF);
//					dif = (int) s1.values[i] - (int) s2.values[i];
//			    	dist += dif * dif;				
//				}
//				
//				// to check only step after step
//				if ( dist > maxDist ) return -dist;	
//			}
//			return dist;
//		}
		
		@Override
		public int compareTo(IFeature arg0) {
			// the bigger the scale, the greater the importance
			RootSIFT d1 = this;
			RootSIFT d2 = (RootSIFT) arg0;
			// this result in reverse order when sorting (from grater to smaller)
			return Double.compare(d2.data[scaleIndex], d1.data[scaleIndex]);

		}
	//	
//		@Override
//		public final IFeatureCollector getFeature(Class featureClass) {
////			if ( featureClass.equals(this.getClass()) ) return this;
////			return null;
//			return this;
//		}

		public boolean equals(Object obj) {
			if ( this == obj ) return true;
			RootSIFT givenF = (RootSIFT) obj;
			if ( values.length != givenF.values.length ) return false;
			for ( int i=0; i<values.length; i++ ) {
				if ( values[i] != givenF.values[i] ) return false;
			}
		
			// not used in distance
			for ( int i=0; i<data.length; i++) {
				if ( data[i] != givenF.data[i] ) return false;
			}
			return true;
		}


		@Override
		public AbstractLabel getLabel() {
			return ((ILabeled) linkedGroup).getLabel();
		}

		@Override
		public final float getScale() {
			return data[scaleIndex];
		}

	        @Override
	        public final float getNormScale() {
	            return linkedGroup.getNormScale() * getScale();
	        }

		@Override
		public final float getOrientation() {
			return data[oriIndex];
		}

//		@Override
//		public double getX() {
//			return row;
//		}
	//
//		@Override
//		public double getY() {
//			return col;
//		}

	        @Override
		public float[] getXY() {
			if (xy == null) {
				xy = new float[] { data[rowIndex], data[colIndex] };
			}
			return xy;
		}

	    @Override
		public synchronized float[] getNormXY() {
			if (normxy == null) {
				float[] tnormxy = new float[2];
				float[] mean = linkedGroup.getMeanXY();
				float scale = linkedGroup.getNormScale();
				tnormxy[0] = (data[rowIndex] - mean[0]) * scale;
				tnormxy[1] = (data[colIndex] - mean[1]) * scale;
				normxy = tnormxy;
			}
			return normxy;
		}

		public Point2D.Double getPoint2D() {
	                return new Point2D.Double( data[rowIndex], data[colIndex] );
		}


		public static RootSIFT getMean(Collection<RootSIFT> coll) {
			
			if ( coll.size() == 0 ) return null;
			
			long[] tempVec = new long[vLen];
			
			int laplaceTemp = 0;
			
			for ( Iterator<RootSIFT> it = coll.iterator(); it.hasNext(); ) {
				byte[] currVec = it.next().values;

				for ( int i=0; i<vLen; i++) {
					tempVec[i] += currVec[i];
				}
			}
			
			byte[] newVec = new byte[vLen];	
			for ( int i=0; i<vLen; i++) {
				newVec[i] = (byte) ( Math.round( tempVec[i] / (double) coll.size() ) );
			}
					
			return new RootSIFT( newVec );
			
		}

		public String toString() {
			String tStr = "";
			for (int i=0; i<values.length; i++ ) {
				tStr += " " + values[i];
			}
			tStr+="\n";
			return tStr;
		}

		public boolean hasZeroValues() {
			for ( int i=0; i<vLen; i++) {
				if ( values[i] != 0 ) return false;
			}
			return true;
		}
	
	}

