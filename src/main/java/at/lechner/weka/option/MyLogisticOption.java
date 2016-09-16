package at.lechner.weka.option;

import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.Utils;

public class MyLogisticOption extends MyOption {

	private Logistic logistic;

	public MyLogisticOption(Logistic logistic, Instances trainingsData) throws Exception {
		super(logistic, trainingsData);
		this.logistic = logistic;
	}

	public MyLogisticOption(Logistic logistic, Instances trainingsData, int numFolds) throws Exception {
		super(logistic, trainingsData, numFolds);
		this.logistic = logistic;
	}

	public MyLogisticOption(Logistic logistic, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(logistic, options, trainingsData, numFolds);
		this.logistic = logistic;
	}

	public MyLogisticOption(Logistic logistic, String[] options, Instances trainingsData) throws Exception {
		super(logistic, options, trainingsData);
		this.logistic = logistic;
	}

	public MyLogisticOption(Logistic logistic, String options, Instances trainingsData, int numFolds) throws Exception {
		super(logistic, options, trainingsData, numFolds);
		this.logistic = logistic;
	}

	public MyLogisticOption(Logistic logistic, String options, Instances trainingsData) throws Exception {
		super(logistic, options, trainingsData);
		this.logistic = logistic;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		logistic.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		logistic.setOptions(Utils.splitOptions(options));
	}

}
