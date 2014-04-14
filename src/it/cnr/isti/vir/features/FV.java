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
import it.cnr.isti.vir.util.bytes.DoubleByteArrayUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * <img src="\Users\lucia\Documents\NetBeansProjects\Lucia\LV_Fhisher\Info\fvToHTML-0.png" /> <p>
 * <img src="C:\Users\lucia\Documents\NetBeansProjects\Lucia\LV_Fhisher\Info\fvToHTML-1.png" />
 * 
 *@author Lucia Vadicamo
 */
public class FV extends AbstractFeature{
    public AbstractFeaturesCollector linkedFC;
    double[] values;
    
    public double[] getValues() {
        return values;
    }
    
    public int size() {
        return values.length;
    }
    
    @Override
    public void writeData(DataOutput out) throws IOException {
        out.writeInt( values.length);
        
        byte[] b = new byte[Double.BYTES*values.length];
        DoubleByteArrayUtil.convToBytes(values, b, 0);
        out.write(b);
        //for ( int i=0; i<values.length; i++ )
        //    out.writeDouble(values[i]);
    }
    
    public void writeData(ByteBuffer buff) throws IOException {
        buff.putInt( values.length);
        for ( int i=0; i<values.length; i++ )
            buff.putDouble(values[i]);
    }
    
    public FV(ByteBuffer in ) throws Exception {
        this(in, null);
    }
    
    public FV(ByteBuffer in, AbstractFeaturesCollector fc ) throws Exception {
        int size = in.getInt();
        values = new double[size];
        for ( int i=0; i<values.length; i++ ) {
            values[i] = in.getDouble();
        }
        linkedFC = fc;
    }
    
    public  FV(double[] values) {
        this.values = values;
    }
    
    public FV(DataInput in ) throws Exception {
        this(in, null);
    }
    
    public FV(DataInput in, AbstractFeaturesCollector fc ) throws Exception {
        int size = in.readInt();
        
        int nBytes = Double.BYTES*size;
        byte[] bytes = new byte[nBytes];
		in.readFully(bytes);
		values = DoubleByteArrayUtil.get(bytes, 0, size);
    }
    

    /**
     *  Compute Fisher vector given the local descriptors (after PCA dimensionality reduction) and
     * the Gmm parameters (mixture weight, mean and covariance matrix)
     *
     * @param x
     * @param gmm
     * @param compute_mu_part
     * @param compute_w_part
     * @param power_norm
     * @param compute_sigma_part
     * @return 
     * @throws Exception
     *
     */
    
    public static final   FV getFV(double[][] x, Gmm gmm, boolean power_norm,   boolean compute_w_part, boolean compute_mu_part,boolean compute_sigma_part) throws Exception{
        if(compute_w_part){
            if(compute_mu_part)
                if(compute_sigma_part)
                    return(getFV_wms(x, gmm, power_norm)) ;
                else
                    return(getFV_wm(x, gmm, power_norm));
            else{
                if(compute_sigma_part)
                    return(getFV_ws(x, gmm, power_norm));
                else
                    return(getFV_w(x, gmm, power_norm));
            }
        }else{
            if(compute_mu_part)
                if(compute_sigma_part)
                    return(getFV_ms(x, gmm, power_norm))  ;
                else
                    return(getFV_m(x, gmm, power_norm));
            else{
                if(compute_sigma_part)
                    return(getFV_s(x, gmm, power_norm))  ;
                else
                    return null;
            }
        }
    }
    

 
    public static final   FV getFV_w(double[][] x2, Gmm gmm, boolean power_norm) throws Exception{    
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        
        int T=x2.length; //sample size
       
        double[][] p= gmm.compute_p(x2);
        
        double[] fv=new double[k];
        
        double[] S0= new double[k];

        double l2sum=0;

        
        for (int t=0; t<T; t++){
        	double[] pt = p[t];
         	for(int i=0; i<k; i++){
            	double p_ti=pt[i];
                if((float)p_ti!=0.f){
                    S0[i]+=p_ti;
                }
            }
        }
            
        for(int i=0; i<k; i++){
            double sqrt_wi=Math.sqrt(w[i]);
            double fv_i=(S0[i]/T-w[i])/sqrt_wi;
            
            if(power_norm)
                fv_i= Math.signum(fv_i)*Math.sqrt(Math.abs(fv_i));
            
            l2sum+=fv_i*fv_i;
            fv[i]=fv_i;            
        }
        
//        for(int i=0; i<k; i++){
//            for (int t=0; t<T; t++){
//            	double p_ti=p[t][i];
//                if((float)p_ti!=0.f){
//                    S0[i]+=p_ti;
//                }
//            }
//            double sqrt_wi=Math.sqrt(w[i]);
//            double fv_i=(S0[i]/T-w[i])/sqrt_wi;
//            
//            if(power_norm)
//                fv_i= Math.signum(fv_i)*Math.sqrt(Math.abs(fv_i));
//            
//            l2sum+=fv_i*fv_i;
//            fv[i]=fv_i;
//            
//        }
        
        //l2 normalization
        
        if(l2sum!=0){
            l2sum= Math.sqrt(l2sum);
            for(int i=0; i<k; i++){
                fv[i]/=l2sum;
            }
        }
        
        return new FV(fv);
        
    }
    
