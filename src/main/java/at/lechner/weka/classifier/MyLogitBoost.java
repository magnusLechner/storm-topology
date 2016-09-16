package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyLogitBoostOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.meta.LogitBoost;
import weka.core.Instances;
import weka.core.Utils;

public class MyLogitBoost extends MyClassifier {

	private LogitBoost logitBoost;

	public MyLogitBoost(LogitBoost logitBoost) {
		this(logitBoost, "LogitBoost");
	}

	public MyLogitBoost(LogitBoost logitBoost, String name) {
		super(logitBoost, name);
		this.logitBoost = logitBoost;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			// TODO

//			String[] options1 = Utils.splitOptions("-F 1 -M 1.5");
//			
//			addOption(options1);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		logitBoost.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = logitBoost.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	// weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE -F 1 -M 1.0 -W
	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyLogitBoostOption option1 = new MyLogitBoostOption(new LogitBoost(), trainingsData);
		option1.setOptions("-O 4 -E 4");
		option1.addCVParameter("I 20 100 5");

		MyLogitBoostOption option2 = new MyLogitBoostOption(new LogitBoost(), trainingsData);
		option2.setOptions("-Q -O 4 -E 4");
		option2.addCVParameter("I 20 100 5");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier logitBoost = new MyLogitBoost(new LogitBoost());
		classifiers.add(logitBoost);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
