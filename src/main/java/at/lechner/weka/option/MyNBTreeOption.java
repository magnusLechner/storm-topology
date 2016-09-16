package at.lechner.weka.option;

import weka.classifiers.trees.NBTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyNBTreeOption extends MyOption {

	private NBTree nbTree;

	public MyNBTreeOption(NBTree nbTree, Instances trainingsData) throws Exception {
		super(nbTree, trainingsData);
		this.nbTree = nbTree;
	}

	public MyNBTreeOption(NBTree nbTree, Instances trainingsData, int numFolds) throws Exception {
		super(nbTree, trainingsData, numFolds);
		this.nbTree = nbTree;
	}

	public MyNBTreeOption(NBTree nbTree, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(nbTree, options, trainingsData, numFolds);
		this.nbTree = nbTree;
	}

	public MyNBTreeOption(NBTree nbTree, String[] options, Instances trainingsData) throws Exception {
		super(nbTree, options, trainingsData);
		this.nbTree = nbTree;
	}

	public MyNBTreeOption(NBTree nbTree, String options, Instances trainingsData, int numFolds) throws Exception {
		super(nbTree, options, trainingsData, numFolds);
		this.nbTree = nbTree;
	}

	public MyNBTreeOption(NBTree nbTree, String options, Instances trainingsData) throws Exception {
		super(nbTree, options, trainingsData);
		this.nbTree = nbTree;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		nbTree.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		nbTree.setOptions(Utils.splitOptions(options));
	}
	
}
