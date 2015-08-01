package br.com.uff.tcc.rrodovalho.distance;

import br.com.uff.tcc.rrodovalho.domain.Label;

public class LabelDistance {

	public static double getDistance(Label l1,Label l2,SimilarityMeasureEnum distance){
		
		switch (distance) {
			case EuclideanDistance:
				return euclideanDistance(l1, l2);
			case QuadraticEuclideanDistance:
				return quadraticEuclideanDistance(l1, l2);
			case ManhattanDistance:
				return manhattanDistance(l1, l2);
			case ChebychevDistance:
				return chebychevDistance(l1, l2);
			default:
				return 0;
			}		
	}
	
	private static double euclideanDistance(Label l1,Label l2){
		int length = l1.getClassificationArray().length;
		int sum = 0;
		for(int i=0;i<length;i++){
			sum += Math.pow(l1.getClassificationArray()[i] - l2.getClassificationArray()[i],2);
		}
		return Math.sqrt(sum);
	}
	
	private static double quadraticEuclideanDistance(Label l1,Label l2){
		int length = l1.getClassificationArray().length;
		int sum = 0;
		for(int i=0;i<length;i++){
			sum += Math.pow(l1.getClassificationArray()[i] - l2.getClassificationArray()[i],2);
		}
		return sum;
	}
	
	private static double manhattanDistance(Label l1,Label l2){
		int length = l1.getClassificationArray().length;
		int sum = 0;
		for(int i=0;i<length;i++){
			sum += Math.abs(l1.getClassificationArray()[i] - l2.getClassificationArray()[i]);
		}
		return sum;
	}
	
	private static double chebychevDistance(Label l1,Label l2){
		int length = l1.getClassificationArray().length;
		int aux = 0;
		double max=-99;
		for(int i=0;i<length;i++){
			aux = Math.abs(l1.getClassificationArray()[i] - l2.getClassificationArray()[i]);
			max = Math.max(aux, max);
		}
		return max;
	}
	
	
}
