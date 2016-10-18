package at.storm.commons.featurevector.selector;

import java.util.List;

import at.storm.commons.featurevector.aggregate.AggregationFeatureVectorGenerator;
import at.storm.commons.featurevector.aggregate.ListResultsFeatureVectorGenerator;
import at.storm.commons.featurevector.aggregate.OriginAndResultFeatureVectorGenerator;
import at.storm.commons.svm.box.SVMBox;

public class AggregationFVGSelector {

	public static AggregationFeatureVectorGenerator selectFVG(List<SVMBox> boxes,
			Class<? extends AggregationFeatureVectorGenerator> aggregateFeatureVectorGenerator) {
		if (aggregateFeatureVectorGenerator.equals(ListResultsFeatureVectorGenerator.class)) {
			return new ListResultsFeatureVectorGenerator(boxes);
		}else if (aggregateFeatureVectorGenerator.equals(OriginAndResultFeatureVectorGenerator.class)) {
			return new OriginAndResultFeatureVectorGenerator(boxes);
		} else {
			throw new UnsupportedOperationException("AggregateFeatureVectorGenerator '"
					+ aggregateFeatureVectorGenerator.getName() + "' is not supported!");
		}
	}

}
