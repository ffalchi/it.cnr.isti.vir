package it.cnr.isti.vir.util.string;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {
	
  
	  public static String getString(long timeInMilliSeconds) {
		  long seconds = timeInMilliSeconds / 1000;
		  long minutes = seconds / 60;
		  long hours = minutes / 60;
		  long days = hours / 24;
		  
		  String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60 + "."  + timeInMilliSeconds % 1000; 
		  
		  return time;
	  }

}
