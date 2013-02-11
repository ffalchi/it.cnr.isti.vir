package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.classification.AbstractLabel;
import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.IFeature;
import it.cnr.isti.vir.util.Trigonometry;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;

public class BoFLF implements ILocalFeature, Cloneable{

	public  final int bag;
	private final float[] xy;
    private float[] normxy;
	public final float ori;
	public final float scale;
	
	private  final static int byteSize = 20;
	
	public final static int getByteSize() {
		return byteSize;
	}
	
	private BoFLFGroup linkedGroup;
	
	
	public BoFLF(BoFLF given, BoFLFGroup group) {
		this(given.bag, given.xy, given.ori, given.scale, group);
	}
	
	public BoFLF(ByteBuffer in, BoFLFGroup linkedGroup) {
		bag = in.getInt();
		xy = new float[2];
		xy[0] = in.getFloat();
		xy[1] = in.getFloat();
		ori = in.getFloat();
		scale = in.getFloat();
		this.linkedGroup = linkedGroup;
	}
	

	public static BoFLF readTOPSURF(String line, BoFLFGroup linkedGroup) {
		String[] temp = line.split("(\\s)+");
		int i = 0;
		if ( temp[0].equals("") ) i++; 
		int tBag = Integer.parseInt(temp[i++]);
		float[] tXY = new float[2];
		tXY[0] = Float.parseFloat(temp[i++]);
		tXY[1] = Float.parseFloat(temp[i++]);
		float tScale = Float.parseFloat(temp[i++]);
		float tOri   = (float) Trigonometry.getStdRadian( Double.parseDouble(temp[i++]) );
		
		return new BoFLF( tBag, tXY, tOri, tScale, linkedGroup) ;
	}
	
	public BoFLF(int bag, float[] xy, float ori, float scale, BoFLFGroup linkedGroup) {
		this.bag = bag;
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
	
	public void writeData(ByteBuffer buff) throws IOException {
		buff.putInt(bag);
		buff.putFloat(xy[0]);
		buff.putFloat(xy[1]);
		buff.putFloat(ori);
		buff.putFloat(scale);
	}


	@Override
	public int compareTo(IFeature o) {
		BoFLF given = (BoFLF) o;
		if ( bag != given.bag ) 	return Integer.compare(bag, given.bag);
				//bag-given.bag;
		if ( scale != given.scale ) return Float.compare(given.scale, scale ); // biggest first
		if ( xy[0] != given.xy[0] ) return Float.compare(xy[0], given.xy[0] );
		if ( xy[1] != given.xy[1] ) return Float.compare(xy[1], given.xy[1] );
		if ( ori != given.ori ) 	return Float.compare(ori, given.ori );
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
	public AbstractLFGroup<BoFLF> getLinkedGroup() {
		return linkedGroup;
	}

	@Override
	public ILocalFeature getUnlinked() {
		return new BoFLF( bag, xy, ori, scale, null);
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

	public String toString() {
		String tStr = "";
		tStr += bag +
				" " + xy[0] +
				" " + xy[1] + 
				" " + ori +
				" " + scale +
				"\n";
				
		return tStr;
	}

}
