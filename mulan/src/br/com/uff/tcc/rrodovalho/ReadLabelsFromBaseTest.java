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
import weka.datagenerators.ClusterDefinition;
import weka.gui.beans.Clusterer;

public class ReadLabelsFromBaseTest {

	static Map<Integer,Instance> instanceMapping = new HashMap<Integer,Instance>();
	static ArrayList<int []> labelClassificationArray = new ArrayList<int []>();
	static ArrayList<Integer> instaceKeyMapping = new ArrayList<Integer>();
	
	public static void main(String[] args) throws InvalidDataFormatException {
		// TODO Auto-generated method stub
		
		String arffPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotionsLess.arff";
		
		String xmlPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.xml";
		
		
		MultiLabelInstances mInstances = new MultiLabelInstances(arffPath,xmlPath);		
		
		int numberOfLabels = mInstances.getNumLabels();
		int [] indexOfLabels = mInstances.getLabelIndices();
	    
//		Map<Integer,Instance> instanceMapping = new HashMap<Integer,Instance>();
//		ArrayList<int []> labelClassificationArray = new ArrayList<int []>();
//		ArrayList<Integer> instaceKeyMapping = new ArrayList<Integer>();
		
		Instances instances = mInstances.getDataSet();
		
		//System.out.println("N instances "+mInstances.getNumInstances());
		
//		int id=0;
//		double[] aux = null;
//		int classificationLabelsArray[] = null;
//	    for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
//			Instance instance = (Instance) iterator.next();
//			aux = instance.toDoubleArray();
//			classificationLabelsArray = new int[indexOfLabels.length];
//			for(int i=0;i<indexOfLabels.length;i++){
//				classificationLabelsArray[i] = (int) aux[indexOfLabels[i]];
//			}
//			labelClassificationArray.add(classificationLabelsArray);
//			instanceMapping.put(id, instance);
//			instaceKeyMapping.add(id);
//			id++;
//		}
		
		ArrayList<Element> elements = new ArrayList<Element>();
		
		int id=0;
		double[] aux = null;
		int classificationLabelsArray[] = null;
	    for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			aux = instance.toDoubleArray();
			classificationLabelsArray = new int[indexOfLabels.length];
			for(int i=0;i<indexOfLabels.length;i++){
				classificationLabelsArray[i] = (int) aux[indexOfLabels[i]];
				//System.out.println(classificationLabelsArray[i]);
			}
			
			elements.add(new Element(id,instance,classificationLabelsArray));
			id++;
		}
		
	    
//		int dimension = mInstances.getNumInstances();
//		
//		double[][] similarityMatrix = new double[dimension][dimension];
//		
//		for(int i=0;i<dimension;i++){
//			for(int j=0;j<dimension;j++){
//				if(i!=j){
//					similarityMatrix[i][j] = getEuclideanDistance(labelClassificationArray.get(i), labelClassificationArray.get(j));
//				}
//				else{
//					similarityMatrix[i][j] = 0;
//				}
//			}
//		}
		
//		DecimalFormat df = new DecimalFormat("##.##");
//		for(int i=0;i<dimension;i++){
//			for(int j=0;j<dimension;j++){
//				System.out.print(df.format(similarityMatrix[i][j])+" ");
//			}
//			System.out.println();
//		}

		//------------------------------------------------------------
		
//		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
//		ArrayList<Integer> whoOut = new ArrayList<Integer>();
//		id=0;
//		Cluster root = new Cluster();
//		root.setId(id);
//		id++;
//		root.setElements(instaceKeyMapping);
//		clusterList.add(root);
//		int j=1;
//		double[][] sMatrix;// = new double[dimension][dimension];
//		Cluster clus,clus2=null;
//		double[] biggestAverageAndID;
//		//double[] similarities;
//		do{
//			
//			clus = getBiggerCluster(clusterList);
//			//sMatrix = getSimilarityMatrix(clus);
//			sMatrix = getSimilarityMatrix();
//			biggestAverageAndID = getBiggerAverageSimilarity(sMatrix);
//			clus2 = null;
//			while(biggestAverageAndID[0]>0){
//				
//				clus.removeElement((int)biggestAverageAndID[1]);
//				if(clus2==null){
//					clus2 = new Cluster();
//				}
//				clus2.setId(id);
//				id++;
//				clus2.addElement((int)biggestAverageAndID[1]);
//				whoOut.add((int)biggestAverageAndID[1]);
//				biggestAverageAndID[0] = getSimilaritiesFromNewCluster(sMatrix,whoOut);
//			}
//			
//			j++;
//			
//		}while(!hasTwoElementsPerCluster(clusterList));
		
		
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		ArrayList<Integer> whoOut = new ArrayList<Integer>();
		id=0;
		Cluster root = new Cluster();
		root.setId(id);
		id++;
		root.setElements(elements);
		clusterList.add(root);
		int j=1;
		//double[][] sMatrix;// = new double[dimension][dimension];
		SimilarityMatrixElement [][] sMatrix;
		Cluster clus;
		double[] biggestAverageAndID;
		//double[] similarities;
		do{
			printClusterList(clusterList);
			clus = getBiggestCluster(clusterList);
			sMatrix = getSimilarityMatrix(clus);
			//sMatrix = getSimilarityMatrix();
			biggestAverageAndID = getBiggestAverageSimilarity(sMatrix);
			while(biggestAverageAndID[0]>0){
				
				Element elementRemoved = clus.removeElement((int)biggestAverageAndID[1]);
				addOrUpdateClusterList(clusterList, clus);
				Cluster	clus2 = new Cluster();
				clus2.setId(id);
				id++;
				clus2.addElement(elementRemoved);
				addOrUpdateClusterList(clusterList, clus2);
				whoOut.add((int)biggestAverageAndID[1]);
				biggestAverageAndID[0] = getSimilaritiesFromNewCluster(sMatrix,whoOut);
				printClusterList(clusterList);
			}
			
			j++;
			
		}while(!hasTwoElementsPerCluster(clusterList));	
		
		
		
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
	
