package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyOption;
import at.lechner.weka.option.MySimpleLogisticOption;
import weka.classifiers.functions.SimpleLogistic;
import weka.core.Instances;
import weka.core.Utils;

public class MySimpleLogistic extends MyClassifier {

	private SimpleLogistic simpleLogistic;

	public MySimpleLogistic(SimpleLogistic simpleLogistic) {
		this(simpleLogistic, "SimpleLogistic");
	}

	public MySimpleLogistic(SimpleLogistic simpleLogistic, String name) {
		super(simpleLogistic, name);
		this.simpleLogistic = simpleLogistic;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			String[] options1 = Utils.splitOptions("-H 30 -M 150 -I 0 -W 0.0");
			String[] options2 = Utils.splitOptions("-H 15 -M 150 -I 0 -W 0.0 -A");

			addOption(options1);
			addOption(options2);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		simpleLogistic.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = simpleLogistic.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MySimpleLogisticOption option1 = new MySimpleLogisticOption(new SimpleLogistic(), trainingsData);
		option1.addCVParameter("H 15 90 6");
		option1.addCVParameter("M 150 900 6");

		MySimpleLogisticOption option2 = new MySimpleLogisticOption(new SimpleLogistic(), trainingsData);
		option2.setOptions("-A");
		option2.addCVParameter("H 15 90 6");
		option2.addCVParameter("M 150 900 6");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier simpleLogistic = new MySimpleLogistic(new SimpleLogistic());
		classifiers.add(simpleLogistic);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
