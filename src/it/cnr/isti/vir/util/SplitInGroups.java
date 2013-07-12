package it.cnr.isti.vir.util;

import java.util.Arrays;

public class SplitInGroups {

	
	public static int[] split(int n, int nGroup) {
		
		int min = n/nGroup;
		int[] group = new int[nGroup];
		Arrays.fill(group, min);
		
		int rest = n%nGroup;
		for ( int i=0; i<rest; i++) {
			group[i]++;
		}
		
		return group;
	}
}
