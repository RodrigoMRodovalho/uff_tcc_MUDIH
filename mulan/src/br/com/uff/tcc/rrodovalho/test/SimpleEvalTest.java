package br.com.uff.tcc.rrodovalho.test;

import br.com.uff.tcc.rrodovalho.classifier.RRDHC;
import mulan.classifier.MultiLabelLearner;
import mulan.classifier.meta.HOMER;
import mulan.classifier.meta.HierarchyBuilder;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.ClassifierChain;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.Measure;
import weka.classifiers.trees.J48;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rrodovalho on 03/03/16.
 */
public class SimpleEvalTest {

    public static void main(String[] args) {

        try {

            String base = "yeast";
            String arffFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/"+base+"/"+base+".arff";
            String xmlFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/"+base+"/"+base+".xml";
            //String outPath = "/home/rrodovalho/Documents/TCC/SimpleEvalTest/"+base+"/";
            String outPath = "/home/rrodovalho/Documents/TCC/CrossValidationEvalTest/"+base+"/";

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances(arffFilename, xmlFilename);

            List<Measure> measures = new ArrayList<>();
            measures.add(new mulan.evaluation.measure.HammingLoss());
            measures.add(new mulan.evaluation.measure.SubsetAccuracy());
            measures.add(new mulan.evaluation.measure.ExampleBasedFMeasure());
            measures.add(new mulan.evaluation.measure.MicroFMeasure(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroFMeasure(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroAUC(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MicroAUC(dataset.getNumLabels()));

            ArrayList<MultiLabelLearner> multiLabelLearnerArrayList = new ArrayList<>();
            multiLabelLearnerArrayList.add(new BinaryRelevance(new J48()));
            multiLabelLearnerArrayList.add(new LabelPowerset(new J48()));
            multiLabelLearnerArrayList.add(new ClassifierChain(new J48()));
            multiLabelLearnerArrayList.add(new RRDHC (new LabelPowerset(new J48()),3));
            multiLabelLearnerArrayList.add(new RRDHC (new LabelPowerset(new J48()),4));
            multiLabelLearnerArrayList.add(new RRDHC (new LabelPowerset(new J48()),5));
            multiLabelLearnerArrayList.add(new RRDHC (new ClassifierChain(new J48()),3));
            multiLabelLearnerArrayList.add(new RRDHC (new ClassifierChain(new J48()),4));
            multiLabelLearnerArrayList.add(new RRDHC (new ClassifierChain(new J48()),5));
            multiLabelLearnerArrayList.add(new HOMER(new LabelPowerset(new J48()),3, HierarchyBuilder.Method.Clustering));
            multiLabelLearnerArrayList.add(new HOMER (new LabelPowerset(new J48()),4, HierarchyBuilder.Method.Clustering));
            multiLabelLearnerArrayList.add(new HOMER (new LabelPowerset(new J48()),5, HierarchyBuilder.Method.Clustering));
            multiLabelLearnerArrayList.add(new HOMER (new ClassifierChain(new J48()),3, HierarchyBuilder.Method.Clustering));
            multiLabelLearnerArrayList.add(new HOMER (new ClassifierChain(new J48()),4, HierarchyBuilder.Method.Clustering));
            multiLabelLearnerArrayList.add(new HOMER (new ClassifierChain(new J48()),5, HierarchyBuilder.Method.Clustering));

            ArrayList<String> outputNames = new ArrayList<>();
            outputNames.add("BinaryRelevanceJ48");
            outputNames.add("LabelPowersetJ48");
            outputNames.add("ChassifierChainJ48");
            outputNames.add("RRDCH_LabelPowersetJ48_C3");
            outputNames.add("RRDCH_LabelPowersetJ48_C4");
            outputNames.add("RRDCH_LabelPowersetJ48_C5");
            outputNames.add("RRDCH_ClassifierChainJ48_C3");
            outputNames.add("RRDCH_ClassifierChainJ48_C4");
            outputNames.add("RRDCH_ClassifierChainJ48_C5");
            outputNames.add("HOMERLabelPowersetJ48_C3");
            outputNames.add("HOMER_LabelPowersetJ48_C4");
            outputNames.add("HOMER_LabelPowersetJ48_C5");
            outputNames.add("HOMER_ClassifierChainJ48_C3");
            outputNames.add("HOMER_ClassifierChainJ48_C4");
            outputNames.add("HOMER_ClassifierChainJ48_C5");

            FileWriter fw = null;
            BufferedWriter bw = null;
            Evaluator evaluator;
            Evaluation evaluation;
            MultipleEvaluation results;
            checkDir(outPath);
            for(int j=6;j<11;j+=2){
                for(int i=0;i<multiLabelLearnerArrayList.size();i++){
                    evaluator = new Evaluator();
                    //multiLabelLearnerArrayList.get(i).build(dataset);
                    //evaluation = evaluator.evaluate(multiLabelLearnerArrayList.get(i),dataset,measures);
                    results = evaluator.crossValidate(multiLabelLearnerArrayList.get(i), dataset, measures,j);
                    //fw = new FileWriter(outPath+base+"-"+outputNames.get(i)+".txt");
                    fw = new FileWriter(outPath+base+"-"+outputNames.get(i)+j+"Folds.txt");
                    bw = new BufferedWriter(fw);
                    //bw.write(evaluation.toString());
                    bw.write(results.toString());
                    bw.close();
                    evaluator = null;
                    evaluation = null;
                    results = null;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
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