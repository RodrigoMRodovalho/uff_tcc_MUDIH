package br.com.uff.tcc.rrodovalho.domain;

public class Label {
	private String labelName;
	private int[] classificationArray;
	private int id;
	
	public Label(int id,String labelName,int[] classificationArray){
		this.id = id;
		this.labelName = labelName;
		this.classificationArray = classificationArray;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public int[] getClassificationArray() {
		return classificationArray;
	}

	public void setClassificationArray(int[] classificationArray) {
		this.classificationArray = classificationArray;
	}
	
	public void print(){
		System.out.print("ID:  "+this.id+"     LabelName:  "+this.labelName+"  ClassArray:  ");
		for(int j=0;j<this.classificationArray.length;j++){
			System.out.print(this.classificationArray[j]+"  ");
		}
		System.out.println();
	}

	@Override
	public String toString() {
		return this.labelName;
	}
	
	
	
	
}
