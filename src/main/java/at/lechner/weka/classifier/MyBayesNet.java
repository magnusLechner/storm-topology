package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyBayesNetOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.bayes.BayesNet;
import weka.core.Instances;

public class MyBayesNet extends MyClassifier {

	private BayesNet bayesNet;

	public MyBayesNet(BayesNet bayesNet) {
		this(bayesNet, "BayesNet");
	}

	public MyBayesNet(BayesNet bayesNet, String name) {
		super(bayesNet, name);
		this.bayesNet = bayesNet;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] option1 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"BAYES", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option2 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "MDL",
					"-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option3 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"ENTROPY", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option4 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"CROSS_CLASSIC", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option5 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"CROSS_BAYES", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option6 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"BAYES", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option7 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "MDL",
					"-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option8 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"ENTROPY", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };

			String[] option9 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"CROSS_CLASSIC", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A",
					"0.5" };

			String[] option10 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
					"CROSS_BAYES", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A",
					"0.5" };

			addOption(option1);
			addOption(option2);
			addOption(option3);
			addOption(option4);
			addOption(option5);
			addOption(option6);
			addOption(option7);
			addOption(option8);
			addOption(option9);
			addOption(option10);
			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		bayesNet.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = bayesNet.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyBayesNetOption option1 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op1 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "BAYES", "-E",
				"weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option1.setOptions(op1);

		MyBayesNetOption option2 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op2 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "MDL", "-E",
				"weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option2.setOptions(op2);

		MyBayesNetOption option3 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op3 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "ENTROPY",
				"-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option3.setOptions(op3);

		MyBayesNetOption option4 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op4 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
				"CROSS_CLASSIC", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option4.setOptions(op4);

		MyBayesNetOption option5 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op5 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "CROSS_BAYES",
				"-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option5.setOptions(op5);

		MyBayesNetOption option6 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op6 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "BAYES",
				"-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option6.setOptions(op6);

		MyBayesNetOption option7 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op7 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "MDL", "-mbc",
				"-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option7.setOptions(op7);

		MyBayesNetOption option8 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op8 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S", "ENTROPY",
				"-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option8.setOptions(op8);

		MyBayesNetOption option9 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op9 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
				"CROSS_CLASSIC", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A",
				"0.5" };
		option9.setOptions(op9);

		MyBayesNetOption option10 = new MyBayesNetOption(new BayesNet(), trainingsData);
		String[] op10 = { "-D", "-Q", "weka.classifiers.bayes.net.search.local.K2", "--", "-P", "1", "-S",
				"CROSS_BAYES", "-mbc", "-E", "weka.classifiers.bayes.net.estimate.SimpleEstimator", "--", "-A", "0.5" };
		option10.setOptions(op10);

		options.add(option1);
		options.add(option2);
		options.add(option3);
		options.add(option4);
		options.add(option5);
		options.add(option6);
		options.add(option7);
		options.add(option8);
		options.add(option9);
		options.add(option10);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier bayesNet = new MyBayesNet(new BayesNet());
		classifiers.add(bayesNet);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
