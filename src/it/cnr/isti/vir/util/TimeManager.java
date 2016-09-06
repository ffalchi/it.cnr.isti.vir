/*******************************************************************************
 * Copyright (c) 2013, Fabrizio Falchi (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package it.cnr.isti.vir.util;

import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.string.Percentage;
import it.cnr.isti.vir.util.string.Time;

import java.util.Properties;

public class TimeManager {

	public static long standardMinInterval = 10000; // millis
	
	public long start;
	public long last;
	public long lastResetTime;
	
	public long minInterval = standardMinInterval;
	
	private int totNEle = -1;
	
	private int curr = 0;
	private int lastResetCurr = 0;
	
	public synchronized void setTotNEle(int totNEle) {
		this.totNEle = totNEle;
	}
	
	public TimeManager() {
		start = System.currentTimeMillis();
		last = start;
		lastResetTime = start;
	}
	
	public TimeManager(int totNEle) {
		this();
		
		this.totNEle = totNEle;
	}
	
//	public TimeManager(long minInterval) {
//		this();
//		
//		this.minInterval = minInterval;
//	}
	
	public long getTime() {
		return System.currentTimeMillis()-start;
	}
	
	public long getTime_sec() {
		return getTime() / 1000;
	}

	
	public int getTime_min() {
		return (int) (getTime_sec() / 60);
	}

	public int getTime_h() {
		return getTime_min() / 60	;
	}
	
	public boolean hasToOutput() {
		long curr = System.currentTimeMillis();
		if ( (curr-last) > minInterval ) {
			last = curr;
			return true;
		}
		return false;
	}
	
	public boolean hasToOutput_long(int nTimes) {
		long currT = System.currentTimeMillis();
		if ( (currT-last) > minInterval*nTimes ) {
			last = currT;
			return true;
		}
		return false;
	}
	
	public long getTotalTime() {
		return System.currentTimeMillis() - start;
	}
	
	public String getTotalTime_STR() {
		return Time.getString_millis(getTotalTime());
	}

	public static void set(Properties properties) {
		String tStr = properties.getProperty("TimeManager.standardMinInterval");
		if ( tStr != null && tStr != "" ) {
			standardMinInterval = Long.parseLong(tStr);
		}
		Log.info_verbose("TimeManager.standardMinInterval was set to " + standardMinInterval + " millis");
	}
	
//	public long getExtimatedTimeToComplete(double perc) {
//		long curr = System.currentTimeMillis();
//		
//		return (long) ((curr-start)/(perc/100));
//	}
	
	public long getExtimatedTimeToComplete(int curr, int tot) {
		long currT = System.currentTimeMillis();
		
		return (long) ((double)(currT-lastResetTime)/(curr-lastResetCurr)*(tot-curr));
	}
	
	public String getExtimatedTimeToComplete_STR(int curr, int tot) {
		return Time.getString_sec( getExtimatedTimeToComplete(curr, tot) );
	}
	
	public String getProgressString(int curr, int tot) {
		if ( totNEle<0 )
			return curr + " " + Time.getString_sec(getTotalTime()) ;
		return curr + "/" + tot + " " + Percentage.getString(curr, tot) + " " + "ETC " + getExtimatedTimeToComplete_STR(curr, tot);
	}
	
	public String getProgressString(int curr ) {
		return curr + "/" + totNEle + " " + Percentage.getString(curr, totNEle) + " " + "ETC " + getExtimatedTimeToComplete_STR(curr, totNEle);
	}
	
	public final void reportProgress() {
		curr++;
		if ( Log.verbose ) {
			if ( hasToOutput() ) {
				Log.info_verbose_indent(getProgressString(curr, totNEle));
			}
		}
	}
	
	public final void reportProgress(int i) {
		curr+= i;
		if ( Log.verbose ) {
			if ( hasToOutput() ) {
				Log.info_verbose_indent(getProgressString(curr, totNEle));
			}
		}
	}

	public void resetExtimation() {
		lastResetCurr = curr;
		lastResetTime = System.currentTimeMillis();
	}

	public int getCurr() {
		return curr;
	}

}

