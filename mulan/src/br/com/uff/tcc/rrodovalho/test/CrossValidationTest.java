package br.com.uff.tcc.rrodovalho.test;

import mulan.classifier.lazy.MLkNN;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import weka.classifiers.trees.J48;
import weka.core.Utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rrodovalho on 21/02/16.
 */
public class CrossValidationTest {

    public static void main(String[] args) {

        try {
            // e.g. -arff emotions.arff

            String arffFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/emotions/emotions.arff";
            // e.g. -xml emotions.xml
            String xmlFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/emotions/emotions.xml";

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances(arffFilename, xmlFilename);

            RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));
            MLkNN learner2 = new MLkNN();

            Evaluator eval = new Evaluator();
            MultipleEvaluation results;

            int numFolds = 10;
            results = eval.crossValidate(learner1, dataset, numFolds);
            System.out.println(results);
            results = eval.crossValidate(learner2, dataset, numFolds);
            System.out.println(results);
        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(CrossValidationTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrossValidationTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
