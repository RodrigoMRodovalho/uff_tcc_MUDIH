package br.com.uff.tcc.rrodovalho.test;

import br.com.uff.tcc.rrodovalho.core.DivisiveHierarchicalClustering;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;
import br.com.uff.tcc.rrodovalho.domain.Measure;
import mulan.classifier.meta.HOMER;
import mulan.classifier.meta.HierarchyBuilder;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.loss.HammingLoss;
import weka.classifiers.trees.J48;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rrodovalho on 02/02/16.
 */
public class HomerExample {

    public static final int MAX_NUM_OF_LABELS = 30;
    public static final String ARFF = "arff";
    public static final String XML = "xml";


    public static void main(String args[]) throws Exception {


        ArrayList<String> inputBasesPathArray = new ArrayList<String>();
        Map<String,String> inOutMap = new HashMap<String, String>();
        Map<String,String> xmlMap = new HashMap<String, String>();
        Map<String,String> outputResultMap = new HashMap<String, String>();
        String commonInputPath = "/home/rrodovalho/Dropbox/TCC_Bases/bases/";
        String commonOutPath = "/home/rrodovalho/Documents/HomerTest/TCC/";
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


        List<mulan.evaluation.measure.Measure> measures = null;


        MultiLabelInstances mInstances = null;
        HOMER homer = null;
        Evaluator eval = null;
        Evaluation eval2 = null;
        FileWriter fw = null;
        BufferedWriter bw = null;

        HierarchyBuilder.Method m[]={
                HierarchyBuilder.Method.BalancedClustering,
                HierarchyBuilder.Method.DivisiveHierarchicalClustering
        };

        for(int i=0;i<inputBasesPathArray.size();i++) {
            String s = inputBasesPathArray.get(i);
            System.out.println("Trying with "+s);
            mInstances = new MultiLabelInstances(s, xmlMap.get(s));
            if(mInstances.getNumLabels()>MAX_NUM_OF_LABELS && mInstances.getNumInstances()>2000) {
                System.out.println("Out of bounds");
                continue;
            }
            System.out.println("Starting with "+s);
            String o = outputResultMap.get(s);
            checkDir(o);

            measures = new ArrayList<>();
            measures.add(new mulan.evaluation.measure.HammingLoss());
            measures.add(new mulan.evaluation.measure.SubsetAccuracy());
            measures.add(new mulan.evaluation.measure.ExampleBasedFMeasure());
            measures.add(new mulan.evaluation.measure.MicroFMeasure(mInstances.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroFMeasure(mInstances.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroAUC(mInstances.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MicroAUC(mInstances.getNumLabels()));
            for(int k=0;k<3;k++) {
                for (int j = 0; j < 2; j++) {
                    homer = new HOMER(new BinaryRelevance(new J48()),k+6 , m[j]);
                    homer.build(mInstances);
                    eval = new Evaluator();
                    eval2 = eval.evaluate(homer, mInstances, measures);
                    fw = new FileWriter(o + "Method" + m[j].name() +"#Cluster"+ (k+6)+".txt");
                    bw = new BufferedWriter(fw);
                    bw.write(eval2.toString());
                    bw.close();
                }
            }
            measures = null;
        }

        //DivisiveHierarchicalClustering method = null;
        //method = new DivisiveHierarchicalClustering();
        //method.build(mInstances, SimilarityMeasureEnum.EuclideanDistance);









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
