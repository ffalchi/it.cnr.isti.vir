package it.cnr.isti.vir.features;

import it.cnr.isti.vir.util.bytes.LongByteArrayUtil;
import it.cnr.isti.vir.util.math.Binarization;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BinaryLongs extends AbstractFeature implements ILongBinaryValues {

	public AbstractFeaturesCollector linkedFC;
	
	public long[] values;

    public BinaryLongs(ByteBuffer in ) throws Exception {
        this(in, null);
    }
	
	public BinaryLongs(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
		int size = in.getInt();
		linkedFC = fc;
		if ( size != 0 ) { 
			values = new long[size];
			for ( int i=0; i<values.length; i++ ) {
				values[i] = in.getLong();
			}
		}	
	}
	
	public BinaryLongs(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
		
		int size = in.readInt();

		if ( size != 0 ) {
			int nBytes = Long.BYTES*size;
			byte[] bytes = new byte[nBytes];
			in.readFully(bytes);
			values = LongByteArrayUtil.getArr(bytes, 0, size);
		}
    }
	
	public BinaryLongs(long[] values) {
		this.values = values;
	}
	
	public BinaryLongs(float[] f ) {
		values = Binarization.getLongs(f );
	}

	public BinaryLongs(float[] f, float thr) {
		values = Binarization.getLongs(f, thr);
	}
	
	public BinaryLongs(DataInput in ) throws Exception {
		this(in, null);
	}
	
	
	
	final int getDim() {
		return values.length;
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		if ( values == null ) {
			out.writeInt( 0);
		} else {
			out.writeInt( values.length );
			byte[] b = new byte[LongByteArrayUtil.BYTES*values.length];
			LongByteArrayUtil.convToBytes(values, b, 0);
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
	


	@Override
	public int getLength() {
		return values.length;
	}

	@Override
	public final long[] getValues() {
		return values;
	}


	@Override
	public int getNBits() {
		return values.length * 64;
	}

	public int bitCount() {
		int res = 0;
		for (int i=0; i<values.length; i++) {
			res += Long.bitCount(values[i]);
		}
		
		return res;
	}
	
}
