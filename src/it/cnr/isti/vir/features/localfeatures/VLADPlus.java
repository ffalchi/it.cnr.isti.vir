package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.IByteValues;
import it.cnr.isti.vir.features.IFloatValues;
import it.cnr.isti.vir.features.IIntValues;
import it.cnr.isti.vir.features.bof.LFWords;
import it.cnr.isti.vir.util.math.Norm;
import it.cnr.isti.vir.util.math.Normalize;

import java.io.DataInput;
import java.nio.ByteBuffer;

public class VLADPlus extends VLAD {

    public VLADPlus(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public VLADPlus(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		super(in, fc);
	}
	
	public VLADPlus(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		super(in, fc);
    }
	
	public VLADPlus(float[] values) {
		super(values);
	}

	public VLADPlus(DataInput in ) throws Exception {
		super(in);
	}
	
    public static final  VLADPlus getVLADPlus(ALocalFeaturesGroup features, LFWords fWords) throws Exception {
    	ALocalFeature[] refs = (ALocalFeature[]) fWords.getFeatures();
    	ALocalFeature[] lf  = features.getLocalFeatures();
        
        
        if ( ! (refs[0] instanceof IArrayValues) ) {
        	throw new Exception( "VLAD can't be computed for " + features.getClass() );
        }
        
        int d = ((IArrayValues) refs[0]).getLength(); 
        
        int size = refs.length * d;
        	
        float[] values = new float[size];
                
        if ( refs[0] instanceof IFloatValues ) {
        	float[] residuals = new float[d];
			
        	if ( lf.length == 0 ) {
				// NO LOCAL FEATURES WERE FOUND!
				for ( int i=0; i<size; ) {
					float[] ref = ((IFloatValues) refs[i/d]).getValues();
					for ( int id=0; id<d; id++) {
						values[i++] = -ref[id];
					}					
				}
			} else {
			
				
				for (int iLF = 0; iLF < lf.length; iLF++) {
	
					float[] curr = ((IFloatValues) lf[iLF]).getValues();
	
					int iW = fWords.getNNIndex(lf[iLF]);
					int start = iW * d;
					int end = start + d;
	
					float[] ref = ((IFloatValues) refs[iW]).getValues();
	
					float norm = Norm.l2(residuals);
					
					if ( norm != 0 ) {
						int i = iW * d;
						for (int j=0; j<d; j++) {
							values[i++] += residuals[j] / norm;
						}
					}
					
				}
			}
			
		} else if ( refs[0] instanceof IByteValues
	        		|| refs[0] instanceof IIntValues) {
			int[] residuals = new int[d];
			
			if ( lf.length == 0 ) {
				// NO LOCAL FEATURES WERE FOUND!				
				for ( int i=0; i<size; ) {
					byte[] ref = ((IByteValues) refs[i/d]).getValues();
					for ( int id=0; id<d; id++) {
						values[i++] = -ref[id];
					}					
				}
			} else {
			
				for (int iLF = 0; iLF < lf.length; iLF++) {
	
					byte[] curr = ((IByteValues) lf[iLF]).getValues();
	
					int iW = fWords.getNNIndex(lf[iLF]);
					byte[] ref = ((IByteValues) refs[iW]).getValues();
	
					for (int j=0; j<d; j++) {
						residuals[j] = curr[j] - ref[j];						
					}
					
					float norm = (float) Norm.l2(residuals);
					
					if ( norm != 0 ) {
	 					int i = iW * d;
						for (int j=0; j<d; j++) {
							values[i++] += residuals[j] / norm;
						}
					}
					
				}
			}
				        
		} else {
        	throw new Exception( "VLAD can't be computed for " + features.getClass() );
		}

        Normalize.power(values, 0.2);
        
        Normalize.l2(values);
        
        System.out.print("L2: " + Norm.l2(values) );
        
        return new VLADPlus(values);
    }

}
