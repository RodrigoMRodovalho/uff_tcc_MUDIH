package br.com.uff.tcc.rrodovalho.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class ClusterOfLabels{
		
	/**
	 * 
	 */
	private int id;
	private int father_id;
	ArrayList<Label> labels = new ArrayList<Label>();
	
	public ClusterOfLabels(){
		
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
	
//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//    	Cluster cloned = (Cluster)super.clone();
//        return cloned;
//    }
	
	 public static Object clone(Object copyObject) {
		    try {
		      ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		      ObjectOutputStream oos = new ObjectOutputStream(baos);
		      oos.writeObject(copyObject);
		      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		      ObjectInputStream ois = new ObjectInputStream(bais);
		      Object deepCopy = ois.readObject();
		      return deepCopy;
		    } catch (IOException e) {
		      e.printStackTrace();
		    } catch(ClassNotFoundException e) {
		      e.printStackTrace();
		    }
		    return null;
	}
     
	
}
