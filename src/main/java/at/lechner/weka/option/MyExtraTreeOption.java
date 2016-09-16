package at.lechner.weka.option;

import weka.classifiers.trees.ExtraTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyExtraTreeOption extends MyOption {

	private ExtraTree extraTree;

	public MyExtraTreeOption(ExtraTree extraTree, Instances trainingsData) throws Exception {
		super(extraTree, trainingsData);
		this.extraTree = extraTree;
	}

	public MyExtraTreeOption(ExtraTree extraTree, Instances trainingsData, int numFolds) throws Exception {
		super(extraTree, trainingsData, numFolds);
		this.extraTree = extraTree;
	}

	public MyExtraTreeOption(ExtraTree extraTree, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(extraTree, options, trainingsData, numFolds);
		this.extraTree = extraTree;
	}

	public MyExtraTreeOption(ExtraTree extraTree, String[] options, Instances trainingsData) throws Exception {
		super(extraTree, options, trainingsData);
		this.extraTree = extraTree;
	}

	public MyExtraTreeOption(ExtraTree extraTree, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(extraTree, options, trainingsData, numFolds);
		this.extraTree = extraTree;
	}

	public MyExtraTreeOption(ExtraTree extraTree, String options, Instances trainingsData) throws Exception {
		super(extraTree, options, trainingsData);
		this.extraTree = extraTree;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		extraTree.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		extraTree.setOptions(Utils.splitOptions(options));
	}

}
