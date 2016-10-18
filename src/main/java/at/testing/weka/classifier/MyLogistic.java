package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyLogisticOption;
import at.testing.weka.option.MyOption;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;
import weka.core.Utils;

public class MyLogistic extends MyClassifier {

	private Logistic logistic;

	public MyLogistic(Logistic logistic) {
		this(logistic, "Logistic");
	}

	public MyLogistic(Logistic logistic, String name) {
		super(logistic, name);
		this.logistic = logistic;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-R 1.0E-7 -M -1 -num-decimal-places 4");

			addOption(options1);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		logistic.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = logistic.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyLogisticOption option1 = new MyLogisticOption(new Logistic(), trainingsData);
		option1.addCVParameter("R 1.0E-9 1.0E-7 5");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier logistic = new MyLogistic(new Logistic());
		classifiers.add(logistic);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
