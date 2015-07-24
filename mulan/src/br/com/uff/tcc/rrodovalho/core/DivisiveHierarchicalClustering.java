package br.com.uff.tcc.rrodovalho.core;

import java.util.ArrayList;
import java.util.Iterator;

import br.com.uff.tcc.rrodovalho.Cluster;
import br.com.uff.tcc.rrodovalho.Element;
import br.com.uff.tcc.rrodovalho.distance.EuclideanDistance;
import br.com.uff.tcc.rrodovalho.domain.ClusterOfLabels;
import br.com.uff.tcc.rrodovalho.domain.Label;
import br.com.uff.tcc.rrodovalho.domain.Measure;
import br.com.uff.tcc.rrodovalho.domain.SimilarityMatrix;
import br.com.uff.tcc.rrodovalho.domain.SimilarityMatrixElement;
import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.Instances;


public class DivisiveHierarchicalClustering {
	
	private MultiLabelInstances mInstances;
	private String[] labelNames;
	private boolean log= false;
	private SimilarityMatrix originalSimilarityMatrixx;
	
	public DivisiveHierarchicalClustering(MultiLabelInstances mInstances){
		this.mInstances = mInstances;
		labelNames = this.mInstances.getLabelNames();
	}
	
	public ArrayList<Label> getLabelsArray(){
		
		int [] indexOfLabels = mInstances.getLabelIndices();
		Instances instances = mInstances.getDataSet();
		int numInstances = mInstances.getNumInstances();
		int instanceIndex =0;
		ArrayList<Label> labels = new ArrayList<Label>();
		double[] aux = null;
		
	 	
		for(int index=0;index<mInstances.getNumLabels();index++){	
			
			labels.add(new Label(index, labelNames[index], new int[numInstances]));
		}
		for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
			Instance instance = (Instance) iterator.next();
			aux = instance.toDoubleArray();
						
			for(int i=0;i<mInstances.getNumLabels();i++){
				labels.get(i).getClassificationArray()[instanceIndex] = (int)aux[indexOfLabels[i]];
			}
			instanceIndex++;
			
		}
		return labels;
	}
	
			
	public void build(){
		
		ArrayList<ArrayList<ClusterOfLabels>> arrayArrayCluster = new ArrayList<ArrayList<ClusterOfLabels>>();
		ArrayList<ClusterOfLabels> aux;
		int j=0;
		int id=1;
		ArrayList<ClusterOfLabels> clusterList = new ArrayList<ClusterOfLabels>();
		ArrayList<Integer> whoOut;
		ClusterOfLabels root = new ClusterOfLabels();
		root.setLabels(getLabelsArray());
		root.setId(id);
		root.setFather_id(-1);
		System.out.println("ROOT SIZE "+root.getLabels().size());
		aux = new ArrayList<ClusterOfLabels>();
		//Cluster root_clone = (Cluster) Cluster.clone(root);
		//System.out.println("ROOT SIZE CLONE "+root_clone.getElements().size());
		aux.add(j,root);
		arrayArrayCluster.add(j,aux);
		clusterList.add(root);
		SimilarityMatrix sMatrix;
		ClusterOfLabels clus;
		//initializeOriginalSimilarityMatrix(root);
		initializeOriginalSimilarityMatrixx(root);
		//originalSimilarityMatrixx.print();
		Measure biggestAverage;
		//double[] biggestAverageAndID;
		printClusterList(clusterList);
		printCardinalitiesByClusterList(clusterList);
		
		do{
			
			//printClusterList(clusterList);
			clus = getBiggestCluster(clusterList);
			sMatrix = getSimilarityMatrixx(clus);
			System.out.println("\n");
			log=false;
			biggestAverage = getBiggestAverageSimilarityy(sMatrix);
			ClusterOfLabels	clus2 = new ClusterOfLabels();
			whoOut = new ArrayList<Integer>();
			
			//System.out.println("Cluster Cardinality "+getCardinalityByCluster(clus));
			//System.out.println("Cardinality "+getCardinalityByCluster(clus));
			while(biggestAverage.getValue()>=0){

				Label labelRemoved = clus.removeLabel(biggestAverage.getID());
				log=false;
				if(log)labelRemoved.print();
				 
				//System.out.println("REMOVED ELEMENT ID  "+elementRemoved.getId());
				//clus.printCluster();
                //System.out.println("Cardinality "+getCardinalityByCluster(clus));
                
                //clus2.addElement(new Element(elementRemoved.getId(),elementRemoved.getInstace(),elementRemoved.getLabelsArray()));
                clus2.addLabel(labelRemoved);
                //clus2.printCluster();
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
				whoOut.add(biggestAverage.getID());
//				System.out.print("\nWHOOUT   ");
//				for(int i=0;i<whoOut.size();i++){
//					System.out.print(whoOut.get(i)+"   ");
//				}
//				System.out.println("\n");
				
				//System.out.println("WHO OUT:  "+whoOut.toString());
				log=false;
				biggestAverage = getSimilaritiesFromNewClusterr(sMatrix,whoOut);
				
//				aux = new ArrayList<Cluster>();
//				aux.add(clus);
//				aux.add(clus2);
//				arrayArrayCluster.add(aux);
				if(clus2.getLabels().size()==1){
					clusterList.add(clus2);
				}
				
				printClusterList(clusterList);
				printCardinalitiesByClusterList(clusterList);
				//printCluster(clus2);
			}
			//clusterList.add(new Cluster(clus2.getId(),clus2.getFather_id(),clus2.getElements()));
			//clusterList.add(clus2);
			//arrayArrayCluster.add(clusterList);
			//printCardinalitiesByClusterList(clusterList);
		}while(!hasXLabelsPerCluster(clusterList,2));
		//x(arrayArrayCluster);
		System.out.println("\nOUT OF THE MAIN WHILE");
		//System.out.println(arrayArrayCluster.size());
		//printClusterList(clusterList);
		log=false;
		divideToUniqueLabelPerCluster(clusterList);
		System.out.println("\n\n");
		printClusterList(clusterList);
		printCardinalitiesByClusterList(clusterList);
	}
		
	public void printCardinalitiesByClusterList(ArrayList<ClusterOfLabels> clusterList){
		System.out.println("CARDINALITY\n");
		for(int i=0;i<clusterList.size();i++){
			System.out.println("Index "+i+": "+getCardinalityByCluster(clusterList.get(i)));
		}
		System.out.println("\n");
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
	
	public double getCardinalityByCluster(ClusterOfLabels cluster) {
		
		ArrayList<Label> labels = cluster.getLabels();
		double tam = labels.size();
		double sum=0;
		double cardinality=0;
		for(int i=0;i<tam;i++){
			sum+= getNumOfLabels(labels.get(i).getClassificationArray());
		}
		cardinality = sum/mInstances.getNumInstances();
		
		return cardinality;
	}
	
		
	public void divideToUniqueLabelPerCluster(ArrayList<ClusterOfLabels> clusterList){
		
		ClusterOfLabels cluster;
		SimilarityMatrix sMatrix;
		int index = 0;
		double similarityAverage;
		double maxSimilarityAverage = -999999999;
		do{
			for(int i=0;i<clusterList.size();i++){
				cluster = clusterList.get(i);
				if(cluster.getLabels().size()>1){
					sMatrix = getSimilarityMatrixx(cluster);
				    similarityAverage = getBiggestAverageSimilarityy(sMatrix).getValue();
					if(similarityAverage>maxSimilarityAverage){
						index = i;
					}
				}
			}
			
			Label label = clusterList.get(index).removeLabel(clusterList.get(index).getLabels().get(0).getId());
			ClusterOfLabels c = new ClusterOfLabels();
			c.addLabel(label);
			clusterList.add(c);
			
	   }while(!hasXLabelsPerCluster(clusterList,1));
	}
	
	public void printClusterList(ArrayList<ClusterOfLabels> clusterList){
		System.out.println("#Clusters  -- "+clusterList.size());
		System.out.print("[ ");
		for(int i=0;i<clusterList.size();i++){
			printCluster(clusterList.get(i));
		}
		System.out.println("]\n");
	}
	
	public void printCluster(ClusterOfLabels cluster){
		//System.out.println("\nCluster ID "+cluster.getId());
		System.out.println("\n#Labels "+cluster.getLabels().size());
		System.out.print("( ");
		for(int j=0;j<cluster.getLabels().size();j++){
			System.out.print(cluster.getLabels().get(j).getLabelName().toString()+" , ");
		}
		System.out.print(") ");
	}
	
	
	
	public ClusterOfLabels getBiggestCluster(ArrayList<ClusterOfLabels> clusters){
		
		int i=0,max=0;
		ClusterOfLabels cluster = clusters.get(0);
		max = cluster.getLabels().size();
		
		for(i=1;i<clusters.size();i++){
			if(clusters.get(i).getLabels().size()>max){
				max = clusters.get(i).getLabels().size();
				cluster = clusters.get(i);
			}
		}
		return cluster;
	}
	
	
	public void initializeOriginalSimilarityMatrixx(ClusterOfLabels cluster){
		int dim = cluster.getLabels().size();
		originalSimilarityMatrixx = new SimilarityMatrix(dim);
		Label l1,l2;
		for(int i=0;i<dim;i++){
			l1 = cluster.getLabels().get(i);
			for(int j=0;j<dim;j++){
				if(i!=j){
					l2 = cluster.getLabels().get(j);
					originalSimilarityMatrixx.setSimilarityMatrixElement(i, j, 
							new SimilarityMatrixElement(l1.getId(),EuclideanDistance.getDistance(l1, l2)));
				}
				else{
					originalSimilarityMatrixx.setSimilarityMatrixElement(i, j, 
							new SimilarityMatrixElement(l1.getId(),0.0));
				}
			}
		}
		l1=null;
		l2 = null;
	}
	
	
	public SimilarityMatrix getSimilarityMatrixx(ClusterOfLabels cluster){
		
		ArrayList<Label> labels = cluster.getLabels();
		int dim = labels.size();
		SimilarityMatrix m = new SimilarityMatrix(dim);
		Label l1,l2;
		for(int i=0;i<dim;i++){
			l1 = labels.get(i);
			for(int j=0;j<dim;j++){
				if(i!=j){
					l2 = labels.get(j);
					m.setSimilarityMatrixElement(i, j, 
							new SimilarityMatrixElement(l1.getId(),
									originalSimilarityMatrixx.getSimilarityMatrixElement(l1.getId(), l2.getId()).getDistance()));
				}
				else{
					m.setSimilarityMatrixElement(i, j, 
							new SimilarityMatrixElement(l1.getId(),0.0));
				}
			}
		}
		if(log)m.print();
		if(log)System.out.println("\n");
		l1 = null;
		l2 = null;
		labels=null;
		return m;
	}
	
  public Measure getBiggestAverageSimilarityy(SimilarityMatrix m){
		
		int index=0,id=0,dim = m.getDim();
		double average=0,sum = 0,maxAverage=-999999.0,aux=0;
		StringBuilder s = null;
		if(log)s = new StringBuilder();
		for(int i=0;i<dim;i++){
			sum = 0;
			if(log)s.append("ID  " +i+" ");
			for(int j=0;j<dim;j++){
				aux = m.getSimilarityMatrixElement(i, j).getDistance(); 
				if(log)s.append("  "+aux);
				sum+=aux;
				id=m.getSimilarityMatrixElement(i, j).getId();
			}
			if(log)s.append("   / "+(dim-1));
			average = sum/(dim-1);
			if(log)s.append("  =  "+average+"\n");
						
			if(average>=maxAverage){
				maxAverage = average;
				index=id;
			}
		}
		if(log)System.out.println(s);
		if(log)System.out.println("INDEX: "+index+ "  Max Average :   "+maxAverage);
		return new Measure(index, maxAverage);
	}
		
  public Measure getSimilaritiesFromNewClusterr(SimilarityMatrix m,ArrayList<Integer>whoOut){
	
	int numberOfOutElements = whoOut.size();
	int dim = m.getDim();
	int index=-1;
	double siAverage = 0,siaux=0;
	double saAverage = 0,saaux=0;
	double diff = 0;
	double maxDiff = -999999;
	StringBuilder sa=null,si=null,d = null;
	if(log)sa = new StringBuilder();
	if(log)si = new StringBuilder();
	if(log)d = new StringBuilder();
	for(int i=0;i<dim;i++){
		if(!whoOut.contains(m.getSimilarityMatrixElement(i, 0).getId())){
			if(log)si.append("ID:  "+m.getSimilarityMatrixElement(i, 0).getId()+"  ");
			if(log)sa.append("ID:  "+m.getSimilarityMatrixElement(i, 0).getId()+"  ");
			siAverage = 0;
			saAverage = 0;
			diff = 0;
			for(int j=0;j<dim;j++){
				if(i!=j){
					if(whoOut.contains(m.getSimilarityMatrixElement(j, 0).getId())){
						saaux = m.getSimilarityMatrixElement(i, j).getDistance();
						if(log)sa.append(saaux+"  ");
						saAverage+=saaux;
					}
					else{
						siaux = m.getSimilarityMatrixElement(i, j).getDistance();
						if(log)si.append(siaux+"   ");
						siAverage+= siaux;
					}
				}
			}
			diff = (siAverage/((dim-1)-numberOfOutElements)) - (saAverage/(numberOfOutElements));
			if(log)d.append("DIFF "+diff+"\n");
			if(log)si.append(" / "+((dim-1)-numberOfOutElements)+"\n");
			if(log)sa.append(" / "+numberOfOutElements+"\n");
			if(maxDiff<=diff){
				maxDiff = diff;
				index = m.getSimilarityMatrixElement(i, 0).getId();
			}
	     }
	}
	if(log)System.out.println(si);
	if(log)System.out.println(sa);
	if(log)System.out.println(d);
	if(log)System.out.println("INDEX: "+index+ "  Max Average :   "+maxDiff);
	return new Measure(index,maxDiff);
}

	public boolean hasXLabelsPerCluster(ArrayList<ClusterOfLabels> labels,int number){
		
		for(int i=0;i<labels.size();i++){
			if(labels.get(i).getLabels().size()>number){
				return false;
			}
		}
		return true;
	}	
}
