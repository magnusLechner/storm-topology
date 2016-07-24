package at.illecker.sentistorm.commons.featurevector.aggregate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.illecker.sentistorm.commons.FeaturedTweet;
import at.illecker.sentistorm.commons.Tweet;
import at.illecker.sentistorm.commons.svm.box.SVMBox;

public class OriginAndResultFeatureVectorGenerator extends AggregationFeatureVectorGenerator {
	private static final Logger LOG = LoggerFactory.getLogger(OriginAndResultFeatureVectorGenerator.class);

	private List<SVMBox> boxes;
	private int[] featureVectorDistance;
	private final int m_vectorSize;
	
	public OriginAndResultFeatureVectorGenerator(List<SVMBox> boxes) {
		this.boxes = boxes;
		featureVectorDistance = new int[boxes.size() + 1];
		int vectorSize = 0;
		for(int i = 0; i < boxes.size(); i++) {
			//old fv + predicted class
			vectorSize += boxes.get(i).getPredictor().getFeatureVectorSize() + 1;
			featureVectorDistance[i + 1] = vectorSize;
		}
		m_vectorSize = vectorSize;
		LOG.info("VectorSize: " + m_vectorSize);
	}
	
	@Override
	public int getFeatureVectorSize() {
		return m_vectorSize;
	}

	@Override
	public Map<Integer, Double> generateFeatureVector(Tweet tweet) {
		Map<Integer, Double> resultFeatureVector = new TreeMap<Integer, Double>();
		
//		System.out.println("TWEET: " + tweet.getText());
		
		for (int i = 0; i < boxes.size(); i++) {
			FeaturedTweet featuredTweet = boxes.get(i).getPredictor().prepareFeatureTweet(tweet);
			if (featuredTweet.getFeatureVector().size() > 0) {
				for(Entry<Integer, Double> entry : featuredTweet.getFeatureVector().entrySet()) {
					resultFeatureVector.put(featureVectorDistance[i] + entry.getKey(), entry.getValue());	
				}
				resultFeatureVector.put(featureVectorDistance[i + 1] , boxes.get(i).predict(featuredTweet));
			}
			
//			System.out.println("BOX NAME: " + boxes.get(i).getName());
//			System.out.println("FV: " + featuredTweet.getFeatureVector());
//			System.out.println("Prediction: " + boxes.get(i).predict(featuredTweet));
			
		}
		
//		System.out.println("OVERALL FV: " + resultFeatureVector);
		
		return resultFeatureVector;
	}

}
