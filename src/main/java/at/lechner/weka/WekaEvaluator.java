package at.lechner.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import at.lechner.util.EvaluationUtil;
import at.lechner.weka.classifier.MyA1DE;
import at.lechner.weka.classifier.MyBFTree;
import at.lechner.weka.classifier.MyBayesNet;
import at.lechner.weka.classifier.MyCHIRP;
import at.lechner.weka.classifier.MyClassifier;
import at.lechner.weka.classifier.MyExtraTree;
import at.lechner.weka.classifier.MyFunctionalTree;
import at.lechner.weka.classifier.MyJ48;
import at.lechner.weka.classifier.MyLMT;
import at.lechner.weka.classifier.MyLogistic;
import at.lechner.weka.classifier.MyLogitBoost;
import at.lechner.weka.classifier.MyNBTree;
import at.lechner.weka.classifier.MyNaiveBayesMultinomial;
import at.lechner.weka.classifier.MyPART;
import at.lechner.weka.classifier.MyREPTree;
import at.lechner.weka.classifier.MyRandomForest;
import at.lechner.weka.classifier.MySimpleCart;
import at.lechner.weka.classifier.MySimpleLogistic;
import at.lechner.weka.classifier.MyZeroR;
import at.lechner.weka.statistic.ClassifierWrapper;
import at.lechner.weka.statistic.ConfusionMatrixStatistic;
import at.lechner.weka.statistic.MyEvaluation;
import at.lechner.weka.statistic.OptionWrapper;
import at.lechner.weka.statistic.StatisticPrinter;
import at.lechner.weka.statistic.WekaStatistic;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.bayes.AveragedNDependenceEstimators.A1DE;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SimpleLogistic;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.misc.CHIRP;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.BFTree;
import weka.classifiers.trees.ExtraTree;
import weka.classifiers.trees.FT;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.NBTree;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instances;

public class WekaEvaluator {

	public static final String TEST = "src/main/resources/arff/Twitch/Test.arff";
	public static final String TRAINING = "src/main/resources/arff/Twitch/Training.arff";

	public static final String WEKA_EVA_OUT = "src/main/evaluation/weka/successive_addition/weka_complete_overview.tsv";

	public static final String FIX_TRAINING_ARFF = "src/main/resources/arff/Twitch/weka_testing/complete/Training.arff";
	public static final String FIX_TEST_ARFF = "src/main/resources/arff/Twitch/weka_testing/complete/Test.arff";

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

	public void optimizeParameters(List<MyClassifier> classifiers) throws Exception {
		BufferedReader training = readDataFile(trainingARFF);
		Instances trainingInstance = new Instances(training);
		trainingInstance.setClassIndex(trainingInstance.numAttributes() - 1);

		StringBuilder sb = new StringBuilder();

		System.out.println("##### start optimizing Parameters #####");

		for (MyClassifier classifier : classifiers) {

			System.out.println("Current Classifier: " + classifier.getName());

			String parameter = classifier.findOptimalParameters(trainingInstance);
			sb.append(classifier.getName()).append("\n").append(parameter).append("\n");
		}
		EvaluationUtil.generateTSV("src/main/evaluation/weka/weka_evaluation/optimalParameters", sb.toString());

		System.out.println("##### finished optimizing Parameters #####");
	}

