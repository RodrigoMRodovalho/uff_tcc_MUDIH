package br.com.uff.tcc.rrodovalho;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.uff.tcc.rrodovalho.domain.SimilarityMatrixElement;
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
	    		
		Instances instances = mInstances.getDataSet();
				
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
		
	    	
	    int j=1;
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		ArrayList<Integer> whoOut;// = new ArrayList<Integer>();
		//id=0;
		Cluster root = new Cluster();
		root.setId(j);
		root.setFather_id(j);
		//id++;
		root.setElements(elements);
		clusterList.add(root);
		
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
			Cluster	clus2 = new Cluster();
			whoOut = new ArrayList<Integer>();
			//clus2.setId(id);
			//clus2.setFather_id(clus.getId());
			id++;
			while(biggestAverageAndID[0]>0){
                Element elementRemoved = clus.removeElement((int)biggestAverageAndID[1]);
				System.out.println("REMOVED ELEMENT ID  "+elementRemoved.getId());
				clus.printCluster();
                //addOrUpdateClusterList(clusterList, clus);
				clus2.addElement(elementRemoved);
				clus2.printCluster();
				//addOrUpdateClusterList(clusterList, clus2);
				whoOut.add((int)biggestAverageAndID[1]);
				System.out.println("WHO OUT:  "+whoOut.toString());
				biggestAverageAndID = getSimilaritiesFromNewCluster(sMatrix,whoOut);
				//printClusterList(clusterList);
			}
			clusterList.add(clus2);
			j++;
		}while(!hasTwoElementsPerCluster(clusterList));	
		System.out.println("\n\n");
		printClusterList(clusterList);
	}
	
	public static double getEuclideanDistance(int [] l1,int[] l2){
		
		int lenght = l1.length;
		int sum = 0;
		for(int i=0;i<lenght;i++){
			sum += Math.pow(l1[i] - l2[i],2);
		}
		
		return Math.sqrt(sum);
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
		System.out.println("BIGGEST CLUSTER");
		cluster.printCluster();
		return cluster;
	}
	
	public static boolean hasTwoElementsPerCluster(ArrayList<Cluster> elements){
		
		for(int i=0;i<elements.size();i++){
			if(elements.get(i).getElements().size()>2){
				return false;
			}
		}
		return true;
	}
	
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
		printMatrix(m);
		return m;
	}
	
	public static void printMatrix(SimilarityMatrixElement[][] m){
		int dim = m.length;
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				m[i][j].printSimiliarityElement();
			}
			System.out.println();
		}
	}
	
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
			System.out.printf("AVERAGE %.2f\n",average);
			if(average>=maxAverage){
				maxAverage = average;
				index=id;
			}
		}
		bAverageVector[0] = maxAverage;
		bAverageVector[1] = index;
		System.out.printf("Maior similaridade %.2f\n",maxAverage);
		System.out.println("Sai do grupo o elemento: "+((int)bAverageVector[1]));
		return bAverageVector;
	}
	
	public static double[] getSimilaritiesFromNewCluster(SimilarityMatrixElement[][] m,ArrayList<Integer>whoOut){
		
		double[] maxDiffVector = new double[2];
		int numberOfOutElements = whoOut.size();
		int dim = m.length;
		int index=-1;
		double siAverage = 0;
		double saAverage = 0;
		double diff = 0;
		double maxDiff = -99999999;
		for(int i=0;i<dim;i++){
			if(!whoOut.contains(m[i][0].getId())){
				siAverage = 0;
				saAverage = 0;
				diff = 0;
				for(int j=0;j<dim;j++){
					if(i!=j){
						if(whoOut.contains(m[j][0].getId())){
							saAverage+=m[i][j].getDistance();
						}
						else{
							siAverage+=m[i][j].getDistance();
						}
					}
				}
				diff = (siAverage/((dim-1)-numberOfOutElements)) - (saAverage/(numberOfOutElements));
				
				System.out.printf("Elemento %d  , Si =  %.2f   , Sa = %.2f , diff = %.2f\n",m[i][0].getId(),(siAverage/((dim-1)-numberOfOutElements)),(saAverage/(numberOfOutElements)),diff);
				if(maxDiff<=diff){
					maxDiff = diff;
					index = m[i][0].getId();
				}
		     }
		}
		System.out.printf("Elemento %d com MAX %.2f\n",index,maxDiff);
		maxDiffVector[0] = maxDiff;
		maxDiffVector[1] = index;
		return maxDiffVector;
	}
	
	
//	public static int getIDByIndex(SimilarityMatrixElement[][] m,int j){
//		return m[j][0].getId();
//	}
	
	public static void printClusterList(ArrayList<Cluster> clusterList){
		System.out.println("#Clusters  -- "+clusterList.size());
		System.out.print("[ ");
		for(int i=0;i<clusterList.size();i++){
			System.out.println("#Elements "+clusterList.get(i).getElements().size());
			System.out.print("( ");
			for(int j=0;j<clusterList.get(i).getElements().size();j++){
				System.out.print(clusterList.get(i).getElements().get(j).getInstace().toString()+" , ");
			}
			System.out.println(") ");
		}
		System.out.println("]");
	}
	
}
