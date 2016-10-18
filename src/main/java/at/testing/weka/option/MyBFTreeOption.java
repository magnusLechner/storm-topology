package at.testing.weka.option;

import weka.classifiers.trees.BFTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyBFTreeOption extends MyOption {

	private BFTree bfTree;

	public MyBFTreeOption(BFTree bfTree, Instances trainingsData) throws Exception {
		super(bfTree, trainingsData);
		this.bfTree = bfTree;
	}

	public MyBFTreeOption(BFTree bfTree, Instances trainingsData, int numFolds) throws Exception {
		super(bfTree, trainingsData, numFolds);
		this.bfTree = bfTree;
	}

	public MyBFTreeOption(BFTree bfTree, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(bfTree, options, trainingsData, numFolds);
		this.bfTree = bfTree;
	}

	public MyBFTreeOption(BFTree bfTree, String[] options, Instances trainingsData) throws Exception {
		super(bfTree, options, trainingsData);
		this.bfTree = bfTree;
	}

	public MyBFTreeOption(BFTree bfTree, String options, Instances trainingsData, int numFolds) throws Exception {
		super(bfTree, options, trainingsData, numFolds);
		this.bfTree = bfTree;
	}

	public MyBFTreeOption(BFTree bfTree, String options, Instances trainingsData) throws Exception {
		super(bfTree, options, trainingsData);
		this.bfTree = bfTree;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		bfTree.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		bfTree.setOptions(Utils.splitOptions(options));
	}
	
}