    public static final   FV getFV_m(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
        int d=gmm.getD();
        if(d!=x[0].length)
            throw new Exception("Gmm and descriptors dimensions are not consistent");
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        double[] mu=gmm.getMu();//dimesion k*d
        double[] sigma=gmm.getSigma();//dimesion k*d
        
        int T=x.length; //sample size
        
        double[][] p= gmm.compute_p(x);
        
        double[] fv=new double[k*d];
        
        
        double[] S0= new double[k];
        double[] S1= new double[d*k];
        
        double l2sum=0;
        
        for (int t=0; t<T; t++){
        	double[] pt = p[t];
        	
	        for(int i=0; i<k; i++){
	            int i_col=i*d;
            
            	double p_ti = pt[i];               
                
                if((float)p_ti!=0.f){
                	double[] xt = x[t];
                	 
                    S0[i]+=p_ti;
                    for(int j=0; j<d; j++){
                        S1[i_col+j] += p_ti*xt[j];
                    }
                }
            }
            
        }
        
        for(int i=0; i<k; i++){
        	int i_col=i*d;
            double sqrt_wi_T=Math.sqrt(w[i])*T;
            
            for(int j=0; j<d; j++){
                int h=i_col+j;
                double fv_h=(S1[h]-S0[i]*mu[h])/(Math.sqrt(sigma[h])*sqrt_wi_T);
                
                if(power_norm)
                    fv_h= Math.signum(fv_h)*Math.sqrt(Math.abs(fv_h));
                
                l2sum+=fv_h*fv_h;
                fv[h]=fv_h;
            }
            
        }

        //l2 normalization
        if(l2sum!=0){
            l2sum= Math.sqrt(l2sum);
            for(int i=0; i<k*d; i++)
                fv[i]/=l2sum;
        }
        return new FV(fv);
    }
    
    
    
