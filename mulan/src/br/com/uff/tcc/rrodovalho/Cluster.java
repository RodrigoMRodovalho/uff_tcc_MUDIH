package br.com.uff.tcc.rrodovalho;

import java.util.ArrayList;

public class Cluster {
	
	private int id;
	private int father_id;
	ArrayList<Element> elements = new ArrayList<Element>();
	
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
	
	public void addElement(Element element){
		this.elements.add(element);
	}
	
	public ArrayList<Element> getElements(){
		return this.elements;
	}
	
	public void setElements(ArrayList<Element> elements){
		this.elements = elements;
	}
	
	public Element removeElement(int elementID){
		
		Element element=null;
		for(int i=0;i<this.elements.size();i++){
			if(this.elements.get(i).getId()==elementID){
				element = this.elements.get(i);
				this.elements.remove(i);
				break;
			}
		}
		return element;
		
	}
	
	
}
