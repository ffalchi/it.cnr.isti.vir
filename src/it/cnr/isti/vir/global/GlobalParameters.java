package it.cnr.isti.vir.global;

import it.cnr.isti.vir.util.TimeManager;

import java.util.Properties;

public class GlobalParameters {

	
	public static final void set(Properties properties) {
		ParallelOptions.set(properties);
		TimeManager.set(properties);
		Log.set(properties);
	}
}
