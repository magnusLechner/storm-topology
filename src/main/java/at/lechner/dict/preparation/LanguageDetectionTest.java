package at.lechner.dict.preparation;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import at.lechner.commons.MyTuple;
import at.lechner.commons.Sentiment;
import at.lechner.preparation.SVMPreparation;

public class LanguageDetectionTest {

	public static final String COMPLETE = "src/main/resources/preparation/complete-labeling/complete_all.txt";
//	public static final String NOT_CORRECT_LANGUAGE_PREDICTED = "src/main/resources/preparation/complete-labeling/complete_all.txt";

	private int allSize;
	private int correctAll;
	private int correctNegative;
	private int correctNeutral;
	private int correctPositives;
	private int negativesCount;
	private int neutralCount;
	private int positivesCount;

	public void test() throws LangDetectException {
		MyTuple[] tuples = SVMPreparation.getMyTuples(COMPLETE);

		allSize = tuples.length;
		for (MyTuple tuple : tuples) {
			if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
				negativesCount++;
			} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
				neutralCount++;
			} else {
				positivesCount++;
			}
		}

		System.out.println("src/main/resources/language-detection/shuyo_lang-detect/profiles");
		DetectorFactory.loadProfile("src/main/resources/language-detection/shuyo_lang-detect/profiles");
		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

			Detector detector = DetectorFactory.create();
			detector.append(msg);

			if (detector.detect().equals("en")) {
				correctAll++;
				if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
					correctNegative++;
				} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
					correctNeutral++;
				} else {
					correctPositives++;
				}
			}
		}
		printResult();
		reset();

		System.out.println("src/main/resources/language-detection/shuyo_lang-detect/profiles.sm");
		DetectorFactory.loadProfile("src/main/resources/language-detection/shuyo_lang-detect/profiles.sm");
		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

			Detector detector = DetectorFactory.create();
			detector.append(msg);

			if (detector.detect().equals("en")) {
				correctAll++;
				if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
					correctNegative++;
				} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
					correctNeutral++;
				} else {
					correctPositives++;
				}
			}
		}
		printResult();
		reset();

		System.out.println("src/main/resources/language-detection/optimaize/languages");
		DetectorFactory.loadProfile("src/main/resources/language-detection/optimaize/languages");
		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

			Detector detector = DetectorFactory.create();
			detector.append(msg);

			if (detector.detect().equals("en")) {
				correctAll++;
				if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
					correctNegative++;
				} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
					correctNeutral++;
				} else {
					correctPositives++;
				}
			}
		}
		printResult();
		reset();

		System.out.println("src/main/resources/language-detection/optimaize/languages.shorttext");
		DetectorFactory.loadProfile("src/main/resources/language-detection/optimaize/languages.shorttext");
		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

			Detector detector = DetectorFactory.create();
			detector.append(msg);

			if (detector.detect().equals("en")) {
				correctAll++;
				if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
					correctNegative++;
				} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
					correctNeutral++;
				} else {
					correctPositives++;
				}
			}
		}
		printResult();
		reset();
	}

	private void printResult() {
		System.out.println("overall precision: " + correctAll / allSize);
		System.out.println("negatives precision: " + correctNegative / negativesCount);
		System.out.println("neutral precision: " + correctNeutral / neutralCount);
		System.out.println("positives precision: " + correctPositives / positivesCount);
		System.out.println();
	}

	private void reset() {
		correctAll = 0;
		correctNegative = 0;
		correctNeutral = 0;
		correctPositives = 0;
	}

	public static void main(String[] args) throws Exception {
		LanguageDetectionTest langTest = new LanguageDetectionTest();
		langTest.test();
	}

}