	public List<List<MyEvaluation>> evaluateAll(List<MyClassifier> classifiers) throws Exception {
		BufferedReader training = readDataFile(trainingARFF);
		BufferedReader test = readDataFile(testARFF);

		Instances trainingInstance = new Instances(training);
		trainingInstance.setClassIndex(trainingInstance.numAttributes() - 1);

		Instances testInstance = new Instances(test);
		testInstance.setClassIndex(testInstance.numAttributes() - 1);

		System.out.println("##### start evaluation #####");

		// Run for each model
		List<List<MyEvaluation>> allEvaluations = new ArrayList<List<MyEvaluation>>();
		for (int i = 0; i < classifiers.size(); i++) {

			System.out.println("Current Classifier: " + classifiers.get(i).getName() + "  Different Options: "
					+ classifiers.get(i).getOptionsListSize());

			List<MyEvaluation> classifierEvaluations = classify(classifiers.get(i), trainingInstance, testInstance);
			allEvaluations.add(classifierEvaluations);
		}

		System.out.println("##### finished evaluation #####");

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

				System.out.println("Current Option: " + myClassifier.getCompleteOption(i));

				myClassifier.setCurrentOption(myClassifier.getOption(i));
				myClassifier.buildClassifier(trainingSet);
				evaluation.evaluateModel(myClassifier.getClassifier(), testSet);

				MyEvaluation myEval = new MyEvaluation(trainingSet.size(), testSet.size(), myClassifier.getName(),
						myClassifier.getCompleteCurrentOption(), evaluation);
				evaluations.add(myEval);
			}
		}

		return evaluations;
	}

	public static List<MyClassifier> createClassifiers() {
		List<MyClassifier> classifiers = new ArrayList<MyClassifier>();
		try {
			MyClassifier a1de = new MyA1DE(new A1DE());
			MyClassifier bayesNet = new MyBayesNet(new BayesNet());
			MyClassifier bfTree = new MyBFTree(new BFTree());
			MyClassifier chirp = new MyCHIRP(new CHIRP());
			MyClassifier extraTree = new MyExtraTree(new ExtraTree());
			MyClassifier functionalTree = new MyFunctionalTree(new FT());
			MyClassifier j48 = new MyJ48(new J48());
			MyClassifier lmt = new MyLMT(new LMT());
			MyClassifier logistic = new MyLogistic(new Logistic());
			MyClassifier logitBoost = new MyLogitBoost(new LogitBoost());
			MyClassifier naiveBayesMultinomial = new MyNaiveBayesMultinomial(new NaiveBayesMultinomial());
			MyClassifier nbTree = new MyNBTree(new NBTree());
			MyClassifier part = new MyPART(new PART());
			MyClassifier randomForest = new MyRandomForest(new RandomForest());
			MyClassifier repTree = new MyREPTree(new REPTree());
			MyClassifier simpleCart = new MySimpleCart(new SimpleCart());
			MyClassifier simpleLogistic = new MySimpleLogistic(new SimpleLogistic());
			MyClassifier zeroR = new MyZeroR(new ZeroR());

			classifiers.add(a1de);
			classifiers.add(bayesNet);
			classifiers.add(bfTree);
			classifiers.add(chirp);
			classifiers.add(extraTree);
			classifiers.add(functionalTree);
			classifiers.add(j48);
			classifiers.add(lmt);
			classifiers.add(logistic);
			classifiers.add(logitBoost);
			classifiers.add(naiveBayesMultinomial);
			classifiers.add(nbTree);
			classifiers.add(part);
			classifiers.add(randomForest);
			classifiers.add(repTree);
			classifiers.add(simpleCart);
			classifiers.add(simpleLogistic);
			classifiers.add(zeroR);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifiers;
	}

	public static void printWekaCompleteResults(List<List<List<List<MyEvaluation>>>> complete) {
		printWekaCompleteResults(WEKA_EVA_OUT, complete);
	}

	public static void printWekaCompleteResults(String output, List<List<List<List<MyEvaluation>>>> complete) {
		List<List<List<WekaStatistic>>> allSplits = aggregateResults(complete);

		List<ClassifierWrapper> classifierWrapperList = new ArrayList<ClassifierWrapper>();

		for (int j = 0; j < allSplits.get(0).size(); j++) {
			classifierWrapperList.add(new ClassifierWrapper());
			List<WekaStatistic> singleClassifier = allSplits.get(0).get(j);
			for (int k = 0; k < singleClassifier.size(); k++) {
				classifierWrapperList.get(j).addOption(new OptionWrapper());
			}
		}

		for (int i = 0; i < allSplits.size(); i++) {
			List<List<WekaStatistic>> allClassifier = allSplits.get(i);
			for (int j = 0; j < allClassifier.size(); j++) {
				List<WekaStatistic> singleClassifier = allClassifier.get(j);
				for (int k = 0; k < singleClassifier.size(); k++) {
					WekaStatistic option = singleClassifier.get(k);
					classifierWrapperList.get(j).getOption(k).addSplit(option);
					classifierWrapperList.get(j).setName(option.getClassifierName());
					classifierWrapperList.get(j).getOption(k).setOption(option.getClassifierOption());
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		StatisticPrinter statPrint = new StatisticPrinter();

		for (ClassifierWrapper classifierWrapper : classifierWrapperList) {
			for (OptionWrapper optionWrapper : classifierWrapper.getOptions()) {

				statPrint.getClassifierName().append(classifierWrapper.getName()).append("\t");
				statPrint.getOptionName().append(optionWrapper.getOptionName()).append("\t");
				statPrint.appendStandardNames();

				for (int i = 0; i < optionWrapper.getSplits().size(); i++) {
					statPrint.getClassifierName().append("\t");
					statPrint.getOptionName().append(String.valueOf(optionWrapper.getSplit(i).getTrainSize()) + "\t");

					statPrint.getOverallRecall()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgOverallRecall()) + "\t");
					statPrint.getStdDevOverallRecall()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevOverallRecall()) + "\t");
					statPrint.getMacroOverallRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroOverallRecall()) + "\t");
					statPrint.getStdDevMacroOverallRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroOverallRecall()) + "\t");
					statPrint.getMacroPosNegRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroPosNegRecall()) + "\t");
					statPrint.getStdDevMacroPosNegRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroPosNegRecall()) + "\t");

					statPrint.getOverallPrecision()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgOverallPrecision()) + "\t");
					statPrint.getStdDevOverallPrecision()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevOverallPrecision()) + "\t");
					statPrint.getMacroOverallPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroOverallPrecision()) + "\t");
					statPrint.getStdDevMacroOverallPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroOverallPrecision()) + "\t");
					statPrint.getMacroPosNegPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroPosNegPrecision()) + "\t");
					statPrint.getStdDevMacroPosNegPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroPosNegPrecision()) + "\t");

					statPrint.getNegativeRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNegativeRecall()) + "\t");
					statPrint.getStdDevNegativeRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNegativeRecall()) + "\t");
					statPrint.getNegativePrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNegativePrecision()) + "\t");
					statPrint.getStdDevNegativePrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNegativePrecision()) + "\t");
					statPrint.getNegativeFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNegativeFMeasure()) + "\t");
					statPrint.getStdDevNegativeFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNegativeFMeasure()) + "\t");

					statPrint.getNeutralRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNeutralRecall()) + "\t");
					statPrint.getStdDevNeutralRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNeutralRecall()) + "\t");
					statPrint.getNeutralPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNeutralPrecision()) + "\t");
					statPrint.getStdDevNeutralPrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNeutralPrecision()) + "\t");
					statPrint.getNeutralFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgNeutralFMeasure()) + "\t");
					statPrint.getStdDevNeutralFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevNeutralFMeasure()) + "\t");

					statPrint.getPositiveRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgPositiveRecall()) + "\t");
					statPrint.getStdDevPositiveRecalls()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevPositiveRecall()) + "\t");
					statPrint.getPositivePrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgPositivePrecision()) + "\t");
					statPrint.getStdDevPositivePrecisions()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevPositivePrecision()) + "\t");
					statPrint.getPositiveFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgPositiveFMeasure()) + "\t");
					statPrint.getStdDevPositiveFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevPositiveFMeasure()) + "\t");

					statPrint.getMicroFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMicroFMeasure()) + "\t");
					statPrint.getStdDevMicroFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMicroFMeasure()) + "\t");
					statPrint.getMacroFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroFMeasure()) + "\t");
					statPrint.getStdDevMacroFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroFMeasure()) + "\t");

					statPrint.getMicroPosNegFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMicroPosNegFMeasure()) + "\t");
					statPrint.getStdDevMicroPosNegFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMicroPosNegFMeasure()) + "\t");
					statPrint.getMacroPosNegFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getAvgMacroPosNegFMeasure()) + "\t");
					statPrint.getStdDevMacroPosNegFMeasure()
							.append(completeFormat(optionWrapper.getSplit(i).getStdDevMacroPosNegFMeasure()) + "\t");
				}
				statPrint.appendAllTab();
			}

			sb.append(statPrint.getCompleteString());
			statPrint.clearAll();
		}

		EvaluationUtil.generateTSV("src/main/evaluation/weka/weka_evaluation/weka_result.tsv", sb.toString());
	}

	private static List<List<List<WekaStatistic>>> aggregateResults(List<List<List<List<MyEvaluation>>>> complete) {
		List<List<List<WekaStatistic>>> allSplits = new ArrayList<List<List<WekaStatistic>>>();

		for (int k = 0; k < complete.get(0).size(); k++) {
			List<List<WekaStatistic>> allClassifiers = new ArrayList<List<WekaStatistic>>();
			for (int i = 0; i < complete.get(0).get(0).size(); i++) {
				List<WekaStatistic> singleClassifierStatistics = new ArrayList<WekaStatistic>();
				for (int j = 0; j < complete.get(0).get(0).get(i).size(); j++) {
					MyEvaluation myEva = complete.get(0).get(k).get(i).get(j);
					singleClassifierStatistics.add(new WekaStatistic(myEva.getTrainingSize(), myEva.getTestSize(),
							myEva.getClassifierOption(), myEva.getClassifierName()));
				}
				allClassifiers.add(singleClassifierStatistics);
			}
			allSplits.add(allClassifiers);
		}

		for (List<List<List<MyEvaluation>>> run : complete) {
			for (int k = 0; k < run.size(); k++) {
				List<List<MyEvaluation>> split = run.get(k);
				for (int i = 0; i < split.size(); i++) {
					List<MyEvaluation> classifier = split.get(i);
					for (int j = 0; j < classifier.size(); j++) {
						MyEvaluation option = classifier.get(j);
						Evaluation eva = option.getEvaluation();
						WekaStatistic currentStat = allSplits.get(k).get(i).get(j);

						currentStat.addNotUnclassified((100 - eva.pctUnclassified()) / 100);

						ConfusionMatrixStatistic cms = new ConfusionMatrixStatistic(convertCM(eva.confusionMatrix()));
						currentStat.addCMS(cms);
					}
				}
			}
		}

		return allSplits;
	}

	private static int[][] convertCM(double[][] cm) {
		int[][] newCM = new int[cm[0].length][cm[1].length];
		for (int i = 0; i < cm[0].length; i++) {
			for (int j = 0; j < cm[1].length; j++) {
				newCM[i][j] = (int) cm[i][j];
			}
		}
		return newCM;
	}

	private static String completeFormat(Double d) {
		DecimalFormat df = new DecimalFormat("##.####");
		return replaceDot(df.format(d));
	}

	private static String replaceDot(String s) {
		return s.replace(".", ",");
	}

	public static void main(String[] args) throws Exception {
		WekaEvaluator weka = new WekaEvaluator("src/main/resources/arff/Twitch/weka_testing/complete/Training.arff",
				"src/main/resources/arff/Twitch/weka_testing/complete/Test.arff");

		List<MyClassifier> classifiers = WekaEvaluator.createClassifiers();

//		weka.optimizeParameters(classifiers);
		weka.evaluateAll(classifiers);
	}

}