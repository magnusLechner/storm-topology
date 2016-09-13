package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyOption;
import at.lechner.weka.option.MyRandomForestOption;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class MyRandomForest extends MyClassifier {

	private RandomForest randomForest;

	public MyRandomForest(RandomForest randomForest) {
		this(randomForest, "RandomForest");
	}

	public MyRandomForest(RandomForest randomForest, String name) {
		super(randomForest, name);
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			// TODO add options
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

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyRandomForestOption option1 = new MyRandomForestOption(new RandomForest(), trainingsData);
		option1.setOptions("-U -num-slots 0");
		option1.addCVParameter("I 50 300 6");
		option1.addCVParameter("N 2 8 7");
		option1.addCVParameter("M 1 5 5");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier randomForest = new MyRandomForest(new RandomForest());
		classifiers.add(randomForest);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
