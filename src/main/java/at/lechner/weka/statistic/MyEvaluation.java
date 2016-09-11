package at.lechner.weka.statistic;

import weka.classifiers.Evaluation;

public class MyEvaluation {

	private int trainingSize;
	private int testSize;
	private String classifierName;
	private String classifierOption;
	private Evaluation evaluation;
	
	public MyEvaluation(int trainingSize, int testSize, String classifierName, String classifierOption,
			Evaluation evaluation) {
		super();
		this.trainingSize = trainingSize;
		this.testSize = testSize;
		this.classifierName = classifierName;
		this.classifierOption = classifierOption;
		this.evaluation = evaluation;
	}

	public int getTrainingSize() {
		return trainingSize;
	}

	public void setTrainingSize(int trainingSize) {
		this.trainingSize = trainingSize;
	}

	public int getTestSize() {
		return testSize;
	}

	public void setTestSize(int testSize) {
		this.testSize = testSize;
	}

	public String getClassifierName() {
		return classifierName;
	}

	public void setClassifierName(String classifierName) {
		this.classifierName = classifierName;
	}

	public String getClassifierOption() {
		return classifierOption;
	}

	public void setClassifierOption(String classifierOption) {
		this.classifierOption = classifierOption;
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
	
}
