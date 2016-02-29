package br.com.uff.tcc.rrodovalho.classifier;

import br.com.uff.tcc.rrodovalho.distance.LabelDistance;
import br.com.uff.tcc.rrodovalho.distance.SimilarityMeasureEnum;
import br.com.uff.tcc.rrodovalho.domain.*;
import mulan.classifier.InvalidDataException;
import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.meta.MultiLabelMetaLearner;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.data.LabelsMetaData;
import mulan.data.LabelsMetaDataImpl;
import mulan.data.MultiLabelInstances;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SystemInfo;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by rrodovalho on 21/02/16.
 */
public class RRDHC extends MultiLabelMetaLearner{
    private final int mNumClusters;
    /**
     * Creates a new instance.
     *
     * @param baseLearner the base multi-label learner which will be used
     *                    internally to handle the data.
     */

    private SimilarityMatrix originalSimilarityMatrixx;
    private ArrayList<ClusterOfLabels> clusterList;
    private MultiLabelInstances mInstances;
    private MultiLabelLearner[] ensemble;

    public RRDHC(MultiLabelLearner baseLearner,int numCluster) {
        super(baseLearner);
        mNumClusters = numCluster;
    }


    private ArrayList<Label> getLabelsArray(MultiLabelInstances mInstances){

        Instances instances = mInstances.getDataSet();
        int numInstances = mInstances.getNumInstances();
        int instanceIndex =0;
        ArrayList<Label> labels = new ArrayList<Label>();
        double[] aux = null;

        for(int index=0;index<mInstances.getNumLabels();index++){
            labels.add(new Label(index, labelNames[index], new int[numInstances]));
        }
        //Timestamp statingTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        //System.out.println("Starting build at "+statingTimestamp.toString());

//        for(int j=0;j<numInstances;j++){
//
//            for(int i=0;i<mInstances.getNumLabels();i++){
//                labels.get(i).getClassificationArray()[instanceIndex] = (int) mInstances.getDataSet().get(j).value(labelIndices[i]);
//            }
//            instanceIndex++;
//        }

        for (Iterator<?> iterator = instances.iterator(); iterator.hasNext();) {
            Instance instance = (Instance) iterator.next();
            aux = instance.toDoubleArray();

            for(int i=0;i<mInstances.getNumLabels();i++){
                labels.get(i).getClassificationArray()[instanceIndex] = (int)aux[labelIndices[i]];
            }
            instanceIndex++;
        }
        //statingTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        //System.out.println("End build at "+statingTimestamp.toString());
        return labels;
    }

    private void initializeOriginalSimilarityMatrixx(ClusterOfLabels cluster){
        int dim = cluster.getLabels().size();
        originalSimilarityMatrixx = new SimilarityMatrix(dim);
        Label l1,l2;
        for(int i=0;i<dim;i++){
            l1 = cluster.getLabels().get(i);
            for(int j=0;j<dim;j++){
                if(i!=j){
                    l2 = cluster.getLabels().get(j);
                    originalSimilarityMatrixx.setSimilarityMatrixElement(i, j,
                            new SimilarityMatrixElement(l1.getId(), LabelDistance.getDistance(l1, l2, SimilarityMeasureEnum.EuclideanDistance)));
                }
                else{
                    originalSimilarityMatrixx.setSimilarityMatrixElement(i, j,
                            new SimilarityMatrixElement(l1.getId(),0.0));
                }
            }
        }
        l1=null;
        l2 = null;
    }

    private ClusterOfLabels getBiggestCluster(ArrayList<ClusterOfLabels> clusters){

        int i=0,max=0;
        ClusterOfLabels cluster = clusters.get(0);
        max = cluster.getLabels().size();

        for(i=1;i<clusters.size();i++){
            if(clusters.get(i).getLabels().size()>max){
                max = clusters.get(i).getLabels().size();
                cluster = clusters.get(i);
            }
        }
        return cluster;
    }

    private SimilarityMatrix getSimilarityMatrixx(ClusterOfLabels cluster){

        ArrayList<Label> labels = cluster.getLabels();
        int dim = labels.size();
        SimilarityMatrix m = new SimilarityMatrix(dim);
        Label l1,l2;
        for(int i=0;i<dim;i++){
            l1 = labels.get(i);
            for(int j=0;j<dim;j++){
                if(i!=j){
                    l2 = labels.get(j);
                    m.setSimilarityMatrixElement(i, j,
                            new SimilarityMatrixElement(l1.getId(),
                                    originalSimilarityMatrixx.getSimilarityMatrixElement(l1.getId(), l2.getId()).getDistance()));
                }
                else{
                    m.setSimilarityMatrixElement(i, j,
                            new SimilarityMatrixElement(l1.getId(),0.0));
                }
            }
        }
        l1 = null;
        l2 = null;
        labels=null;
        return m;
    }

