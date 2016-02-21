/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

//package mulan.classifier.meta;
package br.com.uff.ic.tarcisio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import mulan.classifier.MultiLabelLearner;
import mulan.classifier.MultiLabelLearnerBase;
import mulan.classifier.MultiLabelOutput;
import mulan.classifier.meta.HierarchyBuilder.Method;
import mulan.classifier.meta.MultiLabelMetaLearner;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.DataUtils;
import mulan.data.LabelNode;
import mulan.data.LabelsMetaData;
import mulan.data.MultiLabelInstances;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;

/**
 <!-- globalinfo-start -->
 * Class implementing the Hierarchy Of Multi-labEl leaRners algorithm. For more information, see<br/>
 * <br/>
 * Grigorios Tsoumakas, Ioannis Katakis, Ioannis Vlahavas: Effective and Efficient Multilabel Classification in Domains with Large Number of Labels. In: Proc. ECML/PKDD 2008 Workshop on Mining Multidimensional Data (MMD'08), 2008.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;inproceedings{Tsoumakas2008,
 *    author = {Grigorios Tsoumakas and Ioannis Katakis and Ioannis Vlahavas},
 *    booktitle = {Proc. ECML/PKDD 2008 Workshop on Mining Multidimensional Data (MMD'08)},
 *    title = {Effective and Efficient Multilabel Classification in Domains with Large Number of Labels},
 *    year = {2008},
 *    location = {Antwerp, Belgium}
 * }
 * </pre>
 * <p/>
 <!-- technical-bibtex-end -->
 *
 * @author Grigorios Tsoumakas
 * @version 2012.02.27
 */
public class HCDC extends MultiLabelMetaLearner {

    private final int numClusters;
	private final HierarchyBuilderForHCDC.Method method;
	private MultiLabelLearner[] classifier;
    private HierarchyBuilderForHCDC hb;
    private Instances[] header;
    //private mulan.classifier.meta.HierarchyBuilderForHCDC.Method method;
    private MultiLabelInstances[] m;
    //private Instances clusterML;
    private int[] numMetaLabels;
    private int totalNumMetaLabels;
    private int[] order;
    

    /**
     * Default constructor
     */
    public HCDC() {
        super(new BinaryRelevance(new J48()));
        method = HierarchyBuilderForHCDC.Method.BalancedClustering;
        numClusters = 3;
        m=null;
    }

    /**
     * Creates a new instance based on given multi-label learner, number of 
     * children and partitioning method
     * 
     * @param mll multi-label learner
     * @param clusters number of partitions
     * @param method partitioning method
     */
    public HCDC( MultiLabelLearner mll, int clusters, HierarchyBuilderForHCDC.Method method) {
        super(mll);
        this.method = method;
        numClusters = clusters;
    }

