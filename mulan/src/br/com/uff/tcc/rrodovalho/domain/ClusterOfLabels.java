package br.com.uff.tcc.rrodovalho.domain;

import java.util.ArrayList;


public class ClusterOfLabels{
		
	private int id;
	private int father_id;
	ArrayList<Label> labels = new ArrayList<Label>();
	
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
	
	public void printCluster(){
		System.out.println("#############CLUSTER#########");
		System.out.println("Cluster ID:"+this.id);
		System.out.println("Cluster FATHER_ID:"+this.father_id);
		System.out.println("Cluster Labels:  ");
		for(int i=0;i<this.labels.size();i++){
			this.labels.get(i).print();
			System.out.println();
		}
		System.out.println("############END##############");
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
}
