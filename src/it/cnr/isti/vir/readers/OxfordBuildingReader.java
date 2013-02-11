package it.cnr.isti.vir.readers;

import it.cnr.isti.vir.features.FeaturesCollectorArr;
import it.cnr.isti.vir.features.localfeatures.BoFLF;
import it.cnr.isti.vir.features.localfeatures.BoFLFGroup;
import it.cnr.isti.vir.id.IDString;
import it.cnr.isti.vir.id.AbstractID;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

public class OxfordBuildingReader implements IObjectsReader<FeaturesCollectorArr> {
	File[] currFiles = null;
	int currFileIndex = 0;
	static Class idClass = IDString.class;
	File dir;
	BufferedReader br = null;
	
	
	public void openDirectory(File dir) throws IOException {
		this.dir = dir;
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
	            return name.endsWith(".txt") || name.endsWith(".gz");
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
			br = new BufferedReader( new FileReader(currFile));
			
			fc = getObj(br);
			br.close();
			br = null;
			if ( fc != null && fc.id == null ) {					
				// also remove "oxc1_"
				fc.id = (AbstractID) idClass.getConstructor(String.class).newInstance(filename.substring(5, filename.indexOf('.')));
			}
			if ( fc != null ) return fc;
			System.err.println("Null objects in " + currFiles[currFileIndex].getAbsolutePath());
		}
		
		return null;
		
	}
	
	public FeaturesCollectorArr getObj(BufferedReader br) throws Exception {
		String line = null;
		
		FeaturesCollectorArr fc = null;
		
		String first = br.readLine();
		String nFeatures = br.readLine();
		BoFLFGroup group = null;
		int n = Integer.parseInt(nFeatures);
		BoFLF[] arr = new BoFLF[n];
		
		for ( int i=0; i<n; i++ ) {
		    String[] temp = br.readLine().split("(\\s)+");
		    int bag = Integer.parseInt(temp[0]) -1 ; // The dataset min word is 1
		    float x = Float.parseFloat(temp[1]);
		    float y = Float.parseFloat(temp[2]);
		    float a = Float.parseFloat(temp[3]);
		    float b = Float.parseFloat(temp[4]);
		    float c = Float.parseFloat(temp[5]);
		    
		    float area = (float) (2*Math.PI/Math.sqrt(4*a*c-b*b));
		    
		    float scale  = (float) Math.sqrt(area);
		    float[] xy = {x, y};
			arr[i] = new BoFLF(bag, xy,  0.0F, scale, group);
		}
		group = new BoFLFGroup(arr, fc, null);
		fc = new FeaturesCollectorArr( group, null, null );
		return fc;
	}

	@Override
	public void open(BufferedReader br) {
		this.br = br;
		
	}

	
}
