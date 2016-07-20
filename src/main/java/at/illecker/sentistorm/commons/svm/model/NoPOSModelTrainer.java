package at.illecker.sentistorm.commons.svm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.illecker.sentistorm.commons.Dataset;
import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.featurevector.nopos.NoPOSFeatureVectorGenerator;
import at.illecker.sentistorm.commons.featurevector.selector.NoPOSFVGSelector;
import at.illecker.sentistorm.components.Preprocessor;
import at.illecker.sentistorm.components.Tokenizer;
import libsvm.svm_model;

public class NoPOSModelTrainer extends ModelTrainer {

	private Dataset dataset;
	private Class<? extends NoPOSFeatureVectorGenerator> noPOSFeatureVectorGeneratorClass;
	private NoPOSFeatureVectorGenerator fvg = null;
	private boolean generateARFF;

	public NoPOSModelTrainer(Dataset dataset,
			Class<? extends NoPOSFeatureVectorGenerator> noPOSFeatureVectorGeneratorClass, int nFold,
			boolean generateARFF) {
		super(dataset, nFold);
		this.dataset = dataset;
		this.noPOSFeatureVectorGeneratorClass = noPOSFeatureVectorGeneratorClass;
		this.generateARFF = generateARFF;
	}

	public NoPOSModelTrainer(Dataset dataset, NoPOSFeatureVectorGenerator fvg, int nFold, boolean generateARFF) {
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

		if (fvg == null) {
			fvg = NoPOSFVGSelector.selectFVG(noPOSFeatureVectorGeneratorClass, preprocessedTweets);
		}

		List<FeaturedTweet> featuredTrainTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < preprocessedTweets.size(); i++) {
			List<String> preprocessedTweet = preprocessedTweets.get(i);
			Map<Integer, Double> featureVector = fvg.generateFeatureVector(preprocessedTweet);
			featuredTrainTweets.add(new FeaturedTweet(trainTweets.get(i), tokenizedTweets.get(i),
					preprocessedTweets.get(i), featureVector));
		}
		
		if(generateARFF) {
			generateARFF(featuredTrainTweets, fvg.getFeatureVectorSize());
		}

		return generateModel(featuredTrainTweets);
	}

}
