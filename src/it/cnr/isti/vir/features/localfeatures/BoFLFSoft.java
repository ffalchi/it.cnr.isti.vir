package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.features.IFeaturesCollector;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BoFLFSoft implements ILocalFeature {

	public  final int[] bag;
	public 	final float[] wBag;
	private final float[] xy;
    private float[] normxy;
	public final float ori;
	public final float scale;
	
//	private  final static int byteSize = 20;
	
	public final int getByteSize() {
		return 1 + 4 + 4 + 4 * 2 + bag.length * 4 + wBag.length * 4;
	}
	
	private BoFLFGroupSoft linkedGroup;
	
	
	public void writeData(ByteBuffer buff) throws IOException {
		buff.put( (byte) bag.length );
		for ( int i=0; i<bag.length ; i++ ) {
			buff.putInt(bag[i]);
		}
		for ( int i=0; i<wBag.length ; i++ ) {
			buff.putFloat(wBag[i]);
		}
		buff.putFloat(xy[0]);
		buff.putFloat(xy[1]);
		buff.putFloat(ori);
		buff.putFloat(scale);
	}
	
	public BoFLFSoft(ByteBuffer in, BoFLFGroupSoft linkedGroup) {
		byte size = in.get();
		bag = new int[size];
		wBag = new float[size];
		for ( int i=0; i<size; i++ ) {
			bag[i] = in.getInt();
		}
		for ( int i=0; i<size; i++ ) {
			wBag[i] = in.getFloat();
		}		
		xy = new float[2];
		xy[0] = in.getFloat();
		xy[1] = in.getFloat();
		ori = in.getFloat();
		scale = in.getFloat();
		this.linkedGroup = linkedGroup;
	}
	
	public BoFLFSoft(int[] bag, float[] wBag, float[] xy, float ori, float scale, BoFLFGroupSoft linkedGroup) {
		this.bag = bag;
		this.wBag = wBag;
		this.xy = xy;
		this.ori = ori;
		this.scale = scale;
		this.linkedGroup = linkedGroup;		
	}

	@Override
	public void writeData(DataOutput out) throws IOException {
		byte[] tArr = new byte[20];
		ByteBuffer buff = ByteBuffer.wrap(tArr);
		writeData(buff);
		out.write(tArr);
	}



	@Override
	public int compareTo(IFeature o) {
		BoFLFSoft given = (BoFLFSoft) o;
		for ( int i=0; i<bag.length; i++ ) {
			if ( bag[i] != given.bag[i] ) 	return bag[i]-given.bag[i];
		}
		for ( int i=0; i<wBag.length; i++ ) {
			if ( wBag[i] != given.wBag[i] ) return Float.compare( wBag[i], given.wBag[i] );
		}
		if ( scale != given.scale ) return Float.compare( scale, given.scale );
		if ( xy[0] != given.xy[0] ) return Float.compare( xy[0], given.xy[0] );
		if ( xy[1] != given.xy[1] ) return Float.compare( xy[1], given.xy[1] );
		if ( ori != given.ori ) 	return Float.compare( ori, given.ori );
		return 0;
	}

	@Override
	public AbstractLabel getLabel() {
		return ((ILabeled) linkedGroup).getLabel();
	}

	@Override
	public Class getGroupClass() {
		return BoFLFGroup.class;
	}

	@Override
	public AbstractLFGroup<BoFLFSoft> getLinkedGroup() {
		return linkedGroup;
	}

	@Override
	public ILocalFeature getUnlinked() {
		return new BoFLFSoft( bag, wBag, xy, ori, scale, null);
	}

	@Override
	public float getScale() {
		return scale;
	}

	@Override
	public float getOrientation() {
		return ori;
	}

	@Override
	public float[] getXY() {
		return xy;
	}

        @Override
	public float[] getNormXY() {
        	if ( normxy == null ) {
                    normxy = new float[2];
                    float[] mean = linkedGroup.getMeanXY();
                    float scale = linkedGroup.getNormScale();
                    normxy[0] = ( xy[0] - mean[0] ) * scale;
                    normxy[1] = ( xy[1] - mean[1] ) * scale;
                }
		return normxy;
	}

	@Override
	public float getNormScale() {
		return linkedGroup.getNormScale() * getScale();
	}


}
