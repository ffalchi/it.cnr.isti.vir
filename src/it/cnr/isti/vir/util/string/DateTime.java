package it.cnr.isti.vir.util.string;
import it.cnr.isti.vir.experiments.ILaunchable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class DateTime implements ILaunchable {
	
  public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

  public static String now() {
    Calendar cal = Calendar.getInstance();
    return sdf.format(cal.getTime());
  }

  public static void  main(String arg[]) {
	  System.out.println();
  }

  public static void launch(Properties prop) {
	  System.out.println("Now : " + DateTime.now());
  }
}
