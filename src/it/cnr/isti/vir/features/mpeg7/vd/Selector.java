package it.cnr.isti.vir.features.mpeg7.vd;

import java.io.Serializable;

public class Selector implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5891158337176111145L;
	boolean SC = true;
	boolean CS = true;
	boolean CL = true;
	boolean DC = true;
	boolean EH = true;
	boolean HT = true;
	int selectedCount = 0;
	
	public Selector(boolean setAllFlag) {
		setAll(setAllFlag); 
	}
	public Selector(String string) {
		setAll( false );
		set( string );
	}

	public final void set( String descriptors ) {
		
		String temp = descriptors.toLowerCase();
		
		if( temp.contains("sc")) SC = true;
		if( temp.contains("cs")) CS = true;
		if( temp.contains("cl")) CL = true;
		if( temp.contains("dc")) DC = true;
		if( temp.contains("eh")) EH = true;
		if( temp.contains("ht")) HT = true;	
		
		countSelected();
	}
	
	public final boolean isMultipleSelected() {
		return selectedCount > 1;
		
	}
	
	protected void countSelected() {
		selectedCount = 0;
		if ( SC ) selectedCount++;
		if ( CS ) selectedCount++;
		if ( CL ) selectedCount++;
		if ( DC ) selectedCount++;
		if ( EH ) selectedCount++;
		if ( HT ) selectedCount++;
		
	}
	
	
	public final boolean isCL() {
		return CL;
	}
	public final void setCL(boolean cl) {
		CL = cl;
		countSelected();
	}
	public final boolean isCS() {
		return CS;
	}
	public final void setCS(boolean cs) {
		CS = cs;
		countSelected();
	}
	public final boolean isDC() {
		return DC;
	}
	public final void setDC(boolean dc) {
		DC = dc;
		countSelected();
	}
	public final boolean isEH() {
		return EH;
	}
	public final void setEH(boolean eh) {
		EH = eh;
		countSelected();
	}
	public final boolean isHT() {
		return HT;
	}
	public final void setHT(boolean ht) {
		HT = ht;
		countSelected();
	}
	public final boolean isSC() {
		return SC;
	}
	public final void setSC(boolean sc) {
		SC = sc;
		countSelected();
	}
	
	public final void setAll(boolean flag) {
		SC = flag;
		CS = flag;
		CL = flag;
		DC = flag;
		EH = flag;
		HT = flag;
		countSelected();
	}

}
