package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyNBTreeOption;
import at.lechner.weka.option.MyOption;
import weka.classifiers.trees.NBTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyNBTree extends MyClassifier {

	private NBTree nbTree;

	public MyNBTree(NBTree nbTree) {
		this(nbTree, "NBTree");
	}

	public MyNBTree(NBTree nbTree, String name) {
		super(nbTree, name);
		this.nbTree = nbTree;
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
		nbTree.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = nbTree.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyNBTreeOption option1 = new MyNBTreeOption(new NBTree(), trainingsData);

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier nbTree = new MyNBTree(new NBTree());
		classifiers.add(nbTree);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
