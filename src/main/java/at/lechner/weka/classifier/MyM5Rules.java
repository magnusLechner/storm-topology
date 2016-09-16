package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyM5RulesOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.rules.M5Rules;
import weka.core.Instances;
import weka.core.Utils;

public class MyM5Rules extends MyClassifier {

	private M5Rules m5Rules;

	public MyM5Rules(M5Rules m5Rules) {
		this(m5Rules, "M5Rules");
	}

	public MyM5Rules(M5Rules m5Rules, String name) {
		super(m5Rules, name);
		this.m5Rules = m5Rules;
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
		m5Rules.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = m5Rules.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyM5RulesOption option1 = new MyM5RulesOption(new M5Rules(), trainingsData);
		option1.addCVParameter("M 2 10 5");

		MyM5RulesOption option2 = new MyM5RulesOption(new M5Rules(), trainingsData);
		option2.setOptions("-R");
		option2.addCVParameter("M 2 10 5");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier m5Rules = new MyM5Rules(new M5Rules());
		classifiers.add(m5Rules);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
