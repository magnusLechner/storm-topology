package at.testing.weka.option;

import weka.classifiers.rules.PART;
import weka.core.Instances;
import weka.core.Utils;

public class MyPARTOption extends MyOption {

	private PART part;

	public MyPARTOption(PART part, Instances trainingsData) throws Exception {
		super(part, trainingsData);
		this.part = part;
	}

	public MyPARTOption(PART part, Instances trainingsData, int numFolds) throws Exception {
		super(part, trainingsData, numFolds);
		this.part = part;
	}

	public MyPARTOption(PART part, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(part, options, trainingsData, numFolds);
		this.part = part;
	}

	public MyPARTOption(PART part, String[] options, Instances trainingsData) throws Exception {
		super(part, options, trainingsData);
		this.part = part;
	}

	public MyPARTOption(PART part, String options, Instances trainingsData, int numFolds) throws Exception {
		super(part, options, trainingsData, numFolds);
		this.part = part;
	}

	public MyPARTOption(PART part, String options, Instances trainingsData) throws Exception {
		super(part, options, trainingsData);
		this.part = part;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		part.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		part.setOptions(Utils.splitOptions(options));
	}

}
