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
