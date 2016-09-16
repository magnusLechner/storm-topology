package at.lechner.weka.option;

import weka.classifiers.trees.FT;
import weka.core.Instances;
import weka.core.Utils;

public class MyFunctionalTreeOption extends MyOption {

	private FT functionalTrees;

	public MyFunctionalTreeOption(FT functionalTrees, Instances trainingsData) throws Exception {
		super(functionalTrees, trainingsData);
		this.functionalTrees = functionalTrees;
	}

	public MyFunctionalTreeOption(FT functionalTrees, Instances trainingsData, int numFolds) throws Exception {
		super(functionalTrees, trainingsData, numFolds);
		this.functionalTrees = functionalTrees;
	}

	public MyFunctionalTreeOption(FT functionalTrees, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(functionalTrees, options, trainingsData, numFolds);
		this.functionalTrees = functionalTrees;
	}

	public MyFunctionalTreeOption(FT functionalTrees, String[] options, Instances trainingsData) throws Exception {
		super(functionalTrees, options, trainingsData);
		this.functionalTrees = functionalTrees;
	}

	public MyFunctionalTreeOption(FT functionalTrees, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(functionalTrees, options, trainingsData, numFolds);
		this.functionalTrees = functionalTrees;
	}

	public MyFunctionalTreeOption(FT functionalTrees, String options, Instances trainingsData) throws Exception {
		super(functionalTrees, options, trainingsData);
		this.functionalTrees = functionalTrees;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		functionalTrees.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		functionalTrees.setOptions(Utils.splitOptions(options));
	}

}
