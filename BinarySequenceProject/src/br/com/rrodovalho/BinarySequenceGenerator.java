package br.com.rrodovalho;

import java.util.ArrayList;
import java.util.List;

public class BinarySequenceGenerator {

	public static void main(String[] args) {
		
		int length = 15;
		binarySequence(length);

	}
	public static List<String> binarySequence(final int length) {
	    final int noOfItems = 1 << length;
	    final List<String> sequences = new ArrayList<>(noOfItems);
	    final String format = "%" + length + "s";
	    for (int num = 0; num < noOfItems; num++) {
	        final String binary = String.format(format,
	                Integer.toBinaryString(num)).replace(' ', '0');
	        char[] string = binary.toCharArray();
	        char[] b = new char[length*2];
	        int pos=0;
	        for(int j=0;j<string.length;j++){
	        	b[pos] = string[j];
	        	b[pos+1]=',';
	        	pos=pos+2;
	        }
	        
	        System.out.println(num+","+getB(b));
	        sequences.add(binary);
	    }
	    return sequences;
	}
	
	public static String getB(char[] b){
		StringBuilder s = new StringBuilder();
		
		for(int i=0;i<b.length-1;i++){
		   s.append(b[i]);
		}
		
		return s.toString();
	}
}
