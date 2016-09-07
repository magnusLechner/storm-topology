package at.lechner.labeling;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import at.lechner.commons.MyTuple;
import at.lechner.commons.Sentiment;
import at.lechner.util.BasicUtil;

public class LocalLabeling {

	public static final String UNLABELED_MESSAGES = "src/main/resources/preparation/self-labeling/toBeLabeled.txt";
	public static final String UNLABELED_ONLY_ENGLISH_MESSAGES = "src/main/resources/preparation/self-labeling/only_english_labeling.txt";
	public static final String LABELED_MESSAGES = "src/main/resources/preparation/self-labeling/labeled.txt";
	public static final String PROFILE_PATH = "/home/magnus/workspace/storm-topology/src/main/resources/language-detection/profiles/shorttext";

	private static final String CERTAIN_PATH = "src/main/resources/preparation/self-labeling/fresh_labeled_certain.txt";
	private static final String UNCERTAIN_PATH = "src/main/resources/preparation/self-labeling/uncertain.txt";
	private static final String MERGE_UNCERTAIN_PATH = "src/main/resources/preparation/self-labeling/merge_uncertain.txt";

	public static int sumNeg = 0;
	public static int sumNeu = 0;
	public static int sumPos = 0;
	public static int sumUnd = 0;

	public static void main(String[] args) throws IOException, LangDetectException {
		 labelOriginals();
//		labelOnlyEnOriginals();

		// labelUncertain(MERGE_UNCERTAIN_PATH);
		// labelUncertain(UNCERTAIN_PATH);
	}

