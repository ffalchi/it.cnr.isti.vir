/*******************************************************************************
 * Copyright (c) 2013, Lucia Vadicamo (NeMIS Lab., ISTI-CNR, Italy)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/

package it.cnr.isti.vir.features;
import it.cnr.isti.vir.features.localfeatures.FloatsLF;

public class Embedding {

	/**
	 * @param x matrix of a set of local descriptor (float values), each local descriptor is a row of the matrix 
	 * @param degree order of the polynomial embedding
	 * @return Polynomial embedding of a given degree
	 * @throws Exception
	 */
	public static final   FloatsLF get_poly(float[][] x, int degree, double exp_pn) throws Exception{
		switch (degree) {
		case 1:  return(get_poly1(x, exp_pn));

		case 2: return(get_poly2(x,exp_pn));

		case 3:  return(get_poly3(x,exp_pn));

		default: 
			throw new Exception("Polynomial embedding of degree"+degree+"is not yet supported");

		}

	}



	/**
	 * 
	 * @param x matrix of a set of local descriptor (float values), each local descriptor is a row of the matrix 
	 * @return Polynomial embedding of degree 1
	 * @throws Exception
	 */
	public static final   FloatsLF get_poly1(float[][] x,double exp_pn) throws Exception{

		int T=x.length; //sample size
		int D=x[0].length; //local descriptor dimension


		float[] polysum=new float[D];
		for(int t=0; t<T; t++){
			float[] xt = x[t];
			for(int d=0;d<D; d++){
				polysum[d]+=xt[d];
			}
		}

		double norm = 0;
		for ( int j=0; j<polysum.length; j++) {
			double val =polysum[j] ;
			//powernorm
			val=Math.signum(val)*Math.pow(Math.abs(val), exp_pn);//Math.sqrt(Math.abs(val));//Math.sqrt(Math.abs(val));
			
			polysum[j]= (float) val;
			norm += val * val;
		}
		norm=Math.sqrt(norm);
		if(norm>0){
			for ( int j=0; j<polysum.length; j++) {
				polysum[j]/= (float) norm ;
			}
		}
		return new FloatsLF(polysum);

	}
	/**
	 * 
	 * @param x matrix of a set of local descriptor (float values), each local descriptor is a row of the matrix 
	 * @return Polynomial embedding of degree 2
	 * @throws Exception
	 */
	public static final   FloatsLF get_poly2(float[][] x,double exp_pn) throws Exception{

		int T=x.length; //sample size
		int D=x[0].length;//local descriptor dimension
		float sqrt2= (float) Math.sqrt(2);
		
		float[] polysum=new float[(D*(D+1))/2];
		
		for(int t=0; t<T; t++){
			float[] xt = x[t];
				int pos=D;
			//new
			for(int d=0; d<D; d++)
				polysum[d]+=xt[d]*xt[d];
		
			for (int i=0; i<D; i++) {
				for(int j=i+1;j<D; j++) {
					polysum[pos++]+=sqrt2*xt[i]*xt[j];
				}
			}
		
			
			//my old
//			for(int d=0;d<D; d++){
//				polysum[d]+=xt[d]*xt[d];
//				int num=D+(d*(d-1))/2;
//				for(int j=0; j<d;j++){
//					polysum[j+num]+=sqrt2*xt[d]*xt[j];
//				}
		//	}
		}

		double norm = 0;
		for ( int j=0; j<polysum.length; j++) {
			double val =polysum[j] ;
			//powernorm
			val=Math.signum(val)*Math.pow(Math.abs(val), exp_pn);//Math.sqrt(Math.abs(val));//Math.pow(Math.abs(val), 0.2);//Math.sqrt()
			
			polysum[j]= (float) val;
			norm += val * val;
		}
		norm=Math.sqrt(norm);

		if(norm>0){
			for ( int j=0; j<polysum.length; j++) {
				polysum[j]= (polysum[j]/(float)norm) ;
			}
		}
		
		return new FloatsLF(polysum);

	}
	/**
	 * 
	 * @param x matrix of a set of local descriptor (float values), each local descriptor is a row of the matrix 
	 * @return Polynomial embedding of degree 3
	 * @throws Exception
	 */
	public static final   FloatsLF get_poly3(float[][] x,double exp_pn) throws Exception{

		int T=x.length; //sample size
		int D=x[0].length;
		float sqrt3= (float) Math.sqrt(3);
		float sqrt6= (float) Math.sqrt(6);

		float[] polysum=new float[(D*D*D+3*D*D+2*D)/6];
		for(int t=0; t<T; t++){
			float[] xt = x[t];
			for(int d=0; d<D;d++) {
				polysum[d]+=xt[d]*xt[d]*xt[d];
			}
			
			//new
			int pos=D;
			for(int i=0;i<D;i++) {
				for(int j=0;j<D;j++) {
					if(i==j)
						continue;
					polysum[pos++]+=xt[i]*xt[i]*xt[j]*sqrt3;
						
				}
			}
			
			for(int i=0;i<D;i++) {
			   float xti=xt[i];
			   for(int j=i+1;j<D;j++) {
				   float xtj=xt[j];
				   for(int k=j+1;k<D; k++)
					   polysum[pos++]+=xti*xtj*xt[k]*sqrt6;   
			   }
			}
			
			//my OLD
//			
//			int idx1=D;
//			int idx2=(D*(D+1))/2;
//			for(int d=0;d<D; d++){
//				if(xt[d]!=0){
//					polysum[d]+=xt[d]*xt[d]*xt[d];
//					for(int j=0; j<d;j++){
//						if(xt[j]!=0){
//							polysum[idx1]+=sqrt3*xt[d]*xt[d]*xt[j];
//							idx1++;
//							for(int l=0; l<j; l++){
//								polysum[idx2]+=sqrt6* xt[d]*xt[j]*xt[l];
//								idx2++;
//							}
//						}
//						else{
//							idx1++;
//							idx2+=j;
//						}
//
//					}
//				}
//				else{
//					idx1+=d;
//					idx2+=d*(d+1)/2;
//				}
//
//
//				for(int j=d+1; j<D;j++){
//					polysum[idx1]=sqrt3*xt[d]*xt[d]*xt[j];
//					idx1++;
//				}
//			}
		}

		double norm = 0;
		for ( int j=0; j<polysum.length; j++) {
			double val =polysum[j] ;
			//powernorm
			val=Math.signum(val)*Math.pow(Math.abs(val), exp_pn);//Math.sqrt(Math.abs(val));//Math.pow(Math.abs(val), 0.2);//
			
			polysum[j]= (float) val;
			
			norm += val * val;
		}
		norm=Math.sqrt(norm);

		if(norm>0){
			for ( int j=0; j<polysum.length; j++) {
				polysum[j]= (polysum[j]/(float)norm) ;
			}
		}
		return new FloatsLF(polysum);

	}
}