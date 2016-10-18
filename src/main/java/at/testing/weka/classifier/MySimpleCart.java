package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyOption;
import at.testing.weka.option.MySimpleCartOption;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;
import weka.core.Utils;

public class MySimpleCart extends MyClassifier {

	private SimpleCart simpleCart;

	public MySimpleCart(SimpleCart simpleCart) {
		this(simpleCart, "SimpleCart");
	}

	public MySimpleCart(SimpleCart simpleCart, String name) {
		super(simpleCart, name);
		this.simpleCart = simpleCart;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-M 4 -N 15 -C 1.0 -S 1");
			String[] options2 = Utils.splitOptions("-M 3 -N 15 -A -C 1.0 -S 1");

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
		simpleCart.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = simpleCart.getOptions();
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

		MySimpleCartOption option1 = new MySimpleCartOption(new SimpleCart(), trainingsData);
		option1.addCVParameter("M 2 5 4");
		option1.addCVParameter("N 3 15 5");

		MySimpleCartOption option2 = new MySimpleCartOption(new SimpleCart(), trainingsData);
		option2.setOptions("-A");
		option2.addCVParameter("M 2 5 4");
		option2.addCVParameter("N 3 15 5");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier simpleCart = new MySimpleCart(new SimpleCart());
		classifiers.add(simpleCart);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
