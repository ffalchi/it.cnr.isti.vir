package it.cnr.isti.vir.util.string;

import it.cnr.isti.vir.global.LocaleParameters;

import java.text.DecimalFormat;

public class Percentage {


	static final DecimalFormat number_format = new DecimalFormat("#.###", LocaleParameters.symbols);
	
	public static final String getString(int i, int n) {
		double perc = (double) i / n * 100.0;
		
		return number_format.format(perc) + "%";
	}
	
	public static final String getProgressString(int curr, int total) {
		return curr + "/" + total + "\t" + Percentage.getString(curr, total);
	}

}
