package at.lechner.preparation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import at.lechner.commons.MyTupel;
import at.lechner.commons.Sentiment;
import at.lechner.util.BasicUtil;

public class SVMPreparation implements PreparationTool {

	private static final String UNIQUE_MESSAGES_ALL = "src/main/resources/preparation/complete_result.tsv";
	private static final String UNIQUE_MESSAGES_ORIGINAL = "src/main/resources/preparation/original-labeling/unique-messages.txt";
	private static final String UNIQUE_MESSAGES_SELF_LABELING = "src/main/resources/preparation/self-labeling/complete_self_labeling_and_lenn.txt";

	private static final String SEPARATE_MESSAGES_POSITIVE = "src/main/resources/preparation/svm/unique-messages-positive.txt";
	private static final String SEPARATE_MESSAGES_NEUTRAL = "src/main/resources/preparation/svm/unique-messages-neutral.txt";
	private static final String SEPARATE_MESSAGES_NEGATIVE = "src/main/resources/preparation/svm/unique-messages-negative.txt";

	private static final String SEPARATE_MESSAGES_SELF_LABELING_POSITIVE = "src/main/resources/preparation/self-labeling/separated-classes/positives.txt";
	private static final String SEPARATE_MESSAGES_SELF_LABELING_NEUTRAL = "src/main/resources/preparation/self-labeling/separated-classes/neutrals.txt";
	private static final String SEPARATE_MESSAGES_SELF_LABELING_NEGATIVE = "src/main/resources/preparation/self-labeling/separated-classes/negatives.txt";

	private static final int FIX_TRAIN_SIZE = 100;

	private static final String TEST_TSV = "src/main/resources/datasets/Twitch/twitch-test.tsv";
	private static final String TRAINING_TSV = "src/main/resources/datasets/Twitch/twitch-training.tsv";

	private static final String TEST_TEST_TSV = "src/main/resources/preparation/self-labeling/separated-classes/test.tsv";
	private static final String TEST_TRAINING_TSV = "src/main/resources/preparation/self-labeling/separated-classes/training.tsv";

	public static void main(String[] args) throws IOException {
		// System.out.println("PREPARATION SVMTRAINING START");
		// createTestAndTrainingTSV(SEPARATE_MESSAGES_POSITIVE,
		// SEPARATE_MESSAGES_NEUTRAL, SEPARATE_MESSAGES_NEGATIVE,
		// TEST_TSV, TRAINING_TSV);
		// System.out.println("PREPARATION SVMTRAINING STOP");

		// separateDataBySentiment(UNIQUE_MESSAGES_SELF_LABELING,
		// SEPARATE_MESSAGES_SELF_LABELING_POSITIVE,
		// SEPARATE_MESSAGES_SELF_LABELING_NEUTRAL,
		// SEPARATE_MESSAGES_SELF_LABELING_NEGATIVE);

		// createTestAndTrainingTSV(SEPARATE_MESSAGES_SELF_LABELING_POSITIVE,
		// SEPARATE_MESSAGES_SELF_LABELING_NEUTRAL,
		// SEPARATE_MESSAGES_SELF_LABELING_NEGATIVE, TEST_TEST_TSV,
		// TEST_TRAINING_TSV);

		separateMergedLabelSessions(UNIQUE_MESSAGES_ALL, UNIQUE_MESSAGES_ORIGINAL, UNIQUE_MESSAGES_SELF_LABELING);
	}

	public static void separateMergedLabelSessions(String allPath, String toRemovePath, String resultPath) {
		Set<String> originalMessages = new HashSet<String>();
		List<MyTupel> result = new ArrayList<MyTupel>();
		List<MyTupel> tmp = new ArrayList<MyTupel>();
		MyTupel[] original = getMyTuples(toRemovePath);
		MyTupel[] all = getMyTuples(allPath);

		for (MyTupel tuple : original) {
			originalMessages.add(tuple.getText());
		}
		for (MyTupel tuple : all) {
			if (!originalMessages.contains(tuple.getText())) {
				tmp.add(tuple);
			}
		}
		for(int i = 0; i < tmp.size(); i++) {
			MyTupel current = tmp.get(i);
			result.add(new MyTupel(i+1, current.getText(), current.getSentiment().toString()));
		}
		print(resultPath, result);
	}

