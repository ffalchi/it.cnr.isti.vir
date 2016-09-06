package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.distance.L2;
import it.cnr.isti.vir.features.AbstractFeature;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.FeatureClassCollector;
import it.cnr.isti.vir.features.Floats;
import it.cnr.isti.vir.features.mpeg7.vd.ColorLayout;
import it.cnr.isti.vir.features.mpeg7.vd.ColorStructure;
import it.cnr.isti.vir.features.mpeg7.vd.DominantColor;
import it.cnr.isti.vir.features.mpeg7.vd.EdgeHistogram;
import it.cnr.isti.vir.features.mpeg7.vd.HomogeneousTexture;
import it.cnr.isti.vir.features.mpeg7.vd.RegionShape;
import it.cnr.isti.vir.features.mpeg7.vd.ScalableColor;
import it.cnr.isti.vir.similarity.metric.IMetric;

import java.util.Arrays;

public class KHIWappen implements IMetric<AbstractFeaturesCollector> {
	
	public FeatureClassCollector reqFeatures = new FeatureClassCollector(
			/* 0 */ ColorLayout.class,			
			/* 1 */ ColorStructure.class, 		// Whole
			/* 2 */ DominantColor.class,
			/* 3 */ ScalableColor.class,
			/* 4 */ EdgeHistogram.class,
			/* 5 */ HomogeneousTexture.class,
			/* 6 */ RegionShape.class, // Regions
			/* 7 */ Floats.class  // Whole
			);

	private Class[] c = {
			/* 0 */ ColorLayout.class,			
			/* 1 */ ColorStructure.class, 		// Whole
			/* 2 */ DominantColor.class,
			/* 3 */ ScalableColor.class,
			/* 4 */ EdgeHistogram.class,
			/* 5 */ HomogeneousTexture.class,
			/* 6 */ RegionShape.class, 			// Regions
			/* 7 */ Floats.class  // Whole		
	};
	
	private boolean[] descriptorMask = new boolean[c.length];
	
	public KHIWappen( Class<? extends AbstractFeature> ... featureClasses ) {		
		for ( Class<? extends AbstractFeature> f : featureClasses ) {
			for ( int i=0; i<c.length; i++) {
				if ( f.equals(c[i])) descriptorMask[i]=true;
			}
//			if ( f.equals(ColorLayout.class) ) 				descriptorMask[0] = true;
//			else if ( f.equals(ColorStructure.class) ) 	descriptorMask[1] = true;
//			else if ( f.equals(DominantColor.class) )  	descriptorMask[2] = true;
//			else if ( f.equals(ScalableColor.class) )  	descriptorMask[3] = true;
//			else if ( f.equals(EdgeHistogram.class) )  	descriptorMask[4] = true;
//			else if ( f.equals(HomogeneousTexture.class) ) descriptorMask[5] = true;
//			else if ( f.equals(RegionShape.class) )        descriptorMask[6] = true;
		}
	}
	
	public void resetClasses( Class<? extends AbstractFeature> ... featureClasses ) {		
		Arrays.fill(descriptorMask, false);
		for ( Class<? extends AbstractFeature> f : featureClasses ) {
			for ( int i=0; i<c.length; i++) {
				if ( f.equals(c[i])) descriptorMask[i]=true;
			}
//			if ( f.equals(ColorLayout.class) ) 				descriptorMask[0] = true;
//			else if ( f.equals(ColorStructure.class) ) 	descriptorMask[1] = true;
//			else if ( f.equals(DominantColor.class) )  	descriptorMask[2] = true;
//			else if ( f.equals(ScalableColor.class) )  	descriptorMask[3] = true;
//			else if ( f.equals(EdgeHistogram.class) )  	descriptorMask[4] = true;
//			else if ( f.equals(HomogeneousTexture.class) ) descriptorMask[5] = true;
//			else if ( f.equals(RegionShape.class) )        descriptorMask[6] = true;
		}
	}

	public String getClasses() {
		StringBuilder tStr = new StringBuilder();
		
		for ( int i=0; i<c.length; i++) {
			if ( descriptorMask[i]==true ) tStr.append(c[i].getSimpleName() + " ");
		}
		
		return tStr.toString();
	}
	
	public static final double wNorm = 1.0 / ( 1.5+2.5+4.5+0.5+2.5 ); 

	public static final double[] w = {
		wNorm * 1.5  * 1.0/300.0,  	//CL
		wNorm * 2.5  * 1.0/10200.0, //CS
		wNorm * 1.0  * 1.0/20.0,	//DC
		wNorm * 2.5  * 1.0/3000.0, 	//SC
		wNorm * 4.5  * 1.0/68.0, 	//EH
		wNorm * 0.5  * 1.0/25.0,  	//HT
		wNorm * 1.0,  	//RS
		wNorm * 1.0   	//Floats
	};
	
