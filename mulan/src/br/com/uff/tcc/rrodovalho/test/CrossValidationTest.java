package br.com.uff.tcc.rrodovalho.test;

import br.com.uff.ic.tarcisio.DCHierarchicalClusterer;
import br.com.uff.ic.tarcisio.HCDC;
import br.com.uff.ic.tarcisio.HierarchyBuilderForHCDC;
import br.com.uff.tcc.rrodovalho.classifier.RRDHC;
import mulan.classifier.lazy.MLkNN;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.evaluation.measure.Measure;
import weka.classifiers.trees.J48;
import weka.core.Utils;

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
            // e.g. -arff emotions.arff

            String arffFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/emotions/emotions.arff";
            // e.g. -xml emotions.xml
            String xmlFilename = "/home/rrodovalho/Dropbox/TCC_Bases/bases/emotions/emotions.xml";

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances(arffFilename, xmlFilename);



            RRDHC rrdhc = new RRDHC(new LabelPowerset(new J48()),2);
            rrdhc.build(dataset);

            //HCDC hcdc = new HCDC(new LabelPowerset(new J48()),3, HierarchyBuilderForHCDC.Method.HierarchicalClusterer);
            //hcdc.build(dataset);

            //hcdc.Imprimir();

            List<Measure> measures = new ArrayList<>();
             measures.add(new mulan.evaluation.measure.HammingLoss());
             measures.add(new mulan.evaluation.measure.SubsetAccuracy());
             measures.add(new mulan.evaluation.measure.ExampleBasedFMeasure());
                        measures.add(new mulan.evaluation.measure.MicroFMeasure(dataset.getNumLabels()));
                        measures.add(new mulan.evaluation.measure.MacroFMeasure(dataset.getNumLabels()));
                        measures.add(new mulan.evaluation.measure.MacroAUC(dataset.getNumLabels()));
                        measures.add(new mulan.evaluation.measure.MicroAUC(dataset.getNumLabels()));

            Evaluator eval0 = new Evaluator();
            Evaluation eval2 = eval0.evaluate(rrdhc, dataset,measures);

            //System.out.println(eval2.toString());

            RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));
            MLkNN learner2 = new MLkNN();

            Evaluator eval = new Evaluator();
            MultipleEvaluation results;

            BinaryRelevance binaryRelevance = new BinaryRelevance(new J48());
            binaryRelevance.build(dataset);


            int numFolds = 3;
            results = eval.crossValidate(binaryRelevance, dataset, numFolds);
            System.out.println(results);
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