    public static final   FV getFV_s(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
        
        int d=gmm.getD();
        if(d!=x[0].length)
            throw new Exception("Gmm and descriptors dimensions are not consistent");
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        double[] mu=gmm.getMu();//dimesion k*d
        double[] sigma=gmm.getSigma();//dimesion k*d
        
        int T=x.length; //sample size
        
        double[][] p= gmm.compute_p(x);
    
        double[] fv=new double[k*d];
        
         
        double[] S0= new double[k];
        double[] S1= new double[d*k];
        double[] S2= new double[d*k];
        
        double l2sum=0;
        
        for(int i=0; i<k; i++){
            int i_col=i*d;
            for (int t=0; t<T; t++){
            	double p_ti=p[t][i];
                double[] xt = x[t];
                if((float)p_ti!=0.f){
                    S0[i]+=p_ti;
                    for(int j=0; j<d; j++){
                        double temp=p_ti*xt[j];
                        S1[i_col+j]+=temp;
                        S2[i_col+j]+=temp*xt[j];
                    }
                }
            }
            double sqrt_wi_T=T*Math.sqrt(w[i]);
            double sqrt_2wi_T=Math.sqrt(2)*sqrt_wi_T;
            
            
            for(int j=0; j<d; j++){
                int h=i_col+j;
                double mu_h=mu[h];
                double sigma_h=sigma[h];
                double fv_h=(S2[h]-2*mu_h*S1[h]+(mu_h*mu_h-sigma_h)*S0[i])/(sqrt_2wi_T*sigma_h);
                if(power_norm){
                    fv_h= Math.signum(fv_h)*Math.sqrt(Math.abs(fv_h));
                }
                
                l2sum+=fv_h*fv_h;
                fv[h]=fv_h;
            }
        }
        
        
        //l2 normalization
        
        if(l2sum!=0){
            l2sum= Math.sqrt(l2sum);
            
            for(int i=0; i<k*d; i++){
                fv[i]/=l2sum;
            }
        }

        return new FV(fv);
        
    }
    
    
    public static final   FV getFV_wm(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
        int d=gmm.getD();
        if(d!=x[0].length)
            throw new Exception("Gmm and descriptors dimensions are not consistent");
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        double[] mu=gmm.getMu();//dimesion k*d
        double[] sigma=gmm.getSigma();//dimesion k*d
        double T=x.length; //sample size
        
        double[][] p= gmm.compute_p(x);
        double[] fvW=new double[k];
        double[] fvM=new double[k*d];
        
      
        double[] S0= new double[k];
        double[] S1= new double[d*k];
        
        double l2sum=0;
        
        for(int i=0; i<k; i++){
            int i_col=i*d;
            for (int t=0; t<T; t++){
            	double p_ti=p[t][i];
                double[] xt = x[t];
                if((float)p_ti!=0.f){
                    S0[i]+=p_ti;
                    for(int j=0; j<d; j++){
                        S1[i_col+j]+=p_ti*xt[j];
                    }
                }
            }
            double sqrt_wi= Math.sqrt(w[i]);
            double sqrt_wi_T= sqrt_wi*T;
            
            double fvW_i=(S0[i]/T-w[i])/sqrt_wi;
            
            
            if(power_norm)
                fvW_i= Math.signum(fvW_i)*Math.sqrt(Math.abs(fvW_i));
            
            l2sum+=fvW_i*fvW_i;
            fvW[i]=fvW_i;
            
            for(int j=0; j<d; j++){
                int h=i_col+j;
                
                double fvM_h=(S1[h]-S0[i]*mu[h])/(Math.sqrt(sigma[h])*sqrt_wi_T);
                if(power_norm)
                    fvM_h= Math.signum(fvM_h)*Math.sqrt(Math.abs(fvM_h));
                
                l2sum+=fvM_h*fvM_h;
                fvM[h]=fvM_h;
            }
        }
        double[] fv=new double[k+k*d];
        
        //l2 normalization
        
        if(l2sum==0){
            for(int i=0; i<k+k*d; i++)
                fv[i]=0;
        }
        else{
            l2sum= Math.sqrt(l2sum);
            for(int i=0; i<k; i++){
                fv[i]=fvW[i]/l2sum;
            }
            for(int i=0; i<k*d; i++){
                fv[k+i]=fvM[i]/l2sum;
            }
        }
        
        return new FV(fv);
    }
    
   

