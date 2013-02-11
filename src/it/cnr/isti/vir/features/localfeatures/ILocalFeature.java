package it.cnr.isti.vir.features.localfeatures;

import it.cnr.isti.vir.classification.ILabeled;
import it.cnr.isti.vir.features.IFeature;
import java.awt.geom.Point2D;

public interface ILocalFeature extends IFeature, Comparable<IFeature>, ILabeled {
	
	public Class<AbstractLFGroup> getGroupClass();
	
	public AbstractLFGroup getLinkedGroup();
	
	public ILocalFeature getUnlinked(); 

	public float getScale();

    public float getNormScale();
	
	public float getOrientation();
	
//	public double getX();
//	
//	public double getY();
	
	public float[] getXY();

    public float[] getNormXY();
}
