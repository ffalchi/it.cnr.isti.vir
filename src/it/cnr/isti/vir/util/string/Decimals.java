package it.cnr.isti.vir.util.string;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Decimals {
		
	
	static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	
	static DecimalFormat df;
	
	static {
		
		otherSymbols.setDecimalSeparator('.');
		df = new DecimalFormat("###.###", otherSymbols);
		df.setRoundingMode(RoundingMode.HALF_DOWN);
	}
	
	public static final String getString(double value) {
		return  df.format(value);
	}
}
