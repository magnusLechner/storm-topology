package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyFunctionalTreeOption;
import at.testing.weka.option.MyOption;
import weka.classifiers.trees.FT;
import weka.core.Instances;
import weka.core.Utils;

public class MyFunctionalTree extends MyClassifier {

	private FT functionalTree;

	public MyFunctionalTree(FT functionalTree) {
		this(functionalTree, "FunctionalTree");
	}

	public MyFunctionalTree(FT functionalTree, String name) {
		super(functionalTree, name);
		this.functionalTree = functionalTree;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-F 0 -M 15 -I 15 -W 0.0");
			String[] options2 = Utils.splitOptions("-F 0 -M 25 -I 20 -W 0.0 -A");
			String[] options3 = Utils.splitOptions("-F 0 -M 15 -I 20 -W 0.0 -A");

			addOption(options1);
			addOption(options2);
			addOption(options3);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		functionalTree.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = functionalTree.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyFunctionalTreeOption option1 = new MyFunctionalTreeOption(new FT(), trainingsData);

		MyFunctionalTreeOption option2 = new MyFunctionalTreeOption(new FT(), trainingsData);
		option2.setOptions("-A");
		option2.addCVParameter("M 5 25 5");

		MyFunctionalTreeOption option3 = new MyFunctionalTreeOption(new FT(), trainingsData);
		option3.setOptions("-A");
		option3.addCVParameter("F 0 2 1");
		option3.addCVParameter("M 5 25 5");

		MyFunctionalTreeOption option4 = new MyFunctionalTreeOption(new FT(), trainingsData);
		option4.setOptions("-A");
		option4.addCVParameter("F 0 2 1");
		option4.addCVParameter("I 20 100 5");

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
		MyClassifier functionalTree = new MyFunctionalTree(new FT());
		classifiers.add(functionalTree);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
