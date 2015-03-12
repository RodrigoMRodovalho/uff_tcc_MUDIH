package br.com.uff.tcc.rrodovalho;

import weka.core.Instance;

public class Element {
	
	private int id;
	private Instance instace;
	private int[] labelsArray;
	
	
	public Element(int id, Instance instace,int[] labelsArray) {
		this.id = id;
		this.instace = instace;
		this.labelsArray = labelsArray;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Instance getInstace() {
		return instace;
	}
	public void setInstace(Instance instace) {
		this.instace = instace;
	}
	public int[] getLabelsArray() {
		return labelsArray;
	}
	public void setLabelsArray(int[] labelsArray) {
		this.labelsArray = labelsArray;
	}
	
	
	
}
