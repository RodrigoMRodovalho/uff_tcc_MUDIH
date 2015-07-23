package br.com.uff.tcc.rrodovalho.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.uff.tcc.rrodovalho.core.DivisiveHierarchicalClustering;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;

public class DivisiveHierarchicalClusteringTest {

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException {
		
		String arffPath = "/media/rrodovalho/34DA291821DB8E46/workspace_TCC_DEV/tcc_development/mulan/data/emotionsLess - 6_.arff";
		
		String xmlPath = "/media/rrodovalho/34DA291821DB8E46/workspace_TCC_DEV/tcc_development/mulan/data/emotions.xml";
				
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);
		
		System.out.println("CARDINALITY "+mInstances.getCardinality());
		
		DivisiveHierarchicalClustering method = new DivisiveHierarchicalClustering(mInstances);
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		System.out.println("Started at "+currentTimestamp.toString());
		method.build();
		currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		System.out.println("\n\n\nFinished at "+currentTimestamp.toString());
	}

}
