package at.testing.weka.option;

import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;
import weka.core.Utils;

public class MyBayesNetOption extends MyOption {

	private BayesNet bayesNet;

	public MyBayesNetOption(BayesNet bayesNet, Instances trainingsData) throws Exception {
		super(bayesNet, trainingsData);
		this.bayesNet = bayesNet;
	}

	public MyBayesNetOption(BayesNet bayesNet, Instances trainingsData, int numFolds) throws Exception {
		super(bayesNet, trainingsData, numFolds);
		this.bayesNet = bayesNet;
	}

	public MyBayesNetOption(BayesNet bayesNet, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(bayesNet, options, trainingsData, numFolds);
		this.bayesNet = bayesNet;
	}

	public MyBayesNetOption(BayesNet bayesNet, String[] options, Instances trainingsData) throws Exception {
		super(bayesNet, options, trainingsData);
		this.bayesNet = bayesNet;
	}

	public MyBayesNetOption(BayesNet bayesNet, String options, Instances trainingsData, int numFolds) throws Exception {
		super(bayesNet, options, trainingsData, numFolds);
		this.bayesNet = bayesNet;
	}

	public MyBayesNetOption(BayesNet bayesNet, String options, Instances trainingsData) throws Exception {
		super(bayesNet, options, trainingsData);
		this.bayesNet = bayesNet;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		bayesNet.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		bayesNet.setOptions(Utils.splitOptions(options));
	}

}
