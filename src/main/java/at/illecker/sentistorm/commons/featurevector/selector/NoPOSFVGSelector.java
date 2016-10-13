package at.illecker.sentistorm.commons.featurevector.selector;

import java.util.List;

import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSSpecialFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSCombinedFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSSentimentFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSTfIdfFeatureVectorGenerator;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
import at.illecker.sentistorm.commons.tfidf.ngram.MessageNGrams;
import at.illecker.sentistorm.commons.tfidf.nopos.NoPOSTweetTfIdf;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;

public class NoPOSFVGSelector {

	// changind arguments around feels like cheating...
	public static NoPOSFeatureVectorGenerator selectFVG(List<Tweet> trainTweets,
			Class<? extends NoPOSFeatureVectorGenerator> noPOSFeatureVectorGenerator) {
		return selectFVG(noPOSFeatureVectorGenerator, getPreprocessedTweets(trainTweets));
	}

	public static NoPOSFeatureVectorGenerator selectFVG(
			Class<? extends NoPOSFeatureVectorGenerator> noPOSFeatureVectorGenerator,
			List<List<String>> preprocessedTweets) {
		if (noPOSFeatureVectorGenerator.equals(NoPOSSentimentFeatureVectorGenerator.class)) {
			return new NoPOSSentimentFeatureVectorGenerator();
		} else if (noPOSFeatureVectorGenerator.equals(NoPOSTfIdfFeatureVectorGenerator.class)) {
			NoPOSTweetTfIdf tweetTfIdfNoPOS = NoPOSTweetTfIdf.createFromPreprocessedTokens(preprocessedTweets,
					TfType.LOG, TfIdfNormalization.COS);
			return new NoPOSTfIdfFeatureVectorGenerator(tweetTfIdfNoPOS);
		} else if (noPOSFeatureVectorGenerator.equals(NoPOSSpecialFeatureVectorGenerator.class)) {
			return new NoPOSSpecialFeatureVectorGenerator();
		} else if (noPOSFeatureVectorGenerator.equals(NoPOSCombinedFeatureVectorGenerator.class)) {
			NoPOSTweetTfIdf tweetTfIdfNoPOS = NoPOSTweetTfIdf.createFromPreprocessedTokens(preprocessedTweets,
					TfType.LOG, TfIdfNormalization.COS);
//			MessageNGrams nGrams = MessageNGrams.createFromTokens(preprocessedTweets, TfType.RAW,
//					TfIdfNormalization.COS);
			MessageNGrams nGrams = null;
			return new NoPOSCombinedFeatureVectorGenerator(tweetTfIdfNoPOS, nGrams);
		} else {
			throw new UnsupportedOperationException(
					"NoPOSFeatureVectorGenerator '" + noPOSFeatureVectorGenerator.getName() + "' is not supported!");
		}
	}

	private static List<List<String>> getPreprocessedTweets(List<Tweet> trainTweets) {
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		return preprocessor.preprocessTweets(tokenizedTweets);
	}

}