	public static Cluster getBiggestCluster(ArrayList<Cluster> clusters){
		
		int i=0,max=0;
		Cluster cluster = clusters.get(0);
		
		for(i=0;i<clusters.size();i++){
			if(clusters.get(i).getElements().size()>max){
				max = clusters.get(i).getElements().size();
				cluster = clusters.get(i);
			}
		}
		
		return cluster;
	}
	
	public static boolean hasTwoElementsPerCluster(ArrayList<Cluster> elements){
		
		for(int i=0;i<elements.size();i++){
			if(elements.get(i).getElements().size()>2){
				return true;
			}
		}
		return false;
	}
	
//	public static double[][] getSimilarityMatrix(Cluster cluster){
//		
//		int dim = cluster.getElements().size();
//		double[][] m = new double[dim][dim];
//		
//		for(int i=0;i<dim;i++){
//			for(int j=0;j<dim;j++){
//				if(i!=j){
//					m[i][j] = getEuclideanDistance(cluster.getElements().get(i).getLabelsArray(),cluster.getElements().get(j).getLabelsArray() );
//				}
//				else{
//					m[i][j] = 0;
//				}
//			}
//		}
//		return m;
//	}
	
	public static SimilarityMatrixElement[][] getSimilarityMatrix(Cluster cluster){
		
		int dim = cluster.getElements().size();
		SimilarityMatrixElement[][] m = new SimilarityMatrixElement[dim][dim];
		
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				if(i!=j){
					m[i][j] = new SimilarityMatrixElement(cluster.getElements().get(i).getId(),getEuclideanDistance(cluster.getElements().get(i).getLabelsArray(),cluster.getElements().get(j).getLabelsArray()));
				}
				else{
					m[i][j] = new SimilarityMatrixElement(cluster.getElements().get(i).getId(),0.0);
					
				}
			}
		}
		return m;
	}
	
//	public static double[][] getSimilarityMatrix(){
//		
//		
//		double[][] m = {
//				{ 0.0, 4.47, 4.0, 2.0, 2.24,2.83 },
//                { 4.47, 0.0, 2.0, 4.0, 2.24,7.21 },
//                { 4.0, 2.0, 0.0, 4.47, 2.24,6.32 },
//                { 2.0, 4.0, 4.47, 0.0, 2.24, 4.47 },
//                { 2.24, 2.24, 2.24, 2.24, 0.0,5.0 },
//                { 2.83, 7.21, 6.32, 4.47, 5.0,0.0 },
//            };
//		
//		return m;
//	}
	
	public static SimilarityMatrixElement[][] getSimilarityMatrix(){
		
		
		SimilarityMatrixElement[][] m = {
				{ new SimilarityMatrixElement(0,0.0), new SimilarityMatrixElement(0,4.47),new SimilarityMatrixElement(0,4.0),new SimilarityMatrixElement(0, 2.0),new SimilarityMatrixElement(0,2.24) ,new SimilarityMatrixElement(0,2.83) },
                { new SimilarityMatrixElement(1,4.47), new SimilarityMatrixElement(1,0.0),new SimilarityMatrixElement(1,2.0),new SimilarityMatrixElement(1, 4.0),new SimilarityMatrixElement(1,2.24) ,new SimilarityMatrixElement(1,7.21) },
                { new SimilarityMatrixElement(2,4.0), new SimilarityMatrixElement(2,2.0),new SimilarityMatrixElement(2,0.0),new SimilarityMatrixElement(2, 4.47),new SimilarityMatrixElement(2,2.24) ,new SimilarityMatrixElement(2,6.32) },
                { new SimilarityMatrixElement(3,2.0), new SimilarityMatrixElement(3,4.0),new SimilarityMatrixElement(3,4.47),new SimilarityMatrixElement(3, 0.0),new SimilarityMatrixElement(3,2.24) ,new SimilarityMatrixElement(3,4.47) },
                { new SimilarityMatrixElement(4,2.24), new SimilarityMatrixElement(4,2.24),new SimilarityMatrixElement(4,2.24),new SimilarityMatrixElement(4, 2.24),new SimilarityMatrixElement(4,0.0) ,new SimilarityMatrixElement(4,5.0) },
                { new SimilarityMatrixElement(5,2.83), new SimilarityMatrixElement(5,7.21),new SimilarityMatrixElement(5,6.32),new SimilarityMatrixElement(5, 4.47),new SimilarityMatrixElement(5,5.0) ,new SimilarityMatrixElement(5,0.0) },
            };
		
		return m;
	}
	
