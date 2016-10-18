package at.testing.weka.option;

import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

public class MyJ48Option extends MyOption {

	private J48 j48;

	public MyJ48Option(J48 j48, Instances trainingsData) throws Exception {
		super(j48, trainingsData);
		this.j48 = j48;
	}
	
	public MyJ48Option(J48 j48, Instances trainingsData, int numFolds) throws Exception {
		super(j48, trainingsData, numFolds);
		this.j48 = j48;
	}

	public MyJ48Option(J48 j48, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super(j48, options, trainingsData, numFolds);
		this.j48 = j48;
	}

	public MyJ48Option(J48 j48, String[] options, Instances trainingsData) throws Exception {
		super(j48, options, trainingsData);
		this.j48 = j48;
	}

	public MyJ48Option(J48 j48, String options, Instances trainingsData, int numFolds) throws Exception {
		super(j48, options, trainingsData, numFolds);
		this.j48 = j48;
	}

	public MyJ48Option(J48 j48, String options, Instances trainingsData) throws Exception {
		super(j48, options, trainingsData);
		this.j48 = j48;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		j48.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		j48.setOptions(Utils.splitOptions(options));
	}

}
