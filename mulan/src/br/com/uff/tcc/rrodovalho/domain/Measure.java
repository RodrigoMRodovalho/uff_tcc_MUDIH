package br.com.uff.tcc.rrodovalho.domain;

public class Measure {

	private int ID;
	private double value;
	
	public Measure(int ID, double value) {
		this.ID = ID;
		this.value = value;
	}
	public int getID() {
		return this.ID;
	}
	public void setID(int iD) {
		this.ID = iD;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	public String toString(){
		return "Index: "+this.ID+" Value:  "+this.value;
	}
	
	
	
}
