package br.com.uff.tcc.rrodovalho.classifier.meta;

import weka.classifiers.Classifier;
import weka.core.Instance;
import mulan.classifier.InvalidDataException;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.meta.MultiLabelMetaLearner;
import mulan.classifier.transformation.ClassifierChain;
import mulan.data.MultiLabelInstances;

public class RRDHCM extends MultiLabelMetaLearner{

	private static final long serialVersionUID = 1L;
	
	public RRDHCM(Classifier classifier) {
		super(new ClassifierChain(classifier));
	}

	@Override
	protected void buildInternal(MultiLabelInstances trainingSet)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected MultiLabelOutput makePredictionInternal(Instance instance)
			throws Exception, InvalidDataException {
		// TODO Auto-generated method stub
		return null;
	}

}