    @Override
    protected void buildInternal(MultiLabelInstances trainingSet) throws Exception {
        debug("Learning the hierarchy of models");
        this.labelIndices=trainingSet.getLabelIndices();
        this.numLabels=trainingSet.getNumLabels();
        order=new int[trainingSet.getNumLabels()];
        hb = new HierarchyBuilderForHCDC(numClusters, method);
        LabelsMetaData[] labelHierarchy = hb.buildListLabelHierarchy(trainingSet);
       // labelHierarchy.
        debug("Constructing the hierarchical multilabel dataset");
        MultiLabelInstances meta[] = new MultiLabelInstances[labelHierarchy.length];
		for (int i = 0; i < meta.length; i++) {
			List<String> labels=new ArrayList<String>();
			System.out.println(labelHierarchy[i].getLabelNames());
			System.out.println();
			for(String s:labelHierarchy[i].getLabelNames()){
				for (String s2: trainingSet.getLabelsMetaData().getLabelNames()) {
					if(s.equals(s2)){
						labels.add(s);
						break;
					}
				}
			}
			meta[i]=HierarchyBuilderForHCDC.createHierarchicalDataset(hb.getCluster(trainingSet,labels), labelHierarchy[i]);
			
		}
		m=meta;
		header=new Instances[meta.length];
        classifier = new MultiLabelLearnerBase[meta.length];
        for (int j = 0; j < meta.length; j++) {
			header[j] = new Instances(meta[j].getDataSet(), 0);
			debug("Training the hierarchical classifier");
			System.out.println("j=" +j+"   de  "+(meta.length-1));
			System.out.println(meta[j].getLabelNames());
			classifier[j] = getBaseLearner().makeCopy();
			classifier[j].setDebug(getDebug());
			classifier[j].build(meta[j]);
        }
        Set<String> leafLabels = trainingSet.getLabelsMetaData().getLabelNames();
        Set<String> metaLabels;
		numMetaLabels=new int[meta.length]; 
		totalNumMetaLabels=0;
        for (int i = 0; i < meta.length; i++) {
			metaLabels = labelHierarchy[i].getLabelNames();
			for (String string : leafLabels) {
				metaLabels.remove(string);
			}
			numMetaLabels[i]= metaLabels.size();
			totalNumMetaLabels+=metaLabels.size();
		}
        int pos=0;
		for (int i = 0; i < m.length; i++) {
			int[] listI = m[i].getLabelIndices();
			for (int j = 0; j < m[i].getNumLabels() - numMetaLabels[i]; j++) {
				order[pos]=listI[j];
				pos++;
			}
		}
		order=SortOrder();
		System.out.println();
    }
    protected MultiLabelOutput makePredictionInternal(Instance instance) throws Exception {
        Instance transformed = DataUtils.createInstance(instance, instance.weight(), instance.toDoubleArray());
        
        for (int i = 0; i < totalNumMetaLabels; i++) {
            transformed.insertAttributeAt(transformed.numAttributes());
        }
        boolean[][] newBipartition=new boolean[classifier.length][] ;
		double[][] newConfidences= new double[classifier.length][];
		int c=0;
		
		for (int i = 0; i < classifier.length; i++) {
			transformed.setDataset(header[i]);
			MultiLabelOutput mlo = classifier[i].makePrediction(transformed);
			boolean[] oldBipartition = mlo.getBipartition();
			newBipartition[i] = new boolean[m[i].getNumLabels()-numMetaLabels[i]];			
			System.arraycopy(oldBipartition, 0, newBipartition[i], 0, oldBipartition.length-numMetaLabels[i]);
			
			double[] oldConfidences = mlo.getConfidences();
			newConfidences[i] = new double[m[i].getNumLabels()-numMetaLabels[i]];
			System.arraycopy(oldConfidences, 0, newConfidences[i], 0, oldConfidences.length-numMetaLabels[i]);
			
			c+=newConfidences[i].length;
		}
		boolean[] bipartition=new boolean[c];
		double[] confidences=new double [c];
		c=0;
		for (int j = 0; j <classifier.length ; j++) {
			for (int i = 0; i < newBipartition[j].length ; i++) {
				bipartition[order[c]]=newBipartition[j][i];
				confidences[order[c]]=newConfidences[j][i];
				c++;
			}
		}
		MultiLabelOutput newMLO = new MultiLabelOutput(bipartition, confidences);
		return newMLO;	
    }
    protected MultiLabelOutput makePredictionInternal(Instance instance, int classi) throws Exception {
        Instance transformed = DataUtils.createInstance(instance, instance.weight(), instance.toDoubleArray());
        for (int i = 0; i < totalNumMetaLabels; i++) {
            transformed.insertAttributeAt(transformed.numAttributes());
        }
        
        MultiLabelOutput mlo = classifier[classi].makePrediction(transformed);
		boolean[] oldBipartition = mlo.getBipartition();
		//System.out.println("old:" + Arrays.toString(oldBipartition));
		boolean[] newBipartition = new boolean[numLabels];
		//System.arraycopy(oldBipartition, 0, newBipartition, 0, numLabels);
		for(int i=0;i<oldBipartition.length;i++){
			if(i==numLabels) break;
			newBipartition[i]=oldBipartition[i];
		}
		//System.out.println("new:" + Arrays.toString(newBipartition));
		double[] oldConfidences = mlo.getConfidences();
		double[] newConfidences = new double[numLabels];
		//System.arraycopy(oldConfidences, 0, newConfidences, 0, numLabels);
		for(int i=0;i<oldConfidences.length;i++){
			if(i==numLabels) break;
			newConfidences[i]=oldConfidences[i];
		}
		MultiLabelOutput newMLO = new MultiLabelOutput(newBipartition, newConfidences);
		return newMLO;
    }

