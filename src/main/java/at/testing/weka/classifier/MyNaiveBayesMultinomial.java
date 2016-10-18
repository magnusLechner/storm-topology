package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyNaiveBayesMultinomialOption;
import at.testing.weka.option.MyOption;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.core.Instances;

public class MyNaiveBayesMultinomial extends MyClassifier {

	private NaiveBayesMultinomial naiveBayesMultinomial;

	public MyNaiveBayesMultinomial(NaiveBayesMultinomial naiveBayesMultinomial) {
		this(naiveBayesMultinomial, "NaiveBayesMultinomial");
	}

	public MyNaiveBayesMultinomial(NaiveBayesMultinomial naiveBayesMultinomial, String name) {
		super(naiveBayesMultinomial, name);
		this.naiveBayesMultinomial = naiveBayesMultinomial;
		addTestOptions();
	}

	public void addTestOptions() {
		try {

			// There are no parameters for naive-bayes-multinomial

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		naiveBayesMultinomial.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = naiveBayesMultinomial.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	// There are no parameters vor naive-bayes-multinomial
	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyNaiveBayesMultinomialOption option1 = new MyNaiveBayesMultinomialOption(new NaiveBayesMultinomial(),
				trainingsData);

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier naiveBayesMultinomial = new MyNaiveBayesMultinomial(new NaiveBayesMultinomial());
		classifiers.add(naiveBayesMultinomial);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
