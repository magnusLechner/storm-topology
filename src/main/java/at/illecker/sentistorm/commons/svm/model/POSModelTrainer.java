package at.illecker.sentistorm.commons.svm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.featurevector.FeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.selector.FVGSelector;
import at.illecker.sentistorm.components.POSTagger;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;
import cmu.arktweetnlp.Tagger.TaggedToken;
import libsvm.svm_model;

public class POSModelTrainer extends ModelTrainer {

	private Dataset dataset;
	private Class<? extends FeatureVectorGenerator> featureVectorGeneratorClass;
	private FeatureVectorGenerator fvg = null;
	private boolean generateARFF;

	public POSModelTrainer(Dataset dataset, Class<? extends FeatureVectorGenerator> featureVectorGeneratorClass,
			int nFold, boolean generateARFF) {
		super(dataset, nFold);
		this.dataset = dataset;
		this.featureVectorGeneratorClass = featureVectorGeneratorClass;
		this.generateARFF = generateARFF;
	}

	public POSModelTrainer(Dataset dataset, FeatureVectorGenerator fvg, int nFold, boolean generateARFF) {
		super(dataset, nFold);
		this.dataset = dataset;
		this.fvg = fvg;
		this.generateARFF = generateARFF;
	}

	@Override
	public svm_model generateModel() {
		List<Tweet> trainTweets = dataset.getTrainTweets(false, true);
		List<List<String>> tokenizedTweets = Tokenizer.tokenizeTweets(trainTweets);
		Preprocessor preprocessor = Preprocessor.getInstance();
		List<List<String>> preprocessedTweets = preprocessor.preprocessTweets(tokenizedTweets);
		POSTagger posTagger = POSTagger.getInstance();
		List<List<TaggedToken>> taggedTweets = posTagger.tagTweets(preprocessedTweets);

		if (fvg == null) {
			fvg = FVGSelector.selectFVG(featureVectorGeneratorClass, taggedTweets);
		}

		List<FeaturedTweet> featuredTrainTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < taggedTweets.size(); i++) {
			List<TaggedToken> taggedTweet = taggedTweets.get(i);
			Map<Integer, Double> featureVector = fvg.generateFeatureVector(taggedTweet);
			featuredTrainTweets.add(new FeaturedTweet(trainTweets.get(i), tokenizedTweets.get(i),
					preprocessedTweets.get(i), taggedTweet, featureVector));
		}

		if (generateARFF) {
			generateARFF(featuredTrainTweets, fvg.getFeatureVectorSize());
		}

		return generateModel(featuredTrainTweets);
	}

}
