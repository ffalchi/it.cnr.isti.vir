package it.cnr.isti.vir.features;

import java.io.DataInput;
import java.nio.ByteBuffer;

public class CNNFloats extends Floats {

    public CNNFloats(ByteBuffer in ) throws Exception {
        super(in);
    }
	
	public CNNFloats(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		super(in, fc);	
	}
	
	public CNNFloats(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		super(in, fc);
    }
	
	public CNNFloats(float[] values) {
		super(values);
	}
	
	public CNNFloats(Floats f) {
		super(f.values);
	}

	public CNNFloats(DataInput in ) throws Exception {
		super(in);
	}

}
