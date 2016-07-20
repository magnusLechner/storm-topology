package at.illecker.sentistorm.commons.svm.box;

import java.util.List;

import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.svm.model.ModelTrainer;
import at.illecker.sentistorm.commons.svm.prediction.Predictor;
import libsvm.svm_model;

public abstract class SVMBox {

	private ModelTrainer modelTrainer;
	private Predictor predictor;
	private String name;
	
	public svm_model generateModel() {
		if(modelTrainer == null) {
			throw new NullPointerException("No ModelTrainer was assigned.");
		}
		return modelTrainer.generateModel();
	}
	
	public double predict(Tweet tweet) {
		if(predictor == null) {
			throw new NullPointerException("No Predictor was assigned.");
		}
		return predictor.predict(tweet);
	}
	
	public double predict(FeaturedTweet featuredTweet) {
		if(predictor == null) {
			throw new NullPointerException("No Predictor was assigned.");
		}
		return predictor.predict(featuredTweet);
	}
	
	public List<Double> predict(List<Tweet> tweets) {
		if(predictor == null) {
			throw new NullPointerException("No Predictor was assigned.");
		}
		return predictor.predict(tweets);
	}
	
	public void shutdown() {
		if(predictor != null) {
			predictor.shutdown();
		}
	}

	public ModelTrainer getModelTrainer() {
		return modelTrainer;
	}

	public void setModelTrainer(ModelTrainer modelTrainer) {
		this.modelTrainer = modelTrainer;
	}

	public Predictor getPredictor() {
		return predictor;
	}

	public void setPredictor(Predictor predictor) {
		this.predictor = predictor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
