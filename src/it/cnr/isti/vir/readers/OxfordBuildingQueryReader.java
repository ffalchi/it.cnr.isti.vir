package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.IFeaturesCollector;
import it.cnr.isti.vir.features.localfeatures.BoFLF;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.AbstractID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

public class OxfordBuildingQueryReader implements IObjectsReader<FeaturesCollectorArr> {
	File[] currFiles = null;
	int currFileIndex = 0;
	static Class idClass = IDString.class;
	HashMap<AbstractID, IFeaturesCollector> ht;
	BufferedReader br = null;
	File dir;
	
	public OxfordBuildingQueryReader(HashMap<AbstractID, IFeaturesCollector> ht) {
		this.ht = ht;
	}
	
	
	public void openDirectory(File dir) throws IOException {
		this.dir=dir;
		currFiles = getFilesInDirectory(dir);

	    currFileIndex = 0;
	    if ( br != null ) {
	    	br.close();
	    }
	    br = null;
	}
	
	protected static final File[] getFilesInDirectory(File dir) {
	    
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.endsWith("_query.txt");
	        }
	    };		
	    return dir.listFiles(filter);
	}
	
	
	public final FeaturesCollectorArr getObj() throws Exception {
		FeaturesCollectorArr fc = null;

			
		if ( currFiles.length == 0 || currFileIndex == currFiles.length) return null;
		if (currFiles != null && currFileIndex<currFiles.length) {
			File currFile = currFiles[currFileIndex++];
			if ( br != null) br.close();
			String filename = currFile.getName();
			//System.out.println(filename);
			String ext = (filename.lastIndexOf(".")==-1)?"":filename.substring(filename.lastIndexOf(".")+1,filename.length());
			if ( ext.equals("gz")) {
				//System.out.println("zipped file");
				br = new BufferedReader(
						new InputStreamReader(
								new GZIPInputStream(
										new FileInputStream(currFile)) ));
			}
			else br = new BufferedReader( new FileReader(currFile));
			
			fc = getObj(br);
			br.close();
			br = null;
			if ( fc != null && fc.id == null ) {					
				// also remove "oxc1_"
				fc.id = (AbstractID) idClass.getConstructor(String.class).newInstance(filename.substring(0, filename.indexOf('.')));
			}
			if ( fc != null ) return fc;
			System.err.println("Null objects in " + currFiles[currFileIndex].getAbsolutePath());
		}
		
		return null;
		
	}
	
	public FeaturesCollectorArr getObj(BufferedReader br) throws Exception {
		
	    String[] temp = br.readLine().split("(\\s)+");
	    
	    AbstractID dataID = new IDString( temp[0].substring(5) );	    
	    IFeaturesCollector dataFC = ht.get(dataID);
	    BoFLFGroup dataGroup = (BoFLFGroup) dataFC.getFeature(BoFLFGroup.class);
	    
	    
	    float xMin = Float.parseFloat(temp[1]);
	    float yMin = Float.parseFloat(temp[2]);
	    float xMax = Float.parseFloat(temp[3]);
	    float yMax = Float.parseFloat(temp[4]);	
		
		FeaturesCollectorArr fc = null;

		BoFLFGroup group = null;
		ArrayList list = dataGroup.getLF(xMin, yMin, xMax, yMax);
		BoFLF[] arr = new BoFLF[list.size()]; 
		list.toArray(arr);
		group = new BoFLFGroup(arr, fc, null);
		for ( int i=0; i<arr.length; i++) {
			arr[i] = new BoFLF(arr[i], group); // relinking
		}
		fc = new FeaturesCollectorArr( group, null, null );
		return fc;
	}

	@Override
	public void open(BufferedReader br) {
		this.br = br;
		
	}
	
	public HashSet<AbstractID> getPositiveResults(AbstractID qID) throws FileNotFoundException, IOException{
		HashSet<AbstractID> hs = new HashSet();
		String sID = qID.toString();
		String okFileName = sID.substring(0, sID.length()-6) + "_ok.txt";
		String goodFileName = sID.substring(0, sID.length()-6) + "_good.txt";
		BufferedReader br = new BufferedReader( new FileReader(dir.getCanonicalPath()+ File.separator + okFileName));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			if ( strLine.length() > 0 ) {
				hs.add( new IDString(strLine));
			}
		}
		br = new BufferedReader( new FileReader(dir.getCanonicalPath()+ File.separator + goodFileName));
		while ((strLine = br.readLine()) != null)   {
			if ( strLine.length() > 0 ) {
				hs.add( new IDString(strLine));
			}
		}
		return hs;
	}
	
	public HashSet<AbstractID> getAmbiguousResults(AbstractID qID) throws FileNotFoundException, IOException{
		HashSet<AbstractID> hs = new HashSet();
		String sID = qID.toString();
		String ambFileName = sID.substring(0, sID.length()-6) + "_junk.txt";
		BufferedReader br = new BufferedReader( new FileReader(dir.getCanonicalPath()+ File.separator +  ambFileName));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			if ( strLine.length() > 0 ) {
				hs.add( new IDString(strLine));
			}
		}
		return hs;
	}	
}
