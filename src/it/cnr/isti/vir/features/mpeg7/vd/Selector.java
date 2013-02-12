/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
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
