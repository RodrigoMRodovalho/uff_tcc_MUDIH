package br.com.uff.tcc.rrodovalho.core;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.Instances;
import br.com.uff.tcc.rrodovalho.distance.LabelDistance;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;
import br.com.uff.tcc.rrodovalho.domain.ClusterOfLabels;
import br.com.uff.tcc.rrodovalho.domain.Label;
import br.com.uff.tcc.rrodovalho.domain.Measure;
import br.com.uff.tcc.rrodovalho.domain.SimilarityMatrix;
import br.com.uff.tcc.rrodovalho.domain.SimilarityMatrixElement;
import br.com.uff.tcc.rrodovalho.graphics.GraphViz;


public class DivisiveHierarchicalClustering {
	
	private final static int NONE=0;
	private MultiLabelInstances mInstances;
	private String[] labelNames;
	private boolean log= false;
	private String graphOutputPath=null;
	private String dataSetInfo = null;
	private SimilarityMatrix originalSimilarityMatrixx;
	private SimilarityMeasureEnum distanceMethod;
	private int iteratorCounter=0;
	private StringBuilder fileLog;
	private int numPartitions = 0;
	GraphViz gv;

	public DivisiveHierarchicalClustering(int numPartitions){
		this.numPartitions = numPartitions;
	}

	
	private ArrayList<Label> getLabelsArray(){

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
	
			
	public  ArrayList<String>[] build(MultiLabelInstances mInstances,SimilarityMeasureEnum distanceMethod){
		
		this.mInstances = mInstances;
		this.labelNames = this.mInstances.getLabelNames();
		this.distanceMethod = distanceMethod;
		this.dataSetInfo = this.mInstances.getDataSet().relationName()
													   .concat("_L")
													   .concat(this.mInstances.getNumLabels()+"_N")
													   .concat(this.mInstances.getNumInstances()+"_")
													   .concat(distanceMethod.name());
		ClusterOfLabels.numDataSetInstances = this.mInstances.getNumInstances();
		//this.fileLog = new StringBuilder();
		//this.fileLog.append(this.dataSetInfo+"\n");
		//gv = new GraphViz();
		//gv.init();
		//gv.setGraphTitle(this.dataSetInfo);
		
		ArrayList<String>[] ret =  buildInternal(mInstances);
		
		//gv.close();
		//drawGraph();
		return ret;
	}

	public ArrayList<String>[] returnLabels(ArrayList<ClusterOfLabels> cluterList){

		ArrayList<String>[] childrenLabels = new ArrayList[cluterList.size()];

		ArrayList<String> array ;
		for(int i=0;i<cluterList.size();i++){
			array = new ArrayList<>();
			for(int j=0;j<cluterList.get(i).getLabels().size();j++){
				array.add(cluterList.get(i).getLabels().get(j).getLabelName());
			}
			childrenLabels[i] = array;
		}

		return childrenLabels;
	}

	private ArrayList<String>[] buildInternal(MultiLabelInstances mInstances){
		
		//Timestamp statingTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		//System.out.println("Starting build at "+statingTimestamp.toString());
		//this.fileLog.append("Starting build at "+statingTimestamp.toString()+"\n");
		
		int j=1;
		ArrayList<ClusterOfLabels> clusterList = new ArrayList<ClusterOfLabels>();
		ArrayList<Integer> whoOut;
		ClusterOfLabels root = new ClusterOfLabels();
		SimilarityMatrix sMatrix;
		Measure biggestAverage;
		ClusterOfLabels clus;

		root.setLabels(getLabelsArray());
		root.setId(j);
		root.setFather_id(j);
		//System.out.println("ROOT SIZE "+root.getLabels().size());
		clusterList.add(root);

		initializeOriginalSimilarityMatrixx(root);
		//printClusterList(clusterList);
		//printCardinalitiesByClusterList(clusterList);
		
		//gv.addln(root.getId()+";");
		//gv.addNode(root.getId(), root.toString(),root.getCardinality(),NONE);
		
		do{

			clus = getBiggestCluster(clusterList);
			//ClusterOfLabels auxClus = ClusterOfLabels.newInstance(clus);
			ClusterOfLabels auxClus = clus.newInstance();
			auxClus.setId(++j);
			auxClus.setFather_id(clus.getId());
			sMatrix = getSimilarityMatrixx(auxClus);
			//System.out.println("\n");
			//this.fileLog.append("\n\n");
			//log=false;
			biggestAverage = getBiggestAverageSimilarityy(sMatrix);
			ClusterOfLabels	clus2 = new ClusterOfLabels();
			whoOut = new ArrayList<Integer>();
			j++;
			while(biggestAverage.getValue()>=0){

				Label labelRemoved = auxClus.removeLabel(biggestAverage.getID());
				//log=false;
				//if(log)labelRemoved.print();
                clus2.addLabel(labelRemoved);
                clus2.setId(j);
                clus2.setFather_id(auxClus.getFather_id());
				whoOut.add(biggestAverage.getID());
				//log=false;
				biggestAverage = getSimilaritiesFromNewClusterr(sMatrix,whoOut);

//				if(clus2.getLabels().size()==1){
//					clusterList.add(clus2);
//				}
				
			}
			clusterList.remove(clus);
			clusterList.add(auxClus);
			clusterList.add(clus2);
			//printCardinalitiesByClusterList(clusterList);
			
			clus=null;
			iteratorCounter++;
			
			//printClusterList(clusterList);
			//gv.addNode(auxClus.getId(), auxClus.toString(),auxClus.getCardinality(),iteratorCounter);
			//gv.addRelation(auxClus.getFather_id(), auxClus.getId());
			
			//gv.addNode(clus2.getId(), clus2.toString(),clus2.getCardinality(),NONE);
			//gv.addRelation(clus2.getFather_id(), clus2.getId());
			if(clusterList.size()>=numPartitions){
				return returnLabels(clusterList);
			}
		}while(!hasXLabelsPerCluster(clusterList,2));

		return returnLabels(clusterList);
		//System.out.println("\nOUT OF THE MAIN WHILE");
		//log=false;
		//divideToUniqueLabelPerCluster(clusterList,j);
		//System.out.println("\n\n");
		//this.fileLog.append("\n\n\n");
		//printClusterList(clusterList);
		//printCardinalitiesByClusterList(clusterList);
		
		//Timestamp finishTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		//System.out.println("Build finished at "+finishTimestamp.toString());
		//this.fileLog.append("Build finished at "+finishTimestamp.toString()+"\n");
		
		//iteratorCounter=0;
		//return null;
	}
		
	private void drawGraph(){
		String type = "png";
		String path = null;
		if(graphOutputPath!=null){
			path = graphOutputPath;
		}
		else{
			path = "/tmp/";
		}
		String absolutePath = path.concat(this.dataSetInfo).concat(".") + type;
		File out = new File(absolutePath);   // Linux
		System.out.println("Starting creation of graph");
	    gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
	    
//	    Thread t = new Thread(new Runnable() {
//			@Override
//			public void run() {
//				gv.openGraph(absolutePath);
//			}
//		});
//	    t.start();
	    gv = null;
	}
	
//	private void printCardinalitiesByClusterList(ArrayList<ClusterOfLabels> clusterList){
//		System.out.println("CARDINALITY\n");
//		for(int i=0;i<clusterList.size();i++){
//			System.out.println("Index "+i+": "+clusterList.get(i).getCardinality());
//		}
//		System.out.println("\n");
//	}
	
	
//	private int getNumOfLabels(int[] labels){
//		
//		int tam = labels.length;
//		int sum=0;
//		for(int i=0;i<tam;i++){
//			if(labels[i]==1){
//				sum++;
//			}
//		}
//		return sum;
//	}
//	
//	private double getCardinalityByCluster(ClusterOfLabels cluster) {
//		
//		ArrayList<Label> labels = cluster.getLabels();
//		double tam = labels.size();
//		double sum=0;
//		double cardinality=0;
//		for(int i=0;i<tam;i++){
//			sum+= getNumOfLabels(labels.get(i).getClassificationArray());
//		}
//		cardinality = sum/mInstances.getNumInstances();
//		
//		return cardinality;
//	}
	
		
	private void divideToUniqueLabelPerCluster(ArrayList<ClusterOfLabels> clusterList,int j){
		
		ClusterOfLabels cluster=null;
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
			
			//ClusterOfLabels firstBro = ClusterOfLabels.newInstance(clusterList.get(index));
			ClusterOfLabels firstBro = clusterList.get(index).newInstance();
			firstBro.setId(++j);
			firstBro.setFather_id(clusterList.get(index).getId());
			Label label = firstBro.removeLabel(firstBro.getLabels().get(0).getId());
			ClusterOfLabels secondBro = new ClusterOfLabels();
			secondBro.setId(++j);
			secondBro.setFather_id(firstBro.getFather_id());
			secondBro.addLabel(label);
			
			clusterList.remove(clusterList.get(index));
			clusterList.add(firstBro);
			clusterList.add(secondBro);
			iteratorCounter++;
			//gv.addNode(firstBro.getId(), firstBro.toString(),firstBro.getCardinality(),iteratorCounter);
			//gv.addRelation(firstBro.getFather_id(), firstBro.getId());
			
			//gv.addNode(secondBro.getId(), secondBro.toString(),secondBro.getCardinality(),NONE);
			 //gv.addRelation(secondBro.getFather_id(), secondBro.getId());
			
			firstBro = null;
			secondBro = null;
			
	   }while(!hasXLabelsPerCluster(clusterList,1));
	}
	