    private Measure getBiggestAverageSimilarityy(SimilarityMatrix m){

        int index=0,id=0,dim = m.getDim();
        double average=0,sum = 0,maxAverage=-999999.0,aux=0;
        StringBuilder s = null;
        for(int i=0;i<dim;i++){
            sum = 0;
            for(int j=0;j<dim;j++){
                aux = m.getSimilarityMatrixElement(i, j).getDistance();
                sum+=aux;
                id=m.getSimilarityMatrixElement(i, j).getId();
            }
            average = sum/(dim-1);

            if(average>=maxAverage){
                maxAverage = average;
                index=id;
            }
        }
        return new Measure(index, maxAverage);
    }

    private Measure getSimilaritiesFromNewClusterr(SimilarityMatrix m,ArrayList<Integer>whoOut){

        int numberOfOutElements = whoOut.size();
        int dim = m.getDim();
        int index=-1;
        double siAverage = 0,siaux=0;
        double saAverage = 0,saaux=0;
        double diff = 0;
        double maxDiff = -999999;
        StringBuilder sa=null,si=null,d = null;
        for(int i=0;i<dim;i++){
            if(!whoOut.contains(m.getSimilarityMatrixElement(i, 0).getId())){
                siAverage = 0;
                saAverage = 0;
                diff = 0;
                for(int j=0;j<dim;j++){
                    if(i!=j){
                        if(whoOut.contains(m.getSimilarityMatrixElement(j, 0).getId())){
                            saaux = m.getSimilarityMatrixElement(i, j).getDistance();
                            saAverage+=saaux;
                        }
                        else{
                            siaux = m.getSimilarityMatrixElement(i, j).getDistance();
                            siAverage+= siaux;
                        }
                    }
                }
                diff = (siAverage/((dim-1)-numberOfOutElements)) - (saAverage/(numberOfOutElements));
                if(maxDiff<=diff){
                    maxDiff = diff;
                    index = m.getSimilarityMatrixElement(i, 0).getId();
                }
            }
        }
        return new Measure(index,maxDiff);
    }

    private boolean hasXLabelsPerCluster(ArrayList<ClusterOfLabels> labels,int number){

        for(int i=0;i<labels.size();i++){
            if(labels.get(i).getLabels().size()>number){
                return false;
            }
        }
        return true;
    }

    private void divideToUniqueLabelPerCluster(ArrayList<ClusterOfLabels> clusterList,int j){

        ClusterOfLabels cluster=null;
        SimilarityMatrix sMatrix;
        int index = 0;
        double similarityAverage;
        double maxSimilarityAverage = -999999999;
        do{
            for(int i=0;i<clusterList.size();i++){
                cluster = clusterList.get(i);
                if(cluster.getLabels().size()>1){
                    sMatrix = getSimilarityMatrixx(cluster);
                    similarityAverage = getBiggestAverageSimilarityy(sMatrix).getValue();
                    if(similarityAverage>maxSimilarityAverage){
                        index = i;
                    }
                }
            }

            //ClusterOfLabels firstBro = ClusterOfLabels.newInstance(clusterList.get(index));
            ClusterOfLabels firstBro = clusterList.get(index).newInstance();
            firstBro.setId(++j);
            firstBro.setFather_id(clusterList.get(index).getId());
            Label label = firstBro.removeLabel(firstBro.getLabels().get(0).getId());
            ClusterOfLabels secondBro = new ClusterOfLabels();
            secondBro.setId(++j);
            secondBro.setFather_id(firstBro.getFather_id());
            secondBro.addLabel(label);

            clusterList.remove(clusterList.get(index));
            clusterList.add(firstBro);
            clusterList.add(secondBro);
            firstBro = null;
            secondBro = null;

        }while(!hasXLabelsPerCluster(clusterList,1));
    }

