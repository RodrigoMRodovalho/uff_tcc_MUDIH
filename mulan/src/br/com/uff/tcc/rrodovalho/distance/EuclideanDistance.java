package br.com.uff.tcc.rrodovalho.distance;

import br.com.uff.tcc.rrodovalho.domain.Label;

public class EuclideanDistance {
	
	public static double getDistance(Label l1,Label l2){
		int length = l1.getClassificationArray().length;
		int sum = 0;
		for(int i=0;i<length;i++){
			sum += Math.pow(l1.getClassificationArray()[i] - l2.getClassificationArray()[i],2);
		}
		return Math.sqrt(sum);
	}
	
}
