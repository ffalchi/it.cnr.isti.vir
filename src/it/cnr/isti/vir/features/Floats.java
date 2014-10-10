package it.cnr.isti.vir.features;

import it.cnr.isti.vir.features.localfeatures.SIFT;
import it.cnr.isti.vir.util.Mean;
import it.cnr.isti.vir.util.bytes.FloatByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class Floats extends AbstractFeature implements IFloatValues {

	public AbstractFeaturesCollector linkedFC;
	
	float[] values;
	
	final int getDim() {
		return values.length;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length);
			byte[] b = new byte[FloatByteArrayUtil.BYTES*values.length];
			FloatByteArrayUtil.convToBytes(values, b, 0);
			out.write(b);
		}
	}
	
	public void writeData(ByteBuffer buff) throws IOException {
		if ( values == null ) {
			buff.putInt( 0);
		} else {
			buff.putInt( values.length);
			for ( int i=0; i<values.length; i++ )
				buff.putFloat(values[i]);
		}
	}
	
    public Floats(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public Floats(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new float[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getFloat();
			}
		}	
	}
	
	public Floats(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Float.BYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = FloatByteArrayUtil.get(bytes, 0, size);
		}
    }
	
	public Floats(float[] values) {
		this.values = values;
	}

	public Floats(DataInput in ) throws Exception {
		this(in, null);
	}

	@Override
	public int getLength() {
		return values.length;
	}

	@Override
	public float[] getValues() {
		return values;
	}
	
	public static Floats getMean(Collection<Floats> coll) {
		if ( coll.size() == 0 ) return null;
		float[][] values = new float[coll.size()][];
		int i=0;
		for ( Iterator<Floats> it = coll.iterator(); it.hasNext(); ) {
			values[i++] = it.next().values;
		}
				
		return new Floats(Mean.getMean(values));		
	}

	public void reduceToDim(int dim) throws Exception {
		if ( dim > values.length )
				throw new Exception("Requested dimensionality greater than current.");
		values = Arrays.copyOf(values, dim);
		
	}
	
}