    @Override
    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result = new TechnicalInformation(Type.INPROCEEDINGS);
        result.setValue(Field.AUTHOR, "Grigorios Tsoumakas and Ioannis Katakis and Ioannis Vlahavas");
        result.setValue(Field.TITLE, "Effective and Efficient Multilabel Classification in Domains with Large Number of Labels");
        result.setValue(Field.BOOKTITLE, "Proc. ECML/PKDD 2008 Workshop on Mining Multidimensional Data (MMD'08)");
        result.setValue(Field.LOCATION, "Antwerp, Belgium");
        result.setValue(Field.YEAR, "2008");
        return result;
    }

    //spark temporary edit for complexity measures   
    
    
    /**
     * Returns the number of nodes
     * 
     * @return number of nodes
     */
/*    public long getNoNodes() {
        return classifier.getNoNodes();
    }

    /**
     * Returns the number of classifier evaluations
     * 
     * @return number of classifier evaluations
     */
  /*  public long getNoClassifierEvals() {
        return classifier.getNoClassifierEvals();
    }

    /**
     * Returns the total number of instances used for training
     * 
     * @return total number of instances used for training
     */
   /* public long getTotalUsedTrainInsts() {
        return classifier.getTotalUsedTrainInsts();
    }
*/
    public void Imprimir(){
    	 FileWriter arq=null;
		try {
			arq = new FileWriter("dados.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	PrintWriter gravarArq = new PrintWriter(arq);
    	System.out.printf("\n \n \n");
    	if(arq!=null) gravarArq.printf("Numero de clusters= "+m.length+"\n");
    	System.out.println("Numero de clusters= "+m.length);
    	for(int i=0; i<m.length;i++){
    		if(arq!=null) gravarArq.printf("cluster "+i+": Numero de r�tulos= "+
    	    		m[i].getNumLabels()+"   Numero de instancias= "+m[i].getNumInstances());
    		if(arq!=null) gravarArq.printf(m[i].getLabelNames()+"\n");
    		System.out.println(m[i].getLabelNames());
    		System.out.println("cluster "+i+": Numero de r�tulos= "+
    				m[i].getNumLabels()+"   Numero de instancias= "+m[i].getNumInstances());
    		System.out.println(m[i].getLabelNames());
    		if(arq!=null) gravarArq.printf(m[i].getLabelNames()+"\n");
    		System.out.println("---------------------------------------");
			MultiLabelOutput mlo;
			System.out.println("Bipartition / raking / confidence / pValues");
			if(arq!=null) gravarArq.printf("Bipartition / raking / confidence / pValues\n");
			try {
				for(int j=0; j<m[i].getNumInstances();j++){
    			
					mlo=makePredictionInternal(m[i].getDataSet().get(j), i);
					System.out.printf(mlo.getBipartition()+" / "+
							mlo.getRanking()+" / "+ mlo.getConfidences()+" /  "+
							mlo.getPvalues()+"\n");
					if(arq!=null) gravarArq.printf(mlo.getBipartition()+" / "+
							mlo.getRanking()+" / "+ mlo.getConfidences()+" /  "+
							mlo.getPvalues()+"\n");
    		}
    		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	System.out.printf("\n \n \n");
    }
    public MultiLabelInstances[] getMLIarray(){
    	return this.m;
    }
    private int[] SortOrder(){
    	int[] temp=new int[order.length];
    	for(int i=0;i<order.length;i++){
    		for(int j=0;j<this.numLabels;j++){
    			if(order[i]==this.labelIndices[j]){
    				temp[i]=j;
    				break;
    			}
    		}
    	}
    	return temp;
    }
    
    public String globalInfo() {
        return "Class implementing the Hierarchy Of Multi-labEl leaRners " +
               "algorithm. For more information, see\n\n"
                + getTechnicalInformation().toString();
    }
}