package at.illecker.sentistorm.commons.featurevector.selector;

import java.util.List;

import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSBooleanFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSCombinedFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSSentimentFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSTfIdfFeatureVectorGenerator;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
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
			NoPOSTweetTfIdf tweetTfIdfNoPOS = NoPOSTweetTfIdf.createFromTaggedTokens(preprocessedTweets, TfType.RAW,
					TfIdfNormalization.COS, true);
			return new NoPOSTfIdfFeatureVectorGenerator(tweetTfIdfNoPOS);
		} else if (noPOSFeatureVectorGenerator.equals(NoPOSBooleanFeatureVectorGenerator.class)) {
			return new NoPOSBooleanFeatureVectorGenerator();
		} else if (noPOSFeatureVectorGenerator.equals(NoPOSCombinedFeatureVectorGenerator.class)) {
			NoPOSTweetTfIdf tweetTfIdfNoPOS = NoPOSTweetTfIdf.createFromTaggedTokens(preprocessedTweets, TfType.RAW,
					TfIdfNormalization.COS, true);
			return new NoPOSCombinedFeatureVectorGenerator(true, tweetTfIdfNoPOS);
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
