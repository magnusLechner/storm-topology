package at.lechner.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class WekaEvaluator {

	public static final String TEST = "src/main/resources/arff/Twitch/Test.arff";
	public static final String TRAINING = "src/main/resources/arff/Twitch/Training.arff";

	private String trainingARFF;
	private String testARFF;

	public WekaEvaluator() {
		trainingARFF = TRAINING;
		testARFF = TEST;
	}

	public WekaEvaluator(String trainingARFF, String testARFF) {
		this.trainingARFF = trainingARFF;
		this.testARFF = testARFF;
	}

	public List<Evaluation> evaluateAll(List<Classifier> classifiers) throws Exception {
		BufferedReader training = readDataFile(trainingARFF);
		BufferedReader test = readDataFile(testARFF);

		Instances trainingInstance = new Instances(training);
		trainingInstance.setClassIndex(trainingInstance.numAttributes() - 1);

		Instances testInstance = new Instances(test);
		testInstance.setClassIndex(testInstance.numAttributes() - 1);

		// Run for each model
		List<Evaluation> evaluations = new ArrayList<Evaluation>();
		for (int i = 0; i < classifiers.size(); i++) {
			Evaluation evaluation = classify(classifiers.get(i), trainingInstance, testInstance);
			evaluations.add(evaluation);
		}

		printEvaluations(evaluations);
		
		return evaluations;
	}

	private void printEvaluations(List<Evaluation> evaluations) {
		for (int i = 0; i < evaluations.size(); i++) {
			System.out.println("percentage unclassified: " + evaluations.get(i).pctUnclassified());
			System.out.println("percentage correct classified: " + evaluations.get(i).pctCorrect());
			System.out.println(" ");
			System.out.println("precision NEGATIVE: " + evaluations.get(i).precision(0));
			System.out.println("recall NEGATIVE: " + evaluations.get(i).recall(0));
			System.out.println("precision NEUTRAL: " + evaluations.get(i).precision(1));
			System.out.println("recall NEUTRAL: " + evaluations.get(i).recall(1));
			System.out.println("precision POSITIVE: " + evaluations.get(i).precision(2));
			System.out.println("recall POSITIVE: " + evaluations.get(i).recall(2));
			System.out.println(" ");
			System.out.println("Summary String: ");
			System.out.println(evaluations.get(i).toSummaryString());
		}
	}
	
	private BufferedReader readDataFile(String pathToARFF) {
		BufferedReader inputReader = null;

		try {
			inputReader = new BufferedReader(new FileReader(pathToARFF));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + pathToARFF);
		}

		return inputReader;
	}

	private Evaluation classify(Classifier classifier, Instances trainingSet, Instances testingSet) throws Exception {
		Evaluation evaluation = new Evaluation(trainingSet);

		classifier.buildClassifier(trainingSet);
		evaluation.evaluateModel(classifier, testingSet);

		return evaluation;
	}

	public Classifier createJ48() {
		// options
		String[] options = new String[1];
		options[0] = "-U";

		// Use a set of classifiers
		J48 j48 = new J48();
		try {
			j48.setOptions(options);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return j48;
	}

	public static void main(String[] args) throws Exception {
		// TEST
		List<Classifier> classifiers = new ArrayList<Classifier>();
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		Classifier j48 = weka.createJ48();
		classifiers.add(j48);

		weka.evaluateAll(classifiers);
	}

}