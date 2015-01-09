package it.cnr.isti.vir.util.string;

public class ToString {

	static final String newLine = System.getProperty("line.separator");
	
	public static String getString(double[][] m) {
		StringBuilder builder= new StringBuilder();
		
		builder.append( "[" + m.length + ",");
		if ( m.length == 0 )
			builder.append( "null" );
		else 
			builder.append( m[0].length );
			builder.append( "] {" + newLine );
		for ( int ir=0; ir<m.length; ir++) {
			double[] r = m[ir];
			builder.append( " " );
			for ( int ic=0; ic<r.length; ic++) {
				if ( ic!=0 ) builder.append( "\t" );
				builder.append( r[ic] );				
			}
			builder.append( newLine );
		}
		builder.append( "}" + newLine );
		
		return builder.toString();
	}
	
	public static String getString(double[] m) {
		StringBuilder builder= new StringBuilder();
		builder.append( "[" + m.length + "] { " );
		
		for ( int ic=0; ic<m.length; ic++) {
			if ( ic!=0 ) builder.append( "\t" );
			builder.append( m[ic] );				
		}
		builder.append( " }\n" );
		
		return builder.toString();
	}
	
}
