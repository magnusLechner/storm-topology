package at.testing.weka.option;

import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.core.Instances;
import weka.core.Utils;

public class MyA1DEOption extends MyOption {

	private A1DE a1de;

	public MyA1DEOption(A1DE a1de, Instances trainingsData) throws Exception {
		super(a1de, trainingsData);
		this.a1de = a1de;
	}

	public MyA1DEOption(A1DE a1de, Instances trainingsData, int numFolds) throws Exception {
		super(a1de, trainingsData, numFolds);
		this.a1de = a1de;
	}

	public MyA1DEOption(A1DE a1de, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(a1de, options, trainingsData, numFolds);
		this.a1de = a1de;
	}

	public MyA1DEOption(A1DE a1de, String[] options, Instances trainingsData) throws Exception {
		super(a1de, options, trainingsData);
		this.a1de = a1de;
	}

	public MyA1DEOption(A1DE a1de, String options, Instances trainingsData, int numFolds) throws Exception {
		super(a1de, options, trainingsData, numFolds);
		this.a1de = a1de;
	}

	public MyA1DEOption(A1DE a1de, String options, Instances trainingsData) throws Exception {
		super(a1de, options, trainingsData);
		this.a1de = a1de;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		a1de.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		a1de.setOptions(Utils.splitOptions(options));
	}

}