    public static final   FV getFV_ws(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
        
        int d=gmm.getD();
        if(d!=x[0].length)
            throw new Exception("Gmm and descriptors dimensions are not consistent");
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        double[] mu=gmm.getMu();//dimesion k*d
        double[] sigma=gmm.getSigma();//dimesion k*d
        
        int T=x.length; //sample size
        
        double[][] p= gmm.compute_p(x);
        
        double[] fvW=new double[k];
        double[] fvS=new double[k*d];
        

        double[] S0= new double[k];
        double[] S1= new double[d*k];
        double[] S2= new double[d*k];
        
        double l2sum=0;
        
        for(int i=0; i<k; i++){
            int i_col=i*d;// #
            for (int t=0; t<T; t++){
                double p_ti=p[t][i];
                double[] xt = x[t];
                if((float)p_ti!=0.f){
                    S0[i]+=p_ti;
                    for(int j=0; j<d; j++){
                        double temp=p_ti*xt[j];
                        S1[i_col+j]+=temp;
                        S2[i_col+j]+=temp*xt[j];
                    }
                }
            }
            double sqrt_wi_T=Math.sqrt(w[i])*T;
            double sqrt_2wi_T=Math.sqrt(2)*sqrt_wi_T;
            
            double  fvW_i=(S0[i]-T*w[i])/sqrt_wi_T;
        
            if(power_norm)
                fvW_i= Math.signum(fvW_i)*Math.sqrt(Math.abs(fvW_i));
            
            l2sum+=fvW_i*fvW_i;
            fvW[i]=fvW_i;
            
            for(int j=0; j<d; j++){
                int h=i_col+j;
                double mu_h=mu[h];
                double sigma_h=sigma[h];
                double fvS_h=(S2[h]-2*mu_h*S1[h]+(mu_h*mu_h-sigma_h)*S0[i])/(sqrt_2wi_T*sigma_h);
          
                if(power_norm)
                    fvS_h= Math.signum(fvS_h)*Math.sqrt(Math.abs(fvS_h));
                
                l2sum+=fvS_h*fvS_h;
                fvS[h]=fvS_h;
            }
        }
        
        int fv_dim=k+k*d;
        double[] fv=new double[fv_dim];
        
        //l2 normalization
        if(l2sum==0){
            for(int i=0; i<fv_dim; i++)
                fv[i]=0;
        }
        else{
            l2sum= Math.sqrt(l2sum);
            for(int i=0; i<k; i++)
                fv[i]=fvW[i]/l2sum;
            
            for(int i=0; i<k*d; i++)
                fv[k+i]=fvS[i]/l2sum;
            
        }
        
        return new FV(fv);
        
    }
    
   public static final   FV getFV_ms(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
        
        int d=gmm.getD();
        if(d!=x[0].length)
            throw new Exception("Gmm and descriptors dimensions are not consistent");
        int k=gmm.getK();
        double[] w=gmm.getW();//dimesion k
        double[] mu=gmm.getMu();//dimesion k*d
        double[] sigma=gmm.getSigma();//dimesion k*d
        
        int T=x.length; //sample size
        
        double[][] p= gmm.compute_p(x);        
        
        double[] fvM=new double[k*d];
        double[] fvS=new double[k*d];
        
        double[] S0= new double[k];
        double[] S1= new double[d*k];
        double[] S2= new double[d*k];
        
        double l2sum=0;
        
        for(int i=0; i<k; i++){
            int i_col=i*d;
            for (int t=0; t<T; t++){
            	double[] xt = x[t];
                double p_ti=p[t][i];
                if((float)p_ti!=0.f){
                    S0[i]+=p_ti;
                    for(int j=0; j<d; j++){
                        double temp=p_ti*xt[j];
                        S1[i_col+j]+=temp;
                        S2[i_col+j]+=temp*xt[j];
                    }
                }
            }
            double sqrt_wi_T=T*Math.sqrt(w[i]);
            double sqrt_2wi_T=Math.sqrt(2)*sqrt_wi_T;
            
            
            for(int j=0; j<d; j++){
                int h=i_col+j;
                double mu_h=mu[h];
                double sigma_h=sigma[h];
                double fvM_h=(S1[h]-S0[i]*mu_h)/(Math.sqrt(sigma_h)*sqrt_wi_T);
                double fvS_h=(S2[h]-2*mu_h*S1[h]+(mu_h*mu_h-sigma_h)*S0[i])/(sqrt_2wi_T*sigma_h);
                if(power_norm){
                    fvM_h= Math.signum(fvM_h)*Math.sqrt(Math.abs(fvM_h));
                    fvS_h= Math.signum(fvS_h)*Math.sqrt(Math.abs(fvS_h));
                }
                
                l2sum+=fvM_h*fvM_h+fvS_h*fvS_h;//full fv
                fvM[h]=fvM_h;
                fvS[h]= fvS_h;
                
            }
        }
        
        int fv_dim=2*k*d;
        double[] fv=new double[fv_dim];
        
        
        //l2 normalization
        
        if(l2sum==0){
            for(int i=0; i<fv_dim; i++)
                fv[i]=0;
        }
        else{
            l2sum= Math.sqrt(l2sum);
            
            for(int i=0; i<k*d; i++){
                fv[i]=fvM[i]/l2sum;
                fv[k*d+i]=fvS[i]/l2sum;
            }
        }

        
        return new FV(fv);
        
   }
   
