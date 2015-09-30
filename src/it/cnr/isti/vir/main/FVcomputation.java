package it.cnr.isti.vir.main;

import it.cnr.isti.vir.experiments.Launch;
import it.cnr.isti.vir.features.AbstractFeaturesCollector;
import it.cnr.isti.vir.features.Bmm;
import it.cnr.isti.vir.features.FV;
import it.cnr.isti.vir.features.Gmm;
import it.cnr.isti.vir.features.IArrayValues;
import it.cnr.isti.vir.features.localfeatures.ALocalFeature;
import it.cnr.isti.vir.features.localfeatures.ALocalFeaturesGroup;
import it.cnr.isti.vir.file.FeaturesCollectorsArchive;
import it.cnr.isti.vir.global.Log;
import it.cnr.isti.vir.id.AbstractID;
import it.cnr.isti.vir.id.IHasID;
import it.cnr.isti.vir.util.MatrixConversion;
import it.cnr.isti.vir.util.PropertiesUtils;
import it.cnr.isti.vir.util.TimeManager;

import java.io.File;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class FVcomputation {

	public static final String className = "FVcomputation";
	
	public static void usage() {
		System.out.println("Usage: " + className + "<properties filename>.properties");
		System.out.println();
		System.out.println("Properties file must contain:");
		System.out.println("- "+className+".lfArchive=<input  archive>");
		System.out.println("- "+className+".featureClass=<class of the feature>");
		System.out.println("- "+className+".mixtureModel=<input mixture model directory (GMM or BMM)>");
		System.out.println("- "+className+".fvArchive=<out archive>");
		System.out.println("Properties file optionals:");
		System.out.println("- ["+className+".byteorder=< ByteOrder (BIG_ENDIAN or LITTLE_ENDIAN) used for reading the mixture model, BIG_ENDIAN default>]");
		System.out.println("- ["+className+".wPart=< false default>]");
		System.out.println("- ["+className+".muPart=< true default>]");
		System.out.println("- ["+className+".sigmaPart=< false default>]");
		System.out.println("- ["+className+".powerNorm=< true default>]");
		//TO do PCA
		System.exit(0);
	}
	
	public static void main(String[] args) throws Exception {
		
		if ( args.length == 1 ) {
			usage();
		}

		Launch.launch(FVcomputation.class.getName(), args[0]);	
	
	}
	

	public static void launch(Properties prop) throws Exception {
		
		String mixtureModelFileName = PropertiesUtils.getAbsolutePath(prop, className+".mixtureModel");
		ByteOrder byteorder=ByteOrder.BIG_ENDIAN;
		String bstr = prop.getProperty(className+".byteorder");
		if ( bstr !=null && bstr.startsWith("LITTLE")) {
			Log.info_verbose( className+".byteorder"+ " is set to LITTLE_ENDIAN" );
			byteorder=ByteOrder.LITTLE_ENDIAN;
		}
		else {
			Log.info_verbose( className+".byteorder"+ " is set to BIG_ENDIAN" );
		}
		if(mixtureModelFileName.endsWith("gmm")){
			//procedi con gmm	
			Gmm gmm= new Gmm(mixtureModelFileName,byteorder);
			createFVusingGMM(gmm, prop);
		}
		else {
			if(mixtureModelFileName.endsWith("bmm")){
				//procedi con bmm
				Bmm bmm= new Bmm(mixtureModelFileName,byteorder);
				createFVusingBMM(bmm, prop);
			}
			else
				throw new Exception(className+".mixtureModel file extension is not valid (only .gmm and .bmm are supported)");
		}

	}



		public static void createFVusingGMM(Gmm gmm,Properties prop) throws Exception {
			File lfArchive_file = PropertiesUtils.getFile( prop, className+".lfArchive");
			File fvArchive_file = PropertiesUtils.getFile(prop, className+".fvArchive");
			boolean compute_w_part= PropertiesUtils.getIfExistsDefFalse(prop,className+".wPart" );
			boolean compute_mu_part= PropertiesUtils.getIfExistsDefTrue(prop, className+".muPart");
			boolean compute_sigma_part= PropertiesUtils.getIfExistsDefFalse(prop,className+".sigmaPart" );
			boolean power_norm=PropertiesUtils.getIfExistsDefTrue(prop, className+".powerNorm");
			// Features or Local Features Group class
			Class c = PropertiesUtils.getClass(prop, className+".featureClass");
			
			FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file);
			FeaturesCollectorsArchive outArchive = 
//					FeaturesCollectorsArchive.create(fvArchive_file);
				inArchive.getSameType(fvArchive_file);
		    
	
			TimeManager tm = new TimeManager();
			int  dimlf=gmm.getMu().length/gmm.getK();
		
			if ( ALocalFeature.class.isAssignableFrom(c)  ) {
				Class<? extends ALocalFeaturesGroup> cGroup = ALocalFeaturesGroup.getGroupClass(c);
				Log.info("Group class has been set to: " + cGroup);
				Log.info("Creating: " + fvArchive_file );
				for ( AbstractFeaturesCollector fc : inArchive ) { //empty group case
					AbstractID id= ((IHasID) fc).getID();
					ALocalFeaturesGroup lfGroup = fc.getFeature(cGroup);
					if ( lfGroup == null || lfGroup.size() == 0 ) {
						FV fv=FV.getFV(new double[1][dimlf],gmm,power_norm,compute_w_part, compute_mu_part, compute_sigma_part);
						AbstractFeaturesCollector fcc = fc.createWithSameInfo(fv);
		                outArchive.add(fcc);
		                continue;
					}
					
					ALocalFeature[] lfArr = lfGroup.lfArr;
									
					double [][] lFeatureMatrix=MatrixConversion.getDoubles((Collection<? extends IArrayValues>) Arrays.asList(lfArr));//qualcosa di simile
					
					FV fv=FV.getFV( lFeatureMatrix, gmm, power_norm,  compute_w_part,  compute_mu_part,compute_sigma_part);//compute fisher vector
					//AbstractFeaturesCollector fcc = new FeaturesCollectorArr(fv,id);
					AbstractFeaturesCollector fcc = fc.createWithSameInfo(fv);
	                outArchive.add(fcc);
					
					Log.info_verbose_progress(tm, outArchive.size() , inArchive.size());
				}
				Log.info("Done" );
			
			}
			Log.info(outArchive.getInfo());
	
			Log.info("FVs were created in "+ tm.getTotalTime_STR());
	
			outArchive.close();
	
		}
		
		public static void createFVusingBMM(Bmm bmm,Properties prop) throws Exception {
			System.out.println();
			File lfArchive_file = PropertiesUtils.getFile( prop, className+".lfArchive");
			File fvArchive_file = PropertiesUtils.getFile(prop, className+".fvArchive");
			boolean compute_w_part= PropertiesUtils.getIfExistsDefFalse(prop,className+".wPart" );
			boolean compute_mu_part= PropertiesUtils.getIfExistsDefTrue(prop, className+".muPart");
			boolean power_norm=PropertiesUtils.getIfExistsDefTrue(prop, className+".powerNorm");
			// Features or Local Features Group class
			Class c = PropertiesUtils.getClass(prop, className+".featureClass");
			
			FeaturesCollectorsArchive inArchive = new FeaturesCollectorsArchive(lfArchive_file);
			FeaturesCollectorsArchive outArchive = 
//					FeaturesCollectorsArchive.create(fvArchive_file);
				inArchive.getSameType(fvArchive_file);
		    
	
			TimeManager tm = new TimeManager();
			int  dimlf=bmm.getMu().length/bmm.getK();
			
			if ( ALocalFeature.class.isAssignableFrom(c)  ) {
				Class<? extends ALocalFeaturesGroup> cGroup = ALocalFeaturesGroup.getGroupClass(c);
				Log.info("Group class has been set to: " + cGroup);
				for ( AbstractFeaturesCollector fc : inArchive ) { //empty group case
					AbstractID id= ((IHasID) fc).getID();
					ALocalFeaturesGroup lfGroup = fc.getFeature(cGroup);
					if ( lfGroup == null || lfGroup.size() == 0 ) {
						FV fv=FV.getFV(new double[1][dimlf],bmm,power_norm,compute_w_part, compute_mu_part);
//						AbstractFeaturesCollector fcc = new FeaturesCollectorArr(fv,id);
						AbstractFeaturesCollector fcc = fc.createWithSameInfo(fv);
		                outArchive.add(fcc);
		                continue;
					}
					
					ALocalFeature[] lfArr = lfGroup.lfArr;
									
					double [][] lFeatureMatrix=MatrixConversion.getDoubles((Collection<? extends IArrayValues>) Arrays.asList(lfArr));//qualcosa di simile
					
					FV fv=FV.getFV( lFeatureMatrix, bmm, power_norm,  compute_w_part,  compute_mu_part);//compute fisher vector
//					AbstractFeaturesCollector fcc = new FeaturesCollectorArr(fv,id);
					AbstractFeaturesCollector fcc = fc.createWithSameInfo(fv);
	                outArchive.add(fcc);
					
					Log.info_verbose_progress(tm, outArchive.size() , inArchive.size());
				}

			
			}
			Log.info(outArchive.getInfo());
	
			Log.info("FVs were created in "+ tm.getTotalTime_STR());
	
			outArchive.close();
		}
}
