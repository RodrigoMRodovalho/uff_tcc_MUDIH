package br.com.uff.tcc.rrodovalho.test;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import br.com.uff.tcc.rrodovalho.core.DivisiveHierarchicalClustering;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;

public class DivisiveHierarchicalClusteringTest {

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException, IOException {
		
		ArrayList<String> inputBasesPathArray = new ArrayList<String>();
		Map<String,String> xmlMap = new HashMap<String, String>();
		Map<String,String> outputResultMap = new HashMap<String, String>();
		String commonInputPath = "/home/rrodovalho/Dropbox/TCC_Bases/";
		String commonOutPath = "/home/rrodovalho/Documents/TCC/";
		String f,arff;
		
		f = commonInputPath.concat("bases/bibtex/bibtex.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("bibtex/"));
		
		f = commonInputPath.concat("bases/bookmarks/bookmarks.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("bookmarks/"));
		
		f = commonInputPath.concat("bases/corel5k/Corel5k.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("corel5k/"));
				
		f = commonInputPath.concat("bases/Corel16k001/Corel16k001.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("Corel16k001/"));
		
		f = commonInputPath.concat("bases/delicious/delicious.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("delicious/"));
		
		f = commonInputPath.concat("bases/emotions/emotions.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("emotions/"));
		
		f = commonInputPath.concat("bases/enron/enron.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("enron/"));
		
		f = commonInputPath.concat("bases/genbase/genbase.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("genbase/"));
		
		f = commonInputPath.concat("bases/mediamill/mediamill.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("mediamill/"));
		
		
		f = commonInputPath.concat("bases/medical/medical.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("medical/"));
		
		
		f = commonInputPath.concat("bases/rcv1subset1/rcv1subset1.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("rcv1subset1/"));
		
		
		f = commonInputPath.concat("bases/scene/scene.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("scene/"));
		
		f = commonInputPath.concat("bases/tmc2007/tmc2007.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("tmc2007/"));
		
		
		f = commonInputPath.concat("bases/yeast/yeast.");
		arff = f.concat("arff");
		inputBasesPathArray.add(arff);
		xmlMap.put(arff, f.concat("xml"));
		outputResultMap.put(arff, commonOutPath.concat("yeast/"));
		
		f= null;
		arff=null;

		MultiLabelInstances mInstances = null;
		DivisiveHierarchicalClustering method = null;
		
		SimilarityMeasureEnum e[]={SimilarityMeasureEnum.EuclideanDistance,
				SimilarityMeasureEnum.ChebychevDistance,
				SimilarityMeasureEnum.QuadraticEuclideanDistance,
				SimilarityMeasureEnum.ManhattanDistance
		};
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		for(int i=0;i<inputBasesPathArray.size();i++){
			String s = inputBasesPathArray.get(i);
			String o = outputResultMap.get(s);
			checkDir(o);
			mInstances = new MultiLabelInstances(s,xmlMap.get(s));
			method = new DivisiveHierarchicalClustering(0);
			method.setGraphOutputImagePath(o);
			for(int j=1;j<2;j++){
				method.build(mInstances, e[j]);
				fw = new FileWriter(o+"out"+e[j].name()+".txt");
				bw = new BufferedWriter(fw);
				bw.write(s+"\n");
				bw.write(method.buildLog());
				bw.close();
			}
			s = null;
			o=null;
		}
	}
	
	public static void checkDir(String dir){
		final File file = new File(dir);
		final File parent_directory = file.getAbsoluteFile();

		if (null != parent_directory)
		{
		    parent_directory.mkdirs();
		}
	}

}