   public static final   FV getFV_wms(double[][] x, Gmm gmm, boolean power_norm) throws Exception{
       
       int d=gmm.getD();
       if(d!=x[0].length)
           throw new Exception("Gmm and descriptors dimensions are not consistent");
       int k=gmm.getK();
       double[] w=gmm.getW();//dimesion k
       double[] mu=gmm.getMu();//dimesion k*d
       double[] sigma=gmm.getSigma();//dimesion k*d
       
       int T=x.length; //sample size
       
       double[][] p= gmm.compute_p(x);
       
       double[] fvW=new double[k];
       double[] fvM=new double[k*d];
       double[] fvS=new double[k*d];
       
       
       double[] S0= new double[k];
       double[] S1= new double[d*k];
       double[] S2= new double[d*k];
       
       double l2sum=0;
       
       for(int i=0; i<k; i++){
           int i_col=i*d;
           for (int t=0; t<T; t++){
        	   double[] xt = x[t];
               double p_ti=p[t][i];
               if((float)p_ti!=0.f){
                   S0[i]+=p_ti;
                   for(int j=0; j<d; j++){
                       double temp=p_ti*xt[j];
                       S1[i_col+j]+=temp;
                       S2[i_col+j]+=temp*xt[j];
                   }
               }
           }
           double sqrt_wi=Math.sqrt(w[i]);
           double sqrt_wi_T=sqrt_wi*T;
           double sqrt_2wi_T=Math.sqrt(2)*sqrt_wi_T;
           
           double fvW_i=(S0[i]/T-w[i])/sqrt_wi;
           if(power_norm)
               fvW_i= Math.signum(fvW_i)*Math.sqrt(Math.abs(fvW_i));
           
           
            l2sum+=fvW_i*fvW_i;
            fvW[i]=fvW_i;
                    
            for(int j=0; j<d; j++){
                int h=i_col+j;
                 double mu_h=mu[h];
                double sigma_h=sigma[h];
                double fvM_h=(S1[h]-S0[i]*mu_h)/(Math.sqrt(sigma_h)*sqrt_wi_T);
                double fvS_h=(S2[h]-2*mu_h*S1[h]+(mu_h*mu_h-sigma_h)*S0[i])/(sqrt_2wi_T*sigma_h);
                if(power_norm){
                    fvM_h= Math.signum(fvM_h)*Math.sqrt(Math.abs(fvM_h));
                    fvS_h= Math.signum(fvS_h)*Math.sqrt(Math.abs(fvS_h));
                }
                
                l2sum+=fvM_h*fvM_h+fvS_h*fvS_h;
                fvM[h]=fvM_h;
                fvS[h]=fvS_h;
            }
        }
        
        int fv_dim=k+2*k*d;
        double[] fv=new double[fv_dim];

        //l2 normalization
        
        if(l2sum==0){
            for(int i=0; i<fv_dim; i++)
                fv[i]=0;
        }
        else{
            l2sum= Math.sqrt(l2sum);
            for(int i=0; i<k; i++){
                fv[i]=fvW[i]/l2sum;
            }
            for(int i=0; i<k*d; i++){
                fv[k+i]=fvM[i]/l2sum;
                fv[k+k*d+i]=fvS[i]/l2sum;
            }
        }
        
        
        
        return new FV(fv);
        
    }
  
    
    
    
    public static final double getDistance( FV s1, FV s2 ) {
        if ( s1.size() != s2.size() ) return Double.MAX_VALUE;//1.0;
        
        double dist = 0;
        double dis_i;
        double[] v1 = s1.values;
        double[] v2 = s2.values;
        for ( int i=0; i<s1.size(); i++ ) {
            dis_i=v1[i]- v2[i];
            dist += dis_i*dis_i;
        }
        
        return dist;
    }
    

    public static final double getDistance( FV s1, FV s2, double max ) {
        return getDistance(s1, s2);
    }
    
    
}