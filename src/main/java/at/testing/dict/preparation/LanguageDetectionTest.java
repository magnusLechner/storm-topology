package at.testing.dict.preparation;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.language.LanguageIdentifier;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import at.storm.commons.dict.TwitchEmoticons;
import at.storm.components.Tokenizer;
import at.testing.commons.MyTuple;
import at.testing.commons.Sentiment;
import at.testing.preparation.SVMPreparation;

public class LanguageDetectionTest {

	public static final String COMPLETE = "src/main/resources/preparation/complete-labeling/complete_all.txt";

	public static final String SHUYO_LONG = "src/main/resources/language-detection/shuyo_lang-detect/profiles";
	public static final String SHUYO_SHORT = "src/main/resources/language-detection/shuyo_lang-detect/profiles.sm";

	public static final String OPTIMAIZE_LONG = "src/main/resources/language-detection/optimaize/languages";
	public static final String OPTIMAIZE_SHORT = "src/main/resources/language-detection/optimaize/languages.shorttext";

	private int allSize;
	private int correctAll;
	private int correctNegative;
	private int correctNeutral;
	private int correctPositives;
	private int negativesCount;
	private int neutralCount;
	private int positivesCount;

	// optimaize newest shuyo
	public void optimaize_newest_shuyo(String path) throws LangDetectException {
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

		System.out.println(path);

		DetectorFactory.loadProfile(path);
		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

//			List<String> tokens = Tokenizer.tokenize(msg);
//			List<String> asd = new ArrayList<String>();
//			for(String s : tokens) {
//				if(!TwitchEmoticons.getInstance().isTwitchEmoticon(s)) {
//					asd.add(s);
//				}	
//			}
//			String k = "";
//			for(String g : asd) {
//				k += g;
//			}
//			msg = k.trim();

			Detector detector = DetectorFactory.create();
			detector.append(msg);

			Boolean languageCorrect = null;
			try {
				languageCorrect = detector.detect().equals("en");
			} catch (Exception e) {
				languageCorrect = false;
			}

			if (languageCorrect) {
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
	}

	// tika
	@SuppressWarnings("deprecation")
	public void tika() {
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

		for (MyTuple tuple : tuples) {
			String msg = tuple.getText();

			List<String> tokens = Tokenizer.tokenize(msg);
			List<String> asd = new ArrayList<String>();
			for (String s : tokens) {
				if (!TwitchEmoticons.getInstance().isTwitchEmoticon(s)) {
					asd.add(s);
				}
			}
			String k = "";
			for (String g : asd) {
				k += g;
			}
			msg = k.trim();

			Boolean languageCorrect = null;
			try {
				LanguageIdentifier identifier = new LanguageIdentifier(msg);
				languageCorrect = identifier.getLanguage().equals("en");
			} catch (Exception e) {
				languageCorrect = false;
			}

			if (languageCorrect) {
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
	}

//	public void optimaize_shuyo_fork() throws IOException {
//		MyTuple[] tuples = SVMPreparation.getMyTuples(COMPLETE);
//
//		allSize = tuples.length;
//		for (MyTuple tuple : tuples) {
//			if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
//				negativesCount++;
//			} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
//				neutralCount++;
//			} else {
//				positivesCount++;
//			}
//		}
//
//		// load all languages:
//		List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
//		
//		
////		List<String> languages = new ArrayList<String>();
////		languages.add("en");
////		languages.add("fr");
////		languages.add("de");
////		List<LanguageProfile> languageProfiles = new LanguageProfileReader().read(languages);
//
//		// build language detector:
//		LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
//				.withProfiles(languageProfiles).build();
//
//		TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
//
//		for (MyTuple tuple : tuples) {
//			String msg = tuple.getText();
//
//			Boolean languageCorrect = null;
//			try {
//				// query:
//				TextObject textObject = textObjectFactory.forText(msg);
//				Optional<LdLocale> lang = languageDetector.detect(textObject);
//
//				if (lang.get().getLanguage().equals("en")) {
//					languageCorrect = true;
//				} else {
//					languageCorrect = false;
//				}
//
//			} catch (Exception e) {
//				languageCorrect = false;
//			}
//
//			if (languageCorrect) {
//				correctAll++;
//				if (tuple.getSentiment().equals(Sentiment.NEGATIVE)) {
//					correctNegative++;
//				} else if (tuple.getSentiment().equals(Sentiment.NEUTRAL)) {
//					correctNeutral++;
//				} else {
//					correctPositives++;
//				}
//			}
//		}
//		printResult();
//	}

	private void printResult() {
		DecimalFormat df = new DecimalFormat("0.000");
		System.out.println("overall precision: " + df.format((double) correctAll / allSize));
		System.out.println("negatives precision: " + df.format((double) correctNegative / negativesCount));
		System.out.println("neutral precision: " + df.format((double) correctNeutral / neutralCount));
		System.out.println("positives precision: " + df.format((double) correctPositives / positivesCount));
		System.out.println();
	}

	public static void main(String[] args) throws Exception {
		LanguageDetectionTest langTest = new LanguageDetectionTest();

		// OPTIMAIZE NO SHUYO
//		langTest.optimaize_newest_shuyo(SHUYO_LONG);

//		overall precision: 0,623
//		negatives precision: 0,625
//		neutral precision: 0,696
//		positives precision: 0,442

//		langTest.optimaize_newest_shuyo(SHUYO_SHORT);

//		overall precision: 0,660
//		negatives precision: 0,630
//		neutral precision: 0,729
//		positives precision: 0,515

//		langTest.optimaize_newest_shuyo(OPTIMAIZE_LONG);

//		overall precision: 0,564
//		negatives precision: 0,558
//		neutral precision: 0,641
//		positives precision: 0,379

		langTest.optimaize_newest_shuyo(OPTIMAIZE_SHORT);

//		overall precision: 0,705
//		negatives precision: 0,696
//		neutral precision: 0,768
//		positives precision: 0,561

		// TIKA
//		langTest.tika();

//		overall precision: 0,321
//		negatives precision: 0,285
//		neutral precision: 0,387
//		positives precision: 0,193

		// OPTIMAIZE WITH SHUYO
//		langTest.optimaize_shuyo_fork();
	}

}
