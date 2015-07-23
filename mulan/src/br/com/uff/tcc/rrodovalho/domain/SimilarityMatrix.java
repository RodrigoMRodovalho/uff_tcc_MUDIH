package br.com.uff.tcc.rrodovalho.domain;

public class SimilarityMatrix {

	private SimilarityMatrixElement[][] matrix;
	private int dim;

	public SimilarityMatrix(int dim) {
		this.dim = dim;
		matrix = new SimilarityMatrixElement[dim][dim];
	}

	public void print(){
		for(int i=0;i<this.dim;i++){
			for(int j=0;j<this.dim;j++){
				matrix[i][j].printSimiliarityElement();
			}
			System.out.println();
		}
	}
	
	
	public SimilarityMatrixElement[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(SimilarityMatrixElement[][] matrix) {
		this.matrix = matrix;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int dim) {
		this.dim = dim;
	}
	
	public SimilarityMatrixElement getSimilarityMatrixElement(int row,int col){		
		return matrix[row][col];
	}
	
	public void setSimilarityMatrixElement(int row,int col,SimilarityMatrixElement sElement){
		this.matrix[row][col] = sElement;		
	}
	
	
	
	
}
