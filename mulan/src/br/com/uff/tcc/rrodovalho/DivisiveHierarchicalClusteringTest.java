package br.com.uff.tcc.rrodovalho;

import java.sql.Timestamp;
import java.util.Calendar;

import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;

public class DivisiveHierarchicalClusteringTest {

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException {
		
		String arffPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotionsLess.arff";
		
		String xmlPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.xml";
				
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);
		
		System.out.println("CARDINALITY "+mInstances.getCardinality());
		
		DivisiveHierarchicalClustering method = new DivisiveHierarchicalClustering(mInstances);
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		System.out.println("Started at "+currentTimestamp.toString());
		method.build();
		currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		System.out.println("Finished at "+currentTimestamp.toString());
	}

}
