package it.cnr.isti.vir.similarity;

import java.util.Properties;

public abstract class IGroupSimilarity<F> implements ISimilarity<F> {
	
	protected static final int optFt1 =0;
	protected static final int optFt2 =1;
	protected static final int optAvg =2;
	protected static final int optMin =3;
	protected static final int optMax =4;

	protected final int option;
	
	protected long distCount = 0;
	
	public IGroupSimilarity() {
		option = optFt1;
	}
	
	public IGroupSimilarity(Properties properties) throws SimilarityOptionException {
		this(properties.getProperty("simOption"));
	}
	
	public IGroupSimilarity(String opt) throws SimilarityOptionException {
		if ( opt != null ) {
			if ( opt.equals("def")) {
				option = optFt1;
			} else if ( opt.equals("query")) {
				option = optFt1;
			} else if ( opt.equals("data")) {
				option = optFt2;
			} else if ( opt.equals("avg")) {
				option = optAvg;
			} else if ( opt.equals("min")) {
				option = optMin;
			} else if ( opt.equals("max")) {
				option = optMax;
			} else {
				throw new SimilarityOptionException("Option " + opt + " not found!");
			}	
		} else {
			option = 0;
		}
	}
	
	public String toString() {
		String optionStr = "";
		switch (option) {
			case optFt1:	optionStr="optFt1"; break;
			case optFt2:	optionStr="optFt2"; break;
			case optAvg:	optionStr="optAvg";	break;
			case optMin:	optionStr="optMin"; break;
			case optMax:	optionStr="optMax"; break;	
			default: 	break;
		
		}
		return this.getClass().toString() + " " + optionStr;
	}

	
	@Override
	public final double distance(F fc1, F fc2, double max) {
		return distance(fc1, fc2);
	}

	@Override
	public final long getDistCount() {
		return distCount;
	}
	
}