	@Override
	public FeatureClassCollector getRequestedFeaturesClasses() {		
		return reqFeatures;
	}
	
	
/*	public final double distance(Image img1, Image img2) {
		return distance(img1.getFeatures(), img2.getFeatures());
	}*/
	
	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2 ) {
	
		return distance(f1,f2, Double.MAX_VALUE);
	}

	

	public final double distance(AbstractFeaturesCollector f1, AbstractFeaturesCollector f2, double max ) {
		double dist = 0;

		//CL
		int i=0;
		if ( descriptorMask[i] == true) {
			dist += w[i] * DominantColor.mpeg7XMDistance( f1.getFeature(DominantColor.class), f2.getFeature(DominantColor.class) );
			if ( dist > max ) return -dist;
		}
		
		//CS
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * ColorStructure.mpeg7XMDistance( f1.getFeature(ColorStructure.class), f2.getFeature(ColorStructure.class) );
			if ( dist > max ) return -dist;
		}
		
		//DC
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * ColorLayout.mpeg7XMDistance( f1.getFeature(ColorLayout.class), f2.getFeature(ColorLayout.class) );
			if ( dist > max ) return -dist;	
		
		}

		
		//SC
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * ScalableColor.mpeg7XMDistance( f1.getFeature(ScalableColor.class), f2.getFeature(ScalableColor.class) );
			if ( dist > max ) return -dist;
		}
						
		//EH
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * EdgeHistogram.mpeg7XMDistance( f1.getFeature(EdgeHistogram.class), f2.getFeature(EdgeHistogram.class) );
			if ( dist > max ) return -dist;
		}

		//HT
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * HomogeneousTexture.mpeg7XMDistance_NOption( (f1.getFeature(HomogeneousTexture.class)).preComputed, ((HomogeneousTexture) f2.getFeature(HomogeneousTexture.class)).preComputed );
			if ( dist > max ) return -dist;
		}
		
		//RS
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * RegionShape.mpeg7XMDistance( f1.getFeature(RegionShape.class), f2.getFeature(RegionShape.class) );
		}
		
		//Floats
		i++;
		if ( descriptorMask[i] == true) {
			dist += w[i] * L2.get( f1.getFeature(Floats.class), f2.getFeature(Floats.class) );
		}
			
//		double tDist = SAPIRFeature.mpeg7XMDistance(new SAPIRFeature((SAPIRObject) f1), new SAPIRFeature((SAPIRObject) f2));
		
//		if ( Math.abs(tDist-dist)/dist > 0.001 ) System.err.println("SON CAZZI! " + tDist + " " + dist);
		
		return dist;
	}
	
	public String toString() {
		return this.getClass().toString();
	}
	
//	@Override
//	public SAPIRObject getMean(Collection<SAPIRObject> coll) {
//		int size = coll.size();
//		
//		if ( coll.size() == 0 ) return null;
//		
//		// ColorLayout
//		ColorLayout firstCL = coll.iterator().next().cl;
//		int yACCoeff_n = firstCL.getYACCoeff_n();
//		int cbACCoeff_n = firstCL.getCBACCoeff_n();
//		int crACCoeff_n  = firstCL.getCRACCoeff_n();
//		
//		byte[][] clVArr = new byte[size][];
//		byte[][] csVArr = new byte[size][];
//		short[][] scVArr = new short[size][];
//		float[][] ehVArr = new float[size][];
//		float[][] htVArr = new float[size][];
//		int i=0;
//		for ( Iterator<SAPIRObject> it = coll.iterator(); it.hasNext(); ) {
//			SAPIRObject curr = it.next();
//			clVArr[i] = curr.cl.getByteArray();
//			csVArr[i] = curr.cs.values;
//			scVArr[i] = curr.sc.coeff;
//			ehVArr[i] = curr.eh.totHistogram;
//			htVArr[i] = curr.ht.preComputed;
//			i++;
//		}
//		ColorLayout clMean = new ColorLayout(Mean.getMean(clVArr), yACCoeff_n, cbACCoeff_n, crACCoeff_n);
//		ScalableColor scMean = new ScalableColor(Mean.getMean(scVArr));		
//		EdgeHistogram ehMean = new EdgeHistogram(Mean.getMean(ehVArr));		
//		ColorStructure csMean = new ColorStructure(Mean.getMean(csVArr));		
//		HomogeneousTexture htMean = new HomogeneousTexture(Mean.getMean(htVArr));	
//		
//		return new SAPIRObject(clMean, csMean, scMean, ehMean, htMean, null);
//	}
	
	public String getStatsString() { return ""; }


	@Override
	public long getDistCount() {
		// TODO Auto-generated method stub
		return 0;
	};
	
}