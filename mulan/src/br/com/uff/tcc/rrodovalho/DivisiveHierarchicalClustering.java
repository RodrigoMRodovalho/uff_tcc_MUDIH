package br.com.uff.tcc.rrodovalho;

import java.util.ArrayList;
import java.util.Iterator;

import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.Instances;


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
		
		ArrayList<ArrayList<Cluster>> arrayArrayCluster = new ArrayList<ArrayList<Cluster>>();
		ArrayList<Cluster> aux;
		int j=0;
		int id=1;
		ArrayList<Cluster> clusterList = new ArrayList<Cluster>();
		ArrayList<Integer> whoOut;
		Cluster root = new Cluster();
		root.setElements(getElementsArray());
		root.setId(id);
		root.setFather_id(-1);
		System.out.println("ROOT SIZE "+root.getElements().size());
		aux = new ArrayList<Cluster>();
		//Cluster root_clone = (Cluster) Cluster.clone(root);
		//System.out.println("ROOT SIZE CLONE "+root_clone.getElements().size());
		aux.add(j,root);
		arrayArrayCluster.add(j,aux);
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
			//System.out.println("Cluster Cardinality "+getCardinalityByCluster(clus));
			//System.out.println("Cardinality "+getCardinalityByCluster(clus));
			while(biggestAverageAndID[0]>0){
				
				 Element elementRemoved = clus.removeElement((int)biggestAverageAndID[1]);
				//System.out.println("REMOVED ELEMENT ID  "+elementRemoved.getId());
				//clus.printCluster();
                //System.out.println("Cardinality "+getCardinalityByCluster(clus));
                
                //clus2.addElement(new Element(elementRemoved.getId(),elementRemoved.getInstace(),elementRemoved.getLabelsArray()));
                clus2.addElement(elementRemoved);
                
                //ArrayList<Cluster> aux2 = new ArrayList<Cluster>();
                //int clus_ID = clus.getId();              
//                Cluster clus_clone = (Cluster)Cluster.clone(clus);
//                Cluster clus2_clone = (Cluster)Cluster.clone(clus2);
//                id++;
//                clus_clone.setId(id);
//                clus_clone.setElements(clus.getElements());
//                clus_clone.setFather_id(clus_ID);
//                id++;
//                clus2_clone.setId(id);
//                
//                clus2_clone.setFather_id(clus_ID);
//                clus2_clone.setElements(clus2.getElements());
                //aux2.add(new Cluster(clus.getId(),clus.getFather_id(),clus.getElements()));
                //aux2.add(new Cluster(clus2.getId(),clus2.getFather_id(),clus2.getElements()));
                //j++;
                //arrayArrayCluster.add(j,aux2);
                //System.out.println("Cardinality "+getCardinalityByCluster(clus2));
				//clus2.printCluster();
                //System.out.println("Cluster Cardinality "+getCardinalityByCluster(clus2));
				whoOut.add((int)biggestAverageAndID[1]);
				//System.out.println("WHO OUT:  "+whoOut.toString());
				biggestAverageAndID = getSimilaritiesFromNewCluster(sMatrix,whoOut);
//				aux = new ArrayList<Cluster>();
//				aux.add(clus);
//				aux.add(clus2);
//				arrayArrayCluster.add(aux);
				printClusterList(clusterList);
			}
			//clusterList.add(new Cluster(clus2.getId(),clus2.getFather_id(),clus2.getElements()));
			clusterList.add(clus2);
			//arrayArrayCluster.add(clusterList);
			//printCardinalitiesByClusterList(clusterList);
		}while(!hasXElementsPerCluster(clusterList,2));
		//x(arrayArrayCluster);
		System.out.println(arrayArrayCluster.size());
		//printClusterList(clusterList);
		divideToUniqueElementsPerCluster(clusterList);
		System.out.println("\n\n");
		printClusterList(clusterList);
	}
	
	public void x(ArrayList<ArrayList<Cluster>> c){
		
		for(int i=0;i<c.size();i++){
			//printCardinalitiesByClusterList(c.get(i));
		   System.out.println("INDEX : "+i);
			ArrayList<Cluster> a = c.get(i);
		    for(int j=0;j<a.size();j++){
		    	a.get(j).printCluster();
		    }
		}
	}
	
	public void printCardinalitiesByClusterList(ArrayList<Cluster> clusterList){
		
		for(int i=0;i<clusterList.size();i++){
			System.out.println("Index "+i+": "+getCardinalityByCluster(clusterList.get(i)));
		}
		
	}
	
	
	public int getNumOfLabels(int[] labels){
		
		int tam = labels.length;
		int sum=0;
		for(int i=0;i<tam;i++){
			if(labels[i]==1){
				sum++;
			}
		}
		return sum;
	}
	
	public double getCardinalityByCluster(Cluster cluster) {
		
		ArrayList<Element> elements = cluster.getElements();
		double tam = elements.size();
		double sum=0;
		double cardinality=0;
		for(int i=0;i<tam;i++){
			sum+= getNumOfLabels(elements.get(i).getLabelsArray());
		}
		cardinality = sum/tam;
		
		return cardinality;
		
		
		
//		//LabelsMetaData labelsMetaData = this.mInstances.getLabelsMetaData();
//		
//		String xmlPath = "D:\\workspace_TCC_DEV\\tcc_development\\mulan\\data\\emotions.xml";
//		
//		Instance instance = cluster.getElements().get(0).getInstace();
//		
//		System.out.println("Instance "+instance);
//		System.out.println("Cluster Size "+cluster.getElements().size());
//		for(int i=1;i<cluster.getElements().size();i++){
//			instance = instance.mergeInstance(cluster.getElements().get(i).getInstace());
//		}
//		System.out.println("Instance Merged "+instance);
//				
//		Instances instances = new Instances(instance.dataset());
//			
//		MultiLabelInstances mInstances = null;
//		try {
////			mInstances = new MultiLabelInstances(instances, labelsMetaData);
//			mInstances = new MultiLabelInstances(instances, xmlPath);
//			System.out.println("Numero de instancias "+mInstances.getNumInstances());
//		
//		} catch (InvalidDataFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//			
//		return mInstances.getCardinality();
	}
	
	public void divideToUniqueElementsPerCluster(ArrayList<Cluster> clusterList){
		
		Cluster cluster;
		SimilarityMatrixElement[][] sMatrix;
		int index = 0;
		double similarityAverage;
		double maxSimilarityAverage = -999999999;
		do{
			for(int i=0;i<clusterList.size();i++){
				cluster = clusterList.get(i);
				if(cluster.getElements().size()>1){
					sMatrix = getSimilarityMatrix(cluster);
				    similarityAverage = getBiggestAverageSimilarity(sMatrix)[0];
					if(similarityAverage>maxSimilarityAverage){
						index = i;
					}
				}
			}
			
			Element element = clusterList.get(index).removeElement(clusterList.get(index).getElements().get(0).getId());
			Cluster c = new Cluster();
			c.addElement(element);
			clusterList.add(c);
			
	   }while(!hasXElementsPerCluster(clusterList,1));
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
		//System.out.println("BIGGEST CLUSTER");
		//cluster.printCluster();
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
		//printMatrix(m);
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
			//System.out.printf("AVERAGE %.2f\n",average);
			if(average>=maxAverage){
				maxAverage = average;
				index=id;
			}
		}
		bAverageVector[0] = maxAverage;
		bAverageVector[1] = index;
		//System.out.printf("Maior similaridade %.2f\n",maxAverage);
		//System.out.println("Sai do grupo o elemento: "+((int)bAverageVector[1]));
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
				
				//System.out.printf("Elemento %d  , Si =  %.2f   , Sa = %.2f , diff = %.2f\n",m[i][0].getId(),(siAverage/((dim-1)-numberOfOutElements)),(saAverage/(numberOfOutElements)),diff);
				if(maxDiff<=diff){
					maxDiff = diff;
					index = m[i][0].getId();
				}
		     }
		}
		//System.out.printf("Elemento %d com MAX %.2f\n",index,maxDiff);
		maxDiffVector[0] = maxDiff;
		maxDiffVector[1] = index;
		return maxDiffVector;
	}
	
	public boolean hasXElementsPerCluster(ArrayList<Cluster> elements,int number){
		
		for(int i=0;i<elements.size();i++){
			if(elements.get(i).getElements().size()>number){
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
