package decisiontree;

import support.decisiontree.DecisionTreeData;

import java.lang.Math;
import support.decisiontree.DecisionTreeNode;
import support.decisiontree.ID3;
import support.decisiontree.Attribute;

import java.util.Arrays;

import jdk.internal.util.xml.impl.Attrs;

/**
  * This class is where your ID3 algorithm should be implemented.
  */
public class MyID3 implements ID3 {

    /**
     * Constructor. You don't need to edit this.
     */
    public MyID3() {
        
    }

    /**
     * This is the trigger method that actually runs the algorithm.
     * This will be called by the visualizer when you click 'train'.
     */
    @Override
    public DecisionTreeNode id3Trigger(DecisionTreeData data) {
        // TODO run the algorithm, return the root of the tree
        return this.myID3Algorithm(data, null);
    }

    /*
     * this is my id3 algo
     * purpose: build a decision tree
     * input: decision tree data  and 'parent data' which is null in the first run-through
     */
    private DecisionTreeNode myID3Algorithm(DecisionTreeData data, DecisionTreeData parentData) {
    	DecisionTreeNode node = new DecisionTreeNode();
    	String classfic = new String();
    	switch(this.caseChecker(data)){
    		case 1:
    		//empty data -- return new node w/ the most frequent classification in parent data
    			classfic = this.getMajorityClassification(parentData);
    			node.setElement(classfic);
    			break;
    		case 2:
    		//same classifications -- return new node w/ that classification
    			classfic = data.getExamples()[0][data.getExamples()[0].length-1];
    			node.setElement(classfic);
    			break;
    		case 3:
    		//empty attributes -- return node w/ most frequent classification in data
    			classfic = this.getMajorityClassification(data);
    			node.setElement(classfic);
    			break;
    		case 4:
    			/*
    			 * THIS IS THE MEAT OF THE ID3 -- recursive part
    			 * I find the attribute with the largest gain
    			 * for each value of this attribute
    			 * 		new data = all examples in data such that ex.attribute = val
    			 * 		node = return id3 with new data
    			 * 		child that node 
    			 * return the node
    			 */
    			Attribute maxA = this.argMax(data);
    			node = new DecisionTreeNode();
    			node.setElement(maxA.getName());
    			java.util.Set<java.lang.String> values = maxA.getValues();
    			java.util.ArrayList<Attribute> attrs = data.getAttributeList();
    			java.util.ArrayList<Attribute> newAttrs = new java.util.ArrayList<Attribute>(attrs);
    			newAttrs.remove(maxA);
    			for (String val : values){
    				java.lang.String[][] examples = this.createNewExamples(data,maxA, val);
    				classfic = this.getMajorityClassification(data);
    				DecisionTreeData newData = new DecisionTreeData(examples, newAttrs, data.getClassifications());
    				node.addChild(val, this.myID3Algorithm(newData, data));
    			}
    			break;
    	}
    	return node;
    }
    
    public int caseChecker(DecisionTreeData data){
    	/*
    	 * purpose: helper method to see which case I should use for the id3 algo, mostly for readability
    	 * input: the data
    	 * output: an int representing the case I should follow in the id3 algo, 
    	 * helps me see what case I'm following
    	 */
    	if (this.isDataEmpty(data)){
    		return 1;
    	}
    	else if (this.isSame(data)){
    		return 2;
    	}
    	else if (this.isAttrEmpty(data)) {
    		return 3;
    	}
    	else {
    		return 4;
    	}
    }
    
    public boolean isDataEmpty(DecisionTreeData data){
    	//checks if the data is empty
    	if (data.getExamples().length == 0){
    		return true;
    	}
    	return false;
    }
    
    public boolean isSame(DecisionTreeData data){
    	//checks if every classification is the same
    	java.lang.String[][] examples = data.getExamples();
    	for (int row = 0; row < examples.length-1;row++){
    		//if a classification doesnt match the next row's, return false
    		if (!(examples[row][examples[row].length-1].equals(examples[row+1][examples[row+1].length-1]))){
    			return false;
    		}
    	}
    	return true;
    }
    
    public boolean isAttrEmpty(DecisionTreeData data){
    	//checks if the attribute list is empty
    	if (data.getAttributeList().isEmpty()){
    		return true;
    	}
    	return false;
    }
    	