//	public static double[] getBiggestAverageSimilarity(double[][] m){
//		
//		int index=0,dim = m.length;
//		double[] bAverageVector = new double[2];
//		double average=0,sum = 0,maxAverage=-9999999999.0;
//		for(int i=0;i<dim;i++){
//			sum = 0;
//			for(int j=0;j<dim;j++){
//				sum+= m[i][j];
//			}
//			average = sum/(dim-1);
//			System.out.println("AVERAGE "+average);
//			if(average>=maxAverage){
//				maxAverage = average;
//				index=i;
//			}
//		}
//		bAverageVector[0] = maxAverage;
//		bAverageVector[1] = index;
//		System.out.println((int)bAverageVector[1]);
//		return bAverageVector;
//	}
	
	public static double[] getBiggestAverageSimilarity(SimilarityMatrixElement[][] m){
		
		int index=0,id=0,dim = m.length;
		double[] bAverageVector = new double[2];
		double average=0,sum = 0,maxAverage=-9999999999.0;
		for(int i=0;i<dim;i++){
			sum = 0;
			for(int j=0;j<dim;j++){
				sum+= m[i][j].getDistance();
				id=m[i][j].getId();
			}
			average = sum/(dim-1);
			System.out.println("AVERAGE "+average);
			if(average>=maxAverage){
				maxAverage = average;
				index=id;
			}
		}
		bAverageVector[0] = maxAverage;
		bAverageVector[1] = index;
		System.out.println((int)bAverageVector[1]);
		return bAverageVector;
	}
	
	public static boolean belongOutElements(int index,ArrayList<Integer> out){
		
		if(out.contains(index)){
			return true;
		}
		else{
		   return false;	
		}
	}
	
//	public static double getSimilaritiesFromNewCluster(double[][] m,ArrayList<Integer >whoOut){
//		
//		
//		int numberOfOutElements = whoOut.size();
//		int dim = m.length;
//		//TODO finish method
//		double siAverage = 0;
//		double saAverage = 0;
//		double diff = 0;
//		double maxDiff = -99999999;
//		for(int i=0;i<dim;i++){
//			siAverage = 0;
//			saAverage = 0;
//			diff = 0;
//			for(int j=0;j<dim;j++){
//				if(i!=j){
//					if(whoOut.contains(j)){
//						saAverage+=m[i][j];
//					}
//					else{
//						siAverage+=m[i][j];
//					}
//				}
//			}
//			diff = (siAverage/((dim-1)-numberOfOutElements)) - (saAverage/(numberOfOutElements));
//			if(maxDiff<=diff){
//				maxDiff = diff;
//			}
//		}
//		System.out.println("MAX "+maxDiff);
//		
//		return maxDiff;
//	}
	
	public static double getSimilaritiesFromNewCluster(SimilarityMatrixElement[][] m,ArrayList<Integer>whoOut){
		
		
		int numberOfOutElements = whoOut.size();
		int dim = m.length;
		//TODO finish method
		double siAverage = 0;
		double saAverage = 0;
		double diff = 0;
		double maxDiff = -99999999;
		for(int i=0;i<dim;i++){
			siAverage = 0;
			saAverage = 0;
			diff = 0;
			for(int j=0;j<dim;j++){
				if(i!=j){
					if(whoOut.contains(j)){
						saAverage+=m[i][j].getDistance();
					}
					else{
						siAverage+=m[i][j].getDistance();
					}
				}
			}
			diff = (siAverage/((dim-1)-numberOfOutElements)) - (saAverage/(numberOfOutElements));
			if(maxDiff<=diff){
				maxDiff = diff;
			}
		}
		System.out.println("MAX "+maxDiff);
		
		return maxDiff;
	}
	
	public static void printClusterList(ArrayList<Cluster> clusterList){
		System.out.print("[ ");
		for(int i=0;i<clusterList.size();i++){
			System.out.print("( ");
			for(int j=0;j<clusterList.get(i).getElements().size();j++){
				System.out.print(clusterList.get(i).getElements().get(j).getInstace().toString()+"  ");
			}
			System.out.print(") ");
		}
		System.out.print(" ]");
	}
	
	public static void addOrUpdateClusterList(ArrayList<Cluster> clusterList,Cluster cluster){
		
		boolean isUpdate =  false;
		for(int i=0;i<clusterList.size();i++){
			if(clusterList.get(i).getId()==cluster.getId()){
				clusterList.set(i, cluster);
				isUpdate = true;
				break;
			}
		}
		if(!isUpdate){
			clusterList.add(cluster);
		}
		
	}
}
