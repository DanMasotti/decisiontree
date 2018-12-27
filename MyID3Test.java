package decisiontree;

import org.hamcrest.Matcher;

import org.junit.Test;

import support.decisiontree.DataReader;
import support.decisiontree.DecisionTreeData;

import java.util.Arrays;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import support.decisiontree.DecisionTreeData;

import java.lang.Math;
import support.decisiontree.DecisionTreeNode;
import support.decisiontree.ID3;
import support.decisiontree.Attribute;

import java.util.Arrays;

import jdk.internal.util.xml.impl.Attrs;

/**
 * This class can be used to test the functionality of your MyID3 implementation.
 * Use the Heap stencil and your heap tests as a guide!
 * 
 */

public class MyID3Test {
	
	@Test
	public void simpleTest() {
	    
	    MyID3 id3 = new MyID3();

	    // This creates a DecisionTreeData object that you can use for testing.
	    DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
	    // FILL
	    
	    /*
	     * 
	     */
	    assert(!id3.isDataEmpty(shortData));
	    assert(!id3.isSame(shortData));
	    assert(!id3.isAttrEmpty(shortData));
	}
	
	/**
	 * TODO: add your tests below!
	 */
	
	@Test
	public void testCaseCheck(){
		//check if I'm getting the recursive case as expected
		MyID3 id3 = new MyID3();
		DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
		assert(id3.caseChecker(shortData)==4);
	}
	
	
	@Test
	public void testArgMax(){
		//am I getting the most important attribute?
		MyID3 id3 = new MyID3();
		DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
		assert(id3.argMax(shortData).getName().equals(" Pat"));
	}
	
	@Test
	public void testMajorityClassification(){
		//am I getting the majority classification
		MyID3 id3 = new MyID3();
		DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
		assert(id3.getMajorityClassification(shortData).equals(" true"));
	}
	
	@Test
	public void testEntropy(){
		//testing if the entropy is working for simple proportions -- 
		//I used a wolfram widget to find the actual entropy values
		MyID3 id3 = new MyID3();
		assert(id3.boolEntropy(1,1) == 1);
		assert(id3.boolEntropy(2, 1) >= .9 && id3.boolEntropy(2,1) <= .92);
		assert(id3.boolEntropy(3, 1) >= .80 && id3.boolEntropy(3, 1) <= .82);
		assert(id3.boolEntropy(0, 1) == 0);
	}
	
	@Test
	public void testRemainder(){
		//am I calculating the right remainder
		MyID3 id3 = new MyID3();
		DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
		Attribute maxAtt = id3.argMax(shortData);
		double remainder = id3.remainder(shortData, maxAtt);
		assert(remainder >= .45 && remainder <= .55);
	}
	
	@Test
	public void testGainCalc(){
		//am I calculating the right information gain?
		MyID3 id3 = new MyID3();
		DecisionTreeData shortData = DataReader.readFile("/course/cs0160/lib/decisiontree-data/short-data-training.csv");
		Attribute maxAtt = id3.argMax(shortData);
		//the sixes come from the patron's attribute number of p and n
		double gain = id3.calcGain(6,6,shortData, maxAtt);
		assert(gain >= .5 && gain <= .6);
	}
	
}