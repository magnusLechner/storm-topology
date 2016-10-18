package at.testing.weka.option;

import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;

public class MyRandomForestOption extends MyOption {

	private RandomForest randomForest;

	public MyRandomForestOption(RandomForest randomForest, Instances trainingsData) throws Exception {
		super(randomForest, trainingsData);
		this.randomForest = randomForest;
	}

	public MyRandomForestOption(RandomForest randomForest, Instances trainingsData, int numFolds) throws Exception {
		super(randomForest, trainingsData, numFolds);
		this.randomForest = randomForest;
	}

	public MyRandomForestOption(RandomForest randomForest, String[] options, Instances trainingsData, int numFolds)
			throws Exception {
		super(randomForest, options, trainingsData, numFolds);
		this.randomForest = randomForest;
	}

	public MyRandomForestOption(RandomForest randomForest, String[] options, Instances trainingsData) throws Exception {
		super(randomForest, options, trainingsData);
		this.randomForest = randomForest;
	}

	public MyRandomForestOption(RandomForest randomForest, String options, Instances trainingsData, int numFolds)
			throws Exception {
		super(randomForest, options, trainingsData, numFolds);
		this.randomForest = randomForest;
	}

	public MyRandomForestOption(RandomForest randomForest, String options, Instances trainingsData) throws Exception {
		super(randomForest, options, trainingsData);
		this.randomForest = randomForest;
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		randomForest.setOptions(options);
	}

	@Override
	public void setOptions(String options) throws Exception {
		randomForest.setOptions(Utils.splitOptions(options));
	}

}
