package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyOption;
import at.testing.weka.option.MyREPTreeOption;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyREPTree extends MyClassifier {

	private REPTree repTree;

	public MyREPTree(REPTree repTree) {
		this(repTree, "REPTree");
	}

	public MyREPTree(REPTree repTree, String name) {
		super(repTree, name);
		this.repTree = repTree;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-M 2 -V 1.0E-4 -N 5 -S 1 -L -1 -I 0.0");

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
		repTree.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = repTree.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyREPTreeOption option1 = new MyREPTreeOption(new REPTree(), trainingsData);
		option1.addCVParameter("M 2 5 4");
		option1.addCVParameter("V 1.0E-4 1.0E-2 5");
		option1.addCVParameter("N 2 5 4");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier repTree = new MyREPTree(new REPTree());
		classifiers.add(repTree);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
