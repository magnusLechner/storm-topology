package at.lechner.weka.classifier;

import weka.classifiers.trees.RandomForest;

public class MyRandomForest extends MyClassifier {

	private static final RandomForest randomForest = new RandomForest();
	
	public MyRandomForest() {
		this("RandomForest");
	}

	public MyRandomForest(String name) {
		super(randomForest, name);
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			//TODO add options
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		randomForest.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = randomForest.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}
	
}
