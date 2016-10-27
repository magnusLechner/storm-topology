/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.storm.commons.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;
import weka.classifiers.Evaluation;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.storm.commons.Configuration;
import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.SentimentClass;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.nopos.NoPOSCombinedFeatureVectorGenerator;
import at.storm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.storm.commons.featurevector.nopos.NoPOSSentimentFeatureVectorGenerator;
import at.storm.commons.featurevector.nopos.NoPOSTfIdfFeatureVectorGenerator;
import at.storm.commons.featurevector.pos.CombinedFeatureVectorGenerator;
import at.storm.commons.featurevector.pos.FeatureVectorGenerator;
import at.storm.commons.featurevector.pos.SentimentFeatureVectorGenerator;
import at.storm.commons.featurevector.pos.SpecialFeatureVectorGenerator;
import at.storm.commons.featurevector.pos.TfIdfFeatureVectorGenerator;
import at.storm.commons.featurevector.selector.FVGSelector;
import at.storm.commons.featurevector.selector.NoPOSFVGSelector;
import at.storm.commons.svm.box.NoPOSSVMBox;
import at.storm.commons.svm.box.POSSVMBox;
import at.storm.commons.svm.box.SVMBox;
import at.storm.commons.svm.prediction.statistic.PredictionStatistic;
import at.storm.commons.tfidf.TfIdfNormalization;
import at.storm.commons.tfidf.TfType;
import at.storm.commons.tfidf.pos.TweetTfIdf;
import at.storm.commons.util.io.SerializationUtils;
import at.storm.components.POSTagger;
import at.storm.components.Preprocessor;
import at.storm.components.Tokenizer;
import at.testing.commons.MyTuple;
import at.testing.evaluation.PipelineEvaluation;
import at.testing.preparation.ARFFParser;
import at.testing.preparation.SVMPreparation;
import at.testing.util.EvaluationUtil;
import at.testing.weka.ARFFTrainer;
import at.testing.weka.WekaEvaluator;
import at.testing.weka.classifier.MyClassifier;
import at.testing.weka.classifier.MyJ48;
import at.testing.weka.statistic.MyEvaluation;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class SVM {
	public static final String SVM_PROBLEM_FILE = "svm_problem.txt";
	public static final String SVM_MODEL_FILE_SER = "svm_model.ser";
	private static final Logger LOG = LoggerFactory.getLogger(SVM.class);

	public static svm_parameter getDefaultParameter() {
		svm_parameter param = new svm_parameter();
		// type of SVM
		param.svm_type = svm_parameter.C_SVC; // default

		// type of kernel function
		param.kernel_type = svm_parameter.RBF; // default

		// degree in kernel function (default 3)
		param.degree = 3;

		// gamma in kernel function (default 1/num_features)
		// gamma = 2^−15, 2^−13, ..., 2^3
		param.gamma = Double.MIN_VALUE;

		// parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)
		// C = 2^−5, 2^−3, ..., 2^15
		param.C = 1; // cost of constraints violation default 1

		// coef0 in kernel function (default 0)
		param.coef0 = 0;

		// parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)
		param.nu = 0.5;

		// epsilon in loss function of epsilon-SVR (default 0.1)
		param.p = 0.1;

		// tolerance of termination criterion (default 0.001)
		param.eps = 0.001;

		// whether to use the shrinking heuristics, 0 or 1
		// (default 1)
		param.shrinking = 1;

		// whether to train a SVC or SVR model for probability estimates, 0 or 1
		// (default 0)
		// 1 means model with probability information is obtained
		param.probability = 1;

		// parameter C of class i to weight*C, for C-SVC (default 1)
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		// cache memory size in MB (default 100)
		param.cache_size = 200;

		return param;
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

	public static svm_model train(svm_problem svmProb, svm_parameter svmParam) {
		// set gamma to default 1/num_features if not specified
		if (svmParam.gamma == Double.MIN_VALUE) {
			svmParam.gamma = 1 / (double) svmProb.l;
		}

		String paramCheck = svm.svm_check_parameter(svmProb, svmParam);
		if (paramCheck != null) {
			LOG.error("svm_check_parameter: " + paramCheck);
		}

		return svm.svm_train(svmProb, svmParam);
	}

	public static double crossValidate(svm_problem svmProb, svm_parameter svmParam, int nFold) {
		return crossValidate(svmProb, svmParam, nFold, false);
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

	//TODO 
	//schau kernel 			in yaml
	//schau training.tsv 	richtige groeße
	//schau gewichte		in yaml	
	//schau gamma max.		in dieser methode
	public static void coarseGrainedParamterSearch(svm_problem svmProb, svm_parameter svmParam) {
		// coarse grained paramter search
		int maxC = 11;
		double[] c = new double[maxC];
		// C = 2^−5, 2^−3, ..., 2^15
		for (int i = 0; i < maxC; i++) {
			c[i] = Math.pow(2, -1 + (i * 2));
		}
		
		//linear
//		int maxC = 4;
//		double[] c = new double[maxC];
//		for (int i = 0; i < maxC; i++) {
//			c[i] = Math.pow(2, -2 + i);
//		}
//		int maxGamma = 1;
		
		//rbf
		int maxGamma = 10;
		double[] gamma = new double[maxGamma];
		// gamma = 2^−15, 2^−13, ..., 2^3
		for (int j = 0; j < maxGamma; j++) {
			gamma[j] = Math.pow(2, -21 + (j * 2));
		}
		
		paramterSearch(svmProb, svmParam, c, gamma);
	}

	private static class FindParameterCallable implements Callable<double[]> {
		private svm_problem m_svmProb;
		private svm_parameter m_svmParam;
		private long m_i;
		private long m_j;

		public FindParameterCallable(svm_problem svmProb, svm_parameter svmParam, long i, long j) {
			m_svmProb = svmProb;
			m_svmParam = svmParam;
			m_i = i;
			m_j = j;
		}

		@Override
		public double[] call() throws Exception {
			long startTime = System.currentTimeMillis();
			double accuracy = crossValidate(m_svmProb, m_svmParam, 10);
			long estimatedTime = System.currentTimeMillis() - startTime;
			return new double[] { m_i, m_j, accuracy, m_svmParam.C, m_svmParam.gamma, estimatedTime };
		}
	}

	public static void paramterSearch(svm_problem svmProb, svm_parameter svmParam, double[] c, double[] gamma) {
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(cores);
		Set<Callable<double[]>> callables = new HashSet<Callable<double[]>>();

		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < gamma.length; j++) {
				svm_parameter param = (svm_parameter) svmParam.clone();
				param.C = c[i];
				param.gamma = gamma[j];
				callables.add(new FindParameterCallable(svmProb, param, i, j));
			}
		}

		try {
			long startTime = System.currentTimeMillis();
			List<Future<double[]>> futures = executorService.invokeAll(callables);
			for (Future<double[]> future : futures) {
				double[] result = future.get();
				LOG.info("findParamters[" + result[0] + "," + result[1] + "] C=" + result[3] + " gamma=" + result[4]
						+ " accuracy: " + result[2] + " time: " + result[5] + " ms");
			}
			long estimatedTime = System.currentTimeMillis() - startTime;
			LOG.info("findParamters total execution time: " + estimatedTime + " ms - " + (estimatedTime / 1000)
					+ " sec");

			// output CSV file

			StringBuilder sb = new StringBuilder();

			System.out.println(
					"CSV file of paramterSearch with C=" + Arrays.toString(c) + " gamma=" + Arrays.toString(gamma));
			sb.toString();
			System.out.println("i;j;C;gamma;accuracy;time_ms");
			for (Future<double[]> future : futures) {
				double[] result = future.get();
				// LOG.info(result[0] + ";" + result[1] + ";" + result[3] + ";"
				// + result[4] + ";" + result[2] + ";"
				// + result[5]);
				System.out.println(result[0] + "\t" + result[1] + "\t" + result[3] + "\t" + result[4] + "\t" + result[2]
						+ "\t" + result[5]);
			}

		} catch (InterruptedException e) {
			LOG.error("InterruptedException: " + e.getMessage());
		} catch (ExecutionException e) {
			LOG.error("ExecutionException: " + e.getMessage());
		}

		executorService.shutdown();
	}

	public static double evaluate(Map<Integer, Double> featureVector, svm_model svmModel, int totalClasses) {
		return evaluate(featureVector, svmModel, totalClasses, false);
	}

	public static double evaluate(Map<Integer, Double> featureVector, svm_model svmModel, int totalClasses,
			boolean logging) {

		// set feature nodes
		svm_node[] testNodes = new svm_node[featureVector.size()];
		int i = 0;
		for (Map.Entry<Integer, Double> feature : featureVector.entrySet()) {
			svm_node node = new svm_node();
			node.index = feature.getKey();
			node.value = feature.getValue();
			testNodes[i] = node;
			i++;
		}

		double predictedClass = svm.svm_predict(svmModel, testNodes);

		if (logging) {
			int[] labels = new int[totalClasses];
			svm.svm_get_labels(svmModel, labels);

			double[] probEstimates = new double[totalClasses];
			double predictedClassProb = svm.svm_predict_probability(svmModel, testNodes, probEstimates);

			for (i = 0; i < totalClasses; i++) {
				LOG.info("Label[" + i + "]: " + labels[i] + " Probability: " + probEstimates[i]);
			}
			LOG.info("PredictedClass: " + predictedClassProb);
		}

		return predictedClass;
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

	public static void svm(Dataset dataset, Class<? extends FeatureVectorGenerator> featureVectorGenerator,
			int nFoldCrossValidation, boolean parameterSearch, boolean useSerialization) {

		FeatureVectorGenerator fvg = null;
		Preprocessor preprocessor = null;
		POSTagger posTagger = null;

		// Prepare Train tweets
		LOG.info("Prepare Train data...");
		List<FeaturedTweet> featuredTrainTweets = null;
		if (useSerialization) {
			featuredTrainTweets = SerializationUtils.deserialize(dataset.getTrainDataSerializationFile());
		}

		if (featuredTrainTweets == null) {
			// Read train tweets
			List<Tweet> trainTweets = dataset.getTrainTweets(false);

			LOG.info("Read train tweets from " + dataset.getTrainDataFile());
			Dataset.printTweetStats(trainTweets);

			// Tokenize
			LOG.info("Tokenize train tweets...");
			List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);

			// Preprocess
			preprocessor = Preprocessor.getInstance();

			LOG.info("Preprocess train tweets...");
			List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);

			// POS Tagging
			posTagger = POSTagger.getInstance();
			LOG.info("POS Tagging of train tweets...");
			List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);

			// Create Feature Vector Generator
			if (featureVectorGenerator.equals(SentimentFeatureVectorGenerator.class)) {
				LOG.info("Load SentimentFeatureVectorGenerator...");
				fvg = new SentimentFeatureVectorGenerator();

			} else if (featureVectorGenerator.equals(TfIdfFeatureVectorGenerator.class)) {
				TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.LOG,
						TfIdfNormalization.COS, true);
				LOG.info("Load TfIdfFeatureVectorGenerator...");
				fvg = new TfIdfFeatureVectorGenerator(tweetTfIdf);

			} else if (featureVectorGenerator.equals(CombinedFeatureVectorGenerator.class)) {
				TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.LOG,
						TfIdfNormalization.COS, true);
				LOG.info("Load CombinedFeatureVectorGenerator...");
				fvg = new CombinedFeatureVectorGenerator(true, tweetTfIdf);

			} else {
				throw new UnsupportedOperationException(
						"FeatureVectorGenerator '" + featureVectorGenerator.getName() + "' is not supported!");
			}

			// Feature Vector Generation
			LOG.info("Generate Feature Vectors for train tweets...");
			featuredTrainTweets = new ArrayList<FeaturedTweet>();
			for (int i = 0; i < taggedTweets.size(); i++) {
				List<TaggedToken> taggedTweet = taggedTweets.get(i);
				Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
				featuredTrainTweets.add(new FeaturedTweet(trainTweets.get(i), tokenizedTweets.get(i),
						preprocessedTweets.get(i), taggedTweet, featureVector));
			}

			// Serialize training data including feature vectors
			if (useSerialization) {
				SerializationUtils.serializeCollection(featuredTrainTweets, dataset.getTrainDataSerializationFile());
			}
		}

		// Prepare Test tweets
		LOG.info("Prepare Test data...");
		List<FeaturedTweet> featuredTestTweets = null;
		if (useSerialization) {
			featuredTestTweets = SerializationUtils.deserialize(dataset.getTestDataSerializationFile());
		}

		if (featuredTestTweets == null) {
			if (fvg == null) {
				LOG.error("Train and test data must use the same FeatureVectorGenerator!");
				System.exit(1);
			}

			// read test tweets
			List<Tweet> testTweets = dataset.getTestTweets();
			LOG.info("Read test tweets from " + dataset.getTestDataFile());
			Dataset.printTweetStats(testTweets);

			// Tokenize
			LOG.info("Tokenize test tweets...");
			List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(testTweets);

			// Preprocess
			LOG.info("Preprocess test tweets...");
			List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);

			// POS Tagging
			LOG.info("POS Tagging of test tweets...");
			List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);

			// Feature Vector Generation
			LOG.info("Generate Feature Vectors for test tweets...");
			featuredTestTweets = new ArrayList<FeaturedTweet>();
			for (int i = 0; i < taggedTweets.size(); i++) {
				List<TaggedToken> taggedTweet = taggedTweets.get(i);
				Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
				featuredTestTweets.add(new FeaturedTweet(testTweets.get(i), tokenizedTweets.get(i),
						preprocessedTweets.get(i), taggedTweet, featureVector));
			}

			// Serialize test data
			if (useSerialization) {
				SerializationUtils.serializeCollection(featuredTestTweets, dataset.getTestDataSerializationFile());
			}
		}

		// Optional parameter search of C and gamma
		if (parameterSearch) {
			svm_parameter svmParam = dataset.getSVMParam();
			LOG.info("Generate SVM problem...");
			svm_problem svmProb = generateProblem(featuredTrainTweets);

			// 1) coarse grained paramter search
			coarseGrainedParamterSearch(svmProb, svmParam);

////			 2) fine grained paramter search
//			// C = 2^6, ..., 2^12
//			double[] c = new double[7];
//			for (int i = 0; i < 7; i++) {
//				c[i] = Math.pow(2, 12 + i);
//			}
//			// gamma = 2^−14, 2^−13, ..., 2^-8
//			double[] gamma = new double[7];
//			for (int j = 0; j < 7; j++) {
//				gamma[j] = Math.pow(2, -20 + j);
//			}
//
//			LOG.info("SVM paramterSearch...");
//			LOG.info("Kernel: " + svmParam.kernel_type);
//			paramterSearch(svmProb, svmParam, c, gamma);

		} else {

			int totalClasses = 3;
			// classes 0 = negative, 1 = neutral, 2 = positive

			svm_model svmModel = null;
			LOG.info("Try loading SVM model...");
			// deserialize svmModel
			if (useSerialization) {
				svmModel = SerializationUtils
						.deserialize(dataset.getDatasetPath() + File.separator + SVM_MODEL_FILE_SER);
			}

			if (svmModel == null) {
				LOG.info("Generate SVM problem...");
				svm_problem svmProb = generateProblem(featuredTrainTweets);

				// save svm problem in libSVM format
				saveProblem(svmProb, dataset.getDatasetPath() + File.separator + SVM_PROBLEM_FILE);

				// train model
				LOG.info("Train SVM model...");
				long startTime = System.currentTimeMillis();
				svmModel = train(svmProb, dataset.getSVMParam());
				LOG.info("Train SVM model finished after " + (System.currentTimeMillis() - startTime) + " ms");

				// serialize svm model
				if (useSerialization) {
					SerializationUtils.serialize(svmModel,
							dataset.getDatasetPath() + File.separator + SVM_MODEL_FILE_SER);
				}

				if (nFoldCrossValidation > 1) {
					LOG.info("Run n-fold cross validation...");
					startTime = System.currentTimeMillis();
					double accuracy = crossValidate(svmProb, dataset.getSVMParam(), nFoldCrossValidation, true);
					LOG.info("Cross Validation finished after " + (System.currentTimeMillis() - startTime) + " ms");
					LOG.info("Cross Validation Accurancy: " + accuracy);
				}
			}

			// Evaluate test tweets
			long countMatches = 0;
			int[][] confusionMatrix = new int[totalClasses][totalClasses];
			LOG.info("Evaluate test tweets...");

			long startTime = System.currentTimeMillis();
			for (FeaturedTweet tweet : featuredTestTweets) {

				Map<Integer, Double> featureVector = tweet.getFeatureVector();

				double predictedClass = evaluate(featureVector, svmModel, totalClasses);

				int actualClass = tweet.getScore().intValue();
				if (predictedClass == actualClass) {
					countMatches++;
				}
				confusionMatrix[actualClass][(int) predictedClass]++;
			}

			LOG.info("Evaluate finished after " + (System.currentTimeMillis() - startTime) + " ms");
			LOG.info("Total test tweets: " + featuredTestTweets.size());
			LOG.info("Matches: " + countMatches);
			double accuracy = (double) countMatches / (double) featuredTestTweets.size();
			LOG.info("Accuracy: " + accuracy);

			printStats(confusionMatrix);

			svm.EXEC_SERV.shutdown();
		}
	}

	public static void evaluateBoxesPipeline(Dataset dataset, boolean withPOS, int iterations, int nFold)
			throws IOException {
		Double[] cpuTime = new Double[iterations];
		Double[] precision = new Double[iterations];
		Double[] recall = new Double[iterations];
		Double[] positiveRecall = new Double[iterations];
		Double[] positivePrecision = new Double[iterations];
		Double[] positiveFMeasure = new Double[iterations];
		Double[] neutralRecall = new Double[iterations];
		Double[] neutralPrecision = new Double[iterations];
		Double[] neutralFMeasure = new Double[iterations];
		Double[] negativeRecall = new Double[iterations];
		Double[] negativePrecision = new Double[iterations];
		Double[] negativeFMeasure = new Double[iterations];
		Double[] microFMeasure = new Double[iterations];
		Double[] microPosNegFMeasure = new Double[iterations];
		Double[] macroFMeasure = new Double[iterations];
		Double[] macroPosNegFMeasure = new Double[iterations];

		Map<String, Double[]> statistics = new LinkedHashMap<String, Double[]>();

		SVMBox pipelineBox = null;

		try {
			for (int k = -2; k < iterations; k++) {

				// random split in test and training data
				SVMPreparation.prepareForCrossValidation();

				if (!withPOS) {
					NoPOSFeatureVectorGenerator noPOSFVG = NoPOSFVGSelector
							.selectFVG(dataset.getTrainTweets(false, true), NoPOSCombinedFeatureVectorGenerator.class);
					pipelineBox = new NoPOSSVMBox(dataset, noPOSFVG, nFold, false);
				} else {
					FeatureVectorGenerator posFVG = FVGSelector.selectFVG(dataset.getTrainTweets(false, true),
							CombinedFeatureVectorGenerator.class);
					pipelineBox = new POSSVMBox(dataset, posFVG, nFold, false);
				}

				pipelineBox.setName("PipeLine-Box");

				System.out.println("Training finished.");

				pipelineBox.getPredictor().getPredictionStatistic().startNewStopWatch();
				pipelineBox.predict(dataset.getTestTweets(true));
				pipelineBox.getPredictor().getPredictionStatistic().elapsedTime();

				if (k >= 0) {
					cpuTime[k] = pipelineBox.getPredictor().getPredictionStatistic().getSumElapsedTime();
					recall[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMicroOverallRecall();
					precision[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getMicroOverallPrecision();

					positiveRecall[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getRecallPositive();
					positivePrecision[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getPrecisionPositive();
					positiveFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getPositiveFMeasure();

					neutralRecall[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS().getRecallNeutral();
					neutralPrecision[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getPrecisionNeutral();
					neutralFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getNeutralFMeasure();

					negativeRecall[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getRecallNegative();
					negativePrecision[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getPrecisionNegative();
					negativeFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getNegativeFMeasure();

					microFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMicroFMeasure();
					microPosNegFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getMicroPosNegFMeasure();
					macroFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMacroFMeasure();
					macroPosNegFMeasure[k] = pipelineBox.getPredictor().getPredictionStatistic().getCMS()
							.getMicroPosNegFMeasure();

					System.out.println("IsNegativeButPredictedAsNeutral : "
							+ pipelineBox.getPredictor().getPredictionStatistic().getIsNegativeButPredictedAsNeutral());
					System.out.println("IsNegativeButPredictedAsPositive: " + pipelineBox.getPredictor()
							.getPredictionStatistic().getIsNegativeButPredictedAsPositive());
					System.out.println("IsNeutralButPredictedAsNegative : "
							+ pipelineBox.getPredictor().getPredictionStatistic().getIsNeutralButPredictedAsNegative());
					System.out.println("IsNeutralButPredictedAsPositive : "
							+ pipelineBox.getPredictor().getPredictionStatistic().getIsNeutralButPredictedAsPositive());
					System.out.println("IsPositiveButPredictedAsNegative: " + pipelineBox.getPredictor()
							.getPredictionStatistic().getIsPositiveButPredictedAsNegative());
					System.out.println("IsPositiveButPredictedAsNeutral : "
							+ pipelineBox.getPredictor().getPredictionStatistic().getIsPositiveButPredictedAsNeutral());
					System.out.println("Amount of messages with Twitch-Emote          : "
							+ pipelineBox.getPredictor().getPredictionStatistic().getMsgsWithTwitchEmote());
					System.out.println("Amount of messages with Sentiment-Twitch-Emote: "
							+ pipelineBox.getPredictor().getPredictionStatistic().getMsgsWithSentimentTwitchEmote());

					System.out.println("Lenn Evaluation result (REMEMBER: -1 is NEGATIVE and 1 is POSITIVE): "
							+ (pipelineBox.getPredictor().getPredictionStatistic().getLennSum()
									/ dataset.getTestTweets(true).size()));

					if (k == iterations - 1) {
						activateDebugOutput(false, false, k, pipelineBox);
					}
				}
			}
		} finally {
			if (pipelineBox != null) {
				pipelineBox.shutdown();
			}
		}

		statistics.put("recall", recall);
		statistics.put("precision", precision);
		statistics.put("cpu-time", cpuTime);

		statistics.put("positiveRecall", positiveRecall);
		statistics.put("positivePrecision", positivePrecision);
		statistics.put("positive FMeasure", positiveFMeasure);

		statistics.put("neutralRecall", neutralRecall);
		statistics.put("neutralPrecision", neutralPrecision);
		statistics.put("neutral FMeasure", neutralFMeasure);

		statistics.put("negativeRecall", negativeRecall);
		statistics.put("negativePrecision", negativePrecision);
		statistics.put("negative FMeasure", negativeFMeasure);

		statistics.put("micro f-Measure", microFMeasure);
		statistics.put("micro pos/neg f-Measure", microPosNegFMeasure);
		statistics.put("macro f-Measure", macroFMeasure);
		statistics.put("macro pos/neg f-Measure", macroPosNegFMeasure);

		printPipelineResults(iterations, statistics);
	}

	public static void evaluateDynamicSlices(Dataset dataset, boolean withPOS, int iterations, int nFold,
			boolean shutdown, int sliceGenerator, int startTrainingSetSize, int stepSize, int startTestSize)
			throws IOException {
		List<List<Double>> trainingSize = new ArrayList<List<Double>>(iterations);
		List<List<Double>> testSize = new ArrayList<List<Double>>(iterations);
		List<List<Double>> cpuTime = new ArrayList<List<Double>>(iterations);
		List<List<Double>> precision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroAvgPrecision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroAvgPosNegPrecision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> recall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroAvgRecall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroAvgPosNegRecall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> positiveRecall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> positivePrecision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> positiveFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> neutralRecall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> neutralPrecision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> neutralFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> negativeRecall = new ArrayList<List<Double>>(iterations);
		List<List<Double>> negativePrecision = new ArrayList<List<Double>>(iterations);
		List<List<Double>> negativeFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> microFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> microPosNegFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> macroPosNegFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> averageAccuracy = new ArrayList<List<Double>>(iterations);
		List<List<Double>> errorRate = new ArrayList<List<Double>>(iterations);
		List<List<Double>> otherFMeasure = new ArrayList<List<Double>>(iterations);
		List<List<Double>> accuracy = new ArrayList<List<Double>>(iterations);

		Map<String, List<List<Double>>> statistics = new LinkedHashMap<String, List<List<Double>>>();

		SVMBox pipelineBox = null;

		try {
			for (int currentIteration = 0; currentIteration < iterations; currentIteration++) {

				System.err.println("ITERATION: " + currentIteration);

				List<Double> trainingSizeSingleRun = new ArrayList<Double>();
				List<Double> testSizeSingleRun = new ArrayList<Double>();
				List<Double> cpuTimeSingleRun = new ArrayList<Double>();
				List<Double> recallSingleRun = new ArrayList<Double>();
				List<Double> macroAvgRecallSingleRun = new ArrayList<Double>();
				List<Double> macroAvgPosNegRecallSingleRun = new ArrayList<Double>();
				List<Double> precisionSingleRun = new ArrayList<Double>();
				List<Double> macroAvgPrecisionSingleRun = new ArrayList<Double>();
				List<Double> macroAvgPosNegPrecisionSingleRun = new ArrayList<Double>();
				List<Double> positiveRecallSingleRun = new ArrayList<Double>();
				List<Double> positivePrecisionSingleRun = new ArrayList<Double>();
				List<Double> positiveFMeasureSingleRun = new ArrayList<Double>();
				List<Double> neutralRecallSingleRun = new ArrayList<Double>();
				List<Double> neutralPrecisionSingleRun = new ArrayList<Double>();
				List<Double> neutralFMeasureSingleRun = new ArrayList<Double>();
				List<Double> negativeRecallSingleRun = new ArrayList<Double>();
				List<Double> negativePrecisionSingleRun = new ArrayList<Double>();
				List<Double> negativeFMeasureSingleRun = new ArrayList<Double>();
				List<Double> microFMeasureSingleRun = new ArrayList<Double>();
				List<Double> microPosNegFMeasureSingleRun = new ArrayList<Double>();
				List<Double> macroFMeasureSingleRun = new ArrayList<Double>();
				List<Double> macroPosNegFMeasureSingleRun = new ArrayList<Double>();
				List<Double> averageAccuracySingleRun = new ArrayList<Double>();
				List<Double> errorRateSingleRun = new ArrayList<Double>();
				List<Double> otherFMeasureSingleRun = new ArrayList<Double>();
				List<Double> accuracySingleRun = new ArrayList<Double>();

				if (currentIteration >= 0) {
					trainingSize.add(trainingSizeSingleRun);
					testSize.add(testSizeSingleRun);
					cpuTime.add(cpuTimeSingleRun);

					recall.add(recallSingleRun);
					macroAvgRecall.add(macroAvgRecallSingleRun);
					macroAvgPosNegRecall.add(macroAvgPosNegRecallSingleRun);
					precision.add(precisionSingleRun);
					macroAvgPrecision.add(macroAvgPrecisionSingleRun);
					macroAvgPosNegPrecision.add(macroAvgPosNegPrecisionSingleRun);

					positiveRecall.add(positiveRecallSingleRun);
					positivePrecision.add(positivePrecisionSingleRun);
					positiveFMeasure.add(positiveFMeasureSingleRun);

					neutralRecall.add(neutralRecallSingleRun);
					neutralPrecision.add(neutralPrecisionSingleRun);
					neutralFMeasure.add(neutralFMeasureSingleRun);

					negativeRecall.add(negativeRecallSingleRun);
					negativePrecision.add(negativePrecisionSingleRun);
					negativeFMeasure.add(negativeFMeasureSingleRun);

					microFMeasure.add(microFMeasureSingleRun);
					microPosNegFMeasure.add(microPosNegFMeasureSingleRun);
					macroFMeasure.add(macroFMeasureSingleRun);
					macroPosNegFMeasure.add(macroPosNegFMeasureSingleRun);

					averageAccuracy.add(averageAccuracySingleRun);
					errorRate.add(errorRateSingleRun);
					
					otherFMeasure.add(otherFMeasureSingleRun);
					accuracy.add(accuracySingleRun);
				}
				
				List<List<List<MyTuple>>> slices = getSlices(sliceGenerator, startTrainingSetSize, stepSize,
						startTestSize);

				// fix training and test set
//				List<List<List<MyTuple>>> slices = getFixTrainAndTest();

				Iterator<List<List<MyTuple>>> iter = slices.iterator();
				while (iter.hasNext()) {
					System.err.println("ITERATION: " + currentIteration);
					List<List<MyTuple>> slice = iter.next();
					SVMPreparation.prepareSlice(slice.get(0), slice.get(1));

					if (!withPOS) {
						NoPOSFeatureVectorGenerator noPOSFVG = NoPOSFVGSelector.selectFVG(
								dataset.getTrainTweets(false, true), NoPOSCombinedFeatureVectorGenerator.class);
						pipelineBox = new NoPOSSVMBox(dataset, noPOSFVG, nFold, false);
					} else {
						FeatureVectorGenerator posFVG = FVGSelector.selectFVG(dataset.getTrainTweets(false, true),
								CombinedFeatureVectorGenerator.class);
						pipelineBox = new POSSVMBox(dataset, posFVG, nFold, false);
					}

					pipelineBox.setName("PipeLine-Box");

					pipelineBox.getPredictor().getPredictionStatistic().startNewStopWatch();
					pipelineBox.predict(dataset.getTestTweets(true));
					pipelineBox.getPredictor().getPredictionStatistic().elapsedTime();

//					System.out.println(
//							"Empty FV: " + pipelineBox.getPredictor().getPredictionStatistic().getCountEmptyFV());
//					System.out.println("CM: " + Arrays
//							.deepToString(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getCM()));

					if (currentIteration >= 0) {
						trainingSizeSingleRun.add((double) dataset.getTrainTweets(false, true).size());
						testSizeSingleRun.add((double) dataset.getTestTweets(true).size());
						cpuTimeSingleRun.add(pipelineBox.getPredictor().getPredictionStatistic().getSumElapsedTime());

						recallSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMicroOverallRecall());
						macroAvgRecallSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMacroOverallRecalls());
						macroAvgPosNegRecallSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMacroPosNegRecalls());
						precisionSingleRun.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS()
								.getMicroOverallPrecision());
						macroAvgPrecisionSingleRun.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS()
								.getMacroOverallPrecision());
						macroAvgPosNegPrecisionSingleRun.add(pipelineBox.getPredictor().getPredictionStatistic()
								.getCMS().getMacroPosNegPrecisions());

						positiveRecallSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getRecallPositive());
						positivePrecisionSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getPrecisionPositive());
						positiveFMeasureSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getPositiveFMeasure());

						neutralRecallSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getRecallNeutral());
						neutralPrecisionSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getPrecisionNeutral());
						neutralFMeasureSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getNeutralFMeasure());

						negativeRecallSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getRecallNegative());
						negativePrecisionSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getPrecisionNegative());
						negativeFMeasureSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getNegativeFMeasure());

						microFMeasureSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMicroFMeasure());
						microPosNegFMeasureSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMicroPosNegFMeasure());
						macroFMeasureSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMacroFMeasure());
						macroPosNegFMeasureSingleRun.add(
								pipelineBox.getPredictor().getPredictionStatistic().getCMS().getMacroPosNegFMeasure());

						averageAccuracySingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getAverageAccuracy());
						errorRateSingleRun
								.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getErrorRate());

						otherFMeasureSingleRun.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getOtherPosNegFMeasure());
						accuracySingleRun.add(pipelineBox.getPredictor().getPredictionStatistic().getCMS().getAccuracy());
						
						activateDebugOutput(false, iter.hasNext(), currentIteration, pipelineBox);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (shutdown && pipelineBox != null) {
				pipelineBox.shutdown();
			}
		}

		statistics.put("recall", recall);
		statistics.put("macro-avg. recall", macroAvgRecall);
		statistics.put("macro-avg. Pos/Neg recall", macroAvgPosNegRecall);
		statistics.put("precision", precision);
		statistics.put("macro-avg. precision", macroAvgPrecision);
		statistics.put("macro-avg. Pos/Neg precision", macroAvgPosNegPrecision);
		statistics.put("positiveRecall", positiveRecall);
		statistics.put("positivePrecision", positivePrecision);
		statistics.put("positive fmeasure", positiveFMeasure);
		statistics.put("neutralRecall", neutralRecall);
		statistics.put("neutralPrecision", neutralPrecision);
		statistics.put("neutral fmeasure", neutralFMeasure);
		statistics.put("negativeRecall", negativeRecall);
		statistics.put("negativePrecision", negativePrecision);
		statistics.put("negative fmeasure", negativeFMeasure);
		statistics.put("micro f-Measure", microFMeasure);
		statistics.put("micro pos/neg f-Measure", microPosNegFMeasure);
		statistics.put("macro f-Measure", macroFMeasure);
		statistics.put("macro pos/neg f-Measure", macroPosNegFMeasure);
		statistics.put("other pos/neg f-Measure", otherFMeasure);
		statistics.put("accuracy", accuracy);
		statistics.put("average accuracy", averageAccuracy);
		statistics.put("error rate", errorRate);
		statistics.put("cpu-time (for complete test-set)", cpuTime);
		statistics.put("training-size", trainingSize);
		statistics.put("test-size", testSize);

		printDynamicSlicesResults(iterations, sliceGenerator, statistics, startTrainingSetSize, stepSize,
				startTestSize);
	}

	public static void evaluateDynamicSlicesWeka(Dataset dataset, boolean withPOS, int iterations, int sliceGenerator,
			int startTrainingSetSize, int stepSize, int testSize) throws IOException {

		List<List<List<List<MyEvaluation>>>> complete = new ArrayList<List<List<List<MyEvaluation>>>>();
		try {
			for (int currentIteration = 0; currentIteration < iterations; currentIteration++) {
				System.err.println("ITERATION: " + currentIteration);

				List<List<List<MyEvaluation>>> singleRun = new ArrayList<List<List<MyEvaluation>>>();
				if (currentIteration >= 0) {
					complete.add(singleRun);
				}

				List<List<List<MyTuple>>> slices = getSlices(sliceGenerator, startTrainingSetSize, stepSize, testSize);

				// Fix training and test set TODO
//				List<List<List<MyTuple>>> slices = getFixTrainAndTest();

				Iterator<List<List<MyTuple>>> iter = slices.iterator();
				while (iter.hasNext()) {
					System.err.println("ITERATION: " + currentIteration);
					List<List<MyTuple>> slice = iter.next();
					SVMPreparation.prepareSlice(slice.get(0), slice.get(1));

					if (!withPOS) {
						NoPOSFeatureVectorGenerator noPOSFVG = NoPOSFVGSelector.selectFVG(
								dataset.getTrainTweets(false, true), NoPOSCombinedFeatureVectorGenerator.class);
						ARFFTrainer.generateNoPOSARFF(SVMPreparation.TRAINING_TSV, ARFFTrainer.TRAINING_PATH, noPOSFVG);
						ARFFTrainer.generateNoPOSARFF(SVMPreparation.TEST_TSV, ARFFTrainer.TEST_PATH, noPOSFVG);
					} else {
						FeatureVectorGenerator posFVG = FVGSelector.selectFVG(dataset.getTrainTweets(false, true),
								CombinedFeatureVectorGenerator.class);
						ARFFTrainer.generatePOSARFF(SVMPreparation.TRAINING_TSV, ARFFTrainer.TRAINING_PATH, posFVG);
						ARFFTrainer.generatePOSARFF(SVMPreparation.TEST_TSV, ARFFTrainer.TEST_PATH, posFVG);
					}

					WekaEvaluator weka = new WekaEvaluator(ARFFTrainer.TRAINING_PATH, ARFFTrainer.TEST_PATH);
					List<MyClassifier> classifiers = createClassifiers();
					List<List<MyEvaluation>> evaluations = weka.evaluateAll(classifiers);

					if (currentIteration >= 0) {
						singleRun.add(evaluations);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		printWekaCompleteResults(complete);
	}

	public static void main(String[] args) throws IOException {
		Dataset dataset = Configuration.getDataSetTwitch();
		// Dataset dataset = Configuration.getDataSetMyTest();

		boolean parameterSearch = false;
		boolean useSerialization = true;
		int nFoldCrossValidation = 1;
		int featureVectorLevel = 2;
		int iterations = 100;
		boolean withPOS = true;
		
		// evaluateBoxesPipeline(dataset, iterations, nFoldCrossValidation);

		
		int addVsTest = 7;

		//these have no real functionality atm
		List<Integer> startTrainingSizeList = new ArrayList<Integer>();
		List<Integer> stepList = new ArrayList<Integer>();
		List<Integer> testSizeList = new ArrayList<Integer>();

		startTrainingSizeList.add(200);
		stepList.add(200);
		testSizeList.add(300);

		
		for (int j = 0; j < startTrainingSizeList.size(); j++) {
			// SVM
			evaluateDynamicSlices(dataset, withPOS, iterations, nFoldCrossValidation, false, addVsTest,
					startTrainingSizeList.get(j), stepList.get(j), testSizeList.get(j));

			// Weka
//			evaluateDynamicSlicesWeka(dataset, false, iterations, addVsTest, startTrainingSizeList.get(j),
//					stepList.get(j), testSizeList.get(j));
		}
		svm.EXEC_SERV.shutdown();

//		if (featureVectorLevel == 0) {
//			SVM.svm(dataset, SentimentFeatureVectorGenerator.class, nFoldCrossValidation, parameterSearch,
//					useSerialization);
//		} else if (featureVectorLevel == 1) {
//			SVM.svm(dataset, TfIdfFeatureVectorGenerator.class, nFoldCrossValidation, parameterSearch,
//					useSerialization);
//		} else {
//			SVM.svm(dataset, CombinedFeatureVectorGenerator.class, nFoldCrossValidation, parameterSearch,
//					useSerialization);
//		}

	}

	private static List<List<List<MyTuple>>> getSlices(int sliceGenerator, int startTrainingSetSize, int stepSize,
			int startTestSize) {
		List<List<List<MyTuple>>> slices = null;
		// ADDITION - 709 only
		if (sliceGenerator == 0) {
			slices = SVMPreparation.prepareAdditionVsRestRun(startTrainingSetSize, stepSize);
		} else if (sliceGenerator == 1) {
			slices = SVMPreparation.prepareAdditionVsRestSubsetRun(startTrainingSetSize, stepSize);
		} else if (sliceGenerator == 2) {
			slices = SVMPreparation.prepareAdditionVsTestRun(startTrainingSetSize, stepSize, startTestSize);
		}
		// RANDOM - 709 only
		else if (sliceGenerator == 3) {
			slices = SVMPreparation.prepareRandomVsRestRun(startTrainingSetSize, stepSize);
		} else if (sliceGenerator == 4) {
			slices = SVMPreparation.prepareRandomVsRestSubsetRun(startTrainingSetSize, stepSize);
		} else if (sliceGenerator == 5) {
			slices = SVMPreparation.prepareRandomVsTestRun(startTrainingSetSize, stepSize, startTestSize);
		}
		// EQUALLY DISTRIBUTED TEST-SENTIMENTS: TEST AND TRAINING FROM
		// DIFFERENT FILES
		else if (sliceGenerator == 6) {
			slices = SVMPreparation.prepareAdditionVsEquallyDistibutedTestRun(100, 50, 300,
					SVMPreparation.UNIQUE_MESSAGES_ORIGINAL,
					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_POSITIVE,
					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEUTRAL,
					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEGATIVE, false);
		}
		// EQUALLY DISTRIBUTED TEST-SENTIMENTS: TEST AND TRAINING FROM
		// SAME FILE
		else if (sliceGenerator == 7) {
			// ALL SELF LABELED DATA USED
//			slices = SVMPreparation.prepareAdditionVsEquallyDistibutedTestRun(200, 200, 300,
//					SVMPreparation.UNIQUE_MESSAGES_SELF_LABELING_AND_LENN,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_POSITIVE,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEUTRAL,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEGATIVE);

			// EQUALLY DISTRIBUTED TRAININGSDATA - 200/200/300
//			slices = SVMPreparation.prepareAdditionVsEquallyDistibutedTestAndTrainingRun(5000, 200, 300,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_POSITIVE,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEUTRAL,
//					SVMPreparation.SEPARATE_MESSAGES_SELF_AND_LENN_LABELING_NEGATIVE);

			
			
			// ALL DATA EVER 500/500/300
//			slices = SVMPreparation.prepareAdditionVsEquallyDistibutedTestRun(2742, 300, 300,
//					SVMPreparation.UNIQUE_MESSAGES_ALL, SVMPreparation.SEPARATE_MESSAGES_ALL_POSITIVE,
//					SVMPreparation.SEPARATE_MESSAGES_ALL_NEUTRAL, SVMPreparation.SEPARATE_MESSAGES_ALL_NEGATIVE);

			// EQUALLY DISTRIBUTED TRAININGSDATA - 300/300/300
//			slices = SVMPreparation.prepareAdditionVsEquallyDistibutedTestAndTrainingRun(2742, 300, 300,
//					SVMPreparation.SEPARATE_MESSAGES_ALL_POSITIVE, SVMPreparation.SEPARATE_MESSAGES_ALL_NEUTRAL,
//					SVMPreparation.SEPARATE_MESSAGES_ALL_NEGATIVE);
			
			
//			slices = SVMPreparation.prepareAdditionVsTestRun(509, 300, 200, SVMPreparation.UNIQUE_MESSAGES_ORIGINAL);
			slices = SVMPreparation.prepareAdditionVsTestRun(2442, 300, 600, SVMPreparation.UNIQUE_MESSAGES_ALL);
		}
		return slices;
	}

	private static List<MyClassifier> createClassifiers() {
		return WekaEvaluator.createClassifiers();
	}

	private static void printWekaCompleteResults(List<List<List<List<MyEvaluation>>>> complete) {
		WekaEvaluator.printWekaCompleteResults(complete);
	}

	private static void printDynamicSlicesResults(int iterations, int sliceGenerator,
			Map<String, List<List<Double>>> statistics, int startTrainingSize, int stepSize, int testSize) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < iterations; i++) {
			sb.append("RUN: " + (i + 1));
			sb.append("\n");
			for (Entry<String, List<List<Double>>> entry : statistics.entrySet()) {
				List<Double> singleRun = entry.getValue().get(i);
				sb.append(entry.getKey());
				sb.append("\t");
				for (Double value : singleRun) {
					sb.append(String.valueOf(value).replace(".", ","));
					sb.append("\t");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		EvaluationUtil.generateTSV("src/main/evaluation/successive_addition_evaluation/all_runs_results_"
				+ sliceGenerator + "_" + startTrainingSize + "_" + stepSize + "_" + testSize + ".tsv", sb.toString());

		// for smaug
//		EvaluationUtil.generateTSV("/home/stud/lechner/" + "all_runs_results_" + sliceGenerator + "_" + startTrainingSize + "_" + stepSize + "_"
//				+ testSize + ".tsv", sb.toString());

		StandardDeviation mathStdDev = new StandardDeviation();
		Map<String, List<Double>> results = new LinkedHashMap<String, List<Double>>();
		int lengthOfSingleRun = statistics.get("recall").get(0).size();

		for (Entry<String, List<List<Double>>> entry : statistics.entrySet()) {
			List<Double> avgOfSteps = new ArrayList<Double>();
			List<Double> stdDevOfSteps = new ArrayList<Double>();
			for (int i = 0; i < lengthOfSingleRun; i++) {
				double sum = 0.0;
				double[] stdDevValues = new double[iterations];
				int countNotAvailable = 0;
				for (int j = 0; j < iterations; j++) {
					if (entry.getValue().get(j).get(i) < 0) {
						countNotAvailable++;
					} else {
						sum += entry.getValue().get(j).get(i);
						stdDevValues[j] = entry.getValue().get(j).get(i);
					}
				}
				if (iterations - countNotAvailable == 0) {
					avgOfSteps.add(0.0);
					stdDevOfSteps.add(0.0);
				} else {
					avgOfSteps.add(sum / (iterations - countNotAvailable));
					stdDevOfSteps.add(mathStdDev.evaluate(stdDevValues));
				}
			}
			results.put(entry.getKey(), avgOfSteps);
			results.put(entry.getKey() + " StdDev", stdDevOfSteps);
		}
		sb = new StringBuilder();
		for (Entry<String, List<Double>> entry : results.entrySet()) {
			sb.append(entry.getKey());
			sb.append("\t");
			for (Double value : entry.getValue()) {
				sb.append(String.valueOf(value).replace(".", ","));
				sb.append("\t");
			}
			sb.append("\n");
		}
		EvaluationUtil.generateTSV("src/main/evaluation/successive_addition_evaluation/averaged_results_"
				+ sliceGenerator + "_" + startTrainingSize + "_" + stepSize + "_" + testSize + ".tsv", sb.toString());

		// for smaug
//		EvaluationUtil.generateTSV("/home/stud/lechner/" + "averaged_results_" + sliceGenerator + "_" + startTrainingSize + "_" + stepSize + "_"
//				+ testSize + ".tsv", sb.toString());
	}

	private static void printPipelineResults(int iterations, Map<String, Double[]> statistics) {
		List<double[]> statisticList = new ArrayList<double[]>();

		String pipelineResult = "";
		int j = 0;
		for (Entry<String, Double[]> entry : statistics.entrySet()) {
			double[] values = new double[iterations];
			for (int i = 0; i < iterations; i++) {
				values[i] = entry.getValue()[i];
			}
			statisticList.add(values);
			pipelineResult += PipelineEvaluation.calcEvaluationStatistics(values, false);
			if (j == 2 || j == 4 || j == 6) {
				pipelineResult += "\n";
			}
			j++;
		}
		EvaluationUtil.generateTSV("src/main/evaluation/pipeline_result.tsv", pipelineResult);

		String pipelineAnalysis = PipelineEvaluation.createPipelineAnalysis(iterations, statisticList);
		EvaluationUtil.generateTSV("src/main/evaluation/pipeline_analyse.tsv", pipelineAnalysis);
	}

	private static void activateDebugOutput(boolean active, boolean hasNext, int currentIteration, SVMBox pipelineBox) {
		if (active) {
			if (!hasNext) {
				writeWrongPredictedMessages(currentIteration, pipelineBox.getPredictor().getPredictionStatistic());
				writeNoFeatureVectorMessages(2, pipelineBox.getPredictor().getPredictionStatistic());
			}
		}
	}

	private static void writeWrongPredictedMessages(int iteration, PredictionStatistic predictionStatistic) {
		String outputPath = "src/main/evaluation/WrongPredictedMessages_" + iteration + ".tsv";
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			List<List<String>> wrongPredictedMessages = predictionStatistic.getWrongPredictedMessages();

			writer.write("ACTUAL-CLASS" + "\t" + "PREDICTED-CLASS" + "\t" + "TEXT" + "\t" + "FEATURE-VECTOR" + "\n");
			// writer.write("TEXT" + "\t" +"ACTUAL-CLASS" + "\t" +
			// "PREDICTED-CLASS" + "\t" + "FEATURE-VECTOR" + "\n");

			for (int i = 0; i < wrongPredictedMessages.size(); i++) {
				String actual = "";
				String predic = "";
				String message = "";
				String fv = "";
				for (int j = 0; j < wrongPredictedMessages.get(i).size(); j++) {

					SentimentClass sentiment = null;
					int value = -1;
					if (j == 1) {
						value = Integer.parseInt(wrongPredictedMessages.get(i).get(j));
					}
					if (j == 2) {
						double predicted = Double.valueOf(wrongPredictedMessages.get(i).get(j));
						value = (int) predicted;
					}
					if (j > 0 && j < 3) {
						if (value == 0) {
							sentiment = SentimentClass.NEGATIVE;
						}
						if (value == 1) {
							sentiment = SentimentClass.NEUTRAL;
						}
						if (value == 2) {
							sentiment = SentimentClass.POSITIVE;
						}
					}

					if (j == 0 || j == 3) {
						if (j == 0) {
							message = wrongPredictedMessages.get(i).get(j) + "\t";
						} else if (j == 3) {
							fv = wrongPredictedMessages.get(i).get(j) + "\t";
						}
						// writer.write(wrongPredictedMessages.get(i).get(j) +
						// "\t");
					} else {
						if (j == 1) {
							actual = sentiment + "\t";
						} else if (j == 2) {
							predic = sentiment + "\t";
						}
						// writer.write(sentiment + "\t");
					}
				}
				writer.write(actual + predic + message + fv);
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeNoFeatureVectorMessages(int iteration, PredictionStatistic predictionStatistic) {
		String outputPath = "src/main/evaluation/NoFeatureVectorMessages_" + iteration + ".tsv";
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			List<FeaturedTweet> featuredTweets = predictionStatistic.getNoFeatureVectorMessages();
			for (int i = 0; i < featuredTweets.size(); i++) {

				SentimentClass sentiment = null;
				if (featuredTweets.get(i).getScore() == 0) {
					sentiment = SentimentClass.NEGATIVE;
				}
				if (featuredTweets.get(i).getScore() == 1) {
					sentiment = SentimentClass.NEUTRAL;
				}
				if (featuredTweets.get(i).getScore() == 2) {
					sentiment = SentimentClass.POSITIVE;
				}

				writer.write(featuredTweets.get(i).getText() + "\t" + sentiment);
				writer.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static List<List<List<MyTuple>>> getFixTrainAndTest() {
		MyTuple[] training = SVMPreparation.getMyTuples(SVMPreparation.FIX_TESTING_SET_TRAINDATA);
		MyTuple[] test = SVMPreparation.getMyTuples(SVMPreparation.FIX_TESTING_SET_TESTDATA);

		List<MyTuple> trainingList = new ArrayList<MyTuple>(Arrays.asList(training));
		List<MyTuple> testList = new ArrayList<MyTuple>(Arrays.asList(test));

		List<List<MyTuple>> completeSlice = new ArrayList<List<MyTuple>>();
		completeSlice.add(trainingList);
		completeSlice.add(testList);

		List<List<List<MyTuple>>> slices = new ArrayList<List<List<MyTuple>>>();
		slices.add(completeSlice);

		return slices;
	}

}