    private ArrayList<ClusterOfLabels> run(MultiLabelInstances trainingSet){
    //private ArrayList<String>[] run(MultiLabelInstances trainingSet){

        int j=1;
        clusterList = new ArrayList<ClusterOfLabels>();
        ArrayList<Integer> whoOut;
        ClusterOfLabels root = new ClusterOfLabels();
        SimilarityMatrix sMatrix;
        Measure biggestAverage;
        ClusterOfLabels clus;

        root.setLabels(getLabelsArray(trainingSet));
        root.setId(j);
        root.setFather_id(j);
        clusterList.add(root);

        boolean control = false;
        initializeOriginalSimilarityMatrixx(root);
//        printClusterList(clusterList);

        do{
            clus = getBiggestCluster(clusterList);
            ClusterOfLabels auxClus = clus.newInstance();
            auxClus.setId(++j);
            auxClus.setFather_id(clus.getId());
            sMatrix = getSimilarityMatrixx(auxClus);
            biggestAverage = getBiggestAverageSimilarityy(sMatrix);
            ClusterOfLabels	clus2 = new ClusterOfLabels();
            whoOut = new ArrayList<Integer>();
            j++;
            while(biggestAverage.getValue()>=0){

                Label labelRemoved = auxClus.removeLabel(biggestAverage.getID());
                clus2.addLabel(labelRemoved);
                clus2.setId(j);
                clus2.setFather_id(auxClus.getFather_id());
                whoOut.add(biggestAverage.getID());
                biggestAverage = getSimilaritiesFromNewClusterr(sMatrix,whoOut);
            }
            clusterList.remove(clus);
            clusterList.add(auxClus);
            clusterList.add(clus2);

            if(clusterList.size() == mNumClusters){
                control = true;
                break;
            }
            clus=null;

        }while(!hasXLabelsPerCluster(clusterList,2));

        if(!control){
            divideToUniqueLabelPerCluster(clusterList,j);
        }

//        ArrayList<String>[] childrenLabels = new ArrayList[clusterList.size()];
//
//        ArrayList<String> array ;
//        for(int i=0;i<clusterList.size();i++){
//            array = new ArrayList<>();
//                for(int k=0;k<clusterList.get(i).getLabels().size();k++){
//                        array.add(clusterList.get(i).getLabels().get(k).getLabelName());
//                }
//                childrenLabels[i] = array;
//        }
//
//        return childrenLabels;
        return clusterList;
    }

    public MultiLabelInstances getMLIFromLabels(ClusterOfLabels labels){

        int firstLabelIndex = labelIndices[0];
        int[] indexs = new int[labels.getLabels().size()];
        int[] indexToRemove = new int[numLabels-labels.getLabels().size()];
        Instances shell=null;
        LabelsMetaDataImpl m = ((LabelsMetaDataImpl) mInstances.getLabelsMetaData().clone());

        //Todo devo ordenar os labels? Investigar
        int control;
        int pos =0;
        for(int j=0;j<labelNames.length;j++){
            control = 0;
            for(int k=0;k<labels.getLabels().size();k++){
                if(labelNames[j].equals(labels.getLabels().get(k).getLabelName())){
                    control = 1;
                    break;
                }
            }
            if(control!=1){
                indexToRemove[pos]=firstLabelIndex+j;
                pos++;
                m.removeLabelNode(labelNames[j]);
            }

        }

        LabelsMetaData labelsMetaData = m;
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indexToRemove);
        try {
            remove.setInputFormat(mInstances.getDataSet());
            shell = Filter.useFilter(mInstances.getDataSet(), remove);
            return new MultiLabelInstances(shell,labelsMetaData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void buildInternal(MultiLabelInstances trainingSet) throws Exception {

//        ArrayList<String>[] clusters = run(trainingSet);
        mInstances = trainingSet;
        ArrayList<ClusterOfLabels> clusters = run(trainingSet);

        ensemble = new MultiLabelLearner[clusters.size()];
        MultiLabelInstances[] data = new MultiLabelInstances[clusters.size()];

        for(int i=0;i<clusters.size();i++){
            data[i] = getMLIFromLabels(clusters.get(i));
            ensemble[i] = baseLearner.makeCopy();
            ensemble[i].build(data[i]);
        }

    }

    @Override
    protected MultiLabelOutput makePredictionInternal(Instance instance) throws Exception, InvalidDataException {

        MultiLabelOutput multiLabelOutput[] = new MultiLabelOutput[ensemble.length];

        for(int i=0;i<multiLabelOutput.length;i++){
            multiLabelOutput[i] = ensemble[i].makePrediction(instance);
        }

        //Todo - Combinar essas saidas para resoltar num único MultiLabelOuput

        return null;
    }
}
