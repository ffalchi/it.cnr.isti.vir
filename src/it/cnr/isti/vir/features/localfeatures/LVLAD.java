package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.pca.PrincipalComponents;
import it.cnr.isti.vir.util.ArrayValuesConversion;
import it.cnr.isti.vir.util.math.Normalize;
import it.cnr.isti.vir.util.math.VectorMath;

public class LVLAD {

	public static final  FloatsLFGroup getLVLAD(
			ALocalFeature[] lfArr,
			LFWords words,
			Double ssr,
			boolean intraNorm,
			boolean rn,
			PrincipalComponents pc
			) throws Exception {
		
		final int nLF = lfArr.length;
		final int nWords = words.size();
		final ALocalFeature[] refs = (ALocalFeature[]) words.getFeatures();
		final int d = ((IArrayValues) refs[0]).getLength();
		final int dLVLAD = d*nWords;
		if ( nLF == 0 ) {
			// TODO !
		}
		
		int[] nearestWord = new int[nLF];
		double[][] residuals = new double[nLF][];
				
		//ArrayList<FloatsLF> res = new ArrayList<FloatsLF>(nLF);
		FloatsLF[] res = new FloatsLF[nLF];
		
		// Evaluating residuals
		for ( int i=0; i<nLF; i++ ) {
			ALocalFeature lf = lfArr[i];
			nearestWord[i] = words.getNNIndex(lf);
			
			if ( refs[0] instanceof IArrayValues ) {
				residuals[i] = VectorMath.diff(
						(IArrayValues) lf,
						(IArrayValues) refs[nearestWord[i]]);
				
				if ( rn ) {
					Normalize.l2(residuals[i]);
				}				
				

			} else {
	        	throw new Exception( "LVLAD can't be computed for " + refs[0].getClass() );
			}
		}
		
//		TIntHashSet hset = new TIntHashSet();
		
		// Computing LocalVLAD
		for ( int iLF=0; iLF<nLF; iLF++ ) {
			ALocalFeature lf = lfArr[iLF];
			KeyPoint kp = lf.kp;
			
//			if ( hset.add(kp.getXYScaleHashCode()) )
//				continue;
			
			float scale = kp.getScale();					
			double range = 0.7*scale;
			double xyEucMaxD_sq = range*range;
			
			double[] lvlad = new double[dLVLAD];
			
			
			
			// Residuals sum
			int count=0;
			for ( int j=0; j<nLF; j++ ) {
				KeyPoint currKP = lfArr[j].kp;
				
//				double scaleRatio = currKP.getScale() / scale;
//				if ( scaleRatio > 1.0 ) continue;
				
				double sqDist = KeyPoint.distance_sq(kp, currKP);
				if ( sqDist <= xyEucMaxD_sq ) {
				
					//double weight = ( 1.0 - Math.sqrt(sqDist) / range );
					//double weight = ( 1.0 - Math.sqrt(sqDist) / range );
					//double weight = 1.0 - sqDist / xyEucMaxD_sq;
					
					for ( int k=nearestWord[j]*d, l=0; l<d; k++, l++) {
						lvlad[k] += residuals[j][l];
					}
				}
				count++;
			}

			// extra weight to current LF
//			double currLFWeight = count / 2.0; // did not work (0.487 mAP Oxford)
//			for ( int k=nearestWord[iLF]*d, l=0; l<d; k++, l++) {
//				lvlad[k] += currLFWeight*residuals[iLF][l];
//			}
			
			//if ( count < 8 ) continue;
			
			if ( intraNorm) {
	        	
	        	/* INTRANORM 
	        	 * See "All about VLAD" paper
	        	 */
	        	for ( int i=0; i<refs.length; i++ ) {
	        		int start = d * i;
	        		int end = start + d;
	        		Normalize.l2(lvlad, start, end);         		
	        	}
	        	
	        	
	        }
	        
	        if ( ssr == null ) {
	           	/* SSR */
	           	// SSR has been proposed in 2012
	           	// "Negative evidences and co-occurrences in image retrieval: the benefit of PCA and whitening"
	    		Normalize.ssr(lvlad);
	    	} else {

	    		Normalize.sPower(lvlad, ssr);
	    	}
	        
	        
	        // L2 Normalization (performed in any case)
	        Normalize.l2(lvlad);   
	        
	        float[] lvlad_f = null;
			if (pc == null ) {
				lvlad_f = VectorMath.getFloats( lvlad  );				
			} else {
				lvlad_f = pc.project_float( lvlad );
			}
			
			//res.add( new FloatsLF(kp, lvlad_f ) );
			
			
//			// concatenare con la SIFT originale
			float[] lf_f = ArrayValuesConversion.getFloats( (IArrayValues) lf);
			Normalize.l2(lf_f);
			
			//VectorMath.multiply(lf_f, 0.5F);
			
			float[] res_f = new float[lf_f.length + lvlad_f.length ];
			System.arraycopy(lf_f, 0, res_f, 0, lf_f.length);
			System.arraycopy(lvlad_f, 0, res_f, lf_f.length, lvlad_f.length);
			
			Normalize.l2(res_f);
			
			
			res[iLF] =  new FloatsLF(kp, res_f );
		}
		return new FloatsLFGroup(res);
		
		
		
//		return new FloatsLFGroup(res.toArray(new FloatsLF[res.size()]));
	
	}
	
}
