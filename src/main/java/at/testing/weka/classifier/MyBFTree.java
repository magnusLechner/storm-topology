package at.testing.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.testing.weka.WekaEvaluator;
import at.testing.weka.option.MyBFTreeOption;
import at.testing.weka.option.MyOption;
import weka.classifiers.trees.BFTree;
import weka.core.Instances;
import weka.core.Utils;

public class MyBFTree extends MyClassifier {

	private BFTree bfTree;

	public MyBFTree(BFTree bfTree) {
		this(bfTree, "BFTree");
	}

	public MyBFTree(BFTree bfTree, String name) {
		super(bfTree, name);
		this.bfTree = bfTree;
		addTestOptions();
	}

	public void addTestOptions() {
		try {
			String[] options1 = Utils.splitOptions("-M 2 -N 5 -A -C 1.0 -P PREPRUNED -S 1");
			String[] options2 = Utils.splitOptions("-M 2 -N 5 -G -A -C 1.0 -P PREPRUNED -S 1");
			String[] options3 = Utils.splitOptions("-N 6 -M 2 -C 1.0 -P POSTPRUNED -S 1");
			String[] options4 = Utils.splitOptions("-N 4 -M 2 -G -C 1.0 -P POSTPRUNED -S 1");
			String[] options5 = Utils.splitOptions("-N 4 -M 2 -A -C 1.0 -P POSTPRUNED -S 1");
			String[] options6 = Utils.splitOptions("-N 4 -M 2 -G -A -C 1.0 -P POSTPRUNED -S 1");

			addOption(options1);
			addOption(options2);
			addOption(options3);
			addOption(options4);
			addOption(options5);
			addOption(options6);

			if (getOptionsList().size() == 0) {
				System.err.println(getName() + ": No test options!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setCurrentOption(String[] options) throws Exception {
		bfTree.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = bfTree.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyBFTreeOption option1 = new MyBFTreeOption(new BFTree(), trainingsData);
		option1.addCVParameter("N 4 6 3");

		MyBFTreeOption option2 = new MyBFTreeOption(new BFTree(), trainingsData);
		option2.setOptions("-G");
		option2.addCVParameter("N 4 6 3");

		MyBFTreeOption option3 = new MyBFTreeOption(new BFTree(), trainingsData);
		option3.setOptions("-A -P PREPRUNED");

		MyBFTreeOption option4 = new MyBFTreeOption(new BFTree(), trainingsData);
		option4.setOptions("-A -G -P PREPRUNED");

		MyBFTreeOption option5 = new MyBFTreeOption(new BFTree(), trainingsData);
		option5.setOptions("-A");
		option5.addCVParameter("N 4 6 3");

		MyBFTreeOption option6 = new MyBFTreeOption(new BFTree(), trainingsData);
		option6.setOptions("-A -G");
		option6.addCVParameter("N 4 6 3");

		// change order
		options.add(option3);
		options.add(option4);
		options.add(option1);
		options.add(option2);
		options.add(option5);
		options.add(option6);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier bfTree = new MyBFTree(new BFTree());
		classifiers.add(bfTree);

		weka.optimizeParameters(classifiers);
		// weka.evaluateAll(classifiers);
	}

}
