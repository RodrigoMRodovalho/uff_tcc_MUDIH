package br.com.uff.tcc.rrodovalho.domain;

import mulan.classifier.MultiLabelLearner;
import weka.classifiers.Classifier;

import java.util.Objects;

/**
 * Created by rrodovalho on 04/03/16.
 */
public class LearnerWrapper {

    private Object[] learners;
    private int numLearners;

    public LearnerWrapper(int numLearners) {
        this.numLearners = numLearners;
        learners = new Object[numLearners];
    }

    public void addLearner(Classifier newLearner, int pos){
        this.learners[pos] = newLearner;
    }

    public void addLearner(MultiLabelLearner newLearner, int pos){
        this.learners[pos] = newLearner;
    }

    public Object getLearner(int pos){
        return this.learners[pos];
    }

    public int getNumOfLearners(){
        return numLearners;
    }

}
