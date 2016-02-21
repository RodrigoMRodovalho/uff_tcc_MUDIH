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

	public static final String ARFF = "arff";
	public static final String XML = "xml";

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException, IOException {
		
		ArrayList<String> inputBasesPathArray = new ArrayList<String>();
		Map<String,String> xmlMap = new HashMap<String, String>();
		Map<String,String> inOutMap = new HashMap<String, String>();
		Map<String,String> outputResultMap = new HashMap<String, String>();
		String commonInputPath = "/home/rrodovalho/Dropbox/TCC_Bases/bases/";
		String commonOutPath = "/home/rrodovalho/Documents/TCC/";
		String f,arff;

//        inOutMap.put("bibtex/bibtex.","bibtex/");
//        inOutMap.put("bookmarks/bookmarks.","bookmarks/");
//        inOutMap.put("corel5k/Corel5k.","corel5k/");
//        inOutMap.put("Corel16k001/Corel16k001.","Corel16k001/");
//        inOutMap.put("delicious/delicious.","delicious/");
//        inOutMap.put("enron/enron.","enron/");
//        inOutMap.put("genbase/genbase.","genbase/");
//        inOutMap.put("mediamill/mediamill.","mediamill/");
//        inOutMap.put("medical/medical.","medical/");
//        inOutMap.put("rcv1subset1/rcv1subset1.","rcv1subset1/");
//        inOutMap.put("scene/scene.","scene/");
		  inOutMap.put("emotions/emotions.","emotions/");
//        inOutMap.put("tmc2007/tmc2007.","tmc2007/");
//        inOutMap.put("yeast/yeast.","yeast/");


		for (Map.Entry<String, String> entry : inOutMap.entrySet())
		{
			f = commonInputPath.concat(entry.getKey());
			arff = f.concat(ARFF);
			inputBasesPathArray.add(arff);
			xmlMap.put(arff, f.concat(XML));
			outputResultMap.put(arff, commonOutPath.concat(entry.getValue()));
		}
		
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
			method = new DivisiveHierarchicalClustering();
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
