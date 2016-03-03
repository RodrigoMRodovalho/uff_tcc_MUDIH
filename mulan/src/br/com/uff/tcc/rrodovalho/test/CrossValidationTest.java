package br.com.uff.tcc.rrodovalho.test;

import br.com.uff.tcc.rrodovalho.classifier.RRDHC;
import mulan.classifier.meta.HOMER;
import mulan.classifier.meta.HierarchyBuilder;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.Measure;
import weka.classifiers.trees.J48;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rrodovalho on 21/02/16.
 */
public class CrossValidationTest {

    public static void main(String[] args) {

        try {
            String arffFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/yeast/yeast.arff";
            String xmlFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/yeast/yeast.xml";

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances(arffFilename, xmlFilename);


//            RRDHC rrdhc = new RRDHC(new BinaryRelevance(new J48()),3);
//            RRDHC rrdhc = new RRDHC(new LabelPowerset(new J48()),5);
//            BinaryRelevance rrdhc = new BinaryRelevance(new J48());
//            LabelPowerset rrdhc = new LabelPowerset(new J48());
            HOMER rrdhc = new HOMER(new LabelPowerset(new J48()), 3, HierarchyBuilder.Method.Clustering);
            //rrdhc.build(dataset);

            List<Measure> measures = new ArrayList<>();
             measures.add(new mulan.evaluation.measure.HammingLoss());
             measures.add(new mulan.evaluation.measure.SubsetAccuracy());
             measures.add(new mulan.evaluation.measure.ExampleBasedFMeasure());
            measures.add(new mulan.evaluation.measure.MicroFMeasure(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroFMeasure(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MacroAUC(dataset.getNumLabels()));
            measures.add(new mulan.evaluation.measure.MicroAUC(dataset.getNumLabels()));

            //Evaluator eval0 = new Evaluator();
            //Evaluation eval2 = eval0.evaluate(rrdhc, dataset,measures);

            //System.out.println("\n\n"+eval2.toString());

            Evaluator eval = new Evaluator();
            MultipleEvaluation results;

            int numFolds = 3;
            results = eval.crossValidate(rrdhc, dataset, numFolds);
            System.out.println(results);

        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(CrossValidationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrossValidationTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


}
