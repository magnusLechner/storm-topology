package at.illecker.sentistorm.commons.featurevector.selector;

import java.util.List;

import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.featurevector.pos.SpecialFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.CombinedFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.FeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.POSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.SentimentAndPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.SentimentFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.TfIdfAndPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.pos.TfIdfFeatureVectorGenerator;
import at.illecker.sentistorm.commons.tfidf.TfIdfNormalization;
import at.illecker.sentistorm.commons.tfidf.TfType;
import at.illecker.sentistorm.commons.tfidf.TweetTfIdf;
import at.illecker.sentistorm.components.POSTagger;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;
import cmu.arktweetnlp.Tagger.TaggedToken;

public class FVGSelector {

	//changind arguments around feels like cheating...
	public static FeatureVectorGenerator selectFVG(List<Tweet> trainTweets,
			Class<? extends FeatureVectorGenerator> featureVectorGenerator) {
		
		System.out.println("FVG: trainTweets.size:   " + trainTweets.size());
		
		return selectFVG(featureVectorGenerator, getTaggedTweets(trainTweets));
	}

	public static FeatureVectorGenerator selectFVG(Class<? extends FeatureVectorGenerator> featureVectorGenerator,
			List<List<TaggedToken>> taggedTweets) {
		if (featureVectorGenerator.equals(SentimentFeatureVectorGenerator.class)) {
			return new SentimentFeatureVectorGenerator();
		} else if (featureVectorGenerator.equals(TfIdfFeatureVectorGenerator.class)) {
			TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.RAW, TfIdfNormalization.COS,
					true);
			return new TfIdfFeatureVectorGenerator(tweetTfIdf);
		} else if (featureVectorGenerator.equals(SentimentAndPOSFeatureVectorGenerator.class)) {
			return new SentimentAndPOSFeatureVectorGenerator(true);
		} else if (featureVectorGenerator.equals(POSFeatureVectorGenerator.class)) {
			return new POSFeatureVectorGenerator(true);
		} else if (featureVectorGenerator.equals(TfIdfAndPOSFeatureVectorGenerator.class)) {
			TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.RAW, TfIdfNormalization.COS,
					true);
			return new TfIdfAndPOSFeatureVectorGenerator(true, tweetTfIdf);
		} else if (featureVectorGenerator.equals(SpecialFeatureVectorGenerator.class)) {
			return new SpecialFeatureVectorGenerator();
		} else if (featureVectorGenerator.equals(CombinedFeatureVectorGenerator.class)) {
			TweetTfIdf tweetTfIdf = TweetTfIdf.createFromTaggedTokens(taggedTweets, TfType.RAW, TfIdfNormalization.COS,
					true);
			return new CombinedFeatureVectorGenerator(true, tweetTfIdf);
		} else {
			throw new UnsupportedOperationException(
					"FeatureVectorGenerator '" + featureVectorGenerator.getName() + "' is not supported!");
		}
	}

	private static List<List<TaggedToken>> getTaggedTweets(List<Tweet> trainTweets) {
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		POSTagger posTagger = POSTagger.getInstance();
		return posTagger.tagTweets(preprocessedTweets);
	}

}
