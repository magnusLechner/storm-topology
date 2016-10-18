package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyJ48Option;
import at.testing.weka.option.MyOption;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;

public class MyJ48 extends MyClassifier {

	private J48 j48;

	public MyJ48(J48 j48) {
		this(j48, "J48");
	}

	public MyJ48(J48 j48, String name) {
		super(j48, name);
		this.j48 = j48;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-C 0.05 -M 2 -A");
			String[] options2 = Utils.splitOptions("-C 0.05 -M 2");
			String[] options3 = Utils.splitOptions("-N 4 -R -Q 1 -M 2 -A");
			String[] options4 = Utils.splitOptions("-N 4 -R -Q 1 -M 2");

			addOption(options1);
			addOption(options2);
			addOption(options3);
			addOption(options4);
			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		j48.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = j48.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyJ48Option option1 = new MyJ48Option(new J48(), trainingsData);
		option1.setOptions("-A");
		option1.addCVParameter("C 0.05 0.5 10");

		MyJ48Option option2 = new MyJ48Option(new J48(), trainingsData);
		option2.addCVParameter("C 0.05 0.5 10");

		MyJ48Option option3 = new MyJ48Option(new J48(), trainingsData);
		option3.setOptions("-R -A");
		option3.addCVParameter("N 2 5 4");

		MyJ48Option option4 = new MyJ48Option(new J48(), trainingsData);
		option4.setOptions("-R");
		option4.addCVParameter("N 2 5 4");

		options.add(option1);
		options.add(option2);
		options.add(option3);
		options.add(option4);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier j48 = new MyJ48(new J48());
		classifiers.add(j48);

		// weka.optimizeParameters(classifiers);
		weka.evaluateAll(classifiers);
	}

}
