package it.cnr.isti.vir.util.string;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	  

	
	public static String getString(long timeInMilliSeconds, boolean millis ) {
		  long seconds = timeInMilliSeconds / 1000;
		  long minutes = seconds / 60;
		  long hours = minutes / 60;
		  long days = hours / 24;
		  
		  String time = "";
		  if ( days > 0 ) time += String.format("%dd:", days);
		  if ( hours> 0 ) time += String.format("%02dh:", hours%24);
		  if ( minutes> 0 ) time += String.format("%02dm:", minutes%60);
		  time += String.format("%02d\"", seconds%60);
		  if (millis) time += String.format(":%03d", timeInMilliSeconds);
		  //String time = days + "d:" + hours % 24 + "h:" + minutes % 60 + "':" + seconds % 60 + "\"."  + timeInMilliSeconds % 1000; 
		  
		  return time;
	}
	  
	
	public static String getString_millis(long timeInMilliSeconds  ) {
		return getString(timeInMilliSeconds, true);
	}
	
	public static String getString_sec(long timeInMilliSeconds) {
		return getString(timeInMilliSeconds, false);
	}

}
