package br.com.uff.tcc.rrodovalho;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.Instances;

public class ReadLabelsFromBaseTest {

	
	public static void main(String[] args) throws InvalidDataFormatException {
		// TODO Auto-generated method stub
		
		String arffPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.arff";
		
		String xmlPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.xml";
		
		
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);		
		
		int numberOfLabels = mInstances.getNumLabels();
		int [] indexOfLabels = mInstances.getLabelIndices();
	    
		Map<Integer,Instance> instanceMapping = new HashMap<Integer,Instance>();
		ArrayList<int []> labelClassificationArray = new ArrayList<int []>();
		
		Instances instances = mInstances.getDataSet();
		
		int id=0;
		double[] aux = null;
		int classificationLabelsArray[] = null;
	    for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			aux = instance.toDoubleArray();
			classificationLabelsArray = new int[indexOfLabels.length];
			for(int i=0;i<indexOfLabels.length;i++){
				classificationLabelsArray[i] = (int) aux[indexOfLabels[i]];
			}
			labelClassificationArray.add(classificationLabelsArray);
			instanceMapping.put(id, instance);
			id++;
		}
		
		int dimension = numberOfLabels;
		double[][] similarityMatrix = new double[dimension][dimension];
		
		for(int i=0;i<dimension;i++){
			for(int j=0;j<dimension;j++){
				if(i!=j){
					similarityMatrix[i][j] = getEuclideanDistance(labelClassificationArray.get(i), labelClassificationArray.get(j));
				}
				else{
					similarityMatrix[i][j] = 0;
				}
			}
		}
		
		DecimalFormat df = new DecimalFormat("##.##");
		for(int i=0;i<dimension;i++){
			for(int j=0;j<dimension;j++){
				System.out.print(df.format(similarityMatrix[i][j])+" ");
			}
			System.out.println();
		}

		
		
		
		
		
		
		
//		for(int k=0;k<instanceAndLabelsMap.size();k++){
//			instanceAndLabelsMap.keySet().iterator()
//			System.out.println(instanceAndLabelsMap.get(instanceAndLabelsMap.keySet()));
//		}
		
		
//		System.out.println(instances.get(0).toDoubleArray()[73]);
//		
//		System.out.println(mInstances.getFeatureAttributes().size());
//		int s = mInstances.getFeatureIndices()[0];
//		System.out.println(s);
//		
//		int t = mInstances.getLabelIndices()[0];
//		System.out.println(t);
		
	}
	
	public static double getEuclideanDistance(int [] l1,int[] l2){
		
		int lenght = l1.length;
		int sum = 0;
		for(int i=0;i<lenght;i++){
			sum += Math.pow(l1[i] - l2[i],2);
		}
		
		return Math.sqrt(sum);
	}

	public static double getAverageSimilarityFromLine(int line,int [] elements,double[][] similarityMatrix){
		
		int divisor = elements.length;
		for(int i=0;i<similarityMatrix.length;i++){
						
		}
		
		
		return 0;
	}
	
	public static ArrayList<int[]> getArrayOfLabelClassificationFromInstances(){
		
		return null;
	}
	
}
