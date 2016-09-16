package at.lechner.weka.option;

import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyREPTreeOption extends MyOption {

	private REPTree repTree;

	public MyREPTreeOption(REPTree repTree, Instances trainingsData) throws Exception {
		super(repTree, trainingsData);
		this.repTree = repTree;
	}

	public MyREPTreeOption(REPTree repTree, Instances trainingsData, int numFolds) throws Exception {
		super(repTree, trainingsData, numFolds);
		this.repTree = repTree;
	}

	public MyREPTreeOption(REPTree repTree, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(repTree, options, trainingsData, numFolds);
		this.repTree = repTree;
	}

	public MyREPTreeOption(REPTree repTree, String[] options, Instances trainingsData) throws Exception {
		super(repTree, options, trainingsData);
		this.repTree = repTree;
	}

	public MyREPTreeOption(REPTree repTree, String options, Instances trainingsData, int numFolds) throws Exception {
		super(repTree, options, trainingsData, numFolds);
		this.repTree = repTree;
	}

	public MyREPTreeOption(REPTree repTree, String options, Instances trainingsData) throws Exception {
		super(repTree, options, trainingsData);
		this.repTree = repTree;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		repTree.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		repTree.setOptions(Utils.splitOptions(options));
	}

}