	private static List<LabelMessage> readUnlabeledMessagesFile(String path) throws FileNotFoundException, IOException {
		List<LabelMessage> unlabeledMessages = new ArrayList<LabelMessage>();
		JsonParser parser = new JsonParser();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				JsonObject json = (JsonObject) parser.parse(line);
				unlabeledMessages.add(new LabelMessage(json));
			}
		}
		return unlabeledMessages;
	}

	private static List<LabelMessage> readLabeledMessagesFile(String path) throws FileNotFoundException, IOException {
		List<LabelMessage> labeledMessages = new ArrayList<LabelMessage>();
		JsonParser parser = new JsonParser();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				JsonObject json = (JsonObject) parser.parse(line);
				String sentimentString = json.get("sentiment").getAsString();
				JsonElement element = json.get("json");
				JsonObject originalJson = (JsonObject) element;

				if (getSentimentFromString(sentimentString).equals(Sentiment.NEGATIVE)) {
					sumNeg++;
				} else if (getSentimentFromString(sentimentString).equals(Sentiment.NEUTRAL)) {
					sumNeu++;
				} else if (getSentimentFromString(sentimentString).equals(Sentiment.POSITIVE)) {
					sumPos++;
				} else if (getSentimentFromString(sentimentString).equals(Sentiment.UNDEFINED)) {
					sumUnd++;
				}

				labeledMessages.add(new LabelMessage(originalJson, getSentimentFromString(sentimentString)));
			}
		}
		return labeledMessages;
	}

	private static void writeLabeledMessages(String path, List<LabelMessage> labeledMessages) {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "utf-8"))) {
			for (int i = 0; i < labeledMessages.size(); i++) {
				JsonObject json = new JsonObject();
				json.addProperty("sentiment", labeledMessages.get(i).getSentiment().toString());
				json.add("json", labeledMessages.get(i).getJson());
				writer.write(json.toString() + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Sentiment getSentimentFromString(String sentimentString) {
		switch (sentimentString) {
		case "NEGATIVE":
			return Sentiment.NEGATIVE;
		case "NEUTRAL":
			return Sentiment.NEUTRAL;
		case "POSITIVE":
			return Sentiment.POSITIVE;
		case "UNDEFINED":
			return Sentiment.UNDEFINED;
		}
		return null;
	}

	// Label everything
	public static void labelOriginals() throws IOException, LangDetectException {
		// https://github.com/shuyo/language-detection
		DetectorFactory.loadProfile("src/main/resources/language-detection/shuyo_lang-detect/profiles.sm");
		
		Scanner scanner = new Scanner(System.in);
		int save = 0;
		try {
			List<LabelMessage> unlabeled = readUnlabeledMessagesFile(UNLABELED_MESSAGES);
			List<LabelMessage> labeled = readLabeledMessagesFile(LABELED_MESSAGES);

			boolean exit = false;
			int lastUnlabeledIndex = labeled.size();
			while (!exit) {
				if (unlabeled.size() <= lastUnlabeledIndex) {
					exit = true;
					continue;
				}
				String msg = unlabeled.get(lastUnlabeledIndex).getMessage();
				System.out.println(msg);

				Detector detector = DetectorFactory.create();
		        detector.append(msg);
		        System.out.println("NEUE DETECTION:  " + detector.detect());

				boolean next = false;
				LabelMessage labeledMessage = new LabelMessage(unlabeled.get(lastUnlabeledIndex).getJson());
				while (!next) {
					System.out.println(
							"NEG: a    NEU: s    POS: d    UNDEF: f    NEXT: w    BACK: z    STOP: capital P    COUNT: c");
					String input = scanner.next();
					switch (input) {
					case "a":
						labeledMessage.setSentiment(Sentiment.NEGATIVE);
						sumNeg++;
						break;
					case "s":
						labeledMessage.setSentiment(Sentiment.NEUTRAL);
						sumNeu++;
						break;
					case "d":
						labeledMessage.setSentiment(Sentiment.POSITIVE);
						sumPos++;
						break;
					case "f":
						labeledMessage.setSentiment(Sentiment.UNDEFINED);
						sumUnd++;
						break;
					case "z":
						next = true;
						if (labeled.size() > 0) {
							Sentiment sent = labeled.get(labeled.size() - 1).getSentiment();
							if (sent.equals(Sentiment.NEGATIVE)) {
								sumNeg--;
							} else if (sent.equals(Sentiment.NEUTRAL)) {
								sumNeu--;
							} else if (sent.equals(Sentiment.POSITIVE)) {
								sumPos--;
							} else if (sent.equals(Sentiment.UNDEFINED)) {
								sumUnd--;
							}
							labeled.remove(labeled.size() - 1);
						}
						lastUnlabeledIndex--;
						lastUnlabeledIndex--;
						break;
					case "P":
						next = true;
						exit = true;
						break;
					case "aw":
						labeledMessage.setSentiment(Sentiment.NEGATIVE);
						sumNeg++;
						next = true;
						break;
					case "sw":
						labeledMessage.setSentiment(Sentiment.NEUTRAL);
						sumNeu++;
						next = true;
						break;
					case "dw":
						labeledMessage.setSentiment(Sentiment.POSITIVE);
						sumPos++;
						next = true;
						break;
					case "fw":
						labeledMessage.setSentiment(Sentiment.UNDEFINED);
						sumUnd++;
						next = true;
						break;
					case "c":
						System.out.println(
								"NEG: " + sumNeg + "  NEU: " + sumNeu + "  POS: " + sumPos + "  UND: " + sumUnd);
						break;
					}
				}
				if (labeledMessage.getSentiment() != null) {
					labeled.add(labeledMessage);
				}
				lastUnlabeledIndex++;
				save++;
				if (save % 10 == 0) {
					writeLabeledMessages(LABELED_MESSAGES, labeled);
				}
			}
			writeLabeledMessages(LABELED_MESSAGES, labeled);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	public static void labelOnlyEnOriginals() throws IOException, LangDetectException {
		// https://github.com/shuyo/language-detection
		DetectorFactory.loadProfile("src/main/resources/language-detection/shuyo_lang-detect/profiles.sm");

		Scanner scanner = new Scanner(System.in);
		int save = 0;
		try {
			List<LabelMessage> unlabeled = readUnlabeledMessagesFile(UNLABELED_ONLY_ENGLISH_MESSAGES);
			List<LabelMessage> labeled = readLabeledMessagesFile(LABELED_MESSAGES);

			int i = 0;
			int msgCounter = 0;

			while (i < labeled.size()) {
				String msg = unlabeled.get(msgCounter).getMessage();

				Detector detector = DetectorFactory.create();
		        detector.append(msg);
		        String lang = detector.detect();
				
				boolean isEnglish = false;
				if (lang != null) {
					if (lang.equals("en")) {
						isEnglish = true;
					}
				}
				if (!isEnglish) {
					msgCounter++;
					continue;
				} else {
					i++;
					msgCounter++;
				}
			}

			// start new labeling
			boolean exit = false;
			int lastUnlabeledIndex = msgCounter;
			while (!exit) {
				if (unlabeled.size() <= lastUnlabeledIndex) {
					exit = true;
					continue;
				}
				String msg = unlabeled.get(lastUnlabeledIndex).getMessage();

				// Language detection
				Detector detector = DetectorFactory.create();
		        detector.append(msg);
		        String lang = detector.detect();

				boolean isEnglish = false;
				if (lang != null) {
					if (lang.equals("en")) {
						isEnglish = true;
					}
				}
				if (!isEnglish) {
					lastUnlabeledIndex++;
					continue;
				}
				System.out.println(msg);

				boolean next = false;
				LabelMessage labeledMessage = new LabelMessage(unlabeled.get(lastUnlabeledIndex).getJson());
				while (!next) {
					System.out.println(
							"NEG: a    NEU: s    POS: d    UNDEF: f    NEXT: w    BACK: z    STOP: capital P    COUNT: c");
					String input = scanner.next();
					switch (input) {
					case "a":
						labeledMessage.setSentiment(Sentiment.NEGATIVE);
						sumNeg++;
						break;
					case "s":
						labeledMessage.setSentiment(Sentiment.NEUTRAL);
						sumNeu++;
						break;
					case "d":
						labeledMessage.setSentiment(Sentiment.POSITIVE);
						sumPos++;
						break;
					case "f":
						labeledMessage.setSentiment(Sentiment.UNDEFINED);
						sumUnd++;
						break;
					case "z":

						// TODO

						next = true;
						if (labeled.size() > 0) {
							Sentiment sent = labeled.get(labeled.size() - 1).getSentiment();
							if (sent.equals(Sentiment.NEGATIVE)) {
								sumNeg--;
							} else if (sent.equals(Sentiment.NEUTRAL)) {
								sumNeu--;
							} else if (sent.equals(Sentiment.POSITIVE)) {
								sumPos--;
							} else if (sent.equals(Sentiment.UNDEFINED)) {
								sumUnd--;
							}
							labeled.remove(labeled.size() - 1);
						}
						lastUnlabeledIndex--;
						lastUnlabeledIndex--;
						break;
					case "P":
						next = true;
						exit = true;
						break;
					case "aw":
						labeledMessage.setSentiment(Sentiment.NEGATIVE);
						sumNeg++;
						next = true;
						break;
					case "sw":
						labeledMessage.setSentiment(Sentiment.NEUTRAL);
						sumNeu++;
						next = true;
						break;
					case "dw":
						labeledMessage.setSentiment(Sentiment.POSITIVE);
						sumPos++;
						next = true;
						break;
					case "fw":
						labeledMessage.setSentiment(Sentiment.UNDEFINED);
						sumUnd++;
						next = true;
						break;
					case "c":
						System.out.println(
								"NEG: " + sumNeg + "  NEU: " + sumNeu + "  POS: " + sumPos + "  UND: " + sumUnd);
						break;
					}
				}
				if (labeledMessage.getSentiment() != null) {
					labeled.add(labeledMessage);
				}
				lastUnlabeledIndex++;
				save++;
				if (save % 10 == 0) {
					writeLabeledMessages(LABELED_MESSAGES, labeled);
				}
			}
			writeLabeledMessages(LABELED_MESSAGES, labeled);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LangDetectException e) {
			e.printStackTrace();
		} finally {
			scanner.close();
		}
	}

	public static void labelUncertain(String path) throws IOException {
		List<MyTuple> tuples = new ArrayList<MyTuple>();
		Scanner scanner = new Scanner(System.in);
		try {
			String[] lines = BasicUtil.readLines(path);
			for (int i = 0; i < lines.length; i++) {
				boolean next = false;
				MyTuple tuple = new MyTuple();
				while (!next) {
					System.out.println(
							"NEG: a    NEU: s    POS: d    UNDEF: f    NEXT: w    STOP: capital P    COUNT: c");
					String input = scanner.next();
					switch (input) {
					case "a":
						tuple.setText(lines[i]);
						tuple.setSentiment("NEGATIVE");
						sumNeg++;
						break;
					case "s":
						tuple.setText(lines[i]);
						tuple.setSentiment("NEUTRAL");
						sumNeu++;
						break;
					case "d":
						tuple.setText(lines[i]);
						tuple.setSentiment("POSITIVE");
						sumPos++;
						break;
					case "f":
						tuple.setText(lines[i]);
						tuple.setSentiment("UNDEFINED");
						sumUnd++;
						break;
					case "P":
						next = true;
						break;
					case "aw":
						tuple.setText(lines[i]);
						tuple.setSentiment("NEGATIVE");
						sumNeg++;
						next = true;
						break;
					case "sw":
						tuple.setText(lines[i]);
						tuple.setSentiment("NEUTRAL");
						sumNeu++;
						next = true;
						break;
					case "dw":
						tuple.setText(lines[i]);
						tuple.setSentiment("POSITIVE");
						sumPos++;
						next = true;
						break;
					case "fw":
						tuple.setText(lines[i]);
						tuple.setSentiment("UNDEFINED");
						sumUnd++;
						next = true;
						break;
					case "c":
						System.out.println(
								"NEG: " + sumNeg + "  NEU: " + sumNeu + "  POS: " + sumPos + "  UND: " + sumUnd);
						break;
					}
				}
				if (tuple.getSentiment() != null) {
					tuples.add(tuple);
				}
			}
			print(CERTAIN_PATH, tuples);
		} finally {
			scanner.close();
		}
	}

	public static void print(String toPath, List<MyTuple> certain) {
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

}
