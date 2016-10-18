package at.testing.weka.option;

import weka.classifiers.meta.LogitBoost;
import weka.core.Instances;
import weka.core.Utils;

public class MyLogitBoostOption extends MyOption {

	private LogitBoost logitBoost;

	public MyLogitBoostOption(LogitBoost logicBoost, Instances trainingsData) throws Exception {
		super(logicBoost, trainingsData);
		this.logitBoost = logicBoost;
	}

	public MyLogitBoostOption(LogitBoost logicBoost, Instances trainingsData, int numFolds) throws Exception {
		super(logicBoost, trainingsData, numFolds);
		this.logitBoost = logicBoost;
	}

	public MyLogitBoostOption(LogitBoost logicBoost, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(logicBoost, options, trainingsData, numFolds);
		this.logitBoost = logicBoost;
	}

	public MyLogitBoostOption(LogitBoost logicBoost, String[] options, Instances trainingsData) throws Exception {
		super(logicBoost, options, trainingsData);
		this.logitBoost = logicBoost;
	}

	public MyLogitBoostOption(LogitBoost logicBoost, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(logicBoost, options, trainingsData, numFolds);
		this.logitBoost = logicBoost;
	}

	public MyLogitBoostOption(LogitBoost logicBoost, String options, Instances trainingsData) throws Exception {
		super(logicBoost, options, trainingsData);
		this.logitBoost = logicBoost;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		logitBoost.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		logitBoost.setOptions(Utils.splitOptions(options));
	}

}
