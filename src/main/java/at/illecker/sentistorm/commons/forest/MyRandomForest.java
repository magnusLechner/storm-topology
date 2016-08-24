package at.illecker.sentistorm.commons.forest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class MyRandomForest {

	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}

		return inputReader;
	}

	private static Classifier buildClassifier(Instances trainInstances) throws Exception {
		RandomForest randomForest = new RandomForest();
		// randomForest.setNumTrees(20);

		// You can set the options here
		/*
		 * -I <number of trees> Number of trees to build.
		 * 
		 * -K <number of features> Number of features to consider (<0 =
		 * int(log_2(#predictors)+1)).
		 * 
		 * -S Seed for random number generator. (default 1)
		 * 
		 * -depth <num> The maximum depth of the trees, 0 for unlimited.
		 * (default 0)
		 */
		// String[] options = new String[1];
		// options[0] = "-I 20";
		// randomForest.setOptions(options);

		randomForest.buildClassifier(trainInstances);
		return randomForest;
	}

	private static Instances index(Instances trainInstances) {
		try {
			NGramTokenizer tokenizer = new NGramTokenizer();
			tokenizer.setNGramMinSize(1);
			tokenizer.setNGramMaxSize(1);
			tokenizer.setDelimiters("\\W");

			StringToWordVector filter = new StringToWordVector();
			filter.setTokenizer(tokenizer);
			filter.setInputFormat(trainInstances);
			filter.setWordsToKeep(1000000);
			filter.setDoNotOperateOnPerClassBasis(true);
			filter.setLowerCaseTokens(true);

			return Filter.useFilter(trainInstances, filter);
		} catch (Exception e) {
			System.out.println("Problem found when training");
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// BufferedReader trainFile =
		// readDataFile("resources/random_forest/MyTest/myTraining.arff");
		// BufferedReader testFile =
		// readDataFile("resources/random_forest/MyTest/myTest.arff");

		BufferedReader trainFile = readDataFile("src/main/resources/random_forest/Twitch/twitch-training.arff");
		BufferedReader testFile = readDataFile("src/main/resources/random_forest/Twitch/twitch-test.arff");

		Instances trainInstances = new Instances(trainFile);
		// Make the last attribute be the class
		trainInstances.setClassIndex(trainInstances.numAttributes() - 1);
		Instances testInstances = new Instances(testFile);
		testInstances.setClassIndex(testInstances.numAttributes() - 1);

		trainInstances = index(trainInstances);
		testInstances = index(testInstances);

		// for(Instance asd : testInstances) {
		// System.out.println(asd);
		// }

		Classifier classifier = buildClassifier(trainInstances);

		Evaluation evaluation = new Evaluation(trainInstances);
		evaluation.evaluateModel(classifier, testInstances);

		System.out.println(evaluation.toSummaryString("\nResults\n======\n", true));
		System.out.println(evaluation.toClassDetailsString());
	}

}
