package it.cnr.isti.vir.util;

import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.string.Percentage;
import it.cnr.isti.vir.util.string.Time;

import java.util.Properties;

public class TimeManager {

	public static long standardMinInterval = 10000; // millis
	
	public long start;
	public long last;
	
	public long minInterval = standardMinInterval;
	
	public TimeManager() {
		start = System.currentTimeMillis();
		last = start;
	}
	
	public TimeManager(long minInterval) {
		this();
		
		this.minInterval = minInterval;
	}
	
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
	
	public void resetOutputTimer() {
		last = start;
	}
	
	public boolean hasToOutput_long(int nTimes) {
		long curr = System.currentTimeMillis();
		if ( (curr-last) > minInterval*nTimes ) {
			last = curr;
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
	
	public long getExtimatedTimeToComplete(double perc) {
		long curr = System.currentTimeMillis();
		
		return (long) ((curr-start)/(perc/100));
	}
	
	public long getExtimatedTimeToComplete(int curr, int tot) {
		long currT = System.currentTimeMillis();
		
		return (long) ((double)(currT-start)/curr*(tot-curr));
	}
	
	public String getExtimatedTimeToComplete_STR(int curr, int tot) {
		return Time.getString_sec( getExtimatedTimeToComplete(curr, tot) );
	}
	
	public String getProgressString(int curr, int tot) {
		return curr + "/" + tot + " " + Percentage.getString(curr, tot) + " " + "ETC " + getExtimatedTimeToComplete_STR(curr, tot);
	}
}
