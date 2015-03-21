package br.com.uff.tcc.rrodovalho;

import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;

public class DivisiveHierarchicalClusteringTest {

	public static void main(String[] args) throws InvalidDataFormatException {
		
		String arffPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotionsLess.arff";
		
		String xmlPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.xml";
				
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);
		
		DivisiveHierarchicalClustering method = new DivisiveHierarchicalClustering(mInstances);
		
		method.build();

	}

}
