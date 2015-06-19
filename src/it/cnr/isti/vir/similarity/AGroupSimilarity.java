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
package it.cnr.isti.vir.similarity;

import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.util.math.HarmonicMean;

import java.util.Properties;

public abstract class AGroupSimilarity<F extends ALocalFeaturesGroup> implements ISimilarity<F> {
	
	protected static final int optFt1 =0;
	protected static final int optFt2 =1;
	protected static final int optAvg =2;
	protected static final int optMin =3;
	protected static final int optMax =4;
	protected static final int optHrm =5;
	protected static final int optAbs =6;

	protected int absNormMax = 100000;
	
	protected int option;
	
	protected long distCount = 0;
	
	public AGroupSimilarity() {
		option = optFt1;
	}
	
	public AGroupSimilarity(Properties properties) throws SimilarityOptionException {
		this(properties.getProperty("simOption"));
		
	}
	
	public void set(Properties properties) {
		properties.getProperty("simOption");
		
	}
	
	public AGroupSimilarity(String opt) throws SimilarityOptionException {
		option =  getSimOptionInt(opt);
	}
	
	public static int getSimOptionInt(String opt) throws SimilarityOptionException {
		int option = 0;
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
			} else if ( opt.equals("hrm")) {
				option = optHrm;
			} else if ( opt.equals("abs")) {
				option = optAbs;
			} else {
				throw new SimilarityOptionException("Option " + opt + " not found!");
			}	
		}
		
		return option;
	}
	
	public double getPercentage(int n, int size1, int size2) {
		switch (option) {
			case optFt1:	return n / (double) size1;
			case optFt2:	return n / (double) size2;
			case optAvg:	return ( n/size1 + n/size2) / 2.0;
			case optMin:	return Math.max( n/(double) size1, n/(double) size1);
			case optMax:	return Math.min( n/(double) size1, n/(double) size1);	
			case optHrm:   return HarmonicMean.get(n/(double) size1, n/(double) size1);
			case optAbs:   return n / (double) absNormMax;
			default: 		return n / (double) absNormMax;
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
			case optHrm:   optionStr="optHrm"; break;	
			case optAbs:   optionStr="optAbs"; break;	
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
	
	public abstract Class<F> getRequestedGroup();
	
}
