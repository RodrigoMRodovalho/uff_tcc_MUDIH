package br.com.uff.tcc.rrodovalho.domain;

import mulan.data.MultiLabelInstances;
import weka.core.Instances;

/**
 * Created by rrodovalho on 04/03/16.
 */
public class DatasetWrapper {

    private MultiLabelInstances multiLabelInstances;
    private Instances instances;
    private boolean isSingleLabell;

    public DatasetWrapper(MultiLabelInstances multiLabelInstances){
        this.multiLabelInstances = multiLabelInstances;
        isSingleLabell = false;
    }

    public DatasetWrapper(Instances instances){
        this.instances = instances;
        isSingleLabell = true;
    }

    public MultiLabelInstances getMultiLabelInstances() {
        return multiLabelInstances;
    }

    public Instances getInstances() {
        return instances;
    }

    public boolean isSingleLabel() {
        return isSingleLabell;
    }

}
