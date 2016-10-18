package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyA1DEOption;
import at.testing.weka.option.MyOption;
import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.core.Instances;
import weka.core.Utils;

public class MyA1DE extends MyClassifier {

	private A1DE a1de;

	public MyA1DE(A1DE a1de) {
		this(a1de, "A1DE");
	}

	public MyA1DE(A1DE a1de, String name) {
		super(a1de, name);
		this.a1de = a1de;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-F 1 -M 1.5");
			String[] options2 = Utils.splitOptions("-F 1 -M 1.5 -W");
			
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
		a1de.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = a1de.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyA1DEOption option1 = new MyA1DEOption(new A1DE(), trainingsData);
		option1.setOptions("-W");
		option1.addCVParameter("F 1 3 3");
		option1.addCVParameter("M 0.5 2 4");

		MyA1DEOption option2 = new MyA1DEOption(new A1DE(), trainingsData);
		option2.addCVParameter("F 1 3 3");
		option2.addCVParameter("M 0.5 2 4");

		options.add(option1);
		options.add(option2);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier a1de = new MyA1DE(new A1DE());
		classifiers.add(a1de);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
