package at.lechner.weka.classifier;

import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.WekaEvaluator;
import at.lechner.weka.option.MyOption;
import at.lechner.weka.option.MyRandomForestOption;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.Utils;

public class MyRandomForest extends MyClassifier {

	private RandomForest randomForest;

	public MyRandomForest(RandomForest randomForest) {
		this(randomForest, "RandomForest");
	}

	public MyRandomForest(RandomForest randomForest, String name) {
		super(randomForest, name);
		this.randomForest = randomForest;
		addTestOptions();
	}

	// -P
	// Size of each bag, as a percentage of the
	// training set size. (default 100)
	// -O
	// Calculate the out of bag error.
	// -store-out-of-bag-predictions
	// Whether to store out of bag predictions in internal evaluation object.
	// -output-out-of-bag-complexity-statistics
	// Whether to output complexity-based statistics when out-of-bag evaluation is performed.
	// -print
	// Print the individual classifiers in the output
	// -I <num>
	// Number of iterations.
	// (current value 100)
	// -num-slots <num>
	// Number of execution slots.
	// (default 1 - i.e. no parallelism)
	// (use 0 to auto-detect number of cores)
	// -K <number of attributes>
	// Number of attributes to randomly investigate. (default 0)
	// (<1 = int(log_2(#predictors)+1)).
	// -M <minimum number of instances>
	// Set minimum number of instances per leaf.
	// (default 1)
	// -V <minimum variance for split>
	// Set minimum numeric class variance proportion
	// of train variance for split (default 1e-3).
	// -S <num>
	// Seed for random number generator.
	// (default 1)
	// -depth <num>
	// The maximum depth of the tree, 0 for unlimited.
	// (default 0)
	// -N <num>
	// Number of folds for backfitting (default 0, no backfitting).
	// -U
	// Allow unclassified instances.
	// -B
	// Break ties randomly when several attributes look equally good.
	// -output-debug-info
	// If set, classifier is run in debug mode and
	// may output additional info to the console
	// -do-not-check-capabilities
	// If set, classifier capabilities are not checked before classifier is built
	// (use with caution).
	// -num-decimal-places
	// The number of decimal places for the output of numbers in the model (default 2).

	public void addTestOptions() {
		try {
			// -P "I 50.0 300.0 6.0" -P "N 2.0 6.0 5.0" -X 5 -S 1 -W weka.classifiers.trees.RandomForest -- -P 100 -I 200 -num-slots 0 -K 0 -M 1.0 -V 0.001 -S 1 -N 2 -U
			// -I 200 -N 2 -P 100 -num-slots 0 -K 0 -M 1.0 -V 0.001 -S 1 -U
			String[] options1 = Utils.splitOptions("-I 200 -N 2 -P 100 -num-slots 0 -K 0 -M 1.0 -V 0.001 -S 1 -U");
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
		randomForest.setOptions(options);
	}

	@Override
	public String getCompleteCurrentOption() {
		String[] option = randomForest.getOptions();
		String res = "";
		for (String s : option) {
			res += s + " ";
		}
		return res;
	}

	@Override
	public List<MyOption> defineOptionsForOptimization(Instances trainingsData) throws Exception {
		List<MyOption> options = new ArrayList<MyOption>();

		MyRandomForestOption option1 = new MyRandomForestOption(new RandomForest(), trainingsData);
		option1.setOptions("-U -num-slots 0");
		option1.addCVParameter("I 50 300 6");
		option1.addCVParameter("N 2 6 5");

		options.add(option1);

		return options;
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		MyClassifier randomForest = new MyRandomForest(new RandomForest());
		classifiers.add(randomForest);

		// weka.optimizeParameters(classifiers);
		weka.evaluateAll(classifiers);
	}

}
