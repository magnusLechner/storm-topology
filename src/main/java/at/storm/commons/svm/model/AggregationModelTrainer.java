package at.storm.commons.svm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.storm.commons.Dataset;
import at.storm.commons.FeaturedTweet;
import at.storm.commons.Tweet;
import at.storm.commons.featurevector.aggregate.AggregationFeatureVectorGenerator;
import at.storm.commons.featurevector.selector.AggregationFVGSelector;
import at.storm.commons.svm.box.SVMBox;
import libsvm.svm_model;

public class AggregationModelTrainer extends ModelTrainer {

	private Dataset dataset;
	private List<SVMBox> boxes;
	private Class<? extends AggregationFeatureVectorGenerator> aggregateFVGClass;
	private AggregationFeatureVectorGenerator aggregateFVG = null;
	private boolean generateARFF;

	public AggregationModelTrainer(List<SVMBox> boxes, Dataset dataset,
			Class<? extends AggregationFeatureVectorGenerator> aggregateFVGClass, int nFold, boolean generateARFF) {
		super(dataset, nFold);
		this.boxes = boxes;
		this.dataset = dataset;
		this.aggregateFVGClass = aggregateFVGClass;
		this.generateARFF = generateARFF;
	}

	public AggregationModelTrainer(List<SVMBox> boxes, Dataset dataset, AggregationFeatureVectorGenerator aggregateFVG,
			int nFold, boolean generateARFF) {
		super(dataset, nFold);
		this.boxes = boxes;
		this.dataset = dataset;
		this.aggregateFVG = aggregateFVG;
		this.generateARFF = generateARFF;
	}

	@Override
	public svm_model generateModel() {
		if (aggregateFVG == null) {
			aggregateFVG = AggregationFVGSelector.selectFVG(boxes, aggregateFVGClass);
		}
		List<Tweet> trainTweets = dataset.getTrainTweets(false, true);
		List<FeaturedTweet> featuredTrainTweets = new ArrayList<FeaturedTweet>();
		for (int i = 0; i < trainTweets.size(); i++) {
			Map<Integer, Double> featureVector = aggregateFVG.generateFeatureVector(trainTweets.get(i));
			featuredTrainTweets.add(new FeaturedTweet(trainTweets.get(i), featureVector));
		}
		
		if(generateARFF) {
			generateARFF(featuredTrainTweets, aggregateFVG.getFeatureVectorSize());
		}

		return generateModel(featuredTrainTweets);
	}

}
