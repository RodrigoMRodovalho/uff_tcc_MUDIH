package br.com.uff.tcc.rrodovalho.domain;

import java.util.ArrayList;


public class ClusterOfLabels{
		
	private int id;
	private int father_id;
	ArrayList<Label> labels = new ArrayList<Label>();
	public static int numDataSetInstances=0; 
	
	public ClusterOfLabels(){
		
	}
	
//	public static ClusterOfLabels newInstance(final ClusterOfLabels cl){
//		ClusterOfLabels c = new ClusterOfLabels();
//		c.setId(cl.getId());
//		c.setFather_id(cl.getFather_id());
//		c.setLabels(cl.getLabels());
//		return c;
//	}
	
	public ClusterOfLabels newInstance(){
		ClusterOfLabels c = new ClusterOfLabels();
		c.setId(this.id);
		c.setFather_id(this.father_id);
		c.setLabels(this.labels);
		return c;
	}
	
	public ClusterOfLabels(int id,int father_id,ArrayList<Label> labels){
		this.id = id;
		this.father_id = father_id;
		this.labels = labels;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFather_id() {
		return father_id;
	}
	public void setFather_id(int father_id) {
		this.father_id = father_id;
	}
	
	public void addLabel(Label label){
		this.labels.add(label);
	}
	
	public ArrayList<Label> getLabels(){
		return this.labels;
	}
	
	public void setLabels(ArrayList<Label> labels){
		this.labels = labels;
	}
	
	public Label removeLabel(int labelID){
		
		Label label=null;
		for(int i=0;i<this.labels.size();i++){
			if(this.labels.get(i).getId()==labelID){
				label = this.labels.get(i);
				this.labels.remove(i);
				break;
			}
		}
		return label;
		
	}
	
	public String printCluster(){
		StringBuilder sb = new StringBuilder();
		//System.out.println("#############CLUSTER##########");
		sb.append("#############CLUSTER##########\n");
		//System.out.println("Cluster ID:"+this.id);
		sb.append("Cluster ID:"+this.id+"\n");
		//System.out.println("Cluster FATHER_ID:"+this.father_id);
		sb.append("Cluster FATHER_ID:"+this.father_id+"\n");
		//System.out.println("#Labels: "+this.labels.size());
		sb.append("#Labels: "+this.labels.size()+"\n");
		//System.out.println("Cluster Labels:\n{");
		sb.append("Cluster Labels:\n{\n");
		//System.out.println(this.toString());
		sb.append(this.toString()+"\n");
		//System.out.println("}");
		sb.append("}\n");
//		for(int i=0;i<this.labels.size();i++){
//			//this.labels.get(i).print();
//			System.out.println(this.labels.get(i).toString());
//		}
		//System.out.println("Cluster Cardinality: "+getCardinality());
		sb.append("Cluster Cardinality: "+getCardinality()+"\n");
		//System.out.println("############END##############\n");
		sb.append("############END##############\n\n");
		
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<this.labels.size();i++){
			s.append(this.labels.get(i).toString());
			if(i!=this.labels.size()-1){
				s.append(", ");
			}
		}
		return String.valueOf(s);
	}
	
	public double getCardinality(){
		double tam = labels.size();
		double sum=0;
		double cardinality=0;
		for(int i=0;i<tam;i++){
			sum+= getNumOfLabels(this.labels.get(i).getClassificationArray());
		}
		cardinality = sum/numDataSetInstances;
		return cardinality;
	}
	
	private int getNumOfLabels(int[] labels){
		
		int tam = labels.length;
		int sum=0;
		for(int i=0;i<tam;i++){
			if(labels[i]==1){
				sum++;
			}
		}
		return sum;
	}
}
