package br.com.uff.tcc.rrodovalho;

import java.util.ArrayList;

public class Cluster {
	
	private int id;
	private int father_id;
	ArrayList<Integer> elements = new ArrayList<Integer>();
	
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
	
	public void addElement(int element){
		this.elements.add(element);
	}
	
	public ArrayList<Integer> getElements(){
		return this.elements;
	}
	
	public void setElements(ArrayList<Integer> elements){
		this.elements = elements;
	}
	
	public void removeElement(int element){
		
		for(int i=0;i<this.elements.size();i++){
			if(this.elements.get(i)==element){
				this.elements.remove(i);
			}
			break;
		}
		
	}
	
	
}