    public Attribute argMax(DecisionTreeData data){
    	/*
    	 * purpose: get the attribute with the largest information gain
    	 * input: the data
    	 * output: the attribute with the largest information gain
    	 */
    	double maxGain = 0;
    	java.util.ArrayList<Attribute> attrList = data.getAttributeList();
    	Attribute argMax = attrList.get(0);
    	String[] classfics = data.getClassifications();
    	String[][] examples = data.getExamples();
    	for (Attribute attr : attrList){
    		int p = 0;
    		int n = 0;
    		//count the classifications in the examples
    		for (int row=0; row < examples.length; row++){
        		if (examples[row][examples[row].length-1].equals(classfics[0])){
        			p++;
        		}
    			else{
    				n++;
    			}
    		}
    		//typical argmax condition structure
    		double myGain = this.calcGain(p, n, data, attr);
    		if (myGain > maxGain){
    			argMax = attr;
    			maxGain = myGain;
    		}
    	}
    	return argMax;
    }
    
    public double calcGain(int pos, int neg , DecisionTreeData data, Attribute attr){
    	/*
    	 * purpose: calculates the information gain from using a particular attribute
    	 * input: number of examples classified positive, negative, the data, and that data's attributes
    	 * output: a number representing the information gained from using this attribute to split on
    	 */
    	double entropy = this.boolEntropy(pos, neg);
    	double remainder = this.remainder(data, attr);
    	double gain = entropy - remainder;
    	return gain;
    }
    
    public double remainder(DecisionTreeData data, Attribute attr){
    	/*
    	 * purpose: this helper method implements the formula for calculating the remainder
    	 * input: the data, and an attribute
    	 * output: a number that represents the remainder
    	 */
    	double remainder = 0;
    	java.util.Set<java.lang.String> values = attr.getValues();
    	java.lang.String[] classfics = data.getClassifications();
    	String [][] examples = data.getExamples();
    	for (java.lang.String val: values){
    		double pK = 0;
    		double nK = 0;
    		for (int row=0; row < examples.length; row++){
    			if (examples[row][attr.getColumn()].equals(val)){
    				if (examples[row][examples[row].length-1].equals(classfics[0])){
    					pK++;
    				}	
    				else{
    					nK++;
    				}
    			}
    		}
    		if (pK + nK == 0){
    			continue;
    		}
    		double prop = ((double) (pK + nK))/(examples.length);
    		remainder += (prop * this.boolEntropy(pK, nK));
    	}
    	return remainder;
    }
    
    public double boolEntropy(double p, double n){
    	/*
    	 * purpose: calculate the entropy H(s)
    	 * input: the number of examples classified as positive, and negative
    	 * output: a double representing the entropy of this example
    	 */
    	double q = ((double) p) / (p+n);
    	return -(q*this.log_2(q)+(1-q)*this.log_2(1-q));
    }
    
    public double log_2(double q){
    	/*
    	 * purpose:calculates log base 2, with log 0 = 0
    	 */
    	if (q==0.0){
    		return 0;
    	}
    	else {
    		return (Math.log(q))/(Math.log(2));
    	}
    }
    
    public String[][] createNewExamples(DecisionTreeData data, Attribute attr, String val){
    	/*
    	 * purpose: creates a new set of examples by looking at the old data and matching to val
    	 * input: the data, attribute, and the value we're matching 
    	 * output: a string 2d array filled with the new data
    	 */
   
    	String[][] examples = data.getExamples();
    	int newRowsNum = 0; 
    	for (int row = 0; row < examples.length; row++ ){
    		if ((examples[row][attr.getColumn()]).equals(val)){
    			newRowsNum++;
    		}
    	}
    	String[][] newExamples = new String[newRowsNum][examples[0].length];
    	int tmp = 0;
    	for (int row = 0; row < examples.length; row++){
    		if ((examples[row][attr.getColumn()]).equals(val)){
    			newExamples[tmp] = examples[row];
    			tmp++;
    		}
    	}
    	return newExamples;
    }
    
    public String getMajorityClassification(DecisionTreeData data){
    	/*
    	 * purpose: gets the most frequent classification
    	 * input: data
    	 * output: the number of trues/false
    	 */
    	String[][] examples = data.getExamples();
    	String pos = data.getClassifications()[0];
    	String neg = data.getClassifications()[1];
    	int posCount = 0;
    	int negCount = 0;
    	for (int row = 0; row<examples.length; row++){
    		if (examples[row][examples[row].length-1].equals(pos)){
    			posCount++;
    		}
    		else{
    			negCount++;
    		}	
    }
    	if (posCount > negCount){
    		return pos;
    	}
    	else{
    		return neg;
    	}
    }
}
