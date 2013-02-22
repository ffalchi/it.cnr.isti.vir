package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.features.IFeaturesCollector;

import java.io.DataInput;
import java.nio.ByteBuffer;

public class ORBGroup extends ALocalFeaturesGroup<ORB> {

	public static final byte version = 0;
	
	public ORBGroup(ORB[] arr, IFeaturesCollector fc) {
		super(arr, fc);
	}

	public ORBGroup(IFeaturesCollector fc) {
		super(fc);
	}

	public ORBGroup(DataInput in) throws Exception {
		this(in, null);
	}
	
	public ORBGroup(DataInput in, IFeaturesCollector fc) throws Exception {
		super(fc);
		in.readByte(); // version
		int nBytes = in.readInt();
		byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		ByteBuffer bBuffer = ByteBuffer.wrap(bytes);
		lfArr = new ORB[bBuffer.getInt()];
		for (int i = 0; i < lfArr.length; i++) {
			this.lfArr[i] = new ORB(bBuffer, this);
		}
	}
	
	
	public ORBGroup(ByteBuffer in, IFeaturesCollector fc) throws Exception {
		super(fc);
		in.get(); // version
		in.getInt(); // nBytes
		int nLFs = in.getInt();
		lfArr = new ORB[nLFs];
		for ( int i = 0; i < nLFs; i++ ) {
			lfArr[i] = new ORB(in, this);
		}
	}

	@Override
	public Class<ORB> getLocalFeatureClass() {
		return ORB.class;
	}

	@Override
	public ALocalFeaturesGroup<ORB> create(ORB[] arr, IFeaturesCollector fc) {
		return new ORBGroup( arr, fc);
	}
	@Override
	public byte getSerVersion() {
		return version;
	}

}
