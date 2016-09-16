package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyExtraTreeOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.trees.ExtraTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyExtraTree extends MyClassifier {

	private ExtraTree extraTree;

	public MyExtraTree(ExtraTree extraTree) {
		this(extraTree, "ExtraTree");
	}

	public MyExtraTree(ExtraTree extraTree, String name) {
		super(extraTree, name);
		this.extraTree = extraTree;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-N 6 -K -1 -S 1");

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
		extraTree.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = extraTree.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyExtraTreeOption option1 = new MyExtraTreeOption(new ExtraTree(), trainingsData);
		option1.addCVParameter("N 2 6 5");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier extraTree = new MyExtraTree(new ExtraTree());
		classifiers.add(extraTree);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
