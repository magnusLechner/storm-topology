package at.lechner.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.classifier.MyClassifier;
import at.lechner.weka.classifier.MyJ48;
import at.lechner.weka.classifier.MyRandomForest;
import at.lechner.weka.statistic.MyEvaluation;
import at.lechner.weka.statistic.WekaStatistic;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class WekaEvaluator {

	public static final String TEST = "src/main/resources/arff/Twitch/Test.arff";
	public static final String TRAINING = "src/main/resources/arff/Twitch/Training.arff";

	public static final String WEKA_EVA_OUT = "src/main/evaluation/weka/successive_addition/weka_complete_overview.tsv";

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

	public List<List<MyEvaluation>> evaluateAll(List<MyClassifier> classifiers) throws Exception {
		BufferedReader training = readDataFile(trainingARFF);
		BufferedReader test = readDataFile(testARFF);

		Instances trainingInstance = new Instances(training);
		trainingInstance.setClassIndex(trainingInstance.numAttributes() - 1);

		Instances testInstance = new Instances(test);
		testInstance.setClassIndex(testInstance.numAttributes() - 1);

		// Run for each model
		List<List<MyEvaluation>> allEvaluations = new ArrayList<List<MyEvaluation>>();
		for (int i = 0; i < classifiers.size(); i++) {
			List<MyEvaluation> classifierEvaluations = classify(classifiers.get(i), trainingInstance, testInstance);
			allEvaluations.add(classifierEvaluations);
		}
		
		return allEvaluations;
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

	private static List<MyEvaluation> classify(MyClassifier myClassifier, Instances trainingSet, Instances testSet)
			throws Exception {
		List<MyEvaluation> evaluations = new ArrayList<MyEvaluation>();
		if (myClassifier.getOptionsList().size() == 0) {
			Evaluation evaluation = new Evaluation(trainingSet);

			myClassifier.buildClassifier(trainingSet);
			evaluation.evaluateModel(myClassifier.getClassifier(), testSet);
			MyEvaluation myEval = new MyEvaluation(trainingSet.size(), testSet.size(), myClassifier.getName(),
					myClassifier.getCompleteCurrentOption(), evaluation);
			evaluations.add(myEval);
		} else {
			for (int i = 0; i < myClassifier.getOptionsListSize(); i++) {
				Evaluation evaluation = new Evaluation(trainingSet);

				myClassifier.setCurrentOption(myClassifier.getMyOption(i));
				myClassifier.buildClassifier(trainingSet);
				evaluation.evaluateModel(myClassifier.getClassifier(), testSet);

				MyEvaluation myEval = new MyEvaluation(trainingSet.size(), testSet.size(), myClassifier.getName(),
						myClassifier.getCompleteCurrentOption(), evaluation);
				evaluations.add(myEval);
			}
		}

		return evaluations;
	}

	// TODO add more
	public static List<MyClassifier> createTestClassifiers() {
		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		try {
			MyClassifier j48 = new MyJ48();
			MyClassifier randomForest = new MyRandomForest();
			
			classifiers.add(j48);
			classifiers.add(randomForest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifiers;
	}

	public static void printWekaCompleteResults(List<List<List<List<MyEvaluation>>>> complete) {
		printWekaCompleteResults(WEKA_EVA_OUT, complete);
	}

	// TODO
	public static void printWekaCompleteResults(String output, List<List<List<List<MyEvaluation>>>> complete) {
		List<List<WekaStatistic>> statistics = new ArrayList<List<WekaStatistic>>();
		
		for (int i = 0; i < complete.get(0).get(0).size(); i++) {
			List<WekaStatistic> singleClassifierStatistics = new ArrayList<WekaStatistic>();
			for (int j = 0; j < complete.get(0).get(0).get(i).size(); j++) {
				singleClassifierStatistics.add(new WekaStatistic());
			}
			statistics.add(singleClassifierStatistics);
		}

		System.out.println("ANZAHL an RUNS: " + complete.size());
		for (List<List<List<MyEvaluation>>> run : complete) {
			System.out.println("Anzahl an SPLITS: " + run.size());
			for (List<List<MyEvaluation>> split : run) {
				System.out.println("Anzahl an Classifier: " + split.size());
				for (List<MyEvaluation> classifier : split) {
					System.out.println("Anzahl an Options: " + classifier.size());
					for (MyEvaluation option : classifier) {
						WekaStatistic weka = new WekaStatistic();
						// TODO evaluation to weka
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		List<MyClassifier> classifiers = WekaEvaluator.createTestClassifiers();

		weka.evaluateAll(classifiers);
	}

}