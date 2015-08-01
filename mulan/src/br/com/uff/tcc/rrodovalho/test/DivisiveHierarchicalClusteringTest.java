package br.com.uff.tcc.rrodovalho.test;


import br.com.uff.tcc.rrodovalho.core.DivisiveHierarchicalClustering;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;

public class DivisiveHierarchicalClusteringTest {

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException {
		
		String arffPath = "/media/rrodovalho/34DA291821DB8E46/workspace_TCC_DEV/tcc_development/mulan/data/testData/sparseDataSet-test.arff";
		String xmlPath = "/media/rrodovalho/34DA291821DB8E46/workspace_TCC_DEV/tcc_development/mulan/data/testData/sparseDataSet.xml";
				
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);
		
		System.out.println("CARDINALITY "+mInstances.getCardinality());
		
		SimilarityMeasureEnum e[]={SimilarityMeasureEnum.EuclideanDistance,
				SimilarityMeasureEnum.QuadraticEuclideanDistance,
				SimilarityMeasureEnum.ManhattanDistance,
				SimilarityMeasureEnum.ChebychevDistance
		};
		
		String oPath = "/home/rrodovalho/Documents/TCC/";
				
		DivisiveHierarchicalClustering method = new DivisiveHierarchicalClustering();
		method.setGraphOutputImagePath(oPath);
		
		for(int i=0;i<e.length;i++){
			method.build(mInstances,e[i]);
		}
		
	}

}
