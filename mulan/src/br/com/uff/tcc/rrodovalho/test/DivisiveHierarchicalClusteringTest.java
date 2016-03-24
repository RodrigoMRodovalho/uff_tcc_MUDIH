package br.com.uff.tcc.rrodovalho.test;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import br.com.uff.tcc.rrodovalho.classifier.RRDHC;
import mulan.classifier.MultiLabelLearner;
import mulan.classifier.meta.HOMER;
import mulan.classifier.meta.HierarchyBuilder;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.ClassifierChain;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import br.com.uff.tcc.rrodovalho.core.DivisiveHierarchicalClustering;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.Measure;
import weka.classifiers.trees.J48;

public class DivisiveHierarchicalClusteringTest {

	public static final String ARFF = "arff";
	public static final String XML = "xml";
	static List<Measure> measures ;

	public static void main(String[] args) throws InvalidDataFormatException, CloneNotSupportedException, IOException {
		
		ArrayList<String> inputBasesPathArray = new ArrayList<String>();
		Map<String,String> xmlMap = new HashMap<String, String>();
		Map<String,String> inOutMap = new HashMap<String, String>();
		Map<String,String> outputResultMap = new HashMap<String, String>();
		String commonInputPath = "/home/rrodovalho/Dropbox/TCC_Bases/bases/";
		String commonOutPath = "/home/rrodovalho/Dropbox/TCC/RRDHC/LastResults/";
		String f,arff;

		//Pendent

//		inOutMap.put("bibtex/bibtex.","bibtex/");
//		inOutMap.put("bookmarks/bookmarks.","bookmarks/");
//		inOutMap.put("corel5k/Corel5k.","corel5k/");
//		inOutMap.put("Corel16k001/Corel16k001.","Corel16k001/");
//		inOutMap.put("delicious/delicious.","delicious/");
		inOutMap.put("rcv1subset1/rcv1subset1.","rcv1subset1/");


//   Muito tempo demora
//		inOutMap.put("tmc2007/tmc2007.","tmc2007/");

//		Error
//		inOutMap.put("mediamill/mediamill.","mediamill/");


		//Done
//		inOutMap.put("genbase/genbase.","genbase/");
//		inOutMap.put("emotions/emotions.","emotions/");
//		inOutMap.put("yeast/yeast.","yeast/");
//		inOutMap.put("scene/scene.","scene/");
//		inOutMap.put("enron/enron.","enron/");
//		inOutMap.put("medical/medical.","medical/");


		ArrayList<MultiLabelLearner> multiLabelLearnerArrayList = new ArrayList<>();
		multiLabelLearnerArrayList.add(new RRDHC (new LabelPowerset(new J48()),5));
		multiLabelLearnerArrayList.add(new RRDHC (new ClassifierChain(new J48()),5));
		multiLabelLearnerArrayList.add(new HOMER (new LabelPowerset(new J48()),5, HierarchyBuilder.Method.Clustering));
		multiLabelLearnerArrayList.add(new HOMER (new ClassifierChain(new J48()),5, HierarchyBuilder.Method.Clustering));

		ArrayList<String> outputNames = new ArrayList<>();
		outputNames.add("RRDHC_LabelPowersetJ48_5C10F");
		outputNames.add("RRDHC_ClassifierChainJ48_5C10F");
		outputNames.add("HOMER_LabelPowersetJ48_5C10F");
		outputNames.add("HOMER_ClassifierChainJ48_5C10F");

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

		FileWriter fw = null;
		BufferedWriter bw = null;
		Evaluator evaluator;
		Evaluation evaluation;
		MultipleEvaluation results;

		for(int i=0;i<inputBasesPathArray.size();i++){
			String s = inputBasesPathArray.get(i);
			String o = outputResultMap.get(s);
			checkDir(o);
			mInstances = new MultiLabelInstances(s,xmlMap.get(s));

			setMeasuresArray(mInstances);

			for(int j=0;j<multiLabelLearnerArrayList.size();j++){
				fw = new FileWriter(o+outputNames.get(j)+".txt");
				bw = new BufferedWriter(fw);
				evaluator = new Evaluator();
				bw.write("Info: "+o+outputNames.get(j)+"\n");
				bw.write("Num Instances "+mInstances.getNumInstances()+"\n");
				bw.write("Num Labels "+mInstances.getNumLabels()+"\n");
				System.out.println("Info: "+o+outputNames.get(j));
				System.out.println("Num Instances "+mInstances.getNumInstances());
				System.out.println("Num Labels "+mInstances.getNumLabels());
				Timestamp timeStamp = new Timestamp(Calendar.getInstance().getTime().getTime());
				System.out.println("Crossvalidate starting at "+timeStamp.toString());
				bw.write("Crossvalidate starting at "+timeStamp.toString()+"\n");
				results = evaluator.crossValidate(multiLabelLearnerArrayList.get(j), mInstances, measures,10);
				timeStamp = new Timestamp(Calendar.getInstance().getTime().getTime());
				System.out.println("Crossvalidate ending at "+timeStamp.toString());
				bw.write("Crossvalidate ending at "+timeStamp.toString()+"\n");
				bw.write(results.toString());
				bw.close();
				evaluator = null;
				evaluation = null;
				results = null;
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

	public static void setMeasuresArray(MultiLabelInstances mInstances){
		measures = null;
		measures = new ArrayList<>();
		measures.add(new mulan.evaluation.measure.HammingLoss());
		measures.add(new mulan.evaluation.measure.SubsetAccuracy());
		measures.add(new mulan.evaluation.measure.ExampleBasedFMeasure());
		measures.add(new mulan.evaluation.measure.MicroFMeasure(mInstances.getNumLabels()));
		measures.add(new mulan.evaluation.measure.MacroFMeasure(mInstances.getNumLabels()));
		measures.add(new mulan.evaluation.measure.MacroAUC(mInstances.getNumLabels()));
		measures.add(new mulan.evaluation.measure.MicroAUC(mInstances.getNumLabels()));
	}

}
