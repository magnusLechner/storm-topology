package at.lechner.weka.option;

import weka.classifiers.rules.M5Rules;
import weka.core.Instances;
import weka.core.Utils;

public class MyM5RulesOption extends MyOption {

	private M5Rules m5Rules;

	public MyM5RulesOption(M5Rules m5Rules, Instances trainingsData) throws Exception {
		super(m5Rules, trainingsData);
		this.m5Rules = m5Rules;
	}

	public MyM5RulesOption(M5Rules m5Rules, Instances trainingsData, int numFolds) throws Exception {
		super(m5Rules, trainingsData, numFolds);
		this.m5Rules = m5Rules;
	}

	public MyM5RulesOption(M5Rules m5Rules, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(m5Rules, options, trainingsData, numFolds);
		this.m5Rules = m5Rules;
	}

	public MyM5RulesOption(M5Rules m5Rules, String[] options, Instances trainingsData) throws Exception {
		super(m5Rules, options, trainingsData);
		this.m5Rules = m5Rules;
	}

	public MyM5RulesOption(M5Rules m5Rules, String options, Instances trainingsData, int numFolds) throws Exception {
		super(m5Rules, options, trainingsData, numFolds);
		this.m5Rules = m5Rules;
	}

	public MyM5RulesOption(M5Rules m5Rules, String options, Instances trainingsData) throws Exception {
		super(m5Rules, options, trainingsData);
		this.m5Rules = m5Rules;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		m5Rules.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		m5Rules.setOptions(Utils.splitOptions(options));
	}

}
