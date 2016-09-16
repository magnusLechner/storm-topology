package at.lechner.weka.option;

import weka.classifiers.functions.SimpleLogistic;
import weka.core.Instances;
import weka.core.Utils;

public class MySimpleLogisticOption extends MyOption {

	private SimpleLogistic simpleLogistic;

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, Instances trainingsData) throws Exception {
		super(simpleLogistic, trainingsData);
		this.simpleLogistic = simpleLogistic;
	}

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, Instances trainingsData, int numFolds)
			throws Exception {
		super(simpleLogistic, trainingsData, numFolds);
		this.simpleLogistic = simpleLogistic;
	}

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, String[] options, Instances trainingsData,
			int numFolds) throws Exception {
		super(simpleLogistic, options, trainingsData, numFolds);
		this.simpleLogistic = simpleLogistic;
	}

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, String[] options, Instances trainingsData)
			throws Exception {
		super(simpleLogistic, options, trainingsData);
		this.simpleLogistic = simpleLogistic;
	}

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(simpleLogistic, options, trainingsData, numFolds);
		this.simpleLogistic = simpleLogistic;
	}

	public MySimpleLogisticOption(SimpleLogistic simpleLogistic, String options, Instances trainingsData)
			throws Exception {
		super(simpleLogistic, options, trainingsData);
		this.simpleLogistic = simpleLogistic;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		simpleLogistic.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		simpleLogistic.setOptions(Utils.splitOptions(options));
	}

}
