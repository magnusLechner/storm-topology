package at.illecker.sentistorm.commons.svm.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.lechner.preparation.ARFFParser;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public abstract class ModelTrainer {
	public static final String SVM_PROBLEM_FILE = "svm_problem.txt";
	private static final Logger LOG = LoggerFactory.getLogger(ModelTrainer.class);

	private Dataset dataset;
	private int nFold;
	private double crossValidationStatistic = -1.0;

	public ModelTrainer(Dataset dataset, int nFold) {
		this.dataset = dataset;
		this.nFold = nFold;
	}

	public abstract svm_model generateModel();

	public svm_model generateModel(List<FeaturedTweet> featuredTweets) {
		svm_problem svmProb = generateProblem(featuredTweets);
		saveProblem(svmProb, dataset.getDatasetPath() + File.separator + SVM_PROBLEM_FILE);

		svm_model svmModel = train(svmProb, dataset.getSVMParam());

		if (nFold > 1) {
			double accuracy = crossValidate(svmProb, dataset.getSVMParam(), nFold, false);
			crossValidationStatistic = accuracy;
		}
		return svmModel;
	}

	public static void saveProblem(svm_problem svmProb, String file) {
		// save problem in libSVM format
		// <label> <index1>:<value1> <index2>:<value2> ...
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < svmProb.l; i++) {
				// <label>
				br.write(Double.toString(svmProb.y[i]));
				for (int j = 0; j < svmProb.x[i].length; j++) {
					if (svmProb.x[i][j].value != 0) {
						// <index>:<value>
						br.write(" " + svmProb.x[i][j].index + ":" + svmProb.x[i][j].value);
					}
				}
				br.newLine();
				br.flush();
			}
			br.close();
			LOG.info("saved svm_problem in " + file);
		} catch (IOException e) {
			LOG.error("IOException: " + e.getMessage());
		}
	}

	public static svm_problem generateProblem(List<FeaturedTweet> featuredTweets) {
		int dataCount = featuredTweets.size();

		svm_problem svmProb = new svm_problem();
		svmProb.y = new double[dataCount];
		svmProb.l = dataCount;
		svmProb.x = new svm_node[dataCount][];

		int i = 0;
		for (FeaturedTweet tweet : featuredTweets) {
			Map<Integer, Double> featureVector = tweet.getFeatureVector();

			// set feature nodes
			svmProb.x[i] = new svm_node[featureVector.size()];
			int j = 0;
			for (Map.Entry<Integer, Double> feature : featureVector.entrySet()) {
				svm_node node = new svm_node();
				node.index = feature.getKey();
				node.value = feature.getValue();
				svmProb.x[i][j] = node;
				j++;
			}

			// set class / label
			svmProb.y[i] = tweet.getScore();
			i++;
		}

		return svmProb;
	}

	public static svm_model train(svm_problem svmProb, svm_parameter svmParam) {
		// set gamma to default 1/num_features if not specified
		if (svmParam.gamma == Double.MIN_VALUE) {
			svmParam.gamma = 1 / (double) svmProb.l;
		}

		String paramCheck = svm.svm_check_parameter(svmProb, svmParam);
		if (paramCheck != null) {
			LOG.error("svm_check_parameter: " + paramCheck);
		}

		// Disables SVM output
		svm.svm_set_print_string_function(new libsvm.svm_print_interface() {
			@Override
			public void print(String s) {
			}
		});

		return svm.svm_train(svmProb, svmParam);
	}

	public static double crossValidate(svm_problem svmProb, svm_parameter svmParam, int nFold, boolean printStats) {

		// set gamma to default 1/num_features if not specified
		if (svmParam.gamma == Double.MIN_VALUE) {
			svmParam.gamma = 1 / (double) svmProb.l;
		}

		double[] target = new double[svmProb.l];
		svm.svm_cross_validation(svmProb, svmParam, nFold, target);

		double correctCounter = 0;
		for (int i = 0; i < svmProb.l; i++) {
			if (target[i] == svmProb.y[i]) {
				correctCounter++;
			}
		}

		// svmProb.l == |training size| == 450
		double accuracy = correctCounter / (double) svmProb.l;
		LOG.info("Cross Validation Accuracy: " + (100.0 * accuracy));

		if (printStats) {
			printStats(getConfusionMatrix(svmProb.y, target));
		}

		return accuracy;
	}

	public static int[][] getConfusionMatrix(double[] actualClass, double[] predictedClass) {
		if (actualClass.length != predictedClass.length) {
			return null;
		}

		// find the total number of classes
		int maxClassNum = 0;
		for (int i = 0; i < actualClass.length; i++) {
			if (actualClass[i] > maxClassNum)
				maxClassNum = (int) actualClass[i];
		}
		// add 1 because of class zero
		maxClassNum++;

		// create confusion matrix
		// rows represent the instances in an actual class
		// cols represent the instances in a predicted class
		int[][] confusionMatrix = new int[maxClassNum][maxClassNum];
		for (int i = 0; i < actualClass.length; i++) {
			confusionMatrix[(int) actualClass[i]][(int) predictedClass[i]]++;
		}
		return confusionMatrix;
	}

	public static void printStats(int[][] confusionMatrix) {
		int totalClasses = confusionMatrix.length;
		int total = 0;
		int totalCorrect = 0;

		int[] rowSum = new int[totalClasses];
		int[] colSum = new int[totalClasses];
		for (int i = 0; i < totalClasses; i++) {
			for (int j = 0; j < totalClasses; j++) {
				total += confusionMatrix[i][j];
				rowSum[i] += confusionMatrix[i][j];
				colSum[i] += confusionMatrix[j][i];
			}
			totalCorrect += confusionMatrix[i][i];
		}

		LOG.info("Confusion Matrix:");
		// print header
		StringBuffer sb = new StringBuffer();
		sb.append("\t\t");
		for (int i = 0; i < totalClasses; i++) {
			sb.append("\t").append(i);
		}
		sb.append("\t").append("total");
		LOG.info(sb.toString());
		// print matrix
		for (int i = 0; i < totalClasses; i++) {
			int[] predictedClasses = confusionMatrix[i];
			sb = new StringBuffer();
			sb.append("Class:\t" + i);
			for (int j = 0; j < predictedClasses.length; j++) {
				sb.append("\t").append(predictedClasses[j]);
			}
			sb.append("\t" + rowSum[i]);
			LOG.info(sb.toString());
		}
		sb = new StringBuffer();
		sb.append("total").append("\t");
		for (int i = 0; i < totalClasses; i++) {
			sb.append("\t").append(colSum[i]);
		}
		LOG.info(sb.toString() + "\n");

		LOG.info("Total: " + total);
		LOG.info("Correct: " + totalCorrect);
		LOG.info("Accuracy: " + (totalCorrect / (double) total));

		LOG.info("Scores per class:");
		double[] FScores = new double[totalClasses];
		for (int i = 0; i < totalClasses; i++) {
			int correctHitsPerClass = confusionMatrix[i][i];

			double precision = correctHitsPerClass / (double) colSum[i];
			double recall = correctHitsPerClass / (double) rowSum[i];
			FScores[i] = 2 * ((precision * recall) / (precision + recall));

			LOG.info("Class: " + i + " Precision: " + precision + " Recall: " + recall + " F-Score: " + FScores[i]);
		}

		// FScoreWeighted is a weighted average of the classes' f-scores,
		// weighted
		// by the proportion of how many elements are in each class.
		double FScoreWeighted = 0;
		for (int i = 0; i < totalClasses; i++) {
			FScoreWeighted += FScores[i] * colSum[i];
		}
		FScoreWeighted /= total;
		LOG.info("F-Score weighted: " + FScoreWeighted);

		// F-Score average of positive and negative
		double FScoreAveragePosNeg = (FScores[0] + FScores[1]) / 2;
		LOG.info("F-Score average(pos,neg): " + FScoreAveragePosNeg);

		// Macro-average: Average precision, recall, or F1 over the classes of
		// interest.

		// Micro-average: Sum corresponding cells to create a 2 x 2 confusion
		// matrix, and calculate precision in terms of the new matrix.
		// (In this set-up, precision, recall, and F1 are all the same.)
	}

	public Dataset getDataset() {
		return dataset;
	}

	public int getnFold() {
		return nFold;
	}

	public double getCrossValidationStatistic() {
		return crossValidationStatistic;
	}

	public void generateARFF(List<FeaturedTweet> featuredTweets, int featureVectorSize) {
		String outputPath = "resources/arff/Twitch/Training.arff";
		ARFFParser.generateARFF(featuredTweets, featureVectorSize, outputPath);
	}

}
