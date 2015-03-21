package br.com.uff.tcc.rrodovalho;

import java.util.ArrayList;
import java.util.Iterator;

import weka.core.Instance;
import weka.core.Instances;
import mulan.data.MultiLabelInstances;

public class DivisiveHierarchicalClustering {
	private MultiLabelInstances mInstances;
	
	public DivisiveHierarchicalClustering(MultiLabelInstances mInstances){
		this.mInstances = mInstances;
	}
	
	public ArrayList<Element> getElementsArray(){
		
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
	    
	    return elements;
	}
	
	public void build(){
		
		int j=1;
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		ArrayList<Integer> whoOut;
		Cluster root = new Cluster();
		root.setElements(getElementsArray());
		clusterList.add(root);
		SimilarityMatrixElement [][] sMatrix;
		Cluster clus;
		double[] biggestAverageAndID;
		
		do{
			printClusterList(clusterList);
			clus = getBiggestCluster(clusterList);
			sMatrix = getSimilarityMatrix(clus);
			biggestAverageAndID = getBiggestAverageSimilarity(sMatrix);
			Cluster	clus2 = new Cluster();
			whoOut = new ArrayList<Integer>();
			while(biggestAverageAndID[0]>0){
                Element elementRemoved = clus.removeElement((int)biggestAverageAndID[1]);
				System.out.println("REMOVED ELEMENT ID  "+elementRemoved.getId());
				clus.printCluster();
				clus2.addElement(elementRemoved);
				clus2.printCluster();
				whoOut.add((int)biggestAverageAndID[1]);
				System.out.println("WHO OUT:  "+whoOut.toString());
				biggestAverageAndID = getSimilaritiesFromNewCluster(sMatrix,whoOut);
			}
			clusterList.add(clus2);
			j++;
		}while(!hasTwoElementsPerCluster(clusterList));	
		System.out.println("\n\n");
		printClusterList(clusterList);
	}
	
	
	public void printClusterList(ArrayList<Cluster> clusterList){
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
	
	
	
	public Cluster getBiggestCluster(ArrayList<Cluster> clusters){
		
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
	
	public SimilarityMatrixElement[][] getSimilarityMatrix(Cluster cluster){
		
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
	
	public double[] getBiggestAverageSimilarity(SimilarityMatrixElement[][] m){
		
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
	
public  double[] getSimilaritiesFromNewCluster(SimilarityMatrixElement[][] m,ArrayList<Integer>whoOut){
		
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
	
	public boolean hasTwoElementsPerCluster(ArrayList<Cluster> elements){
		
		for(int i=0;i<elements.size();i++){
			if(elements.get(i).getElements().size()>2){
				return false;
			}
		}
		return true;
	}

	public double getEuclideanDistance(int [] l1,int[] l2){
		
		int lenght = l1.length;
		int sum = 0;
		for(int i=0;i<lenght;i++){
			sum += Math.pow(l1[i] - l2[i],2);
		}
		return Math.sqrt(sum);
	}
	
	public  void printMatrix(SimilarityMatrixElement[][] m){
		int dim = m.length;
		for(int i=0;i<dim;i++){
			for(int j=0;j<dim;j++){
				m[i][j].printSimiliarityElement();
			}
			System.out.println();
		}
	}
	
}
