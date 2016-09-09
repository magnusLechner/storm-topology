package at.lechner.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import at.lechner.weka.classifier.MyClassifier;
import at.lechner.weka.classifier.MyJ48;
import weka.classifiers.Evaluation;
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

	public List<List<Evaluation>> evaluateAll(List<MyClassifier> classifiers) throws Exception {
		BufferedReader training = readDataFile(trainingARFF);
		BufferedReader test = readDataFile(testARFF);

		Instances trainingInstance = new Instances(training);
		trainingInstance.setClassIndex(trainingInstance.numAttributes() - 1);

		Instances testInstance = new Instances(test);
		testInstance.setClassIndex(testInstance.numAttributes() - 1);

		// Run for each model
		List<List<Evaluation>> allEvaluations = new ArrayList<List<Evaluation>>();
		for (int i = 0; i < classifiers.size(); i++) {
			List<Evaluation> classifierEvaluations = classify(classifiers.get(i), trainingInstance, testInstance);
			allEvaluations.add(classifierEvaluations);
		}

		printEvaluations(allEvaluations, classifiers);

		return allEvaluations;
	}

	private void printEvaluations(List<List<Evaluation>> allEvaluations, List<MyClassifier> classifiers) {
		for (int i = 0; i < classifiers.size(); i++) {
			System.out.println("#####  " + classifiers.get(i).getName() + "  #####" + "\n");
			for (int j = 0; j < allEvaluations.get(i).size(); j++) {
				System.out.println("Used Options: " + classifiers.get(i).getCompleteOption(j));
				System.out.println("overall percentage unclassified: " + allEvaluations.get(i).get(j).pctUnclassified());
				System.out.println("overall percentage correct classified: " + allEvaluations.get(i).get(j).pctCorrect());
				System.out.println(" ");
				System.out.println("precision NEGATIVE: " + allEvaluations.get(i).get(j).precision(0));
				System.out.println("recall NEGATIVE: " + allEvaluations.get(i).get(j).recall(0));
				System.out.println("precision NEUTRAL: " + allEvaluations.get(i).get(j).precision(1));
				System.out.println("recall NEUTRAL: " + allEvaluations.get(i).get(j).recall(1));
				System.out.println("precision POSITIVE: " + allEvaluations.get(i).get(j).precision(2));
				System.out.println("recall POSITIVE: " + allEvaluations.get(i).get(j).recall(2));
				System.out.println(" ");
				System.out.println("Summary String: ");
				System.out.println(allEvaluations.get(i).get(j).toSummaryString());
				System.out.println("\n");
			}
			System.out.println("\n");
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

	private List<Evaluation> classify(MyClassifier myClassifier, Instances trainingSet, Instances testingSet)
			throws Exception {
		List<Evaluation> evaluations = new ArrayList<Evaluation>();
		if (myClassifier.getOptionsList().size() == 0) {
			Evaluation evaluation = new Evaluation(trainingSet);

			myClassifier.buildClassifier(trainingSet);
			evaluation.evaluateModel(myClassifier.getClassifier(), testingSet);
			evaluations.add(evaluation);
		} else {
			for (int i = 0; i < myClassifier.getOptionsListSize(); i++) {
				Evaluation evaluation = new Evaluation(trainingSet);

				myClassifier.setOption(myClassifier.getOption(i));
				myClassifier.buildClassifier(trainingSet);
				evaluation.evaluateModel(myClassifier.getClassifier(), testingSet);

				evaluations.add(evaluation);
			}
		}

		return evaluations;
	}

	public static void main(String[] args) throws Exception {
		// TEST
		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/Test.arff");

		MyClassifier j48 = new MyJ48();
		classifiers.add(j48);

		weka.evaluateAll(classifiers);
	}

}