	private static void createTestAndTrainingTSV(String positivePath, String neutralPath, String negativePath,
			String testPath, String trainingPath) throws IOException {
		String[] posLines = BasicUtil.readLines(positivePath);
		String[] neuLines = BasicUtil.readLines(neutralPath);
		String[] negLines = BasicUtil.readLines(negativePath);

		HashMap<String, Sentiment> testMsgs = new HashMap<String, Sentiment>();
		HashMap<String, Sentiment> trainingMsgs = new HashMap<String, Sentiment>();

		// set number for test data
		addToLists(testMsgs, trainingMsgs, posLines);
		addToLists(testMsgs, trainingMsgs, neuLines);
		addToLists(testMsgs, trainingMsgs, negLines);

		// set number for training data
		// addToLists(trainingMsgs, testMsgs, posLines);
		// addToLists(trainingMsgs, testMsgs, neuLines);
		// addToLists(trainingMsgs, testMsgs, negLines);

		createTSV(testMsgs, testPath);
		createTSV(trainingMsgs, trainingPath);
	}
	
	public static void print(String toPath, List<MyTupel> certain) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < certain.size(); i++) {
			sb.append(certain.get(i).getId());
			sb.append("\t");
			sb.append(certain.get(i).getSentiment());
			sb.append("\t");
			sb.append(certain.get(i).getText());
			sb.append("\n");
		}
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(toPath), "utf-8"))) {
			writer.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static MyTupel[] getMyTuples(String filePath) {
		String[] lines = BasicUtil.readLines(filePath);
		return getMyTuples(lines);
	}

	private static MyTupel[] getMyTuples(String[] lines) {
		List<MyTupel> tupels = new ArrayList<MyTupel>();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.trim().length() == 0) {
				continue;
			}
			String[] parts = line.split("\t");
			String msg = "";
			for (int j = 2; j < parts.length; j++) {
				msg += parts[j];
				if (j < parts.length - 1) {
					msg += "\t";
				}
			}
			MyTupel tuple = new MyTupel(Integer.valueOf(parts[0]), msg, parts[1]);
			tupels.add(tuple);
		}
		return tupels.toArray(new MyTupel[tupels.size()]);
	}

	private static void addToLists(HashMap<String, Sentiment> containsMsgs, HashMap<String, Sentiment> containsMsgsNot,
			String[] lines) {
		int number = FIX_TRAIN_SIZE;
		int[] randoms = getRandoms(number, lines.length);

		for (int i = 0; i < lines.length; i++) {
			String[] splitLine = BasicUtil.splitLine(lines[i], "\t");
			if (containsInt(randoms, i)) {
				containsMsgs.put(splitLine[2], Sentiment.getSentiment(splitLine[1]));
			} else {
				containsMsgsNot.put(splitLine[2], Sentiment.getSentiment(splitLine[1]));
			}
		}
	}

	public static void separateDataBySentiment(String inputPath, String positivePath, String neutralPath,
			String negativePath) throws IOException {
		String[] lines = BasicUtil.readLines(inputPath);
		List<String> positiveSentiment = new ArrayList<String>();
		List<String> neutralSentiment = new ArrayList<String>();
		List<String> negativeSentiment = new ArrayList<String>();
		for (int i = 0; i < lines.length; i++) {
			String[] splitedLine = BasicUtil.splitLine(lines[i], "\t");
			Sentiment sentimentLine = Sentiment.getSentiment(splitedLine[1]);
			if (sentimentLine == Sentiment.POSITIVE) {
				positiveSentiment.add(splitedLine[2]);
			} else if (sentimentLine == Sentiment.NEUTRAL) {
				neutralSentiment.add(splitedLine[2]);
			} else if (sentimentLine == Sentiment.NEGATIVE) {
				negativeSentiment.add(splitedLine[2]);
			}
		}

		createTSV(positiveSentiment, Sentiment.POSITIVE, positivePath);
		createTSV(neutralSentiment, Sentiment.NEUTRAL, neutralPath);
		createTSV(negativeSentiment, Sentiment.NEGATIVE, negativePath);
	}

	public static void extractMessagesOnce(String inputPath, String outputPath, boolean withMixed) throws IOException {
		String[] lines = BasicUtil.readLines(inputPath);
		MyTupel[] twitchTupels = BasicUtil.extractTwitchLabeling(lines, withMixed);
		HashMap<String, Sentiment> messages = new HashMap<String, Sentiment>();
		for (int i = 0; i < twitchTupels.length; i++) {
			MyTupel curTwitchTupel = twitchTupels[i];
			if (messages.get(curTwitchTupel.getText()) == null) {
				messages.put(curTwitchTupel.getText(), curTwitchTupel.getSentiment());
			}
		}
		createTSV(messages, outputPath);
	}

	private static void createTSV(HashMap<String, Sentiment> messages, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			int i = 1;
			for (Entry<String, Sentiment> entry : messages.entrySet()) {
				writer.write(i + "\t" + entry.getValue() + "\t" + entry.getKey() + "\n");
				i++;
			}
		}
	}

	private static void createTSV(List<String> messages, Sentiment sentiment, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			int i = 1;
			for (String message : messages) {
				writer.write(i + "\t" + sentiment + "\t" + message + "\n");
				i++;
			}
		}
	}

	private static void createTSV(String text, String outputPath) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath), "utf-8"))) {
			writer.write(text);
		}
	}

	public static void prepareForCrossValidation() throws IOException {
		deleteTwitchDataset();
		createTestAndTrainingTSV(SEPARATE_MESSAGES_POSITIVE, SEPARATE_MESSAGES_NEUTRAL, SEPARATE_MESSAGES_NEGATIVE,
				TEST_TSV, TRAINING_TSV);
	}

	private static void deleteTwitchDataset() {
		File svmModel = new File("src/main/resources/datasets/Twitch/svm_model.ser");
		File svmProblem = new File("src/main/resources/datasets/Twitch/svm_problem.txt");
		File testTSV = new File("src/main/resources/datasets/Twitch/twitch-test.tsv");
		File testTSVSer = new File("src/main/resources/datasets/Twitch/twitch-test.tsv.ser");
		File trainingTSV = new File("src/main/resources/datasets/Twitch/twitch-training.tsv");
		File trainingTSVSer = new File("src/main/resources/datasets/Twitch/twitch-training.tsv.ser");

		List<File> files = new ArrayList<File>();

		files.add(svmModel);
		files.add(svmProblem);
		files.add(testTSV);
		files.add(testTSVSer);
		files.add(trainingTSV);
		files.add(trainingTSVSer);

		for (File f : files) {
			if (f.exists()) {
				f.delete();
			} else {
				System.err.println("FILE NOT FOUND TO DELETE: " + f.getName());
			}
		}
	}

	private static int[] getRandoms(int countRandomNum, int maxLength) {
		List<Integer> indexList = new ArrayList<Integer>();
		List<Integer> randomIndices = new ArrayList<Integer>();
		int[] resultArray = new int[countRandomNum];

		if (countRandomNum > maxLength) {
			System.out.println("ERROR. getRandoms.");
			return null;
		}
		for (int i = 0; i < maxLength; i++) {
			indexList.add(i);
		}

		Random r = new Random();
		while (randomIndices.size() != countRandomNum) {
			int msgsIndex = Math.abs(r.nextInt(maxLength));
			randomIndices.add(indexList.get(msgsIndex));
			indexList.remove(msgsIndex);
			maxLength = maxLength - 1;
		}
		for (int i = 0; i < randomIndices.size(); i++) {
			resultArray[i] = randomIndices.get(i);
		}
		return resultArray;
	}

	private static boolean containsInt(int[] intArr, int value) {
		for (int i = 0; i < intArr.length; i++) {
			if (intArr[i] == value) {
				return true;
			}
		}
		return false;
	}

	public static void checkIfSame(String in1, String in2) throws IOException {
		String[] linesIn1 = BasicUtil.readLines(in1);
		String[] linesIn2 = BasicUtil.readLines(in2);

		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();

		for (int i = 0; i < linesIn1.length; i++) {
			String[] splitLine = linesIn1[i].split("\t");
			set1.add(splitLine[2]);
		}

		for (int i = 0; i < linesIn2.length; i++) {
			String[] splitLine = linesIn2[i].split("\t");
			set2.add(splitLine[2]);
		}

		int countContains = 0;
		int countContainsNot = 0;

		for (String s : set1) {
			if (set2.contains(s)) {
				countContains++;
			} else {
				countContainsNot++;
			}
		}

		System.out.println("COUNT CONTAIN    : " + countContains);
		System.out.println("COUNT CONTAIN NOT: " + countContainsNot);
	}

	public static MyTupel[] createMyTupelsFromFile(String pathToFile) {
		MyTupel[] tupels = null;
		String[] allMessagesLines = BasicUtil.readLines(pathToFile);
		tupels = new MyTupel[allMessagesLines.length];
		for (int i = 0; i < allMessagesLines.length; i++) {
			String[] splittedLine = allMessagesLines[i].split("\t");
			tupels[i] = new MyTupel(Integer.valueOf(splittedLine[0]), splittedLine[2], splittedLine[1]);
		}
		return tupels;
	}

	public static List<List<List<MyTupel>>> prepareAdditionVsRestSubsetRun(int startTrainingSetSize,
			int testSizeAndTrainingStepSize) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (startTrainingSetSize + testSizeAndTrainingStepSize > tupels.length) {
			return null;
		}
		int randomIndex = 0;
		int[] randoms = getRandoms(tupels.length, tupels.length);

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = new ArrayList<MyTupel>();
		List<MyTupel> testSet = null;

		while (randomIndex < randoms.length) {
			slice = new ArrayList<List<MyTupel>>();
			if (trainingSet.size() == 0) {
				for (int i = 0; i < startTrainingSetSize; i++) {
					trainingSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
				}
			} else {
				trainingSet = new ArrayList<>(trainingSet);
				trainingSet.addAll(testSet);
			}
			testSet = new ArrayList<MyTupel>();
			for (int i = 0; i < testSizeAndTrainingStepSize && randomIndex < randoms.length; i++) {
				testSet.add(tupels[randoms[randomIndex]]);
				randomIndex++;
			}
			if (randomIndex + testSizeAndTrainingStepSize > randoms.length) {
				for (int i = randomIndex; i < randoms.length; i++) {
					testSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
				}
			}
			slice.add(trainingSet);
			slice.add(testSet);
			slices.add(slice);
		}
		return slices;
	}

	public static List<List<List<MyTupel>>> prepareAdditionVsRestRun(int startTrainingSetSize, int trainingSteps) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (startTrainingSetSize + trainingSteps > tupels.length || trainingSteps <= 0 || startTrainingSetSize <= 0) {
			return null;
		}
		int randomIndex = 0;
		int[] randoms = getRandoms(tupels.length, tupels.length);

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = new ArrayList<MyTupel>();
		List<MyTupel> testSet = null;

		while (randomIndex < randoms.length - 1) {
			slice = new ArrayList<List<MyTupel>>();
			if (trainingSet.size() == 0) {
				for (int i = 0; i < startTrainingSetSize; i++) {
					trainingSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
					if (randomIndex == randoms.length) {
						return slices;
					}
				}
			} else {
				for (int i = 0; i < trainingSteps; i++) {
					trainingSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
					if (randomIndex == randoms.length) {
						return slices;
					}
				}
			}
			testSet = new ArrayList<MyTupel>();
			for (int i = randomIndex; i < tupels.length; i++) {
				testSet.add(tupels[randoms[i]]);
			}
			if (randomIndex + 2 * trainingSteps > randoms.length) {
				randomIndex = randoms.length;
			}
			List<MyTupel> newTrainingSet = new ArrayList<MyTupel>(trainingSet);
			slice.add(newTrainingSet);
			slice.add(testSet);
			slices.add(slice);
		}
		return slices;
	}

	public static List<List<List<MyTupel>>> prepareAdditionVsTestRun(int startTrainingSetSize, int trainingSteps,
			int testSize) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (testSize >= tupels.length || startTrainingSetSize + testSize > tupels.length || trainingSteps <= 0
				|| startTrainingSetSize <= 0) {
			return null;
		}
		int randomIndex = 0;
		int[] randoms = getRandoms(tupels.length, tupels.length);

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = new ArrayList<MyTupel>();
		List<MyTupel> testSet = new ArrayList<MyTupel>();

		for (int i = 0; i < testSize; i++) {
			testSet.add(tupels[randoms[randomIndex]]);
			randomIndex++;
		}

		while (randomIndex < randoms.length - 1) {
			slice = new ArrayList<List<MyTupel>>();
			if (trainingSet.size() == 0) {
				for (int i = 0; i < startTrainingSetSize; i++) {
					trainingSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
					if (randomIndex == randoms.length) {
						return slices;
					}
				}
			} else {
				for (int i = 0; i < trainingSteps; i++) {
					trainingSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
					if (randomIndex == randoms.length) {
						break;
					}
				}
			}
			List<MyTupel> newTrainingSet = new ArrayList<MyTupel>(trainingSet);
			List<MyTupel> newTestSet = new ArrayList<MyTupel>(testSet);
			slice.add(newTrainingSet);
			slice.add(newTestSet);
			slices.add(slice);
		}
		return slices;
	}

	public static List<List<List<MyTupel>>> prepareRandomVsRestSubsetRun(int startTrainingSetSize,
			int testSizeAndTrainingStepSize) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (startTrainingSetSize + testSizeAndTrainingStepSize > tupels.length) {
			return null;
		}

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = null;
		List<MyTupel> testSet = null;

		int trainingSetSize = startTrainingSetSize;
		boolean lastRun = false;
		while (!lastRun) {
			slice = new ArrayList<List<MyTupel>>();
			trainingSet = new ArrayList<MyTupel>();
			testSet = new ArrayList<MyTupel>();

			int randomIndex = 0;
			int[] randoms = getRandoms(tupels.length, tupels.length);

			for (int i = 0; i < trainingSetSize; i++) {
				trainingSet.add(tupels[randoms[randomIndex]]);
				randomIndex++;
			}
			for (int i = 0; i < testSizeAndTrainingStepSize; i++) {
				testSet.add(tupels[randoms[randomIndex]]);
				randomIndex++;
				if (randomIndex == tupels.length) {
					break;
				}
			}
			trainingSetSize += testSizeAndTrainingStepSize;
			if (trainingSetSize + testSizeAndTrainingStepSize >= tupels.length) {
				lastRun = true;
				for (int i = randomIndex; i < randoms.length; i++) {
					testSet.add(tupels[randoms[randomIndex]]);
					randomIndex++;
				}
			}

			slice.add(trainingSet);
			slice.add(testSet);
			slices.add(slice);
		}
		return slices;
	}

	public static List<List<List<MyTupel>>> prepareRandomVsRestRun(int startTrainingSetSize, int trainingSteps) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (startTrainingSetSize + trainingSteps > tupels.length) {
			return null;
		}

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = null;
		List<MyTupel> testSet = null;

		boolean lastRun = false;
		while (!lastRun) {
			slice = new ArrayList<List<MyTupel>>();

			int[] randoms = getRandoms(tupels.length, tupels.length);

			trainingSet = new ArrayList<MyTupel>();
			for (int i = 0; i < startTrainingSetSize; i++) {
				trainingSet.add(tupels[randoms[i]]);
			}
			testSet = new ArrayList<MyTupel>();
			for (int i = startTrainingSetSize; i < tupels.length; i++) {
				testSet.add(tupels[randoms[i]]);
			}
			startTrainingSetSize += trainingSteps;
			if (startTrainingSetSize + trainingSteps > randoms.length) {
				lastRun = true;
			}
			slice.add(trainingSet);
			slice.add(testSet);
			slices.add(slice);
		}
		return slices;
	}

	public static List<List<List<MyTupel>>> prepareRandomVsTestRun(int startTrainingSetSize, int trainingSteps,
			int testSize) {
		List<List<List<MyTupel>>> slices = new ArrayList<List<List<MyTupel>>>();
		MyTupel[] tupels = createMyTupelsFromFile(UNIQUE_MESSAGES_ORIGINAL);

		if (testSize >= tupels.length || startTrainingSetSize + testSize > tupels.length || trainingSteps <= 0
				|| startTrainingSetSize <= 0) {
			return null;
		}
		int randomIndex = 0;
		int[] randoms = getRandoms(tupels.length, tupels.length);

		List<List<MyTupel>> slice = null;
		List<MyTupel> trainingSet = null;
		List<MyTupel> testSet = new ArrayList<MyTupel>();

		for (int i = 0; i < testSize; i++) {
			testSet.add(tupels[randoms[randomIndex]]);
			randomIndex++;
		}

		MyTupel[] possibleTrainingTupel = new MyTupel[tupels.length - randomIndex];
		int index = 0;
		for (int i = randomIndex; i < tupels.length; i++) {
			possibleTrainingTupel[index] = tupels[randoms[i]];
			index++;
		}

		int trainingSize = startTrainingSetSize;
		boolean lastRun = false;
		while (trainingSize <= possibleTrainingTupel.length) {
			trainingSet = new ArrayList<MyTupel>();
			slice = new ArrayList<List<MyTupel>>();

			int[] trainingRandoms = getRandoms(possibleTrainingTupel.length, possibleTrainingTupel.length);
			for (int i = 0; i < trainingSize; i++) {
				trainingSet.add(possibleTrainingTupel[trainingRandoms[i]]);
			}

			List<MyTupel> newTrainingSet = new ArrayList<MyTupel>(trainingSet);
			List<MyTupel> newTestSet = new ArrayList<MyTupel>(testSet);
			slice.add(newTrainingSet);
			slice.add(newTestSet);
			slices.add(slice);

			if (lastRun) {
				return slices;
			}

			trainingSize += trainingSteps;
			if (trainingSize > tupels.length - testSize) {
				trainingSize = tupels.length - testSize;
				lastRun = true;
			}
		}
		return slices;
	}

	public static void prepareSlice(List<MyTupel> trainingSet, List<MyTupel> testSet) {
		deleteTwitchDataset();
		try {
			createTSV(buildTSVString(trainingSet), TRAINING_TSV);
			createTSV(buildTSVString(testSet), TEST_TSV);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String buildTSVString(List<MyTupel> tupel) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tupel.size(); i++) {
			sb.append(tupel.get(i).getId());
			sb.append("\t");
			sb.append(tupel.get(i).getSentiment());
			sb.append("\t");
			sb.append(tupel.get(i).getText());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public void prepare(boolean withMixed) {
		try {
			System.out.println("PREPARATION SVMTRAINING START");
			createTestAndTrainingTSV(SEPARATE_MESSAGES_POSITIVE, SEPARATE_MESSAGES_NEUTRAL, SEPARATE_MESSAGES_NEGATIVE,
					TEST_TSV, TRAINING_TSV);
			System.out.println("PREPARATION SVMTRAINING STOP");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
