package at.lechner.weka.option;

import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.core.Utils;

public abstract class MyOption {

	private Instances trainingsData;
	private CVParameterSelection cvps;

	public MyOption(Classifier classifier, Instances trainingsData) throws Exception {
		super();
		this.cvps = new CVParameterSelection();
		cvps.setClassifier(classifier);
		cvps.setNumFolds(10);
		this.trainingsData = trainingsData;
	}

	public MyOption(Classifier classifier, Instances trainingsData, int numFolds) throws Exception {
		super();
		this.cvps = new CVParameterSelection();
		cvps.setClassifier(classifier);
		cvps.setNumFolds(numFolds);
		this.trainingsData = trainingsData;
	}

	public MyOption(Classifier classifier, String[] options, Instances trainingsData, int numFolds) throws Exception {
		super();
		setOptions(options);
		this.cvps = new CVParameterSelection();
		cvps.setClassifier(classifier);
		cvps.setNumFolds(numFolds);
		this.trainingsData = trainingsData;
	}

	public MyOption(Classifier classifier, String[] options, Instances trainingsData) throws Exception {
		this(classifier, options, trainingsData, 10);
	}

	public MyOption(Classifier classifier, String options, Instances trainingsData, int numFolds) throws Exception {
		super();
		setOptions(options);
		this.cvps = new CVParameterSelection();
		cvps.setClassifier(classifier);
		cvps.setNumFolds(numFolds);
		this.trainingsData = trainingsData;
	}

	public MyOption(Classifier classifier, String options, Instances trainingsData) throws Exception {
		this(classifier, options, trainingsData, 10);
	}

	public abstract void setOptions(String[] options) throws Exception;

	public abstract void setOptions(String options) throws Exception;

	public String getOptionsToString() {
		return "Options: " + Utils.joinOptions(cvps.getOptions());
	}

	public void addCVParameter(String parameter) throws Exception {
		cvps.addCVParameter(parameter);
	}

	public void buildClassifier() throws Exception {
		System.out.println("Started building classifier: " + getOptionsToString());
		cvps.buildClassifier(trainingsData);
	}

	public String getBestClassifierOptions() {
		return Utils.joinOptions(cvps.getBestClassifierOptions());
	}
}
