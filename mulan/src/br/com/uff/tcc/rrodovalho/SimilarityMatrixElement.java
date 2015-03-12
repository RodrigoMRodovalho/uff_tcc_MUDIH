package br.com.uff.tcc.rrodovalho;

public class SimilarityMatrixElement {
	
	private int id;
	private double distance;
		
	public SimilarityMatrixElement(int id, double distance) {
		this.id = id;
		this.distance = distance;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	
}
