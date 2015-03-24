package br.com.uff.tcc.rrodovalho;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class Cluster{
		
	/**
	 * 
	 */
	private int id;
	private int father_id;
	ArrayList<Element> elements = new ArrayList<Element>();
	
	public Cluster(){
		
	}
	
	public Cluster(int id,int father_id,ArrayList<Element> elements){
		this.id = id;
		this.father_id = father_id;
		this.elements = elements;
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
	
	public void printCluster(){
		System.out.println("#############CLUSTER#########");
		System.out.println("Cluster ID:"+this.id);
		System.out.println("Cluster FATHER_ID:"+this.father_id);
		System.out.println("Cluster Elements:  ");
		for(int i=0;i<this.elements.size();i++){
			System.out.println("Element ID: "+(this.elements.get(i).getId()));
			System.out.print("Element Labels: ");
			for(int j=0;j<this.elements.get(i).getLabelsArray().length;j++){
				System.out.print(this.elements.get(i).getLabelsArray()[j]+"  ");
			}
			System.out.println();
		}
		System.out.println("s############END##############");
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
