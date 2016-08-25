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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import at.lechner.commons.Sentiment;

public class LocalLabeling {

	public static final String UNLABELED_MESSAGES = "src/main/resources/preparation/self-labeling/toBeLabeled.txt";
	public static final String LABELED_MESSAGES = "src/main/resources/preparation/self-labeling/labeled.txt";

	public static int sumNeg = 0;
	public static int sumNeu = 0;
	public static int sumPos = 0;
	public static int sumUnd = 0;
	
	public static void main(String[] args) {
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
				System.out.println(unlabeled.get(lastUnlabeledIndex).getMessage());

				// input
				// add to labeled with sentiment
				boolean next = false;
				LabelMessage labeledMessage = new LabelMessage(unlabeled.get(lastUnlabeledIndex).getJson());
				while (!next) {
					System.out.println("NEG: a    NEU: s    POS: d    UNDEF: f    NEXT: w    BACK: z    STOP: capital P    COUNT: c");
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
						if(labeled.size() > 0) {
							Sentiment sent = labeled.get(labeled.size() - 1).getSentiment();
							if(sent.equals(Sentiment.NEGATIVE)) {
								sumNeg--;
							} else if(sent.equals(Sentiment.NEUTRAL)) {
								sumNeu--;
							} else if(sent.equals(Sentiment.POSITIVE)) {
								sumPos--;
							} else if(sent.equals(Sentiment.UNDEFINED)) {
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
						System.out.println("NEG: " + sumNeg + "  NEU: " + sumNeu + "  POS: " + sumPos + "  UND: " + sumUnd);
						break;
					}
				}
				if (labeledMessage.getSentiment() != null) {
					labeled.add(labeledMessage);
				}
				lastUnlabeledIndex++;
				save++;
				if(save%10 == 0) {
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
				
				if(getSentimentFromString(sentimentString).equals(Sentiment.NEGATIVE)) {
					sumNeg++;
				} else if(getSentimentFromString(sentimentString).equals(Sentiment.NEUTRAL)) {
					sumNeu++;
				} else if(getSentimentFromString(sentimentString).equals(Sentiment.POSITIVE)) {
					sumPos++;
				} else if(getSentimentFromString(sentimentString).equals(Sentiment.UNDEFINED)) {
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

}