	private void printClusterList(ArrayList<ClusterOfLabels> clusterList){
		//System.out.println("Iteration: "+iteratorCounter);
		//System.out.println("#Clusters  -- "+clusterList.size());
		//System.out.println("[");
		this.fileLog.append("Iteration: "+iteratorCounter+"\n");
		this.fileLog.append("#Clusters  -- "+clusterList.size()+"\n");
		this.fileLog.append("[\n");
		
		for(int i=0;i<clusterList.size();i++){
			//printCluster(clusterList.get(i));
			this.fileLog.append(clusterList.get(i).printCluster());
		}
		//System.out.println("]\n");
		this.fileLog.append("]\n\n");
	}
//	
//	private void printCluster(ClusterOfLabels cluster){
//		System.out.println("\nCluster ID "+cluster.getId());
//		System.out.println("Cluster Father ID "+cluster.getFather_id());
//		System.out.println("#Labels "+cluster.getLabels().size());
//		System.out.print("( ");
//		for(int j=0;j<cluster.getLabels().size();j++){
//			System.out.print(cluster.getLabels().get(j).getLabelName().toString()+" , ");
//		}
//		System.out.print(") \n");
//	}
	
	
	
	private ClusterOfLabels getBiggestCluster(ArrayList<ClusterOfLabels> clusters){
		
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
	
	
	private void initializeOriginalSimilarityMatrixx(ClusterOfLabels cluster){
		int dim = cluster.getLabels().size();
		originalSimilarityMatrixx = new SimilarityMatrix(dim);
		Label l1,l2;
		for(int i=0;i<dim;i++){
			l1 = cluster.getLabels().get(i);
			for(int j=0;j<dim;j++){
				if(i!=j){
					l2 = cluster.getLabels().get(j);
					originalSimilarityMatrixx.setSimilarityMatrixElement(i, j, 
							new SimilarityMatrixElement(l1.getId(),LabelDistance.getDistance(l1, l2,this.distanceMethod)));
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
	
	
	private SimilarityMatrix getSimilarityMatrixx(ClusterOfLabels cluster){
		
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
	
	private Measure getBiggestAverageSimilarityy(SimilarityMatrix m){
		
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
		
 private Measure getSimilaritiesFromNewClusterr(SimilarityMatrix m,ArrayList<Integer>whoOut){
	
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

   private boolean hasXLabelsPerCluster(ArrayList<ClusterOfLabels> labels,int number){
		
		for(int i=0;i<labels.size();i++){
			if(labels.get(i).getLabels().size()>number){
				return false;
			}
		}
		return true;
	}
	
	//--------------------
	public void setGraphOutputImagePath(String path){
		this.graphOutputPath = path;
	}
	
	public String buildLog(){
		return this.fileLog.toString();
	}
	
}
