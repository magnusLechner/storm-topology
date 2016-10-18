package at.testing.weka.option;

import weka.classifiers.trees.LMT;
import weka.core.Instances;
import weka.core.Utils;

public class MyLMTOption extends MyOption {

	private LMT lmt;

	public MyLMTOption(LMT lmt, Instances trainingsData) throws Exception {
		super(lmt, trainingsData);
		this.lmt = lmt;
	}

	public MyLMTOption(LMT lmt, Instances trainingsData, int numFolds) throws Exception {
		super(lmt, trainingsData, numFolds);
		this.lmt = lmt;
	}

	public MyLMTOption(LMT lmt, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(lmt, options, trainingsData, numFolds);
		this.lmt = lmt;
	}

	public MyLMTOption(LMT lmt, String[] options, Instances trainingsData) throws Exception {
		super(lmt, options, trainingsData);
		this.lmt = lmt;
	}

	public MyLMTOption(LMT lmt, String options, Instances trainingsData, int numFolds) throws Exception {
		super(lmt, options, trainingsData, numFolds);
		this.lmt = lmt;
	}

	public MyLMTOption(LMT lmt, String options, Instances trainingsData) throws Exception {
		super(lmt, options, trainingsData);
		this.lmt = lmt;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		lmt.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		lmt.setOptions(Utils.splitOptions(options));
	}

}
